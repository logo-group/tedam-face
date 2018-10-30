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
import com.lbs.tedam.ui.TedamFaceEvents.TedamFolderSelectEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamTree;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.EnumsV2.TedamFolderType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@ViewScope
public class WindowSelectTedamFolder extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private TedamFolderDataProvider tedamFolderDataProvider;
    private TedamTree<TedamFolder> tedamTree;
    private Panel treePanel;
    private TedamFolderType folderType;

    @Autowired
    public WindowSelectTedamFolder(TedamFolderDataProvider tedamFolderDataProvider, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.tedamFolderDataProvider = tedamFolderDataProvider;
    }

    protected void organizeTedamFolderTestTreePanel() throws LocalizedException {
        tedamFolderDataProvider.buildDataProvider(folderType);
        tedamTree.setDataProvider(tedamFolderDataProvider.getTreeDataProvider());
        tedamTree.expand(tedamFolderDataProvider.getTreeDataProvider().getTreeData().getRootItems());
    }

    private void initTedamTree() {
        tedamTree = new TedamTree<>();
    }

    @Override
    protected Component buildContent() {
        initTedamTree();
        treePanel = new Panel();
        treePanel.setSizeFull();
        treePanel.setContent(tedamTree);
        return treePanel;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TedamFolderSelectEvent(tedamTree.getSelectedItems().iterator().next()));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.selecttedamfolder.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        folderType = (TedamFolderType) parameters.get(UIParameter.FOLDER_TYPE);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
        organizeTedamFolderTestTreePanel();
    }

    @Override
    protected boolean readyToClose() {
        if (tedamTree.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.selecttedamfolder"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {

    }

}
