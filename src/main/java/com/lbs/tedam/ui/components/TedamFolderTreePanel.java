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

package com.lbs.tedam.ui.components;

import com.lbs.tedam.model.TedamFolder;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamLabel;
import com.lbs.tedam.ui.components.basic.TedamTree;
import com.lbs.tedam.ui.components.layout.TedamHorizontalLayout;
import com.lbs.tedam.ui.components.layout.TedamVerticalLayout;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Resource;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class TedamFolderTreePanel extends TedamVerticalLayout {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private TedamButton btnAdd;
    private TedamButton btnEdit;
    private TedamButton btnRemove;
    private TedamButton btnShowAll;
    private TedamTree<TedamFolder> tedamTree;
    private Panel treePanel;

    private TedamFolderTreePanelListener listener;

    @PostConstruct
    public void init() {
        initUI();
        initTedamTree();
        buildVerticalLayout();
    }

    private void initUI() {
        setSizeFull();
    }

    private void initTedamTree() {
        tedamTree = new TedamTree<>();
        tedamTree.addSelectionListener(e -> listener.addSelectionListener(e));
    }

    private void buildVerticalLayout() {
        treePanel = new Panel();
        treePanel.setSizeFull();
        TedamHorizontalLayout footer = buildFooter();
        treePanel.setContent(tedamTree);
        addComponents(treePanel, footer);
        setExpandRatio(treePanel, 1);
    }

    private TedamHorizontalLayout buildFooter() {
        TedamHorizontalLayout footer = new TedamHorizontalLayout();
        footer.setStyleName("v-window-bottom-toolbar");
        btnAdd = buildButton("view.tedamfoldertreepanel.button.btnAdd", VaadinIcons.PLUS);
        btnAdd.addClickListener(e -> listener.addButtonClickOperations());
        btnEdit = buildButton("view.tedamfoldertreepanel.button.btnEdit", VaadinIcons.EDIT);
        btnEdit.addClickListener(e -> listener.editButtonClickOperations());
        btnRemove = buildButton("view.tedamfoldertreepanel.button.btnRemove", VaadinIcons.MINUS);
        btnRemove.addClickListener(e -> listener.removeButtonClickOperations());
        btnShowAll = buildButton("view.tedamfoldertreepanel.button.btnShowAll", VaadinIcons.ALIGN_JUSTIFY);
        btnShowAll.addClickListener(e -> listener.showAllButtonClickOperations());
        TedamLabel label = new TedamLabel(" ");
        footer.addComponents(btnAdd, btnRemove, btnEdit, btnShowAll, label);
        footer.setExpandRatio(label, 1);
        return footer;
    }

    private TedamButton buildButton(String id, Resource icon) {
        TedamButton button = new TedamButton(id, icon);
        button.setId(button.getId());
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        button.setWidthUndefined();
        button.setCaption("");
        return button;
    }

    public TedamTree<TedamFolder> getTedamTree() {
        return tedamTree;
    }

    public TedamFolder getSelectedFolder() {
        if (!tedamTree.getSelectedItems().isEmpty()) {
            return tedamTree.getSelectedItems().iterator().next();
        }
        return null;
    }

    public void setClickListener(TedamFolderTreePanelListener listener) {
        this.listener = listener;
    }

    public void loadSelectedFolder(TedamFolder selectedFolder) {
        List<TedamFolder> folderList = new ArrayList<>();
        if (selectedFolder != null) {
            while (selectedFolder.getParentFolder() != null) {
                folderList.add(selectedFolder);
                selectedFolder = selectedFolder.getParentFolder();
            }
            tedamTree.expand(folderList);
            tedamTree.select(folderList.get(0));
        }
    }

    public interface TedamFolderTreePanelListener {

        public void showAllButtonClickOperations();

        public void addSelectionListener(SelectionEvent<TedamFolder> e);

        public void removeButtonClickOperations();

        public void editButtonClickOperations();

        public void addButtonClickOperations();

    }

}
