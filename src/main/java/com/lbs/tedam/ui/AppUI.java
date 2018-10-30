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

package com.lbs.tedam.ui;

import com.lbs.tedam.ui.localization.LocalizationManager;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.view.access.AccessDeniedView;
import com.lbs.tedam.util.HasLogger;
import com.vaadin.addon.charts.ChartOptions;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("apptheme")
@SpringUI
@Viewport("width=device-width,initial-scale=1.0,user-scalable=no")
@Title("TEDAMFacev2")
public class AppUI extends UI implements HasLogger {

    private static final long serialVersionUID = 1L;

    private final SpringViewProvider viewProvider;
    private final NavigationManager navigationManager;
    private final MainView mainView;

    @Autowired
    public AppUI(LocalizationManager localizationManager, SpringViewProvider viewProvider, NavigationManager navigationManager, MainView mainView) {
        this.viewProvider = viewProvider;
        this.navigationManager = navigationManager;
        this.mainView = mainView;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setErrorHandler(event -> {
            Throwable t = DefaultErrorHandler.findRelevantThrowable(event.getThrowable());
            getLogger().error("Error during request", t);
        });

        // Set the theme ("globally") for all Charts
        ChartOptions.get(this).setTheme(new ChartsTheme());

        viewProvider.setAccessDeniedViewClass(AccessDeniedView.class);
        setContent(mainView);

        navigationManager.navigateToDefaultView();
    }

}
