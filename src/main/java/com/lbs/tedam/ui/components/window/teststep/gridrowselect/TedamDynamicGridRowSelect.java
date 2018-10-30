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

package com.lbs.tedam.ui.components.window.teststep.gridrowselect;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.DTO.GridCell;
import com.lbs.tedam.ui.components.grid.TedamDynamicGrid;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension of native Vaadin Grid.
 *
 * @param <T> Class type for grid objects.
 */
@SpringComponent
@ViewScope
public class TedamDynamicGridRowSelect extends TedamDynamicGrid<List<GridCell>> implements TedamLocalizerWrapper {

    private static final long serialVersionUID = 1L;

    /**
     * Menu bar column for read and delete operations.
     */

    /**
     * One parameter constructor.
     *
     * @param config       Grid config instance to build grid.
     * @param dataProvider Data provider.
     */
    public TedamDynamicGridRowSelect() {
        super(SelectionMode.MULTI);
    }

    /**
     * We are setting columns for row selection
     *
     * @param captions
     * @author Ahmet.Izgi
     */
    @Override
    public void initData(List<GridCell> totalRows) {
        for (GridCell entry : totalRows) {
            addColumn(h -> {
                GridCell gridCell = h.get(index++ % totalRows.size());
                return gridCell.getValue();
            }).setCaption(entry.getCaption());
        }
    }

    @Override
    public List<GridCell> getSelectedComponent() {
        List<GridCell> tempGridCellList = new ArrayList<>();
        for (List<GridCell> gridRow : getSelectedItems()) {
            tempGridCellList.add(gridRow.get(0));
        }
        return tempGridCellList;
    }

    @Override
    public void setSelectedComponent(List<GridCell> data) {
    }

    @Override
    protected void resetSelectedComponent() {
    }

}
