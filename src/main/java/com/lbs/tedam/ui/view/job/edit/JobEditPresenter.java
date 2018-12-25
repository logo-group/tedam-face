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
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.lbs.tedam.app.TedamRestTemplate;
import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.JobService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Client;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.model.JobDetail;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TestSet;
import com.lbs.tedam.ui.TedamFaceEvents.ClientSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TestSetEvent;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.job.JobGridView;
import com.lbs.tedam.ui.view.jobmanager.TedamManagerPresenter;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.EnumsV2.CommandStatus;
import com.lbs.tedam.util.EnumsV2.JobStatus;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class JobEditPresenter extends AbstractEditPresenter<Job, JobService, JobEditPresenter, JobEditView> {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final JobClientDataProvider clientDataProvider;
	private final JobDetailDataProvider testSetDataProvider;
	private TedamRestTemplate restTemplate = new TedamRestTemplate();

	@Autowired
	public JobEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, JobService jobService,
			TedamUserService userService, BeanFactory beanFactory, JobClientDataProvider clientDataProvider,
			JobDetailDataProvider testSetDataProvider, PropertyService propertyService) {
		super(viewEventBus, navigationManager, jobService, Job.class, beanFactory, userService, propertyService);
		this.clientDataProvider = clientDataProvider;
		this.testSetDataProvider = testSetDataProvider;
	}

	@Override
	public void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		Job job;
		Integer id = (Integer) windowParameters.get(UIParameter.ID);
		ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
		if (id == 0) {
			job = new Job();
			job.setProject(SecurityUtils.getCurrentUser(getUserService()).getProject());
		} else {
			job = getService().getById(id);
			if (job == null) {
				getView().showNotFound();
				return;
			}
			isAuthorized(job);
		}
		refreshView(job, mode);

		clientDataProvider.provideJobClients(job);
		testSetDataProvider.provideTestSets(job);
		getView().organizeClientsGrid(clientDataProvider);
		getView().organizeTestSetsGrid(testSetDataProvider);
		getTitleForHeader();
		organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
		setGridEditorAttributes(getView().getGridJobDetails(), mode != ViewMode.VIEW);
	}

	@PostConstruct
	public void init() {
		subscribeToEventBus();
	}

	@Override
	protected Class<? extends View> getGridView() {
		return JobGridView.class;
	}

	public void prepareJobDetailWindow() throws LocalizedException {
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		List<TestSet> testSetList = getView().getGridJobDetails().getGridDataProvider().getListDataProvider().getItems()
				.stream().map(jobDetail -> jobDetail.getTestSet()).collect(Collectors.toList());
		windowParameters.put(UIParameter.SELECTED_LIST, testSetList);
		getView().openTestSetSelectWindow(windowParameters);
	}

	public void prepareClientWindow() throws LocalizedException {
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		windowParameters.put(UIParameter.SELECTED_LIST,
				new ArrayList<>(getView().getGridClients().getGridDataProvider().getListDataProvider().getItems()));
		getView().openClientSelectWindow(windowParameters);
	}

	@EventBusListenerMethod
	public void clientSelectedEvent(ClientSelectEvent event) {
		List<Client> clientList = event.getClientList();
		for (Client client : clientList) {
			getView().getGridClients().getGridDataProvider().getListDataProvider().getItems().add(client);
		}
		getView().getGridClients().refreshAll();
		getView().getGridClients().scrollToEnd();
		setHasChanges(true);
	}

	@EventBusListenerMethod
	public void testSetSelectedEvent(TestSetEvent event) {
		List<TestSet> testSetList = event.getTestSetList();
		for (TestSet testSet : testSetList) {
			JobDetail jobDetail = new JobDetail(null, testSet);
			getView().getGridJobDetails().getGridDataProvider().getListDataProvider().getItems().add(jobDetail);
		}
		getView().getGridJobDetails().refreshAll();
		getView().getGridJobDetails().scrollToEnd();
		setHasChanges(true);
	}

	protected void removeJobDetails() {
		if (getView().getGridJobDetails().getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		for (JobDetail jobDetail : getView().getGridJobDetails().getSelectedItems()) {
			getView().getGridJobDetails().getGridDataProvider().removeItem(jobDetail);
		}
		getView().getGridJobDetails().refreshAll();
		setHasChanges(true);
	}

	protected void removeClients() {
		if (getView().getGridClients().getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		Job job = getItem();
		for (Client client : getView().getGridClients().getSelectedItems()) {
			job.getClients().remove(client);
			getView().getGridClients().getGridDataProvider().removeItem(client);
		}
		getView().getGridClients().refreshAll();
		setHasChanges(true);
	}

	@Override
	protected Job save(Job item) throws LocalizedException {
		if (item.getJobDetails().isEmpty()) {
			getView().showJobDetailsEmpty();
			return null;
		} else if (item.getPlannedDate() == null && item.isRunEveryDay()) {
			getView().showPlannedDateEmpty();
			return null;
		}
		setJobDetailPosition(item);
		Job saved = super.save(item);
		startPlannedJob(item);
		return saved;
	}

	private void startPlannedJob(Job job) throws LocalizedException {
		switch (job.getStatus()) {
		case COMPLETED:
		case NOT_STARTED:
		case STOPPED:
			if (job.getPlannedDate() != null) {
				job = getService().saveJobAndJobDetailsStatus(job, JobStatus.PLANNED, CommandStatus.NOT_STARTED,
						SecurityUtils.getCurrentUser(getUserService()).getTedamUser());
				String responseString = restTemplate.postForObject(
						getPropertyService().getPropertyByNameAndParameter(Constants.PROPERTY_CONFIG,
								Constants.PROPERTY_JOBRUNNER_REST_URL).getValue() + TedamManagerPresenter.START_JOB,
						job.getId(), String.class);
				getView().showJobMessage(job, responseString);
			}
			break;
		default:
			break;
		}
	}

	private void setJobDetailPosition(Job job) {
		for (ListIterator<JobDetail> listIterator = job.getJobDetails().listIterator(); listIterator.hasNext();) {
			JobDetail jobDetail = listIterator.next();
			jobDetail.setPosition(listIterator.nextIndex());
		}
	}

	@Override
	protected Project getProjectByEntity(Job entity) {
		return entity.getProject();
	}

	@Override
	protected void getTitleForHeader() {
		if (getItem().getName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getName());
		}
	}

}
