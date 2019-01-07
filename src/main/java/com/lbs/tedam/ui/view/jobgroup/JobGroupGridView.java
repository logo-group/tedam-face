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

package com.lbs.tedam.ui.view.jobgroup;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.data.service.JobGroupService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractGridView;
import com.lbs.tedam.ui.view.jobgroup.edit.JobGroupEditView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;

@SpringView
public class JobGroupGridView extends AbstractGridView<JobGroup, JobGroupService, JobGroupGridPresenter, JobGroupGridView> {

	private static final long serialVersionUID = 1L;

	@Autowired
	public JobGroupGridView(JobGroupGridPresenter presenter) {
		super(presenter, SelectionMode.MULTI);
	}

	@PostConstruct
	private void init() {
		getPresenter().setView(this);
		setHeader(getLocaleValue("view.jobgroupgrid.header"));
		getTopBarLayout().addComponents(buildFollowJobGroups(), buildUnfollowJobGroups());
	}

	private TedamGridConfig<JobGroup> config = new TedamGridConfig<JobGroup>() {

		@Override
		public List<GridColumn> getColumnList() {
			return GridColumns.GridColumn.JOB_GROUP_COLUMNS;
		}

		@Override
		public Class<JobGroup> getBeanType() {
			return JobGroup.class;
		}

		@Override
		public List<RUDOperations> getRUDOperations() {
			List<RUDOperations> operations = new ArrayList<RUDOperations>();
			operations.add(RUDOperations.DELETE);
			operations.add(RUDOperations.VIEW);
			return operations;
		}

	};

	@Override
	public void buildGridColumnDescription() {
		getGrid().getColumn(GridColumn.JOB_GROUP_NAME.getColumnName()).setDescriptionGenerator(JobGroup::getName);
	}

	@Override
	protected TedamGridConfig<JobGroup> getTedamGridConfig() {
		return config;
	}

	@Override
	protected Class<? extends View> getEditView() {
		return JobGroupEditView.class;
	}

	private Component buildFollowJobGroups() {
		TedamButton followJobs = new TedamButton("view.jobgrid.followJobs", VaadinIcons.EYE);
		followJobs.setWidthUndefined();
		followJobs.setCaption("");
		followJobs.addClickListener(e -> {
			if (getGrid().getSelectedItems().isEmpty()) {
				return;
			}
			try {
				getPresenter().followJobGroups(new ArrayList<>(getGrid().getSelectedItems()));
				getGrid().deselectAll();
			} catch (LocalizedException e1) {
				logError(e1);
			}

		});
		return followJobs;
	}

	private Component buildUnfollowJobGroups() {
		TedamButton unfollowJobs = new TedamButton("view.jobgrid.unfollowJobs", VaadinIcons.EYE_SLASH);
		unfollowJobs.setWidthUndefined();
		unfollowJobs.setCaption("");
		unfollowJobs.addClickListener(e -> {
			if (getGrid().getSelectedItems().isEmpty()) {
				return;
			}
			try {
				getPresenter().unfollowJobGroups(new ArrayList<>(getGrid().getSelectedItems()));
				getGrid().deselectAll();
			} catch (LocalizedException e1) {
				logError(e1);
			}

		});
		return unfollowJobs;
	}

	protected void showActivated(boolean oneActivated, List<String> alreadyActiveNames) {
		String message = "";
		if (oneActivated) {
			String addedMessage = getLocaleValue("view.jobgrid.messages.showJobsFollowed");
			message += addedMessage + "\n";
		}
		if (alreadyActiveNames.size() > 0) {
			String alreadyActivatedMessage = getLocaleValue("view.jobgrid.messages.showAlreadyFollowed");
			message += alreadyActivatedMessage + "\n";
			for (String s : alreadyActiveNames) {
				message += s + "\n";
			}
		}
		if (message.length() > 0)
			TedamNotification.showTrayNotification(message, NotifyType.WARNING);
	}

	protected void showDeActivated(boolean oneDeActivated, List<String> alreadyDeActiveNames) {
		String message = "";
		if (oneDeActivated) {
			String addedMessage = getLocaleValue("view.jobgrid.messages.showJobsUnfollowed");
			message += addedMessage + "\n";
		}
		if (alreadyDeActiveNames.size() > 0) {
			String alreadyActivatedMessage = getLocaleValue("view.jobgrid.messages.showAlreadyUnfollowed");
			message += alreadyActivatedMessage + "\n";
			for (String s : alreadyDeActiveNames) {
				message += s + "\n";
			}
		}
		if (message.length() > 0)
			TedamNotification.showNotification(message, NotifyType.WARNING);
	}

}
