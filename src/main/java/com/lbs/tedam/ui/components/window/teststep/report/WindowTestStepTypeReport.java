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

package com.lbs.tedam.ui.components.window.teststep.report;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.generator.steptype.ReportGenerator;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.EnumsV2.TestStepType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import javax.annotation.PostConstruct;
import java.util.Map;

@SpringComponent
@ViewScope
public class WindowTestStepTypeReport extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private BeanFactory beanFactory;
    private ReportWindowPresenter reportWindowPresenter;
    private TedamTextField tfReportWaitSleepMillis;
    private TedamTextField tfReportName;

    @Autowired
    public WindowTestStepTypeReport(ViewEventBus viewEventBus, ReportWindowPresenter presenter, BeanFactory beanFactory, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.reportWindowPresenter = presenter;
        this.beanFactory = beanFactory;
    }

    @PostConstruct
    private void initView() {
        reportWindowPresenter.init(this);
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        tfReportName = new TedamTextField("view.reportedit.reportName", "half", true, false);
        tfReportWaitSleepMillis = new TedamTextField("view.reportedit.reportWaitMillis", "half", true, true);
        addSection(getLocaleValue("view.viewedit.section.values"), tfReportName, tfReportWaitSleepMillis);
        reportWindowPresenter.fillComponentsWithValues();
        return getMainLayout();
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(reportWindowPresenter.getTestStep()));
    }

    private ReportGenerator getGenerator() {
        ReportGenerator generator = (ReportGenerator) GeneratorFactory.getGenerator(TestStepType.REPORT, beanFactory);
        generator.setReportName(tfReportName.getValue());
        generator.setReportWaitSleepMillis(tfReportWaitSleepMillis.getValue().isEmpty() ? 0L : (Long.valueOf(tfReportWaitSleepMillis.getValue()) * Constants.ONE_SECOND));
        reportWindowPresenter.getTestStep().setGenerator(generator);
        return generator;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeReport.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        reportWindowPresenter.enterView(parameters);
        initWindow();
    }

    public TedamTextField getTfReportName() {
        return tfReportName;
    }

    public TedamTextField getTfReportWaitSleepMillis() {
        return tfReportWaitSleepMillis;
    }

    @Override
    protected boolean readyToClose() {
        if (!getGenerator().validate()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.teststeptype"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }
}
