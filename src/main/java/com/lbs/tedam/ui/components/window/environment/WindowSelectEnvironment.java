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
package com.lbs.tedam.ui.components.window.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Environment;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.TedamFaceEvents.EnvironmentSelectEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@PrototypeScope
public class WindowSelectEnvironment extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private WindowSelectEnvironmentDataProvider gridDataProviderEnvironment;
    private TedamFilterGrid<Environment> gridEnvironment;

    @Autowired
    public WindowSelectEnvironment(WindowSelectEnvironmentDataProvider gridDataProviderEnvironment,
                                   ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.gridDataProviderEnvironment = gridDataProviderEnvironment;
    }

    @Override
    protected Component buildContent() {
        TedamGridConfig<Environment> gridConfigFiles = new TedamGridConfig<Environment>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.ENVIRONMENT_COLUMNS;
            }

            @Override
            public Class<Environment> getBeanType() {
                return Environment.class;
            }
        };
        gridEnvironment = new TedamFilterGrid<>(gridConfigFiles, gridDataProviderEnvironment, SelectionMode.SINGLE);
		gridEnvironment.setId("EnvironmentSelectWindow");
        return gridEnvironment;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new EnvironmentSelectEvent(gridEnvironment.getClickedItem()));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.environment.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridEnvironment.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.environment"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

    @Override
    protected void okButtonPressed() throws LocalizedException {
        confirmDefineEnvironment();
    }

    private void confirmDefineEnvironment() {
        TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {
                    @Override
                    public void onConfirm() {
                        if (!readyToClose()) {
                            return;
                        }
                        publishCloseSuccessEvent();
                        windowClose();
                        close();
                    }

                    @Override
                    public void onCancel() {
                    }
                }, getLocaleValue("confirm.message.defineEnvironment"), getLocaleValue("general.button.ok"),
                getLocaleValue("general.button.cancel"));
    }

	@Override
	public TedamGrid<?> getWindowGrid() {
		return gridEnvironment;
	}

}
