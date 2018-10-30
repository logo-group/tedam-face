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

package com.lbs.tedam.ui.components.window.teststep.gridsearch;

import com.lbs.tedam.localization.TedamLocalizerWrapper;
import com.lbs.tedam.model.DTO.GridCell;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.grid.TedamDynamicGrid;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Extension of native Vaadin Grid.
 *
 * @param <T> Class type for grid objects.
 */
@SpringComponent
@ViewScope
public class TedamDynamicGridSearch extends TedamDynamicGrid<List<GridCell>> implements TedamLocalizerWrapper {

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
    public TedamDynamicGridSearch() {
        super(SelectionMode.NONE);
        selectedComponent = new HashMap<GridCell, TedamTextField>();
    }

    @Override
    public void initData(List<GridCell> gridData) {
        for (GridCell entry : gridData) {
            if (entry.getValue() != null) {
                addComponentColumn(h -> {
                    GridCell gridCell = h.get(index++ % gridData.size());
                    return getTextField(gridCell);
                }).setCaption(getLocaleValue("window.tedamdynamicgridsearch.value"));
            } else {
                addColumn(h -> {
                    GridCell gridCell = h.get(index++ % gridData.size());
                    return gridCell.getTag() + " - " + gridCell.getCaption();
                }).setCaption(getLocaleValue("window.tedamdynamicgridsearch.caption"));
            }
        }

    }

    @Override
    public List<GridCell> getSelectedComponent() {
        List<GridCell> gridCellList = new ArrayList<>();
        for (Entry<GridCell, TedamTextField> entry : getSelectedComponentMap().entrySet()) {
            TedamTextField textField = entry.getValue();
            GridCell gridCell = entry.getKey();
            gridCell.setValue(textField.getValue());
            gridCellList.add(gridCell);
        }
        return gridCellList;
    }

    @Override
    public void setSelectedComponent(List<GridCell> data) {
        for (GridCell gridCell : data) {
            TedamTextField textField = createTextField(gridCell);
            textField.setValue(gridCell.getValue());
            getSelectedComponentMap().put(gridCell, textField);
        }
    }

    @Override
    protected void resetSelectedComponent() {
        selectedComponent = new HashMap<GridCell, TedamTextField>();
    }

    private TedamTextField getTextField(GridCell gridCell) {
        HashMap<GridCell, TedamTextField> map = getSelectedComponentMap();
        if (map.containsKey(gridCell)) {
            return map.get(gridCell);
        } else {
            return createTextField(gridCell);
        }
    }

    private TedamTextField createTextField(GridCell gridCell) {
        TedamTextField textField = new TedamTextField("", "", false, true);
        textField.setData(gridCell);
        textField.setId("id_" + gridCell.getTag());
        textField.addValueChangeListener(new ValueChangeListener<String>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent<String> event) {
                if (StringUtils.isEmpty(event.getValue().trim())) {
                    getSelectedComponentMap().remove(gridCell);
                } else {
                    getSelectedComponentMap().put(gridCell, textField);
                }
            }
        });
        return textField;
    }

    @SuppressWarnings("unchecked")
    private HashMap<GridCell, TedamTextField> getSelectedComponentMap() {
        return ((HashMap<GridCell, TedamTextField>) selectedComponent);
    }

}
