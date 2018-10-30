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

package com.lbs.tedam.ui.view.jobparameter;

import com.lbs.tedam.data.service.JobParameterService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.jobparametervalue.WindowJobParameterValue;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractGridView;
import com.lbs.tedam.ui.view.jobparameter.edit.JobParameterEditView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringView
public class JobParameterGridView extends AbstractGridView<JobParameter, JobParameterService, JobParameterGridPresenter, JobParameterGridView> {

    private static final long serialVersionUID = 1L;

    private WindowJobParameterValue windowJobParameterValue;

    private TedamGridConfig<JobParameter> config = new TedamGridConfig<JobParameter>() {

        @Override
        public List<GridColumn> getColumnList() {
            return GridColumns.GridColumn.JOB_PARAMETER_COLUMNS;
        }

        @Override
        public Class<JobParameter> getBeanType() {
            return JobParameter.class;
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
    public JobParameterGridView(JobParameterGridPresenter presenter, WindowJobParameterValue windowJobParameterValue) {
        super(presenter, SelectionMode.MULTI);
        this.windowJobParameterValue = windowJobParameterValue;
    }

    @PostConstruct
    private void init() {
        getPresenter().setView(this);

        setHeader(getLocaleValue("view.jobparametersgrid.header"));
        getTopBarLayout().addComponents(buildUploadEnvironmentButton());

    }

    @Override
    public void buildGridColumnDescription() {
        getGrid().getColumn(GridColumn.JOB_PARAMETER_NAME.getColumnName()).setDescriptionGenerator(JobParameter::getName);
    }

    private Component buildUploadEnvironmentButton() {
        TedamButton uploadEnvironment = new TedamButton("view.jobparametersgrid.uploadEnvironments", VaadinIcons.UPLOAD);
        uploadEnvironment.setCaption("");
        uploadEnvironment.addStyleName("primary");
        uploadEnvironment.setWidthUndefined();
        uploadEnvironment.addClickListener(e -> {
            try {
                getPresenter().prepareJobParameterValueWindow();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        return uploadEnvironment;
    }

    @Override
    protected TedamGridConfig<JobParameter> getTedamGridConfig() {
        return config;
    }

    @Override
    protected Class<? extends View> getEditView() {
        return JobParameterEditView.class;
    }

    public void showSelectOneJobParameter() {
        TedamNotification.showNotification(getLocaleValue("view.jobparametersgrid.messages.selectOneJobParameter"), NotifyType.WARNING);
    }

    public void showJobParameterNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.jobparametersgrid.messages.jobParameterNotSelected"), NotifyType.WARNING);
    }

    public void openJobParameterValueWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowJobParameterValue.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowJobParameterValue.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

}
