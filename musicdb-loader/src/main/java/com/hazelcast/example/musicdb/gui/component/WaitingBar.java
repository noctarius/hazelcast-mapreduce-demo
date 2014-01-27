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

import com.googlecode.lanterna.gui.TextGraphics;
import com.googlecode.lanterna.gui.Theme;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.terminal.ACS;
import com.googlecode.lanterna.terminal.TerminalSize;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingBar extends AbstractLifecycleComponent {

    private static final char[] FILL_WAITER = {
            ACS.BLOCK_SPARSE, ACS.BLOCK_MIDDLE,
            ACS.BLOCK_DENSE, ACS.BLOCK_SOLID,
            ACS.BLOCK_DENSE, ACS.BLOCK_MIDDLE,
            ACS.BLOCK_SPARSE};

    private static final char FILL_EMPTY_CHAR = ' ';

    private final Timer timer = new Timer("waiting-bar-animator");

    private final int preferredWidth;

    private int position = -(FILL_WAITER.length + 1);

    public WaitingBar(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    @Override
    protected TerminalSize calculatePreferredSize() {
        return new TerminalSize(preferredWidth, 1);
    }

    @Override
    public void repaint(TextGraphics graphics) {
        char[] characters = new char[preferredWidth];
        Arrays.fill(characters, FILL_EMPTY_CHAR);

        updatePosition();

        int offset = 0;
        if (position < 0) {
            offset = FILL_WAITER.length - (FILL_WAITER.length + position);
        }
        for (int i = offset; i < FILL_WAITER.length; i++) {
            int pos = position + i;
            if (pos >= preferredWidth) {
                break;
            } else if (pos < 0) {
                break;
            }
            characters[pos] = FILL_WAITER[i];
        }

        graphics.applyTheme(Theme.Category.PROGRESS_BAR_COMPLETED);
        graphics.drawString(0, 0, new String(characters));
    }

    @Override
    protected void onRegisterComponent(Window window) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                invalidate();
            }
        }, 100, 100);
    }

    @Override
    protected void onUnregisterComponent(Window window) {
        timer.cancel();
        timer.purge();
    }

    private void updatePosition() {
        position++;
        if (position == preferredWidth + FILL_WAITER.length) {
            position = -FILL_WAITER.length;
        }
    }

}
