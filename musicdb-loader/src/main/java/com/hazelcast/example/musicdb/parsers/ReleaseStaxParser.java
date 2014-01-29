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

import com.hazelcast.example.musicdb.model.*;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;

public class ReleaseStaxParser extends StaxParser<Release> {

    private static final String RELEASES = "releases";
    private static final String RELEASE = "release";
    private static final String IMAGES = "images";
    private static final String IMAGE = "image";
    private static final String ARTISTS = "artists";
    private static final String ARTIST = "artist";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String NAME_VARIATION = "anv";
    private static final String JOIN = "join";
    private static final String ROLE = "role";
    private static final String TRACKS = "tracks";
    private static final String TITLE = "title";
    private static final String LABELS = "labels";
    private static final String LABEL = "label";
    private static final String EXTRA_ARTISTS = "extraartists";
    private static final String FORMATS = "formats";
    private static final String FORMAT = "format";
    private static final String DESCRIPTIONS = "descriptions";
    private static final String DESCRIPTION = "description";
    private static final String GENRES = "genres";
    private static final String GENRE = "genre";
    private static final String STYLES = "styles";
    private static final String STYLE = "style";
    private static final String COUNTRY = "country";
    private static final String RELEASED = "released";
    private static final String NOTES = "notes";
    private static final String MASTER_ID = "master_id";
    private static final String DATA_QUALITY = "data_quality";
    private static final String TRACK_LIST = "tracklist";
    private static final String TRACK = "track";
    private static final String POSITION = "position";
    private static final String DURATION = "duration";
    private static final String IDENTIFIERS = "identifiers";
    private static final String IDENTIFIER = "identifier";
    private static final String VIDEOS = "videos";
    private static final String VIDEO = "video";

    private final int sampleSetSize;
    private int currentSampleSetSize = 0;

    private Release release;
    private ArtistRef artistRef;
    private Format format;
    private Track track;
    private Video video;

    public ReleaseStaxParser(boolean dryRun, int sampleSetSize) {
        super(dryRun, Release.class);
        this.sampleSetSize = sampleSetSize;
    }

    @Override
    protected XMLEvent startElement(String name, StartElement element,
                                    XMLEventReader reader, ParserTarget<Release> target) throws Exception {
        String previousElement = elementStack.peek(1);
        switch (name) {
            case RELEASE:
                if (RELEASES.equals(previousElement)) {
                    release = new Release();
                    return null;
                }
                break;

            case IMAGE:
                if (IMAGES.equals(previousElement)) {
                    handleImage(element);
                    return null;
                }
                break;

            case ARTIST:
                if (ARTISTS.equals(previousElement)
                        || EXTRA_ARTISTS.equals(previousElement)) {
                    artistRef = new ArtistRef();
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

            case TRACK:
                if (TRACK_LIST.equals(previousElement)) {
                    track = new Track();
                    return null;
                }
                break;

            case TITLE:
                if (RELEASE.equals(previousElement)) {
                    return handleReleaseTitle(element, reader);
                } else if (TRACK.equals(previousElement)) {
                    return handleTrackTitle(element, reader);
                } else if (VIDEO.equals(previousElement)) {
                    return handleVideoTitle(element, reader);
                }
                break;

            case LABEL:
                if (LABELS.equals(previousElement)) {
                    handleLabel(element);
                    return null;
                }
                break;

            case FORMAT:
                if (FORMATS.equals(previousElement)) {
                    format = new Format();
                    handleFormat(element);
                    return null;
                }
                break;

            case DESCRIPTION:
                if (DESCRIPTIONS.equals(previousElement)) {
                    return handleFormatDescription(element, reader);
                } else if (VIDEO.equals(previousElement)) {
                    return handleVideoDescription(element, reader);
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

            case COUNTRY:
                if (RELEASE.equals(previousElement)) {
                    return handleCountry(element, reader);
                }
                break;

            case RELEASED:
                if (RELEASE.equals(previousElement)) {
                    return handleReleased(element, reader);
                }
                break;

            case NOTES:
                if (RELEASE.equals(previousElement)) {
                    return handleNotes(element, reader);
                }
                break;

            case MASTER_ID:
                if (RELEASE.equals(previousElement)) {
                    return handleMasterId(element, reader);
                }
                break;

            case DATA_QUALITY:
                if (RELEASE.equals(previousElement)) {
                    return handleDataQuality(element, reader);
                }
                break;

            case POSITION:
                if (TRACK.equals(previousElement)) {
                    return handlePosition(element, reader);
                }
                break;

            case DURATION:
                if (TRACK.equals(previousElement)) {
                    return handleDuration(element, reader);
                }
                break;

            case IDENTIFIER:
                if (IDENTIFIERS.equals(previousElement)) {
                    handleIdentifier(element);
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

            case RELEASES:
            case IMAGES:
            case ARTISTS:
            case LABELS:
            case EXTRA_ARTISTS:
            case FORMATS:
            case GENRES:
            case STYLES:
            case TRACK_LIST:
            case IDENTIFIERS:
            case DESCRIPTIONS:
            case VIDEOS:
            case TRACKS:
                return null;
        }
        notHandled(element.getName().getLocalPart(), Type.Element);
        return null;
    }

    @Override
    protected XMLEvent endElement(String name, EndElement element,
                                  XMLEventReader reader, ParserTarget<Release> target) throws Exception {
        String previousElement = elementStack.peek(1);
        if (RELEASE.equals(name)) {
            if (sampleSetSize > 0 && ++currentSampleSetSize == sampleSetSize) {
                cancel();
            }
            target.pushElement(release);
            release = null;
        } else if (VIDEO.equals(name)) {
            release.getVideos().add(video);
            video = null;
        } else if (FORMAT.equals(name)) {
            release.getFormats().add(format);
            format = null;
        } else if (ARTIST.equals(name)) {
            if (ARTISTS.equals(previousElement)) {
                release.getArtists().add(artistRef);
                artistRef = null;
            } else if (EXTRA_ARTISTS.equals(previousElement)) {
                release.getExtraArtists().add(artistRef);
                artistRef = null;
            }
        } else if (TRACK.equals(name)) {
            release.getTracks().add(track);
            track = null;
        }
        return null;
    }

    private void handleIdentifier(StartElement element) {
        Identifier identifier = new Identifier();
        Iterator<Attribute> iterator = element.getAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            switch (attribute.getName().getLocalPart()) {
                case "description":
                    identifier.setDescription(attribute.getValue());
                    break;

                case "type":
                    identifier.setType(attribute.getValue());
                    break;

                case "value":
                    identifier.setValue(attribute.getValue());
                    break;

                default:
                    notHandled(attribute.getName().getLocalPart(), Type.Attribute);
            }
        }
        release.getIdentifiers().add(identifier);
    }

    private void handleLabel(StartElement element) {
        LabelRef label = new LabelRef();
        Iterator<Attribute> iterator = element.getAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            switch (attribute.getName().getLocalPart()) {
                case "catno":
                    label.setCatNo(attribute.getValue());
                    break;

                case "name":
                    label.setName(attribute.getValue());
                    break;

                default:
                    notHandled(attribute.getName().getLocalPart(), Type.Attribute);
            }
        }
        release.getLabels().add(label);
    }

    private void handleFormat(StartElement element) {
        Iterator<Attribute> iterator = element.getAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            switch (attribute.getName().getLocalPart()) {
                case "qty":
                    format.setQty(Integer.parseInt(attribute.getValue()));
                    break;

                case "name":
                    format.setName(attribute.getValue());
                    break;

                case "text":
                    format.setText(attribute.getValue());
                    break;

                default:
                    notHandled(attribute.getName().getLocalPart(), Type.Attribute);
            }
        }
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

    private XMLEvent handleTrackTitle(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String title = nextEvent.asCharacters().getData();
            track.setTitle(title);
        }
        return nextEvent;
    }

