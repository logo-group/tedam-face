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

package com.lbs.tedam.ui.view.environment.edit;

import com.lbs.tedam.data.service.EnvironmentService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Environment;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamJobParameterValueComboBox;
import com.lbs.tedam.ui.components.layout.TedamVerticalLayout;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringView
public class EnvironmentEditView extends AbstractEditView<Environment, EnvironmentService, EnvironmentEditPresenter, EnvironmentEditView> {

    private static final long serialVersionUID = 1L;

    private TedamTextField name;
    private TedamVerticalLayout tedamJobParameterValueComboBoxContainer;

    @Autowired
    public EnvironmentEditView(EnvironmentEditPresenter presenter) {
        super(presenter);
    }

    @PostConstruct
    private void initView() {
        name = new TedamTextField("view.environmentedit.textfield.name", "half", true, true);
        tedamJobParameterValueComboBoxContainer = new TedamVerticalLayout();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name);
        addSection(getLocaleValue("view.environmentedit.section.parameters"), 1, null, tedamJobParameterValueComboBoxContainer);

        getPresenter().setView(this);
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.environmentedit.header");
    }

    public void buildTedamJobParameterValueComboBoxContainer(Environment environment) throws LocalizedException {
        tedamJobParameterValueComboBoxContainer.removeAllComponents();
        List<JobParameter> jobParameters = getPresenter().getActiveJobParameters();
        for (JobParameter jobParameter : jobParameters) {
            TedamJobParameterValueComboBox jobParameterValueComboBox = createTedamJobParameterValueComboBox(environment, jobParameter);
            tedamJobParameterValueComboBoxContainer.addComponent(jobParameterValueComboBox);
        }
    }

    public TedamVerticalLayout getTedamJobParameterValueComboBoxContainer() {
        return tedamJobParameterValueComboBoxContainer;
    }

    public void showSameEnvironmentIsExist() {
        TedamNotification.showNotification(getLocaleValue("view.environmentedit.messages.showSameEnvironmentIsExist"), NotifyType.ERROR);
    }

    public void showParametersEmpty() {
        TedamNotification.showNotification(getLocaleValue("view.environmentedit.messages.showParametersEmpty"), NotifyType.ERROR);
    }

    private TedamJobParameterValueComboBox createTedamJobParameterValueComboBox(Environment environment, JobParameter jobParameter) {
        TedamJobParameterValueComboBox tedamJobParameterValueComboBox = getPresenter().getBeanFactory().getBean(TedamJobParameterValueComboBox.class);
        // TODO: should be checked
        getPresenter().fillTedamJobParameterValueComboBox(tedamJobParameterValueComboBox, environment, jobParameter);
        return tedamJobParameterValueComboBox;
    }

}
