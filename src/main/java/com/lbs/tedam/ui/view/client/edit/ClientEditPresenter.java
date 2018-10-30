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

package com.lbs.tedam.ui.view.client.edit;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.ClientService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Client;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.client.ClientGridView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.Map;

@SpringComponent
@ViewScope
public class ClientEditPresenter extends AbstractEditPresenter<Client, ClientService, ClientEditPresenter, ClientEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    public ClientEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, ClientService clientService, TedamUserService userService, BeanFactory beanFactory,
                               PropertyService propertyService) {
        super(viewEventBus, navigationManager, clientService, Client.class, beanFactory, userService, propertyService);
    }

    @Override
    protected void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        Client client;
        Integer id = (Integer) windowParameters.get(UIParameter.ID);
        ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
        if (id == 0) {
            client = new Client();
            client.setProject(SecurityUtils.getUserSessionProject());
        } else {
            client = getService().getById(id);
            if (client == null) {
                getView().showNotFound();
                return;
            }
            isAuthorized(client);
        }
        refreshView(client, mode);
        organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
    }

    @Override
    protected Class<? extends View> getGridView() {
        return ClientGridView.class;
    }

    @Override
    protected Project getProjectByEntity(Client entity) {
        return entity.getProject();
    }

}
