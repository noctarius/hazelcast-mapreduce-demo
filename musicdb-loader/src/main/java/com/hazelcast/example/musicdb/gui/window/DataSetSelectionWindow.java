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
import com.googlecode.lanterna.gui.component.RadioCheckBoxList;
import com.hazelcast.example.musicdb.gui.component.ButtonAdapter;

import java.util.List;

public class DataSetSelectionWindow extends Window {

    private final RadioCheckBoxList dataSets;
    private final ButtonAdapter select;

    public DataSetSelectionWindow(List<String> dataSets) {
        super("Select DataSet to download");

        this.dataSets = new RadioCheckBoxList();
        this.addComponent(this.dataSets);
        for (int i = 0; i < dataSets.size(); i++) {
            this.dataSets.addItem(dataSets.get(i));
        }
        this.select = new ButtonAdapter("Select");
        this.addComponent(this.select);
    }

    public RadioCheckBoxList getDataSets() {
        return dataSets;
    }

    public ButtonAdapter getSelect() {
        return select;
    }
}
