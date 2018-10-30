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

package com.lbs.tedam.ui.components.window.environment;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.EnvironmentService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.Environment;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.List;

@SpringComponent
@PrototypeScope
public class WindowSelectEnvironmentDataProvider extends AbstractDataProvider<Environment> {

    private static final long serialVersionUID = 1L;

    @Autowired
    public WindowSelectEnvironmentDataProvider(EnvironmentService environmentService, TedamUserService userService) throws LocalizedException {
        TedamUser user = SecurityUtils.getCurrentUser(userService).getTedamUser();
        Project project = SecurityUtils.getUserSessionProject();
        List<Environment> environmentListWithFavorites = environmentService.getEnvironmentListWithFavorites(user, project);
        buildListDataProvider(environmentListWithFavorites);
    }

}