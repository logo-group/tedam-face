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

package com.lbs.tedam.ui.view.testset.edit;

import com.lbs.tedam.model.TestCase;
import com.lbs.tedam.model.TestCaseTestRun;
import com.lbs.tedam.model.TestSet;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.ArrayList;
import java.util.List;

@SpringComponent
@PrototypeScope
public class TestCaseTestRunDataProvider extends AbstractDataProvider<TestCaseTestRun> {

    private static final long serialVersionUID = 1L;

    public void setTestSet(TestSet testSet) {
        List<TestCaseTestRun> testCaseTestRunList = new ArrayList<>();
        for (TestCase testCase : testSet.getTestCasesOrdered()) {
            testCaseTestRunList.addAll(testCase.getTestCaseTestRunList());
        }
        buildListDataProvider(testCaseTestRunList);
    }

    public void setTestCase(TestCase testCase) {
        buildListDataProvider(testCase.getTestCaseTestRunList());
    }

}