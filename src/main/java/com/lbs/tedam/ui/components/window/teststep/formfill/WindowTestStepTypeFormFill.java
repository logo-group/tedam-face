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

/**
 *
 */
package com.lbs.tedam.ui.components.window.teststep.formfill;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.FormFillGenerator;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.teststeptype.formfill.FormFillEditPresenter;
import com.lbs.tedam.ui.view.teststeptype.formfill.FormFillEditView;
import com.lbs.tedam.util.EnumsV2.TestStepType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.*;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@ViewScope
public class WindowTestStepTypeFormFill extends TedamWindow {

    private static final long serialVersionUID = 1L;

    FormFillEditView formFillEditView;
    FormFillEditPresenter formFillEditPresenter;

    @Autowired
    public WindowTestStepTypeFormFill(ViewEventBus viewEventBus, FormFillEditView formFillEditView, FormFillEditPresenter formFillEditPresenter, PropertyService propertyService) {
        super(WindowSize.BIG, viewEventBus, propertyService);
        this.formFillEditView = formFillEditView;
        this.formFillEditPresenter = formFillEditPresenter;
    }

    @Override
    protected Component buildContent() {
        return formFillEditView;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(formFillEditPresenter.getTestStep()));
    }

    private FormFillGenerator getGenerator() {
        FormFillGenerator formFillGenerator = (FormFillGenerator) GeneratorFactory.getGenerator(TestStepType.FORM_FILL, formFillEditPresenter.getBeanFactory());
        formFillGenerator.setSnapshotDefinition(formFillEditPresenter.getItem());
        formFillEditPresenter.getTestStep().setGenerator(formFillGenerator);
        return formFillGenerator;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeFormFill.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        formFillEditPresenter.enterView(parameters);
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (formFillEditPresenter.focusFirstErrorField() || !getGenerator().validate()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.teststeptype"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
        formFillEditPresenter.destroy();
    }

    @Override
    protected void cancelButtonPressed() throws LocalizedException {
        formFillEditPresenter.resetSnapshotDefinition();
        super.cancelButtonPressed();
    }

    @Override
    protected void okButtonPressed() throws LocalizedException {
        setSnapshotValueOrder(formFillEditView.getGridSnapshotValues().getGridDataProvider().getListDataProvider().getItems());
        super.okButtonPressed();
    }

    public void setSnapshotValueOrder(Collection<SnapshotValue> collection) {
        List<SnapshotValue> snapshotValueList = new ArrayList<>(collection);
        for (ListIterator<SnapshotValue> listIterator = snapshotValueList.listIterator(); listIterator.hasNext(); ) {
            SnapshotValue snapshotValue = listIterator.next();
            snapshotValue.setOrder(listIterator.nextIndex());
        }
    }
}
