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

public class Master extends SerializableModel {

    private String mainRelease;
    private final List<Image> images = new ArrayList<>();
    private final List<ArtistRef> artists = new ArrayList<>();
    private final List<String> genres = new ArrayList<>();
    private final List<String> styles = new ArrayList<>();
    private String title;
    private String dataQuality;
    private final List<Video> videos = new ArrayList<>();
    private int year;
    private String notes;

    public String getMainRelease() {
        return mainRelease;
    }

    public void setMainRelease(String mainRelease) {
        this.mainRelease = mainRelease;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<ArtistRef> getArtists() {
        return artists;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getStyles() {
        return styles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataQuality() {
        return dataQuality;
    }

    public void setDataQuality(String dataQuality) {
        this.dataQuality = dataQuality;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(mainRelease);
        out.writeInt(images.size());
        for (Image image : images) {
            out.writeObject(image);
        }
        out.writeInt(artists.size());
        for (ArtistRef artist : artists) {
            out.writeObject(artist);
        }
        out.writeInt(genres.size());
        for (String genre : genres) {
            out.writeUTF(genre);
        }
        out.writeInt(styles.size());
        for (String style : styles) {
            out.writeUTF(style);
        }
        out.writeUTF(title);
        out.writeUTF(dataQuality);
        out.writeInt(videos.size());
        for (Video video : videos) {
            out.writeObject(video);
        }
        out.writeInt(year);
        out.writeUTF(notes);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        mainRelease = in.readUTF();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            images.add((Image) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            artists.add((ArtistRef) in.readObject());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            genres.add(in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            styles.add(in.readUTF());
        }
        title = in.readUTF();
        dataQuality = in.readUTF();
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            videos.add((Video) in.readObject());
        }
        year = in.readInt();
        notes = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Master master = (Master) o;

        if (year != master.year) return false;
        if (mainRelease != null ? !mainRelease.equals(master.mainRelease) : master.mainRelease != null) return false;
        if (title != null ? !title.equals(master.title) : master.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mainRelease != null ? mainRelease.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + year;
        return result;
    }
}
