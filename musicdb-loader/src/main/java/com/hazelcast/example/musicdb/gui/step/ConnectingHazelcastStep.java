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

package com.hazelcast.example.musicdb.gui.step;

import com.googlecode.lanterna.gui.Window;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.example.musicdb.gui.Step;
import com.hazelcast.example.musicdb.gui.StepOperation;
import com.hazelcast.example.musicdb.gui.window.WaitingWindow;

public class ConnectingHazelcastStep extends Step {

    @Override
    public Window createWindow() {
        return new WaitingWindow("Connecting to cluster", "Connecting to remote cluster, please wait...");
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                String address = (String) getContext().get("address");

                ClientConfig cc = new ClientConfig();
                cc.addAddress(address);

                HazelcastInstance hazelcastInstance = HazelcastClient.newHazelcastClient(cc);
                getContext().put("hazelcast", hazelcastInstance);
                transition();
            }
        };
    }
}
