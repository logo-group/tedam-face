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

package com.lbs.tedam.ui.view;

import com.lbs.tedam.data.service.FormDefinitionService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.SnapshotDefinitionService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.AbstractFillTestStepGenerator;
import com.lbs.tedam.model.DTO.FormName;
import com.lbs.tedam.model.FormDefinition;
import com.lbs.tedam.model.SnapshotDefinition;
import com.lbs.tedam.model.SnapshotValue;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.view.teststeptype.snapshot.SnapshotValueDataProvider;
import com.lbs.tedam.util.Enums.Regex;
import com.lbs.tedam.util.TedamDOMUtils;
import com.lbs.tedam.util.TedamXPathUtils;
import com.vaadin.navigator.View;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathExpressionException;

public abstract class AbstractFillTestStepEditPresenter<P extends AbstractEditPresenter<SnapshotDefinition, SnapshotDefinitionService, P, V>, V extends AbstractEditView<SnapshotDefinition, SnapshotDefinitionService, P, V>>
        extends AbstractEditPresenter<SnapshotDefinition, SnapshotDefinitionService, P, V> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private final SnapshotValueDataProvider snapshotValueDataProvider;
    private final FormDefinitionService formDefinitionService;
    private TestStep testStep;
    private SnapshotDefinition tempSnapshotDefinition;

    public AbstractFillTestStepEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, SnapshotDefinitionService service,
                                             Class<SnapshotDefinition> entityType, BeanFactory beanFactory, TedamUserService userService, SnapshotValueDataProvider snapshotValueDataProvider,
                                             FormDefinitionService formDefinitionService, PropertyService propertyService) {
        super(viewEventBus, navigationManager, service, entityType, beanFactory, userService, propertyService);
        this.snapshotValueDataProvider = snapshotValueDataProvider;
        this.formDefinitionService = formDefinitionService;
    }

    public SnapshotDefinition buildSnapshotDefinitionEntity() throws LocalizedException {
        SnapshotDefinition snapshotDefinition = createSnapshotDefinitionFromParameter();
        tempSnapshotDefinition = snapshotDefinition.cloneSnapshotDefinitionWithId();
        snapshotDefinition.setDefinitionType(generateSnapshotDefinitionType());
        if (!snapshotDefinition.isNew()) {
            return snapshotDefinition;
        }
        if (!StringUtils.isEmpty(getTestStep().getFilename())) {
            Document doc = TedamDOMUtils.domParserStarter(getPropertyService().getTestcaseFolder(getTestStep().getTestCaseId()) + getTestStep().getFilename());
            FormName formName = TedamXPathUtils.getFormNameAndMode(doc.getDocumentElement());
            FormDefinition formDef = formDefinitionService.getFormDefByNameAndMode(formName.getName(), formName.getMode());

            if (formDef != null && formDef.getId() != 0) {
                snapshotDefinition.setFormDefinition(formDef);
            } else {
                try {
                    snapshotDefinition.setFormDefinition(formDefinitionService.saveUpdateFormContent(doc.getDocumentElement(), false));
                } catch (XPathExpressionException e) {
                    getLogger().error(e.getMessage());
                }
            }
        }
        return snapshotDefinition;
    }

    private AbstractFillTestStepGenerator getAbstractFillTestStepGenerator() {
        return (AbstractFillTestStepGenerator) getTestStep().getGenerator();
    }

    public SnapshotDefinition createSnapshotDefinitionFromParameter() throws LocalizedException {
        String parameter = findTestStepParameter();
        if (StringUtils.isEmpty(parameter)) {
            SnapshotDefinition snapshotDefinition = new SnapshotDefinition();
            snapshotDefinition.setDescription("");
            return snapshotDefinition;
        } else {
            return getAbstractFillTestStepGenerator().getSnapshotDefinition();
        }
    }

    private String findTestStepParameter() {
        String parameter = null;
        if (!StringUtils.isEmpty(getTestStep().getParameter())) {
            // Although formfill and filterfill are not required, they have been added to verify
            // as needed. He does not bother the others.
            String[] split = getTestStep().getParameter().split(Regex.PARAMETER_SPLITTER.getRegex());
            parameter = split[0];
        }
        return parameter;
    }

    public SnapshotValueDataProvider getSnapshotValueDataProvider() {
        return snapshotValueDataProvider;
    }

    public String generateSnapshotDefinitionType() {
        return String.format("TC%06d", testStep.getTestCaseId()) + (testStep.isLookUp() ? "-LookUp" : "") + "-" + testStep.getType().getValue() + "-";
    }

    public TestStep getTestStep() {
        return testStep;
    }

    public void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public void removeRow(SnapshotValue snapshotValue) {
        SnapshotDefinition snapshotDefinition = getBinder().getBean();
        snapshotDefinition.getSnapshotValues().remove(snapshotValue);
        snapshotValueDataProvider.removeItem(snapshotValue);
        snapshotValueDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void removeLookUpParameter(SnapshotValue snapshotValue) {
        snapshotValue.setLookUpParameter("");
        snapshotValueDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void resetSnapshotDefinition() throws LocalizedException {
        getAbstractFillTestStepGenerator().setSnapshotDefinition(tempSnapshotDefinition);
    }

    @Override
    protected Class<? extends View> getGridView() {
        return null;
    }

}
