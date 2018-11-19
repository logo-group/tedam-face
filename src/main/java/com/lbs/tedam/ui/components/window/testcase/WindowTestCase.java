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
package com.lbs.tedam.ui.components.window.testcase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TestCase;
import com.lbs.tedam.model.TestSet;
import com.lbs.tedam.ui.TedamFaceEvents;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;

/**
 * @author Canberk.Erkmen<br>
 */
@SpringComponent
@PrototypeScope
public class WindowTestCase extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private WindowTestCaseDataProvider gridDataProviderTestCase;
    private TedamFilterGrid<TestCase> gridTestCase;

    private TestSet testSet;
    private SelectionMode selectionMode;

    @Autowired
    public WindowTestCase(WindowTestCaseDataProvider gridDataProviderProject, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.gridDataProviderTestCase = gridDataProviderProject;
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        TedamGridConfig<TestCase> gridConfigFiles = new TedamGridConfig<TestCase>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.VIEW);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.TEST_CASES_COLUMNS;
            }

            @Override
            public Class<TestCase> getBeanType() {
                return TestCase.class;
            }
        };
        gridDataProviderTestCase.setTestSet(testSet);
        // TODO Is there something like a presenter? Should these things be done here?
        gridTestCase = new TedamFilterGrid<TestCase>(gridConfigFiles, gridDataProviderTestCase, selectionMode);
        gridTestCase.setSizeFull();
		gridTestCase.setId("TestCaseWindow");
        return gridTestCase;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TedamFaceEvents.TestCaseSelectEvent(new ArrayList<>(gridTestCase.getSelectedItems())));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.testcase.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        testSet = (TestSet) parameters.get(UIParameter.TESTSET);
        selectionMode = (SelectionMode) parameters.get(UIParameter.SELECTION_MODE);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridTestCase.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.testcase"), NotifyType.ERROR);
            return false;
        }
        return true;

    }

    @Override
    protected void windowClose() {
    }

	@Override
	public TedamGrid<?> getWindowGrid() {
		return gridTestCase;
	}

}
