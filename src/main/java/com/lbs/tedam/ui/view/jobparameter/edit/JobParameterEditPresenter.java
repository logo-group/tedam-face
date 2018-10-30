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

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.JobParameterService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.model.JobParameterValue;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.jobparameter.JobParameterGridView;
import com.lbs.tedam.ui.view.jobparameters.jobparametervalue.JobParameterValueDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

;

@SpringComponent
@ViewScope
public class JobParameterEditPresenter extends AbstractEditPresenter<JobParameter, JobParameterService, JobParameterEditPresenter, JobParameterEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final JobParameterValueDataProvider jobParameterValueDataProvider;

    @Autowired
    public JobParameterEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, JobParameterService jobParameterService, TedamUserService userService,
                                     BeanFactory beanFactory, JobParameterValueDataProvider jobParameterValueDataProvider, PropertyService propertyService) {
        super(viewEventBus, navigationManager, jobParameterService, JobParameter.class, beanFactory, userService, propertyService);
        this.jobParameterValueDataProvider = jobParameterValueDataProvider;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        JobParameter jobParameter;
        Integer id = (Integer) parameters.get(UIParameter.ID);
        ViewMode mode = (ViewMode) parameters.get(UIParameter.MODE);
        if (id == 0) {
            jobParameter = new JobParameter();
            jobParameter.setProject(SecurityUtils.getCurrentUser(getUserService()).getProject());
        } else {
            jobParameter = getService().getById(id);
            if (jobParameter == null) {
                getView().showNotFound();
                return;
            }
        }
        refreshView(jobParameter, mode);
        jobParameterValueDataProvider.setJobParameter(jobParameter);
        getView().organizeGrid(jobParameterValueDataProvider);
        organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
        setGridEditorAttributes(getView().getGridJobParameterValues(), mode != ViewMode.VIEW);
    }

    public void addRow() throws LocalizedException {
        JobParameterValue jobParameterValue = new JobParameterValue();
        jobParameterValue.setDateCreated(LocalDateTime.now());
        jobParameterValue.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getTedamUser().getUserName());
        jobParameterValueDataProvider.getListDataProvider().getItems().add(jobParameterValue);
        jobParameterValueDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void removeRow(Set<JobParameterValue> jobParameterValueSet) {
        for (JobParameterValue jobParameterValue : jobParameterValueSet) {
            jobParameterValueDataProvider.removeItem(jobParameterValue);
            jobParameterValueDataProvider.getListDataProvider().refreshAll();
        }
        setHasChanges(true);
    }

    @Override
    protected JobParameter save(JobParameter item) throws LocalizedException {
        if (item.getJobParameterValues().isEmpty()) {
            getView().showJobParameterValuesEmpty();
            return null;
        }
        return super.save(item);
    }

    @Override
    protected Class<? extends View> getGridView() {
        return JobParameterGridView.class;
    }

}
