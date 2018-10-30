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

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.annotation.PrototypeScope;

@SpringComponent
@PrototypeScope
public class ConfirmPopup implements TedamLocalizerWrapper {

    @Autowired
    public ConfirmDialogFactory confirmDialogFactory;

    /**
     * Shows the standard before leave confirm dialog on given ui. If the user confirms the the navigation, the given {@literal runOnConfirm} will be executed. Otherwise, nothing
     * will be done.
     *
     * @param view         the view in which to show the dialog
     * @param runOnConfirm the runnable to execute if the user presses {@literal confirm} in the dialog
     */
    public void showLeaveViewConfirmDialog(View view, Runnable runOnConfirm) {
        showLeaveViewConfirmDialog(view, runOnConfirm, () -> {
            // Do nothing on cancel
        });
    }

    /**
     * Shows the standard before leave confirm dialog on given ui. If the user confirms the the navigation, the given {@literal runOnConfirm} will be executed. Otherwise, the given
     * {@literal runOnCancel} runnable will be executed.
     *
     * @param view         the view in which to show the dialog
     * @param runOnConfirm the runnable to execute if the user presses {@literal confirm} in the dialog
     * @param runOnCancel  the runnable to execute if the user presses {@literal cancel} in the dialog
     */
    public void showLeaveViewConfirmDialog(View view, Runnable runOnConfirm, Runnable runOnCancel) {
        ConfirmDialog dialog = confirmDialogFactory.create(getLocaleValue("confirm.header.discardchanges"), getLocaleValue("confirm.message.discardchanges"),
                getLocaleValue("general.button.discardchanges"), getLocaleValue("general.button.cancel"), null);
        dialog.show(view.getViewComponent().getUI(), event -> {
            if (event.isConfirmed()) {
                runOnConfirm.run();
            } else {
                runOnCancel.run();
            }
        }, true);
    }

}
