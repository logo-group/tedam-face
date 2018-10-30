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
import com.lbs.tedam.data.service.TestCaseService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.model.TestCase;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.TedamFolderTreePanel;
import com.lbs.tedam.ui.components.TedamFolderTreePanel.TedamFolderTreePanelListener;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.folder.TedamFolderDataProvider;
import com.lbs.tedam.ui.components.window.folder.WindowSelectTedamFolder;
import com.lbs.tedam.ui.components.window.folder.WindowTedamFolder;
import com.lbs.tedam.ui.components.window.testset.WindowSelectTestSet;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractGridView;
import com.lbs.tedam.ui.view.testcase.edit.TestCaseEditView;
import com.lbs.tedam.util.EnumsV2.TedamFolderType;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringView
public class TestCaseGridView extends AbstractGridView<TestCase, TestCaseService, TestCaseGridPresenter, TestCaseGridView> {

    private static final long serialVersionUID = 1L;

    private TedamFolderTreePanel tedamFolderTreePanel;
    private WindowTedamFolder windowTedamFolder;
    private WindowSelectTestSet windowSelectTestSet;
    private WindowSelectTedamFolder windowSelectTedamFolder;

    private TedamGridConfig<TestCase> config = new TedamGridConfig<TestCase>() {

        @Override
        public List<GridColumn> getColumnList() {
            return GridColumns.GridColumn.TEST_CASES_COLUMNS;
        }

        @Override
        public Class<TestCase> getBeanType() {
            return TestCase.class;
        }

        @Override
        public List<RUDOperations> getRUDOperations() {
            List<RUDOperations> operations = new ArrayList<RUDOperations>();
            operations.add(RUDOperations.DELETE);
            operations.add(RUDOperations.VIEW);
            return operations;
        }

    };

    @Autowired
    public TestCaseGridView(TestCaseGridPresenter presenter, TedamFolderTreePanel tedamFolderTreePanel, WindowTedamFolder windowTedamFolder,
                            WindowSelectTestSet windowSelectTestSet, WindowSelectTedamFolder windowSelectTedamFolder) {
        super(presenter, SelectionMode.MULTI);
        this.tedamFolderTreePanel = tedamFolderTreePanel;
        this.windowTedamFolder = windowTedamFolder;
        this.windowSelectTestSet = windowSelectTestSet;
        this.windowSelectTedamFolder = windowSelectTedamFolder;
    }

    @PostConstruct
    private void init() {
        getPresenter().setView(this);
        setHeader(getLocaleValue("view.testcasesgrid.header"));
        getTopBarLayout().addComponents(buildTestCaseCopyButton(), buildAddTestSetButton(), buildMoveFolderButton());
        buildTedamFolderTreePanel();
    }

    @Override
    public void buildGridColumnDescription() {
        getGrid().getColumn(GridColumn.TEST_CASE_NAME.getColumnName()).setDescriptionGenerator(TestCase::getName);
    }

    protected void organizeTedamFolderTestTreePanel(TedamFolderDataProvider tedamFolderDataProvider) {
        try {
            tedamFolderDataProvider.buildDataProvider(TedamFolderType.TESTCASE);
        } catch (LocalizedException e) {
            logError(e);
        }
        tedamFolderTreePanel.getTedamTree().setDataProvider(tedamFolderDataProvider.getTreeDataProvider());
        tedamFolderTreePanel.getTedamTree().expand(tedamFolderDataProvider.getTreeDataProvider().getTreeData().getRootItems());
    }

    protected void buildTedamFolderTreePanel() {
        getGridLayout().setSecondComponent(getGrid());
        getGridLayout().setFirstComponent(tedamFolderTreePanel);
        getGridLayout().setMinSplitPosition(250, Unit.PIXELS);
        getGridLayout().setMaxSplitPosition(40, Unit.PERCENTAGE);
        getGridLayout().setSplitPosition(20, Unit.PERCENTAGE);
        getGridLayout().setLocked(false);
        tedamFolderTreePanel.setClickListener(new TedamFolderTreePanelListener() {

            @Override
            public void showAllButtonClickOperations() {
                try {
                    getPresenter().showAllTestSet();
                } catch (LocalizedException e) {
                    logError(e);
                }
            }

            @Override
            public void removeButtonClickOperations() {
                try {
                    getPresenter().deleteFolder();
                } catch (LocalizedException e) {
                    logError(e);
                }
            }

            @Override
            public void editButtonClickOperations() {
                try {
                    getPresenter().prepareWindowTedamFolder(true);
                } catch (LocalizedException e) {
                    logError(e);
                }
            }

            @Override
            public void addButtonClickOperations() {
                try {
                    getPresenter().prepareWindowTedamFolder(false);
                } catch (LocalizedException e) {
                    logError(e);
                }
            }

            @Override
            public void addSelectionListener(SelectionEvent<TedamFolder> e) {
                if (!e.getAllSelectedItems().isEmpty()) {
                    TedamFolder selectedFolder = e.getAllSelectedItems().iterator().next();
                    try {
                        ((TestCasesDataProvider) getPresenter().getDataPovider()).setTedamFolder(selectedFolder);
                    } catch (LocalizedException e1) {
                        logError(e1);
                    }
                }

            }
        });
    }

