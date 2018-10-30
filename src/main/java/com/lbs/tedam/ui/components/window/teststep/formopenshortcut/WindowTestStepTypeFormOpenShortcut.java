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

package com.lbs.tedam.ui.components.window.teststep.formopenshortcut;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.FormOpenShortcutGenerator;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.combobox.TedamFormOpenShortcutTypeComboBox;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import javax.annotation.PostConstruct;
import java.util.Map;

@SpringComponent
@PrototypeScope
public class WindowTestStepTypeFormOpenShortcut extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private FormOpenShortcutEditPresenter formOpenShortcutEditPresenter;
    private TedamFormOpenShortcutTypeComboBox tedamFormOpenShortcutTypeComboBox;

    @Autowired
    public WindowTestStepTypeFormOpenShortcut(FormOpenShortcutEditPresenter formOpenEditPresenter, TedamFormOpenShortcutTypeComboBox tedamFormOpenShortcutTypeComboBox,
                                              ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.formOpenShortcutEditPresenter = formOpenEditPresenter;
        this.tedamFormOpenShortcutTypeComboBox = tedamFormOpenShortcutTypeComboBox;
    }

    @PostConstruct
    private void initView() {
        formOpenShortcutEditPresenter.init(this);
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        addSection(getLocaleValue("view.viewedit.section.values"), tedamFormOpenShortcutTypeComboBox);
        formOpenShortcutEditPresenter.fillComponentsWithValues();
        return getMainLayout();
    }

    public TedamFormOpenShortcutTypeComboBox getTedamFormOpenShortcutTypeComboBox() {
        return tedamFormOpenShortcutTypeComboBox;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeFormOpenShortcut.header");
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(formOpenShortcutEditPresenter.getTestStep()));
    }

    private FormOpenShortcutGenerator getGenerator() {
        FormOpenShortcutGenerator generator = (FormOpenShortcutGenerator) formOpenShortcutEditPresenter.getTestStep().getGenerator();
        generator.setFormOpenShortcutType(tedamFormOpenShortcutTypeComboBox.getSelectedItem().get());
        return generator;
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        formOpenShortcutEditPresenter.enterView(parameters);
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
