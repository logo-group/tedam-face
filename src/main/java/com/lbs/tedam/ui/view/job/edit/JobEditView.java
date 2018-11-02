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

package com.lbs.tedam.ui.view.job.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.data.service.JobService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Client;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.model.JobDetail;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamCheckBox;
import com.lbs.tedam.ui.components.basic.TedamDateTimeField;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.NotificationGroupComboBox;
import com.lbs.tedam.ui.components.combobox.TedamEnvironmentComboBox;
import com.lbs.tedam.ui.components.combobox.TedamJobTypeComboBox;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.client.WindowSelectClient;
import com.lbs.tedam.ui.components.window.testset.WindowTestSet;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid.SelectionMode;

@SpringView
public class JobEditView extends AbstractEditView<Job, JobService, JobEditPresenter, JobEditView> {

    private static final long serialVersionUID = 1L;
    private final WindowSelectClient windowClient;
    private final WindowTestSet windowTestSet;
    private TedamTextField name;
    private TedamEnvironmentComboBox jobEnvironment;
	private NotificationGroupComboBox notificationGroup;
    private TedamJobTypeComboBox type;
    private TedamDateTimeField plannedDate;
    private TedamDateTimeField lastExecutedStartDate;
    private TedamDateTimeField lastExecutedEndDate;
    private TedamTextField executionDuration;
    private TedamCheckBox ci;
    private TedamFilterGrid<JobDetail> gridJobDetails;
    private TedamButton addTestSets;
    private TedamButton removeTestSets;
    private TedamButton addClients;
    private TedamButton removeClients;
    private TedamFilterGrid<Client> gridClients;

    @Autowired
	public JobEditView(JobEditPresenter presenter, TedamEnvironmentComboBox jobEnvironment,
			NotificationGroupComboBox notificationGroup, TedamJobTypeComboBox type, WindowSelectClient windowClient,
                       WindowTestSet windowTestSet) {
        super(presenter);
        this.jobEnvironment = jobEnvironment;
        this.type = type;
        this.windowClient = windowClient;
        this.windowTestSet = windowTestSet;
		this.notificationGroup = notificationGroup;
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.jobedit.header");
    }

    @PostConstruct
    private void initView() {

        name = new TedamTextField("view.jobedit.textfield.name", "full", true, true);
        plannedDate = new TedamDateTimeField("view.jobedit.datetimefield.planneddate", "half", false, true);
        lastExecutedStartDate = new TedamDateTimeField("view.jobedit.datetimefield.lastexecutedstartdate", "half", false, false);
        lastExecutedEndDate = new TedamDateTimeField("view.jobedit.datetimefield.lastexecutedenddate", "half", false, false);
        executionDuration = new TedamTextField("view.jobedit.textfield.executionDuration", "half", false, false);

        ci = new TedamCheckBox("view.jobedit.checkbox.ci", null, true, true);

        buildAddAndRemoveButtons();
        buildTestSetsGrid();
        buildClientsGrid();

		addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name, plannedDate, jobEnvironment, type,
				notificationGroup, lastExecutedStartDate, lastExecutedEndDate, executionDuration,
                ci);
        addSection(getLocaleValue("view.jobedit.section.testsets"), 1, null, addTestSets, removeTestSets, gridJobDetails);
        addSection(getLocaleValue("view.jobedit.section.clients"), 2, null, addClients, removeClients, gridClients);

        getPresenter().setView(this);

    }

    private void buildAddAndRemoveButtons() {
        addTestSets = new TedamButton("view.jobedit.button.addtestsets", VaadinIcons.PLUS_CIRCLE);
        addTestSets.addStyleName("half");
        removeTestSets = new TedamButton("view.jobedit.button.removetestsets", VaadinIcons.MINUS_CIRCLE);
        removeTestSets.addStyleName("half");
        addClients = new TedamButton("view.jobedit.button.addclients", VaadinIcons.PLUS_CIRCLE);
        addClients.addStyleName("half");
        removeClients = new TedamButton("view.jobedit.button.removeclients", VaadinIcons.MINUS_CIRCLE);
        removeClients.addStyleName("half");

        addTestSets.addClickListener(e -> {
            try {
                getPresenter().prepareJobDetailWindow();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        removeTestSets.addClickListener(e -> getPresenter().removeJobDetails());
        addClients.addClickListener(e -> {
            try {
                getPresenter().prepareClientWindow();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        removeClients.addClickListener(e -> getPresenter().removeClients());
    }

    protected void organizeTestSetsGrid(AbstractDataProvider<JobDetail> abstractDataProvider) {
        gridJobDetails.setGridDataProvider(abstractDataProvider);
        gridJobDetails.initFilters();
    }

    protected void organizeClientsGrid(AbstractDataProvider<Client> abstractDataProvider) {
        gridClients.setGridDataProvider(abstractDataProvider);
        gridClients.initFilters();
    }

    private TedamGridConfig<JobDetail> buildJobDetailGridConfig() {
        TedamGridConfig<JobDetail> jobDetailsGridConfig = new TedamGridConfig<JobDetail>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.JOB_DETAILS_COLUMNS;
            }

            @Override
            public Class<JobDetail> getBeanType() {
                return JobDetail.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

        };
        return jobDetailsGridConfig;
    }

    private TedamGridConfig<Client> buildClientGridConfig() {
        TedamGridConfig<Client> clientsGridConfig = new TedamGridConfig<Client>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.CLIENT_COLUMNS;
            }

            @Override
            public Class<Client> getBeanType() {
                return Client.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

        };
        return clientsGridConfig;
    }

    protected void buildTestSetsGrid() {
        gridJobDetails = new TedamFilterGrid<JobDetail>(buildJobDetailGridConfig(), SelectionMode.MULTI);
    }

    protected void buildClientsGrid() {
        gridClients = new TedamFilterGrid<Client>(buildClientGridConfig(), SelectionMode.MULTI);
    }

    public TedamFilterGrid<JobDetail> getGridJobDetails() {
        return gridJobDetails;
    }

    public TedamFilterGrid<Client> getGridClients() {
        return gridClients;
    }

    public void openClientSelectWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowClient.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    public void openTestSetSelectWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowTestSet.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    public void showGridRowNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.jobedit.messages.showGridRowNotSelected"), NotifyType.ERROR);
    }

    public void showJobDetailsEmpty() {
        TedamNotification.showNotification(getLocaleValue("view.jobedit.messages.showJobDetailsEmpty"), NotifyType.ERROR);
    }

    @Override
    public void bindFormFields(BeanValidationBinder<Job> binder) {
        super.bindFormFields(binder);
        binder.forField(jobEnvironment).asRequired().bind("jobEnvironment");
		binder.forField(notificationGroup).bind("notificationGroup");
    }
}
