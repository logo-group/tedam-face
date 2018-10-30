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

package com.lbs.tedam.ui.util;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;

public class TedamNotification {
    public static void showNotification(String message, NotifyType notify) {
        Notification notification = new Notification(message);
        notification.setIcon(VaadinIcons.EXCLAMATION);
        // notification.setDelayMsec(Helper.NOTIFICATION_DELAYMSEC);
        switch (notify) {
            case SUCCESS:
                notification.setStyleName("success");
                notification.setDelayMsec(2000);
                break;
            case ERROR:
                notification.setStyleName("error");
                // notification.setDelayMsec(-1);
                break;
            case WARNING:
                notification.setStyleName("warning");
                // notification.setDelayMsec(-1);
                break;
        }
        notification.setPosition(Position.TOP_RIGHT);
        notification.show(Page.getCurrent());
    }

    public static void showTrayNotification(String message, NotifyType notify) {
        Notification notification = new Notification(message, Notification.Type.TRAY_NOTIFICATION);
        switch (notify) {
            case SUCCESS:
                notification.setStyleName("success");
                notification.setDelayMsec(2000);
                break;
            case ERROR:
                notification.setStyleName("error");
                // notification.setDelayMsec(-1);
                break;
            case WARNING:
                notification.setStyleName("warning");
                // notification.setDelayMsec(-1);
                break;
        }
        notification.show(Page.getCurrent());
    }

    public enum NotifyType {
        SUCCESS, ERROR, WARNING
    }
}
