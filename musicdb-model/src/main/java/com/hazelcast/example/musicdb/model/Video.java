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

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import java.io.IOException;

public class Video extends SerializableModel {

    private long duration;
    private boolean embed;
    private String uri;
    private String title;
    private String description;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isEmbed() {
        return embed;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(duration);
        out.writeBoolean(embed);
        out.writeUTF(uri);
        out.writeUTF(title);
        out.writeUTF(description);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        duration = in.readLong();
        embed = in.readBoolean();
        uri = in.readUTF();
        title = in.readUTF();
        description = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Video video = (Video) o;

        if (uri != null ? !uri.equals(video.uri) : video.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }
}
