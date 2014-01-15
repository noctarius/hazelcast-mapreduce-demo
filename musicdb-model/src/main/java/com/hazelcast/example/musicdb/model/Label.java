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

public class Label extends SerializableModel {

    private final List<Image> images = new ArrayList<>();
    private long labelId;
    private String name;
    private String contactInfo;
    private String profile;
    private String dataQuality;
    private final List<String> urls = new ArrayList<>();
    private final List<String> subLabels = new ArrayList<>();
    private String parentLabel;

    public List<Image> getImages() {
        return images;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDataQuality() {
        return dataQuality;
    }

    public void setDataQuality(String dataQuality) {
        this.dataQuality = dataQuality;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getSubLabels() {
        return subLabels;
    }

    public String getParentLabel() {
        return parentLabel;
    }

    public void setParentLabel(String parentLabel) {
        this.parentLabel = parentLabel;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(images.size());
        for (Image image : images) {
            out.writeObject(image);
        }
        out.writeLong(labelId);
        out.writeUTF(name);
        out.writeUTF(contactInfo);
        out.writeUTF(profile);
        out.writeUTF(dataQuality);
        out.writeInt(urls.size());
        for (String url : urls) {
            out.writeUTF(url);
        }
        out.writeInt(subLabels.size());
        for (String subLabel : subLabels) {
            out.writeUTF(subLabel);
        }
        out.writeUTF(parentLabel);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            images.add((Image) in.readObject());
        }
        labelId = in.readLong();
        name = in.readUTF();
        contactInfo = in.readUTF();
        profile = in.readUTF();
        dataQuality = in.readUTF();
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            urls.add(in.readUTF());
        }
        size = in.readInt();
        for (int i = 0; i < size; i++) {
            subLabels.add(in.readUTF());
        }
        parentLabel = in.readUTF();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (labelId != label.labelId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (labelId ^ (labelId >>> 32));
    }
}
