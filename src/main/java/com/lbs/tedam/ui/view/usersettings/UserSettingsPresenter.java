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

package com.lbs.tedam.ui.view.usersettings;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.*;
import com.lbs.tedam.ui.TedamFaceEvents.ClientSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.EnvironmentSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.FavoriteEnvironmentSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.JobSelectEvent;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.user.UserGridView;
import com.lbs.tedam.ui.view.user.edit.UserFavoritesClientDataProvider;
import com.lbs.tedam.ui.view.user.edit.UserProjectDataProvider;
import com.lbs.tedam.util.EnumsV2.TedamUserFavoriteType;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringComponent
@ViewScope
public class UserSettingsPresenter extends AbstractEditPresenter<TedamUser, TedamUserService, UserSettingsPresenter, UserSettingsView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private final UserProjectDataProvider userProjectDataProvider;
    private final UserFavoritesClientDataProvider userFavoritesClientDataProvider;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserSettingsPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, TedamUserService userService, BeanFactory beanFactory,
                                 BCryptPasswordEncoder passwordEncoder, UserProjectDataProvider userProjectDataProvider, UserFavoritesClientDataProvider userFavoritesClientDataProvider,
                                 PropertyService propertyService) {
        super(viewEventBus, navigationManager, userService, TedamUser.class, beanFactory, userService, propertyService);
        this.passwordEncoder = passwordEncoder;
        this.userProjectDataProvider = userProjectDataProvider;
        this.userFavoritesClientDataProvider = userFavoritesClientDataProvider;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        TedamUser tedamUser = getService().getById((Integer) parameters.get(UIParameter.ID));
        ViewMode mode = (ViewMode) parameters.get(UIParameter.MODE);
        if (tedamUser == null) {
            getView().showNotFound();
            return;
        }
        checkAuthority(tedamUser);
        refreshView(tedamUser, mode);
        userProjectDataProvider.setTedamUser(tedamUser);
        userFavoritesClientDataProvider.setTedamUser(tedamUser);
        getView().organizeGrid(userProjectDataProvider);
        getView().organizeUserFavoritesGrid(userFavoritesClientDataProvider);
        organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    @EventBusListenerMethod
    public void clientSelectedEvent(ClientSelectEvent event) {
        List<TedamUserFavorite> list = new ArrayList<>();
        for (Client client : event.getClientList()) {
            TedamUserFavorite userFavorite = new TedamUserFavorite(TedamUserFavoriteType.CLIENT, client.getId());
            userFavorite.setClient(client);
            list.add(userFavorite);
        }
        userFavoritesClientDataProvider.getListDataProvider().getItems().addAll(list);
        userFavoritesClientDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    @EventBusListenerMethod
    public void clientSelectedEvent(JobSelectEvent event) {
        List<TedamUserFavorite> list = new ArrayList<>();
        for (Job job : event.getJobList()) {
            TedamUserFavorite userFavorite = new TedamUserFavorite(TedamUserFavoriteType.JOB, job.getId());
            userFavorite.setJob(job);
            list.add(userFavorite);
        }
        userFavoritesClientDataProvider.getListDataProvider().getItems().addAll(list);
        userFavoritesClientDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    @EventBusListenerMethod
    public void environmentSelectedEvent(EnvironmentSelectEvent event) {
        List<TedamUserFavorite> list = new ArrayList<>();
        Environment environment = event.getEnvironment();
        TedamUserFavorite userFavorite = new TedamUserFavorite(TedamUserFavoriteType.ENVIRONMENT, environment.getId());
        userFavorite.setEnvironment(environment);
        list.add(userFavorite);
        userFavoritesClientDataProvider.getListDataProvider().getItems().addAll(list);
        userFavoritesClientDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    @EventBusListenerMethod
    public void favoriteEnvironmentSelectedEvent(FavoriteEnvironmentSelectEvent event) {
        List<TedamUserFavorite> list = new ArrayList<>();
        for (Environment environment : event.getEnvironmentList()) {
            TedamUserFavorite userFavorite = new TedamUserFavorite(TedamUserFavoriteType.ENVIRONMENT, environment.getId());
            userFavorite.setEnvironment(environment);
            list.add(userFavorite);
        }
        userFavoritesClientDataProvider.getListDataProvider().getItems().addAll(list);
        userFavoritesClientDataProvider.getListDataProvider().refreshAll();
        setHasChanges(true);
    }

    public void removeUserFavorite(TedamUserFavorite userFavorite) {
        getView().getGridUserFavorites().getGridDataProvider().removeItem(userFavorite);
        getView().getGridUserFavorites().refreshAll();
        setHasChanges(true);
    }

    public void prepareClientAddWindow() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        List<Client> list = userFavoritesClientDataProvider.getListDataProvider().getItems().stream().map(userFavorite -> userFavorite.getClient()).collect(Collectors.toList());
        windowParameters.put(UIParameter.SELECTED_LIST, list);
        getView().openClientAddWindow(windowParameters);
    }

    public void prepareJobAddWindow() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        List<Job> list = userFavoritesClientDataProvider.getListDataProvider().getItems().stream().map(userFavorite -> userFavorite.getJob()).collect(Collectors.toList());
        windowParameters.put(UIParameter.SELECTED_LIST, list);
        getView().openJobAddWindow(windowParameters);
    }

    public void prepareEnvironmentAddWindow() throws LocalizedException {
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        List<Environment> list = userFavoritesClientDataProvider.getListDataProvider().getItems().stream().map(userFavorite -> userFavorite.getEnvironment()).collect(Collectors.toList());
        windowParameters.put(UIParameter.SELECTED_LIST, list);
        getView().openEnvironmentAddWindow(windowParameters);
    }

    public void setNewPassword(String value) {
        getBinder().getBean().setPassword(passwordEncoder.encode(value));
    }

    @Override
    protected Class<? extends View> getGridView() {
        return UserGridView.class;
    }

    private void checkAuthority(TedamUser tedamUser) throws LocalizedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        TedamUser user = getService().findByUserName(auth.getName());
        if (!user.getId().equals(tedamUser.getId())) {
            getView().showNotAuthorized();
            return;
        }
    }
}
