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

package com.lbs.tedam.ui.view.testcase;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.*;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.model.*;
import com.lbs.tedam.ui.TedamFaceEvents.TedamFolderEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TedamFolderSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TestSetSelectEvent;
import com.lbs.tedam.ui.components.window.folder.TedamFolderDataProvider;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractGridPresenter;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.EnumsV2.TedamFolderType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
@ViewScope
public class TestCaseGridPresenter extends AbstractGridPresenter<TestCase, TestCaseService, TestCaseGridPresenter, TestCaseGridView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final TestStepService testStepService;
    private final TestSetService testSetService;
    private final SnapshotDefinitionService snapshotDefinitionService;
    private TedamFolderDataProvider tedamFolderDataProvider;
    private TedamFolderService tedamFolderService;

    @Autowired
    public TestCaseGridPresenter(TestCasesDataProvider testCasesDataProvider, NavigationManager navigationManager, TestCaseService service, BeanFactory beanFactory,
                                 ViewEventBus viewEventBus, TedamUserService userService, TestStepService testStepService, SnapshotDefinitionService snapshotDefinitionService,
                                 PropertyService propertyService, TedamFolderDataProvider tedamFolderDataProvider, TedamFolderService tedamFolderService, TestSetService testSetService) {
        super(navigationManager, service, testCasesDataProvider, beanFactory, viewEventBus, propertyService, userService);
        this.testStepService = testStepService;
        this.snapshotDefinitionService = snapshotDefinitionService;
        this.tedamFolderDataProvider = tedamFolderDataProvider;
        this.tedamFolderService = tedamFolderService;
        this.testSetService = testSetService;
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    @EventBusListenerMethod
    public void folderEditedOrAddedEvent(TedamFolderEvent event) throws LocalizedException {
        TedamFolder tedamFolder = event.getTedamFolder();
        tedamFolderService.save(tedamFolder);
        getView().organizeTedamFolderTestTreePanel(tedamFolderDataProvider);
    }

    @EventBusListenerMethod
    public void testCaseMovedEvent(TedamFolderSelectEvent event) throws LocalizedException {
        TedamFolder tedamFolder = event.getTedamFolder();
        List<TestCase> selectedItems = getView().getGrid().getSelectedItems().stream().collect(Collectors.toList());
        for (TestCase testCase : selectedItems) {
            testCase.setTestCaseFolderId(tedamFolder.getId());
        }
        getService().save(selectedItems);
        getView().getGrid().deselectAll();
        showAllTestSet();
    }

    @EventBusListenerMethod
    public void testSetSelectedEvent(TestSetSelectEvent event) throws LocalizedException {
        boolean oneAdded = false;
        List<String> alreadyAddedNames = new ArrayList<>();
        TestSet testSet = event.getTestSet();
        for (TestCase testCase : getView().getGrid().getSelectedItems()) {
            if (!testSet.getTestCases().contains(testCase)) {
                oneAdded = true;
                TestSetTestCase testSetTestCase = new TestSetTestCase(testCase);
                testSetTestCase.setPosition(testSet.getTestSetTestCases().size() + 1);
                testSetTestCase.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getUsername());
                testSet.getTestSetTestCases().add(testSetTestCase);
            } else {
                alreadyAddedNames.add(testCase.getId() + " - " + testCase.getName());
            }
        }
        testSetService.save(testSet);
        getView().showAdded(oneAdded, alreadyAddedNames);
        getView().getGrid().deselectAll();
    }

    public void copyTestCase(Set<TestCase> testCases) throws LocalizedException {
        for (TestCase testCase : testCases) {
            TestCase newTestCase = testCase.cloneTestCase();
            createTestCaseName(newTestCase);
            newTestCase.updateCreationData(SecurityUtils.getCurrentUser(getUserService()).getUsername(), SecurityUtils.getCurrentUser(getUserService()).getUsername(),
                    LocalDateTime.now(), LocalDateTime.now());
            prepareTestStepsAndLookUpsGenerator(newTestCase);
            buildAndSaveFillTestSteps(newTestCase);
            newTestCase = getService().save(newTestCase);
            copyTestCaseFileToNew(testCase, newTestCase);
            ((List<TestCase>) getView().getGrid().getGridDataProvider().getListDataProvider().getItems()).add(0, newTestCase);
        }
        getView().getGrid().refreshAll();

    }

    private void copyTestCaseFileToNew(TestCase testCase, TestCase newTestCase) throws LocalizedException {
        // import files
        try {
            FileUtils.copyDirectory(new File(getPropertyService().getTestcaseFolder(testCase.getId())), new File(getPropertyService().getTestcaseFolder(newTestCase.getId())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildAndSaveFillTestSteps(TestCase newTestCase) throws LocalizedException {
        // import teststeps
        for (TestStep testStep : newTestCase.getTestSteps()) {
            testStep.updateCreationData(SecurityUtils.getCurrentUser(getUserService()).getUsername(), SecurityUtils.getCurrentUser(getUserService()).getUsername(),
                    LocalDateTime.now(), LocalDateTime.now());
            // if the old teststep is formfill and its value is taken into account,
            // and a new snapshotDefinition is created.
            SnapshotDefinition newSnapshotDefiniton = testStepService.copyTestStepFillDefinitions(testStep);
            if (newSnapshotDefiniton != null) {
                //
                snapshotDefinitionService.createLookupParameterContent(newSnapshotDefiniton, newTestCase);
                newSnapshotDefiniton = snapshotDefinitionService.save(newSnapshotDefiniton);
                // set the newly created snapshotDefinition generator and recreate
                // the parameter..
                testStepService.regenerateTestStepParameter(testStep, newSnapshotDefiniton);
            }
        }
    }

    private void createTestCaseName(TestCase newTestCase) throws LocalizedException {
        while (getService().getTestCaseByName(newTestCase.getName()) != null) {
            newTestCase.setName(newTestCase.generateName());
        }
    }

    /**
     * this method prepareTestStepsAndLookUpsGenerator Generates the generators of TestStep and LookUp, and breaks the parameters.<br>
     *
     * @param testCase <br>
     * @throws LocalizedException
     * @author Canberk.Erkmen
     */
    private void prepareTestStepsAndLookUpsGenerator(TestCase testCase) throws LocalizedException {
        for (TestStep testStep : testCase.getTestSteps()) {
            if (!StringUtils.isEmpty(testStep.getParameter())) {
                testStep.setGenerator(GeneratorFactory.getGenerator(testStep.getType(), getBeanFactory()));
                testStep.getGenerator().degenerate(testStep.getParameter());
            }
        }
        for (TestStep lookUp : testCase.getLookUps()) {
            if (!StringUtils.isEmpty(lookUp.getParameter())) {
                lookUp.setGenerator(GeneratorFactory.getGenerator(lookUp.getType(), getBeanFactory()));
                lookUp.getGenerator().degenerate(lookUp.getParameter());
            }
        }

    }

    public void prepareWindowTedamFolder(boolean isEdited) throws LocalizedException {
        Set<TedamFolder> selectedItems = getView().getTedamFolderTreePanel().getTedamTree().getSelectedItems();
        if (isEdited && selectedItems.isEmpty()) {
            getView().showTreeRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        TedamFolder tedamFolder = new TedamFolder();
        tedamFolder.setFolderType(TedamFolderType.TESTCASE);
        tedamFolder.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getUsername());
        tedamFolder.setProject(SecurityUtils.getUserSessionProject());
        if (isEdited) {
            tedamFolder = selectedItems.iterator().next();
        }
        windowParameters.put(UIParameter.TEDAM_FOLDER, tedamFolder);
        getView().openWindowTedamFolder(windowParameters);
    }

    public void prepareWindowSelectTestSet() throws LocalizedException {
        Set<TestCase> selectedItems = getView().getGrid().getSelectedItems();
        if (selectedItems.isEmpty()) {
            getView().showGridRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        getView().openWindowSelectTestSet(windowParameters);
    }

    public void prepareWindowSelectTedamFolder() throws LocalizedException {
        if (getView().getGrid().getSelectedItems().isEmpty()) {
            getView().showGridRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.FOLDER_TYPE, TedamFolderType.TESTCASE);
        getView().openWindowSelectTedamFolder(windowParameters);
    }

    public void showAllTestSet() throws LocalizedException {
        TedamFolder selectedFolder = getView().getTedamFolderTreePanel().getSelectedFolder();
        if (selectedFolder != null) {
            getView().getTedamFolderTreePanel().getTedamTree().deselect(selectedFolder);
            ((TestCasesDataProvider) getDataPovider()).buildDataProvider();
        }
    }

    public void deleteFolder() throws LocalizedException {
        Set<TedamFolder> selectedItems = getView().getTedamFolderTreePanel().getTedamTree().getSelectedItems();
        if (selectedItems.isEmpty()) {
            getView().showTreeRowNotSelected();
            return;
        }
        TedamFolder tedamFolder = selectedItems.iterator().next();
        if (!tedamFolder.getChildFolders().isEmpty()) {
            getView().showFolderCanNotDelete();
            return;
        }
        List<TestCase> testCases = new ArrayList<>(getDataPovider().getListDataProvider().getItems());
        for (TestCase testCase : testCases) {
            testCase.setTestCaseFolderId(tedamFolder.getParentFolder().getId());
        }
        getService().save(testCases);
        tedamFolderService.deleteByLogic(tedamFolder.getId());
        getView().organizeTedamFolderTestTreePanel(tedamFolderDataProvider);
        getView().getTedamFolderTreePanel().getTedamTree().select(tedamFolder.getParentFolder());
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) {
        getView().organizeTedamFolderTestTreePanel(tedamFolderDataProvider);
    }

    public void addButtonClickEvent() {
        Set<TedamFolder> selectedItems = getView().getTedamFolderTreePanel().getTedamTree().getSelectedItems();
        if (selectedItems.isEmpty()) {
            getView().showTreeRowNotSelected();
            return;
        }
        TedamFolder tedamFolder = selectedItems.iterator().next();
        if (getView().getEditView() != null) {
            getNavigationManager().navigateTo(getView().getEditView(), "new" + "?" + UIParameter.FOLDER.toString().toLowerCase() + Constants.EQUAL + tedamFolder.getId());
        }

    }
}
