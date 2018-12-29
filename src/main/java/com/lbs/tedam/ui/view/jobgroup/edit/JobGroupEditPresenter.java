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

package com.lbs.tedam.ui.view.jobgroup.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.JobGroupService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.ui.TedamFaceEvents.JobSelectEvent;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.jobgroup.JobGroupGridView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class JobGroupEditPresenter extends AbstractEditPresenter<JobGroup, JobGroupService, JobGroupEditPresenter, JobGroupEditView> {

	private static final long serialVersionUID = 1L;

	private final JobGroupJobDataProvider jobDataProvider;

	@Autowired
	public JobGroupEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, JobGroupService jobGroupService, TedamUserService userService,
			BeanFactory beanFactory, JobGroupJobDataProvider jobDataProvider, PropertyService propertyService) {
		super(viewEventBus, navigationManager, jobGroupService, JobGroup.class, beanFactory, userService, propertyService);
		this.jobDataProvider = jobDataProvider;
	}

	@PostConstruct
	public void init() {
		subscribeToEventBus();
	}

	@Override
	public void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		JobGroup jobGroup;
		Integer id = (Integer) windowParameters.get(UIParameter.ID);
		ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
		if (id == 0) {
			jobGroup = new JobGroup();
			jobGroup.setProject(SecurityUtils.getCurrentUser(getUserService()).getProject());
		} else {
			jobGroup = getService().getById(id);
			if (jobGroup == null) {
				getView().showNotFound();
				return;
			}
			isAuthorized(jobGroup);
		}
		refreshView(jobGroup, mode);

		jobDataProvider.provideJobGroupJobs(jobGroup);
		getView().organizeJobsGrid(jobDataProvider);
		getTitleForHeader();
		organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
		setGridEditorAttributes(getView().getGridJobs(), mode != ViewMode.VIEW);
	}

	@EventBusListenerMethod
	public void jobSelectedEvent(JobSelectEvent event) {
		List<Job> jobList = event.getJobList();
		for (Job job : jobList) {
			getView().getGridJobs().getGridDataProvider().getListDataProvider().getItems().add(job);
		}
		getView().getGridJobs().refreshAll();
		getView().getGridJobs().scrollToEnd();
		setHasChanges(true);
	}

	public void prepareJobWindow() throws LocalizedException {
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		windowParameters.put(UIParameter.SELECTED_LIST, new ArrayList<>(getView().getGridJobs().getGridDataProvider().getListDataProvider().getItems()));
		getView().openJobSelectWindow(windowParameters);
	}

	protected void removeJobs() {
		if (getView().getGridJobs().getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		JobGroup jobGroup = getItem();
		for (Job job : getView().getGridJobs().getSelectedItems()) {
			jobGroup.getJobs().remove(job);
			getView().getGridJobs().getGridDataProvider().removeItem(job);
		}
		getView().getGridJobs().refreshAll();
		setHasChanges(true);
	}

	@Override
	protected Class<? extends View> getGridView() {
		return JobGroupGridView.class;
	}

	@Override
	protected Project getProjectByEntity(JobGroup entity) {
		return entity.getProject();
	}

	@Override
	protected void getTitleForHeader() {
		if (getItem().getName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getName());
		}
	}
}
