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

package com.lbs.tedam.ui.components.window.teststep.doubleclick;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.DoubleClickGenerator;
import com.lbs.tedam.model.DTO.DoubleClick;
import com.lbs.tedam.ui.TedamFaceEvents.TestStepTypeParameterPreparedEvent;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.combobox.TedamComboBox;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.util.Enums.Regex;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SpringComponent
@PrototypeScope
public class WindowTestStepTypeDoubleClick extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private DoubleClickWindowPresenter presenter;
    private TedamComboBox<DoubleClick> comboBox = new TedamComboBox<DoubleClick>();

    @Autowired
    public WindowTestStepTypeDoubleClick(DoubleClickWindowPresenter presenter, ViewEventBus viewEventBus,
                                         PropertyService propertyService) {
        super(WindowSize.SMALL, viewEventBus, propertyService);
        this.presenter = presenter;
    }

    @PostConstruct
    private void initView() {
        presenter.init(this);
    }

    @Override
    protected Component buildContent() throws LocalizedException {
        addSection(getLocaleValue("view.viewedit.section.values"), comboBox);
        presenter.fillComponentsWithValues();
        return getMainLayout();
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.WindowTestStepTypeDoubleClick.header");
    }

    @Override
    public void publishCloseSuccessEvent() {
        getEventBus().publish(this, new TestStepTypeParameterPreparedEvent(presenter.getTestStep()));
    }

    private DoubleClickGenerator getGenerator() {
        DoubleClickGenerator generator = (DoubleClickGenerator) presenter.getTestStep().getGenerator();
        String itemTag = comboBox.getSelectedItem().get().getTag();
        String itemType = comboBox.getSelectedItem().get().getType();
        String combinedParameter = itemType + Regex.INNER_PARAMETER_SPLITTER.getRegex() + String.valueOf(itemTag);
        generator.setItemTag(combinedParameter);
        return generator;
    }

    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        presenter.enterView(parameters);
        initWindow();
    }

    @Override
    protected boolean readyToClose() {
        if (!getGenerator().validate()) {
            TedamNotification.showNotification(getLocaleValue("window.readytoclose.teststeptype"), NotifyType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    protected void windowClose() {
    }

    public void createMenuBar(String itemTag, List<DoubleClick> doubleClickList) {
        sortDoubleClickByTitle(doubleClickList);

        comboBox.clear();
        comboBox.setItems(doubleClickList);
        comboBox.setItemCaptionGenerator(new ItemCaptionGenerator<DoubleClick>() {

            private static final long serialVersionUID = 1L;

            @Override
            public String apply(DoubleClick item) {
                return item.getTitle();
            }
        });

        setSelectedDoubleClick(itemTag, doubleClickList);
    }

    private void setSelectedDoubleClick(String itemTag, List<DoubleClick> doubleClickList) {
        String[] split = itemTag.split(Regex.INNER_PARAMETER_SPLITTER.getRegex());
        for (DoubleClick doubleClick : doubleClickList) {
            if ((itemTag != null) && split[split.length - 1].equalsIgnoreCase(doubleClick.getTag())) {
                comboBox.setSelectedItem(doubleClick);
                break;
            }
        }
    }

    private void sortDoubleClickByTitle(List<DoubleClick> doubleClickList) {
        Collections.sort(doubleClickList, new Comparator<DoubleClick>() {

            @Override
            public int compare(DoubleClick o1, DoubleClick o2) {
                return o1.getTitle().compareToIgnoreCase(o2.getTitle());
            }
        });
    }
}
