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

package com.lbs.tedam.ui.view.testset.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.TestSetService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TestCaseTestRun;
import com.lbs.tedam.model.TestSet;
import com.lbs.tedam.model.TestSetTestCase;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamDateTimeField;
import com.lbs.tedam.ui.components.basic.TedamPopUpView;
import com.lbs.tedam.ui.components.basic.TedamTextArea;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamTestSetStatusComboBox;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.GridFilterValue;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.layout.TedamVerticalLayout;
import com.lbs.tedam.ui.components.window.testcase.WindowTestCase;
import com.lbs.tedam.ui.components.window.testrun.WindowTestStepTestRun;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.lbs.tedam.ui.view.testcase.edit.TestCaseEditView;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;

@SpringView
public class TestSetEditView extends AbstractEditView<TestSet, TestSetService, TestSetEditPresenter, TestSetEditView> {

    private static final long serialVersionUID = 1L;

    private final TedamTestSetStatusComboBox testSetStatus;
    private final WindowTestCase windowTestCase;
    private final WindowTestStepTestRun windowTestStepTestRun;
    private TedamTextArea description;
    private TedamTextField name;
    private TedamTextField actualDuration;
    private TedamDateTimeField executionDateTime;
    private TedamFilterGrid<TestCaseTestRun> gridTestCaseRun;
    private TedamFilterGrid<TestSetTestCase> gridTestCases;
    private TedamButton addButton;
    private TedamButton removeButton;

    @Autowired
    public TestSetEditView(TestSetEditPresenter presenter, TedamTestSetStatusComboBox testSetStatus, TestCaseTestRunDataProvider testCaseTestRunDataProvider,
                           WindowTestCase windowTestCase, WindowTestStepTestRun windowTestStepTestRun) {
        super(presenter);
        this.testSetStatus = testSetStatus;
        this.windowTestCase = windowTestCase;
        this.windowTestStepTestRun = windowTestStepTestRun;
    }

    @PostConstruct
    private void initView() {
        description = new TedamTextArea("view.testsetedit.textfield.description", "full", true, true);
        name = new TedamTextField("view.testsetedit.textfield.name", "full", true, true);
        actualDuration = new TedamTextField("view.testsetedit.checkbox.actualduration", "half", false, false);
        executionDateTime = new TedamDateTimeField("view.testsetedit.checkbox.dateexecution", "half", false, false);

        buildGrid();

        buildTestCaseTestRunGrid();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name, testSetStatus, actualDuration, executionDateTime, description);
        addSection(getLocaleValue("view.testsetedit.section.testcases"), 1, null, buildTestCasesGridButtons(), gridTestCases);

        addSection(getLocaleValue("section.testrunproperties"), 2, null, gridTestCaseRun);

