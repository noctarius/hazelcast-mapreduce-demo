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

import java.util.ArrayList;
import java.util.List;

class CollectingParserTarget<T> extends ParserTarget<T> {

    private final List<T> elements = new ArrayList<>(1000000);

    CollectingParserTarget(boolean dryRun) {
        super(dryRun);
    }

    @Override
    protected void onNewElement(T element) {
        elements.add(element);
    }

    @Override
    public List<T> getElements() {
        return elements;
    }
}
