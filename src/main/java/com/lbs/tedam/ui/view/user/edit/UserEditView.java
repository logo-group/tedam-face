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

package com.lbs.tedam.ui.view.user.edit;

import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.ui.AppUI;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamPasswordField;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamUserRoleAdminAccessComboBox;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.components.window.project.WindowProject;
import com.lbs.tedam.ui.dialog.ConfirmationListener;
import com.lbs.tedam.ui.dialog.TedamDialog;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.lbs.tedam.util.EnumsV2.TedamUserRole;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Secured(TedamUserRole.Constants.ADMIN)
@SpringView
public class UserEditView extends AbstractEditView<TedamUser, TedamUserService, UserEditPresenter, UserEditView> {

    private static final long serialVersionUID = 1L;

    private final WindowProject windowProject;

    private TedamTextField userName;
    private TedamPasswordField newPass;
    private TedamUserRoleAdminAccessComboBox role;
    private TedamButton addButton;
    private TedamGrid<Project> gridProjects;

    @Autowired
    public UserEditView(UserEditPresenter presenter, TedamUserRoleAdminAccessComboBox role, WindowProject windowProject) {
        super(presenter);
        this.role = role;
        this.windowProject = windowProject;

    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.useredit.header");
    }

    @PostConstruct
    private void initView() {
        userName = new TedamTextField("view.useredit.textfield.username", "half", true, true);
        newPass = new TedamPasswordField("view.useredit.passwordfield.password", "half", true, true);
        newPass.addValueChangeListener(e -> getPresenter().setNewPassword(newPass.getValue()));

        buildProjectsGrid();

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, userName, newPass, role);
        addSection(getLocaleValue("view.environmentedit.section.projects"), 1, null, buildProjectsGridButtons(), gridProjects);

        getPresenter().setView(this);
    }

    private Component buildProjectsGridButtons() {
        HorizontalLayout hLayButtons = new HorizontalLayout();
        addButton = new TedamButton("view.useredit.button.addproject", VaadinIcons.PLUS_CIRCLE);
        addButton.addClickListener(e -> {
            try {
                getPresenter().prepareProjectAddWindow();
            } catch (LocalizedException e1) {
                logError(e1);
            }
        });
        hLayButtons.addComponents(addButton);
        return hLayButtons;
    }

    private void buildProjectsGrid() {
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
                operations.add(RUDOperations.DELETE);
                return operations;
            }

        };
        gridProjects = new TedamGrid<Project>(projectGridConfig, SelectionMode.SINGLE) {
            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void onDeleteSelected(Project project) {
                confirmDelete(project);

            }
        };
    }

    private void confirmDelete(Project project) {
        TedamDialog.confirm(AppUI.getCurrent(), new ConfirmationListener() {

            @Override
            public void onConfirm() {
                getPresenter().removeProject(project);
            }

            @Override
            public void onCancel() {
            }
        }, getLocaleValue("confirm.message.delete"), getLocaleValue("general.button.ok"), getLocaleValue("general.button.cancel"));
    }

    protected void organizeProjectsGrid(AbstractDataProvider<Project> dataProvider) {
        gridProjects.setGridDataProvider(dataProvider);
    }

    public void openProjectAddWindow(Map<UIParameter, Object> windowParameters) throws LocalizedException {
        try {
            windowProject.open(windowParameters);
        } catch (TedamWindowNotAbleToOpenException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bindFormFields(BeanValidationBinder<TedamUser> binder) {
        super.bindFormFields(binder);
        binder.forField(role).asRequired().bind("role");
    }

}
