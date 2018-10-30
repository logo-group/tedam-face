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

package com.lbs.tedam.ui.components.window.teststep.buttonclick;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.ButtonClickGenerator;
import com.lbs.tedam.model.DTO.ButtonCtrl;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamCheckBox;
import com.lbs.tedam.ui.components.basic.TedamMenuBar;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.Enums.Regex;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@SpringComponent
@PrototypeScope
public class WindowTestStepTypeButtonClick extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private ButtonClickWindowPresenter buttonClickEditPresenter;
    private TedamCheckBox chkContinueOnError;
    private TedamMenuBar menuBar;

    @Autowired
    public WindowTestStepTypeButtonClick(ButtonClickWindowPresenter editPresenter, ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.buttonClickEditPresenter = editPresenter;
    }

    @PostConstruct
    private void initView() {
        buttonClickEditPresenter.init(this);
    }

    private void initChkContinueOnError() {
        chkContinueOnError = new TedamCheckBox("view.buttonclickeedit.chkContinueOnError", "full", true, true);
    }

    public TedamCheckBox getChkContinueOnError() {
        return chkContinueOnError;
    }

    public void createMenuBar(String parameter, List<ButtonCtrl> buttonList) {
        for (int i = 0; i < buttonList.size(); i++) {
            ButtonCtrl buttonCtrl = buttonList.get(i);
            String menuCaption = createMenuCaption(buttonCtrl);
            if (buttonList.get(i).getType().equals(com.lbs.tedam.util.Constants.COMBO_CONTROLTYPE_MENUBUTTON)) {
                addMenuItemsForMenuButton(parameter, menuCaption, buttonCtrl);
            } else {
                addMenuItemsForButton(parameter, menuCaption, buttonCtrl);
            }
        }
    }

    private String createMenuCaption(ButtonCtrl buttonCtrl) {
        String menuCaption = buttonCtrl.getTag();
        String xuiDoc = buttonCtrl.getXuiDoc();

        if (xuiDoc.isEmpty() || "unknown".equals(xuiDoc.toLowerCase())) {
            menuCaption = "(" + menuCaption + ")";
        } else {
            menuCaption = xuiDoc + " (" + menuCaption + ")";
        }
        return menuCaption;
    }

    private void addMenuItemsForButton(String parameter, String menuCaption, ButtonCtrl buttonCtrl) {
        String buttonTag = buttonCtrl.getTag();
        String buttonType = buttonCtrl.getType();
        MenuItem menuItem = menuBar.addItem(menuCaption, null);
        menuItem.setCheckable(true);
        boolean isScriptClick = buttonType.contains(com.lbs.tedam.util.Constants.SCRIPT_CLICK);
        String itemDescriptionParameter = buttonTag;
        if (isScriptClick) {
            itemDescriptionParameter = buttonType + Regex.INNER_PARAMETER_SPLITTER.getRegex() + String.valueOf(buttonTag);
        }
        menuItem.setDescription(itemDescriptionParameter);
        if (itemDescriptionParameter.equals(parameter)) {
            menuItem.setChecked(true);
        }
        menuItem.setCommand(new MenuBarCommand());
    }

    private void addMenuItemsForMenuButton(String parameter, String menuCaption, ButtonCtrl buttonCtrl) {
        MenuItem menuButton = menuBar.addItem(menuCaption, null);
        menuButton.setCommand(new MenuBarCommand());
        List<Integer> mbItemNoList = buttonCtrl.getMenuButtonItemTagList();
        menuButton.setDescription(buttonCtrl.getTag());
        for (int j = 0; j < mbItemNoList.size(); j++) {
            MenuItem menuItem = menuButton.addItem(buttonCtrl.getMenuButtonItemTextList().get(j), null);
            menuItem.setCommand(new MenuBarCommand());
            menuItem.setCheckable(true);
            menuItem.setDescription(String.valueOf(mbItemNoList.get(j)));
            if (String.valueOf(mbItemNoList.get(j)).equals(parameter)) {
                menuItem.setChecked(true);
            }
        }
    }

    private void initMenuBar() {
        menuBar = new TedamMenuBar("menuBar");
        menuBar.setSizeFull();
        menuBar.setAutoOpen(true);
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        initChkContinueOnError();
        initMenuBar();
        addSection(getLocaleValue("view.viewedit.section.values"), chkContinueOnError, menuBar);
        buttonClickEditPresenter.fillComponentsWithValues();
        return getMainLayout();
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeButtonClick.header");
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(buttonClickEditPresenter.getTestStep()));
    }

    public ButtonClickGenerator getSelectedButton() {
        ButtonClickGenerator generator = (ButtonClickGenerator) buttonClickEditPresenter.getTestStep().getGenerator();
        generator.setContinueOnError(chkContinueOnError.getValue() ? "1" : "0");
        outer:
        for (MenuItem component : menuBar.getItems()) {
            if (component.isChecked()) {
                generator.setButtonTag(component.getDescription());
                generator.setMenuButtonItemTag(null);
                break;
            } else {
                List<MenuItem> children = component.getChildren();
                if (children != null) {
                    for (MenuItem menuItem : children) {
                        if (menuItem.isChecked()) {
                            generator.setButtonTag(component.getDescription());
                            generator.setMenuButtonItemTag(menuItem.getDescription());
                            break outer;
                        }
                    }
                }
            }
        }
        return generator;
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        buttonClickEditPresenter.enterView(parameters);
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (!getSelectedButton().validate()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.teststeptype"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

    class MenuBarCommand implements Command {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void menuSelected(MenuItem selectedItem) {
            for (MenuItem menuItem : menuBar.getItems()) {
                if (menuItem.getChildren() != null && !menuItem.equals(selectedItem)) {
                    for (MenuItem childMenuItem : menuItem.getChildren()) {
                        if (!childMenuItem.equals(selectedItem)) {
                            childMenuItem.setChecked(false);
                        }
                    }
                }
                if (menuItem.getChildren() == null && !menuItem.equals(selectedItem)) {
                    menuItem.setChecked(false);
                }
            }
        }
    }

}
