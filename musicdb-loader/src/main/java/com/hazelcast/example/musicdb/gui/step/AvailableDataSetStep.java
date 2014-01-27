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
import com.hazelcast.example.musicdb.AutoLoader;
import com.hazelcast.example.musicdb.gui.Step;
import com.hazelcast.example.musicdb.gui.StepOperation;
import com.hazelcast.example.musicdb.gui.window.WaitingWindow;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AvailableDataSetStep extends Step {

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1700.77 Safari/537.36";

    private static final Pattern CHECKSUM_PATTERN = Pattern.compile(">discogs_([0-9]*)_CHECKSUM\\.txt<\\/a><\\/td>");
    private static final String ARTISTS_PATTERN = ">discogs_%DATE%_artists\\.xml\\.gz<\\/a><\\/td>";
    private static final String LABELS_PATTERN = ">discogs_%DATE%_labels\\.xml\\.gz<\\/a><\\/td>";
    private static final String MASTERS_PATTERN = ">discogs_%DATE%_masters\\.xml\\.gz<\\/a><\\/td>";
    private static final String RELEASES_PATTERN = ">discogs_%DATE%_releases\\.xml\\.gz<\\/a><\\/td>";

    @Override
    public Window createWindow() {
        return new WaitingWindow("Retrieving available datasets", "Downloading...");
    }

    @Override
    protected StepOperation createOperation() {
        return new StepOperation() {
            @Override
            public void execute() throws Exception {
                HttpClient client = new DefaultHttpClient();
                HttpGet operation = new HttpGet(AutoLoader.BASE_URL);
                operation.setHeader(HTTP.USER_AGENT, USER_AGENT);
                operation.setHeader("Accept", "text/html,application/xhtml+xml");
                HttpResponse response = client.execute(operation);
                String html = EntityUtils.toString(response.getEntity());

                final List<String> dates = new ArrayList<>();
                Matcher matcher = CHECKSUM_PATTERN.matcher(html);
                while (matcher.find()) {
                    String date = matcher.group(1);
                    if (checkOtherFiles(date, html)) {
                        dates.add(date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6));
                    }
                }

                getContext().put("dates", dates);
                transition();
            }
        };
    }

    private boolean checkOtherFiles(String date, String html) {
        Pattern pattern = Pattern.compile(ARTISTS_PATTERN.replace("%DATE%", date));
        Matcher matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return false;
        }

        pattern = Pattern.compile(LABELS_PATTERN.replace("%DATE%", date));
        matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return false;
        }

        pattern = Pattern.compile(MASTERS_PATTERN.replace("%DATE%", date));
        matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return false;
        }

        pattern = Pattern.compile(RELEASES_PATTERN.replace("%DATE%", date));
        matcher = pattern.matcher(html);
        if (!matcher.find()) {
            return false;
        }

        return true;
    }

}
