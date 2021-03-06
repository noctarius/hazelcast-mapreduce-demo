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

package com.hazelcast.example.musicdb.gui;

import com.googlecode.lanterna.gui.Window;

import java.util.Map;

public abstract class Step {

    private volatile ProcessManager processManager;

    public abstract Window createWindow();

    protected abstract StepOperation createOperation();

    protected final void transition() {
        transition("default");
    }

    protected final void transition(String name) {
        processManager.transition(name);
    }

    protected final Map<String, Object> getContext() {
        return processManager.getContext();
    }

    protected void onActivate() {
    }

    protected void onDeactivate() {
    }

    void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

}
