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

import com.hazelcast.example.musicdb.model.*;
import com.hazelcast.example.musicdb.parsers.*;
import com.hazelcast.nio.serialization.ObjectDataOutputStream;
import com.hazelcast.nio.serialization.SerializationService;
import com.hazelcast.nio.serialization.SerializationServiceBuilder;
import com.hazelcast.partition.strategy.DefaultPartitioningStrategy;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Filler {

    public static void main(String[] args) throws Exception {
        boolean createSampleSet = Boolean.parseBoolean(args[2]);
        int sampleSetSize = 0;
        if (createSampleSet) {
            sampleSetSize = Integer.parseInt(args[3]);
        }
        boolean dryRun = Boolean.parseBoolean(args[1]);
        File basePath = new File(args[0]);
        if (basePath.exists()) {

            File artistsFile = null;
            File labelsFile = null;
            File mastersFile = null;
            File releasesFile = null;

            for (File child : basePath.listFiles()) {
                if (child.isFile()) {
                    String filename = child.getName().toLowerCase();
                    if (filename.endsWith("_artists.xml")) {
                        artistsFile = child;
                    } else if (filename.endsWith("_labels.xml")) {
                        labelsFile = child;
                    } else if (filename.endsWith("_masters.xml")) {
                        mastersFile = child;
                    } else if (filename.endsWith("_releases.xml")) {
                        releasesFile = child;
                    }
                }
            }

            if (artistsFile == null || labelsFile == null || mastersFile == null || releasesFile == null) {
                throw new IllegalStateException("Not all needed files found");
            }

            List<Artist> artists = null;
            ParserResult<Artist> artistParserResult = null;
            try (FileInputStream is = new FileInputStream(artistsFile)) {
                StaxParser<Artist> parser = new ArtistStaxParser(dryRun);
                artistParserResult = parser.parse(is);
                artists = artistParserResult.getResult();
            }

            List<Label> labels = null;
            ParserResult<Label> labelParserResult = null;
            try (FileInputStream is = new FileInputStream(labelsFile)) {
                StaxParser<Label> parser = new LabelStaxParser(dryRun);
                labelParserResult = parser.parse(is);
                labels = labelParserResult.getResult();
            }

            List<Master> masters = null;
            ParserResult<Master> masterParserResult = null;
            try (FileInputStream is = new FileInputStream(mastersFile)) {
                StaxParser<Master> parser = new MasterStaxParser(dryRun);
                masterParserResult = parser.parse(is);
                masters = masterParserResult.getResult();
            }

            List<Release> releases = null;
            ParserResult<Release> releaseParserResult = null;
            try (FileInputStream is = new FileInputStream(releasesFile)) {
                StaxParser<Release> parser = new ReleaseStaxParser(dryRun, sampleSetSize);
                releaseParserResult = parser.parse(is);
                releases = releaseParserResult.getResult();
            }

            System.out.println("Found " + artistParserResult);
            System.out.println("Found " + labelParserResult);
            System.out.println("Found " + masterParserResult);
            System.out.println("Found " + releaseParserResult);

            if (createSampleSet) {
                System.out.println("Collecting data for sample dataset...");

                Set<Artist> neededArtist = new HashSet<>(artists.size() / 1000);
                for (Release release : releases) {
                    for (ArtistRef ref : release.getArtists()) {
                        Artist artist = findArtist(ref.getArtistId(), artists);
                        if (artist != null) {
                            neededArtist.add(artist);
                        }
                    }
                    for (ArtistRef ref : release.getExtraArtists()) {
                        Artist artist = findArtist(ref.getArtistId(), artists);
                        if (artist != null) {
                            neededArtist.add(artist);
                        }
                    }
                }

                File file = new File(basePath, "dataset.sample");
                FileOutputStream fos = new FileOutputStream(file);
                SerializationServiceBuilder builder = new SerializationServiceBuilder();
                SerializationService ss = builder.setAllowUnsafe(true)
                        .setClassLoader(StaxParser.class.getClassLoader())
                        .setEnableCompression(true)
                        .setPartitioningStrategy(new DefaultPartitioningStrategy())
                        .setCheckClassDefErrors(true)
                        .setByteOrder(ByteOrder.BIG_ENDIAN)
                        .build();

                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ObjectDataOutputStream out = new ObjectDataOutputStream(bos, ss, ByteOrder.BIG_ENDIAN);

                System.out.println("Writing " + neededArtist.size() + " artists to sample dataset...");
                out.writeInt(neededArtist.size());
                for (Artist artist : neededArtist) {
                    out.writeObject(artist);
                }
                System.out.println("Writing " + labels.size() + " labels to sample dataset...");
                out.writeInt(labels.size());
                for (Label label : labels) {
                    out.writeObject(label);
                }
                System.out.println("Writing " + masters.size() + " masters to sample dataset...");
                out.writeInt(masters.size());
                for (Master master : masters) {
                    out.writeObject(master);
                }
                System.out.println("Writing " + releases.size() + " releases to sample dataset...");
                out.writeInt(releases.size());
                for (Release release : releases) {
                    out.writeObject(release);
                }
                out.flush();
                out.close();
            }
        }
    }

    private static Artist findArtist(long artistId, List<Artist> artists) {
        for (Artist artist : artists) {
            if (artist.getArtistId() == artistId) {
                return artist;
            }
        }
        return null;
    }

}
