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

package com.lbs.tedam.ui.view.usersettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.model.TedamUserFavorite;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamPasswordField;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamSessionProjectComboBox;
import com.lbs.tedam.ui.components.combobox.TedamUserFavoriteTypeComboBox;
import com.lbs.tedam.ui.components.combobox.TedamUserRoleComboBox;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.client.WindowSelectClient;
import com.lbs.tedam.ui.components.window.environment.WindowSelectFavoriteEnvironment;
import com.lbs.tedam.ui.components.window.job.WindowSelectJob;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.lbs.tedam.util.EnumsV2.TedamUserFavoriteType;
import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid.SelectionMode;

@SpringView
public class UserSettingsView extends AbstractEditView<TedamUser, TedamUserService, UserSettingsPresenter, UserSettingsView> {

    private static final long serialVersionUID = 1L;
    private final WindowSelectClient windowClient;
    private final WindowSelectJob windowJob;
    private final WindowSelectFavoriteEnvironment windowEnvironment;
    private TedamTextField userName;
    private TedamPasswordField newPass;
    private TedamUserRoleComboBox role;
    private TedamButton addButton;
    private TedamGrid<Project> gridProjects;
    private TedamFilterGrid<TedamUserFavorite> gridUserFavorites;
    private TedamSessionProjectComboBox activeProject;
    private TedamUserFavoriteTypeComboBox tedamUserFavoriteTypeComboBox;

    @Autowired
    public UserSettingsView(UserSettingsPresenter presenter, TedamUserRoleComboBox role, TedamUserFavoriteTypeComboBox tedamUserFavoriteTypeComboBox,
                            TedamSessionProjectComboBox activeProject, WindowSelectClient windowClient, WindowSelectJob windowJob, WindowSelectFavoriteEnvironment windowEnvironment) {
        super(presenter);
        this.role = role;
        this.activeProject = activeProject;
        this.windowClient = windowClient;
        this.windowJob = windowJob;
        this.windowEnvironment = windowEnvironment;
        this.tedamUserFavoriteTypeComboBox = tedamUserFavoriteTypeComboBox;
    }

    @PostConstruct
    private void initView() {
        userName = new TedamTextField("view.useredit.textfield.username", "half", true, true);
        newPass = new TedamPasswordField("view.useredit.passwordfield.password", "half", true, true);
        newPass.addValueChangeListener(e -> getPresenter().setNewPassword(newPass.getValue()));
        addButton = new TedamButton("view.usersettings.button.addFavorite", VaadinIcons.PLUS_CIRCLE);
        addButton.setStyleName("half");
        addButton.addClickListener(e -> {
            if (tedamUserFavoriteTypeComboBox.getValue() == null) {
                showUserFavoriteTypeNotSelected();
            } else if (TedamUserFavoriteType.JOB.equals(tedamUserFavoriteTypeComboBox.getValue())) {
                try {
                    getPresenter().prepareJobAddWindow();
                } catch (LocalizedException e1) {
                    logError(e1);
                }
            } else if (TedamUserFavoriteType.CLIENT.equals(tedamUserFavoriteTypeComboBox.getValue())) {
                try {
                    getPresenter().prepareClientAddWindow();
                } catch (LocalizedException e1) {
                    logError(e1);
                }
            } else if (TedamUserFavoriteType.ENVIRONMENT.equals(tedamUserFavoriteTypeComboBox.getValue())) {
                try {
                    getPresenter().prepareEnvironmentAddWindow();
                } catch (LocalizedException e1) {
                    logError(e1);
                }
            }
        });

        buildGrid();
        buildClientsGrid();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, userName, newPass, role, activeProject);
        addSection(getLocaleValue("view.usersettings.section.authorizedprojects"), 1, null, gridProjects);
        addSection(getLocaleValue("view.usersettings.section.userfavorites"), 2, null, tedamUserFavoriteTypeComboBox, addButton, gridUserFavorites);

        getPresenter().setView(this);
    }

    private void buildActiveProject() {
        activeProject.setSelectedItem(SecurityUtils.getUserSessionProject());
        activeProject.addSelectionListener(new SingleSelectionListener<Project>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void selectionChange(SingleSelectionEvent<Project> event) {
                if (!SecurityUtils.getUserSessionProject().equals(event.getValue())) {
                    SecurityUtils.setUserSessionProject(event.getValue());
                    Page.getCurrent().reload();
                }
            }
        });
    }

    private void buildGrid() {
        TedamGridConfig<Project> projectGridConfig = new TedamGridConfig<Project>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.PROJECT_COLUMNS;
            }

            @Override
            public Class<Project> getBeanType() {
                return Project.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

        };
        gridProjects = new TedamGrid<Project>(projectGridConfig, SelectionMode.NONE);
		gridProjects.setId("UserSettingsProjectsGrid");
    }

    private void buildClientsGrid() {
        TedamGridConfig<TedamUserFavorite> clientGridConfig = new TedamGridConfig<TedamUserFavorite>() {

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumns.GridColumn.USER_FAVORITE_COLUMNS;
            }

            @Override
            public Class<TedamUserFavorite> getBeanType() {
                return TedamUserFavorite.class;
            }

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.DELETE);
                return operations;
            }

        };
        gridUserFavorites = new TedamFilterGrid<TedamUserFavorite>(clientGridConfig, SelectionMode.SINGLE) {
            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void onDeleteSelected(TedamUserFavorite userFavorite) {
                confirmDelete(userFavorite);

            }
        };
		gridUserFavorites.setId("UserSettingsFavoritesGrid");
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.useredit.header");
    }

    protected void organizeGrid(AbstractDataProvider<Project> dataProvider) {
        activeProject.setDataProvider(dataProvider.getListDataProvider());
        buildActiveProject();
        gridProjects.setGridDataProvider(dataProvider);
    }

    protected void organizeUserFavoritesGrid(AbstractDataProvider<TedamUserFavorite> dataProvider) {
        gridUserFavorites.setGridDataProvider(dataProvider);
        gridUserFavorites.initFilters();
    }

    public void openClientAddWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowClient.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    public void openJobAddWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowJob.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    public void openEnvironmentAddWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowEnvironment.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    private void confirmDelete(TedamUserFavorite userFavorite) {
        TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

            @Override
            public void onConfirm() {
                getPresenter().removeUserFavorite(userFavorite);
            }

            @Override
            public void onCancel() {
            }
        }, getLocaleValue("confirm.message.delete"), getLocaleValue("general.button.ok"), getLocaleValue("general.button.cancel"));
    }

    public TedamFilterGrid<TedamUserFavorite> getGridUserFavorites() {
        return gridUserFavorites;
    }

    public void showUserFavoriteTypeNotSelected() {
        TedamNotification.showNotification(getLocaleValue("view.usersettings.messages.showUserFavoriteTypeNotSelected"), NotifyType.WARNING);
    }

	@Override
	protected void collectGrids() {
		super.collectGrids();
		getGridList().add(gridProjects);
		getGridList().add(gridUserFavorites);
	}

}
