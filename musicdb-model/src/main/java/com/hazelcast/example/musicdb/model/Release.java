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

package com.hazelcast.example.musicdb.model;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Release extends SerializableModel {

    private final List<Image> images = new ArrayList<>();
    private final List<ArtistRef> artists = new ArrayList<>();
    private String title;
    private final List<ArtistRef> extraArtists = new ArrayList<>();
    private final List<Format> formats = new ArrayList<>();
    private final List<String> genres = new ArrayList<>();
    private final List<String> styles = new ArrayList<>();
    private String country;
    private String released;
    private String notes;
    private int masterId;
    private String dataQuality;
    private final List<Track> tracks = new ArrayList<>();
    private final List<Identifier> identifiers = new ArrayList<>();
    private final List<Video> videos = new ArrayList<>();
    private final List<LabelRef> labels = new ArrayList<>();

    public List<Image> getImages() {
        return images;
    }

    public List<ArtistRef> getArtists() {
        return artists;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ArtistRef> getExtraArtists() {
        return extraArtists;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getStyles() {
        return styles;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getDataQuality() {
        return dataQuality;
    }

    public void setDataQuality(String dataQuality) {
        this.dataQuality = dataQuality;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public List<LabelRef> getLabels() {
        return labels;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(images.size());
        for (Image image : images) {
            out.writeObject(image);
        }
        out.writeInt(artists.size());
        for (ArtistRef artist : artists) {
            out.writeObject(artist);
        }
        out.writeUTF(title);
        out.writeInt(extraArtists.size());
        for (ArtistRef artist : extraArtists) {
            out.writeObject(artist);
        }
        out.writeInt(formats.size());
        for (Format format : formats) {
            out.writeObject(format);
        }
        out.writeInt(genres.size());
        for (String genre : genres) {
            out.writeUTF(genre);
        }
        out.writeInt(styles.size());
        for (String style : styles) {
            out.writeUTF(style);
        }
        out.writeUTF(country);
        out.writeUTF(released);
        out.writeUTF(notes);
        out.writeInt(masterId);
        out.writeUTF(dataQuality);
        out.writeInt(tracks.size());
        for (Track track : tracks) {
            out.writeObject(track);
        }
        out.writeInt(identifiers.size());
        for (Identifier identifier : identifiers) {
            out.writeObject(identifier);
        }
        out.writeInt(videos.size());
        for (Video video : videos) {
            out.writeObject(video);
        }
        out.writeInt(labels.size());
        for (LabelRef label : labels) {
            out.writeObject(label);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            images.add((Image) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            artists.add((ArtistRef) in.readObject());
        }
        title = in.readUTF();
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            extraArtists.add((ArtistRef) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            formats.add((Format) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            genres.add(in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            styles.add(in.readUTF());
        }
        country = in.readUTF();
        released = in.readUTF();
        notes = in.readUTF();
        masterId = in.readInt();
        dataQuality = in.readUTF();
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            tracks.add((Track) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            identifiers.add((Identifier) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            videos.add((Video) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            labels.add((LabelRef) in.readObject());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Release release = (Release) o;

        if (masterId != release.masterId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return masterId;
    }
}
