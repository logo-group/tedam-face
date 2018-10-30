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

package com.lbs.tedam.ui.view.job;

import com.lbs.tedam.data.service.JobService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.environment.WindowSelectEnvironment;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractGridView;
import com.lbs.tedam.ui.view.job.edit.JobEditView;
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
public class JobGridView extends AbstractGridView<Job, JobService, JobGridPresenter, JobGridView> {

    private static final long serialVersionUID = 1L;

    private WindowSelectEnvironment windowSelectEnvironment;

    private TedamGridConfig<Job> config = new TedamGridConfig<Job>() {

        @Override
        public List<GridColumn> getColumnList() {
            return GridColumns.GridColumn.JOB_COLUMNS;
        }

        @Override
        public Class<Job> getBeanType() {
            return Job.class;
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
    public JobGridView(JobGridPresenter presenter, WindowSelectEnvironment windowSelectEnvironment) {
        super(presenter, SelectionMode.MULTI);
        this.windowSelectEnvironment = windowSelectEnvironment;
    }

    @PostConstruct
    private void init() {
        getPresenter().setView(this);
        setHeader(getLocaleValue("view.jobgrid.header"));
        getTopBarLayout().addComponents(buildAddActiveJobs(), buildRemoveActiveJobs(), buildDefineEnvironment());
    }

    @Override
    public void buildGridColumnDescription() {
        getGrid().getColumn(GridColumn.JOB_NAME.getColumnName()).setDescriptionGenerator(Job::getName);
    }

    private Component buildAddActiveJobs() {
        TedamButton addActiveJobs = new TedamButton("view.jobgrid.addActiveJobs", VaadinIcons.INSERT);
        addActiveJobs.setWidthUndefined();
        addActiveJobs.setCaption("");
        addActiveJobs.addClickListener(e -> {
            if (getGrid().getSelectedItems().isEmpty()) {
                showGridRowNotSelected();
                return;
            }
            try {
                getPresenter().addActiveJobs(new ArrayList<>(getGrid().getSelectedItems()));
                getGrid().deselectAll();
            } catch (LocalizedException e1) {
                logError(e1);
            }

        });
        return addActiveJobs;
    }

    private Component buildRemoveActiveJobs() {
        TedamButton removeActiveJobs = new TedamButton("view.jobgrid.removeActiveJobs", VaadinIcons.EXTERNAL_LINK);
        removeActiveJobs.setWidthUndefined();
        removeActiveJobs.setCaption("");
        removeActiveJobs.addClickListener(e -> {
            if (getGrid().getSelectedItems().isEmpty()) {
                showGridRowNotSelected();
                return;
            }
            try {
                getPresenter().removeActiveJobs(new ArrayList<>(getGrid().getSelectedItems()));
                getGrid().deselectAll();
            } catch (LocalizedException e1) {
                logError(e1);
            }

        });
        return removeActiveJobs;
    }

    private Component buildDefineEnvironment() {
        TedamButton btnDefineEnvironment = new TedamButton("view.jobgrid.defineEnvironment");
        btnDefineEnvironment.addStyleName("primary");
        btnDefineEnvironment.setWidthUndefined();
        btnDefineEnvironment.addClickListener(e -> {
            try {
                getPresenter().prepareWindowSelectEnvironment();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        return btnDefineEnvironment;
    }

    protected void showGridRowNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.jobgrid.messages.defineEnvironmentEmptyJob"), NotifyType.ERROR);
    }

    protected void showActivated(boolean oneActivated, List<String> alreadyActiveNames) {
        String message = "";
        if (oneActivated) {
            String addedMessage = getLocaleValue("view.jobgrid.messages.showAddedToActives");
            message += addedMessage + "\n";
        }
        if (alreadyActiveNames.size() > 0) {
            String alreadyActivatedMessage = getLocaleValue("view.jobgrid.messages.showAlreadyAddedToActives");
            message += alreadyActivatedMessage + "\n";
            for (String s : alreadyActiveNames) {
                message += s + "\n";
            }
        }
        if (message.length() > 0)
            TedamNotification.showTrayNotification(message, NotifyType.WARNING);
    }

    @Override
    protected TedamGridConfig<Job> getTedamGridConfig() {
        return config;
    }

    @Override
    protected Class<? extends View> getEditView() {
        return JobEditView.class;
    }

    protected void showDeActivated(boolean oneDeActivated, List<String> alreadyDeActiveNames) {
        String message = "";
        if (oneDeActivated) {
            String addedMessage = getLocaleValue("view.jobgrid.messages.showRemovedToActives");
            message += addedMessage + "\n";
        }
        if (alreadyDeActiveNames.size() > 0) {
            String alreadyActivatedMessage = getLocaleValue("view.jobgrid.messages.showAlreadyRemovedToActives");
            message += alreadyActivatedMessage + "\n";
            for (String s : alreadyDeActiveNames) {
                message += s + "\n";
            }
        }
        if (message.length() > 0)
            TedamNotification.showNotification(message, NotifyType.WARNING);
    }

    public void openWindowSelectEnvironment(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowSelectEnvironment.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowSelectEnvironment.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    public void showEnvironmentsUpdated() {
        TedamNotification.showNotification(getLocaleValue("view.jobgrid.messages.defineEnvironmentSuccess"), NotifyType.SUCCESS);
    }
}
