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

package com.lbs.tedam.ui.view.testset;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.TestSetService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.model.TestSet;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

@SpringComponent
@PrototypeScope
public class TestSetsDataProvider extends AbstractDataProvider<TestSet> {

    private static final long serialVersionUID = 1L;

    private TestSetService testSetService;

    @Autowired
    public TestSetsDataProvider(TestSetService testSetService) throws LocalizedException {
        this.testSetService = testSetService;
        buildListDataProvider(testSetService.getTestSetListWithJobIdByProject(SecurityUtils.getUserSessionProject()));
    }

    public void buildDataProvider() throws LocalizedException {
        refreshDataProviderByItems(testSetService.getTestSetListWithJobIdByProject(SecurityUtils.getUserSessionProject()));
    }

    public void setTedamFolder(TedamFolder tedamFolder) throws LocalizedException {
        refreshDataProviderByItems(testSetService.getTestSetListWithJobIdByProjectAndFolder(SecurityUtils.getUserSessionProject(), tedamFolder));
    }

}