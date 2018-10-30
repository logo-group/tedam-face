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
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Extension of native Vaadin Button.
 */
public class TedamButton extends Button implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    public TedamButton(String id, Resource icon) {
        init(id);
        setIcon(icon);
    }

    public TedamButton(String id) {
        init(id);
    }

    private void init(String id) {
        setEnabled(true);
        setId(id);
        setCaption(getLocaleValue(id));
        setDescription(getLocaleValue(id));
        addStyleName(ValoTheme.BUTTON_TINY);
        setWidth("100%");
        setResponsive(true);
    }
}
