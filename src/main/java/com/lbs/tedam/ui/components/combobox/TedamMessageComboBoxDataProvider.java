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
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.util.TedamDOMUtils;
import com.lbs.tedam.util.TedamXPathUtils;
import com.vaadin.spring.annotation.SpringComponent;
import org.vaadin.spring.annotation.PrototypeScope;
import org.w3c.dom.Document;

import java.util.List;

@SpringComponent
@PrototypeScope
public class TedamMessageComboBoxDataProvider extends AbstractDataProvider<String> implements TedamLocalizerWrapper {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final PropertyService propertyService;

    public TedamMessageComboBoxDataProvider(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    public void setTestStep(TestStep testStep) throws TedamWindowNotAbleToOpenException, LocalizedException {
        Document doc = TedamDOMUtils
                .domParserStarter(propertyService.getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename());
        // TODO What is 4?
        List<String> messages = TedamXPathUtils.getMessages(doc.getDocumentElement(), 4);
        if (messages != null && messages.size() > 0) {
            buildListDataProvider(messages);
        } else {
            throw new TedamWindowNotAbleToOpenException(
                    getLocaleValue("component.TedamMessageComboBox.message.showMessageVerifyCanNotOpen"));
        }

    }

}
