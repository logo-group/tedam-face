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

package com.lbs.tedam.ui.components.combobox;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.util.TedamXPathUtils;
import com.vaadin.spring.annotation.SpringComponent;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.List;

@SpringComponent
@PrototypeScope
public class TedamPopUpComboBoxDataProvider extends AbstractDataProvider<String> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final PropertyService propertyService;

    public TedamPopUpComboBoxDataProvider(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    public void setTestStep(TestStep testStep) throws LocalizedException {
        List<String> popUpItems = TedamXPathUtils
                .getPopupItemList(propertyService.getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename());
        buildListDataProvider(popUpItems);
    }

}
