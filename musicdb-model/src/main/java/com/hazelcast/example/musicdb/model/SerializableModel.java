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

import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class SerializableModel implements IdentifiedDataSerializable {

    private static final Map<Class<?>, Integer> MAPPINGS = new HashMap<>();

    static {
        MAPPINGS.put(Artist.class, 0);
        MAPPINGS.put(ArtistRef.class, 1);
        MAPPINGS.put(Format.class, 2);
        MAPPINGS.put(Identifier.class, 3);
        MAPPINGS.put(Image.class, 4);
        MAPPINGS.put(Label.class, 5);
        MAPPINGS.put(LabelRef.class, 6);
        MAPPINGS.put(Master.class, 7);
        MAPPINGS.put(Release.class, 8);
        MAPPINGS.put(Track.class, 9);
        MAPPINGS.put(Video.class, 10);
    }

    private final int objectId;

    protected SerializableModel() {
        objectId = MAPPINGS.get(getClass());
    }

    @Override
    public int getFactoryId() {
        return MusicDbModelDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return objectId;
    }
}
