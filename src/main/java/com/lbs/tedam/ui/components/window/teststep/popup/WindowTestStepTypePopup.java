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

package com.lbs.tedam.ui.components.window.teststep.popup;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.generator.steptype.PopupGenerator;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.combobox.TedamGridTagComboBox;
import com.lbs.tedam.ui.components.combobox.TedamPopUpComboBox;
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
public class WindowTestStepTypePopup extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private BeanFactory beanFactory;
    private PopupWindowPresenter popupWindowPresenter;
    private TedamGridTagComboBox cbGridTags;
    private TedamPopUpComboBox cbPopupItems;

    @Autowired
    public WindowTestStepTypePopup(ViewEventBus viewEventBus, PopupWindowPresenter presenter, BeanFactory beanFactory, TedamGridTagComboBox cbGridTags,
                                   TedamPopUpComboBox cbPopupItems, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.popupWindowPresenter = presenter;
        this.beanFactory = beanFactory;
        this.cbGridTags = cbGridTags;
        this.cbPopupItems = cbPopupItems;
    }

    @PostConstruct
    private void initView() {
        popupWindowPresenter.init(this);
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        addSection(getLocaleValue("view.viewedit.section.values"), cbGridTags, cbPopupItems);
        popupWindowPresenter.fillComponentsWithValues();

        return getMainLayout();
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(popupWindowPresenter.getTestStep()));
    }

    private PopupGenerator getGenerator() {
        // TODO: It does not need to be a getGenerator anymore at testStep.
        PopupGenerator generator = (PopupGenerator) GeneratorFactory.getGenerator(TestStepType.POPUP, beanFactory);
        generator.setGridTag(cbGridTags.getValue());
        generator.setPopupItem(cbPopupItems.getValue());

        popupWindowPresenter.getTestStep().setGenerator(generator);

        return generator;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypePopup.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        popupWindowPresenter.enterView(parameters);
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

    public TedamGridTagComboBox getCbGridTags() {
        return cbGridTags;
    }

    public TedamPopUpComboBox getCbPopupItems() {
        return cbPopupItems;
    }

    @Override
    protected void windowClose() {
    }
}
