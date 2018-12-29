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

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.data.service.JobGroupService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.job.WindowSelectJob;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid.SelectionMode;

@SpringView
public class JobGroupEditView extends AbstractEditView<JobGroup, JobGroupService, JobGroupEditPresenter, JobGroupEditView> {

	private static final long serialVersionUID = 1L;

	private TedamTextField name;
	private TedamTextField description;
	private TedamButton addJobs;
	private TedamButton removeJobs;
	private TedamFilterGrid<Job> gridJobs;

	private final WindowSelectJob windowJob;

	@Autowired
	public JobGroupEditView(JobGroupEditPresenter presenter, WindowSelectJob windowJob) {
		super(presenter);
		this.windowJob = windowJob;
	}

	@Override
	public String getHeader() {
		return getLocaleValue("view.jobgroupedit.header");
	}

	@PostConstruct
	private void initView() {

		name = new TedamTextField("view.jobgroupedit.textfield.name", "full", true, true);
		description = new TedamTextField("view.jobgroupedit.textfield.description", "full", true, true);

		buildAddAndRemoveButtons();
		buildJobsGrid();

		addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name, description);
		addSection(getLocaleValue("view.jobgroupedit.section.jobs"), 1, null, addJobs, removeJobs, gridJobs);

		getPresenter().setView(this);
	}

	private void buildAddAndRemoveButtons() {
		addJobs = new TedamButton("view.jobgroupedit.button.addjobs", VaadinIcons.PLUS_CIRCLE);
		addJobs.addStyleName("half");
		removeJobs = new TedamButton("view.jobgroupedit.button.removejobs", VaadinIcons.MINUS_CIRCLE);
		removeJobs.addStyleName("half");

		addJobs.addClickListener(e -> {
			try {
				getPresenter().prepareJobWindow();
			} catch (LocalizedException e1) {
				logError(e1);
			}
		});
		removeJobs.addClickListener(e -> getPresenter().removeJobs());
	}

	protected void buildJobsGrid() {
		gridJobs = new TedamFilterGrid<Job>(buildJobGridConfig(), SelectionMode.MULTI);
		gridJobs.setId("JobGroupJobGrid");
	}

	private TedamGridConfig<Job> buildJobGridConfig() {
		TedamGridConfig<Job> jobsGridConfig = new TedamGridConfig<Job>() {

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
				operations.add(RUDOperations.NONE);
				return operations;
			}

		};
		return jobsGridConfig;
	}

	protected void organizeJobsGrid(AbstractDataProvider<Job> abstractDataProvider) {
		gridJobs.setGridDataProvider(abstractDataProvider);
		gridJobs.initFilters();
	}

	public void openJobSelectWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		try {
			windowJob.open(windowParameters);
		} catch (TedamWindowNotAbleToOpenException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void collectGrids() {
		super.collectGrids();
		getGridList().add(gridJobs);
	}

	public TedamFilterGrid<Job> getGridJobs() {
		return gridJobs;
	}

	public void setGridJobs(TedamFilterGrid<Job> gridJobs) {
		this.gridJobs = gridJobs;
	}

	public void showGridRowNotSelected() {
		TedamNotification.showNotification(getLocaleValue("view.jobedit.messages.showGridRowNotSelected"), NotifyType.ERROR);
	}

}
