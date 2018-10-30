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

package com.lbs.tedam.ui.components.window.teststep.gridcellselect;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.DTO.GridCell;
import com.lbs.tedam.ui.components.grid.TedamDynamicGrid;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.CheckBox;

import java.util.Arrays;
import java.util.List;

/**
 * Extension of native Vaadin Grid.
 *
 * @param <T> Class type for grid objects.
 */
@SpringComponent
@ViewScope
public class TedamDynamicGridCellSelect extends TedamDynamicGrid<List<GridCell>> implements TedamLocalizerWrapper {

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
    public TedamDynamicGridCellSelect() {
        super(SelectionMode.NONE);
        selectedComponent = new CheckBox();
    }

    @Override
    public void initData(List<GridCell> gridData) {
        for (GridCell entry : gridData) {
            addComponentColumn(h -> {
                GridCell gridCell = h.get(index++ % gridData.size());
                return createCheckbox(gridCell);
            }).setCaption(entry.getCaption());
        }
    }

    private CheckBox createCheckbox(GridCell gridCell) {
        CheckBox checkbox = new CheckBox(gridCell.getValue());
        checkbox.setData(gridCell);
        CheckBox checked = ((CheckBox) selectedComponent);
        if (checked.getData() != null && ((GridCell) checkbox.getData()).getTag().equals(((GridCell) checked.getData()).getTag())
                && ((GridCell) checkbox.getData()).getRowIndex() == (((GridCell) checked.getData()).getRowIndex())) {
            checkbox.setValue(true);
        }
        checkbox.addValueChangeListener(new ValueChangeListener<Boolean>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    if (checked.getData() != null) {
                        checked.setValue(false);
                    }
                    selectedComponent = event.getSource();
                } else {
                    ((CheckBox) selectedComponent).setData(null);
                }
            }
        });
        return checkbox;
    }

    @Override
    public List<GridCell> getSelectedComponent() {
        return Arrays.asList((GridCell) ((CheckBox) selectedComponent).getData());
    }

    @Override
    public void setSelectedComponent(List<GridCell> data) {
        ((CheckBox) selectedComponent).setData(data.get(0));
    }

    @Override
    protected void resetSelectedComponent() {
        ((CheckBox) selectedComponent).setData(null);
    }

}
