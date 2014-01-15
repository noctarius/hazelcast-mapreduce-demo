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

package com.hazelcast.example.musicdb;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.example.musicdb.export.CustomSerializable;
import com.hazelcast.example.musicdb.export.MapReduceJobSerializer;
import com.hazelcast.example.musicdb.logic.TrackMapper;
import com.hazelcast.example.musicdb.logic.TrackSearchResult;
import com.hazelcast.example.musicdb.model.Artist;
import com.hazelcast.example.musicdb.model.ArtistRef;
import com.hazelcast.example.musicdb.model.Release;
import com.hazelcast.example.musicdb.model.Track;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapReduceTest {

    public static void main(String[] args) throws Exception {
        MapReduceStandaloneSerializer mapReduceSerializer = new MapReduceStandaloneSerializer();
        registerSerializers(mapReduceSerializer);

        try {
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(System.in));
            System.out.print("Which song are you searching for? ");
            String title = reader.readLine();
            System.out.print("Run [local] or [mapreduce]? ");
            boolean local = "local".equalsIgnoreCase(reader.readLine());

            ClientConfig clientConfig = new XmlClientConfigBuilder().build();

            SerializerConfig sc = new SerializerConfig()
                    .setTypeClass(CustomSerializable.class)
                    .setImplementation(mapReduceSerializer);
            clientConfig.getSerializationConfig().addSerializerConfig(sc);

            HazelcastInstance hz = HazelcastClient.newHazelcastClient(clientConfig);
            IMap<String, Release> releases = hz.getMap("releases");
            IMap<Long, Artist> artists = hz.getMap("artists");

            long start = System.nanoTime();
            Map<String, List<TrackSearchResult>> results;
            if (local) {
                results = new HashMap<String, List<TrackSearchResult>>();
                for (Map.Entry<String, Release> entry : releases.entrySet()) {
                    Release value = entry.getValue();
                    if (value.getTracks() == null) {
                        return;
                    }
                    Artist artist = null;
                    for (Track track : value.getTracks()) {
                        String trackTitle = track.getTitle().toLowerCase();
                        if (trackTitle.contains(title)) {
                            if (artist == null) {
                                artist = findArtist(artists, value);
                            }

                            if (artist != null) {
                                TrackSearchResult result = new TrackSearchResult();
                                result.setAlbumName(value.getTitle());
                                result.setArtist(artist);
                                result.setReleaseDate(value.getReleased());
                                result.setTrack(track);

                                String artistName = artist.getName();
                                List<TrackSearchResult> list = results.get(artistName);
                                if (list == null) {
                                    list = new ArrayList<TrackSearchResult>();
                                    results.put(artistName, list);
                                }
                                list.add(result);
                            }
                        }
                    }
                }

            } else {
                JobTracker tracker = hz.getJobTracker("default");
                Job<String, Release> job = tracker.newJob(KeyValueSource.fromMap(releases));

                TrackMapper trackMapper = new TrackMapper();
                trackMapper.title = title;

                ICompletableFuture<Map<String, List<TrackSearchResult>>> future =
                        job.chunkSize(100).mapper(trackMapper).submit();

                results = future.get();
            }
            long runtime = System.nanoTime() - start;

            for (Map.Entry<String, List<TrackSearchResult>> entry : results.entrySet()) {
                System.out.println("Artist: " + entry.getKey());
                for (TrackSearchResult searchResult : entry.getValue()) {
                    System.out.println("\tSong: " + searchResult.getTrack().getTitle()
                            + "(Album: " + searchResult.getAlbumName() + ")");
                }
            }
            System.out.println("Runtime: " + TimeUnit.NANOSECONDS.toMillis(runtime) + " ms");

        } finally {
            HazelcastClient.shutdownAll();
        }
    }

    private static void registerSerializers(MapReduceStandaloneSerializer mapReduceSerializer) {
        ClassLoader cl = MapReduceTest.class.getClassLoader();
        URL url = cl.getResource("META-INF/services/com.hazelcast.example.MapReduceJobSerializer");
        if (url != null) {
            try {
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));

                List<MapReduceJobSerializer> serializers = new ArrayList<>();
                String className;
                while ((className = reader.readLine()) != null) {
                    Class<? extends MapReduceJobSerializer> clazz = (Class) cl.loadClass(className);
                    MapReduceJobSerializer serializer = clazz.newInstance();
                    serializers.add(serializer);
                }

                mapReduceSerializer.registerBundleSerializers(serializers);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Artist findArtist(IMap<Long, Artist> artists, Release release) {
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

}
