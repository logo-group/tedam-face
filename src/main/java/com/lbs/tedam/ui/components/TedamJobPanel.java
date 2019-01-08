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
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.util.DateTimeFormatter;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.util.EnumsV2.CommandStatus;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ProgressBar;

@Component
@Scope("prototype")
public class TedamJobPanel extends AbstractManagerPanel implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    private Job job;
    private JobPanelButtonClickListener clickListener;

    @Autowired
    public TedamJobPanel(DateTimeFormatter dateTimeFormetter) {
        this.dateTimeFormetter = dateTimeFormetter;
    }

    @PostConstruct
    public void init() {
        initUI();
    }

	private void build() {
		setStyleName(PANEL_STYLE);
        addStyleName(TedamStatic.getJobStatusColorMap().get(job.getStatus()).toString().toLowerCase());
        setCaption(job.getId() + " - " + job.getName());
		setIcon(VaadinIcons.FILE);
        buildVerticalLayout(job);
		buildButtons(job.getStatus());
    }

    private void buildVerticalLayout(Job job) {
        mainLayout.removeAllComponents();
        if (job.getLastExecutedStartDate() != null) {
            TedamLabel startDate = buildLabel(dateTimeFormetter.format(job.getLastExecutedStartDate(), LocaleConstants.LOCALE_TRTR));
            mainLayout.addComponent(startDate);
        }
        TedamLabel environment = buildLabel(job.getJobEnvironment().toString());
        TedamLabel lastExecutingUser = buildLabel(getLocaleValue("view.jobmanager.lastexecutingusertitle"));
        if (job.getLastExecutingUser() != null) {
            lastExecutingUser.setValue(lastExecutingUser.getValue() + job.getLastExecutingUser().getUserName());
        }
        int completedJobDetailSize = (int) job.getJobDetails().stream().filter(jobDetail -> CommandStatus.COMPLETED.equals(jobDetail.getStatus())).count();
        ProgressBar progressBar = buildProgressBar(completedJobDetailSize, job.getJobDetails().size());
        TedamHorizontalLayout footer = buildFooter(job);
        mainLayout.addComponents(environment, lastExecutingUser, progressBar, footer);
    }

    private TedamHorizontalLayout buildFooter(Job job) {
        TedamHorizontalLayout footer = new TedamHorizontalLayout();
        footer.setStyleName("v-window-bottom-toolbar");
        btnStart = buildButton("view.tedammanager.button.startjob", job.getId().toString(), VaadinIcons.PLAY);
        btnStart.setDisableOnClick(true);
        btnStart.addClickListener(e -> clickListener.startButtonClickOperations(job));
        btnStop = buildButton("view.tedammanager.button.stopjob", job.getId().toString(), VaadinIcons.STOP);
        btnStop.setDisableOnClick(true);
        btnStop.addClickListener(e -> clickListener.stopButtonClickOperations(job));
		btnUnfollow = buildButton("view.tedammanager.button.unfollowJob", job.getId().toString(), VaadinIcons.EYE_SLASH);
		btnUnfollow.addClickListener(e -> clickListener.unfollowButtonClickOperations(job));
        btnReset = buildButton("view.tedammanager.button.resetJob", job.getId().toString(), VaadinIcons.REFRESH);
        btnReset.addClickListener(e -> clickListener.resetButtonClickOperations(job));
        footer.addComponents(btnStart, btnStop, btnUnfollow, btnReset);
        return footer;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
        build();
    }

    public void setClickListener(JobPanelButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface JobPanelButtonClickListener {

        public void stopButtonClickOperations(Job job);

		public void unfollowButtonClickOperations(Job job);

        public void startButtonClickOperations(Job job);

        public void resetButtonClickOperations(Job job);
    }
}
