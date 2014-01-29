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
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TestChecksumStep extends Step {

    private static final Pattern CHECKSUM_PATTERN = Pattern.compile("discogs_([0-9]*)_CHECKSUM\\.txt");

    private final SingleProgressWindow window = new SingleProgressWindow("Checking checksums");

    @Override
    public Window createWindow() {
        return window;
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                List<String> files = new ArrayList<>((List<String>) getContext().get("files"));
                Path path = (Path) getContext().get("path");

                List<Checksum> checksums = null;
                for (String file : files) {
                    if (CHECKSUM_PATTERN.matcher(file).matches()) {
                        checksums = readChecksumData(file, path);
                        break;
                    }
                }

                if (checksums == null) {
                    throw new RuntimeException("Cannot read checksum data");
                }

                long temp = 0;
                for (Checksum checksum : checksums) {
                    temp += checksum.fileLength;
                }
                final long totalFileLength = temp;

                long finished = 0;
                for (final Checksum checksum : checksums) {
                    window.getOwner().runInEventThread(new Action() {
                        @Override
                        public void doAction() {
                            window.getFileName().setText(checksum.fileName + "...");
                        }
                    });

                    digest.reset();
                    File f = new File(path.toFile(), checksum.fileName);
                    final ProgressFileInputStream pfis = new ProgressFileInputStream(f);
                    DigestInputStream dis = new DigestInputStream(pfis, digest);

                    final long base = finished;

                    // Read data
                    byte[] chunk = new byte[1024];
                    while (dis.read(chunk) > -1) {
                        window.getOwner().runInEventThread(new Action() {
                            @Override
                            public void doAction() {
                                window.getFileStatus().setProgress(pfis.getProgress());
                                double total = ((double) base + pfis.getPosition()) / totalFileLength;
                                window.getTotalStatus().setProgress(total);
                            }
                        });
                    }

                    finished += checksum.fileLength;
                    dis.close();

                    byte[] data = digest.digest();
                    if (!Arrays.equals(data, checksum.checksum)) {
                        getContext().put("error", "Checksum of file " + checksum.fileName
                                + " does not match, please try to re-download");
                        transition("error");
                        return;
                    }
                }

                transition("success");
            }
        };
    }

    private List<Checksum> readChecksumData(String file, Path path) throws IOException {
        File f = new File(path.toFile(), file);
        FileReader fr = new FileReader(f);
        LineNumberReader lnr = new LineNumberReader(fr);

        List<Checksum> checksums = new ArrayList<>();

        String line;
        while ((line = lnr.readLine()) != null) {
            String[] tokens = line.split(" ");
            if (tokens.length == 2) {
                checksums.add(new Checksum(tokens[1].substring(1), path, tokens[0]));
            }
        }
        return checksums;
    }

    private static class Checksum {
        private final String fileName;
        private final long fileLength;
        private final File file;
        private final byte[] checksum;

        private Checksum(String fileName, Path path, String checksum) {
            this.fileName = fileName;
            this.file = new File(path.toFile(), fileName);
            this.fileLength = this.file.length();
            this.checksum = new byte[32];

            int pos = 0;
            for (int i = 0; i < checksum.length(); i += 2) {
                int value = Integer.decode("0x" + checksum.substring(i, i + 2));
                this.checksum[pos++] = (byte) value;
            }
        }
    }

}
