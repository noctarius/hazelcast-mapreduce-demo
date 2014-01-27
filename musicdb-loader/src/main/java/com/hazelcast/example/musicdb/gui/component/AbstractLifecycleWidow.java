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

import com.googlecode.lanterna.gui.Component;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.layout.LayoutParameter;

public class AbstractLifecycleWidow extends Window {

    public AbstractLifecycleWidow(String title) {
        super(title);
    }

    @Override
    public void addComponent(Component component, LayoutParameter... layoutParameters) {
        super.addComponent(component, layoutParameters);
        if (component instanceof AbstractLifecycleComponent) {
            ((AbstractLifecycleComponent) component).onRegisterComponent(this);
        }
    }

    @Override
    public void removeComponent(Component component) {
        super.removeComponent(component);
        if (component instanceof AbstractLifecycleComponent) {
            ((AbstractLifecycleComponent) component).onUnregisterComponent(this);
        }
    }

    @Override
    protected void onClosed() {
        super.onClosed();
        for (int i = 0; i < getComponentCount(); i++) {
            Component component = getComponentAt(i);
            if (component instanceof AbstractLifecycleComponent) {
                ((AbstractLifecycleComponent) component).onUnregisterComponent(this);
            }
        }
    }
}
