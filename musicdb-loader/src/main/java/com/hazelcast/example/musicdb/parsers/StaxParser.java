/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.example.musicdb.parsers;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;
import com.hazelcast.partition.strategy.DefaultPartitioningStrategy;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.util.*;

public abstract class StaxParser<T> {

    protected final FastStack<String> elementStack = new FastStack<>();

    private final StartElementAdapter startElementAdapter = new StartElementAdapter();
    private final XMLEventAdapter xmlEventAdapter = new XMLEventAdapter();

    private final SerializationService ss;

    private final List<T> result;
    private final boolean dryRun;
    private final Class<T> type;

    private boolean cancel = false;

    private int elementCount;
    private long byteSize;

    protected StaxParser(boolean dryRun, Class<T> type) {
        this.result = new ArrayList<>(1000000);
        this.dryRun = dryRun;
        this.type = type;

        try {
            SerializationServiceBuilder builder = new SerializationServiceBuilder();
            ss = builder.setAllowUnsafe(true)
                    .setClassLoader(StaxParser.class.getClassLoader())
                    .setEnableCompression(true)
                    .setPartitioningStrategy(new DefaultPartitioningStrategy())
                    .setCheckClassDefErrors(true)
                    .setByteOrder(ByteOrder.BIG_ENDIAN)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void pushElement(T element) {
        elementCount++;
        Data data = ss.toData(element);
        byteSize += data.getHeapCost();
        if (!dryRun) {
            result.add(element);
        }
        if (elementCount % 10000 == 0) {
            System.out.println("Collected " + elementCount + " elements");
        }
    }

    protected void cancel() {
        cancel = true;
    }

    protected abstract XMLEvent startElement(String name, StartElement element, XMLEventReader reader) throws Exception;

    protected abstract XMLEvent endElement(String name, EndElement element, XMLEventReader reader) throws Exception;

    public final ParserResult<T> parse(InputStream is) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLEventReader reader = factory.createXMLEventReader(is);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            handleEvent(event, reader);

            if (cancel) {
                break;
            }
        }
        return new ParserResult<T>(elementCount, byteSize, result, type);
    }

    private void handleEvent(XMLEvent event, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = null;
        if (event.isStartElement()) {
            xmlEventAdapter.setEvent(event);
            StartElement element = xmlEventAdapter.asStartElement();
            String name = element.getName().getLocalPart();
            elementStack.push(name);
            nextEvent = startElement(name, element, reader);
        } else if (event.isEndElement()) {
            EndElement element = event.asEndElement();
            String name = element.getName().getLocalPart();
            nextEvent = endElement(name, element, reader);

            Set<String> attributes = startElementAdapter.attributes;
            Iterator<Attribute> iterator = startElementAdapter.element.getAttributes();
            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                if (!attributes.contains(attribute.getName().getLocalPart())) {
                    notHandled(attribute.getName().getLocalPart(), Type.Attribute);
                }
            }

            elementStack.pop();
        }
        if (nextEvent != null && !cancel) {
            handleEvent(nextEvent, reader);
        }
    }

    protected void notHandled(String name, Type type) {
        Object[] elements = elementStack.toArray(new String[elementStack.size()]);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elementStack.size; i++) {
            Object e = elements[i];
            sb.append(e).append("->");
        }
        if (type == Type.Attribute) {
            sb.append("[").append(name).append("]");
        } else {
            sb.append("{self}");
        }
        sb.append(" not handled");
        System.err.println(sb.toString());
        System.err.println("");
    }

    protected enum Type {
        Element,
        Attribute
    }

    private class XMLEventAdapter implements XMLEvent {
        private XMLEvent event;

        protected void setEvent(XMLEvent event) {
            this.event = event;
        }

        @Override
        public int getEventType() {
            return event.getEventType();
        }

        @Override
        public Location getLocation() {
            return event.getLocation();
        }

        @Override
        public boolean isStartElement() {
            return event.isStartElement();
        }

        @Override
        public boolean isAttribute() {
            return event.isAttribute();
        }

        @Override
        public boolean isNamespace() {
            return event.isNamespace();
        }

        @Override
        public boolean isEndElement() {
            return event.isEndElement();
        }

        @Override
        public boolean isEntityReference() {
            return event.isEntityReference();
        }

        @Override
        public boolean isProcessingInstruction() {
            return event.isProcessingInstruction();
        }

        @Override
        public boolean isCharacters() {
            return event.isCharacters();
        }

        @Override
        public boolean isStartDocument() {
            return event.isStartDocument();
        }

        @Override
        public boolean isEndDocument() {
            return event.isEndDocument();
        }

        @Override
        public StartElement asStartElement() {
            startElementAdapter.setElement(event.asStartElement());
            return startElementAdapter;
        }

        @Override
        public EndElement asEndElement() {
            return event.asEndElement();
        }

        @Override
        public Characters asCharacters() {
            return event.asCharacters();
        }

        @Override
        public QName getSchemaType() {
            return event.getSchemaType();
        }

        @Override
        public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
            event.writeAsEncodedUnicode(writer);
        }
    }

    private class StartElementAdapter extends XMLEventAdapter implements StartElement {
        private StartElement element;

        private final Set<String> attributes = new HashSet<>();

        protected void setElement(StartElement element) {
            this.element = element;
            setEvent(element);
            attributes.clear();
        }

        @Override
        public QName getName() {
            return element.getName();
        }

        @Override
        public Iterator getAttributes() {
            final Iterator<Attribute> iterator = element.getAttributes();
            return new Iterator<Attribute>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Attribute next() {
                    Attribute next = iterator.next();
                    attributes.add(next.getName().getLocalPart());
                    return next;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("");
                }
            };
        }

        @Override
        public Iterator getNamespaces() {
            return element.getNamespaces();
        }

        @Override
        public Attribute getAttributeByName(QName name) {
            attributes.add(name.getLocalPart());
            return element.getAttributeByName(name);
        }

        @Override
        public NamespaceContext getNamespaceContext() {
            return element.getNamespaceContext();
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return element.getNamespaceURI(prefix);
        }
    }

    protected static class FastStack<E> {
        private int size;
        private FastStackNode<E> tail;

        public E peek() {
            return peek(0);
        }

        public E peek(int n) {
            if (n < 0) {
                throw new IllegalArgumentException();
            }
            if (n > size - 1) {
                return null;
            }
            FastStackNode<E> node = tail;
            for (int i = 0; i < n; i++) {
                node = node.previous;
            }
            return node.value;
        }

        public E pop() {
            FastStackNode<E> oldTail = tail;
            tail = oldTail.previous;
            size--;
            return oldTail.value;
        }

        public void push(E value) {
            if (value == null) {
                throw new NullPointerException();
            }
            FastStackNode<E> newTail = new FastStackNode<>(value);
            newTail.previous = tail;
            tail = newTail;
            size++;
        }

        public int size() {
            return size;
        }

        public E[] toArray(E[] array) {
            if (array == null) {
                throw new NullPointerException();
            }
            if (array.length < size) {
                array = (E[]) Array.newInstance(array.getClass().getComponentType(), size);
            }
            FastStackNode<E> node = tail;
            for (int i = size - 1; i >= 0; i--) {
                array[i] = node.value;
                node = node.previous;
            }
            return array;
        }
    }

    private static class FastStackNode<E> {
        private FastStackNode<E> previous;
        private E value;

        private FastStackNode(E value) {
            this.value = value;
        }
    }

}
