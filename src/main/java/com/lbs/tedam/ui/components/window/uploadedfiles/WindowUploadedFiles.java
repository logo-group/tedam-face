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
package com.lbs.tedam.ui.components.window.uploadedfiles;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFile;
import com.lbs.tedam.ui.TedamFaceEvents.FileAttachEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.Constants;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.ItemClickListener;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@PrototypeScope
public class WindowUploadedFiles extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private UploadedFilesDataProvider gridDataProviderfileNames;
    private TedamFilterGrid<TedamFile> gridUploadedFiles;
    private Integer testCaseId;

    @Autowired
    public WindowUploadedFiles(UploadedFilesDataProvider gridDataProviderfileNames, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.gridDataProviderfileNames = gridDataProviderfileNames;
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        TedamGridConfig<TedamFile> gridConfigFiles = new TedamGridConfig<TedamFile>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.UPLOADED_FILES_COLUMNS;
            }

            @Override
            public Class<TedamFile> getBeanType() {
                return TedamFile.class;
            }
        };
        // TODO Is there something like a presenter? Should these things be done here?
        String testCaseFolder = getPropertyService().getTestcaseFolder(testCaseId);
        gridDataProviderfileNames.setFilePath(testCaseFolder);
        gridUploadedFiles = new TedamFilterGrid<TedamFile>(gridConfigFiles, gridDataProviderfileNames, SelectionMode.SINGLE);

        gridUploadedFiles.addItemClickListener(new ItemClickListener<TedamFile>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClick<TedamFile> event) {
                if (event.getMouseEventDetails().isDoubleClick()) {
                    gridUploadedFiles.select(event.getItem());
                    publishCloseSuccessEvent();
                    close();
                }
            }

        });

        gridUploadedFiles.setSizeFull();
        return gridUploadedFiles;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new FileAttachEvent((TedamFile) gridUploadedFiles.getSelectedItems().toArray()[0]));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.uploadedfiles.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        testCaseId = (Integer) parameters.get(UIParameter.ID);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridUploadedFiles.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.uploadedfiles"), NotifyType.ERROR);
            return false;
        }
        for (TedamFile selectedFile : gridUploadedFiles.getSelectedItems()) {
            String extension = FilenameUtils.getExtension(selectedFile.getName());
            if (!Constants.FILE_EXTENSION_PDF.contains(extension) && !Constants.FILE_EXTENSION_XML.contains(extension)) {
                TedamNotification.showNotification(getLocaleValue("window.readytoclose.notallowedextensionlist"), NotifyType.ERROR);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

}
