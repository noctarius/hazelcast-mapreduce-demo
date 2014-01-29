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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ProgressFileInputStream extends FileInputStream {

    private final ProgressListener listener;
    private final long length;
    private long pos;

    public ProgressFileInputStream(File file) throws FileNotFoundException {
        this(file, null);
    }

    public ProgressFileInputStream(File file, ProgressListener listener) throws FileNotFoundException {
        super(file);
        this.listener = listener;
        this.length = file.length();
    }

    @Override
    public int read() throws IOException {
        int length = super.read();
        if (length > -1) pos += length;
        notifyAction(length);
        return length;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int length = super.read(b);
        if (length > -1) pos += length;
        notifyAction(length);
        return length;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int length = super.read(b, off, len);
        if (length > -1) pos += length;
        notifyAction(length);
        return length;
    }

    @Override
    public long skip(long n) throws IOException {
        long length = super.skip(n);
        if (length > -1) pos += length;
        notifyAction(length);
        return length;
    }

    public double getProgress() {
        return ((double) pos) / length;
    }

    public long getPosition() {
        return pos;
    }

    private void notifyAction(long length) {
        if (listener != null) {
            listener.onProgress(length, pos, length);
        }
    }

    public static interface ProgressListener {
        void onProgress(long readBytes, long position, long length);
    }
}
