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
import com.lbs.tedam.generator.steptype.PopupGenerator;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.components.combobox.TedamGridTagComboBoxDataProvider;
import com.lbs.tedam.ui.components.combobox.TedamPopUpComboBoxDataProvider;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractWindowPresenter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

;

@SpringComponent
@ViewScope
public class PopupWindowPresenter extends AbstractWindowPresenter<WindowTestStepTypePopup> {

    private static final long serialVersionUID = 1L;

    private final TedamGridTagComboBoxDataProvider tedamGridTagComboBoxDataProvider;
    private final TedamPopUpComboBoxDataProvider tedamPopUpComboBoxDataProvider;
    private TestStep testStep;

    @Autowired
    public PopupWindowPresenter(TedamGridTagComboBoxDataProvider tedamGridComboBoxDataProvider, TedamPopUpComboBoxDataProvider tedamPopUpComboBoxDataProvider,
                                PropertyService propertyService) {
        super(propertyService);
        this.tedamGridTagComboBoxDataProvider = tedamGridComboBoxDataProvider;
        this.tedamPopUpComboBoxDataProvider = tedamPopUpComboBoxDataProvider;
    }

    private void buildTedamGridComboBoxDataProvider() throws LocalizedException {
        tedamGridTagComboBoxDataProvider.setTestStep(testStep);
        getWindow().getCbGridTags().setValue(null);
        getWindow().getCbGridTags().setDataProvider(tedamGridTagComboBoxDataProvider.getListDataProvider());
    }

    private void buildTedamPopUpComboBoxDataProvider() throws LocalizedException {
        tedamPopUpComboBoxDataProvider.setTestStep(testStep);
        getWindow().getCbPopupItems().setValue(null);
        getWindow().getCbPopupItems().setDataProvider(tedamPopUpComboBoxDataProvider.getListDataProvider());
    }

    public void enterView(Map<UIParameter, Object> parameters) {
        setTestStep((TestStep) parameters.get(UIParameter.TESTSTEP));
    }

    public TestStep getTestStep() {
        return testStep;
    }

    private void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    @Override
    public void fillComponentsWithValues() throws LocalizedException {
        buildTedamPopUpComboBoxDataProvider();
        buildTedamGridComboBoxDataProvider();

        PopupGenerator generator = (PopupGenerator) testStep.getGenerator();
        generator.degenerate(testStep.getParameter());

        if (generator.getGridTag() != null) {
            getWindow().getCbGridTags().setValue(generator.getGridTag());
        }
        if (generator.getPopupItem() != null) {
            getWindow().getCbPopupItems().setValue(generator.getPopupItem());
        }
    }
}
