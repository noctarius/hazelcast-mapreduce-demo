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

import java.util.HashMap;
import java.util.Map;

public class StepTransition {

    private final String name;
    private final Step step;

    private final Map<String, StepTransition> transitions = new HashMap<>();

    public StepTransition(Step step) {
        this("default", step);
    }

    public StepTransition(String name, Step step) {
        this.name = name;
        this.step = step;
    }

    public StepTransition addTransition(Step step) {
        return addTransition("default", step);
    }

    public StepTransition addTransition(String name, Step step) {
        StepTransition transition = new StepTransition(name, step);
        transitions.put(name, transition);
        return transition;
    }

    public StepTransition addTransition(StepTransition transition) {
        transitions.put(transition.name, transition);
        return transition;
    }

    public String getName() {
        return name;
    }

    public Step getStep() {
        return step;
    }

    StepTransition getTransition(String name) {
        if (transitions.size() == 1) {
            return transitions.values().iterator().next();
        }
        return transitions.get(name);
    }

}
