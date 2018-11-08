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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Property;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class SettingsPresenter implements HasLogger, Serializable {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private final SettingsDataProvider settingsDataProvider;

	private final PropertyService propertyService;

	private SettingsView settingsView;

	@Autowired
	public SettingsPresenter(SettingsDataProvider settingsDataProvider, PropertyService propertyService) {
		this.settingsDataProvider = settingsDataProvider;
		this.propertyService = propertyService;
	}

	public void build() {
		List<Property> configPropertyList = (List<Property>) settingsDataProvider.getListDataProvider().getItems();
		configPropertyList.forEach(property -> {
			settingsView.buildHorLayout(property);
		});
		settingsView.addFooter();
	}

	public void updateConfigProperty() throws LocalizedException {
		List<Property> configPropertyList = (List<Property>) settingsDataProvider.getListDataProvider().getItems();
		List<Property> updatedList = new ArrayList<>();
		configPropertyList.forEach(property -> {
			for (TedamTextField configField : settingsView.getConfigFields()) {
				if (configField.getId().equals("property_" + property.getParameter())) {
					property.setValue(configField.getValue());
					updatedList.add(property);
				}
			}
		});
		propertyService.save(updatedList);
	}

	public void setSettingsView(SettingsView settingsView) {
		this.settingsView = settingsView;
	}

}
