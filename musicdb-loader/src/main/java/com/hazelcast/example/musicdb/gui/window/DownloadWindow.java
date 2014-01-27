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

package com.hazelcast.example.musicdb.gui.window;

import com.github.axet.wget.WGet;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.URLInfo;
import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.ProgressBar;
import com.hazelcast.example.musicdb.AutoLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadWindow extends Window {

    private final Label[] labels;
    private final ProgressBar[] progressBars;
    private final DownloadInfo[] downloadInfos;

    private final ExecutorService executorService;
    private final File tmpDir;

    private volatile Action downloadCompleted;

    public DownloadWindow(List<String> files, Path tmpPath) throws Exception {
        super("Download DataSets");

        this.executorService = Executors.newFixedThreadPool(files.size() + 1);

        this.labels = new Label[files.size()];
        this.progressBars = new ProgressBar[files.size()];
        this.downloadInfos = new DownloadInfo[files.size()];
        this.tmpDir = tmpPath.toFile();
        this.tmpDir.mkdirs();

        for (int i = 0; i < files.size(); i++) {
            this.labels[i] = new Label(files.get(i));
            this.addComponent(this.labels[i]);

            this.progressBars[i] = new ProgressBar(60);
            this.addComponent(this.progressBars[i]);

            URL url = new URL(AutoLoader.BASE_URL + files.get(i));
            this.downloadInfos[i] = new DownloadInfo(url);
        }
    }

    public void setDownloadCompleted(Action downloadCompleted) {
        this.downloadCompleted = downloadCompleted;
    }

    public void startDownload() {
        for (final DownloadInfo downloadInfo : downloadInfos) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    URL source = downloadInfo.getSource();
                    String fileName = source.getFile().replace("/data/", "");

                    downloadInfo.extract();
                    downloadInfo.enableMultipart();
                    WGet wGet = new WGet(downloadInfo, new File(tmpDir, fileName));
                    wGet.download();
                }
            });
        }
        startMonitoring();
    }

    private boolean checkDownloadCompleted(long[] downloads, long[] totals) {
        for (int i = 0; i < downloads.length; i++) {
            if (totals[i] == 0 || downloads[i] != totals[i]) {
                return false;
            }
        }

        getOwner().runInEventThread(downloadCompleted);
        return true;
    }

    private void startMonitoring() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        final long[] totals = new long[downloadInfos.length];
                        final long[] downloads = new long[downloadInfos.length];
                        for (int i = 0; i < downloadInfos.length; i++) {
                            DownloadInfo downloadInfo = downloadInfos[i];
                            List<DownloadInfo.Part> parts = downloadInfo.getParts();

                            long total = 0;
                            long download = 0;
                            if (parts != null) {
                                for (DownloadInfo.Part part : parts) {
                                    if (part.getException() != null) {
                                        if (part.getState() == DownloadInfo.Part.States.ERROR) {
                                            throw new RuntimeException(part.getException());
                                        }
                                    }
                                    total += part.getLength();
                                    download += part.getCount();
                                }
                            } else {
                                if (downloadInfo.getException() != null) {
                                    if (downloadInfo.getState() == URLInfo.States.ERROR) {
                                        throw new RuntimeException(downloadInfo.getException());
                                    }
                                }
                                total = downloadInfo.getLength() == null ? 0 : downloadInfo.getLength();
                                download = downloadInfo.getCount();
                            }

                            totals[i] = total;
                            downloads[i] = download;
                        }

                        if (getOwner() != null) {
                            getOwner().runInEventThread(new Action() {
                                @Override
                                public void doAction() {
                                    for (int i = 0; i < downloadInfos.length; i++) {
                                        progressBars[i].setProgress(((double) downloads[i]) / totals[i]);
                                    }
                                }
                            });
                        }

                        if (checkDownloadCompleted(downloads, totals)) {
                            return;
                        }

                        Thread.sleep(500);
                    } catch (Exception e) {
                        // TODO
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
