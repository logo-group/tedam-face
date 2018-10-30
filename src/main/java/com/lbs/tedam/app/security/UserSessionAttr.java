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

import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.model.TedamUser;
import com.lbs.tedam.ui.components.grid.GridFilterValue;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

public class UserSessionAttr extends User {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private TedamUser tedamUser;

    // TODO: It will be considered.
    private Project project;

    private Locale locale;

    private Map<String, List<GridFilterValue>> userFilterValues = new HashMap<>();

    private Map<String, TedamFolder> userTreeValues = new HashMap<>();

    public UserSessionAttr(TedamUser tedamUser, Project project, Locale locale) {
        super(tedamUser.getUserName(), tedamUser.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(tedamUser.getRole().toString())));
        this.setProject(project);
        this.setTedamUser(tedamUser);
        this.setLocale(locale);
    }

    public TedamUser getTedamUser() {
        return tedamUser;
    }

    public void setTedamUser(TedamUser tedamUser) {
        this.tedamUser = tedamUser;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void saveSelectedFolder(String viewName, TedamFolder selectedFolder) {
        userTreeValues.put(viewName, selectedFolder);
    }

    public TedamFolder loadSelectedFolder(String viewName) {
        return userTreeValues.get(viewName);
    }

    public void saveFilterValue(String viewName, GridFilterValue gridFilterValue) {
        if (!userFilterValues.containsKey(viewName)) {
            userFilterValues.put(viewName, new ArrayList<>());
        }
        List<GridFilterValue> filterList = userFilterValues.get(viewName);
        Iterator<GridFilterValue> iterator = filterList.iterator();
        while (iterator.hasNext()) {
            GridFilterValue next = iterator.next();
            if (next.getGridId().equals(gridFilterValue.getGridId())) {
                iterator.remove();
            }
        }
        filterList.add(gridFilterValue);
    }

    public GridFilterValue loadFilterValue(String viewName, String filterId) {
        List<GridFilterValue> filterList = userFilterValues.get(viewName);
        if (filterList != null) {
            for (GridFilterValue gridFilterValue : filterList) {
                if (gridFilterValue.getGridId().equals(filterId)) {
                    return gridFilterValue;
                }
            }
        }
        return null;
    }

    public void clearFilterValue(String viewName, String filterId) {
        List<GridFilterValue> filterList = userFilterValues.get(viewName);
        if (filterList != null) {
            Iterator<GridFilterValue> iterator = filterList.iterator();
            while (iterator.hasNext()) {
                GridFilterValue next = iterator.next();
                if (next.getGridId().equals(filterId)) {
                    iterator.remove();
                }
            }
        }
    }

}
