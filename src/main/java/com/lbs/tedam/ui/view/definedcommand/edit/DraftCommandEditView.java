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

package com.lbs.tedam.ui.view.definedcommand.edit;

import com.lbs.tedam.data.service.DraftCommandService;
import com.lbs.tedam.model.DraftCommand;
import com.lbs.tedam.model.JobParameter;
import com.lbs.tedam.ui.components.basic.TedamListSelect;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamRunOrderComboBox;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.lbs.tedam.util.EnumsV2.JobParameterType;
import com.lbs.tedam.util.EnumsV2.StaticJobParameter;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class DraftCommandEditView extends AbstractEditView<DraftCommand, DraftCommandService, DraftCommandEditPresenter, DraftCommandEditView> {

    private static final long serialVersionUID = 1L;

    private TedamTextField name;
    private TedamTextField windowsValue;
    private TedamTextField unixValue;
    private TedamTextField firstExpectedResult;
    private TedamTextField lastExpectedResult;
    private TedamRunOrderComboBox runOrder;

    private TedamListSelect<JobParameter> listJobParameters;
    private TedamListSelect<StaticJobParameter> listStaticJobParameters;

    @Autowired
    public DraftCommandEditView(DraftCommandEditPresenter presenter, TedamRunOrderComboBox runOrder) {
        super(presenter);
        this.runOrder = runOrder;
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.draftcommandedit.header");
    }

    @PostConstruct
    private void initView() {
        name = new TedamTextField("view.draftcommandedit.textfield.name", "half", true, true);
        windowsValue = new TedamTextField("view.draftcommandedit.textfield.windowsValue", "full", true, true);
        unixValue = new TedamTextField("view.draftcommandedit.textfield.unixValue", "full", true, true);
        firstExpectedResult = new TedamTextField("view.draftcommandedit.textfield.firstExpectedResult", "half", true, true);
        lastExpectedResult = new TedamTextField("view.draftcommandedit.textfield.lastExpectedResult", "half", true, true);

        buildListJobParameters();
        buildListStaticJobParameters();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name, windowsValue, unixValue, firstExpectedResult, lastExpectedResult, runOrder);
        addSection(getLocaleValue("view.viewedit.section.values"), 1, null, listJobParameters, listStaticJobParameters);

        getPresenter().setView(this);
    }

    private void buildListStaticJobParameters() {
        listStaticJobParameters = new TedamListSelect<>("view.draftcommandedit.list.staticjobparameters");
        listStaticJobParameters.setCaption(listStaticJobParameters.getCaption() + " " + JobParameterType.STATIC.getSign());
        listStaticJobParameters.setItems(StaticJobParameter.class.getEnumConstants());
        listStaticJobParameters.setReadOnly(true);
        listStaticJobParameters.setStyleName("half");
    }

    private void buildListJobParameters() {
        listJobParameters = new TedamListSelect<>("view.draftcommandedit.list.jobparameters");
        listJobParameters.setCaption(listJobParameters.getCaption() + " " + JobParameterType.CONSTANT.getSign());
        listJobParameters.setReadOnly(true);
        listJobParameters.setStyleName("half");
    }

    public TedamListSelect<JobParameter> getListJobParameters() {
        return listJobParameters;
    }

    @Override
    public void bindFormFields(BeanValidationBinder<DraftCommand> binder) {
        super.bindFormFields(binder);
        binder.forField(runOrder).asRequired().bind("runOrder");
    }

}
