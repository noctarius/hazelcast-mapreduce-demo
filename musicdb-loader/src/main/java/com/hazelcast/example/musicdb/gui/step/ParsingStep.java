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

package com.hazelcast.example.musicdb.gui.step;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Window;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.example.musicdb.gui.ProgressFileInputStream;
import com.hazelcast.example.musicdb.gui.ProgressFileInputStream.ProgressListener;
import com.hazelcast.example.musicdb.gui.Step;
import com.hazelcast.example.musicdb.gui.StepOperation;
import com.hazelcast.example.musicdb.gui.window.ParsingProcessWindow;
import com.hazelcast.example.musicdb.model.Artist;
import com.hazelcast.example.musicdb.model.Label;
import com.hazelcast.example.musicdb.model.Master;
import com.hazelcast.example.musicdb.model.Release;
import com.hazelcast.example.musicdb.parsers.ArtistStaxParser;
import com.hazelcast.example.musicdb.parsers.LabelStaxParser;
import com.hazelcast.example.musicdb.parsers.MasterStaxParser;
import com.hazelcast.example.musicdb.parsers.ParserTarget;
import com.hazelcast.example.musicdb.parsers.ReleaseStaxParser;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ParsingStep extends Step {

    private final ParsingProcessWindow window = new ParsingProcessWindow("Parsing dataset");

    @Override
    public Window createWindow() {
        return window;
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                HazelcastInstance hz = (HazelcastInstance) getContext().get("hazelcast");

                IMap<Long, Artist> artists = hz.getMap("artists");
                IMap<Long, Label> labels = hz.getMap("labels");
                IMap<String, Master> masters = hz.getMap("masters");
                IMap<String, Release> releases = hz.getMap("releases");

                Path tempPath = (Path) getContext().get("tempPath");
                File tempDir = tempPath.toFile();

                long totalFileLength = 0;
                for (File file : tempDir.listFiles()) {
                    if (file.isFile() && !file.getName().contains("CHECKSUM")) {
                        totalFileLength += file.length();
                    }
                }

                long totalProgress = 0;
                for (File file : tempDir.listFiles()) {
                    String fileName = file.getName();
                    if (fileName.contains("artists")) {
                        parseArtists(file, artists, totalProgress, totalFileLength);
                        totalProgress += file.length();
                    } else if (fileName.contains("labels")) {
                        parseLabels(file, labels, totalProgress, totalFileLength);
                        totalProgress += file.length();
                    } else if (fileName.contains("masters")) {
                        parseMasters(file, masters, totalProgress, totalFileLength);
                        totalProgress += file.length();
                    } else if (fileName.contains("releases")) {
                        parseReleases(file, releases, totalProgress, totalFileLength);
                        totalProgress += file.length();
                    }
                }
            }
        };
    }

    private void parseReleases(File file, final IMap<String, Release> releases,
                               long totalProgress, long totalFileLength) throws Exception {

        ParserTarget<Release> target = new ParserTarget<Release>(false) {
            @Override
            public List<Release> getElements() {
                return Collections.emptyList();
            }

            @Override
            protected void onNewElement(Release element) {
                String key = element.getTitle() + "-" + element.getMasterId()
                        + "-" + element.getCountry() + "-" + element.getReleased();
                releases.put(key, element);
            }
        };

        updateFilename(file.getName());
        ProgressFileInputStream pfis = new ProgressFileInputStream(file,
                createProgressListener(totalProgress, totalFileLength, target));

        ReleaseStaxParser staxParser = new ReleaseStaxParser(false, 0);
        staxParser.parse(pfis, target);
    }

    private void parseMasters(File file, final IMap<String, Master> masters,
                              long totalProgress, long totalFileLength) throws Exception {

        ParserTarget<Master> target = new ParserTarget<Master>(false) {
            @Override
            public List<Master> getElements() {
                return Collections.emptyList();
            }

            @Override
            protected void onNewElement(Master element) {
                String key = element.getTitle() + "-" + element.getYear()
                        + "-" + element.getMainRelease();
                masters.put(key, element);
            }
        };

        updateFilename(file.getName());
        ProgressFileInputStream pfis = new ProgressFileInputStream(file,
                createProgressListener(totalProgress, totalFileLength, target));

        MasterStaxParser staxParser = new MasterStaxParser(false);
        staxParser.parse(pfis, target);
    }

    private void parseLabels(File file, final IMap<Long, Label> labels,
                             long totalProgress, long totalFileLength) throws Exception {

        ParserTarget<Label> target = new ParserTarget<Label>(false) {
            @Override
            public List<Label> getElements() {
                return Collections.emptyList();
            }

            @Override
            protected void onNewElement(Label element) {
                labels.put(element.getLabelId(), element);
            }
        };

        updateFilename(file.getName());
        ProgressFileInputStream pfis = new ProgressFileInputStream(file,
                createProgressListener(totalProgress, totalFileLength, target));

        LabelStaxParser staxParser = new LabelStaxParser(false);
        staxParser.parse(pfis, target);
    }

    private void parseArtists(File file, final IMap<Long, Artist> artists,
                              long totalProgress, long totalFileLength) throws Exception {

        ParserTarget<Artist> target = new ParserTarget<Artist>(false) {
            @Override
            public List<Artist> getElements() {
                return Collections.emptyList();
            }

            @Override
            protected void onNewElement(Artist element) {
                artists.put(element.getArtistId(), element);
            }
        };

        updateFilename(file.getName());
        ProgressFileInputStream pfis = new ProgressFileInputStream(file,
                createProgressListener(totalProgress, totalFileLength, target));

        ArtistStaxParser staxParser = new ArtistStaxParser(false);
        staxParser.parse(pfis, target);
    }

    private <T> ProgressListener createProgressListener(final long base, final long totalFileLength,
                                                        final ParserTarget<T> parserTarget) {
        return new ProgressFileInputStream.ProgressListener() {
            @Override
            public void onProgress(long readBytes, long position, long length) {
                double currentProgress = (double) position / length;
                double totalProgress = (double) base + position / totalFileLength;

                updateProcessedData(currentProgress, totalProgress, parserTarget.getElementCount());
            }
        };
    }

    private void updateFilename(final String fileName) {
        window.getOwner().runInEventThread(new Action() {
            @Override
            public void doAction() {
                window.getFileName().setText(fileName);
            }
        });
    }

    private void updateProcessedData(final double current, final double total, final long processedRecords) {
        window.getOwner().runInEventThread(new Action() {
            @Override
            public void doAction() {
                window.getProcessRecords().setText("Processed records: " + processedRecords);
                window.getFileStatus().setProgress(current);
                window.getTotalStatus().setProgress(total);
            }
        });
    }

}
