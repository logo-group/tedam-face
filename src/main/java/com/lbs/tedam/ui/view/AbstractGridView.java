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

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.BaseService;
import com.lbs.tedam.data.service.GridPreferenceService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.AbstractBaseEntity;
import com.lbs.tedam.model.GridPreference;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.grid.GridFilterValue;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewBeforeLeaveEvent;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.ItemClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Set;

/**
 * Base grid view class.
 *
 * @param <T> Class type for grid objects.
 */
public abstract class AbstractGridView<T extends AbstractBaseEntity, S extends BaseService<T, Integer>, P extends AbstractGridPresenter<T, S, P, V>, V extends AbstractGridView<T, S, P, V>>
        extends VerticalLayout implements Serializable, View, HasLogger, TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    /**
     * Id for topBarLayout.
     */
    private static final String ID_TOPBAR_LAYOUT = "topBarLayout";

    /**
     * Id for innerLayout.
     */
    private static final String ID_INNER_LAYOUT = "innerLayout";

    /**
     * Id for gridLabel.
     */
    private static final String ID_GRID_LABEL = "gridLabel";

    /**
     * Id for grid.
     */
    private static final String ID_GRID = "grid";

    /**
     * Id for gridLayout.
     */
    private static final String ID_GRID_LAYOUT = "gridLayout";

    /**
     * Label of grid.
     */
    private TedamLabel gridLabel;

    /**
     * Add new button.
     */
    private TedamButton addButton;

    /**
     * Add new button.
     */
    private TedamButton deleteButton;

    /**
     * Clear filter button.
     */
    private TedamButton clearFilterButton;

    /**
     * Layout that grid is put into.
     */
    private HorizontalSplitPanel gridLayout;

    /**
     * Grid instance for grid view.
     */
    private TedamFilterGrid<T> grid;

    /**
     * Presenter instance for presenter.
     */
    private transient P presenter;

    /**
     * Top bar layout that contains label, add new button.
     */
    private TedamHorizontalLayout topBarLayout;

    /**
     * Layout that contains label.
     */
    private TedamHorizontalLayout innerLayout;

    /**
     * selectionMode for grid
     */
    private SelectionMode selectionMode;

    @Autowired
    private GridPreferenceService gridPreferenceService;

    public AbstractGridView(P presenter, SelectionMode selectionMode) {
        this.presenter = presenter;
        this.selectionMode = selectionMode;
    }

    /**
     * Get presenter instance.
     *
     * @return Presenter instance.
     */
    protected P getPresenter() {
        return presenter;
    }

    /**
     * Gets grid config.
     *
     * @return TedamGridConfig instance.
     */
    protected abstract TedamGridConfig<T> getTedamGridConfig();

    /**
     * Gets edit view class to navigate from grid.
     *
     * @return Edit view to navigate.
     */
    protected abstract Class<? extends View> getEditView();

    private Class<? extends View> getGridView() {
        return this.getClass();
    }

    /**
     * Inits base vertical layout.
     */
    private void initVerticalLayout() {
        setStyleName("crud-template");
        setResponsive(true);
        setSpacing(false);
        setSizeFull();
        setMargin(false);
    }

    /**
     * Inits top bar layout.
     */
    private void initTopBarLayout() {
        topBarLayout = new TedamHorizontalLayout();
        topBarLayout.setStyleName("top-bar");
        topBarLayout.setSpacing(false);
        topBarLayout.setWidth("100%");
        topBarLayout.setId(ID_TOPBAR_LAYOUT);
    }

    /**
     * Inits inner layout.
     */
    private void initInnerLayout() {
        innerLayout = new TedamHorizontalLayout();
        innerLayout.setSpacing(false);
        innerLayout.setWidth("100%");
        innerLayout.setId(ID_INNER_LAYOUT);
    }

    /**
     * Inits clear filter button.
     */
    private void initClearFilterButton() {
        clearFilterButton = new TedamButton("general.button.clearFilter", VaadinIcons.ERASER);
        clearFilterButton.setCaption("");
        clearFilterButton.setWidthUndefined();
        clearFilterButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                SecurityUtils.clearFilterValue(getGridView().getName(), grid.getId());
                grid.getFilter().clearAllFilters();
            }
        });
    }

    /**
     * Inits add new button.
     */
    private void initAddButton() {
        addButton = new TedamButton("general.button.add", VaadinIcons.PLUS);
        addButton.addStyleName("friendly");
        addButton.setCaption("");
        addButton.setWidthUndefined();
        addButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                addButtonClickEvent();
            }
        });
    }

    public void addButtonClickEvent() {
        if (getEditView() != null) {
            getPresenter().getNavigationManager().navigateTo(getEditView(), "new");
        }
    }

    /**
     * Inits add new button.
     */
    private void initRemoveButtonButton() {
        deleteButton = new TedamButton("general.button.deleteAll", VaadinIcons.MINUS);
        deleteButton.addStyleName("danger");
        deleteButton.setCaption("");
        deleteButton.setWidthUndefined();
        deleteButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                Set<T> selectedItems = getGrid().getSelectedItems();
                if (selectedItems.size() > 0) {
                    TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

                        @Override
                        public void onConfirm() {
                            try {
                                getPresenter().deleteSelectedLines(selectedItems);
                                getGrid().deselectAll();
                                getGrid().getDataProvider().refreshAll();
                            } catch (LocalizedException e) {
                                logError(e);
                            }
                        }

                        @Override
                        public void onCancel() {
                        }
                    }, getLocaleValue("confirm.message.delete"), getLocaleValue("general.button.ok"), getLocaleValue("general.button.cancel"));
                }

            }
        });
    }

    /**
     * Inits grid label.
     */
    private void initGridLabel() {
        gridLabel = new TedamLabel("");
        gridLabel.setStyleName(ValoTheme.LABEL_H3 + " bold");
        gridLabel.setId(ID_GRID_LABEL);
    }

    /**
     * Inits grid layout.
     */
    private void initGridLayout() {
        gridLayout = new HorizontalSplitPanel();
        gridLayout.setId("listParent");
        gridLayout.setSizeFull();
        gridLayout.setId(ID_GRID_LAYOUT);
    }

    /**
     * Inits grid.
     */
    private void initGrid() {
        AbstractDataProvider<T> dataPovider = getPresenter().getDataPovider();
        grid = new TedamFilterGrid<T>(getTedamGridConfig(), dataPovider, selectionMode) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onViewSelected(T item) {
                onGridViewSelected(item);
            }

            @Override
            public void onDeleteSelected(T item) {
                confirmDelete(item);
            }
        };

        grid.setSizeFull();
        grid.setId(ID_GRID);

        grid.addItemClickListener(new ItemClickListener<T>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClick<T> event) {
                if (event.getMouseEventDetails().isDoubleClick()) {
                    onGridViewSelected(event.getItem());
                }
            }
        });
    }

    /**
     * Inits all components.
     */
    private void initComponents() {
        initVerticalLayout();
        initTopBarLayout();
        initInnerLayout();
        initAddButton();
        initClearFilterButton();
        initRemoveButtonButton();
        initGridLabel();
        initGridLayout();
        initGrid();
        buildGridColumnDescription();
        innerLayout.addComponent(gridLabel);
        topBarLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        topBarLayout.addComponents(innerLayout, addButton, deleteButton, clearFilterButton);
        topBarLayout.setExpandRatio(innerLayout, 1);
        topBarLayout.setSpacing(true);
        gridLayout.setFirstComponent(grid);
        gridLayout.setSplitPosition(100, Unit.PERCENTAGE);
        gridLayout.setLocked(true);
        addComponents(topBarLayout, gridLayout);
        setExpandRatio(gridLayout, 1);
    }

    protected abstract void buildGridColumnDescription();

    @Override
    public void beforeLeave(ViewBeforeLeaveEvent event) {
        getPresenter().beforeLeavingView(event);
        saveFilterValues();
        saveGridPreference();
    }

    private void saveFilterValues() {
        SecurityUtils.saveFilterValue(this.getClass().getName(), grid.saveFilterValues());
    }

    private void saveGridPreference() {
        try {
            GridPreference gridPreference = findGridPreference();
            if (gridPreference == null) {
                gridPreference = grid.saveGridPreference();
                gridPreference.setUserId(SecurityUtils.getUser().getId());
                gridPreference.setProjectId(SecurityUtils.getUserSessionProject().getId());
                gridPreference.setViewId(this.getClass().getName());
            } else {
                gridPreference = grid.saveGridPreference(gridPreference);
            }
            gridPreferenceService.save(gridPreference);
        } catch (LocalizedException e) {
            logError(e);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        laodFilterValues();
        laodGridPreference();
        View.super.enter(event);
        getPresenter().enterView(TedamStatic.getUIParameterMap());
    }

    private void laodFilterValues() {
        GridFilterValue filterValues = SecurityUtils.loadFilterValue(this.getClass().getName(), grid.getId());
        grid.laodFilterValues(filterValues);
    }

    private void laodGridPreference() {
        try {
            GridPreference gridPreference = findGridPreference();
            grid.loadGridPreference(gridPreference);
        } catch (LocalizedException e) {
            logError(e);
        }
    }

    private GridPreference findGridPreference() throws LocalizedException {
        Integer userId = SecurityUtils.getUser().getId();
        Integer projectId = SecurityUtils.getUserSessionProject().getId();
        String viewId = this.getClass().getName();
        String gridId = grid.getId();
        GridPreference gridPreference = gridPreferenceService.findByUserIdAndProjectIdAndViewIdAndGridId(userId,
                projectId, viewId, gridId);
        return gridPreference;
    }

    /**
     * Calling after instance created.
     */
    @PostConstruct
    private void init() {
        initComponents();
    }

    /**
     * To be called when view selected.
     *
     * @param item Item
     */
    public void onGridViewSelected(T item) {
        if (getEditView() != null) {
            getPresenter().getNavigationManager().navigateTo(getEditView(), item.getId());
        }
    }

    /**
     * To be called when delete selected.
     *
     * @param item Item
     * @throws LocalizedException
     */
    public void onGridDeleteSelected(T item) throws LocalizedException {
        getPresenter().delete(item);
        getGrid().getDataProvider().refreshAll();
    }

    /**
     * Executes confirmation for delete operations.
     *
     * @param item Item
     */
    private void confirmDelete(T item) {
        TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

            @Override
            public void onConfirm() {
                try {
                    onGridDeleteSelected(item);
                } catch (LocalizedException e) {
                    logError(e);
                }
            }

            @Override
            public void onCancel() {
            }
        }, getLocaleValue("confirm.message.delete"), getLocaleValue("general.button.ok"), getLocaleValue("general.button.cancel"));
    }

    /**
     * @return the gridLabel
     */
    protected void setHeader(String header) {
        gridLabel.setValue(header);
    }

    /**
     * @return the addButton
     */
    protected TedamButton getAddButton() {
        return addButton;
    }

    /**
     * @return the gridLayout
     */
    public HorizontalSplitPanel getGridLayout() {
        return gridLayout;
    }

    /**
     * @return the grid
     */
    public TedamFilterGrid<T> getGrid() {
        return grid;
    }

    /**
     * @return the topBarLayout
     */
    protected TedamHorizontalLayout getTopBarLayout() {
        return topBarLayout;
    }

    /**
     * @return the innerLayout
     */
    protected TedamHorizontalLayout getInnerLayout() {
        return innerLayout;
    }

    protected void logError(LocalizedException e) {
        getLogger().error(e.getLocalizedMessage(), e);
        TedamNotification.showNotification(e.getLocalizedMessage(), NotifyType.ERROR);
    }
}