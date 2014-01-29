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

import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Label;
import com.googlecode.lanterna.gui.component.ProgressBar;

public class SingleProgressWindow extends Window {

    private final Label fileName;
    private final ProgressBar fileStatus;
    private final ProgressBar totalStatus;

    public SingleProgressWindow(String title) {
        super(title);
        this.fileName = new Label("");
        this.addComponent(this.fileName);
        this.fileStatus = new ProgressBar(60);
        this.addComponent(this.fileStatus);
        this.addComponent(new Label("Total status:"));
        this.totalStatus = new ProgressBar(60);
        this.addComponent(this.totalStatus);
    }

    public Label getFileName() {
        return fileName;
    }

    public ProgressBar getFileStatus() {
        return fileStatus;
    }

    public ProgressBar getTotalStatus() {
        return totalStatus;
    }
}