    private Component buildTestCaseCopyButton() {
        TedamButton btnCopyTestCase = new TedamButton("general.button.copy");
        btnCopyTestCase.addStyleName("primary");
        btnCopyTestCase.setWidthUndefined();
        btnCopyTestCase.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (getGrid().getSelectedItems().isEmpty()) {
                    TedamNotification.showNotification(getLocaleValue("view.testcasesgrid.message.testCaseNotSelected"), NotifyType.ERROR);
                    return;
                }
                try {
                    getPresenter().copyTestCase(getGrid().getSelectedItems());
                } catch (LocalizedException e) {
                    logError(e);
                }
                getGrid().deselectAll();
            }
        });
        return btnCopyTestCase;
    }

    private Component buildAddTestSetButton() {
        TedamButton btnAddTestSet = new TedamButton("view.testcasesgrid.addTestSet");
        btnAddTestSet.addStyleName("primary");
        btnAddTestSet.setWidthUndefined();
        btnAddTestSet.addClickListener(e -> {
            try {
                getPresenter().prepareWindowSelectTestSet();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        return btnAddTestSet;
    }

    private Component buildMoveFolderButton() {
        TedamButton moveFolder = new TedamButton("view.testcasesgrid.moveFolder");
        moveFolder.addStyleName("primary");
        moveFolder.setWidthUndefined();
        moveFolder.addClickListener(e -> {
            try {
                getPresenter().prepareWindowSelectTedamFolder();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        return moveFolder;
    }

    public void openWindowTedamFolder(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowTedamFolder.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowTedamFolder.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    public void openWindowSelectTestSet(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowSelectTestSet.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowSelectTestSet.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    public void openWindowSelectTedamFolder(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowSelectTedamFolder.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowSelectTedamFolder.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    public void showGridRowNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.testsetsgrid.messages.showGridRowNotSelected"), NotifyType.ERROR);
    }

    public void showTreeRowNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.testsetsgrid.messages.showTreeRowNotSelected"), NotifyType.ERROR);
    }

    public void showFolderCanNotDelete() {
        TedamNotification.showNotification(getLocaleValue("view.testsetsgrid.messages.showFolderCanNotDelete"), NotifyType.ERROR);
    }

    protected void showAdded(boolean oneAdded, List<String> alreadyAddedNames) {
        String message = "";
        if (oneAdded) {
            String addedMessage = getLocaleValue("view.testsetsgrid.messages.showAddedToTestSet");
            message += addedMessage + "\n";
        }
        if (alreadyAddedNames.size() > 0) {
            String alreadyAddedMessage = getLocaleValue("view.testsetsgrid.messages.showAlreadyAddedToTestSet");
            message += alreadyAddedMessage + "\n";
            for (String s : alreadyAddedNames) {
                message += s + "\n";
            }
        }
        if (message.length() > 0)
            TedamNotification.showTrayNotification(message, NotifyType.WARNING);
    }

    @Override
    protected TedamGridConfig<TestCase> getTedamGridConfig() {
        return config;
    }

    @Override
    protected Class<? extends View> getEditView() {
        return TestCaseEditView.class;
    }

    public TedamFolderTreePanel getTedamFolderTreePanel() {
        return tedamFolderTreePanel;
    }

    @Override
    public void addButtonClickEvent() {
        getPresenter().addButtonClickEvent();
    }

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        super.beforeLeave(event);
        SecurityUtils.saveSelectedFolder(this.getClass().getName(), tedamFolderTreePanel.getSelectedFolder());
    }

    @Override
    public void enter(ViewChangeEvent event) {
        super.enter(event);
        TedamFolder selectedFolder = SecurityUtils.loadSelectedFolder(this.getClass().getName());
        tedamFolderTreePanel.loadSelectedFolder(selectedFolder);
    }
}
