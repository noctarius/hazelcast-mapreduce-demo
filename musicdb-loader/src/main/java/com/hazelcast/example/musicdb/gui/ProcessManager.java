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

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class ProcessManager {

    private final Map<String, Object> context = new HashMap<>();

    private final ExecutorService executorService;

    private final GUIScreen screen;

    private volatile StepTransition currentTransition;

    public ProcessManager(GUIScreen screen, ExecutorService executorService, StepTransition firstTransition) {
        this.executorService = executorService;
        this.screen = screen;
        this.currentTransition = firstTransition;
    }

    public void start() {
        Step s = currentTransition.getStep();
        activateStep(s);
        while (currentTransition != null) {
            s = currentTransition.getStep();
            activateStep(s);
        }
    }

    void transition(final String name) {
        screen.runInEventThread(new Action() {
            @Override
            public void doAction() {
                StepTransition transition = currentTransition.getTransition(name);
                Step s = currentTransition.getStep();
                currentTransition = transition;
                deactivateStep(s);
            }
        });
    }

    Map<String, Object> getContext() {
        return context;
    }

    private void activateStep(Step step) {
        step.setProcessManager(this);
        Window window = step.createWindow();
        executorService.submit(createDeActivator(step, true));
        screen.showWindow(window, GUIScreen.Position.CENTER);
    }

    private void deactivateStep(Step step) {
        executorService.submit(createDeActivator(step, false));
        screen.getActiveWindow().close();
    }

    private Runnable createDeActivator(final Step step, final boolean activate) {
        return new Runnable() {
            @Override
            public void run() {
                if (activate) {
                    step.onActivate();
                    StepOperation operation = step.createOperation();
                    if (operation != null) {
                        executorService.submit(createRunnableAdapter(operation));
                    }
                } else {
                    step.onDeactivate();
                }
            }
        };
    }

    private Runnable createRunnableAdapter(final StepOperation operation) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    operation.execute();
                } catch (Exception e) {
                    // TODO notify
                    e.printStackTrace();
                }
            }
        };
    }

}
