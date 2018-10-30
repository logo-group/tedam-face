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

import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.Job;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamCheckBox;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.basic.TedamPanel;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.components.window.client.WindowClient;
import com.lbs.tedam.ui.util.Enums.TedamColor;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.util.EnumsV2.JobStatus;
import com.lbs.tedam.util.EnumsV2.JobType;
import com.lbs.tedam.util.EnumsV2.TedamBoolean;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

@SpringView
public class TedamManagerView extends CssLayout implements Serializable, View, HasLogger, TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    private final TedamManagerPresenter tedamManagerPresenter;
    private final WindowClient windowClient;

    private TedamHorizontalLayout topBarLayout;
    private TedamHorizontalLayout midBarLayout;

    @Autowired
    public TedamManagerView(TedamManagerPresenter tedamManagerPresenter, WindowClient windowClient) {
        this.tedamManagerPresenter = tedamManagerPresenter;
        this.windowClient = windowClient;
    }

    @PostConstruct
    public void init() {
        tedamManagerPresenter.setTedamManagerView(this);
        initView();
        tedamManagerPresenter.build();
    }

    private void initView() {
        setResponsive(true);

        initTopBarLayout();
        initMidBarLayout();
        TedamLabel gridLabel = initGridLabel();
        TedamButton btnShowClientMap = initButtonShowClientMap();
        TedamPanel pnlJobStatusButton = initPanelJobStatusButton();
        TedamPanel pnlJobTypeOwnJobButton = initPanelJobTypeAndOwnJobButton();

        topBarLayout.addComponents(gridLabel, pnlJobStatusButton, btnShowClientMap);
        midBarLayout.addComponents(pnlJobTypeOwnJobButton);
        topBarLayout.setExpandRatio(pnlJobStatusButton, 1);
        addComponents(topBarLayout, midBarLayout);

    }

    private TedamPanel initPanelJobStatusButton() {
        TedamPanel pnl = new TedamPanel();
        TedamHorizontalLayout layout = new TedamHorizontalLayout();
        pnl.setContent(layout);
        for (Entry<JobStatus, TedamColor> entry : TedamStatic.getJobStatusColorMap().entrySet()) {
            TedamCheckBox checkBox = new TedamCheckBox("", "full", true, true);
            checkBox.addValueChangeListener(new ValueChangeListener<Boolean>() {

                /** long serialVersionUID */
                private static final long serialVersionUID = 1L;

                @Override
                public void valueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue().equals(TedamBoolean.TRUE.getBooleanValue())) {
                        tedamManagerPresenter.getJobStatusList().add(entry.getKey());
                    } else {
                        tedamManagerPresenter.getJobStatusList().remove(entry.getKey());
                    }
                    tedamManagerPresenter.build();
                }
            });
            checkBox.setCaption(entry.getKey().toString());
            checkBox.setStyleName(entry.getValue().toString().toLowerCase());
            checkBox.setWidthUndefined();
            layout.setSpacing(true);
            layout.addComponent(checkBox);
        }
        pnl.setSizeUndefined();
        return pnl;
    }

    private TedamPanel initPanelJobTypeAndOwnJobButton() {
        TedamPanel pnl = new TedamPanel();
        TedamHorizontalLayout layout = new TedamHorizontalLayout();
        pnl.setContent(layout);
        for (JobType jobType : Arrays.asList(JobType.class.getEnumConstants())) {
            TedamCheckBox checkBox = new TedamCheckBox("", "full", true, true);
            checkBox.addValueChangeListener(new ValueChangeListener<Boolean>() {

                /** long serialVersionUID */
                private static final long serialVersionUID = 1L;

                @Override
                public void valueChange(ValueChangeEvent<Boolean> event) {
                    if (event.getValue().equals(TedamBoolean.TRUE.getBooleanValue())) {
                        tedamManagerPresenter.getJobTypeList().add(jobType);
                    } else {
                        tedamManagerPresenter.getJobTypeList().remove(jobType);
                    }
                    tedamManagerPresenter.build();
                }
            });
            checkBox.setCaption(jobType.toString());
            checkBox.setWidthUndefined();
            layout.setSpacing(true);
            layout.addComponent(checkBox);
        }
        // OWN JOB CHECKBOX
        TedamCheckBox checkBox = new TedamCheckBox("", "full", true, true);
        checkBox.addValueChangeListener(new ValueChangeListener<Boolean>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue().equals(TedamBoolean.TRUE.getBooleanValue())) {
                    tedamManagerPresenter.setAreOnlyOwnJobs(true);
                } else {
                    tedamManagerPresenter.setAreOnlyOwnJobs(false);
                }
                tedamManagerPresenter.build();
            }
        });
        checkBox.setCaption(getLocaleValue("view.tedammanager.ownjob.checkbox"));
        checkBox.setWidthUndefined();
        layout.setSpacing(true);
        layout.addComponent(checkBox);
        pnl.setSizeUndefined();
        return pnl;
    }

    private TedamButton initButtonShowClientMap() {
        TedamButton btnShowClientMap = new TedamButton("view.tedammanager.button.showclientmap");
        btnShowClientMap.addStyleName("friendly");
        btnShowClientMap.setWidthUndefined();
        btnShowClientMap.addClickListener(e -> {
            try {
                tedamManagerPresenter.showClientMap();
            } catch (LocalizedException e1) {
                getLogger().error(e1.getMessage(), e1);
            }
        });
        return btnShowClientMap;
    }

    public void openWindowClientMap(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowClient.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            windowClient.close();
            TedamNotification.showNotification(e.getMessage(), NotifyType.ERROR);
        }
    }

    private TedamLabel initGridLabel() {
        TedamLabel gridLabel = new TedamLabel(getLocaleValue("view.tedammanager.label"));
        gridLabel.setStyleName(ValoTheme.LABEL_H3 + " bold");
        return gridLabel;
    }

    private void initTopBarLayout() {
        topBarLayout = new TedamHorizontalLayout();
        topBarLayout.setStyleName("top-bar");
        topBarLayout.setWidth("100%");
        topBarLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        topBarLayout.setSpacing(true);
    }

    private void initMidBarLayout() {
        midBarLayout = new TedamHorizontalLayout();
        midBarLayout.setStyleName("top-bar");
        midBarLayout.setWidth("100%");
        midBarLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
    }

    public void showJobMessage(Job job, String responseString) {
        NotifyType notifyType = responseString.equals(HttpStatus.OK.getReasonPhrase()) ? NotifyType.SUCCESS : NotifyType.ERROR;
        TedamNotification.showTrayNotification(job.getId() + " - " + job.getName() + " " + job.getStatus(), notifyType);
    }
}
