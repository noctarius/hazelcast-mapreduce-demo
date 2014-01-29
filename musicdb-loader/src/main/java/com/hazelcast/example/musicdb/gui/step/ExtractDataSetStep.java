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
import com.hazelcast.example.musicdb.gui.ProgressFileInputStream;
import com.hazelcast.example.musicdb.gui.Step;
import com.hazelcast.example.musicdb.gui.StepOperation;
import com.hazelcast.example.musicdb.gui.window.SingleProgressWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ExtractDataSetStep extends Step {

    private final SingleProgressWindow window = new SingleProgressWindow("Extracting dataset");

    @Override
    public Window createWindow() {
        return window;
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                List<String> files = new ArrayList<>((List<String>) getContext().get("files"));
                Path path = (Path) getContext().get("path");

                long temp = 0;
                List<File> fileList = new ArrayList<>(4);
                for (String file : files) {
                    if (file.contains("CHECKSUM")) continue;
                    File f = new File(path.toFile(), file);
                    temp += f.length();
                    fileList.add(f);
                }
                final long totalFileLength = temp;

                Path tempPath = Files.createTempDirectory("discoqs");
                getContext().put("tempPath", tempPath);
                tempPath.toFile().deleteOnExit();

                long finished = 0;
                for (final File file : fileList) {
                    window.getOwner().runInEventThread(new Action() {
                        @Override
                        public void doAction() {
                            window.getFileName().setText(file.getName() + "...");
                        }
                    });

                    final long base = finished;
                    if (file.getName().toLowerCase().endsWith(".gz")) {
                        extractToTempPath(file, tempPath, base, totalFileLength);
                    } else {
                        copyToTempPath(file, tempPath, base, totalFileLength);
                    }
                    finished += file.length();
                }

                transition();
            }
        };
    }

    private void extractToTempPath(File file, Path tempPath, final long base, final long totalFileLength) throws IOException {
        String outputFileName = file.getName().substring(0, file.getName().length() - 3);
        Path outputPath = tempPath.resolve(outputFileName);
        Files.createFile(outputPath);

        final ProgressFileInputStream pfis = new ProgressFileInputStream(file);
        final GZIPInputStream is = new GZIPInputStream(pfis);
        final OutputStream os = Files.newOutputStream(outputPath);

        byte[] buffer = new byte[1024];

        int len;
        while ((len = is.read(buffer)) > 0) {
            os.write(buffer, 0, len);
            window.getOwner().runInEventThread(new Action() {
                @Override
                public void doAction() {
                    window.getFileStatus().setProgress(pfis.getProgress());
                    double total = ((double) base + pfis.getPosition()) / totalFileLength;
                    window.getTotalStatus().setProgress(total);
                }
            });
        }

        is.close();
        os.close();
    }

    private void copyToTempPath(File file, Path tempPath, final long base, final long totalFileLength) throws IOException {
        final ProgressFileInputStream pfis = new ProgressFileInputStream(file);

        Path outputPath = tempPath.resolve(file.getName());
        final OutputStream os = Files.newOutputStream(outputPath);
        byte[] buffer = new byte[1024];

        int len;
        while ((len = pfis.read(buffer)) > 0) {
            os.write(buffer, 0, len);
            window.getOwner().runInEventThread(new Action() {
                @Override
                public void doAction() {
                    window.getFileStatus().setProgress(pfis.getProgress());
                    double total = ((double) base + pfis.getPosition()) / totalFileLength;
                    window.getTotalStatus().setProgress(total);
                }
            });
        }

        pfis.close();
        os.close();
    }

}
