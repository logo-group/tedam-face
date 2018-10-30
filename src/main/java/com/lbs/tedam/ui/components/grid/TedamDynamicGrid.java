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

package com.lbs.tedam.ui.components.grid;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.vaadin.ui.Grid;

/**
 * Extension of native Vaadin Grid.
 *
 * @param <T> Class type for grid objects.
 */
public abstract class TedamDynamicGrid<T> extends Grid<T> implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;
    protected Object selectedComponent;
    protected int index = 0;
    /**
     * Menu bar column for read and delete operations.
     */

    private SelectionMode selectionMode;

    /**
     * One parameter constructor.
     *
     * @param config       Grid config instance to build grid.
     * @param dataProvider Data provider.
     */
    public TedamDynamicGrid(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        init();
    }

    public void resetGrid() {
        index = 0;
        resetSelectedComponent();
        removeAllColumns();
    }

    protected abstract void resetSelectedComponent();

    private void init() {
        setSelectionMode(selectionMode);
        setResponsive(true);
        setSizeFull();
        setHeightByRows(9);
    }

    public abstract void initData(T gridData);

    public abstract T getSelectedComponent();

    public abstract void setSelectedComponent(T selectedData);

}
