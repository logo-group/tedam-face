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

import com.lbs.tedam.data.service.BaseService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.AbstractBaseEntity;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import org.springframework.beans.factory.BeanFactory;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.io.Serializable;
import java.util.Map;

public abstract class AbstractGridPresenter<T extends AbstractBaseEntity, S extends BaseService<T, Integer>, P extends AbstractGridPresenter<T, S, P, V>, V extends AbstractGridView<T, S, P, V>>
        implements Serializable, HasLogger {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final transient S service;

    private final TedamUserService userService;
    private final NavigationManager navigationManager;
    private final PropertyService propertyService;
    private final BeanFactory beanFactory;
    private transient V view;
    private ViewEventBus viewEventBus;
    private AbstractDataProvider<T> dataProvider;

    protected AbstractGridPresenter(NavigationManager navigationManager, S service, AbstractDataProvider<T> dataProvider, BeanFactory beanFactory, ViewEventBus viewEventBus,
                                    PropertyService propertyService, TedamUserService userService) {
        this.beanFactory = beanFactory;
        this.service = service;
        this.dataProvider = dataProvider;
        this.navigationManager = navigationManager;
        this.viewEventBus = viewEventBus;
        this.propertyService = propertyService;
        this.userService = userService;
    }

    protected abstract void enterView(Map<UIParameter, Object> parameters);

    public void subscribeToEventBus() {
        viewEventBus.subscribe(this);
    }

    public void destroy() {
        viewEventBus.unsubscribe(this);
    }

    public void init(V view) {
        this.view = view;
    }

    public void beforeLeavingView(ViewBeforeLeaveEvent event) {
        destroy();
        runWithConfirmation(event::navigate, () -> {
        });
    }

    private void runWithConfirmation(Runnable onConfirmation, Runnable onCancel) {
        onConfirmation.run();
    }

    protected S getService() {
        return service;
    }

    public ViewEventBus getViewEventBus() {
        return viewEventBus;
    }

    public AbstractDataProvider<T> getDataPovider() {
        return dataProvider;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    protected void delete(T item) throws LocalizedException {
        getService().deleteByLogic(item.getId());
        dataProvider.removeItem(item);
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    protected void deleteSelectedLines(Iterable<T> entities) throws LocalizedException {
        for (T entity : entities) {
            delete(entity);
        }
    }

    public V getView() {
        return view;
    }

    public void setView(V view) {
        this.view = view;
    }

    public PropertyService getPropertyService() {
        return propertyService;
    }

    public TedamUserService getUserService() {
        return userService;
    }

}
