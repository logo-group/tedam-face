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

/**
 *
 */
package com.lbs.tedam.ui.components.window.job;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.ui.TedamFaceEvents.JobSelectEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@PrototypeScope
public class WindowSelectJob extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private WindowSelectJobDataProvider gridDataProviderJob;
    private TedamFilterGrid<Job> gridJob;
    private List<Job> jobList;

    @Autowired
    public WindowSelectJob(WindowSelectJobDataProvider gridDataProviderJob, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.gridDataProviderJob = gridDataProviderJob;
    }

    @Override
    protected Component buildContent() {
        TedamGridConfig<Job> gridConfigFiles = new TedamGridConfig<Job>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.JOB_COLUMNS;
            }

            @Override
            public Class<Job> getBeanType() {
                return Job.class;
            }
        };
        gridJob = new TedamFilterGrid<>(gridConfigFiles, gridDataProviderJob, SelectionMode.MULTI);
        removeSelectedJobs();
        return gridJob;
    }

    private void removeSelectedJobs() {
        for (Job job : jobList) {
            gridDataProviderJob.getListDataProvider().getItems().remove(job);
        }
        gridDataProviderJob.getListDataProvider().refreshAll();
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new JobSelectEvent(new ArrayList<>(gridJob.getSelectedItems())));
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.job.header");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        jobList = (List<Job>) parameters.get(UIParameter.SELECTED_LIST);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (gridJob.getSelectedItems().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.job"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

}
