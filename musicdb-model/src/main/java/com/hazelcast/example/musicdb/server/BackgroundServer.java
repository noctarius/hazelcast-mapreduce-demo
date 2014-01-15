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

package com.hazelcast.example.musicdb.server;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.example.musicdb.export.CustomSerializable;
import com.hazelcast.example.musicdb.export.MapReduceJobSerializer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BackgroundServer {

    public static void main(String[] args) {
        MapReduceInternalSerializer mapReduceSerializer = new MapReduceInternalSerializer();

        Config config = new XmlConfigBuilder().build();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);

        SerializerConfig sc = new SerializerConfig()
                .setTypeClass(CustomSerializable.class)
                .setImplementation(mapReduceSerializer);
        config.getSerializationConfig().addSerializerConfig(sc);

        Hazelcast.newHazelcastInstance(config);
    }

    private static class MapReduceInternalSerializer implements StreamSerializer<CustomSerializable> {

        private final ConcurrentMap<String, MapReduceJobSerializer> serializers = new ConcurrentHashMap<>();

        @Override
        public void write(ObjectDataOutput out, CustomSerializable object) throws IOException {
            String className = object.getClass().getCanonicalName();
            MapReduceJobSerializer<CustomSerializable> serializer = serializers.get(className);
            if (serializer == null) {
                throw new IOException("Unable to serialize " + className);
            }
            out.writeUTF(className);
            serializer.write(out, object);
        }

        @Override
        public CustomSerializable read(ObjectDataInput in) throws IOException {
            String className = in.readUTF();
            MapReduceJobSerializer<CustomSerializable> serializer = serializers.get(className);
            if (serializer == null) {
                throw new IOException("Unable to deserialize " + className);
            }
            return serializer.read(in);
        }

        @Override
        public int getTypeId() {
            return 1000;
        }

        @Override
        public void destroy() {
        }

        public void registerBundleSerializers(List<MapReduceJobSerializer> serializers) {
            for (MapReduceJobSerializer serializer : serializers) {
                String className = serializer.serializationType().getCanonicalName();
                this.serializers.put(className, serializer);
            }
        }
    }
}
