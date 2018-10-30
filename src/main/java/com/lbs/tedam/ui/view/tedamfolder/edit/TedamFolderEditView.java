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

package com.lbs.tedam.ui.view.tedamfolder.edit;

import com.lbs.tedam.data.service.TedamFolderService;
import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.TedamFolderComboBox;
import com.lbs.tedam.ui.components.combobox.TedamFolderComboBoxDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.spring.annotation.SpringView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView
public class TedamFolderEditView extends AbstractEditView<TedamFolder, TedamFolderService, TedamFolderEditPresenter, TedamFolderEditView> {

    private static final long serialVersionUID = 1L;

    private TedamTextField name;
    private TedamFolderComboBox parentFolder;

    @Autowired
    public TedamFolderEditView(TedamFolderEditPresenter presenter, TedamFolderComboBox parentFolder) {
        super(presenter);
        this.parentFolder = parentFolder;
    }

    @PostConstruct
    private void initView() {
        name = new TedamTextField("view.tedamfolderedit.textfield.name", "full", true, true);

        addSection(getLocaleValue("view.viewedit.section.general"), 0, null, name, parentFolder);

        getCancel().setVisible(false);
        getSave().setVisible(false);

        getPresenter().setView(this);
    }

    @Override
    public String getHeader() {
        return getLocaleValue("view.tedamfolderedit.header");
    }

    public void organizeTedamFolderComboBox(TedamFolderComboBoxDataProvider tedamFolderComboBoxDataProvider) {
        parentFolder.setDataProvider(tedamFolderComboBoxDataProvider.getListDataProvider());
    }

}