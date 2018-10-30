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

package com.lbs.tedam.ui.components.window.teststep.wait;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.WaitGenerator;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractWindowPresenter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import java.util.Map;

@SpringComponent
@ViewScope
public class WaitWindowPresenter extends AbstractWindowPresenter<WindowTestStepTypeWait> {

    private static final long serialVersionUID = 1L;

    private TestStep testStep;

    public WaitWindowPresenter(PropertyService propertyService) {
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
        WaitGenerator generator = (WaitGenerator) testStep.getGenerator();
        generator.degenerate(testStep.getParameter());

        if (generator.getWaitSleepMillis() != null) {
            getWindow().getTfWaitSleepMillis().setValue(String.valueOf(generator.getWaitSleepMillis()));
        }
    }

}
