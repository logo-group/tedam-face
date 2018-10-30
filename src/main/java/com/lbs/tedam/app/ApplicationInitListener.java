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

package com.lbs.tedam.app;

import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Configures the VaadinService instance that serves the app through a servlet.
 * <p>
 * Uses a bootstrap listener to modify the bootstrap HTML page and include icons
 * for home screen for mobile devices.
 */
@Component
public class ApplicationInitListener implements VaadinServiceInitListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        VaadinService service = serviceInitEvent.getSource();

        service.addSessionInitListener(event -> event.getSession().addBootstrapListener(new IconBootstrapListener()));
    }
}
