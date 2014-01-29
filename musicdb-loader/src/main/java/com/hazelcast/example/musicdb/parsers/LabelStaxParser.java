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

import com.hazelcast.example.musicdb.model.Image;
import com.hazelcast.example.musicdb.model.Label;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public class LabelStaxParser extends StaxParser<Label> {

    private static final String NAME_VARIATIONS = "namevariations";
    private static final String LABELS = "labels";
    private static final String LABEL = "label";
    private static final String IMAGES = "images";
    private static final String IMAGE = "image";
    private static final String URLS = "urls";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String PROFILE = "profile";
    private static final String ID = "id";
    private static final String DATA_QUALITY = "data_quality";
    private static final String SUB_LABELS = "sublabels";
    private static final String CONTACT_INFO = "contactinfo";
    private static final String PARENT_LABEL = "parentLabel";

    private Label label;

    public LabelStaxParser(boolean dryRun) {
        super(dryRun, Label.class);
    }

    @Override
    protected XMLEvent startElement(String name, StartElement element,
                                    XMLEventReader reader, ParserTarget<Label> target) throws Exception {
        String previousElement = elementStack.peek(1);
        switch (name) {
            case LABEL:
                if (LABELS.equals(previousElement)) {
                    label = new Label();
                    return null;
                } else if (SUB_LABELS.equals(previousElement)) {
                    return handleSubLabel(element, reader);
                }
                break;

            case IMAGE:
                if (IMAGES.equals(previousElement)) {
                    handleImage(element);
                    return null;
                }
                break;

            case ID:
                if (LABEL.equals(previousElement)) {
                    return handleId(element, reader);
                }
                break;

            case NAME:
                if (LABEL.equals(previousElement)) {
                    return handleName(element, reader);
                }
                break;

            case CONTACT_INFO:
                if (LABEL.equals(previousElement)) {
                    return handleContactInfo(element, reader);
                }
                break;

            case PARENT_LABEL:
                if (LABEL.equals(previousElement)) {
                    return handleParentLabel(element, reader);
                }
                break;

            case DATA_QUALITY:
                if (LABEL.equals(previousElement)) {
                    return handleDateQuality(element, reader);
                }
                break;

            case URL:
                if (URLS.equals(previousElement)) {
                    return handleUrl(element, reader);
                }
                break;

            case PROFILE:
                if (LABEL.equals(previousElement)) {
                    return handleProfile(element, reader);
                }
                break;

            case SUB_LABELS:
            case IMAGES:
            case NAME_VARIATIONS:
            case URLS:
            case LABELS:
                return null;
        }
        notHandled(element.getName().getLocalPart(), Type.Element);
        return null;
    }

    @Override
    protected XMLEvent endElement(String name, EndElement element,
                                  XMLEventReader reader, ParserTarget<Label> target) {
        String previousElement = elementStack.peek(1);
        if (LABEL.equals(name)) {
            if (LABELS.equals(previousElement)) {
                target.pushElement(label);
                label = null;
            }
        }
        return null;
    }

    private XMLEvent handleName(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String name = nextEvent.asCharacters().getData();
            label.setName(name);
        }
        return nextEvent;
    }

    private XMLEvent handleContactInfo(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String contactInfo = nextEvent.asCharacters().getData();
            label.setContactInfo(contactInfo);
        }
        return nextEvent;
    }

    private XMLEvent handleParentLabel(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String parentLabel = nextEvent.asCharacters().getData();
            label.setParentLabel(parentLabel);
        }
        return nextEvent;
    }

    private XMLEvent handleDateQuality(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String dataQuality = nextEvent.asCharacters().getData();
            label.setDataQuality(dataQuality);
        }
        return nextEvent;
    }

    private XMLEvent handleId(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String id = nextEvent.asCharacters().getData();
            label.setLabelId(Long.parseLong(id));
        }
        return nextEvent;
    }

    private XMLEvent handleProfile(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String profile = nextEvent.asCharacters().getData();
            label.setProfile(profile);
        }
        return nextEvent;
    }

    private XMLEvent handleUrl(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String url = nextEvent.asCharacters().getData();
            label.getUrls().add(url);
        }
        return nextEvent;
    }

    private XMLEvent handleSubLabel(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String subLabel = nextEvent.asCharacters().getData();
            label.getSubLabels().add(subLabel);
        }
        return nextEvent;
    }

    private void handleImage(StartElement element) {
        Image image = new Image();
        Iterator<Attribute> iterator = element.getAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            switch (attribute.getName().getLocalPart()) {
                case "height":
                    image.setHeight(Integer.parseInt(attribute.getValue()));
                    break;

                case "width":
                    image.setWidth(Integer.parseInt(attribute.getValue()));
                    break;

                case "type":
                    image.setType(attribute.getValue());
                    break;

                case "uri":
                    image.setUri(attribute.getValue());
                    break;

                case "uri150":
                    image.setUri150(attribute.getValue());
                    break;

                default:
                    notHandled(attribute.getName().getLocalPart(), Type.Attribute);
            }
        }
        label.getImages().add(image);
    }

}
