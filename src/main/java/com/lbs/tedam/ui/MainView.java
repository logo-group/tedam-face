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

package com.lbs.tedam.ui;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.layout.TedamCssLayout;
import com.lbs.tedam.ui.components.layout.TedamVerticalLayout;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.view.client.ClientGridView;
import com.lbs.tedam.ui.view.definedcommand.DraftCommandGridView;
import com.lbs.tedam.ui.view.environment.EnvironmentGridView;
import com.lbs.tedam.ui.view.job.JobGridView;
import com.lbs.tedam.ui.view.jobmanager.TedamManagerView;
import com.lbs.tedam.ui.view.jobparameter.JobParameterGridView;
import com.lbs.tedam.ui.view.project.ProjectGridView;
import com.lbs.tedam.ui.view.settings.SettingsView;
import com.lbs.tedam.ui.view.singlecommand.SingleCommandGridView;
import com.lbs.tedam.ui.view.testcase.TestCaseGridView;
import com.lbs.tedam.ui.view.testset.TestSetGridView;
import com.lbs.tedam.ui.view.user.UserGridView;
import com.lbs.tedam.ui.view.usersettings.UserSettingsView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewLeaveAction;
import com.vaadin.spring.access.SecuredViewAccessControl;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * The main view containing the menu and the content area where actual views are shown.
 * <p>
 * Created as a single View class because the logic is so simple that using a pattern like MVP would add much overhead for little gain. If more complexity is added to the class,
 * you should consider splitting out a presenter.
 */
