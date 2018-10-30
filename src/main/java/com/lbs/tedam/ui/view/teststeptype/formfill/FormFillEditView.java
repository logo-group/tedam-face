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

package com.lbs.tedam.ui.view.teststeptype.formfill;

import com.lbs.tedam.data.service.SnapshotDefinitionService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.SnapshotDefinition;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.lookup.WindowLookUp;
import com.lbs.tedam.ui.components.window.snapshotvalues.WindowSnapshotValues;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringView
public class FormFillEditView extends AbstractEditView<SnapshotDefinition, SnapshotDefinitionService, FormFillEditPresenter, FormFillEditView> {

    private static final long serialVersionUID = 1L;
    private final WindowSnapshotValues windowSnapshotValues;
    private final WindowLookUp windowLookUp;
    private TedamTextField description;
    private TedamTextField definitionType;
    private TedamFilterGrid<SnapshotValue> gridSnapshotValues;
    private TedamButton addButton;
    private TedamButton btnAddLookUp;
    private TedamButton btnRemoveLookUp;
    private TedamButton removeButton;

    @Autowired
    public FormFillEditView(FormFillEditPresenter presenter, WindowSnapshotValues windowSnapshotValues, WindowLookUp windowLookUp) {
        super(presenter);
        this.windowSnapshotValues = windowSnapshotValues;
        this.windowLookUp = windowLookUp;
    }

    @PostConstruct
    private void initView() {
        description = new TedamTextField("view.formfilledit.textfield.description", "full", true, true);
        definitionType = new TedamTextField("view.formfilledit.textfield.definitiontype", "full", true, false);
        buildGrid();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, definitionType, description);
        addSection(getLocaleValue("view.viewedit.section.values"), 1, null, buildTestStepsGridButtons(), gridSnapshotValues);

        getCancel().setVisible(false);
        getSave().setVisible(false);

        getPresenter().setView(this);
    }

    private Component buildTestStepsGridButtons() {
        HorizontalLayout hLayButtons = new HorizontalLayout();

        addButton = new TedamButton("general.button.add", VaadinIcons.PLUS_CIRCLE);
        removeButton = new TedamButton("general.button.delete", VaadinIcons.MINUS_CIRCLE);
        btnAddLookUp = new TedamButton("general.button.addlookup", VaadinIcons.GLASSES);
        btnRemoveLookUp = new TedamButton("general.button.removelookup", VaadinIcons.BUTTON);

        hLayButtons.addComponents(addButton, removeButton, btnAddLookUp, btnRemoveLookUp);

        removeButton.addClickListener(e -> {
            if (gridSnapshotValues.getSelectedItems().size() == 0) {
                showGridRowNotSelected();
                return;
            }
            for (SnapshotValue snapshot : gridSnapshotValues.getSelectedItems()) {
                getPresenter().removeRow(snapshot);
            }
        });

        addButton.addClickListener(e -> {
            try {
                getPresenter().prepareWindowSnapshotValues();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        btnAddLookUp.addClickListener(e -> {
            if (getPresenter().getTestStep().isLookUp()) {
                TedamNotification.showNotification(getLocaleValue("view.formfilledit.message.showLookUpWindowCanNotOpen"), NotifyType.ERROR);
                return;
            }
            if (gridSnapshotValues.getSelectedItems().size() == 0 || !gridSnapshotValues.getSelectedItems().iterator().next().isLookUp()) {
                showSnapshotValueWithLookUpNotSelected();
                return;
            }
            try {
                getPresenter().prepareWindowLookUp();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        btnRemoveLookUp.addClickListener(e -> {
            if (gridSnapshotValues.getSelectedItems().size() == 0) {
                showGridRowNotSelected();
                return;
            }
            getPresenter().removeLookUpParameter(gridSnapshotValues.getSelectedItems().iterator().next());
        });

        return hLayButtons;
    }

    protected void organizeGrid(AbstractDataProvider<SnapshotValue> snapshotValueDataProvider) {
        gridSnapshotValues.setGridDataProvider(snapshotValueDataProvider);
        gridSnapshotValues.initFilters();
    }

    private void showSnapshotValueWithLookUpNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.formfilledit.message.showSnapshotValueWithLookUpNotSelected"), NotifyType.ERROR);
    }

    private void showGridRowNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.formfilledit.message.showGridRowNotSelected"), NotifyType.ERROR);
    }

    protected void buildGrid() {
        TedamGridConfig<SnapshotValue> snapshotValuesGridConfig = new TedamGridConfig<SnapshotValue>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.SNAPSHOT_VALUE_COLUMNS;
            }

            @Override
            public Class<SnapshotValue> getBeanType() {
                return SnapshotValue.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

        };
        gridSnapshotValues = new TedamFilterGrid<SnapshotValue>(snapshotValuesGridConfig, SelectionMode.MULTI);
        gridSnapshotValues.addSelectionListener(new SelectionListener<SnapshotValue>() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void selectionChange(SelectionEvent<SnapshotValue> event) {
                if (!event.getAllSelectedItems().iterator().next().isLookUp()) {
                    btnAddLookUp.setEnabled(false);
                } else {
                    btnAddLookUp.setEnabled(true);
                }
            }
        });
    }

    public TedamFilterGrid<SnapshotValue> getGridSnapshotValues() {
        return gridSnapshotValues;
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.formfilleedit.header");
    }

    public void openSnapshotValueWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowSnapshotValues.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    public void openLookUpWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowLookUp.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

}