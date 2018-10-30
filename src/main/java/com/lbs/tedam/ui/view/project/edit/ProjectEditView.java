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

package com.lbs.tedam.ui.view.project.edit;

import com.lbs.tedam.data.service.ProjectService;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class ProjectEditView extends AbstractEditView<Project, ProjectService, ProjectEditPresenter, ProjectEditView> {

    private static final long serialVersionUID = 1L;

    private TedamTextField name;
    private TedamTextField description;

    @Autowired
    public ProjectEditView(ProjectEditPresenter presenter) {
        super(presenter);
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.projectedit.header");
    }

    @PostConstruct
    private void initView() {
        name = new TedamTextField("view.projectedit.textfield.name", "full", true, true);
        description = new TedamTextField("view.projectedit.textfield.description", "full", true, true);

        addSection(getLocaleValue("view.viewedit.section.values"), 0, null, name, description);

        getPresenter().setView(this);
    }

}
