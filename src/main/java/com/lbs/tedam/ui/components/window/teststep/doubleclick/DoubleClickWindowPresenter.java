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

package com.lbs.tedam.ui.components.window.teststep.doubleclick;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.DoubleClickGenerator;
import com.lbs.tedam.model.DTO.DoubleClick;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractWindowPresenter;
import com.lbs.tedam.util.TedamDOMUtils;
import com.lbs.tedam.util.TedamXPathUtils;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

;

@SpringComponent
@ViewScope
public class DoubleClickWindowPresenter extends AbstractWindowPresenter<WindowTestStepTypeDoubleClick> {

    private static final long serialVersionUID = 1L;

    private TestStep testStep;

    @Autowired
    public DoubleClickWindowPresenter(PropertyService propertyService) {
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
        DoubleClickGenerator generator = (DoubleClickGenerator) testStep.getGenerator();

        generator.degenerate(testStep.getParameter());
        String itemTag = generator.getItemTag();
        List<DoubleClick> doubleClickList = createDoubleClickList(testStep);
        getWindow().createMenuBar(itemTag, doubleClickList);
    }

    private List<DoubleClick> createDoubleClickList(TestStep testStep) throws LocalizedException {
        Document doc = TedamDOMUtils.domParserStarter(
                getPropertyService().getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename());
        List<DoubleClick> doubleClickList = TedamXPathUtils.getDoubleClickList(doc.getDocumentElement());
        return doubleClickList;
    }

}
