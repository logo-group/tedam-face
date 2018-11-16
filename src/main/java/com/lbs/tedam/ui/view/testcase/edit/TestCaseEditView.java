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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.TestCaseService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFile;
import com.lbs.tedam.model.TestCase;
import com.lbs.tedam.model.TestCaseTestRun;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamCheckBox;
import com.lbs.tedam.ui.components.basic.TedamMenuBar;
import com.lbs.tedam.ui.components.basic.TedamPopUpView;
import com.lbs.tedam.ui.components.basic.TedamTabSheet;
import com.lbs.tedam.ui.components.basic.TedamTextArea;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamExecutionStatusComboBox;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.GridFilterValue;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.layout.TedamVerticalLayout;
import com.lbs.tedam.ui.components.window.testcase.WindowTestCase;
import com.lbs.tedam.ui.components.window.testrun.WindowTestStepTestRun;
import com.lbs.tedam.ui.components.window.teststep.WindowSelectTestStep;
import com.lbs.tedam.ui.components.window.teststep.buttonclick.WindowTestStepTypeButtonClick;
import com.lbs.tedam.ui.components.window.teststep.doubleclick.WindowTestStepTypeDoubleClick;
import com.lbs.tedam.ui.components.window.teststep.filterfill.WindowTestStepTypeFilterFill;
import com.lbs.tedam.ui.components.window.teststep.formfill.WindowTestStepTypeFormFill;
import com.lbs.tedam.ui.components.window.teststep.formopen.WindowTestStepTypeFormOpen;
import com.lbs.tedam.ui.components.window.teststep.formopenshortcut.WindowTestStepTypeFormOpenShortcut;
import com.lbs.tedam.ui.components.window.teststep.gridcellselect.WindowTestStepTypeGridCellSelect;
import com.lbs.tedam.ui.components.window.teststep.griddelete.WindowTestStepTypeGridDelete;
import com.lbs.tedam.ui.components.window.teststep.griddoubleclick.WindowTestStepTypeGridDoubleClick;
import com.lbs.tedam.ui.components.window.teststep.gridrowselect.WindowTestStepTypeGridRowSelect;
import com.lbs.tedam.ui.components.window.teststep.gridsearch.WindowTestStepTypeGridSearch;
import com.lbs.tedam.ui.components.window.teststep.messageverify.WindowTestStepTypeMessageVerify;
import com.lbs.tedam.ui.components.window.teststep.popup.WindowTestStepTypePopup;
import com.lbs.tedam.ui.components.window.teststep.report.WindowTestStepTypeReport;
import com.lbs.tedam.ui.components.window.teststep.rowcountverify.WindowTestStepTypeRowCountVerify;
import com.lbs.tedam.ui.components.window.teststep.verify.WindowTestStepTypeVerify;
import com.lbs.tedam.ui.components.window.teststep.wait.WindowTestStepTypeWait;
import com.lbs.tedam.ui.components.window.uploadedfiles.WindowUploadedFiles;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.util.TedamUploadFinishedHandler;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.lbs.tedam.util.EnumsV2.TestStepType;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.UI;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.AllUploadFinishedHandler;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.MultiFileUpload;
import com.wcs.wcslib.vaadin.widget.multifileupload.ui.UploadStateWindow;

