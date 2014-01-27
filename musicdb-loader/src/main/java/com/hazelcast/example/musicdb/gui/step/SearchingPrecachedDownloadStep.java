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

import com.googlecode.lanterna.gui.Window;
import com.hazelcast.example.musicdb.gui.Step;
import com.hazelcast.example.musicdb.gui.StepOperation;
import com.hazelcast.example.musicdb.gui.window.WaitingWindow;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchingPrecachedDownloadStep extends Step {

    private static final Pattern CHECKSUM_PATTERN = Pattern.compile("discogs_([0-9]*)_CHECKSUM\\.txt");
    private static final String ARTISTS_PATTERN = "discogs_%DATE%_artists.xml.gz";
    private static final String LABELS_PATTERN = "discogs_%DATE%_labels.xml.gz";
    private static final String MASTERS_PATTERN = "discogs_%DATE%_masters.xml.gz";
    private static final String RELEASES_PATTERN = "discogs_%DATE%_releases.xml.gz";

    @Override
    public Window createWindow() {
        return new WaitingWindow("Searching precached datasets", "Searching...");
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                Path path = new File(".").toPath();
                getContext().put("path", path);

                List<String> dates = new ArrayList<>();

                File[] files = path.toFile().listFiles();
                for (File file : files) {
                    Matcher matcher = CHECKSUM_PATTERN.matcher(file.getName());
                    if (matcher.matches()) {
                        String date = matcher.group(1);
                        if (checkOtherFiles(files, date)) {
                            dates.add(date);
                        }
                    }
                }

                if (dates.size() == 0) {
                    transition("download");
                } else {
                    getContext().put("dates", dates);
                    transition("select");
                }
            }
        };
    }

    private boolean checkOtherFiles(File[] files, String date) {
        String fileName = ARTISTS_PATTERN.replace("%DATE%", date);
        if (!findFile(fileName, files)) {
            return false;
        }

        fileName = LABELS_PATTERN.replace("%DATE%", date);
        if (!findFile(fileName, files)) {
            return false;
        }

        fileName = MASTERS_PATTERN.replace("%DATE%", date);
        if (!findFile(fileName, files)) {
            return false;
        }

        fileName = RELEASES_PATTERN.replace("%DATE%", date);
        if (!findFile(fileName, files)) {
            return false;
        }

        return true;
    }

    private boolean findFile(String fileName, File[] files) {
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

}
