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
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.project.ProjectGridView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.Map;

@SpringComponent
@ViewScope
public class ProjectEditPresenter extends AbstractEditPresenter<Project, ProjectService, ProjectEditPresenter, ProjectEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    public ProjectEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, ProjectService projectService, TedamUserService userService,
                                BeanFactory beanFactory, PropertyService propertyService) {
        super(viewEventBus, navigationManager, projectService, Project.class, beanFactory, userService, propertyService);
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        Project project;
        if ((Integer) parameters.get(UIParameter.ID) == 0) {
            project = new Project();
        } else {
            project = getService().getById((Integer) parameters.get(UIParameter.ID));
            if (project == null) {
                getView().showNotFound();
                return;
            }
        }
        refreshView(project, (ViewMode) parameters.get(UIParameter.MODE));
    }

    @Override
    protected Class<? extends View> getGridView() {
        return ProjectGridView.class;
    }

}