@SpringView
public class TestCaseEditView
		extends AbstractEditView<TestCase, TestCaseService, TestCaseEditPresenter, TestCaseEditView> {

	private static final long serialVersionUID = 1L;

	private final TedamExecutionStatusComboBox executionStatus;
	private final WindowUploadedFiles windowUploadedFiles;
	private final WindowTestStepTypeFormFill windowTestStepTypeFormFill;
	private final WindowTestStepTypeFilterFill windowTestStepTypeFilterFill;
	private final WindowTestStepTypeVerify windowTestStepTypeVerify;
	private final WindowTestStepTypeMessageVerify windowTestStepTypeMessageVerify;
	private final WindowTestStepTypeRowCountVerify windowTestStepTypeRowCountVerify;
	private final WindowTestStepTypeButtonClick windowTestStepTypeButtonClick;
	private final WindowTestStepTypeDoubleClick windowTestStepTypeDoubleClick;
	private final WindowTestStepTypeFormOpen windowTestStepTypeFormOpen;
	private final WindowTestStepTypeFormOpenShortcut windowTestStepTypeFormOpenShortcut;
	private final WindowTestStepTypeGridSearch windowTestStepTypeGridSearch;
	private final WindowTestStepTypeGridCellSelect windowTestStepTypeGridCellSelect;
	private final WindowTestStepTypeGridRowSelect windowTestStepTypeGridRowSelect;
	private final WindowTestStepTypeGridDoubleClick windowTestStepTypeGridDoubleClick;
	private final WindowTestStepTypeGridDelete windowTestStepTypeGridDelete;
	private final WindowTestStepTypePopup windowTestStepTypePopup;
	private final WindowTestStepTypeReport windowTestStepTypeReport;
	private final WindowTestStepTypeWait windowTestStepTypeWait;
	private final WindowSelectTestStep windowSelectTestStep;
	private final WindowTestCase windowTestCase;

	private final WindowTestStepTestRun windowTestStepTestRun;
	private TedamFilterGrid<TestCaseTestRun> gridTestCaseRun;
	private TedamFilterGrid<TedamFile> gridUploadedFiles;

	private TedamTextArea description;
	private TedamTextField name;
	private TedamCheckBox automated;

	private TedamFilterGrid<TestStep> gridTestSteps;
	private TedamFilterGrid<TestStep> gridLookUps;

	private TedamButton btnAddRow;
	private TedamButton btnRemoveRow;
	private TedamMenuBar mbSelectTestStep;

	private TedamButton btnTransferTestCase;

	private TedamButton btnAttachFile;
	private TedamButton btnResetTestSteps;
	private MultiFileUpload multiFileUpload;
	private TedamUploadFinishedHandler tedamUploadFinishedHandler;

	private TedamTabSheet tsTestStepAndLookUp;

	@Autowired
	public TestCaseEditView(TestCaseEditPresenter presenter, TedamExecutionStatusComboBox executionStatus,
			WindowUploadedFiles windowUploadedFiles, WindowTestStepTypeFormFill windowTestStepTypeFormFill,
			WindowTestStepTypeButtonClick windowTestStepTypeButtonClick,
			WindowTestStepTypeFilterFill windowTestStepTypeFilterFill,
			WindowTestStepTypeVerify windowTestStepTypeVerify,
			WindowTestStepTypeMessageVerify windowTestStepTypeMessageVerify,
			WindowTestStepTypeRowCountVerify windowTestStepTypeRowCountVerify,
			WindowTestStepTypeFormOpen windowTestStepTypeFormOpen,
			WindowTestStepTypeFormOpenShortcut windowTestStepTypeFormOpenShortcut,
			WindowTestStepTypeGridSearch windowTestStepTypeGridSearch,
			WindowTestStepTypeGridCellSelect windowTestStepTypeGridCellSelect,
			WindowTestStepTypeGridRowSelect windowTestStepTypeGridRowSelect,
			WindowTestStepTypeGridDoubleClick windowTestStepTypeGridDoubleClick,
			WindowTestStepTypeGridDelete windowTestStepTypeGridDelete, WindowTestStepTypePopup windowTestStepTypePopup,
			WindowTestStepTypeReport windowTestStepTypeReport, WindowSelectTestStep windowSelectTestStep,
			WindowTestCase windowTestCase, WindowTestStepTestRun windowTestStepTestRun,
			WindowTestStepTypeDoubleClick windowTestStepTypeDoubleClick,
			WindowTestStepTypeWait windowTestStepTypeWait) {
		super(presenter);
		this.executionStatus = executionStatus;
		this.windowUploadedFiles = windowUploadedFiles;
		this.windowTestStepTypeFormFill = windowTestStepTypeFormFill;
		this.windowTestStepTypeButtonClick = windowTestStepTypeButtonClick;
		this.windowTestStepTypeFilterFill = windowTestStepTypeFilterFill;
		this.windowTestStepTypeVerify = windowTestStepTypeVerify;
		this.windowTestStepTypeMessageVerify = windowTestStepTypeMessageVerify;
		this.windowTestStepTypeRowCountVerify = windowTestStepTypeRowCountVerify;
		this.windowTestStepTypeFormOpen = windowTestStepTypeFormOpen;
		this.windowTestStepTypeFormOpenShortcut = windowTestStepTypeFormOpenShortcut;
		this.windowTestStepTypeGridSearch = windowTestStepTypeGridSearch;
		this.windowTestStepTypeGridCellSelect = windowTestStepTypeGridCellSelect;
		this.windowTestStepTypeGridRowSelect = windowTestStepTypeGridRowSelect;
		this.windowTestStepTypeGridDoubleClick = windowTestStepTypeGridDoubleClick;
		this.windowTestStepTypeGridDelete = windowTestStepTypeGridDelete;
		this.windowTestStepTypePopup = windowTestStepTypePopup;
		this.windowTestStepTypeReport = windowTestStepTypeReport;
		this.windowSelectTestStep = windowSelectTestStep;
		this.windowTestCase = windowTestCase;
		this.windowTestStepTestRun = windowTestStepTestRun;
		this.windowTestStepTypeDoubleClick = windowTestStepTypeDoubleClick;
		this.windowTestStepTypeWait = windowTestStepTypeWait;
	}

	@PostConstruct
	private void initView() throws LocalizedException {
		tsTestStepAndLookUp = new TedamTabSheet("teststep");
		description = new TedamTextArea("view.testcaseedit.textfield.description", "full", true, true);
		name = new TedamTextField("view.testcaseedit.textfield.name", "full", true, true);
		automated = new TedamCheckBox("view.testcaseedit.checkbox.automated", null, true, false);

		buildLookUpsGrid();
		buildTestStepsGrid();
		buildTestCaseTestRunGrid();
		buildUploadedFilesGrid();
		buildTestStepsTab();
		buildLookUpsTab();

		tsTestStepAndLookUp.addSelectedTabChangeListener(new SelectedTabChangeListener() {

			/** long serialVersionUID */
			private static final long serialVersionUID = 1L;

			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				if (getActiveTabsGrid().equals(gridTestSteps)) {
					gridLookUps.deselectAll();
					mbSelectTestStep.setEnabled(getViewMode() != ViewMode.VIEW);
				} else {
					gridTestSteps.deselectAll();
					mbSelectTestStep.setEnabled(false);
				}

			}
		});

		addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name, executionStatus, description,
				automated);
		addSection(getLocaleValue("section.uploadedfiles"), 1, null, buildUploadedFilesGridButtons(),
				gridUploadedFiles);
		addSection(getLocaleValue("view.testcaseedit.section.teststeps"), 2, null, buildTestStepsGridButtons(),
				tsTestStepAndLookUp);
		addSection(getLocaleValue("section.testrunproperties"), 3, null, gridTestCaseRun);

		getPresenter().setView(this);
	}

	private void buildTestStepsTab() {
		arrangeGrid(gridTestSteps);
		tsTestStepAndLookUp.addTab(gridTestSteps, getLocaleValue("view.testcaseedit.tab.teststeps"))
				.setId("view.testcaseedit.tab.teststeps");
	}

	private void buildLookUpsTab() {
		arrangeGrid(gridTestSteps);
		tsTestStepAndLookUp.addTab(gridLookUps, getLocaleValue("view.testcaseedit.tab.lookups"))
				.setId("view.testcaseedit.tab.lookups");
	}

	public void setFileUploadFinishedHandlerPath(String path) {
		tedamUploadFinishedHandler.setPath(path);
	}

	private Component buildUploadedFilesGridButtons() throws LocalizedException {
		HorizontalLayout hLayButtons = new HorizontalLayout();

		UploadStateWindow uploadStateWindow = new UploadStateWindow();
		tedamUploadFinishedHandler = new TedamUploadFinishedHandler(getPresenter().getViewEventBus());
		multiFileUpload = new MultiFileUpload(tedamUploadFinishedHandler, uploadStateWindow);
		multiFileUpload.setCaption(getLocaleValue("view.testcaseedit.upload.files"));

		multiFileUpload.setAllUploadFinishedHandler(new AllUploadFinishedHandler() {

			@Override
			public void finished() {
				try {
					getPresenter().refreshUploadedFilesGrid();
				} catch (LocalizedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		hLayButtons.addComponents(multiFileUpload);

		return hLayButtons;
	}

	private Component buildTestStepsGridButtons() {
		HorizontalLayout hLayButtons = new HorizontalLayout();
		btnResetTestSteps = new TedamButton("view.testcaseedit.button.resetteststeps", VaadinIcons.FILE_REMOVE);

		btnAddRow = new TedamButton("view.testcaseedit.button.addrow", VaadinIcons.PLUS_CIRCLE);
		buildMbSelectTestStep();
		btnRemoveRow = new TedamButton("view.testcaseedit.button.removerow", VaadinIcons.MINUS_CIRCLE);

		btnTransferTestCase = new TedamButton("view.testcaseedit.button.importTestCase", VaadinIcons.TRAIN);
		btnAttachFile = new TedamButton("view.testcaseedit.button.attachfile", VaadinIcons.FILE_ADD);

		hLayButtons.addComponents(btnAttachFile, btnResetTestSteps, mbSelectTestStep, btnTransferTestCase, btnAddRow,
				btnRemoveRow);

		btnAttachFile.addClickListener(e -> {
			try {
				getPresenter().prepareUploadedFilesWindow();
			} catch (LocalizedException e2) {
				logError(e2);
			}
		});
		btnResetTestSteps.addClickListener(e -> {
			try {
				getPresenter().resetTestSteps();
			} catch (LocalizedException e1) {
				logError(e1);
			}
		});
		btnTransferTestCase.addClickListener(e -> {
			try {
				getPresenter().prepareTestCaseWindow();
			} catch (LocalizedException e2) {
				logError(e2);
			}
		});
		btnAddRow.addClickListener(e -> {
			try {
				getPresenter().addTestStepRow();
			} catch (LocalizedException e1) {
				logError(e1);
			}
		});
		btnRemoveRow.addClickListener(e -> getPresenter().removeTestStepRow());

		return hLayButtons;
	}

	private void buildMbSelectTestStep() {
		mbSelectTestStep = new TedamMenuBar("view.testcaseedit.button.selectteststep");
		MenuItem menuButton = mbSelectTestStep.addItem(getLocaleValue("view.testcaseedit.button.selectteststep"), null,
				null);

		for (TestStepType testStepType : TestStepType.getFillTestStepTypeList()) {
			menuButton.addItem(testStepType.toString(), new MenuBar.Command() {

				/** long serialVersionUID */
				private static final long serialVersionUID = 1L;

				@Override
				public void menuSelected(com.vaadin.ui.MenuBar.MenuItem selectedItem) {
					Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
					if (getActiveTabsGrid().getSelectedItems().size() != 1) {
						TedamNotification.showNotification(
								getLocaleValue("view.testcaseedit.messages.showTestStepTypeNotSelected"),
								NotifyType.ERROR);
						return;
					}
					windowParameters.put(UIParameter.TEST_STEP_TYPE, testStepType);
					try {
						openSelectTestStepWindow(windowParameters);
					} catch (LocalizedException e) {
						logError(e);
					}
				}
			});

		}

	}

	public TedamFilterGrid<TestStep> getActiveTabsGrid() {
		if (tsTestStepAndLookUp.getSelectedTab().equals(getGridTestSteps())) {
			return gridTestSteps;
		} else if (tsTestStepAndLookUp.getSelectedTab().equals(getGridLookUps())) {
			return gridLookUps;
		}
		return null;
	}

	protected void organizeTestStepsGrid(AbstractDataProvider<TestStep> abstractDataProvider) {
		gridTestSteps.setGridDataProvider(abstractDataProvider);
		gridTestSteps.initFilters();
		fetchSavedFilters(gridTestSteps);
	}

	protected void organizeLookUpsGrid(AbstractDataProvider<TestStep> abstractDataProvider) {
		gridLookUps.setGridDataProvider(abstractDataProvider);
		gridLookUps.initFilters();
		fetchSavedFilters(gridLookUps);
	}

	protected void organizeTestCaseTestRunsGrid(AbstractDataProvider<TestCaseTestRun> dataProvider) {
		gridTestCaseRun.setGridDataProvider(dataProvider);
		gridTestCaseRun.initFilters();
		gridTestCaseRun.sort("startDate", SortDirection.DESCENDING);
		fetchSavedFilters(gridTestCaseRun);
	}

	private void fetchSavedFilters(TedamFilterGrid<?> grid) {
		GridFilterValue filterValues = SecurityUtils.loadFilterValue(this.getClass().getName(), grid.getId());
		grid.laodFilterValues(filterValues);
	}

	protected void organizeUploadedFilesGrid(AbstractDataProvider<TedamFile> dataProvider) {
		gridUploadedFiles.setGridDataProvider(dataProvider);
		gridUploadedFiles.initFilters();
	}

	private TedamGridConfig<TestStep> buildTestStepGridConfig() {
		TedamGridConfig<TestStep> testStepsGridConfig = new TedamGridConfig<TestStep>() {

			@Override
			public List<GridColumn> getColumnList() {
				return GridColumns.GridColumn.TEST_STEPS_COLUMNS;
			}

			@Override
			public Class<TestStep> getBeanType() {
				return TestStep.class;
			}

			@Override
			public List<RUDOperations> getRUDOperations() {
				List<RUDOperations> operations = new ArrayList<RUDOperations>();
				operations.add(RUDOperations.VIEW);
				operations.add(RUDOperations.EDIT);
				return operations;
			}

		};
		return testStepsGridConfig;
	}

	protected void buildUploadedFilesGrid() {
		TedamGridConfig<TedamFile> uploadedFilesGridConfig = new TedamGridConfig<TedamFile>() {

			@Override
			public List<RUDOperations> getRUDOperations() {
				List<RUDOperations> operations = new ArrayList<RUDOperations>();
				operations.add(RUDOperations.DELETE);
				return operations;
			}

			@Override
			public List<GridColumn> getColumnList() {
				return GridColumn.UPLOADED_FILES_COLUMNS;
			}

			@Override
			public Class<TedamFile> getBeanType() {
				return TedamFile.class;
			}

		};
		gridUploadedFiles = new TedamFilterGrid<TedamFile>(uploadedFilesGridConfig, SelectionMode.NONE) {

			@Override
			public void onDeleteSelected(TedamFile item) {
				// TODO Auto-generated method stub
				confirmDelete(item);
			}

		};
		gridUploadedFiles.setHeightByRows(15);
		gridUploadedFiles.setId("TestCaseUploadedFilesGrid");
	}

	private void confirmDelete(TedamFile item) {
		if (!getPresenter().checkFileForDeletion(item)) {
			return;
		}
		TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

			@Override
			public void onConfirm() {
				try {
					getPresenter().deleteFile(item);
				} catch (LocalizedException e) {
					logError(e);
				}
			}

			@Override
			public void onCancel() {
			}
		}, getLocaleValue("confirm.message.delete"), getLocaleValue("general.button.ok"),
				getLocaleValue("general.button.cancel"));
	}

	protected void buildTestCaseTestRunGrid() {
		TedamGridConfig<TestCaseTestRun> testCaseTestRunsGridConfig = new TedamGridConfig<TestCaseTestRun>() {

			@Override
			public List<GridColumn> getColumnList() {
				return GridColumns.GridColumn.TEST_CASE_TEST_RUN_COLUMNS;
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
		gridTestCaseRun.setHeightByRows(15);
		gridTestCaseRun.setId("TestCaseRunGrid");
	}

	private List<Component> buildExecutionResultComponents(TestCaseTestRun item) {
		Integer idValue = gridTestCaseRun.getRowIndex(item) + 1;
		TedamVerticalLayout popupContent = buildPopUpContent(item);
		TedamPopUpView executionResultPopUp = new TedamPopUpView(null, popupContent);
		TedamButton showExecutionResultButton = buildShowExecutionResultButton(idValue);
		showExecutionResultButton.addClickListener(event -> executionResultPopUp.setPopupVisible(true));
		return Arrays.asList(executionResultPopUp, showExecutionResultButton);
	}

	private TedamButton buildShowExecutionResultButton(Integer idValue) {
		TedamButton showExecutionResultButton = new TedamButton("view.testcaseedit.button.showExecutionResult",
				VaadinIcons.INFO);
		showExecutionResultButton.setId(showExecutionResultButton.getId() + "." + idValue);
		showExecutionResultButton.setSizeUndefined();
		showExecutionResultButton.setCaption("");
		return showExecutionResultButton;
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

	protected void buildTestStepsGrid() {
		gridTestSteps = new TedamFilterGrid<TestStep>(buildTestStepGridConfig(), SelectionMode.MULTI) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void onCopyRow() {
				if (canCopy()) {
					try {
						getPresenter().copyTestSteps(getSelectedItems());
					} catch (LocalizedException e) {
						logError(e);
					}
				}
			}

			@Override
			public void onComboValueChange(ValueChangeEvent<Object> event) {
				if (event.getValue() == null || getClickedItem() == null) {
					return;
				}
				getPresenter().onTestStepTypeChange(getClickedItem(), (TestStepType) event.getValue(),
						gridTestSteps.getGridDataProvider());
			}

			@Override
			public void onViewSelected(TestStep testStep) {
				try {
					getPresenter().prepareTestStepTypeWindow(testStep, ViewMode.EDIT);
				} catch (LocalizedException e) {
					logError(e);
				}
			}

			@Override
			public void onEditSelected(TestStep testStep) {
				getEditor().editRow(getRowIndex(testStep));
			}
		};
		gridTestSteps.getColumn(GridColumn.TEST_STEP_DESCRIPTION.getColumnName())
				.setDescriptionGenerator(TestStep::getDescription);
		gridTestSteps.setId("TestStepGrid");
	}

	protected void buildLookUpsGrid() {
		gridLookUps = new TedamFilterGrid<TestStep>(buildTestStepGridConfig(), SelectionMode.MULTI) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onComboValueChange(ValueChangeEvent<Object> event) {
				if (event.getValue() == null || getClickedItem() == null) {
					return;
				}
				getPresenter().onTestStepTypeChange(getClickedItem(), (TestStepType) event.getValue(),
						gridLookUps.getGridDataProvider());
			}

			@Override
			public void onEditSelected(TestStep testStep) {
				try {
					getPresenter().prepareTestStepTypeWindow(testStep, ViewMode.EDIT);
				} catch (LocalizedException e) {
					logError(e);
				}
			}

		};
		gridLookUps.setId("LookUpsGrid");
	}

	private void arrangeGrid(TedamFilterGrid<?> grid) {
		grid.getColumn("description").setMaximumWidth(640).setResizable(false); // to provide wordwrap
		grid.setHeightByRows(10);

	}

	public void showFileNotAttachedToTestStepMessage() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showFileNotAttachedToTestStep"),
				NotifyType.ERROR);
	}

	public void showCanNotDeleteAttachedFileMessage() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showCanNotDeleteAttachedFile"),
				NotifyType.ERROR);
	}

	public void showFileAttachedToTestStepMessage() {
		TedamNotification.showNotification(
				getLocaleValue("view.testcaseedit.messages.showFileAttachedToTestStepMessage"), NotifyType.ERROR);
	}

	public void showFolderNotSelectedToTestCaseMessage() {
		TedamNotification.showNotification(
				getLocaleValue("view.testcaseedit.messages.showFolderNotSelectedToTestCaseMessage"), NotifyType.ERROR);
	}

	public void showTestStepTypeNotSelectedMessage() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showTestStepTypeNotSelected"),
				NotifyType.ERROR);
	}

	public void showTestCaseNotSaved() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showTestCaseNotSaved"),
				NotifyType.ERROR);
	}

	public void showTestStepParametersNotReady() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showTestStepParametersNotReady"),
				NotifyType.ERROR);
	}

	public void showGridRowNotSelected() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showGridRowNotSelected"),
				NotifyType.ERROR);
	}

	public void showCanNotChangeFileWhileParameterIsNotEmpty() {
		TedamNotification.showNotification(
				getLocaleValue("view.testcaseedit.messages.showCanNotChangeFileWhileParameterIsNotEmpty"),
				NotifyType.ERROR);
	}

	public void showCanNotAttachFileToFormOpenSteps() {
		TedamNotification.showNotification(
				getLocaleValue("view.testcaseedit.messages.showCanNotAttachFileToFormOpenSteps"), NotifyType.WARNING);
	}

	public void showTestSetInProgressStatus() {
		TedamNotification.showNotification(getLocaleValue("view.testsetedit.messages.showTestSetInProgressStatus"),
				NotifyType.ERROR);
	}

	public void showFileNotFound() {
		TedamNotification.showNotification(getLocaleValue("view.testsetedit.messages.showFileNotFound"),
				NotifyType.ERROR);
	}

	public void showPDFFileNotAttachedToTestStepMessage() {
		TedamNotification.showNotification(
				getLocaleValue("view.testsetedit.messages.showPDFFileNotAttachedToTestStepMessage"), NotifyType.ERROR);
	}

	public void showFileNotSelectedMessage() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showFileNotSelectedMessage"),
				NotifyType.ERROR);
	}

	public void showTestStepTypeChangeDialog(TestStep testStep, TestStepType newTestStepType,
			AbstractDataProvider<TestStep> abstractDataProvider) {

		TedamDialog.confirm(UI.getCurrent(), new ConfirmationListener() {

			@Override
			public void onConfirm() {
				getPresenter().changeTestStepType(testStep, newTestStepType, abstractDataProvider);
			}

			@Override
			public void onCancel() {
			}
		}, getLocaleValue("confirm.message.teststeptypechange"), getLocaleValue("general.button.confirm"),
				getLocaleValue("general.button.cancel"));

	}

	public void openTestCaseWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestCase.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestCase.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openUploadedFilesWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowUploadedFiles.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowUploadedFiles.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openFormFillWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeFormFill.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeFormFill.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openFilterFillWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeFilterFill.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeFilterFill.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openVerifyWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeVerify.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeVerify.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openMessageVerifyWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeMessageVerify.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeMessageVerify.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openButtonClickWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeButtonClick.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeButtonClick.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openDoubleClickWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeDoubleClick.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeDoubleClick.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openFormOpenWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeFormOpen.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeFormOpen.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openFormOpenShortcutWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeFormOpenShortcut.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeFormOpenShortcut.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openRowCountVerifyWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeRowCountVerify.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeRowCountVerify.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openGridSearchWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeGridSearch.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeGridSearch.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openGridCellSelectWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeGridCellSelect.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeGridCellSelect.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openGridRowSelectWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeGridRowSelect.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeGridRowSelect.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openGridDoubleClickWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeGridDoubleClick.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeGridDoubleClick.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openGridDeleteWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeGridDelete.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeGridDelete.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openPopUpWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypePopup.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypePopup.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openReportWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeReport.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeReport.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openWaitWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowTestStepTypeWait.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowTestStepTypeWait.close();
			TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
		}
	}

	public void openSelectTestStepWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowSelectTestStep.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			windowSelectTestStep.close();
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

	public TedamFilterGrid<TestStep> getGridTestSteps() {
		return gridTestSteps;
	}

	public TedamFilterGrid<TedamFile> getGridUploadedFiles() {
		return gridUploadedFiles;
	}

	public TedamFilterGrid<TestStep> getGridLookUps() {
		return gridLookUps;
	}

	public TedamFilterGrid<TestCaseTestRun> getGridTestCaseRun() {
		return gridTestCaseRun;
	}

	public TedamButton getBtnAttachFile() {
		return btnAttachFile;
	}

	@Override
	public String getHeader() {
		return getLocaleValue("view.testcaseedit.header");
	}

	public TedamTextField getName() {
		return name;
	}

	public void setName(TedamTextField name) {
		this.name = name;
	}

	@Override
	protected void collectGrids() {
		super.collectGrids();
		getGridList().add(gridTestCaseRun);
		getGridList().add(gridUploadedFiles);
		getGridList().add(gridTestSteps);
		getGridList().add(gridLookUps);
	}

}