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
package com.lbs.tedam.ui.components.window.snapshotvalues;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.SnapshotDefinition;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.TedamFaceEvents.SnapshotValuesSelectedEvent;
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
import com.lbs.tedam.ui.view.teststeptype.snapshot.SnapshotValueDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
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
public class WindowSnapshotValues extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private SnapshotValueDataProvider snapshotValueDataProvider;
    private ViewEventBus viewEventBus;
    private TedamFilterGrid<SnapshotValue> gridSnapshotValue;
    private SnapshotDefinition snapshotDefinition;

    private String createdUser;

    private TestStep testStep;

    @Autowired
    public WindowSnapshotValues(SnapshotValueDataProvider snapshotValueDataProvider, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.snapshotValueDataProvider = snapshotValueDataProvider;
        this.viewEventBus = viewEventBus;
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        TedamGridConfig<SnapshotValue> gridConfigFiles = new TedamGridConfig<SnapshotValue>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.VIEW);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.SNAPSHOT_VALUE_COLUMNS;
            }

            @Override
            public Class<SnapshotValue> getBeanType() {
                return SnapshotValue.class;
            }
        };
        // TODO Is there something like a presenter? Should these things be done here?
        snapshotValueDataProvider.setFileForValues(testStep, snapshotDefinition);
        gridSnapshotValue = new TedamFilterGrid<SnapshotValue>(gridConfigFiles, snapshotValueDataProvider, SelectionMode.MULTI) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onViewSelected(SnapshotValue snapshotValue) {
                getEditor().editRow(getRowIndex(snapshotValue));
            }

        };
        gridSnapshotValue.getEditor().setEnabled(true);
        gridSnapshotValue.setSizeFull();
        return gridSnapshotValue;
    }

    @Override
    public void publishCloseSuccessEvent() {
        List<SnapshotValue> snapshotValueList = new ArrayList<>(gridSnapshotValue.getSelectedItems());
        snapshotValueList.forEach(snapshotValue -> snapshotValue.setCreatedUser(createdUser));
        viewEventBus.publish(this, new SnapshotValuesSelectedEvent(snapshotValueList, testStep.getType()));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.snapshotvalue.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        snapshotDefinition = (SnapshotDefinition) parameters.get(UIParameter.SNAPSHOT_DEFINITION);
        testStep = (TestStep) parameters.get(UIParameter.TESTSTEP);
        createdUser = (String) parameters.get(UIParameter.CREATED_USER);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridSnapshotValue.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.snapshotvalues"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }
}
