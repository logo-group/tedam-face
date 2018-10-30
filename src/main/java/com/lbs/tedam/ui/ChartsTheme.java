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

import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.addon.charts.model.style.Theme;
import com.vaadin.addon.charts.themes.ValoLightTheme;

/**
 * Theme for Vaadin Charts. See {@link ValoLightTheme} for a more complex theme.
 */
public class ChartsTheme extends Theme {

    private static final long serialVersionUID = 1L;
    private static final SolidColor COLOR1 = new SolidColor("#a56284");
    private static final SolidColor COLOR2 = new SolidColor("#6c6c93");
    private static final SolidColor COLOR3 = new SolidColor("#fb991c");

    public ChartsTheme() {
        setColors(COLOR1, COLOR2, COLOR3);
        getTitle().setColor(COLOR1);
        getTitle().setFontSize("inherit"); // inherit from CSS
    }
}
