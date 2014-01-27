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
import com.hazelcast.example.musicdb.gui.window.DownloadWindow;

import java.nio.file.Path;
import java.util.List;

public class ExecuteDataSetDownloadStep extends Step {

    private volatile DownloadWindow window;

    @Override
    public Window createWindow() {
        try {
            Path path = (Path) getContext().get("path");
            List<String> files = (List<String>) getContext().get("files");

            window = new DownloadWindow(files, path);
            return window;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                window.startDownload();
                window.setDownloadCompleted(createDownloadCompleted());
            }
        };
    }

    private Action createDownloadCompleted() {
        return new Action() {
            @Override
            public void doAction() {
                transition();
            }
        };
    }

}
