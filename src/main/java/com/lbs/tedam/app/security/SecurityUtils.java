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
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.ui.components.grid.GridFilterValue;
import com.lbs.tedam.util.EnumsV2.TedamUserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SecurityUtils takes care of all such static operations that have to do with security and querying rights from different beans of the UI.
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Util methods only
    }

    private static UserSessionAttr getUserSessionAttr() {
        SecurityContext context = SecurityContextHolder.getContext();
        UserSessionAttr userSessionAttr = (UserSessionAttr) context.getAuthentication().getPrincipal();
        return userSessionAttr;
    }

    public static Project getUserSessionProject() {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        return userSessionAttr.getProject();
    }

    public static void setUserSessionProject(Project project) {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        userSessionAttr.setProject(project);
    }

    public static TedamUser getUser() {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        return userSessionAttr.getTedamUser();
    }

    public static void saveFilterValue(String viewName, GridFilterValue gridFilterValue) {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        userSessionAttr.saveFilterValue(viewName, gridFilterValue);
    }

    public static void saveSelectedFolder(String viewName, TedamFolder tedamFolder) {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        userSessionAttr.saveSelectedFolder(viewName, tedamFolder);
    }

    public static TedamFolder loadSelectedFolder(String viewName) {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        return userSessionAttr.loadSelectedFolder(viewName);
    }

    public static GridFilterValue loadFilterValue(String viewName, String filterId) {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        return userSessionAttr.loadFilterValue(viewName, filterId);
    }

    public static void clearFilterValue(String viewName, String filterId) {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        userSessionAttr.clearFilterValue(viewName, filterId);
    }

    /**
     * Check if currently signed-in user is in the role with the given role name.
     *
     * @param role the role to check for
     * @return <code>true</code> if user is in the role, <code>false</code> otherwise
     */
    public static boolean isCurrentUserInRole(TedamUserRole role) {
        return getUserRoles().stream().filter(roleName -> roleName.equals(Objects.requireNonNull(role.toString()))).findAny().isPresent();
    }

    /**
     * Gets the roles the currently signed-in user belongs to.
     *
     * @return a set of all roles the currently signed-in user belongs to.
     */
    public static Set<String> getUserRoles() {
        SecurityContext context = SecurityContextHolder.getContext();
        return context.getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public static UserSessionAttr getCurrentUser(TedamUserService userService) throws LocalizedException {
        UserSessionAttr userSessionAttr = getUserSessionAttr();
        TedamUser tedamUser = userService.findByUserName(userSessionAttr.getUsername());
        userSessionAttr.setTedamUser(tedamUser);
        return userSessionAttr;

    }

}
