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

package com.lbs.tedam.ui.components;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Singleton factory for creating the "are you sure"-type confirmation dialogs
 * in the application.
 */
@SpringComponent
public class ConfirmDialogFactory extends DefaultConfirmDialogFactory {

    private static final long serialVersionUID = 1L;

    @Override
    protected List<Button> orderButtons(Button cancel, Button notOk, Button ok) {
        return Arrays.asList(ok, cancel);
    }

    @Override
    protected Button buildOkButton(String okCaption) {
        Button okButton = super.buildOkButton(okCaption);
        okButton.addStyleName(ValoTheme.BUTTON_DANGER);
        return okButton;
    }
}
