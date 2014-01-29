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

import com.hazelcast.example.musicdb.model.ArtistRef;
import com.hazelcast.example.musicdb.model.Image;
import com.hazelcast.example.musicdb.model.Master;
import com.hazelcast.example.musicdb.model.Video;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public class MasterStaxParser extends StaxParser<Master> {

    private static final String NAME_VARIATIONS = "namevariations";
    private static final String MASTERS = "masters";
    private static final String MASTER = "master";
    private static final String IMAGES = "images";
    private static final String IMAGE = "image";
    private static final String MAIN_RELEASE = "main_release";
    private static final String ARTISTS = "artists";
    private static final String ARTIST = "artist";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String NAME_VARIATION = "anv";
    private static final String JOIN = "join";
    private static final String ROLE = "role";
    private static final String TRACKS = "tracks";
    private static final String GENRES = "genres";
    private static final String GENRE = "genre";
    private static final String STYLES = "styles";
    private static final String STYLE = "style";
    private static final String TITLE = "title";
    private static final String DATA_QUALITY = "data_quality";
    private static final String VIDEOS = "videos";
    private static final String VIDEO = "video";
    private static final String DESCRIPTION = "description";
    private static final String YEAR = "year";
    private static final String NOTES = "notes";

    private Master master;
    private ArtistRef artistRef;
    private Video video;

    public MasterStaxParser(boolean dryRun) {
        super(dryRun, Master.class);
    }

    @Override
    protected XMLEvent startElement(String name, StartElement element,
                                    XMLEventReader reader, ParserTarget<Master> target) throws Exception {
        String previousElement = elementStack.peek(1);
        switch (name) {
            case MASTER:
                if (MASTERS.equals(previousElement)) {
                    master = new Master();
                    return null;
                }
                break;

            case IMAGE:
                if (IMAGES.equals(previousElement)) {
                    handleImage(element);
                    return null;
                }
                break;

            case VIDEO:
                if (VIDEOS.equals(previousElement)) {
                    video = new Video();
                    handleVideo(element);
                    return null;
                }
                break;

            case ARTIST:
                if (ARTISTS.equals(previousElement)) {
                    artistRef = new ArtistRef();
                    return null;
                }
                break;

            case ID:
                if (ARTIST.equals(previousElement)) {
                    return handleId(element, reader);
                }
                break;

            case TITLE:
                if (MASTER.equals(previousElement)) {
                    return handleTitle(element, reader);
                } else if (VIDEO.equals(previousElement)) {
                    return handleVideoTitle(element, reader);
                }
                break;

            case MAIN_RELEASE:
                if (MASTER.equals(previousElement)) {
                    return handleMainRelease(element, reader);
                }
                break;

            case YEAR:
                if (MASTER.equals(previousElement)) {
                    return handleYear(element, reader);
                }
                break;

            case NOTES:
                if (MASTER.equals(previousElement)) {
                    return handleNotes(element, reader);
                }
                break;

            case NAME:
                if (ARTIST.equals(previousElement)) {
                    return handleName(element, reader);
                }
                break;

            case NAME_VARIATION:
                if (ARTIST.equals(previousElement)) {
                    return handleNameVariation(element, reader);
                }
                break;

            case JOIN:
                if (ARTIST.equals(previousElement)) {
                    return handleJoin(element, reader);
                }
                break;

            case ROLE:
                if (ARTIST.equals(previousElement)) {
                    return handleRole(element, reader);
                }
                break;

            case DATA_QUALITY:
                if (MASTER.equals(previousElement)) {
                    return handleDateQuality(element, reader);
                }
                break;

            case GENRE:
                if (GENRES.equals(previousElement)) {
                    return handleGenre(element, reader);
                }
                break;

            case STYLE:
                if (STYLES.equals(previousElement)) {
                    return handleStyle(element, reader);
                }
                break;

            case DESCRIPTION:
                if (VIDEO.equals(previousElement)) {
                    return handleVideoDescription(element, reader);
                }
                break;

            case MASTERS:
            case IMAGES:
            case GENRES:
            case VIDEOS:
            case STYLES:
            case NAME_VARIATIONS:
            case ARTISTS:
            case TRACKS:
                return null;
        }
        notHandled(element.getName().getLocalPart(), Type.Element);
        return null;
    }

    @Override
    protected XMLEvent endElement(String name, EndElement element,
                                  XMLEventReader reader, ParserTarget<Master> target) {
        if (MASTER.equals(name)) {
            target.pushElement(master);
            master = null;
        } else if (VIDEO.equals(name)) {
            master.getVideos().add(video);
            video = null;
        } else if (ARTIST.equals(name)) {
            master.getArtists().add(artistRef);
            artistRef = null;
        }
        return null;
    }

    private XMLEvent handleTitle(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String title = nextEvent.asCharacters().getData();
            master.setTitle(title);
        }
        return nextEvent;
    }

    private XMLEvent handleMainRelease(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String mainRelease = nextEvent.asCharacters().getData();
            master.setMainRelease(mainRelease);
        }
        return nextEvent;
    }

    private XMLEvent handleYear(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String year = nextEvent.asCharacters().getData();
            master.setYear(Integer.parseInt(year));
        }
        return nextEvent;
    }

    private XMLEvent handleNotes(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String notes = nextEvent.asCharacters().getData();
            master.setNotes(notes);
        }
        return nextEvent;
    }

    private XMLEvent handleName(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String name = nextEvent.asCharacters().getData();
            artistRef.setName(name);
        }
        return nextEvent;
    }

    private XMLEvent handleNameVariation(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String nameVariation = nextEvent.asCharacters().getData();
            artistRef.setNameVariation(nameVariation);
        }
        return nextEvent;
    }

    private XMLEvent handleJoin(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String join = nextEvent.asCharacters().getData();
            artistRef.setJoin(join);
        }
        return nextEvent;
    }

    private XMLEvent handleRole(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String role = nextEvent.asCharacters().getData();
            artistRef.setRole(role);
        }
        return nextEvent;
    }

    private XMLEvent handleDateQuality(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String dataQuality = nextEvent.asCharacters().getData();
            master.setDataQuality(dataQuality);
        }
        return nextEvent;
    }

    private XMLEvent handleId(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String id = nextEvent.asCharacters().getData();
            artistRef.setArtistId(Long.parseLong(id));
        }
        return nextEvent;
    }

    private XMLEvent handleGenre(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String genre = nextEvent.asCharacters().getData();
            master.getGenres().add(genre);
        }
        return nextEvent;
    }

    private XMLEvent handleStyle(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String style = nextEvent.asCharacters().getData();
            master.getStyles().add(style);
        }
        return nextEvent;
    }

    private XMLEvent handleVideoTitle(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String title = nextEvent.asCharacters().getData();
            video.setTitle(title);
        }
        return nextEvent;
    }

    private XMLEvent handleVideoDescription(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String description = nextEvent.asCharacters().getData();
            video.setDescription(description);
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
        master.getImages().add(image);
    }

    private void handleVideo(StartElement element) {
        Iterator<Attribute> iterator = element.getAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            switch (attribute.getName().getLocalPart()) {
                case "duration":
                    video.setDuration(Long.parseLong(attribute.getValue()));
                    break;

                case "embed":
                    video.setEmbed(Boolean.parseBoolean(attribute.getValue()));
                    break;

                case "src":
                    video.setUri(attribute.getValue());
                    break;

                default:
                    notHandled(attribute.getName().getLocalPart(), Type.Attribute);
            }
        }
    }

}
