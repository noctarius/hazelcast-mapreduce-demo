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
import com.googlecode.lanterna.gui.component.Panel;
import com.hazelcast.example.musicdb.gui.component.ButtonAdapter;

public class SearchLocallyFirstQuestionWindow extends Window {

    private final ButtonAdapter yes;
    private final ButtonAdapter no;

    public SearchLocallyFirstQuestionWindow() {
        super("Search for precached datasets");
        this.yes = new ButtonAdapter("Yes");
        this.no = new ButtonAdapter("No");

        Panel panel = new Panel(Panel.Orientation.HORISONTAL);
        panel.addComponent(no);
        panel.addComponent(yes);

        this.addComponent(new Label("Do you first want to search for locally cached datasets?"));
        this.addComponent(panel);
    }

    public ButtonAdapter getYes() {
        return yes;
    }

    public ButtonAdapter getNo() {
        return no;
    }
}
