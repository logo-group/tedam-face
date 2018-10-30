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

package com.lbs.tedam.ui.dialog;

import com.vaadin.ui.UI;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Confirmation wrapper class.
 */
public class TedamDialog {

    /**
     * Confirmation dialog.
     *
     * @param ui             UI instance.
     * @param listener       Listener to execute events on confirm or cancel.
     * @param message        Confirmation message.
     * @param confirmCaption Confirm button caption.
     * @param cancelCaption  Cancel button caption.
     */
    public static void confirm(UI ui, ConfirmationListener listener, String message, String confirmCaption, String cancelCaption) {
        ConfirmDialog.show(ui, "", message, confirmCaption, cancelCaption, new ConfirmDialog.Listener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    listener.onConfirm();
                } else {
                    listener.onCancel();
                }
            }
        }).removeAllCloseShortcuts();
    }

}
