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

/**
 *
 */
package com.lbs.tedam.ui.util;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.TedamFile;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.TedamFaceEvents.FileUploadEvent;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.TedamFileUtils;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadFinishedHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
public class TedamUploadFinishedHandler implements UploadFinishedHandler, TedamLocalizerWrapper {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public File file;
    public String path;
    public ViewEventBus eventBus;
    private List<String> allowedExtensionList = Arrays.asList(Constants.FILE_EXTENSION_XML.replace(".", ""), Constants.FILE_EXTENSION_PDF.replace(".", ""),
            Constants.FILE_EXTENSION_FLT.replace(".", ""), Constants.FILE_EXTENSION_IGV.replace(".", ""));
    private Map<String, ByteArrayInputStream> bais;

    public TedamUploadFinishedHandler(ViewEventBus viewEventBus) {
        this.eventBus = viewEventBus;
    }

    public void setBais(Map<String, ByteArrayInputStream> bais) {
        this.bais = bais;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void handleFile(InputStream in, String fileName, String mimeType, long length, int filesLeftInQueue) {
        if (bais == null) {
            bais = new HashMap<String, ByteArrayInputStream>();
        }

        String extension = FilenameUtils.getExtension(fileName);
        if (!allowedExtensionList.contains(extension)) {
            TedamNotification.showNotification(getLocaleValue("util.tedamfilereceiver.messages.filenotsuitableextension"), NotifyType.ERROR);
            return;
        }
        if (TedamFileUtils.isFileExist(path + fileName)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                IOUtils.copy(in, baos);
                byte[] bytes = baos.toByteArray();
                bais.put(fileName, new ByteArrayInputStream(bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }

            confirmOverride(fileName);
        } else {
            createFile(in, fileName);
        }
    }

    private void createFile(String fileName) {
        FileOutputStream fos = null; // Stream to write to
        byte[] buffer;
        try {
            File file = new File(path + fileName);
            fos = new FileOutputStream(file);
            IOUtils.copy(bais.get(fileName), fos);
            buffer = new byte[bais.get(fileName).available()];
            bais.get(fileName).read(buffer);
            fos.write(buffer);
            fos.close();
            eventBus.publish(this, new FileUploadEvent(new TedamFile(fileName)));
        } catch (FileNotFoundException e) {
            TedamNotification.showNotification(getLocaleValue("util.tedamfilereceiver.messages.filecouldnotopen"), NotifyType.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(InputStream in, String fileName) {
        FileOutputStream fos = null; // Stream to write to
        byte[] buffer;
        try {
            File file = new File(path + fileName);
            fos = new FileOutputStream(file);
            buffer = new byte[in.available()];
            in.read(buffer);
            fos.write(buffer);
            fos.close();
            eventBus.publish(this, new FileUploadEvent(new TedamFile(fileName)));
        } catch (FileNotFoundException e) {
            TedamNotification.showNotification(getLocaleValue("util.tedamfilereceiver.messages.filecouldnotopen"), NotifyType.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void confirmOverride(String fileName) {
        TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

            @Override
            public void onConfirm() {
                createFile(fileName);
            }

            @Override
            public void onCancel() {
                bais.remove(fileName);
            }
        }, getLocaleValue("util.tedamfilereceiver.messages.fileduplicate") + " <" + fileName + ">", getLocaleValue("general.button.ok"), getLocaleValue("general.button.cancel"));
    }
}
