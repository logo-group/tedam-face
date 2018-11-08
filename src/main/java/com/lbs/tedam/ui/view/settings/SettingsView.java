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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.Property;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.EnumsV2.TedamUserRole;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

@Secured(TedamUserRole.Constants.ADMIN)
@SpringView
public class SettingsView extends CssLayout implements Serializable, View, HasLogger, TedamLocalizerWrapper {

	private static final long serialVersionUID = 1L;

	private final SettingsPresenter settingsPresenter;

	private TedamHorizontalLayout topBarLayout;
	private TedamButton btnCancel;
	private TedamButton btnSave;
	private TedamHorizontalLayout tedamHorizontalLayout;
	private List<TedamTextField> configFields;

	@Autowired
	public SettingsView(SettingsPresenter settingsPresenter) {
		this.settingsPresenter = settingsPresenter;
	}

	@PostConstruct
	public void init() {
		configFields = new ArrayList<>();
		settingsPresenter.setSettingsView(this);
		initView();
		settingsPresenter.build();
	}

	private void buildView() {
		removeAllComponents();
		addComponent(topBarLayout);

	}

	private void initView() {
		setResponsive(true);
		setWidth("100%");

		initTopBarLayout();
		TedamLabel gridLabel = initGridLabel();

		topBarLayout.addComponents(gridLabel);
		topBarLayout.setExpandRatio(gridLabel, 1);

		buildView();

	}

	private TedamLabel initGridLabel() {
		TedamLabel gridLabel = new TedamLabel(getLocaleValue("view.settingsview.label"));
		gridLabel.setStyleName(ValoTheme.LABEL_H3 + " bold");
		return gridLabel;
	}

	private void initTopBarLayout() {
		topBarLayout = new TedamHorizontalLayout();
		topBarLayout.setStyleName("top-bar");
		topBarLayout.setWidth("100%");
		topBarLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		topBarLayout.setSpacing(true);
	}

	private TedamHorizontalLayout initFooter() {
		TedamHorizontalLayout horizontalLayFooter = new TedamHorizontalLayout();
		horizontalLayFooter.setStyleName("buttons");
		horizontalLayFooter.setSizeFull();

		btnCancel = new TedamButton("general.button.cancel", VaadinIcons.CLOSE_SMALL);
		btnCancel.addStyleName("cancel");
		btnSave = new TedamButton("general.button.save", VaadinIcons.THUMBS_UP);
		btnSave.addStyleName("primary icon-align-right");
		horizontalLayFooter.addComponents(btnCancel, btnSave);

		btnSave.addClickListener(e -> {
			try {
				settingsPresenter.updateConfigProperty();
				showUpdateSuccess();
			} catch (LocalizedException e1) {
				getLogger().error(e1.getMessage(), e1);
			}
		});

		btnCancel.addClickListener(e -> {
			init();
		});

		horizontalLayFooter.setComponentAlignment(btnCancel, Alignment.TOP_LEFT);
		horizontalLayFooter.setComponentAlignment(btnSave, Alignment.TOP_RIGHT);

		return horizontalLayFooter;
	}

	public void buildHorLayout(Property property) {
		tedamHorizontalLayout = new TedamHorizontalLayout();
		tedamHorizontalLayout.setSpacing(true);
		tedamHorizontalLayout.setMargin(true);
		tedamHorizontalLayout.setSizeFull();
		TedamTextField textField = new TedamTextField("", "full", true, true);
		textField.setId("property_" + property.getParameter());
		textField.setCaption(property.getParameter());
		textField.setValue(property.getValue());
		configFields.add(textField);
		tedamHorizontalLayout.addComponent(textField);
		addComponent(tedamHorizontalLayout);
	}

	public void addFooter() {
		TedamHorizontalLayout layFooter = initFooter();
		addComponent(layFooter);
	}

	/**
	 * @return the btnCancel
	 */
	public TedamButton getBtnCancel() {
		return btnCancel;
	}

	/**
	 * @return the btnSave
	 */
	public TedamButton getBtnSave() {
		return btnSave;
	}

	public List<TedamTextField> getConfigFields() {
		return configFields;
	}

	public void showUpdateSuccess() {
		TedamNotification.showNotification(getLocaleValue("view.settingsview.messages.showUpdateSuccess"),
				NotifyType.SUCCESS);
	}
}
