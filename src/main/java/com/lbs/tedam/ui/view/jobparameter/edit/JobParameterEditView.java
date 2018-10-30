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

package com.lbs.tedam.ui.view.jobparameter.edit;

import com.lbs.tedam.data.service.JobParameterService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.model.JobParameterValue;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@SpringView
public class JobParameterEditView extends AbstractEditView<JobParameter, JobParameterService, JobParameterEditPresenter, JobParameterEditView> {

    private static final long serialVersionUID = 1L;

    private TedamTextField name;
    private TedamFilterGrid<JobParameterValue> gridJobParameterValues;
    private TedamButton addButton;
    private TedamButton removeButton;

    @Autowired
    public JobParameterEditView(JobParameterEditPresenter presenter) {
        super(presenter);
    }

    @PostConstruct
    private void initView() {

        name = new TedamTextField("view.jobparameteredit.textfield.name", "full", true, true);

        buildJobParametersValueGrid();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name);
        addSection(getLocaleValue("view.viewedit.section.values"), 1, null, buildJobParametersValueGridButtons(), gridJobParameterValues);

        getPresenter().setView(this);
    }

    private Component buildJobParametersValueGridButtons() {
        HorizontalLayout hLayButtons = new HorizontalLayout();
        addButton = new TedamButton("view.jobparameteredit.button.addrow", VaadinIcons.PLUS_CIRCLE);
        addButton.setSizeFull();
        removeButton = new TedamButton("view.jobparameteredit.button.removerow", VaadinIcons.MINUS_CIRCLE);
        removeButton.setSizeFull();
        hLayButtons.addComponents(addButton, removeButton);

        removeButton.addClickListener(new ClickListener() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                getPresenter().removeRow(gridJobParameterValues.getSelectedItems());
            }
        });

        addButton.addClickListener(new ClickListener() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    getPresenter().addRow();
                } catch (LocalizedException e) {
                    logError(e);
                }
            }
        });

        return hLayButtons;
    }

    protected void buildJobParametersValueGrid() {
        TedamGridConfig<JobParameterValue> jobParameterValuesGridConfig = new TedamGridConfig<JobParameterValue>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.JOB_PARAMETER_VALUE_COLUMNS;
            }

            @Override
            public Class<JobParameterValue> getBeanType() {
                return JobParameterValue.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.VIEW);
                return operations;
            }

        };
        gridJobParameterValues = new TedamFilterGrid<JobParameterValue>(jobParameterValuesGridConfig, SelectionMode.MULTI) {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void onEditSelected(JobParameterValue jobParameterValue) {
                getEditor().editRow(getRowIndex(jobParameterValue));
            }
        };
    }

    protected void organizeGrid(AbstractDataProvider<JobParameterValue> dataProvider) {
        gridJobParameterValues.setGridDataProvider(dataProvider);
        gridJobParameterValues.initFilters();
    }

    public TedamFilterGrid<JobParameterValue> getGridJobParameterValues() {
        return gridJobParameterValues;
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.jobparameteredit.header");
    }

    public void showJobParameterValuesEmpty() {
        TedamNotification.showNotification(getLocaleValue("view.jobparameteredit.messages.showJobParameterValuesEmpty"), NotifyType.ERROR);
    }

}
