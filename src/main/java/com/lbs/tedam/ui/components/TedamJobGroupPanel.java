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

package com.lbs.tedam.ui.components;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lbs.tedam.localization.LocaleConstants;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.model.JobDetail;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.util.DateTimeFormatter;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.util.EnumsV2.CommandStatus;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ProgressBar;

@Component
@Scope("prototype")
public class TedamJobGroupPanel extends AbstractManagerPanel implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

	private JobGroup jobGroup;
	private JobGroupPanelButtonClickListener clickListener;

    @Autowired
    public TedamJobGroupPanel(DateTimeFormatter dateTimeFormetter) {
        this.dateTimeFormetter = dateTimeFormetter;
    }

    @PostConstruct
    public void init() {
        initUI();
    }

	private void build() {
		setStyleName(PANEL_STYLE);
        addStyleName(TedamStatic.getJobStatusColorMap().get(jobGroup.getStatus()).toString().toLowerCase());
        setCaption(jobGroup.getId() + " - " + jobGroup.getName());
		setIcon(VaadinIcons.FILE_TREE);
        buildVerticalLayout(jobGroup);
		buildButtons(jobGroup.getStatus());
    }

	private void buildVerticalLayout(JobGroup jobGroup) {
        mainLayout.removeAllComponents();
		if (jobGroup.getLastExecutedStartDate() != null) {
			TedamLabel startDate = buildLabel(
					dateTimeFormetter.format(jobGroup.getLastExecutedStartDate(), LocaleConstants.LOCALE_TRTR));
			mainLayout.addComponent(startDate);
		}
		TedamLabel environment = buildLabel("");
		TedamLabel lastExecutingUser = buildLabel(getLocaleValue("view.jobmanager.lastexecutingusertitle"));
		if (jobGroup.getLastExecutingUser() != null) {
			lastExecutingUser.setValue(lastExecutingUser.getValue() + jobGroup.getLastExecutingUser().getUserName());
		}
		int[] countJobDetailSize = countJobDetailSize();
		ProgressBar progressBar = buildProgressBar(countJobDetailSize[0], countJobDetailSize[1]);
		TedamHorizontalLayout footer = buildFooter(jobGroup);
		mainLayout.addComponents(environment, lastExecutingUser, progressBar, footer);
	}

	private int[] countJobDetailSize() {
		int[] countJobDetailSize = new int[2];
		countJobDetailSize[0] = 0;
		countJobDetailSize[1] = 0;
		for (Job job : jobGroup.getJobs()) {
			for (JobDetail jobDetail : job.getJobDetails()) {
				if (CommandStatus.COMPLETED.equals(jobDetail.getStatus())) {
					countJobDetailSize[0]++;
				}
				countJobDetailSize[1]++;
			}
		}
		return countJobDetailSize;
	}

	private TedamHorizontalLayout buildFooter(JobGroup jobGroup) {
        TedamHorizontalLayout footer = new TedamHorizontalLayout();
        footer.setStyleName("v-window-bottom-toolbar");
		btnStart = buildButton("view.tedammanager.button.startjob", jobGroup.getId().toString(), VaadinIcons.PLAY);
        btnStart.setDisableOnClick(true);
		btnStart.addClickListener(e -> clickListener.startButtonClickOperations(jobGroup));
		btnStop = buildButton("view.tedammanager.button.stopjob", jobGroup.getId().toString(), VaadinIcons.STOP);
        btnStop.setDisableOnClick(true);
		btnStop.addClickListener(e -> clickListener.stopButtonClickOperations(jobGroup));
		btnUnfollow = buildButton("view.tedammanager.button.unfollowJob", jobGroup.getId().toString(),
				VaadinIcons.EYE_SLASH);
		btnUnfollow.addClickListener(e -> clickListener.unfollowButtonClickOperations(jobGroup));
		btnReset = buildButton("view.tedammanager.button.resetJob", jobGroup.getId().toString(), VaadinIcons.REFRESH);
		btnReset.addClickListener(e -> clickListener.resetButtonClickOperations(jobGroup));
        footer.addComponents(btnStart, btnStop, btnUnfollow, btnReset);
        return footer;
    }

	public JobGroup getJobGroup() {
        return jobGroup;
    }

	public void setJobGroup(JobGroup jobGroup) {
		this.jobGroup = jobGroup;
        build();
    }

	public void setClickListener(JobGroupPanelButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }

	public interface JobGroupPanelButtonClickListener {

		public void stopButtonClickOperations(JobGroup jobGroup);

		public void unfollowButtonClickOperations(JobGroup jobGroup);

		public void startButtonClickOperations(JobGroup jobGroup);

		public void resetButtonClickOperations(JobGroup jobGroup);
    }
}
