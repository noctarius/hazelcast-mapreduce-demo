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

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.example.musicdb.export.CustomSerializable;
import com.hazelcast.example.musicdb.model.Artist;
import com.hazelcast.example.musicdb.model.ArtistRef;
import com.hazelcast.example.musicdb.model.Release;
import com.hazelcast.example.musicdb.model.Track;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import java.io.IOException;

public class TrackMapper
        implements Mapper<String, Release, String, TrackSearchResult>, HazelcastInstanceAware, CustomSerializable {

    public String title;

    private transient HazelcastInstance hazelcastInstance;
    private transient IMap<Long, Artist> artists;

    @Override
    public void map(String key, Release value, Context<String, TrackSearchResult> context) {
        if (value.getTracks() == null) {
            return;
        }
        Artist artist = null;
        for (Track track : value.getTracks()) {
            String title = track.getTitle().toLowerCase();
            if (title.contains(this.title)) {
                if (artist == null) {
                    artist = findArtist(value);
                }

                if (artist != null) {
                    TrackSearchResult result = new TrackSearchResult();
                    result.setAlbumName(value.getTitle());
                    result.setArtist(artist);
                    result.setReleaseDate(value.getReleased());
                    result.setTrack(track);

                    context.emit(artist.getName(), result);
                }
            }
        }
    }

    private Artist findArtist(Release release) {
        for (ArtistRef artistRef : release.getArtists()) {
            Artist artist = artists.get(artistRef.getArtistId());
            if (artist != null) {
                return artist;
            }
        }
        for (ArtistRef artistRef : release.getExtraArtists()) {
            Artist artist = artists.get(artistRef.getArtistId());
            if (artist != null) {
                return artist;
            }
        }
        return null;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.artists = hazelcastInstance.getMap("artists");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(title);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        title = in.readUTF();
    }
}
