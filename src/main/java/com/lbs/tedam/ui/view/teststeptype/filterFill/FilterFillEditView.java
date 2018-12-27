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

package com.lbs.tedam.ui.view.teststeptype.filterFill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.lbs.tedam.ui.components.window.snapshotvalues.WindowSnapshotValues;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;

@SpringView
public class FilterFillEditView extends AbstractEditView<SnapshotDefinition, SnapshotDefinitionService, FilterFillEditPresenter, FilterFillEditView> {

    private static final long serialVersionUID = 1L;
    private final WindowSnapshotValues windowSnapshotValues;
    private TedamTextField description;
    private TedamTextField definitionType;
    private TedamFilterGrid<SnapshotValue> gridSnapshotValues;
    private TedamButton addButton;
    private TedamButton removeButton;

    @Autowired
    public FilterFillEditView(FilterFillEditPresenter presenter, WindowSnapshotValues windowSnapshotValues) {
        super(presenter);
        this.windowSnapshotValues = windowSnapshotValues;
    }

    @PostConstruct
    private void initView() {
        description = new TedamTextField("view.filterfilledit.textfield.description", "full", true, true);
        definitionType = new TedamTextField("view.filterfilledit.textfield.definitiontype", "full", true, false);
        buildGrid();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, definitionType, description);
		addSection(getLocaleValue("view.viewedit.section.values"), 1, null, buildTestStepsGridButtons(),
				gridSnapshotValues);

        getCancel().setVisible(false);
        getSave().setVisible(false);

        getPresenter().setView(this);
    }

    private Component buildTestStepsGridButtons() {
        HorizontalLayout hLayButtons = new HorizontalLayout();

        addButton = new TedamButton("general.button.add", VaadinIcons.PLUS_CIRCLE);
        removeButton = new TedamButton("general.button.delete", VaadinIcons.MINUS_CIRCLE);

        hLayButtons.addComponents(addButton, removeButton);

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

        return hLayButtons;
    }

    protected void organizeGrid(AbstractDataProvider<SnapshotValue> dataProvider) {
        gridSnapshotValues.setGridDataProvider(dataProvider);
        gridSnapshotValues.initFilters();
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
    }

    public TedamFilterGrid<SnapshotValue> getGridSnapshotValues() {
        return gridSnapshotValues;
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.filterfilleedit.header");
    }

    public void openSnapshotValueWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowSnapshotValues.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    private void showGridRowNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.formfilledit.message.showGridRowNotSelected"), NotifyType.ERROR);
    }

}