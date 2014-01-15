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
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.example.musicdb.model.Artist;
import com.hazelcast.example.musicdb.model.Label;
import com.hazelcast.example.musicdb.model.Master;
import com.hazelcast.example.musicdb.model.Release;
import com.hazelcast.example.musicdb.model.Track;
import com.hazelcast.example.musicdb.parsers.StaxParser;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.monitor.LocalMapStats;
import com.hazelcast.nio.serialization.ObjectDataInputStream;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;
import com.hazelcast.partition.strategy.DefaultPartitioningStrategy;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

public class SampleLoader {

    public static void main(String[] args) throws Exception {
        File basePath = new File(args[0]);
        File sample = new File(basePath, "dataset.sample");

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sample));
        SerializationServiceBuilder builder = new SerializationServiceBuilder();
        SerializationService ss = builder.setAllowUnsafe(true)
                .setClassLoader(StaxParser.class.getClassLoader())
                .setEnableCompression(true)
                .setPartitioningStrategy(new DefaultPartitioningStrategy())
                .setCheckClassDefErrors(true)
                .setByteOrder(ByteOrder.BIG_ENDIAN)
                .build();

        ObjectDataInputStream in = new ObjectDataInputStream(bis, ss);

        HazelcastInstance hz = HazelcastClient.newHazelcastClient();
        IMap<Long, Artist> artists = hz.getMap("artists");
        IMap<Long, Label> labels = hz.getMap("labels");
        IMap<String, Master> masters = hz.getMap("masters");
        IMap<String, Release> releases = hz.getMap("releases");

        System.out.println("Reading artists...");
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            Artist artist = (Artist) in.readObject();
            artists.put(artist.getArtistId(), artist);
        }

        System.out.println("Reading labels...");
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            Label label = (Label) in.readObject();
            labels.put(label.getLabelId(), label);
        }

        System.out.println("Reading masters...");
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            Master master = (Master) in.readObject();
            String key = master.getTitle() + "-" + master.getYear()
                    + "-" + master.getMainRelease();
            masters.put(key, master);
        }

        System.out.println("Reading releases...");
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            Release release = (Release) in.readObject();
            String key = release.getTitle() + "-" + release.getMasterId()
                    + "-" + release.getCountry() + "-" + release.getReleased();
            releases.put(key, release);
        }

        LocalMapStats stats = artists.getLocalMapStats();
        System.out.println("Artists heapcost: " + humanReadableByteCount(stats.getHeapCost(), true)
                + "(" + humanReadableByteCount(stats.getHeapCost(), false) + ")");

        stats = labels.getLocalMapStats();
        System.out.println("Labels heapcost: " + humanReadableByteCount(stats.getHeapCost(), true)
                + "(" + humanReadableByteCount(stats.getHeapCost(), false) + ")");

        stats = masters.getLocalMapStats();
        System.out.println("Masters heapcost: " + humanReadableByteCount(stats.getHeapCost(), true)
                + "(" + humanReadableByteCount(stats.getHeapCost(), false) + ")");

        stats = releases.getLocalMapStats();
        System.out.println("Releases heapcost: " + humanReadableByteCount(stats.getHeapCost(), true)
                + "(" + humanReadableByteCount(stats.getHeapCost(), false) + ")");

        HazelcastClient.shutdownAll();
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
