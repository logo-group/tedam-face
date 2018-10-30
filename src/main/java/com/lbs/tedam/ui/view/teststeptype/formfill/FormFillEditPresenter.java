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

package com.lbs.tedam.ui.view.teststeptype.formfill;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.FormDefinitionService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.SnapshotDefinitionService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.SnapshotDefinition;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.TedamFaceEvents.LookUpSelectedEvent;
import com.lbs.tedam.ui.TedamFaceEvents.SnapshotValuesSelectedEvent;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractFillTestStepEditPresenter;
import com.lbs.tedam.ui.view.teststeptype.snapshot.SnapshotValueDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import java.util.Map;

;

@SpringComponent
@ViewScope
public class FormFillEditPresenter extends AbstractFillTestStepEditPresenter<FormFillEditPresenter, FormFillEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    public FormFillEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, SnapshotDefinitionService snapshotDefinitionService, TedamUserService userService,
                                 BeanFactory beanFactory, SnapshotValueDataProvider snapshotValueDataProvider, FormDefinitionService formDefinitionService, PropertyService propertyService) {
        super(viewEventBus, navigationManager, snapshotDefinitionService, SnapshotDefinition.class, beanFactory, userService, snapshotValueDataProvider, formDefinitionService,
                propertyService);
    }

    @Override
    public void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        subscribeToEventBus();
        setTestStep((TestStep) parameters.get(UIParameter.TESTSTEP));
        SnapshotDefinition snapshotDefinition = buildSnapshotDefinitionEntity();
        if (snapshotDefinition == null) {
            getView().showNotFound();
            return;
        }

        refreshView(snapshotDefinition, (ViewMode) parameters.get(UIParameter.MODE));
        getSnapshotValueDataProvider().setSnapshotDefinition(getTestStep(), snapshotDefinition);
        getView().organizeGrid(getSnapshotValueDataProvider());
        // TODO they look like view a business, we have to move
        organizeComponents(getView().getAccordion(), (ViewMode) parameters.get(UIParameter.MODE) == ViewMode.VIEW);
        setGridEditorAttributes(getView().getGridSnapshotValues(), (ViewMode) parameters.get(UIParameter.MODE) != ViewMode.VIEW);
    }

    @EventBusListenerMethod
    public void snapshotValuesSelectedEvent(SnapshotValuesSelectedEvent event) {
        for (SnapshotValue snapshotValue : event.getSnapshotValues()) {
            getItem().addSnapshotValue(snapshotValue);
        }
        getSnapshotValueDataProvider().getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    @EventBusListenerMethod
    public void lookUpSelectedEvent(LookUpSelectedEvent event) {
        SnapshotValue selectedSnapshotValue = getView().getGridSnapshotValues().getSelectedItems().iterator().next();
        if (StringUtils.isEmpty(selectedSnapshotValue.getLookUpParameter())) {
            selectedSnapshotValue.setLookUpParameter(event.getLookUp().getGenerator().generateLookUp());
        } else {
            selectedSnapshotValue.setLookUpParameter(selectedSnapshotValue.getLookUpParameter() + event.getLookUp().getGenerator().generateLookUp());
        }
        getSnapshotValueDataProvider().getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void prepareWindowSnapshotValues() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.TESTSTEP, getTestStep());
        windowParameters.put(UIParameter.SNAPSHOT_DEFINITION, getItem());
        windowParameters.put(UIParameter.CREATED_USER, SecurityUtils.getCurrentUser(getUserService()).getUsername());
        getView().openSnapshotValueWindow(windowParameters);
    }

    public void prepareWindowLookUp() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.TESTCASE_ID, getTestStep().getTestCaseId());
        getView().openLookUpWindow(windowParameters);
    }

}
