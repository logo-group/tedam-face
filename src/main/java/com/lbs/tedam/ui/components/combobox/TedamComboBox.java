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

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.NativeSelect;

/**
 * Extension of native Vaadin Button.
 */
public class TedamComboBox<T> extends NativeSelect<T> {

    private static final long serialVersionUID = 1L;

    public ListDataProvider<T> listDataProvider;

    public TedamComboBox() {
        setResponsive(true);
        setEmptySelectionAllowed(false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        this.listDataProvider = (ListDataProvider<T>) dataProvider;
        super.setDataProvider(dataProvider);
    }

    public ListDataProvider<T> getListDataProvider() {
        return listDataProvider;
    }

}
