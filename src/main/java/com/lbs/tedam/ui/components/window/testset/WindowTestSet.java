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
package com.lbs.tedam.ui.components.window.testset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TestSet;
import com.lbs.tedam.ui.TedamFaceEvents.TestSetEvent;
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
 * @author Ahmet.Izgi
 */
@SpringComponent
@PrototypeScope
public class WindowTestSet extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private WindowTestSetDataProvider gridDataProviderTestSet;
    private TedamFilterGrid<TestSet> gridTestSets;
    private List<TestSet> testSetList;

    @Autowired
    public WindowTestSet(WindowTestSetDataProvider gridDataProviderTestSet, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.gridDataProviderTestSet = gridDataProviderTestSet;
    }

    @Override
    protected Component buildContent() {
        TedamGridConfig<TestSet> gridConfigFiles = new TedamGridConfig<TestSet>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.BASIC_TEST_SETS_COLUMNS;
            }

            @Override
            public Class<TestSet> getBeanType() {
                return TestSet.class;
            }
        };
        gridTestSets = new TedamFilterGrid<>(gridConfigFiles, gridDataProviderTestSet, SelectionMode.MULTI);
		gridTestSets.setId("TestSetWindow");
        removeSelectedTestSets();
        return gridTestSets;
    }

    private void removeSelectedTestSets() {
        for (TestSet testSet : testSetList) {
            gridDataProviderTestSet.getListDataProvider().getItems().remove(testSet);
        }
        gridDataProviderTestSet.getListDataProvider().refreshAll();
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestSetEvent(new ArrayList<>(gridTestSets.getSelectedItems())));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.testset.header");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        testSetList = (List<TestSet>) parameters.get(UIParameter.SELECTED_LIST);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridTestSets.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.testset"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

	@Override
	public TedamGrid<?> getWindowGrid() {
		return gridTestSets;
	}

}
