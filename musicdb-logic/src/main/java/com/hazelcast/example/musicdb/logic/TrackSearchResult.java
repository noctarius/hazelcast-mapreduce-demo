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

package com.hazelcast.example.musicdb.logic;

import com.hazelcast.example.musicdb.model.Artist;
import com.hazelcast.example.musicdb.model.Track;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class TrackSearchResult implements DataSerializable {
    private String albumName;
    private String releaseDate;
    private Artist artist;
    private Track track;

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(albumName);
        out.writeUTF(releaseDate);
        out.writeObject(artist);
        out.writeObject(track);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        albumName = in.readUTF();
        releaseDate = in.readUTF();
        artist = in.readObject();
        track = in.readObject();
    }

    @Override
    public String toString() {
        return "TrackSearchResult{" +
                "albumName='" + albumName + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", artist=" + artist +
                ", track=" + track +
                '}';
    }
}
