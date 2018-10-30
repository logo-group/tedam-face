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

package com.lbs.tedam.ui.view.settings;

import com.lbs.tedam.model.Property;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@SpringComponent
@ViewScope
public class SettingsPresenter implements HasLogger, Serializable {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final SettingsDataProvider settingsDataProvider;

    private SettingsView settingsView;

    @Autowired
    public SettingsPresenter(SettingsDataProvider settingsDataProvider) {
        this.settingsDataProvider = settingsDataProvider;
    }

    public void build() {
        List<Property> configPropertyList = (List<Property>) settingsDataProvider.getListDataProvider().getItems();
        configPropertyList.forEach(property -> {
            settingsView.buildHorLayout(property);
        });
        settingsView.addFooter();
    }

    public void setSettingsView(SettingsView settingsView) {
        this.settingsView = settingsView;
    }

}
