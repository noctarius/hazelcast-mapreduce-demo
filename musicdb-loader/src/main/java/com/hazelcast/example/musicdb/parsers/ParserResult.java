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

package com.hazelcast.example.musicdb.parsers;

import java.util.List;

public class ParserResult<T> {

    private final int elementCount;
    private final long byteSize;
    private final List<T> result;
    private final Class<T> type;

    public ParserResult(int elementCount, long byteSize, List<T> result, Class<T> type) {
        this.elementCount = elementCount;
        this.byteSize = byteSize;
        this.result = result;
        this.type = type;
    }

    public int getElementCount() {
        return elementCount;
    }

    public long getByteSize() {
        return byteSize;
    }

    public List<T> getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "{ elementCount: " + getElementCount()
                + ", byteSize: " + (humanReadableByteCount(getByteSize(), true)
                + "(" + humanReadableByteCount(getByteSize(), false) + ")")
                + ", type: " + type.getSimpleName() + " }";
    }

    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
