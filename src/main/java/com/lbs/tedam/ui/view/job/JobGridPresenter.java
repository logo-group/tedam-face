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
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Environment;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.ui.TedamFaceEvents.EnvironmentSelectEvent;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractGridPresenter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringComponent
@ViewScope
public class JobGridPresenter extends AbstractGridPresenter<Job, JobService, JobGridPresenter, JobGridView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    public JobGridPresenter(JobDataProvider jobDataProvider, NavigationManager navigationManager, JobService service, BeanFactory beanFactory, ViewEventBus viewEventBus,
                            PropertyService propertyService, TedamUserService userService) {
        super(navigationManager, service, jobDataProvider, beanFactory, viewEventBus, propertyService, userService);
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    public void addActiveJobs(List<Job> jobList) throws LocalizedException {
        boolean oneActivated = false;
        List<String> alreadyActiveNames = new ArrayList<>();

        for (Job job : jobList) {
            if (job.isActive()) {
                alreadyActiveNames.add(job.getName());
            } else if (!oneActivated) {
                oneActivated = true;
            }
            job.setActive(true);
        }

        getService().save(jobList);
        getView().showActivated(oneActivated, alreadyActiveNames);
    }

    public void removeActiveJobs(List<Job> jobList) throws LocalizedException {
        boolean oneDeActivated = false;
        List<String> alreadyDeActiveNames = new ArrayList<>();

        for (Job job : jobList) {
            if (!job.isActive()) {
                alreadyDeActiveNames.add(job.getName());
            } else if (!oneDeActivated) {
                oneDeActivated = true;
            }
            job.setActive(false);
        }

        getService().save(jobList);
        getView().showDeActivated(oneDeActivated, alreadyDeActiveNames);
    }

    public void prepareWindowSelectEnvironment() throws LocalizedException {
        Set<Job> selectedItems = getView().getGrid().getSelectedItems();
        if (selectedItems.isEmpty()) {
            getView().showGridRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        getView().openWindowSelectEnvironment(windowParameters);
    }

    @EventBusListenerMethod
    public void environmentSelectedEvent(EnvironmentSelectEvent event) throws LocalizedException {
        Environment environment = event.getEnvironment();
        for (Job job : getView().getGrid().getSelectedItems()) {
            job.setJobEnvironment(environment);
            getService().save(job);
        }
        getView().showEnvironmentsUpdated();
        getView().getGrid().deselectAll();
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) {
    }

}
