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

package com.hazelcast.example.musicdb.gui.component;

import com.googlecode.lanterna.gui.Action;
import com.googlecode.lanterna.gui.component.Button;

import java.lang.reflect.Field;

public class ButtonAdapter extends Button {

    private volatile Action action;

    public ButtonAdapter(String text) {
        super(text);
        try {
            Field field = Button.class.getDeclaredField("onPressEvent");
            field.setAccessible(true);
            field.set(this, new ActionAdapter());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    private class ActionAdapter implements Action {

        @Override
        public void doAction() {
            if (action != null) {
                action.doAction();
            }
        }
    }

}
