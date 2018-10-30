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

package com.lbs.tedam.ui.components.window.teststep.formopen;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.FormOpenGenerator;
import com.lbs.tedam.model.MenuPath;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamTree;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import javax.annotation.PostConstruct;
import java.util.Map;

@SpringComponent
@PrototypeScope
public class WindowTestStepTypeFormOpen extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private FormOpenEditPresenter formOpenEditPresenter;

    private MenuPathDataProvider dataProviderMenuPath;
    private TedamTree<MenuPath> treeMenuPath;

    @Autowired
    public WindowTestStepTypeFormOpen(FormOpenEditPresenter formOpenEditPresenter, MenuPathDataProvider dataProviderMenuPath, ViewEventBus viewEventBus,
                                      PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
        this.formOpenEditPresenter = formOpenEditPresenter;
        this.dataProviderMenuPath = dataProviderMenuPath;
    }

    @PostConstruct
    private void initView() {
        formOpenEditPresenter.init(this);
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        buildTreeMenuPath();
        addSection(getLocaleValue("view.viewedit.section.values"), treeMenuPath);
        formOpenEditPresenter.fillComponentsWithValues();
        return getMainLayout();
    }

    private void buildTreeMenuPath() {
        treeMenuPath = new TedamTree<>(dataProviderMenuPath.getTreeDataProvider());
        treeMenuPath.setId("treeMenuPath");
    }

    public TedamTree<MenuPath> getTreeMenuPath() {
        return treeMenuPath;
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeFormOpen.header");
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(formOpenEditPresenter.getTestStep()));
    }

    private FormOpenGenerator getGenerator() {
        FormOpenGenerator generator = (FormOpenGenerator) formOpenEditPresenter.getTestStep().getGenerator();
        generator.resetMenuPathList();
        MenuPath selectedMenu = treeMenuPath.getSelectedItems().iterator().next();
        generator.addMenuPathList(selectedMenu);
        while (selectedMenu.getParentMenuPath() != null) {
            generator.addMenuPathList(selectedMenu.getParentMenuPath());
            selectedMenu = selectedMenu.getParentMenuPath();
        }
        return generator;
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        formOpenEditPresenter.enterView(parameters);
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        MenuPath menuPath = treeMenuPath.getSelectedItems().iterator().next();
        if (getGenerator().validate() && !menuPath.getChildMenuPaths().isEmpty()) {
            TedamNotification.showNotification(getLocaleValue("window.teststeptypeformopen.messages.leafNotSelected"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

}
