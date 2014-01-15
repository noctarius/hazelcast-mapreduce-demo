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

package com.hazelcast.example.musicdb;

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
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class StandaloneBackgroundServer {

    public static void main(String[] args) {
        MapReduceStandaloneSerializer mapReduceSerializer = new MapReduceStandaloneSerializer();
        registerSerializers(mapReduceSerializer);

        Config config = new XmlConfigBuilder().build();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);

        SerializerConfig sc = new SerializerConfig()
                .setTypeClass(CustomSerializable.class)
                .setImplementation(mapReduceSerializer);
        config.getSerializationConfig().addSerializerConfig(sc);

        Hazelcast.newHazelcastInstance(config);
    }

    private static void registerSerializers(MapReduceStandaloneSerializer mapReduceSerializer) {
        ClassLoader cl = MapReduceTest.class.getClassLoader();
        URL url = cl.getResource("META-INF/services/com.hazelcast.example.MapReduceJobSerializer");
        if (url != null) {
            try {
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));

                List<MapReduceJobSerializer> serializers = new ArrayList<>();
                String className;
                while ((className = reader.readLine()) != null) {
                    Class<? extends MapReduceJobSerializer> clazz = (Class) cl.loadClass(className);
                    MapReduceJobSerializer serializer = clazz.newInstance();
                    serializers.add(serializer);
                }

                mapReduceSerializer.registerBundleSerializers(serializers);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
