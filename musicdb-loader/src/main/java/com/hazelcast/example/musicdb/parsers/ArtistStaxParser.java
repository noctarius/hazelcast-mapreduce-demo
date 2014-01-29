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

import com.hazelcast.example.musicdb.model.Artist;
import com.hazelcast.example.musicdb.model.Image;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public class ArtistStaxParser extends StaxParser<Artist> {

    private static final String NAME_VARIATIONS = "namevariations";
    private static final String ARTISTS = "artists";
    private static final String ARTIST = "artist";
    private static final String IMAGES = "images";
    private static final String IMAGE = "image";
    private static final String URLS = "urls";
    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String PROFILE = "profile";
    private static final String ID = "id";
    private static final String DATA_QUALITY = "data_quality";
    private static final String ALIASES = "aliases";
    private static final String GROUPS = "groups";
    private static final String REALNAME = "realname";
    private static final String MEMBERS = "members";

    private Artist artist;

    public ArtistStaxParser(boolean dryRun) {
        super(dryRun, Artist.class);
    }

    @Override
    protected XMLEvent startElement(String name, StartElement element,
                                    XMLEventReader reader, ParserTarget<Artist> target) throws Exception {
        String previousElement = elementStack.peek(1);
        switch (name) {
            case ARTIST:
                if (ARTISTS.equals(previousElement)) {
                    artist = new Artist();
                    return null;
                }
                break;

            case IMAGE:
                if (IMAGES.equals(previousElement)) {
                    handleImage(element);
                    return null;
                }
                break;

            case ID:
                if (ARTIST.equals(previousElement)) {
                    return handleId(element, reader);
                }
                break;

            case NAME:
                if (ARTIST.equals(previousElement)) {
                    return handleName(element, reader);
                } else if (ALIASES.equals(previousElement)) {
                    return handleAlias(element, reader);
                } else if (GROUPS.equals(previousElement)) {
                    return handleGroup(element, reader);
                } else if (NAME_VARIATIONS.equals(previousElement)) {
                    return handleNameVariation(element, reader);
                } else if (MEMBERS.equals(previousElement)) {
                    return handleMember(element, reader);
                }
                break;

            case REALNAME:
                if (ARTIST.equals(previousElement)) {
                    return handleRealname(element, reader);
                }
                break;

            case DATA_QUALITY:
                if (ARTIST.equals(previousElement)) {
                    return handleDateQuality(element, reader);
                }
                break;

            case URL:
                if (URLS.equals(previousElement)) {
                    return handleUrl(element, reader);
                }
                break;

            case PROFILE:
                if (ARTIST.equals(previousElement)) {
                    return handleProfile(element, reader);
                }
                break;

            case ARTISTS:
            case IMAGES:
            case ALIASES:
            case GROUPS:
            case NAME_VARIATIONS:
            case URLS:
            case MEMBERS:
                return null;
        }
        notHandled(element.getName().getLocalPart(), Type.Element);
        return null;
    }

    @Override
    protected XMLEvent endElement(String name, EndElement element,
                                  XMLEventReader reader, ParserTarget<Artist> target) {
        if (ARTIST.equals(name)) {
            target.pushElement(artist);
            artist = null;
        }
        return null;
    }

    private XMLEvent handleName(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String name = nextEvent.asCharacters().getData();
            artist.setName(name);
        }
        return nextEvent;
    }

    private XMLEvent handleRealname(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String realname = nextEvent.asCharacters().getData();
            artist.setRealname(realname);
        }
        return nextEvent;
    }

    private XMLEvent handleDateQuality(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String dataQuality = nextEvent.asCharacters().getData();
            artist.setDataQuality(dataQuality);
        }
        return nextEvent;
    }

    private XMLEvent handleId(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String id = nextEvent.asCharacters().getData();
            artist.setArtistId(Long.parseLong(id));
        }
        return nextEvent;
    }

    private XMLEvent handleProfile(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String profile = nextEvent.asCharacters().getData();
            artist.setProfile(profile);
        }
        return nextEvent;
    }

    private XMLEvent handleUrl(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String url = nextEvent.asCharacters().getData();
            artist.getUrls().add(url);
        }
        return nextEvent;
    }

    private XMLEvent handleNameVariation(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String nameVariation = nextEvent.asCharacters().getData();
            artist.getNameVariations().add(nameVariation);
        }
        return nextEvent;
    }

    private XMLEvent handleMember(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String nameVariation = nextEvent.asCharacters().getData();
            artist.getMembers().add(nameVariation);
        }
        return nextEvent;
    }

    private XMLEvent handleAlias(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String alias = nextEvent.asCharacters().getData();
            artist.getAliases().add(alias);
        }
        return nextEvent;
    }

    private XMLEvent handleGroup(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String group = nextEvent.asCharacters().getData();
            artist.getGroups().add(group);
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
        artist.getImages().add(image);
    }

}
