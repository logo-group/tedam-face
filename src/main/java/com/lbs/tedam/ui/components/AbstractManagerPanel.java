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

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.basic.TedamPanel;
import com.lbs.tedam.ui.util.DateTimeFormatter;
import com.lbs.tedam.util.EnumsV2.JobStatus;
import com.vaadin.server.Resource;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class AbstractManagerPanel extends TedamPanel implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

	protected final String PANEL_STYLE = ValoTheme.PANEL_WELL + " jobpanel";

	protected TedamButton btnStart;
	protected TedamButton btnStop;
	protected TedamButton btnUnfollow;
	protected TedamButton btnReset;
	protected DateTimeFormatter dateTimeFormetter;
	protected VerticalLayout mainLayout;

	protected void initUI() {
		setSizeUndefined();
		mainLayout = new VerticalLayout();
		setContent(mainLayout);
	}

	protected void buildButtons(JobStatus jobStatus) {
		if (JobStatus.getInActiveJobStatus().contains(jobStatus)) {
			getBtnStart().setEnabled(true);
			getBtnStop().setEnabled(false);
		} else {
			getBtnStart().setEnabled(false);
			getBtnStop().setEnabled(true);
		}
	}

	protected TedamLabel buildLabel(String parameter) {
		TedamLabel label = new TedamLabel(parameter);
		label.setStyleName(ValoTheme.LABEL_TINY);
		return label;
	}

	protected ProgressBar buildProgressBar(int completedJobDetailSize, int allJobDetailSize) {
		ProgressBar progressBar = new ProgressBar();
		progressBar.setValue(((float) completedJobDetailSize / allJobDetailSize));
		progressBar.setDescription(completedJobDetailSize + "/" + allJobDetailSize);
		return progressBar;
	}

	protected TedamButton buildButton(String id, String jobId, Resource icon) {
		TedamButton button = new TedamButton(id, icon);
		button.setId(button.getId() + jobId);
		button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		button.setWidthUndefined();
		button.setCaption("");
		return button;
	}

	protected TedamButton getBtnStart() {
		return btnStart;
	}

	protected TedamButton getBtnStop() {
		return btnStop;
	}

	protected TedamButton getBtnUnfollow() {
		return btnUnfollow;
	}
}
