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

import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.DraftCommandService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.DraftCommand;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.lbs.tedam.ui.view.definedcommand.DraftCommandGridView;
import com.lbs.tedam.ui.view.jobparameter.JobParametersDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class DraftCommandEditPresenter extends AbstractEditPresenter<DraftCommand, DraftCommandService, DraftCommandEditPresenter, DraftCommandEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final JobParametersDataProvider jobParametersDataProvider;

    @Autowired
    public DraftCommandEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, DraftCommandService draftCommandService, TedamUserService userService,
                                     BeanFactory beanFactory, JobParametersDataProvider jobParametersDataProvider, PropertyService propertyService) {
        super(viewEventBus, navigationManager, draftCommandService, DraftCommand.class, beanFactory, userService, propertyService);
        this.jobParametersDataProvider = jobParametersDataProvider;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        DraftCommand draftCommand;
        Integer id = (Integer) windowParameters.get(UIParameter.ID);
        ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
        if (id == 0) {
            draftCommand = new DraftCommand();
            draftCommand.setProject(SecurityUtils.getUserSessionProject());
        } else {
            draftCommand = getService().getById(id);
            if (draftCommand == null) {
                getView().showNotFound();
                return;
            }
        }
        refreshView(draftCommand, mode);
        getView().getListJobParameters().setItems(jobParametersDataProvider.getListDataProvider().getItems());
		getTitleForHeader();
        organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
    }

    @Override
    protected Class<? extends View> getGridView() {
        return DraftCommandGridView.class;
    }

	@Override
	protected void getTitleForHeader() {
		if (getItem().getName() != null) {
			getView().setTitle(getView().getTitle() + ": " + getItem().getName());
		}
	}

}