    private XMLEvent handlePosition(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String position = nextEvent.asCharacters().getData();
            track.setPosition(position);
        }
        return nextEvent;
    }

    private XMLEvent handleDuration(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String duration = nextEvent.asCharacters().getData();
            track.setDuration(duration);
        }
        return nextEvent;
    }

    private XMLEvent handleCountry(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String country = nextEvent.asCharacters().getData();
            release.setCountry(country);
        }
        return nextEvent;
    }

    private XMLEvent handleReleased(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String released = nextEvent.asCharacters().getData();
            release.setReleased(released);
        }
        return nextEvent;
    }

    private XMLEvent handleNotes(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String notes = nextEvent.asCharacters().getData();
            release.setNotes(notes);
        }
        return nextEvent;
    }

    private XMLEvent handleMasterId(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String masterId = nextEvent.asCharacters().getData();
            release.setMasterId(Integer.parseInt(masterId));
        }
        return nextEvent;
    }

    private XMLEvent handleDataQuality(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String dataQuality = nextEvent.asCharacters().getData();
            release.setDataQuality(dataQuality);
        }
        return nextEvent;
    }

    private XMLEvent handleGenre(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String genre = nextEvent.asCharacters().getData();
            release.getGenres().add(genre);
        }
        return nextEvent;
    }

    private XMLEvent handleStyle(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String style = nextEvent.asCharacters().getData();
            release.getStyles().add(style);
        }
        return nextEvent;
    }

    private XMLEvent handleFormatDescription(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String description = nextEvent.asCharacters().getData();
            format.getDescriptions().add(description);
        }
        return nextEvent;
    }

    private XMLEvent handleReleaseTitle(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String title = nextEvent.asCharacters().getData();
            release.setTitle(title);
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

    private XMLEvent handleRole(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String role = nextEvent.asCharacters().getData();
            artistRef.setRole(role);
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

    private XMLEvent handleId(StartElement element, XMLEventReader reader) throws Exception {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isCharacters()) {
            String id = nextEvent.asCharacters().getData();
            artistRef.setArtistId(Long.parseLong(id));
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
        release.getImages().add(image);
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
