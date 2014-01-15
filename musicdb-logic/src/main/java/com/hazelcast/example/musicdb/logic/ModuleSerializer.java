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

package com.hazelcast.example.musicdb.logic;

import com.hazelcast.example.musicdb.export.MapReduceJobSerializer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import java.io.IOException;

public class ModuleSerializer implements MapReduceJobSerializer<TrackMapper> {

    @Override
    public Class<TrackMapper> serializationType() {
        return TrackMapper.class;
    }

    @Override
    public void write(ObjectDataOutput out, TrackMapper object) throws IOException {
        object.writeData(out);
    }

    @Override
    public TrackMapper read(ObjectDataInput in) throws IOException {
        TrackMapper trackMapper = new TrackMapper();
        trackMapper.readData(in);
        return trackMapper;
    }

    @Override
    public int getTypeId() {
        return 1001;
    }

    @Override
    public void destroy() {
    }
}
