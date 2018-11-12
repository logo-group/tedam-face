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

package com.lbs.tedam.ui.view.testcase.edit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.SnapshotDefinitionService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.data.service.TestCaseService;
import com.lbs.tedam.data.service.TestStepService;
import com.lbs.tedam.exception.CreateNewFileException;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.FilterFillGenerator;
import com.lbs.tedam.generator.steptype.FormFillGenerator;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.generator.steptype.VerifyGenerator;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamFile;
import com.lbs.tedam.model.TestCase;
import com.lbs.tedam.model.TestCaseTestRun;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.TedamFaceEvents.FileAttachEvent;
import com.lbs.tedam.ui.TedamFaceEvents.FileUploadEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TestCaseSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepSelectedEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.components.window.uploadedfiles.UploadedFilesDataProvider;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.testcase.TestCaseGridView;
import com.lbs.tedam.ui.view.testset.edit.TestCaseTestRunDataProvider;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.EnumsV2.TestStepType;
import com.lbs.tedam.util.TedamDOMUtils;
import com.lbs.tedam.util.TedamFileUtils;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Grid.SelectionMode;

;

@SpringComponent
@ViewScope
public class TestCaseEditPresenter
		extends AbstractEditPresenter<TestCase, TestCaseService, TestCaseEditPresenter, TestCaseEditView> {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final TestStepDataProvider testStepDataProvider;
	private final LookUpDataProvider lookUpDataProvider;
	private final SnapshotDefinitionService snapshotDefinitionService;
	private final TestStepService testStepService;
	private final TestCaseTestRunDataProvider testCaseTestRunDataProvider;
	private final UploadedFilesDataProvider uploadedFilesDataProvider;

	@Autowired
	public TestCaseEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager,
			TestCaseService testCaseService, TedamUserService userService, BeanFactory beanFactory,
			TestStepDataProvider testStepDataProvider, SnapshotDefinitionService snapshotDefinitionService,
			LookUpDataProvider lookUpDataProvider, UploadedFilesDataProvider uploadedFilesDataProvider,
			TestStepService testStepService, TestCaseTestRunDataProvider testCaseTestRunDataProvider,
			PropertyService propertyService) {
		super(viewEventBus, navigationManager, testCaseService, TestCase.class, beanFactory, userService,
				propertyService);
		this.testStepDataProvider = testStepDataProvider;
		this.lookUpDataProvider = lookUpDataProvider;
		this.snapshotDefinitionService = snapshotDefinitionService;
		this.testStepService = testStepService;
		this.testCaseTestRunDataProvider = testCaseTestRunDataProvider;
		this.uploadedFilesDataProvider = uploadedFilesDataProvider;
	}

	@Override
	protected void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		TestCase testCase;
		Integer id = (Integer) windowParameters.get(UIParameter.ID);
		ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
		Integer folderId = (Integer) windowParameters.get(UIParameter.FOLDER);
		if ((Integer) windowParameters.get(UIParameter.ID) == 0) {
			testCase = new TestCase();
			testCase.setTestCaseFolderId(folderId);
			testCase.setProject(SecurityUtils.getCurrentUser(getUserService()).getProject());
		} else {
			testCase = getService().getById(id);
			if (testCase == null) {
				getView().showNotFound();
				return;
			} else if (mode.equals(ViewMode.EDIT) && getService().isTestSetInProgressStatus(testCase)) {
				getView().showTestSetInProgressStatus();
				return;
			}
			isAuthorized(testCase);
			getView().setFileUploadFinishedHandlerPath(getPropertyService().getTestcaseFolder(testCase.getId()));
		}
		refreshView(testCase, mode);
		testStepDataProvider.provideTestSteps(testCase);
		lookUpDataProvider.provideLookUps(testCase);
		testCaseTestRunDataProvider.setTestCase(testCase);
		uploadedFilesDataProvider.setFilePath(getPropertyService().getTestcaseFolder(testCase.getId()));
		getView().organizeTestStepsGrid(testStepDataProvider);
		getView().organizeLookUpsGrid(lookUpDataProvider);
		getView().organizeTestCaseTestRunsGrid(testCaseTestRunDataProvider);
		getView().organizeUploadedFilesGrid(uploadedFilesDataProvider);
		getTitleForHeader();
		// TODO canberk the bottom part review, they are not presenter
		// should be view
		organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
		setGridEditorAttributes(getView().getGridTestSteps(), mode != ViewMode.VIEW);
		setGridEditorAttributes(getView().getGridLookUps(), mode != ViewMode.VIEW);
	}

	public String getTestCaseFolder() throws LocalizedException {
		return getPropertyService().getTestcaseFolder(getItem().getId());
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

	@EventBusListenerMethod
	public void testCaseSelectedEvent(TestCaseSelectEvent event) throws LocalizedException {
		TestCase testCaseSource = event.getTestCaseList().get(0);
		TestCase testCaseTarget = getItem();
		// import teststeps
		for (int i = 0; i < testCaseTarget.getTestSteps().size(); i++) {
			if (i == testCaseSource.getTestSteps().size()) {
				break;
			}
			TestStep testStepTarget = testCaseTarget.getTestSteps().get(i);
			TestStep testStepSource = testCaseSource.getTestSteps().get(i);
			prepareTestStepForCopy(testStepTarget, testStepSource);
			testStepService.copyTestStepFillDefinitions(testStepTarget);
			getView().getGridTestSteps().getGridDataProvider().getListDataProvider().refreshItem(testStepTarget);
		}
		// import lookups
		for (TestStep testStepLookUp : testCaseSource.getLookUps()) {
			testStepService.copyTestStepFillDefinitions(testStepLookUp);
			getView().getGridLookUps().getGridDataProvider().getListDataProvider().refreshItem(testStepLookUp);
		}
		// import files
		try {
			FileUtils.copyDirectory(new File(getPropertyService().getTestcaseFolder(testCaseSource.getId())),
					new File(getPropertyService().getTestcaseFolder(getItem().getId())));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setHasChanges(true);
	}

	public void prepareTestStepTestRunWindow(TestCaseTestRun testCaseTestRun) throws LocalizedException {
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		windowParameters.put(UIParameter.TESTCASE_TESTRUN, testCaseTestRun);
		getView().openTestStepTestRunWindow(windowParameters);
	}

	public void prepareUploadedFilesWindow() throws LocalizedException {
		TedamFilterGrid<TestStep> activeTabsGrid = getView().getActiveTabsGrid();
		if (activeTabsGrid.getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		for (TestStep testStep : activeTabsGrid.getSelectedItems()) {
			if (!StringUtils.isEmpty(testStep.getParameter())) {
				getView().showCanNotChangeFileWhileParameterIsNotEmpty();
				return;
			}
		}
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		windowParameters.put(UIParameter.ID, getBinder().getBean().getId());
		getView().openUploadedFilesWindow(windowParameters);
	}

	public void prepareTestCaseWindow() throws LocalizedException {
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		windowParameters.put(UIParameter.SELECTION_MODE, SelectionMode.SINGLE);
		getView().openTestCaseWindow(windowParameters);
	}

	private void prepareTestStepForCopy(TestStep target, TestStep source) throws LocalizedException {
		target.setExpectedFormname(source.getExpectedFormname());
		target.setFilename(source.getFilename());
		target.setGenerator(GeneratorFactory.getGenerator(source.getType(), getBeanFactory()));
		target.getGenerator().degenerate(source.getParameter());
		target.setLookUp(source.isLookUp());
		target.setParameter(source.getParameter());
		target.setType(source.getType());
	}

	@EventBusListenerMethod
	public void fileAttachedEvent(FileAttachEvent event) throws LocalizedException {
		TedamFilterGrid<TestStep> activeTabsGrid = getView().getActiveTabsGrid();
		for (TestStep testStep : activeTabsGrid.getSelectedItems()) {
			if (testStep.getType() == TestStepType.FORM_OPEN || testStep.getType() == TestStepType.FORM_OPEN_SHORTCUT) {
				getView().showCanNotAttachFileToFormOpenSteps();
				continue;
			}
			testStep.setFilename(event.getUploadedFileName().getName());
			String expectedFormName = TedamDOMUtils.getExpectedFormName(
					getPropertyService().getTestcaseFolder(getItem().getId()) + event.getUploadedFileName().getName());
			testStep.setExpectedFormname(expectedFormName);
			activeTabsGrid.getGridDataProvider().getListDataProvider().refreshItem(testStep);
		}
		getView().getActiveTabsGrid().deselectAll();
	}

	@EventBusListenerMethod
	public void fileUploadEvent(FileUploadEvent event) throws LocalizedException {
		uploadedFilesDataProvider.setFilePath(getPropertyService().getTestcaseFolder(getItem().getId()));
		uploadedFilesDataProvider.refreshDataProviderByItems(
				TedamFileUtils.getFiles(getPropertyService().getTestcaseFolder(getItem().getId())));
		getView().organizeUploadedFilesGrid(uploadedFilesDataProvider);

		getView().getGridUploadedFiles().refreshAll();
		getView().getGridUploadedFiles().focus();

	}

	public void refreshUploadedFilesGrid() throws LocalizedException {
		uploadedFilesDataProvider.setFilePath(getPropertyService().getTestcaseFolder(getItem().getId()));
		uploadedFilesDataProvider.refreshDataProviderByItems(
				TedamFileUtils.getFiles(getPropertyService().getTestcaseFolder(getItem().getId())));
		getView().organizeUploadedFilesGrid(uploadedFilesDataProvider);

		getView().getGridUploadedFiles().refreshAll();
		getView().getGridUploadedFiles().focus();

	}

	@EventBusListenerMethod
	public void testStepSelectedEvent(TestStepSelectedEvent event) {
		TestStep targetTestStep = getView().getActiveTabsGrid().getSelectedItems().iterator().next();
		TestStep sourceTestStep = event.getTestStep();
		targetTestStep.setType(sourceTestStep.getType());
		targetTestStep.setParameter(sourceTestStep.getParameter());
		targetTestStep.setFilename(null);
		if (StringUtils.isEmpty(targetTestStep.getDescription())) {
			targetTestStep.setDescription(sourceTestStep.getDescription());
		}
		getView().getGridTestSteps().refreshAll();
		getItem().setAutomated(false);
		setHasChanges(true);
		getView().getActiveTabsGrid().deselectAll();
	}

	@EventBusListenerMethod
	public void testStepTypeParameterPreparedEvent(
			TestStepTypeParameterPreparedEvent testStepTypeParameterPreparedEvent) {
		testStepTypeParameterPreparedEvent.getTestStep()
				.setParameter(testStepTypeParameterPreparedEvent.getTestStep().getGenerator().generate());
		getView().getActiveTabsGrid().getGridDataProvider().getListDataProvider()
				.refreshItem(testStepTypeParameterPreparedEvent.getTestStep());
	}

	public void resetTestSteps() throws LocalizedException {
		TedamFilterGrid<TestStep> activeTabsGrid = getView().getActiveTabsGrid();
		if (activeTabsGrid.getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		for (TestStep testStep : activeTabsGrid.getSelectedItems()) {
			resetTestStep(testStep, activeTabsGrid.getGridDataProvider());
		}
		getView().getActiveTabsGrid().deselectAll();
	}

	public void addTestStepRow() throws LocalizedException {
		TestStep testStep = new TestStep();
		TedamFilterGrid<TestStep> activeTabsGrid = getView().getActiveTabsGrid();
		testStep.setLookUp(activeTabsGrid.equals(getView().getGridTestSteps()) ? false : true);
		testStep.setDateCreated(LocalDateTime.now());
		testStep.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getTedamUser().getUserName());
		testStep.setProject(SecurityUtils.getUserSessionProject());
		activeTabsGrid.getGridDataProvider().getListDataProvider().getItems().add(testStep);
		activeTabsGrid.refreshAll();
		getView().getActiveTabsGrid().deselectAll();
		getView().getActiveTabsGrid().scrollToEnd();
		getItem().setAutomated(false);
		setHasChanges(true);
	}

	public void removeTestStepRow() {
		TedamFilterGrid<TestStep> activeTabsGrid = getView().getActiveTabsGrid();
		if (activeTabsGrid.getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		// TODO:TestSteps are deleted directly from the database. it should be looked
		// at.
		activeTabsGrid.getSelectedItems()
				.forEach(testStep -> activeTabsGrid.getGridDataProvider().removeItem(testStep));
		activeTabsGrid.deselectAll();
		activeTabsGrid.refreshAll();
		setHasChanges(true);
	}

	public void prepareTestStepTypeWindow(TestStep testStep, ViewMode mode) throws LocalizedException {
		testStep.setTestCaseId(getItem().getId());
		if (getItem().getId().equals(Integer.valueOf(0))) {
			getView().showTestCaseNotSaved();
			return;
		} else if (testStep.getType() == null) {
			getView().showTestStepTypeNotSelectedMessage();
			return;
		} else if (!TestStepType.getFormOpenTestStepTypeList().contains(testStep.getType())
				&& !testStep.getType().equals(TestStepType.WAIT) && StringUtils.isEmpty(testStep.getFilename())) {
			// the screen can not be opened while there are no files in the form fill group.
			getView().showFileNotAttachedToTestStepMessage();
			return;
		} else if (TestStepType.getFormOpenTestStepTypeList().contains(testStep.getType())
				&& !StringUtils.isEmpty(testStep.getFilename())) {
			// the form can not open when there is a file in the open group.
			getView().showFileAttachedToTestStepMessage();
			return;
		} else if (!TestStepType.getFormOpenTestStepTypeList().contains(testStep.getType())
				&& !testStep.getType().equals(TestStepType.WAIT) && Files.notExists(Paths.get(
						getPropertyService().getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename()))) {
			// the screen does not open if the associated file does not exist.
			getView().showFileNotFound();
			return;
		} else if (TestStepType.REPORT.equals(testStep.getType()) && !FilenameUtils.getExtension(testStep.getFilename())
				.equals(Constants.FILE_EXTENSION_PDF.replace(".", ""))) {
			// no action is taken if the report is not associated with a pdf.
			getView().showPDFFileNotAttachedToTestStepMessage();
			return;
		}
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		windowParameters.put(UIParameter.MODE,
				StringUtils.isEmpty(testStep.getFilename()) == true ? ViewMode.VIEW : ViewMode.EDIT);
		windowParameters.put(UIParameter.TESTSTEP, testStep);
		if (testStep.getGenerator() == null) {
			testStep.setGenerator(GeneratorFactory.getGenerator(testStep.getType(), getBeanFactory()));
		}
		switch (testStep.getType()) {
		case FORM_OPEN:
			getView().openFormOpenWindow(windowParameters);
			break;
		case FORM_OPEN_SHORTCUT:
			getView().openFormOpenShortcutWindow(windowParameters);
			break;
		case BUTTON_CLICK:
			getView().openButtonClickWindow(windowParameters);
			break;
		case FORM_FILL:
			getView().openFormFillWindow(windowParameters);
			break;
		case FILTER_FILL:
			getView().openFilterFillWindow(windowParameters);
			break;
		case VERIFY:
			getView().openVerifyWindow(windowParameters);
			break;
		case MESSAGE_VERIFY:
			getView().openMessageVerifyWindow(windowParameters);
			break;
		case ROW_COUNT_VERIFY:
			getView().openRowCountVerifyWindow(windowParameters);
			break;
		case GRID_SEARCH:
			getView().openGridSearchWindow(windowParameters);
			break;
		case GRID_CELL_SELECT:
			getView().openGridCellSelectWindow(windowParameters);
			break;
		case GRID_ROW_SELECT:
			getView().openGridRowSelectWindow(windowParameters);
			break;
		case GRID_DOUBLE_CLICK:
			getView().openGridDoubleClickWindow(windowParameters);
			break;
		case GRID_DELETE:
			getView().openGridDeleteWindow(windowParameters);
			break;
		case POPUP:
			getView().openPopUpWindow(windowParameters);
			break;
		case REPORT:
			getView().openReportWindow(windowParameters);
			break;
		case DOUBLE_CLICK:
			getView().openDoubleClickWindow(windowParameters);
			break;
		case WAIT:
			getView().openWaitWindow(windowParameters);
			break;
		default:
			break;
		}
	}

	@Override
	protected Class<? extends View> getGridView() {
		return TestCaseGridView.class;
	}

	@Override
	protected TestCase save(TestCase testCase) throws LocalizedException {
		if (testCase.getTestCaseFolderId() == null) {
			getView().showFolderNotSelectedToTestCaseMessage();
			return null;
		}
		for (TestStep testStep : testCase.getTestSteps()) {
			if (testStep.getGenerator() != null && !testStep.getGenerator().validate()) {
				getView().showTestStepParametersNotReady();
				return null;
			}
		}
		setTestStepPosition(testCase);
		saveDefinitions(testCase);
		return super.save(testCase);

	}

	@Override
	public void okPressed() throws LocalizedException {
		saveFilters(getView().getGridTestSteps());
		saveFilters(getView().getGridLookUps());
		saveFilters(getView().getGridTestCaseRun());
		super.okPressed();
		try {
			TedamFileUtils.createNewFilePath(getPropertyService().getTestcaseFolder(getItem().getId()));
		} catch (CreateNewFileException e) {
			e.printStackTrace();
		}
	}

	private void saveFilters(TedamFilterGrid<?> grid) {
		if (getView().getViewMode() == ViewMode.VIEW) {
			SecurityUtils.saveFilterValue(getView().getClass().getName(), grid.saveFilterValues());
		}
	}

	private void saveDefinitions(TestCase testCase) throws LocalizedException {
		for (TestStep testStep : testCase.getTestSteps()) {
			// the step that was added as the select step of the test will not be the
			// filename.
			if (StringUtils.isEmpty(testStep.getFilename()) || testStep.getType() == null
					|| testStep.getGenerator() == null) {
				continue;
			}
			saveDefinitionsOfTestStepsAndLookUps(testStep);
		}
		for (TestStep testStepLookUp : testCase.getLookUps()) {
			if (StringUtils.isEmpty(testStepLookUp.getFilename()) || testStepLookUp.getType() == null
					|| testStepLookUp.getGenerator() == null) {
				continue;
			}
			saveDefinitionsOfTestStepsAndLookUps(testStepLookUp);
		}
	}

	private void setTestStepPosition(TestCase testCase) {
		for (ListIterator<TestStep> listIterator = testCase.getTestSteps().listIterator(); listIterator.hasNext();) {
			TestStep testStep = listIterator.next();
			testStep.setPosition(listIterator.nextIndex());
		}
	}

	private void saveDefinitionsOfTestStepsAndLookUps(TestStep testStep) throws LocalizedException {
		switch (testStep.getType()) {
		case FORM_FILL:
			FormFillGenerator formFillGenerator = (FormFillGenerator) testStep.getGenerator();
			Integer snapshotDefinitionId = snapshotDefinitionService.save(formFillGenerator.getSnapshotDefinition(),
					getPropertyService().getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename());
			formFillGenerator.setSnapshotDefinition(snapshotDefinitionService.getById(snapshotDefinitionId));
			break;
		case FILTER_FILL:
			FilterFillGenerator filterFillGenerator = (FilterFillGenerator) testStep.getGenerator();
			snapshotDefinitionId = snapshotDefinitionService.save(filterFillGenerator.getSnapshotDefinition(),
					getPropertyService().getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename());
			filterFillGenerator.setSnapshotDefinition(snapshotDefinitionService.getById(snapshotDefinitionId));
			break;
		case VERIFY:
			VerifyGenerator verifyGenerator = (VerifyGenerator) testStep.getGenerator();
			snapshotDefinitionId = snapshotDefinitionService.save(verifyGenerator.getSnapshotDefinition(),
					getPropertyService().getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename());
			verifyGenerator.setSnapshotDefinition(snapshotDefinitionService.getById(snapshotDefinitionId));
			break;
		default:
			break;
		}
		try {
			String parameter = null;
			parameter = testStep.getGenerator().generate();
			testStep.setParameter(parameter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void onTestStepTypeChange(TestStep testStep, TestStepType newTestStepType,
			AbstractDataProvider<TestStep> abstractDataProvider) {
		if (testStep.getType() != newTestStepType && testStep.getType() != null) {
			getView().showTestStepTypeChangeDialog(testStep, newTestStepType, abstractDataProvider);
		}
	}

	public void changeTestStepType(TestStep testStep, TestStepType newTestStepType,
			AbstractDataProvider<TestStep> abstractDataProvider) {
		testStep.setParameter(null);
		testStep.setGenerator(null);
		testStep.setType(newTestStepType);
		if (newTestStepType == TestStepType.FORM_OPEN || newTestStepType == TestStepType.FORM_OPEN_SHORTCUT) {
			testStep.setFilename(null);
		}
	}

	public void copyTestSteps(Set<TestStep> testSteps) throws LocalizedException {
		TedamFilterGrid<TestStep> activeTabsGrid = getView().getActiveTabsGrid();
		for (TestStep testStep : testSteps) {
			TestStep testStepClone = testStep.cloneTestStep();
			testStepClone.setDateCreated(LocalDateTime.now());
			testStepClone.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getTedamUser().getUserName());
			activeTabsGrid.getGridDataProvider().getListDataProvider().getItems().add(testStepClone);
		}
		activeTabsGrid.refreshAll();
		getView().getActiveTabsGrid().deselectAll();
		getView().getActiveTabsGrid().scrollToEnd();
		getItem().setAutomated(false);
		setHasChanges(true);
	}

	public void resetTestStep(TestStep testStep, AbstractDataProvider<TestStep> abstractDataProvider)
			throws LocalizedException {
		testStep.setGenerator(null);
		testStep.setType(null);
		testStep.setExpectedFormname(null);
		testStep.setDateUpdated(LocalDateTime.now());
		testStep.setUpdatedUser(SecurityUtils.getCurrentUser(getUserService()).getTedamUser().getUserName());
		testStep.setParameter(null);
		// Since this is a problem, it is closed with cancel in this way, a wide can be
		// viewed.
		// can be viewed.
		getView().getGridTestSteps().getEditor().cancel();
		abstractDataProvider.getListDataProvider().refreshItem(testStep);
	}

	public boolean checkFileForDeletion(TedamFile tedamFile) {
		for (TestStep testStep : getItem().getTestSteps()) {
			if (testStep.getFilename() != null && testStep.getFilename().equals(tedamFile.getName())) {
				getView().showCanNotDeleteAttachedFileMessage();
				return false;
			}
		}
		return true;
	}

	public void deleteFile(TedamFile tedamFile) throws LocalizedException {
		if (tedamFile == null) {
			getView().showFileNotSelectedMessage();
		} else {
			TedamFileUtils.deleteFile(getPropertyService().getTestcaseFolder(getItem().getId()) + tedamFile.getName());
			getView().getGridUploadedFiles().getGridDataProvider().removeItem(tedamFile);
			getView().getGridUploadedFiles().getDataProvider().refreshAll();
		}
	}

	@Override
	protected Project getProjectByEntity(TestCase entity) {
		return entity.getProject();
	}

	@Override
	protected void getTitleForHeader() {
		if (getItem().getName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getName());
		}
	}

}
