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
import com.hazelcast.example.musicdb.gui.Step;
import com.hazelcast.example.musicdb.gui.StepOperation;
import com.hazelcast.example.musicdb.gui.window.DataSetSelectionWindow;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DataSetSelectorStep extends Step {

    private volatile DataSetSelectionWindow window;

    @Override
    public Window createWindow() {
        List<String> dates = (List<String>) getContext().get("dates");
        window = new DataSetSelectionWindow(dates);
        return window;
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                window.getSelect().setAction(new Action() {
                    @Override
                    public void doAction() {
                        String selectedDate = (String) window.getDataSets().getCheckedItem();
                        selectedDate = selectedDate.replace("-", "");

                        Path path = new File(".").toPath();
                        getContext().put("path", path);

                        List<String> files = new ArrayList<>(5);
                        files.add("discogs_" + selectedDate + "_CHECKSUM.txt");
                        files.add("discogs_" + selectedDate + "_artists.xml.gz");
                        files.add("discogs_" + selectedDate + "_labels.xml.gz");
                        files.add("discogs_" + selectedDate + "_masters.xml.gz");
                        files.add("discogs_" + selectedDate + "_releases.xml.gz");
                        getContext().put("files", files);

                        transition();
                    }
                });
            }
        };
    }
}
