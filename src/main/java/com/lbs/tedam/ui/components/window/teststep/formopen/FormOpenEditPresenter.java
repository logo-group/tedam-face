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

package com.lbs.tedam.ui.components.window.teststep.formopen;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.FormOpenGenerator;
import com.lbs.tedam.model.MenuPath;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractWindowPresenter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

;

@SpringComponent
@ViewScope
public class FormOpenEditPresenter extends AbstractWindowPresenter<WindowTestStepTypeFormOpen> {

    private static final long serialVersionUID = 1L;
    private TestStep testStep;

    @Autowired
    public FormOpenEditPresenter(PropertyService propertyService) {
        super(propertyService);
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
        if (!StringUtils.isEmpty(testStep.getParameter())) {
            testStep.getGenerator().degenerate(testStep.getParameter());
            List<MenuPath> menuPathList = ((FormOpenGenerator) testStep.getGenerator()).getMenuPathList();
            getWindow().getTreeMenuPath().expand(menuPathList);
            getWindow().getTreeMenuPath().select(menuPathList.get(menuPathList.size() - 1));
        }
    }

}
