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

package com.lbs.tedam.ui.components.window.lookup;

import com.lbs.tedam.data.service.TestCaseService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.List;

@SpringComponent
@PrototypeScope
public class WindowLookUpDataProvider extends AbstractDataProvider<TestStep> {

    private static final long serialVersionUID = 1L;
    private final TestCaseService testCaseService;
    private final BeanFactory beanFactory;

    @Autowired
    public WindowLookUpDataProvider(TestCaseService testCaseService, BeanFactory beanFactory) {
        this.testCaseService = testCaseService;
        this.beanFactory = beanFactory;
    }

    public void setTestCaseId(Integer testCaseId) throws LocalizedException {
        List<TestStep> lookUps = testCaseService.getById(testCaseId).getLookUps();

        for (TestStep testStep : lookUps) {
            testStep.setGenerator(GeneratorFactory.getGenerator(testStep.getType(), getBeanFactory()));
            testStep.getGenerator().degenerate(testStep.getParameter());
        }
        buildListDataProvider(lookUps);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

}