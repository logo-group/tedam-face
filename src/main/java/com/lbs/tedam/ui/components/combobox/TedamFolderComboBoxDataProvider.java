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

package com.lbs.tedam.ui.components.combobox;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.TedamFolderService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.util.EnumsV2.TedamFolderType;
import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import java.util.List;

@SpringComponent
@PrototypeScope
public class TedamFolderComboBoxDataProvider extends AbstractDataProvider<TedamFolder> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final TedamFolderService tedamFolderService;

    @Autowired
    public TedamFolderComboBoxDataProvider(TedamFolderService tedamFolderService) {
        this.tedamFolderService = tedamFolderService;
    }

    public void buildDataProvider(TedamFolderType folderType) throws LocalizedException {
        List<TedamFolder> tedamFolderList = tedamFolderService.getTedamFolderListByProjectAndFolderType(SecurityUtils.getUserSessionProject(), folderType);
        tedamFolderList.sort((o1, o2) -> o1.findParentFolder(o1).compareTo(o2.findParentFolder(o2)));
        buildListDataProvider(tedamFolderList);
    }

}
