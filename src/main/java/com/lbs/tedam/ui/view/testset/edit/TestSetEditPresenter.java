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

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.data.service.TestSetService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.*;
import com.lbs.tedam.ui.TedamFaceEvents.TestCaseSelectEvent;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.testset.TestSetGridView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid.SelectionMode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

;

@SpringComponent
@ViewScope
public class TestSetEditPresenter extends AbstractEditPresenter<TestSet, TestSetService, TestSetEditPresenter, TestSetEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final TestCaseDataProvider testCaseDataProvider;
    private final TestCaseTestRunDataProvider testCaseTestRunDataProvider;

    @Autowired
    public TestSetEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, TestSetService testSetService, TedamUserService userService,
                                BeanFactory beanFactory, TestCaseDataProvider testCaseDataProvider, TestCaseTestRunDataProvider testCaseTestRunDataProvider, PropertyService propertyService) {
        super(viewEventBus, navigationManager, testSetService, TestSet.class, beanFactory, userService, propertyService);
        this.testCaseDataProvider = testCaseDataProvider;
        this.testCaseTestRunDataProvider = testCaseTestRunDataProvider;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        TestSet testSet;
        Integer id = (Integer) windowParameters.get(UIParameter.ID);
        ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
        Integer folderId = (Integer) windowParameters.get(UIParameter.FOLDER);
        if ((Integer) windowParameters.get(UIParameter.ID) == 0) {
            testSet = new TestSet();
            testSet.setTestSetFolderId(folderId);
            testSet.setProject(SecurityUtils.getUserSessionProject());
        } else {
            testSet = getService().getById(id);
            if (testSet == null) {
                getView().showNotFound();
                return;
            } else if (mode.equals(ViewMode.EDIT) && getService().isTestSetInProgressStatus(testSet)) {
                getView().showTestSetInProgressStatus();
                return;
            }
            isAuthorized(testSet);
        }
        refreshView(testSet, mode);
        testCaseDataProvider.setTestSet(testSet);
        testCaseTestRunDataProvider.setTestSet(testSet);
        getView().organizeTestCasesGrid(testCaseDataProvider);
        getView().organizeTestCaseTestRunsGrid(testCaseTestRunDataProvider);
        if (getItem().getName() != null) {
            getView().setTitle(getView().getTitle() + ": " + getItem().getName());
        }
        organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
        setGridEditorAttributes(getView().getGridTestCases(), mode != ViewMode.VIEW);
    }

    @Override
    protected void displayTestRunsOperationsColumn(TedamGrid<?> tedamGrid) {
        if (tedamGrid.getBeanType().equals(TestCaseTestRun.class)) {
            tedamGrid.getRUDMenuColumn().setHidden(false);
        }
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    public void prepareSelectTestCaseWindow() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.TESTSET, getItem());
        windowParameters.put(UIParameter.SELECTION_MODE, SelectionMode.MULTI);
        getView().openSelectTestCaseWindow(windowParameters);
    }

    public void prepareTestStepTestRunWindow(TestCaseTestRun testCaseTestRun) throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.TESTCASE_TESTRUN, testCaseTestRun);
        getView().openTestStepTestRunWindow(windowParameters);
    }

    @EventBusListenerMethod
    public void TestCaseSelectedEvent(TestCaseSelectEvent event) {
        for (TestCase testCase : event.getTestCaseList()) {
            TestSetTestCase testSetTestCase = new TestSetTestCase(testCase);
            testCaseDataProvider.getListDataProvider().getItems().add(testSetTestCase);
        }
        testCaseDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void removeRow(Set<TestSetTestCase> testSetTestCaseSet) {
        for (TestSetTestCase testSetTestCase : testSetTestCaseSet) {
            testCaseDataProvider.removeItem(testSetTestCase);
            testCaseDataProvider.getListDataProvider().refreshAll();
        }
        setHasChanges(true);
    }

    @Override
    protected Class<? extends View> getGridView() {
        return TestSetGridView.class;
    }

    @Override
    protected TestSet save(TestSet testSet) throws LocalizedException {
        if (testSet.getTestSetFolderId() == null) {
            getView().showFolderNotSelectedToTestSetMessage();
            return null;
        }
        setTestCasePosition(testSet);
        return super.save(testSet);
    }

    @Override
    public void okPressed() throws LocalizedException {
        saveFilters(getView().getGridTestCases());
        saveFilters(getView().getGridTestCaseRun());
        super.okPressed();
    }

    private void saveFilters(TedamFilterGrid<?> grid) {
        if (getView().getViewMode() == ViewMode.VIEW) {
            SecurityUtils.saveFilterValue(getView().getClass().getName(), grid.saveFilterValues());
        }
    }

    private void setTestCasePosition(TestSet testSet) {
        for (ListIterator<TestSetTestCase> listIterator = testSet.getTestSetTestCases().listIterator(); listIterator.hasNext(); ) {
            TestSetTestCase testCase = listIterator.next();
            testCase.setPosition(listIterator.nextIndex());
        }
    }

    @Override
    protected Project getProjectByEntity(TestSet entity) {
        return entity.getProject();
    }
}
