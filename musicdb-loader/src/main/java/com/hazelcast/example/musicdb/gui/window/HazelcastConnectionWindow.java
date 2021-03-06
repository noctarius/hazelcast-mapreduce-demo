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
import com.googlecode.lanterna.gui.component.TextBox;
import com.hazelcast.example.musicdb.gui.component.ButtonAdapter;

public class HazelcastConnectionWindow extends Window {

    private final TextBox address = new TextBox("127.0.0.1");
    private final ButtonAdapter ok = new ButtonAdapter("Ok");

    public HazelcastConnectionWindow() {
        super("Make Hazelcast connection");
        this.addComponent(address);
        this.addComponent(ok);
    }

    public TextBox getAddress() {
        return address;
    }

    public ButtonAdapter getOk() {
        return ok;
    }
}
