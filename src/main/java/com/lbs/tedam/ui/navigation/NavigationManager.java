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

package com.lbs.tedam.ui.navigation;

import com.lbs.tedam.ui.view.testcase.TestCaseGridView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.internal.Conventions;
import com.vaadin.spring.navigator.SpringNavigator;
import org.springframework.stereotype.Component;

/**
 * Governs view navigation of the app.
 */
@Component
@UIScope
public class NavigationManager extends SpringNavigator {

    private static final long serialVersionUID = 1L;

    /**
     * Find the view id (URI fragment) used for a given view class.
     *
     * @param viewClass the view class to find the id for
     * @return the URI fragment for the view
     */
    public String getViewId(Class<? extends View> viewClass) {
        SpringView springView = viewClass.getAnnotation(SpringView.class);
        if (springView == null) {
            throw new IllegalArgumentException("The target class must be a @SpringView");
        }

        return Conventions.deriveMappingForView(viewClass, springView);
    }

    /**
     * Navigate to the given view class.
     *
     * @param viewClass the class of the target view, must be annotated using {@link SpringView @SpringView}
     */
    public void navigateTo(Class<? extends View> targetView) {
        String viewId = getViewId(targetView);
        navigateTo(viewId);
    }

    public void navigateTo(Class<? extends View> targetView, Object parameter) {
        String viewId = getViewId(targetView);
        navigateTo(viewId + "/" + parameter.toString());
    }

    public void navigateToDefaultView() {
        // If the user wants a specific view, it's in the URL.
        // Otherwise admin goes to DashboardView and everybody else to
        // OrderListView
        if (!getState().isEmpty()) {
            return;
        }
        navigateTo(TestCaseGridView.class);
    }

    /**
     * Update the parameter of the the current view without firing any navigation events.
     *
     * @param parameter the new parameter to set, never <code>null</code>, <code>""</code> to not use any parameter
     */
    public void updateViewParameter(String parameter) {
        String viewName = getViewId(getCurrentView().getClass());
        String parameters;
        if (parameter == null) {
            parameters = "";
        } else {
            parameters = parameter;
        }

        updateNavigationState(new ViewChangeEvent(this, getCurrentView(), getCurrentView(), viewName, parameters));
    }

}
