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

package com.lbs.tedam.ui.view.user.edit;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.ProjectService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.ui.TedamFaceEvents.ProjectEvent;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.user.UserGridView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class UserEditPresenter extends AbstractEditPresenter<TedamUser, TedamUserService, UserEditPresenter, UserEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private final UserProjectDataProvider userProjectDataProvider;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, TedamUserService userService, BeanFactory beanFactory,
                             BCryptPasswordEncoder passwordEncoder, UserProjectDataProvider projectDataProvider, ProjectService projectService, PropertyService propertyService) {
        super(viewEventBus, navigationManager, userService, TedamUser.class, beanFactory, userService, propertyService);
        this.passwordEncoder = passwordEncoder;
        this.userProjectDataProvider = projectDataProvider;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        TedamUser tedamUser;
        if ((Integer) parameters.get(UIParameter.ID) == 0) {
            tedamUser = new TedamUser();
            tedamUser.getProjects().add(SecurityUtils.getUserSessionProject());
        } else {
            tedamUser = getService().getById((Integer) parameters.get(UIParameter.ID));
            if (tedamUser == null) {
                getView().showNotFound();
                return;
            }
        }
        refreshView(tedamUser, (ViewMode) parameters.get(UIParameter.MODE));
        userProjectDataProvider.setTedamUser(tedamUser);
        getView().organizeProjectsGrid(userProjectDataProvider);
		getTitleForHeader();
        organizeComponents(getView().getAccordion(), (ViewMode) parameters.get(UIParameter.MODE) == ViewMode.VIEW);
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    @EventBusListenerMethod
    public void projectSelectedEvent(ProjectEvent event) {
        userProjectDataProvider.getListDataProvider().getItems().addAll(event.getProjectList());
        userProjectDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void removeProject(Project project) {
        TedamUser user = getItem();
        user.getProjects().remove(project);
        userProjectDataProvider.removeItem(project);
        userProjectDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void prepareProjectAddWindow() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.SELECTED_LIST, new ArrayList<Project>(userProjectDataProvider.getListDataProvider().getItems()));
        getView().openProjectAddWindow(windowParameters);
    }

    public void setNewPassword(String value) {
        getBinder().getBean().setPassword(passwordEncoder.encode(value));
    }

    @Override
    protected Class<? extends View> getGridView() {
        return UserGridView.class;
    }

	@Override
	protected void getTitleForHeader() {
		if (getItem().getUserName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getUserName());
		}
	}

}
