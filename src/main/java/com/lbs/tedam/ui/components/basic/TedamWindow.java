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

/**
 *
 */
package com.lbs.tedam.ui.components.basic;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.GridPreferenceService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.GridPreference;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ahmet.Izgi
 */
public abstract class TedamWindow extends Window implements TedamLocalizerWrapper, Serializable, HasLogger {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private final EventBus eventBus;
    private final PropertyService propertyService;
    private VerticalLayout cssFormLayout;
    private TedamButton btnOk;
    private TedamButton btnCancel;

	@Autowired
	private GridPreferenceService gridPreferenceService;

    public TedamWindow(WindowSize windowSize, EventBus eventBus, PropertyService propertyService) {
        this.eventBus = eventBus;
        this.propertyService = propertyService;
        setStyleName(windowSize.getSize());
        setCaption(getHeader());
        removeAllCloseShortcuts();
    }

    protected void initWindow() throws TedamWindowNotAbleToOpenException, LocalizedException {
        cssFormLayout = new VerticalLayout();
        cssFormLayout.setResponsive(true);
        cssFormLayout.setWidth(100f, Unit.PERCENTAGE);

        VerticalLayout layRoot = new VerticalLayout();
        layRoot.setMargin(true);
        layRoot.setSpacing(true);

        Component footer = buildFooter();
        Component customContent = buildContent();
        Panel contentPanel = new Panel();
        contentPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        contentPanel.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);
        contentPanel.setSizeFull();
        contentPanel.setContent(customContent);
        layRoot.addComponent(contentPanel);
        layRoot.addComponent(footer);
        layRoot.setSizeFull();
        layRoot.setExpandRatio(contentPanel, 1);
        setContent(layRoot);
		if (getWindowGrid() != null) {
			laodGridPreference();
		}
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName("v-window-bottom-toolbar");

        TedamLabel footerText = new TedamLabel();
        footerText.setSizeUndefined();

        btnOk = new TedamButton("general.button.ok", VaadinIcons.CHECK);
        btnOk.addStyleName("primary");
        btnOk.setWidthUndefined();

        btnCancel = new TedamButton("general.button.cancel", VaadinIcons.CROSS_CUTLERY);
        btnCancel.setWidthUndefined();

        btnCancel.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    cancelButtonPressed();
                } catch (LocalizedException e) {
                    logError(e);
                }
            }
        });

        btnOk.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    okButtonPressed();
                } catch (LocalizedException e) {
                    logError(e);
                }
            }
        });

        footer.addComponents(footerText, btnOk, btnCancel);
        footer.setExpandRatio(footerText, 1);
        return footer;
    }

	public abstract void open(Map<UIParameter, Object> parameters)
			throws TedamWindowNotAbleToOpenException, LocalizedException;

    protected abstract Component buildContent() throws TedamWindowNotAbleToOpenException, LocalizedException;

    protected abstract String getHeader();

    public EventBus getEventBus() {
        return eventBus;
    }

    protected void okButtonPressed() throws LocalizedException {
        if (!readyToClose()) {
            return;
        }
        publishCloseSuccessEvent();
        windowClose();
        close();
		if (getWindowGrid() != null) {
			saveGridPreference();
		}
    }

    protected void cancelButtonPressed() throws LocalizedException {
        windowClose();
        close();
    }

	protected abstract void windowClose();

    protected abstract boolean readyToClose();

    protected void addSection(String title, Component... c) {
        VerticalLayout vlay = new VerticalLayout();
        vlay.setMargin(false);
        vlay.setSpacing(true);
        vlay.setWidth("100%");
        TedamLabel sectionHeader = new TedamLabel(title);
        sectionHeader.addStyleName("sectionHeader");
        sectionHeader.setWidth("100%");
        vlay.addComponent(sectionHeader);
        for (int i = 0; i < c.length; i++) {
            vlay.addComponent(c[i]);
        }
        cssFormLayout.addComponent(vlay);
        cssFormLayout.setComponentAlignment(vlay, Alignment.TOP_LEFT);
    }

    public VerticalLayout getMainLayout() {
        return cssFormLayout;
    }

    public PropertyService getPropertyService() {
        return propertyService;
    }

    public abstract void publishCloseSuccessEvent();

    protected void logError(LocalizedException e) {
        getLogger().error(e.getLocalizedMessage(), e);
        TedamNotification.showNotification(e.getLocalizedMessage(), NotifyType.ERROR);
    }

	private GridPreference findGridPreference() throws LocalizedException {
		Integer userId = SecurityUtils.getUser().getId();
		Integer projectId = SecurityUtils.getUserSessionProject().getId();
		String viewId = this.getClass().getName();
		String gridId = getWindowGrid().getId();
		GridPreference gridPreference = gridPreferenceService.findByUserIdAndProjectIdAndViewIdAndGridId(userId,
				projectId, viewId, gridId);
		return gridPreference;
	}

	private void saveGridPreference() {
		try {
			GridPreference gridPreference = findGridPreference();
			if (gridPreference == null) {
				gridPreference = getWindowGrid().saveGridPreference();
				gridPreference.setUserId(SecurityUtils.getUser().getId());
				gridPreference.setProjectId(SecurityUtils.getUserSessionProject().getId());
				gridPreference.setViewId(this.getClass().getName());
			} else {
				gridPreference = getWindowGrid().saveGridPreference(gridPreference);
			}
			gridPreferenceService.save(gridPreference);
		} catch (LocalizedException e) {
			logError(e);
		}
	}

	private void laodGridPreference() {
		try {
			GridPreference gridPreference = findGridPreference();
			getWindowGrid().loadGridPreference(gridPreference);
		} catch (LocalizedException e) {
			logError(e);
		}
	}

	public TedamGrid<?> getWindowGrid() {
		return null;
	}
}
