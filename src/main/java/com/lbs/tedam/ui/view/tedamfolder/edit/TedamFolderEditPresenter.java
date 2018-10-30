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

package com.lbs.tedam.ui.view.tedamfolder.edit;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamFolderService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.ui.components.combobox.TedamFolderComboBoxDataProvider;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import java.util.Map;

;

@SpringComponent
@ViewScope
public class TedamFolderEditPresenter extends AbstractEditPresenter<TedamFolder, TedamFolderService, TedamFolderEditPresenter, TedamFolderEditView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private TedamFolder tedamFolder;
    private TedamFolderComboBoxDataProvider tedamFolderComboBoxDataProvider;

    @Autowired
    public TedamFolderEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager, TedamFolderService tedamFolderService, TedamUserService userService,
                                    BeanFactory beanFactory, PropertyService propertyService, TedamFolderComboBoxDataProvider tedamFolderComboBoxDataProvider) {
        super(viewEventBus, navigationManager, tedamFolderService, TedamFolder.class, beanFactory, userService, propertyService);
        this.tedamFolderComboBoxDataProvider = tedamFolderComboBoxDataProvider;
    }

    @Override
    public void enterView(Map<UIParameter, Object> parameters) throws LocalizedException {
        setTedamFolder((TedamFolder) parameters.get(UIParameter.TEDAM_FOLDER));
        tedamFolderComboBoxDataProvider.buildDataProvider(tedamFolder.getFolderType());
        getView().organizeTedamFolderComboBox(tedamFolderComboBoxDataProvider);
        refreshView(tedamFolder, ViewMode.EDIT);
    }

    public TedamFolder getTedamFolder() {
        return tedamFolder;
    }

    public void setTedamFolder(TedamFolder tedamFolder) {
        this.tedamFolder = tedamFolder;
    }

    @Override
    protected Class<? extends View> getGridView() {
        return null;
    }

}
