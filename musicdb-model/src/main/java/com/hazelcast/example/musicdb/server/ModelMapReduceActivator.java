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
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.example.musicdb.export.CustomSerializable;
import com.hazelcast.example.musicdb.export.MapReduceJobSerializer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModelMapReduceActivator implements BundleListener, BundleActivator {

    private final MapReduceSerializer mapReduceSerializer = new MapReduceSerializer();
    private HazelcastInstance hazelcastInstance;

    @Override
    public void start(BundleContext context) throws Exception {
        context.addBundleListener(this);

        Config config = new XmlConfigBuilder().build();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");

        SerializerConfig sc = new SerializerConfig()
                .setTypeClass(CustomSerializable.class)
                .setImplementation(mapReduceSerializer);

        config.getSerializationConfig().addSerializerConfig(sc);
        config.setClassLoader(getClass().getClassLoader());

        hazelcastInstance = Hazelcast.newHazelcastInstance(config);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        hazelcastInstance.getLifecycleService().shutdown();
        context.removeBundleListener(this);
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STARTED) {
            Bundle bundle = event.getBundle();
            URL url = bundle.getResource("META-INF/services/com.hazelcast.example.MapReduceJobSerializer");
            if (url != null) {
                try {
                    LineNumberReader reader = new LineNumberReader(new InputStreamReader(url.openStream()));

                    List<MapReduceJobSerializer> serializers = new ArrayList<>();
                    String className;
                    while ((className = reader.readLine()) != null) {
                        Class<? extends MapReduceJobSerializer> clazz = bundle.loadClass(className);
                        MapReduceJobSerializer serializer = clazz.newInstance();
                        serializers.add(serializer);
                    }

                    mapReduceSerializer.registerBundleSerializers(bundle, serializers);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else if (event.getType() == BundleEvent.STOPPED) {
            mapReduceSerializer.unregisterBundleSerializers(event.getBundle());
        }
    }

}
