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

package com.lbs.tedam.ui.view.singlecommand.edit;

import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.SingleCommandService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.SingleCommand;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.singlecommand.SingleCommandGridView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class SingleCommandEditPresenter extends AbstractEditPresenter<SingleCommand, SingleCommandService, SingleCommandEditPresenter, SingleCommandEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    public SingleCommandEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, SingleCommandService singleCommandService, TedamUserService userService,
                                      BeanFactory beanFactory, PropertyService propertyService) {
        super(viewEventBus, navigationManager, singleCommandService, SingleCommand.class, beanFactory, userService, propertyService);
    }

    @Override
    protected void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        SingleCommand singleCommand;
        Integer id = (Integer) windowParameters.get(UIParameter.ID);
        ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
        if (id == 0) {
            singleCommand = new SingleCommand();
            singleCommand.setProject(SecurityUtils.getUserSessionProject());
        } else {
            singleCommand = getService().getById(id);
            if (singleCommand == null) {
                getView().showNotFound();
                return;
            }
        }
        refreshView(singleCommand, mode);
		getTitleForHeader();
    }

    @Override
    protected Class<? extends View> getGridView() {
        return SingleCommandGridView.class;
    }

	@Override
	protected void getTitleForHeader() {
		if (getItem().getName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getName());
		}
	}

}
