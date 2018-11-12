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

package com.lbs.tedam.ui.view.environment.edit;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.EnvironmentService;
import com.lbs.tedam.data.service.JobParameterService;
import com.lbs.tedam.data.service.ProjectService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Environment;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.model.JobParameterValue;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.ui.components.combobox.TedamJobParameterValueComboBox;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.environment.EnvironmentGridView;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class EnvironmentEditPresenter extends AbstractEditPresenter<Environment, EnvironmentService, EnvironmentEditPresenter, EnvironmentEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final JobParameterService jobParameterService;

    @Autowired
    public EnvironmentEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, EnvironmentService environmentService, TedamUserService userService,
                                    JobParameterService jobParameterService, ProjectService projectService, BeanFactory beanFactory, PropertyService propertyService) {
        super(viewEventBus, navigationManager, environmentService, Environment.class, beanFactory, userService, propertyService);
        this.jobParameterService = jobParameterService;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        Environment environment;
        if ((Integer) parameters.get(UIParameter.ID) == 0) {
            environment = new Environment();
            environment.setProject(SecurityUtils.getUserSessionProject());
        } else {
            environment = getService().getById((Integer) parameters.get(UIParameter.ID));
            if (environment == null) {
                getView().showNotFound();
                return;
            }
            isAuthorized(environment);
        }
        refreshView(environment, (ViewMode) parameters.get(UIParameter.MODE));
		getTitleForHeader();
    }

    protected List<JobParameter> getActiveJobParameters() throws LocalizedException {
        return jobParameterService.getJobParameterListByProject(SecurityUtils.getCurrentUser(getUserService()).getProject());
    }

    @Override
    protected Class<? extends View> getGridView() {
        return EnvironmentGridView.class;
    }

    @Override
    protected void setItem(Environment environment) throws LocalizedException {
        super.setItem(environment);
        getView().buildTedamJobParameterValueComboBoxContainer(environment);
    }

    private void addJobParameterValueEvent(ValueChangeEvent<JobParameterValue> event, Environment environment) {
        environment.getJobParameterValues().remove(event.getOldValue());
        environment.getJobParameterValues().add(event.getValue());
        setHasChanges(true);
    }

    private JobParameterValue getSelectedJobParameterValue(Environment environment, JobParameter jobParameter) {
        for (JobParameterValue jobParameterValue : environment.getJobParameterValues()) {
            if (jobParameter.getId().equals(jobParameterValue.getJobParameterId())) {
                return jobParameterValue;
            }
        }
        return null;
    }

    protected TedamJobParameterValueComboBox fillTedamJobParameterValueComboBox(TedamJobParameterValueComboBox tedamJobParameterValueComboBox, Environment environment,
                                                                                JobParameter jobParameter) {
        tedamJobParameterValueComboBox.setId("cb" + jobParameter.getName());
        tedamJobParameterValueComboBox.setCaption(jobParameter.getName());
        tedamJobParameterValueComboBox.setData(jobParameter);
        tedamJobParameterValueComboBox.setItems(jobParameter.getJobParameterValues());
        tedamJobParameterValueComboBox.setValue(getSelectedJobParameterValue(environment, jobParameter));
        tedamJobParameterValueComboBox.setReadOnly(getView().getViewMode() == ViewMode.VIEW);
        tedamJobParameterValueComboBox.addValueChangeListener(e -> addJobParameterValueEvent(e, environment));
        return tedamJobParameterValueComboBox;
    }

    @Override
    protected Environment save(Environment item) throws LocalizedException {
        if (isParametersEmpty(item)) {
            getView().showParametersEmpty();
            return null;
        }
        if (existEnvironment(item)) {
            getView().showSameEnvironmentIsExist();
            return null;
        }
        return super.save(item);
    }

    private boolean isParametersEmpty(Environment environment) {
        int count = getView().getTedamJobParameterValueComboBoxContainer().getComponentCount();
        if (environment.getJobParameterValues().size() != count) {
            return true;
        }
        return false;
    }

    private boolean existEnvironment(Environment newEnvironment) throws LocalizedException {
        List<Environment> environmentList = getService().getEnvironmentListByProject(SecurityUtils.getUserSessionProject());
        for (Environment environment : environmentList) {
            if (!newEnvironment.getId().equals(0) && newEnvironment.getId().equals(environment.getId())) {
                continue;
            }
            if (environment.getJobParameterValues().containsAll(newEnvironment.getJobParameterValues())
                    && newEnvironment.getJobParameterValues().containsAll(environment.getJobParameterValues())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Project getProjectByEntity(Environment entity) {
        return entity.getProject();
    }

	@Override
	protected void getTitleForHeader() {
		if (getItem().getName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getName());
		}
	}

}
