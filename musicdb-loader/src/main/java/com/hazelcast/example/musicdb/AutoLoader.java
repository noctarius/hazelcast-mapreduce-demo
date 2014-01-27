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

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.hazelcast.example.musicdb.gui.ProcessManager;
import com.hazelcast.example.musicdb.gui.StepTransition;
import com.hazelcast.example.musicdb.gui.step.AvailableDataSetStep;
import com.hazelcast.example.musicdb.gui.step.DataSetSelectorStep;
import com.hazelcast.example.musicdb.gui.step.ExecuteDataSetDownloadStep;
import com.hazelcast.example.musicdb.gui.step.SearchLocallyFirstQuestionStep;
import com.hazelcast.example.musicdb.gui.step.SearchingPrecachedDownloadStep;
import com.hazelcast.example.musicdb.gui.step.TestChecksumStep;
import com.hazelcast.transaction.impl.Transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutoLoader {

    public static final String BASE_URL = "http://www.discogs.com/data/";

    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final GUIScreen screen = TerminalFacade.createGUIScreen();
        if (screen == null) {
            throw new Exception("Couldn't allocate terminal");
        }

        StepTransition search = new StepTransition("search", new SearchingPrecachedDownloadStep());
        StepTransition select = search.addTransition("select", new DataSetSelectorStep());
        select.addTransition("default", new TestChecksumStep());

        StepTransition startDownload = new StepTransition("download", new AvailableDataSetStep());
        StepTransition selectDataSet = startDownload.addTransition("default", new DataSetSelectorStep());
        StepTransition download = selectDataSet.addTransition("default", new ExecuteDataSetDownloadStep());
        download.addTransition("default", new TestChecksumStep());

        StepTransition start = new StepTransition("default", new SearchLocallyFirstQuestionStep());
        start.addTransition(search);
        start.addTransition(startDownload);

        ProcessManager processManager = new ProcessManager(screen, es, start);
        screen.getScreen().startScreen();
        processManager.start();
        screen.getScreen().stopScreen();
    }

}
