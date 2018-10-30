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

package com.lbs.tedam.ui.view;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.TreeDataProvider;

import java.io.Serializable;

/**
 * Abstract wrapper class for TreeDataProvider.
 *
 * @param <T>
 */
public class AbstractTreeDataProvider<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private TreeDataProvider<T> treeDataProvider;

    public TreeDataProvider<T> getTreeDataProvider() {
        return treeDataProvider;
    }

    public void buildTreeDataProvider(TreeData<T> treeData) {
        treeDataProvider = new TreeDataProvider<>(treeData);
    }

    public void removeItem(T item) {
        treeDataProvider.getTreeData().removeItem(item);
    }

}
