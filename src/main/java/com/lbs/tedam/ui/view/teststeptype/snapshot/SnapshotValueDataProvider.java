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

package com.lbs.tedam.ui.view.teststeptype.snapshot;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.SnapshotValueService;
import com.lbs.tedam.exception.DifferencesSnapshotException;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.SnapshotDefinition;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.util.TedamListUtils;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.List;

@SpringComponent
@PrototypeScope
public class SnapshotValueDataProvider extends AbstractDataProvider<SnapshotValue> {

    private static final long serialVersionUID = 1L;

    private final SnapshotValueService snapshotValueService;
    private final PropertyService propertyService;

    @Autowired
    public SnapshotValueDataProvider(SnapshotValueService snapshotValueService, PropertyService propertyService) {
        this.snapshotValueService = snapshotValueService;
        this.propertyService = propertyService;
    }

    public void setSnapshotDefinition(TestStep testStep, SnapshotDefinition snapshotDefinition) throws LocalizedException {
        List<SnapshotValue> values;
        try {
            values = snapshotValueService.getSnapshotValuesFromFile("", propertyService.getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename(),
                    snapshotDefinition.getId());
            for (SnapshotValue snapshotValue : snapshotDefinition.getSnapshotValues()) {
                int index = TedamListUtils.getTagIndexInList(values, snapshotValue.getTag());
                if (index != -1) {
                    snapshotValue.setCaption(values.get(index).getCaption());
                } else {
                    continue;
                }
            }
            buildListDataProvider(snapshotDefinition.getSnapshotValues());
        } catch (DifferencesSnapshotException e) {
            e.printStackTrace();
        }
    }

    public void setFileForValues(TestStep testStep, SnapshotDefinition snapshotDefinition) throws LocalizedException {
        try {
            // TODO where only the fields are taken from the file. For non-snapshot areas
            // added with SnapshotCollector, you should also look at the old code to make improvements.
            List<SnapshotValue> values = snapshotValueService.getSnapshotValuesFromFile("", propertyService.getTestcaseFolder(testStep.getTestCaseId()) + testStep.getFilename(),
                    snapshotDefinition.getId());
            // Currently selected and added are cleared from the list.
            for (SnapshotValue snapshotValue : snapshotDefinition.getSnapshotValues()) {
                int index = TedamListUtils.getTagIndexInList(values, snapshotValue.getTag());
                if (index != -1) {
                    values.remove(index);
                }
            }
            buildListDataProvider(values);
        } catch (DifferencesSnapshotException e) {
            e.printStackTrace();
        }
    }
}