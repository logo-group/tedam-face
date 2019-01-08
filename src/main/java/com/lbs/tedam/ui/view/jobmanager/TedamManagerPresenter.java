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

package com.lbs.tedam.ui.view.jobmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.lbs.tedam.app.TedamRestTemplate;
import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.JobDetailService;
import com.lbs.tedam.data.service.JobGroupService;
import com.lbs.tedam.data.service.JobService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.data.service.TestSetService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.Client;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.model.JobDetail;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.model.DTO.ClientDTO;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.TedamFaceEvents.JobEditedEvent;
import com.lbs.tedam.ui.components.TedamJobGroupPanel;
import com.lbs.tedam.ui.components.TedamJobGroupPanel.JobGroupPanelButtonClickListener;
import com.lbs.tedam.ui.components.TedamJobPanel;
import com.lbs.tedam.ui.components.TedamJobPanel.JobPanelButtonClickListener;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.EnumsV2.ClientStatus;
import com.lbs.tedam.util.EnumsV2.CommandStatus;
import com.lbs.tedam.util.EnumsV2.JobStatus;
import com.lbs.tedam.util.EnumsV2.JobType;
import com.lbs.tedam.util.HasLogger;
import com.lbs.tedam.util.TedamJsonFactory;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;

@SpringComponent
@ViewScope
public class TedamManagerPresenter implements HasLogger, Serializable, TedamLocalizerWrapper {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public static final String START_JOB = "startJob";
	private final String STOP_JOB = "stopJob";
	public static final String START_JOB_GROUP = "startJobGroup";
	private final String STOP_JOB_GROUP = "stopJobGroup";
	private final String GET_CLIENT_MAP = "getClientMap";

	private final BeanFactory beanFactory;
	private final TedamManagerJobDataProvider tedamManagerJobDataProvider;
	private final TedamManagerJobGroupDataProvider tedamManagerJobGroupDataProvider;
	private final JobService jobService;
	private final JobGroupService jobGroupService;
	private final JobDetailService jobDetailService;
	private final TestSetService testSetService;
	private final PropertyService propertyService;
	private final TedamUserService userService;
	private final ViewEventBus viewEventBus;

	private TedamManagerView tedamManagerView;
	private List<JobStatus> jobStatusList = new ArrayList<>();
	private List<JobType> jobTypeList = new ArrayList<>();
	private boolean areOnlyOwnJobs = false;
	private TedamRestTemplate restTemplate = new TedamRestTemplate();

	@Autowired
	public TedamManagerPresenter(PropertyService propertyService, JobService jobService, ViewEventBus viewEventBus,
			BeanFactory beanFactory, TedamManagerJobDataProvider tedamManagerJobDataProvider,
			TedamUserService userService, JobDetailService jobDetailService, TestSetService testSetService,
			TedamManagerJobGroupDataProvider tedamManagerJobGroupDataProvider, JobGroupService jobGroupService) {
		this.propertyService = propertyService;
		this.jobService = jobService;
		this.viewEventBus = viewEventBus;
		this.beanFactory = beanFactory;
		this.tedamManagerJobDataProvider = tedamManagerJobDataProvider;
		this.userService = userService;
		this.jobDetailService = jobDetailService;
		this.testSetService = testSetService;
		this.tedamManagerJobGroupDataProvider = tedamManagerJobGroupDataProvider;
		this.jobGroupService = jobGroupService;
	}

	@PostConstruct
	public void init() {
		viewEventBus.subscribe(this);
	}

	public void build() {
		removeAllTedamJobPanel();

		List<Job> runnableJobList = (List<Job>) tedamManagerJobDataProvider.getListDataProvider().getItems();
		List<JobGroup> runnableJobGroupList = (List<JobGroup>) tedamManagerJobGroupDataProvider.getListDataProvider()
				.getItems();

		sortJobsByExecutionDate(runnableJobList);
		sortJobGroupsByExecutionDate(runnableJobGroupList);

		String loggedInUser = getLoggedInUser();

		runnableJobList.forEach(job -> {
			if (jobStatusList.isEmpty() || jobStatusList.contains(job.getStatus())) {
				if (jobTypeList.isEmpty() || jobTypeList.contains(job.getType())) {
					if (!areOnlyOwnJobs || job.getCreatedUser().equals(loggedInUser)) {
						TedamJobPanel panel = buildPanel(job);
						tedamManagerView.addComponent(panel);
					}
				}
			}
		});

		runnableJobGroupList.forEach(jobGroup -> {
			if (jobStatusList.isEmpty() || jobStatusList.contains(jobGroup.getStatus())) {
				if (!areOnlyOwnJobs || jobGroup.getCreatedUser().equals(loggedInUser)) {
					TedamJobGroupPanel panel = buildJobGroupPanel(jobGroup);
					tedamManagerView.addComponent(panel);
				}
			}
		});
	}