@SpringViewDisplay
@UIScope
public class MainView extends HorizontalLayout implements ViewDisplay, TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;
    private final Map<Class<? extends View>, Button> navigationButtons = new HashMap<>();

    private final NavigationManager navigationManager;
    private final SecuredViewAccessControl viewAccessControl;
    private final TedamUserService userService;

    private TedamButton users;
    private TedamButton projects;
    private TedamButton clients;
    private TedamButton environment;
    private TedamButton jobparameters;
    private TedamButton testcases;
    private TedamButton jobManager;
    private TedamButton testsets;
    private TedamButton jobs;
    private TedamButton settings;
    private TedamButton draftCommands;
    private TedamButton singleCommands;
    private TedamButton userSettings;
    private TedamButton logout;
    private TedamVerticalLayout content;
    private TedamCssLayout menu;

    @Autowired
    public MainView(NavigationManager navigationManager, SecuredViewAccessControl viewAccessControl, TedamUserService userService) {
        this.navigationManager = navigationManager;
        this.viewAccessControl = viewAccessControl;
        this.userService = userService;
    }

    @PostConstruct
    public void init() throws LocalizedException {
        initComponents();
        attachNavigation(userSettings, UserSettingsView.class, SecurityUtils.getCurrentUser(userService).getTedamUser().getId());
        attachNavigation(jobs, JobGridView.class, "");
        attachNavigation(testsets, TestSetGridView.class, "");
        attachNavigation(testcases, TestCaseGridView.class, "");
        attachNavigation(environment, EnvironmentGridView.class, "");
        attachNavigation(jobparameters, JobParameterGridView.class, "");
        attachNavigation(jobManager, TedamManagerView.class, "");
        attachNavigation(users, UserGridView.class, "");
        attachNavigation(projects, ProjectGridView.class, "");
        attachNavigation(draftCommands, DraftCommandGridView.class, "");
        attachNavigation(singleCommands, SingleCommandGridView.class, "");
        attachNavigation(clients, ClientGridView.class, "");
        attachNavigation(settings, SettingsView.class, "");

    }

    private void initComponents() throws LocalizedException {
        setStyleName("app-shell");
        setSpacing(false);
        setSizeFull();
        setResponsive(true);

        content = new TedamVerticalLayout();
        content.setStyleName("content-container v-scrollable");
        content.setSizeFull();
        content.setMargin(false);

        addComponent(buildNavigationBar());
        addComponent(content);
        setExpandRatio(content, 1);

    }

    private TedamCssLayout buildNavigationBar() throws LocalizedException {
        TedamCssLayout navigationContainer = new TedamCssLayout();
        navigationContainer.setStyleName("navigation-bar-container");
        navigationContainer.setWidth("200px");
        navigationContainer.setHeight("100%");
        navigationContainer.addComponent(buildNavigation());
        return navigationContainer;
    }

    private TedamCssLayout buildNavigation() throws LocalizedException {
        TedamCssLayout navigation = new TedamCssLayout();
        navigation.setStyleName("navigation-bar");
        navigation.setSizeFull();

        TedamButton menuButton = new TedamButton("general.button.menu");
        menuButton.setIcon(VaadinIcons.ALIGN_JUSTIFY);
        menuButton.setStyleName("menu borderless");
        menuButton.setWidthUndefined();
        navigation.setWidthUndefined();
        navigation.addComponents(buildHeader(), buildVersion(), menuButton, buildMenu());

        return navigation;
    }

    private Component buildHeader() {
        TedamLabel header = new TedamLabel();
        header.addStyleName("logo");
        header.setWidth("100%");
        header.setValue(getLocaleValue("view.mainview.header"));
        return header;
    }

    private Component buildVersion() {
        TedamLabel version = new TedamLabel();
        version.addStyleName("logo");
        version.setWidth("100%");
        version.setValue(getLocaleValue("tedam.version"));
        version.setId("tedam.version");
        return version;
    }

    private Component buildMenu() throws LocalizedException {
        menu = new TedamCssLayout();
        menu.setStyleName("navigation");

        TedamLabel userLabel = new TedamLabel(SecurityUtils.getCurrentUser(userService).getTedamUser().getUserName());
        userLabel.setStyleName("menuLabel");
        userLabel.setWidth("100%");

        TedamLabel jobRunnerLabel = new TedamLabel(getLocaleValue("view.mainview.label.jobrunner"));
        jobRunnerLabel.setStyleName("menuLabel");
        jobRunnerLabel.setWidth("100%");

        TedamLabel scenarioLabel = new TedamLabel(getLocaleValue("view.mainview.label.scenario"));
        scenarioLabel.setStyleName("menuLabel");
        scenarioLabel.setWidth("100%");

        users = new TedamButton("view.mainview.usersview", VaadinIcons.USERS);
        users.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        jobs = new TedamButton("view.mainview.jobview", VaadinIcons.AUTOMATION);
        jobs.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        jobManager = new TedamButton("view.mainview.jobmanagerview", VaadinIcons.CONTROLLER);
        jobManager.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        clients = new TedamButton("view.mainview.clientsview", VaadinIcons.USER_STAR);
        clients.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        testcases = new TedamButton("view.mainview.testcaseview", VaadinIcons.FILE);
        testcases.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        testsets = new TedamButton("view.mainview.testsetview", VaadinIcons.FOLDER);
        testsets.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        environment = new TedamButton("view.mainview.environmentview", VaadinIcons.BRIEFCASE);
        environment.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        draftCommands = new TedamButton("view.mainview.draftcommandview", VaadinIcons.CODE);
        draftCommands.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        singleCommands = new TedamButton("view.mainview.singlecommandview", VaadinIcons.CONNECT);
        singleCommands.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        jobparameters = new TedamButton("view.mainview.jobparameterview", VaadinIcons.FORM);
        jobparameters.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        projects = new TedamButton("view.mainview.projectsview", VaadinIcons.TABS);
        projects.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        settings = new TedamButton("view.mainview.settingsview", VaadinIcons.AUTOMATION);
        settings.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        userSettings = new TedamButton("view.mainview.usersettingsview", VaadinIcons.USER);
        userSettings.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        logout = new TedamButton("view.mainview.logout", VaadinIcons.EXIT);
        logout.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        logout.addClickListener(e -> logout());

        menu.addComponents(userLabel, userSettings, users, projects, settings, logout, scenarioLabel, testsets, testcases, jobRunnerLabel, singleCommands, draftCommands,
                jobparameters, environment, clients, jobs, jobManager);
        return menu;
    }

    /**
     * Makes clicking the given button navigate to the given view if the user has access to the view.
     * <p>
     * If the user does not have access to the view, hides the button.
     *
     * @param navigationButton the button to use for navigatio
     * @param targetView       the view to navigate to when the user clicks the button
     */
    private void attachNavigation(Button navigationButton, Class<? extends View> targetView, Object parameter) {
        boolean hasAccessToView = viewAccessControl.isAccessGranted(targetView);
        navigationButton.setVisible(hasAccessToView);

        if (hasAccessToView) {
            navigationButtons.put(targetView, navigationButton);
            navigationButton.addClickListener(e -> navigationManager.navigateTo(targetView, parameter));
        }
    }

    @Override
    public void showView(View view) {
        content.removeAllComponents();
        content.addComponent(view.getViewComponent());
        navigationButtons.forEach((viewClass, button) -> button.setStyleName("selected", viewClass == view.getClass()));
    }

    /**
     * Logs the user out after ensuring the currently open view has no unsaved changes.
     */
    public void logout() {
        ViewLeaveAction doLogout = () -> {
            UI ui = getUI();
            ui.getSession().getSession().invalidate();
            ui.getPage().reload();
        };

        navigationManager.runAfterLeaveConfirmation(doLogout);
    }

}
