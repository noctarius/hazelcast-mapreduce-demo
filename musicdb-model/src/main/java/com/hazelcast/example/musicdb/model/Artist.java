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
import java.util.ArrayList;
import java.util.List;

public class Artist extends SerializableModel {

    private final List<Image> images = new ArrayList<>();
    private long artistId;
    private String name;
    private String realname;
    private String dataQuality;
    private final List<String> nameVariations = new ArrayList<>();
    private final List<String> aliases = new ArrayList<>();
    private final List<String> members = new ArrayList<>();
    private String profile;
    private final List<String> urls = new ArrayList<>();
    private final List<String> groups = new ArrayList<>();

    public List<Image> getImages() {
        return images;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getDataQuality() {
        return dataQuality;
    }

    public void setDataQuality(String dataQuality) {
        this.dataQuality = dataQuality;
    }

    public List<String> getNameVariations() {
        return nameVariations;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getGroups() {
        return groups;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(images.size());
        for (Image image : images) {
            out.writeObject(image);
        }
        out.writeLong(artistId);
        out.writeUTF(name);
        out.writeUTF(realname);
        out.writeUTF(dataQuality);
        out.writeInt(nameVariations.size());
        for (String nameVariation : nameVariations) {
            out.writeUTF(nameVariation);
        }
        out.writeInt(aliases.size());
        for (String alias : aliases) {
            out.writeUTF(alias);
        }
        out.writeInt(members.size());
        for (String member : members) {
            out.writeUTF(member);
        }
        out.writeUTF(profile);
        out.writeInt(urls.size());
        for (String url : urls) {
            out.writeUTF(url);
        }
        out.writeInt(groups.size());
        for (String group : groups) {
            out.writeUTF(group);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            images.add((Image) in.readObject());
        }
        artistId = in.readLong();
        name = in.readUTF();
        realname = in.readUTF();
        dataQuality = in.readUTF();
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            nameVariations.add(in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            aliases.add(in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            members.add(in.readUTF());
        }
        profile = in.readUTF();
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            urls.add(in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            groups.add(in.readUTF());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;

        if (artistId != artist.artistId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (artistId ^ (artistId >>> 32));
    }
}
