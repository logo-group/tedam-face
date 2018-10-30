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

package com.lbs.tedam.ui.components.window.teststep.messageverify;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.generator.steptype.MessageVerifyGenerator;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamCheckBox;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.combobox.TedamComboBox;
import com.lbs.tedam.ui.components.combobox.TedamMessageComboBox;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
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
public class WindowTestStepTypeMessageVerify extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private BeanFactory beanFactory;
    private MessageVerifyWindowPresenter messageVerifyWindowPresenter;
    private TedamCheckBox chkContinueOnError;
    private TedamMessageComboBox cbMessage;

    @Autowired
    public WindowTestStepTypeMessageVerify(ViewEventBus viewEventBus, MessageVerifyWindowPresenter presenter, BeanFactory beanFactory, PropertyService propertyService,
                                           TedamMessageComboBox cbMessage) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.messageVerifyWindowPresenter = presenter;
        this.beanFactory = beanFactory;
        this.cbMessage = cbMessage;
    }

    @PostConstruct
    private void initView() {
        messageVerifyWindowPresenter.init(this);
    }

    @Override
    protected Component buildContent() throws TedamWindowNotAbleToOpenException, LocalizedException {
        initChkContinueOnError();
        addSection(getLocaleValue("view.viewedit.section.values"), cbMessage, chkContinueOnError);
        messageVerifyWindowPresenter.fillComponentsWithValues();
        return getMainLayout();
    }

    private void initChkContinueOnError() {
        chkContinueOnError = new TedamCheckBox("view.buttonclickeedit.chkContinueOnError", null, true, true);
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(messageVerifyWindowPresenter.getTestStep()));
    }

    private MessageVerifyGenerator getGenerator() {
        MessageVerifyGenerator generator = (MessageVerifyGenerator) GeneratorFactory.getGenerator(TestStepType.MESSAGE_VERIFY, beanFactory);
        generator.setMessage(cbMessage.getValue());
        generator.setContinueOnError(chkContinueOnError.getValue() ? "1" : "0");
        messageVerifyWindowPresenter.getTestStep().setGenerator(generator);
        return generator;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeMessageVerify.header");
    }

    public TedamCheckBox getChkContinueOnError() {
        return chkContinueOnError;
    }

    public TedamComboBox<String> getCbMessage() {
        return cbMessage;
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        messageVerifyWindowPresenter.enterView(parameters);
        initWindow();
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