        getPresenter().setView(this);
    }

    private Component buildTestCasesGridButtons() {
        HorizontalLayout hLayButtons = new HorizontalLayout();

        addButton = new TedamButton("view.testsetedit.button.addtestcase", VaadinIcons.PLUS_CIRCLE);
        removeButton = new TedamButton("view.testsetedit.button.removetestcase", VaadinIcons.MINUS_CIRCLE);

        removeButton.addClickListener(new ClickListener() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (gridTestCases.getSelectedItems().isEmpty()) {
                    TedamNotification.showNotification(getLocaleValue("view.testsetedit.messages.testCaseNotSelected"), NotifyType.ERROR);
                    return;
                }
                getPresenter().removeRow(gridTestCases.getSelectedItems());
            }
        });
        addButton.addClickListener(e -> {
            try {
                getPresenter().prepareSelectTestCaseWindow();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });

        hLayButtons.addComponents(addButton, removeButton);
        return hLayButtons;
    }

    protected void organizeTestCasesGrid(AbstractDataProvider<TestSetTestCase> dataProvider) {
        gridTestCases.setGridDataProvider(dataProvider);
        gridTestCases.initFilters();
        fetchSavedFilters(gridTestCases);
    }

    protected void organizeTestCaseTestRunsGrid(AbstractDataProvider<TestCaseTestRun> dataProvider) {
        gridTestCaseRun.setGridDataProvider(dataProvider);
        gridTestCaseRun.initFilters();
        gridTestCaseRun.sort("startDate", SortDirection.DESCENDING);
        fetchSavedFilters(gridTestCaseRun);
    }

    protected void fetchSavedFilters(TedamFilterGrid<?> grid) {
        GridFilterValue filterValues = SecurityUtils.loadFilterValue(this.getClass().getName(), grid.getId());
        grid.laodFilterValues(filterValues);
    }

    protected void buildGrid() {
        TedamGridConfig<TestSetTestCase> testCasesGridConfig = new TedamGridConfig<TestSetTestCase>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.TESTSET_TESTCASES_COLUMNS;
            }

            @Override
            public Class<TestSetTestCase> getBeanType() {
                return TestSetTestCase.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.VIEW);
                return operations;
            }

        };
        gridTestCases = new TedamFilterGrid<TestSetTestCase>(testCasesGridConfig, SelectionMode.MULTI) {
            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void onViewSelected(TestSetTestCase testSetTestCase) {
                getPresenter().getNavigationManager().navigateTo(TestCaseEditView.class, testSetTestCase.getTestCase().getId());
            }
        };

        gridTestCases.addItemClickListener(new ItemClickListener<TestSetTestCase>() {
            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClick<TestSetTestCase> event) {
                if (event.getMouseEventDetails().isDoubleClick()) {
                    getPresenter().getNavigationManager().navigateTo(TestCaseEditView.class, event.getItem().getTestCase().getId());
                }
            }
        });

        gridTestCases.setId("TestCasesGrid");
    }

    protected void buildTestCaseTestRunGrid() {
        TedamGridConfig<TestCaseTestRun> testCaseTestRunsGridConfig = new TedamGridConfig<TestCaseTestRun>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.TEST_STEP_TEST_CASE_TEST_RUN_COLUMNS;
            }

            @Override
            public Class<TestCaseTestRun> getBeanType() {
                return TestCaseTestRun.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.VIEW);
                return operations;
            }

        };
        gridTestCaseRun = new TedamFilterGrid<TestCaseTestRun>(testCaseTestRunsGridConfig, SelectionMode.NONE) {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void onViewSelected(TestCaseTestRun testCaseTestRun) {
                try {
                    getPresenter().prepareTestStepTestRunWindow(testCaseTestRun);
                } catch (LocalizedException e) {
                    logError(e);
                }
            }

            @Override
            public List<Component> buildCustomComponentForItem(TestCaseTestRun item) {
                return buildExecutionResultComponents(item);
            }

        };
        gridTestCaseRun.setId("TestCaseTestRunGrid");
    }

    public TedamFilterGrid<TestSetTestCase> getGridTestCases() {
        return gridTestCases;
    }

    public TedamFilterGrid<TestCaseTestRun> getGridTestCaseRun() {
        return gridTestCaseRun;
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.testsetedit.header");
    }

    public void showFolderNotSelectedToTestSetMessage() {
        TedamNotification.showNotification(getLocaleValue("view.testsetedit.messages.showFolderNotSelectedToTestSetMessage"), NotifyType.ERROR);
    }

    public void showTestSetInProgressStatus() {
        TedamNotification.showNotification(getLocaleValue("view.testsetedit.messages.showTestSetInProgressStatus"), NotifyType.ERROR);
    }

    public void openSelectTestCaseWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowTestCase.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowTestCase.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    public void openTestStepTestRunWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowTestStepTestRun.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowTestStepTestRun.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    @Override
    public void bindFormFields(BeanValidationBinder<TestSet> binder) {
        binder.forField(actualDuration).withNullRepresentation("").withConverter(new StringToIntegerConverter(Integer.valueOf(0), "")).bind(TestSet::getActualDuration,
                TestSet::setActualDuration);
        super.bindFormFields(binder);
    }

    private TedamVerticalLayout buildPopUpContent(TestCaseTestRun item) {
        TedamVerticalLayout popupContent = new TedamVerticalLayout();
        popupContent.setWidth("400px");
        TedamTextArea textArea = new TedamTextArea("view.testcaseedit.textarea.executionMessage", "full", true, false);
        textArea.setValue(item.getExecutionMessage());
        textArea.setRows(5);
        popupContent.addComponent(textArea);
        return popupContent;
    }

    private TedamButton buildShowExecutionResultButton(Integer idValue) {
        TedamButton showExecutionResultButton = new TedamButton("view.testcaseedit.button.showExecutionResult", VaadinIcons.INFO);
        showExecutionResultButton.setId(showExecutionResultButton.getId() + "." + idValue);
        showExecutionResultButton.setSizeUndefined();
        showExecutionResultButton.setCaption("");
        return showExecutionResultButton;
    }

    private List<Component> buildExecutionResultComponents(TestCaseTestRun item) {
        Integer idValue = gridTestCaseRun.getRowIndex(item) + 1;
        TedamVerticalLayout popupContent = buildPopUpContent(item);
        TedamPopUpView executionResultPopUp = new TedamPopUpView(null, popupContent);
        TedamButton showExecutionResultButton = buildShowExecutionResultButton(idValue);
        showExecutionResultButton.addClickListener(event -> executionResultPopUp.setPopupVisible(true));
        return Arrays.asList(executionResultPopUp, showExecutionResultButton);
    }

	@Override
	protected void collectGrids() {
		super.collectGrids();
		getGridList().add(gridTestCaseRun);
		getGridList().add(gridTestCases);
	}

}