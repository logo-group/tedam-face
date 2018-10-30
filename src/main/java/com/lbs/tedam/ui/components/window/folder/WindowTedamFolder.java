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
package com.lbs.tedam.ui.components.window.folder;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.ui.TedamFaceEvents.TedamFolderEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.tedamfolder.edit.TedamFolderEditPresenter;
import com.lbs.tedam.ui.view.tedamfolder.edit.TedamFolderEditView;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@ViewScope
public class WindowTedamFolder extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private TedamFolderEditView tedamFolderEditView;
    private TedamFolderEditPresenter tedamFolderEditPresenter;

    @Autowired
    public WindowTedamFolder(ViewEventBus viewEventBus, TedamFolderEditView tedamFolderEditView, TedamFolderEditPresenter tedamFolderEditPresenter,
                             PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.tedamFolderEditView = tedamFolderEditView;
        this.tedamFolderEditPresenter = tedamFolderEditPresenter;
    }

    @Override
    protected Component buildContent() {
        return tedamFolderEditView;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TedamFolderEvent(tedamFolderEditPresenter.getTedamFolder()));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTedamFolder.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        tedamFolderEditPresenter.enterView(parameters);
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (tedamFolderEditPresenter.getTedamFolder().getParentFolder() == null) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.tedamfolder"), NotifyType.ERROR);
            return false;
        }
        TedamFolder folder = tedamFolderEditPresenter.getTedamFolder();
        if (folder != null && folder.getParentFolder() != null
                && folder.getId().equals(folder.getParentFolder().getId())) {
            TedamNotification.showNotification(getLocaleValue("window.WindowTedamFolder.parent"), NotifyType.ERROR);
            return false;
        } else {
            boolean hasConflict = false;
            ArrayList<Integer> controlList = new ArrayList<Integer>();
            TedamFolder tempParentFolder = folder.getParentFolder();
            controlList.add(folder.getId());
            while (tempParentFolder != null) {
                Integer tempParentFolderId = tempParentFolder.getId();
                if (controlList.contains(tempParentFolderId)) {
                    TedamNotification.showNotification(getLocaleValue("window.WindowTedamFolder.root"),
                            NotifyType.ERROR);
                    hasConflict = true;
                    break;
                } else {
                    controlList.add(tempParentFolderId);
                    tempParentFolder = tempParentFolder.getParentFolder();
                }
            }
            if (hasConflict) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void windowClose() {
        tedamFolderEditPresenter.destroy();
    }

}
