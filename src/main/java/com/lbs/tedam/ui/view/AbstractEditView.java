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

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

import com.lbs.tedam.data.service.BaseService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.AbstractBaseEntity;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamDateTimeField;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.layout.TedamCssLayout;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Base edit form view class.
 *
 * @param <T> Class type for edit.
 */
public abstract class AbstractEditView<T extends AbstractBaseEntity, S extends BaseService<T, Integer>, P extends AbstractEditPresenter<T, S, P, V>, V extends AbstractEditView<T, S, P, V>>
        extends VerticalLayout implements Serializable, View, HasLogger, TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    private transient P presenter;

    private ViewMode viewMode;

    private TedamButton btnCancel;
    private TedamButton btnSave;

    private TedamTextField createdUser;
    private TedamDateTimeField dateCreated;
    private TedamTextField updatedUser;
    private TedamDateTimeField dateUpdated;
    private TedamTextField id;

    private Accordion accordion;

    private TedamLabel lblHeader;

    public AbstractEditView(P presenter) {
        this.presenter = presenter;
        initComponents();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String parameter = event.getParameters();
        try {
            if (parameter.contains("new")) {
                Map<UIParameter, Object> uiParameterMap = buildUIParameterMap(parameter);
                getPresenter().enterView(uiParameterMap);
            } else {
                getPresenter().enterView(TedamStatic.getUIParameterMap(Integer.valueOf(parameter), ViewMode.VIEW));
            }
        } catch (LocalizedException e) {
            logError(e);
        }
    }

    private Map<UIParameter, Object> buildUIParameterMap(String parameter) {
        Map<UIParameter, Object> uiParameterMap = TedamStatic.getUIParameterMap(0, ViewMode.NEW);
        if (parameter.contains("?")) {
            String splittedParam = parameter.split("\\?")[1];
            String[] uiParameters = splittedParam.split(Constants.EQUAL);
            UIParameter uiParameter = UIParameter.valueOf(uiParameters[0].toUpperCase());
            switch (uiParameter) {
                case FOLDER:
                    uiParameterMap.put(UIParameter.FOLDER, Integer.valueOf(uiParameters[1]));
                    break;
                default:
                    break;
            }
        }
        return uiParameterMap;
    }

    public abstract String getHeader();

    private void initComponents() {
        // setStyleName("crud-template");
        setResponsive(true);
        setSpacing(false);
        // setSizeFull();
        setMargin(new MarginInfo(false, true, true, true));

        accordion = new Accordion();
        accordion.setResponsive(true);
        accordion.setSizeFull();

        addComponents(initHeader(), accordion);
        setExpandRatio(accordion, 1);
        initBasicFormLayout();

        getCancel().addClickListener(e -> getPresenter().backPressed());
        getSave().addClickListener(e -> {
            try {
                getPresenter().okPressed();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
    }

    /**
     * Get presenter instance.
     *
     * @return Presenter instance.
     */
    protected P getPresenter() {
        return presenter;
    }

    private HorizontalLayout initHeader() {
        TedamHorizontalLayout hLayHeader = new TedamHorizontalLayout();
        hLayHeader.setSizeFull();
        hLayHeader.setId("reportHeader");

        btnCancel = new TedamButton("general.button.cancel", VaadinIcons.CLOSE_SMALL);
        btnCancel.setWidthUndefined();
        btnCancel.addStyleName("cancel");
        btnSave = new TedamButton("general.button.save", VaadinIcons.THUMBS_UP);
        btnSave.setWidthUndefined();
        btnSave.addStyleName("primary icon-align-right");

        lblHeader = new TedamLabel(getHeader());
        lblHeader.setStyleName("h2 colored");
        hLayHeader.addComponents(lblHeader, btnCancel, btnSave);
        hLayHeader.setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT);
        hLayHeader.setComponentAlignment(btnSave, Alignment.MIDDLE_RIGHT);
        hLayHeader.setExpandRatio(lblHeader, 1);
        return hLayHeader;
    }

    private void initBasicFormLayout() {
        id = new TedamTextField("textfield.id", "full", true, false);
        createdUser = new TedamTextField("textfield.createduser", "half", true, false);
        dateCreated = new TedamDateTimeField("textfield.createddate", "half", true, false);
        updatedUser = new TedamTextField("textfield.updateduser", "half", true, false);
        dateUpdated = new TedamDateTimeField("textfield.updateddate", "half", true, false);
        addSection(getLocaleValue("section.userproperties"), 0, null, id, createdUser, dateCreated, updatedUser, dateUpdated);
    }

    protected void addSection(String title, int position, Resource icon, Component... c) {
        TedamCssLayout cssLayout = new TedamCssLayout();
        cssLayout.setStyleName("responsive");
        cssLayout.setResponsive(true);
        cssLayout.setSizeFull();
        for (int i = 0; i < c.length; i++) {
            cssLayout.addComponent(wrapWithHorizontalLayout(c[i]));
        }
        addSectionToAccordion(title, position, icon, cssLayout);
    }

    private void addSectionToAccordion(String title, int position, Resource icon, TedamCssLayout cssLayout) {
        VerticalLayout vlayTemp = new VerticalLayout();
        vlayTemp.setMargin(true);
        vlayTemp.setSpacing(true);
        vlayTemp.addComponent(cssLayout);
        accordion.addTab(vlayTemp, title, icon, position).setStyleName(com.lbs.tedam.ui.util.Constants.TEDAM_ACCORDION_TAB_CSS);
    }

    private Component wrapWithHorizontalLayout(Component c) {
        HorizontalLayout vlayTemp = new HorizontalLayout();
        vlayTemp.setSizeFull();
        vlayTemp.setStyleName(c.getStyleName());
        c.removeStyleName(c.getStyleName());
        c.setSizeFull();
        vlayTemp.setMargin(false);
        vlayTemp.setSpacing(false);
        vlayTemp.addComponent(c);
        return vlayTemp;
    }

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        getPresenter().beforeLeavingView(event);
    }

    public TedamButton getCancel() {
        return btnCancel;
    }

    public TedamButton getSave() {
        return btnSave;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    protected void setViewMode(ViewMode viewMode) {

        // Allow to style different modes separately
        if (getViewMode() != null) {
            removeStyleName(this.getViewMode().name().toLowerCase());
        }
        addStyleName(viewMode.name().toLowerCase());
        this.viewMode = viewMode;
        getBinder().setReadOnly(viewMode == ViewMode.VIEW);
        if (viewMode == ViewMode.VIEW) {
            getCancel().setCaption(getLocaleValue("general.button.back"));
            getCancel().setIcon(VaadinIcons.ANGLE_LEFT);
            getSave().setCaption(getLocaleValue("general.button.edit"));
            getSave().setIcon(VaadinIcons.EDIT);
        } else if (viewMode == ViewMode.NEW) {
            getCancel().setCaption(getLocaleValue("general.button.cancel"));
            getCancel().setIcon(VaadinIcons.ANGLE_LEFT);
            getSave().setCaption(getLocaleValue("general.button.save"));
        } else if (viewMode == ViewMode.EDIT) {
            getCancel().setCaption(getLocaleValue("general.button.cancel"));
            getCancel().setIcon(VaadinIcons.CLOSE);
            getSave().setCaption(getLocaleValue("general.button.update"));
        } else {
            throw new IllegalArgumentException("Unknown mode " + viewMode);
        }

    }

    public Accordion getAccordion() {
        return accordion;
    }

    public BeanValidationBinder<T> getBinder() {
        return getPresenter().getBinder();
    }

    public Stream<Object> validate() {
        Stream<Object> errorFields = getBinder().validate().getFieldValidationErrors().stream().map(BindingValidationStatus::getField);
        return errorFields;
    }

    public void bindFormFields(BeanValidationBinder<T> binder) {
        binder.forField(id).withNullRepresentation("").withConverter(new StringToIntegerConverter(Integer.valueOf(0), "")).bind(T::getId, T::setId);
        getBinder().bindInstanceFields(this);
    }

    public void showNotFound() {
        removeAllComponents();
        addComponent(new Label("Item not found"));
    }

    public void showNotAuthorized() {
        removeAllComponents();
        addComponent(new Label(getLocaleValue("view.usersettings.notauthorized")));
    }

    public void showDataIntegrityException() {
        TedamNotification.showNotification(getLocaleValue("view.abstractedit.messages.DataIntegrityViolationException"), NotifyType.ERROR);
    }

    protected void logError(LocalizedException e) {
        getLogger().error(e.getLocalizedMessage(), e);
        TedamNotification.showNotification(e.getLocalizedMessage(), NotifyType.ERROR);
    }

    protected TedamLabel getLblHeader() {
        return lblHeader;
    }

	public String getTitle() {
		return getHeader();
	}

	public void setTitle(String title) {
		getLblHeader().setValue(title);
	}

	public void showSuccessfulSave() {
		TedamNotification.showNotification(getLocaleValue("view.abstractedit.messages.SuccessfulSave"),
				NotifyType.SUCCESS);
	}

	public void showSuccessfulUpdate() {
		TedamNotification.showNotification(getLocaleValue("view.abstractedit.messages.SuccessfulUpdate"),
				NotifyType.SUCCESS);
	}

}
