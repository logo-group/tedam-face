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

package com.lbs.tedam.ui.view.testcase.edit;

import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.Generator;
import com.lbs.tedam.generator.steptype.GeneratorFactory;
import com.lbs.tedam.model.TestCase;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

@SpringComponent
@PrototypeScope
public class LookUpDataProvider extends AbstractDataProvider<TestStep> {
    private static final long serialVersionUID = 1L;
    private BeanFactory beanFactory;

    @Autowired
    public LookUpDataProvider(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void provideLookUps(TestCase testCase) throws LocalizedException {
        for (TestStep testStep : testCase.getLookUps()) {
            if (testStep.getType() == null || testStep.getParameter() == null) {
                continue;
            }
            // TODO as if it were meaningless
            Generator generator = GeneratorFactory.getGenerator(testStep.getType(), beanFactory);
            if (generator != null) {
                generator.degenerate(testStep.getParameter());
                testStep.setGenerator(generator);
            }
        }
        buildListDataProvider(testCase.getLookUps());
    }

}