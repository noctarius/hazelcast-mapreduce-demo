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

package com.hazelcast.example.musicdb.model;

import com.hazelcast.nio.serialization.ArrayDataSerializableFactory;
import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.DataSerializerHook;
import com.hazelcast.util.ConstructorFunction;

public class MusicDbModelDataSerializerHook implements DataSerializerHook {

    public static final int F_ID = 10000;

    public static final int TYPE_ARTIST = 0;
    public static final int TYPE_ARTIST_REF = 1;
    public static final int TYPE_FORMAT = 2;
    public static final int TYPE_IDENTIFIER = 3;
    public static final int TYPE_IMAGE = 4;
    public static final int TYPE_LABEL = 5;
    public static final int TYPE_LABEL_REF = 6;
    public static final int TYPE_MASTER = 7;
    public static final int TYPE_RELEASE = 8;
    public static final int TYPE_TRACK = 9;
    public static final int TYPE_VIDEO = 10;

    private static final int LEN = TYPE_VIDEO + 1;

    @Override
    public int getFactoryId() {
        return F_ID;
    }

    @Override
    public DataSerializableFactory createFactory() {
        ConstructorFunction[] constructors = new ConstructorFunction[LEN];
        constructors[TYPE_ARTIST] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Artist();
            }
        };
        constructors[TYPE_ARTIST_REF] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new ArtistRef();
            }
        };
        constructors[TYPE_FORMAT] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Format();
            }
        };
        constructors[TYPE_IDENTIFIER] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Identifier();
            }
        };
        constructors[TYPE_IMAGE] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Image();
            }
        };
        constructors[TYPE_LABEL] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Label();
            }
        };
        constructors[TYPE_LABEL_REF] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new LabelRef();
            }
        };
        constructors[TYPE_MASTER] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Master();
            }
        };
        constructors[TYPE_RELEASE] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Release();
            }
        };
        constructors[TYPE_TRACK] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Track();
            }
        };
        constructors[TYPE_VIDEO] = new ConstructorFunction() {
            @Override
            public Object createNew(Object arg) {
                return new Video();
            }
        };
        return new ArrayDataSerializableFactory(constructors);
    }

}
