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

package com.lbs.tedam.ui.components.window.teststep.formopen;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.MenuPathService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.MenuPath;
import com.lbs.tedam.ui.view.AbstractTreeDataProvider;
import com.vaadin.data.TreeData;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
@PrototypeScope
public class MenuPathDataProvider extends AbstractTreeDataProvider<MenuPath> implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    @Autowired
    public MenuPathDataProvider(MenuPathService menuPathService) throws LocalizedException {
        List<MenuPath> menuPathList = menuPathService.getMenuPathListByProject(SecurityUtils.getUserSessionProject());
        TreeData<MenuPath> menuFolderTreeData = buildTreeData(menuPathList);
        buildTreeDataProvider(menuFolderTreeData);
    }

    private TreeData<MenuPath> buildTreeData(List<MenuPath> menuPathList) {
        TreeData<MenuPath> menuFolderTreeData = new TreeData<>();
        List<MenuPath> leafList = menuPathList.stream().filter(menuPath -> menuPath.getParentMenuPath() != null).collect(Collectors.toList());
        for (MenuPath menuPath : menuPathList) {
            menuPath.setCaption(getFormOpenLocaleValue(menuPath.getMenuTag().toString()));
            menuFolderTreeData.addRootItems(menuPath);
        }
        for (MenuPath menuPath : leafList) {
            menuFolderTreeData.setParent(menuPath, menuPath.getParentMenuPath());
        }
        return menuFolderTreeData;
    }

}