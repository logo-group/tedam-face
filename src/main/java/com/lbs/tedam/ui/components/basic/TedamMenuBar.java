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

package com.lbs.tedam.ui.components.basic;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Extension of native Vaadin MenuBar.
 */
public class TedamMenuBar extends MenuBar implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    public TedamMenuBar(String id) {
        super();
        init(id);
    }

    private void init(String id) {
        setId(id);
        addStyleName(ValoTheme.MENUBAR_SMALL);
        setResponsive(true);
    }

}
