/*
 * Copyright 2014-2019 Logo Business Solutions
 * (a.k.a. LOGO YAZILIM SAN. VE TIC. A.S)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.lbs.tedam.ui.util;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.Constants;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class TedamFileReceiver implements Receiver, SucceededListener, TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;
    public File file;
    public String path;
    private List<String> allowedExtensionList = Arrays.asList(Constants.FILE_EXTENSION_XML.replace(".", ""), Constants.FILE_EXTENSION_PDF.replace(".", ""),
            Constants.FILE_EXTENSION_FLT.replace(".", ""), Constants.FILE_EXTENSION_IGV.replace(".", ""));

    public TedamFileReceiver(String path) {
        this.path = path;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        String extension = FilenameUtils.getExtension(filename);
        if (!allowedExtensionList.contains(extension)) {
            TedamNotification.showNotification(getLocaleValue("util.tedamfilereceiver.messages.filenotsuitableextension"), NotifyType.ERROR);
            return null;
        }
        FileOutputStream fos = null; // Stream to write to
        try {
            file = new File(path + filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            TedamNotification.showNotification(getLocaleValue("util.tedamfilereceiver.messages.filecouldnotopen"), NotifyType.ERROR);
            return null;
        }
        return fos;
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {
        TedamNotification.showNotification(getLocaleValue("util.tedamfilereceiver.messages.fileuploaded") + file.getName(), NotifyType.SUCCESS);
    }
}
