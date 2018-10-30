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

package com.lbs.tedam.ui.components.window.teststep.verify;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.generator.steptype.VerifyGenerator;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.teststeptype.verify.VerifyEditPresenter;
import com.lbs.tedam.ui.view.teststeptype.verify.VerifyEditView;
import com.lbs.tedam.util.EnumsV2.TestStepType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.*;

@SpringComponent
@ViewScope
public class WindowTestStepTypeVerify extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private VerifyEditView verifyFillEditView;
    private VerifyEditPresenter verifyEditPresenter;

    @Autowired
    public WindowTestStepTypeVerify(ViewEventBus viewEventBus, VerifyEditView verifyFillEditView, VerifyEditPresenter verifyEditPresenter, PropertyService propertyService) {
        super(WindowSize.BIG, viewEventBus, propertyService);
        this.verifyFillEditView = verifyFillEditView;
        this.verifyEditPresenter = verifyEditPresenter;
    }

    @Override
    protected Component buildContent() {
        return verifyFillEditView;
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(verifyEditPresenter.getTestStep()));
    }

    private VerifyGenerator getGenerator() {
        VerifyGenerator verifyGeneratorGenerator = (VerifyGenerator) GeneratorFactory.getGenerator(TestStepType.VERIFY, verifyEditPresenter.getBeanFactory());
        verifyGeneratorGenerator.setSnapshotDefinition(verifyEditPresenter.getItem());
        verifyGeneratorGenerator.setIgnoreRowIndex(verifyEditPresenter.isChkIgnoreRowIndex());
        verifyEditPresenter.getTestStep().setGenerator(verifyGeneratorGenerator);
        return verifyGeneratorGenerator;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeVerify.header");
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        verifyEditPresenter.enterView(parameters);
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
        verifyEditPresenter.destroy();
    }

    @Override
    protected void cancelButtonPressed() throws LocalizedException {
        verifyEditPresenter.resetSnapshotDefinition();
        super.cancelButtonPressed();
    }

    @Override
    protected void okButtonPressed() throws LocalizedException {
        setSnapshotValueOrder(verifyFillEditView.getGridSnapshotValues().getGridDataProvider().getListDataProvider().getItems());
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
