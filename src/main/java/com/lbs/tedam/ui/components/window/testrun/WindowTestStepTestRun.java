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
package com.lbs.tedam.ui.components.window.testrun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TestCaseTestRun;
import com.lbs.tedam.model.TestStepTestRun;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamPopUpView;
import com.lbs.tedam.ui.components.basic.TedamTextArea;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.layout.TedamVerticalLayout;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@PrototypeScope
public class WindowTestStepTestRun extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private WindowTestStepTestRunDataProvider gridDataProviderTestStepTestRun;
    private TedamFilterGrid<TestStepTestRun> gridTestStepTestRun;
    private TestCaseTestRun testCaseTestRun;

    @Autowired
    public WindowTestStepTestRun(WindowTestStepTestRunDataProvider gridDataProviderTestStepTestRun, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.gridDataProviderTestStepTestRun = gridDataProviderTestStepTestRun;
    }

    @Override
    protected Component buildContent() {
        TedamGridConfig<TestStepTestRun> gridConfigFiles = new TedamGridConfig<TestStepTestRun>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.CUSTOM);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.TEST_STEP_TEST_RUN_COLUMNS;
            }

            @Override
            public Class<TestStepTestRun> getBeanType() {
                return TestStepTestRun.class;
            }
        };
        // TODO Is there something like a presenter? Should these things be done here?
        gridDataProviderTestStepTestRun.setTestCaseTestRun(testCaseTestRun);
        gridTestStepTestRun = new TedamFilterGrid<TestStepTestRun>(gridConfigFiles, gridDataProviderTestStepTestRun, SelectionMode.NONE) {
            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public List<Component> buildCustomComponentForItem(TestStepTestRun item) {
                return buildExecutionResultComponents(item);
            }
        };
        return gridTestStepTestRun;
    }

    private List<Component> buildExecutionResultComponents(TestStepTestRun item) {
        Integer idValue = gridTestStepTestRun.getRowIndex(item) + 1;
        TedamVerticalLayout popupContent = buildPopUpContent(item);
        TedamPopUpView executionResultPopUp = new TedamPopUpView(null, popupContent);
        TedamButton showExecutionResultButton = buildShowExecutionResultButton(idValue);
        showExecutionResultButton.addClickListener(event -> executionResultPopUp.setPopupVisible(true));
        return Arrays.asList(executionResultPopUp, showExecutionResultButton);
    }

    private TedamButton buildShowExecutionResultButton(Integer idValue) {
        TedamButton showExecutionResultButton = new TedamButton("window.teststeptestrun.button.showExecutionResult", VaadinIcons.INFO);
        showExecutionResultButton.setId(showExecutionResultButton.getId() + "." + idValue);
        showExecutionResultButton.setSizeUndefined();
        showExecutionResultButton.setCaption("");
        return showExecutionResultButton;
    }

    private TedamVerticalLayout buildPopUpContent(TestStepTestRun item) {
        TedamVerticalLayout popupContent = new TedamVerticalLayout();
        popupContent.setWidth("400px");
        TedamTextArea textArea = new TedamTextArea("window.teststeptestrun.textarea.executionMessage", "full", true, false);
        textArea.setValue(item.getExecutionMessage());
        textArea.setRows(5);
        popupContent.addComponent(textArea);
        return popupContent;
    }

    @Override
    public void publishCloseSuccessEvent() {
    }

    @Override
    protected String getHeader() {
		return "";
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        testCaseTestRun = (TestCaseTestRun) parameters.get(UIParameter.TESTCASE_TESTRUN);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
		setCaption(getLocaleValue("window.teststeptestrun.header") + " - " + testCaseTestRun.getTestCaseName());
    }

    @Override
    protected boolean readyToClose() {
        return true;
    }

    @Override
    protected void windowClose() {
    }

}
