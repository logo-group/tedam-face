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

package com.lbs.tedam.app.security;

import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.LocaleConstants;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final TedamUserService userService;

    @Autowired
    public UserDetailsServiceImpl(TedamUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TedamUser tedamUser;
        try {
            tedamUser = userService.findByUserName(username);
        } catch (LocalizedException e) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }
        if (null == tedamUser) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        }
        Project project = getProject(tedamUser);
        Locale locale = getLocale();
        return new UserSessionAttr(tedamUser, project, locale);
    }

    // TODO: the default should come.
    private Project getProject(TedamUser tedamUser) {
        Project project = tedamUser.getProjects().iterator().next();
        return project;
    }

    // TODO: the default should come.
    private Locale getLocale() {
        Locale locale = LocaleConstants.LOCALE_TRTR;
        return locale;
    }

}