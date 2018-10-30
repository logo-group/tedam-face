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
package com.lbs.tedam.ui.components.window.teststep;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepSelectedEvent;
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
import com.lbs.tedam.util.EnumsV2.TestStepType;
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
public class WindowSelectTestStep extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private WindowSelectTestStepDataProvider gridDataProviderTestSteps;
    private TedamFilterGrid<TestStep> gridTestSteps;
    private TestStepType testStepType;

    @Autowired
    public WindowSelectTestStep(WindowSelectTestStepDataProvider gridDataProviderfileNames, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.gridDataProviderTestSteps = gridDataProviderfileNames;
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        TedamGridConfig<TestStep> gridConfigFiles = new TedamGridConfig<TestStep>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.TEST_STEPS_COLUMNS;
            }

            @Override
            public Class<TestStep> getBeanType() {
                return TestStep.class;
            }
        };
        // TODO Is there something like a presenter? Should these things be done here?
        gridDataProviderTestSteps.setTestStepType(testStepType);
        gridTestSteps = new TedamFilterGrid<TestStep>(gridConfigFiles, gridDataProviderTestSteps, SelectionMode.SINGLE);
        gridTestSteps.setSizeFull();
        return gridTestSteps;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepSelectedEvent((TestStep) gridTestSteps.getSelectedItems().toArray()[0]));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.selectTestStep.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        testStepType = (TestStepType) parameters.get(UIParameter.TEST_STEP_TYPE);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridTestSteps.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.teststeps"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

}
