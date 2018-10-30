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

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.EnvironmentService;
import com.lbs.tedam.data.service.JobParameterService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.model.JobParameterValue;
import com.lbs.tedam.ui.TedamFaceEvents.JobParameterValueSelectEvent;
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
import java.util.Map;

@SpringComponent
@ViewScope
public class JobParameterGridPresenter extends AbstractGridPresenter<JobParameter, JobParameterService, JobParameterGridPresenter, JobParameterGridView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private final EnvironmentService environmentService;

    @Autowired
    public JobParameterGridPresenter(JobParametersDataProvider jobParametersDataProvider, NavigationManager navigationManager, JobParameterService service, BeanFactory beanFactory,
                                     ViewEventBus viewEventBus, PropertyService propertyService, EnvironmentService environmentService, TedamUserService tedamUserService) {
        super(navigationManager, service, jobParametersDataProvider, beanFactory, viewEventBus, propertyService, tedamUserService);
        this.environmentService = environmentService;
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    @EventBusListenerMethod
    public void jobParameterValueSelectedEvent(JobParameterValueSelectEvent event) throws LocalizedException {
        JobParameterValue jobParameterValue = event.getJobParameterValue();
        environmentService.uploadJobParameterValueToAllEnvironments(jobParameterValue, SecurityUtils.getUserSessionProject(),
                SecurityUtils.getCurrentUser(getUserService()).getUsername());
    }

    public void prepareJobParameterValueWindow() throws LocalizedException {
        if (getView().getGrid().getSelectedItems().size() > 1) {
            getView().showSelectOneJobParameter();
        } else if (getView().getGrid().getSelectedItems().size() == 0) {
            getView().showJobParameterNotSelected();
        } else {
            JobParameter selected = getView().getGrid().getSelectedItems().iterator().next();
            Map<UIParameter, Object> UIParameterMap = TedamStatic.getUIParameterMap();
            UIParameterMap.put(UIParameter.JOB_PARAMETER, selected);
            getView().openJobParameterValueWindow(UIParameterMap);
        }
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) {
    }

}