	private TedamJobGroupPanel buildJobGroupPanel(JobGroup jobGroup) {
		TedamJobGroupPanel panel = beanFactory.getBean(TedamJobGroupPanel.class);
		panel.setClickListener(new JobGroupPanelButtonClickListener() {

			@Override
			public void stopButtonClickOperations(JobGroup jobGroup) {
				try {
					doStopJobGroupButtonClickOperations(jobGroup);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

			@Override
			public void startButtonClickOperations(JobGroup jobGroup) {
				try {
					doStartJobGroupButtonClickOperations(jobGroup);
				} catch (LocalizedException e) {
					tedamManagerView.showExceptionMessage(e);
				}
			}

			@Override
			public void unfollowButtonClickOperations(JobGroup jobGroup) {
				try {
					doUnfollowJobGroupButtonClickOperations(jobGroup);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

			@Override
			public void resetButtonClickOperations(JobGroup jobGroup) {
				try {
					doResetJobGroupButtonClickOperations(jobGroup);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}
		});
		panel.setJobGroup(jobGroup);
		return panel;
	}

	private void doStopJobGroupButtonClickOperations(JobGroup jobGroup) throws LocalizedException {
		String responseString = restTemplate
				.postForObject(
						propertyService.getPropertyByNameAndParameter(Constants.PROPERTY_CONFIG,
								Constants.PROPERTY_JOBRUNNER_REST_URL).getValue() + STOP_JOB_GROUP,
						jobGroup.getId(), String.class);
		tedamManagerView.showPanelMessage(jobGroup.getId(), jobGroup.getName(), jobGroup.getStatus(), responseString);
		rebuildTedamJobGroupPanel(jobGroup);
	}

	private void doStartJobGroupButtonClickOperations(JobGroup jobGroup) throws LocalizedException {
		String responseString = restTemplate
				.postForObject(
						propertyService.getPropertyByNameAndParameter(Constants.PROPERTY_CONFIG,
								Constants.PROPERTY_JOBRUNNER_REST_URL).getValue() + START_JOB_GROUP,
						jobGroup.getId(), String.class);
		tedamManagerView.showPanelMessage(jobGroup.getId(), jobGroup.getName(), jobGroup.getStatus(), responseString);
		rebuildTedamJobGroupPanel(jobGroup);
	}

	private void doUnfollowJobGroupButtonClickOperations(JobGroup jobGroup) throws LocalizedException {
		jobGroup.setActive(false);
		jobGroup = jobGroupService.save(jobGroup);
		TedamJobGroupPanel tedamJobGroupPanel = getTedamJobGroupPanel(jobGroup);
		tedamManagerView.removeComponent(tedamJobGroupPanel);
	}

	private void doResetJobGroupButtonClickOperations(JobGroup jobGroup) throws LocalizedException {
		TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

			@Override
			public void onConfirm() {
				Integer jobId = jobGroup.getId();
				try {
					jobService.resetJob(jobId);
					jobDetailService.resetJobDetail(jobId);
					testSetService.resetTestSet(jobId);
					jobGroup.setStatus(JobStatus.NOT_STARTED);
					rebuildTedamJobGroupPanel(jobGroup);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

			@Override
			public void onCancel() {
			}
		}, getLocaleValue("confirm.message.resetJob"), getLocaleValue("general.button.ok"),
				getLocaleValue("general.button.cancel"));
	}

	private TedamJobPanel buildPanel(Job job) {
		TedamJobPanel panel = beanFactory.getBean(TedamJobPanel.class);
		panel.setClickListener(new JobPanelButtonClickListener() {

			@Override
			public void stopButtonClickOperations(Job job) {
				try {
					doStopButtonClickOperations(job);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

			@Override
			public void startButtonClickOperations(Job job) {
				try {
					doStartButtonClickOperations(job);
				} catch (LocalizedException e) {
					tedamManagerView.showExceptionMessage(e);
				}
			}

			@Override
			public void unfollowButtonClickOperations(Job job) {
				try {
					doUnfollowButtonClickOperations(job);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

			@Override
			public void resetButtonClickOperations(Job job) {
				try {
					doResetButtonClickOperations(job);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

		});
		panel.setJob(job);
		return panel;
	}

	private void doStopButtonClickOperations(Job job) throws LocalizedException {
		job.setStatus(JobStatus.WAITING_STOP);
		jobService.updateJobStatusAndExecutedDateByJobId(job.getId(), JobStatus.WAITING_STOP,
				job.getLastExecutedStartDate(), null);
		String responseString = restTemplate
				.postForObject(
						propertyService.getPropertyByNameAndParameter(Constants.PROPERTY_CONFIG,
								Constants.PROPERTY_JOBRUNNER_REST_URL).getValue() + STOP_JOB,
						job.getId(), String.class);
		tedamManagerView.showPanelMessage(job.getId(), job.getName(), job.getStatus(), responseString);
		rebuildTedamJobPanel(job);
	}

	private void doStartButtonClickOperations(Job job) throws LocalizedException {
		jobService.checkJobBeforeRun(job);
		job = jobService.saveJobAndJobDetailsStatus(job, JobStatus.QUEUED, CommandStatus.NOT_STARTED,
				SecurityUtils.getCurrentUser(userService).getTedamUser());
		String responseString = restTemplate
				.postForObject(
						propertyService.getPropertyByNameAndParameter(Constants.PROPERTY_CONFIG,
								Constants.PROPERTY_JOBRUNNER_REST_URL).getValue() + START_JOB,
						job.getId(), String.class);
		tedamManagerView.showPanelMessage(job.getId(), job.getName(), job.getStatus(), responseString);
		rebuildTedamJobPanel(job);
	}

	private void doUnfollowButtonClickOperations(Job job) throws LocalizedException {
		job.setActive(false);
		job = jobService.save(job);
		TedamJobPanel tedamJobPanel = getTedamJobPanel(job);
		tedamManagerView.removeComponent(tedamJobPanel);
	}

	private void doResetButtonClickOperations(Job job) throws LocalizedException {
		TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

			@Override
			public void onConfirm() {
				Integer jobId = job.getId();
				try {
					jobService.resetJob(jobId);
					jobDetailService.resetJobDetail(jobId);
					testSetService.resetTestSet(jobId);
					job.setStatus(JobStatus.NOT_STARTED);
					rebuildTedamJobPanel(job);
				} catch (LocalizedException e) {
					getLogger().error(e.getMessage(), e);
				}
			}

			@Override
			public void onCancel() {
			}
		}, getLocaleValue("confirm.message.resetJob"), getLocaleValue("general.button.ok"),
				getLocaleValue("general.button.cancel"));
	}

	public List<JobStatus> getJobStatusList() {
		return jobStatusList;
	}

	public List<JobType> getJobTypeList() {
		return jobTypeList;
	}

	private void destroy() {
		viewEventBus.unsubscribe(this);
	}

	public void beforeLeavingView(ViewBeforeLeaveEvent event) {
		destroy();
	}

	@EventBusListenerMethod
	public void jobEditedEvent(JobEditedEvent jobEditedEvent) throws LocalizedException {
		Job job = jobService.save(jobEditedEvent.getJob());
		rebuildTedamJobPanel(job);
	}

	public void rebuildTedamJobPanel(Job job) {
		TedamJobPanel tedamJobPanel = getTedamJobPanel(job);
		if (tedamJobPanel != null) {
			tedamJobPanel.setJob(job);
		}
	}

	private void rebuildTedamJobGroupPanel(JobGroup jobGroup) {
		TedamJobGroupPanel tedamJobGroupPanel = getTedamJobGroupPanel(jobGroup);
		if (tedamJobGroupPanel != null) {
			tedamJobGroupPanel.setJobGroup(jobGroup);
		}
	}

	private TedamJobGroupPanel getTedamJobGroupPanel(JobGroup jobGroup) {
		for (Component component : tedamManagerView) {
			if (component instanceof TedamJobGroupPanel) {
				TedamJobGroupPanel tedamJobGroupPanel = (TedamJobGroupPanel) component;
				if (tedamJobGroupPanel != null && tedamJobGroupPanel.getJobGroup().equals(jobGroup)) {
					return tedamJobGroupPanel;
				}
			}
		}
		return null;
	}

	public void removeAllTedamJobPanel() {
		for (int i = 0; i < tedamManagerView.getComponentCount(); i++) {
			Component component = tedamManagerView.getComponent(i);
			if (component instanceof TedamJobPanel || component instanceof TedamJobGroupPanel) {
				tedamManagerView.removeComponent(component);
				i--;
			}
		}
	}

	private TedamJobPanel getTedamJobPanel(Job job) {
		for (Component component : tedamManagerView) {
			if (component instanceof TedamJobPanel) {
				TedamJobPanel tedamJobPanel = (TedamJobPanel) component;
				if (tedamJobPanel != null && tedamJobPanel.getJob().equals(job)) {
					return tedamJobPanel;
				}
			}
		}
		return null;
	}

	public void showClientMap() throws LocalizedException {
		String responseString = restTemplate.getForObject(propertyService
				.getPropertyByNameAndParameter(Constants.PROPERTY_CONFIG, Constants.PROPERTY_JOBRUNNER_REST_URL)
				.getValue() + GET_CLIENT_MAP, String.class);
		Map<String, ClientStatus> map = TedamJsonFactory.fromJsonMap(responseString, String.class, ClientStatus.class);
		prepareWindowClientMap(map);
	}

	public void prepareWindowClientMap(Map<String, ClientStatus> map) throws LocalizedException {
		Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
		List<ClientDTO> clientDTOList = buildClientDTOList(map);
		windowParameters.put(UIParameter.ITEMS, clientDTOList);
		tedamManagerView.openWindowClientMap(windowParameters);
	}

	private List<ClientDTO> buildClientDTOList(Map<String, ClientStatus> map) throws LocalizedException {
		List<ClientDTO> clientDTOList = new ArrayList<>();
		for (Entry<String, ClientStatus> entry : map.entrySet()) {
			Client client = TedamJsonFactory.fromJson(entry.getKey(), Client.class);
			Project project = getUserActiveProject();
			if (client.getProject().getId().equals(project.getId())) {
				List<JobDetail> jobDetails = jobDetailService.getJobDetailByClient(client);
				ClientDTO clientDTO = new ClientDTO(client.getName(), entry.getValue());
				for (JobDetail jobDetail : jobDetails) {
					if (jobDetail != null) {
						clientDTO.setTestSetId(jobDetail.getTestSetId());
						clientDTO.setTestSetStatus(jobDetail.getStatus());
						clientDTO.setJobId(jobDetail.getJobId());
					}
				}
				clientDTOList.add(clientDTO);
			}
		}
		return clientDTOList;
	}

	private String getLoggedInUser() {
		TedamUser tedamUser = null;
		String username = "";
		try {
			tedamUser = SecurityUtils.getCurrentUser(userService).getTedamUser();
		} catch (LocalizedException e) {
			e.printStackTrace();
		}
		if (tedamUser != null) {
			username = tedamUser.getUserName();
		}
		return username;
	}

	private Project getUserActiveProject() {
		return SecurityUtils.getUserSessionProject();
	}

	private void sortJobsByExecutionDate(List<Job> runnableJobList) {
		Collections.sort(runnableJobList, new Comparator<Job>() {
			@Override
			public int compare(Job j1, Job j2) {
				if (j1.getLastExecutedStartDate() == null || j2.getLastExecutedStartDate() == null) {
					return 0;
				}
				return j2.getLastExecutedStartDate().compareTo(j1.getLastExecutedStartDate());
			}
		});
	}

	private void sortJobGroupsByExecutionDate(List<JobGroup> runnableJobGroupList) {
		Collections.sort(runnableJobGroupList, new Comparator<JobGroup>() {
			@Override
			public int compare(JobGroup j1, JobGroup j2) {
				if (j1.getLastExecutedStartDate() == null || j2.getLastExecutedStartDate() == null) {
					return 0;
				}
				return j2.getLastExecutedStartDate().compareTo(j1.getLastExecutedStartDate());
			}
		});
	}

	public void setTedamManagerView(TedamManagerView tedamManagerView) {
		this.tedamManagerView = tedamManagerView;
	}

	public boolean isAreOnlyOwnJobs() {
		return areOnlyOwnJobs;
	}

	public void setAreOnlyOwnJobs(boolean areOnlyOwnJobs) {
		this.areOnlyOwnJobs = areOnlyOwnJobs;
	}

}
