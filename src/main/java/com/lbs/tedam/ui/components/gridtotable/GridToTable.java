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

package com.lbs.tedam.ui.components.gridtotable;

import com.lbs.tedam.model.DTO.GridCell;
import com.lbs.tedam.model.DTO.GridRow;
import com.lbs.tedam.util.Enums.ScriptParameters;
import com.lbs.tedam.util.EnumsV2.TestStepType;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
public class GridToTable extends GridLayout {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GridToTable.class);
    protected List<GridRow> gridRowList;

    public GridToTable() {
        super();
    }

    /**
     * GridToTable constructor method. It makes adjustments related to the UI and creates the screen according to the incoming fields.
     *
     * @param list
     * @param listOfParameterComponents
     * @param gridSelectType
     * @param selectedGridTag
     * @param oldValues
     */
    @SuppressWarnings("incomplete-switch")
    public GridToTable(List<GridRow> list, Map<String, Object> listOfParameterComponents, TestStepType gridSelectType, String selectedGridTag, String oldValues) {
        super(list.get(0).getSize(), list.size() + 1);
        LOGGER.info("");

        setStyleName("tedamGridLayout");
        Map<ScriptParameters, Object> paramaterMap = new HashMap<>();
        if (oldValues != null) {
            paramaterMap = GridToTableUtil.decomposeGridComponentParameter(selectedGridTag, oldValues);
        }

        this.gridRowList = list;
        switch (gridSelectType) {
            case GRID_DELETE:
            case GRID_DOUBLE_CLICK:
            case GRID_ROW_SELECT:
                buildRowSelectLayout(selectedGridTag, listOfParameterComponents, gridSelectType, paramaterMap);
                break;
            case GRID_CELL_SELECT:
                buildCellSelectLayout(selectedGridTag, listOfParameterComponents, paramaterMap);
                break;
            default:
                break;
        }

        setColumnExpandRatio(2, 1);

        setSpacing(true);
        setMargin(true);
        setSizeFull();
    }

    /**
     * Prepares the UI for CellSelect according to the incoming parameters.
     *
     * @param selectedGridTag
     * @param listOfParameterComponents
     * @param parameterMap
     */
    @SuppressWarnings("unchecked")
    public void buildCellSelectLayout(String selectedGridTag, final Map<String, Object> listOfParameterComponents, Map<ScriptParameters, Object> parameterMap) {

        String columnTag = (String) parameterMap.get(ScriptParameters.COLUMN_TAG);
        List<Integer> rowList = (List<Integer>) parameterMap.get(ScriptParameters.ROW_INDEX_LIST);
        for (int i = 0; i < gridRowList.get(0).getSize(); i++) {
            addComponent(buildHeaderElement(gridRowList.get(0).getCells().get(i).getCaption()));
        }

        for (int i = 0; i < gridRowList.size(); i++) {
            GridRow row = gridRowList.get(i);
            for (int j = 0; j < row.getSize(); j++) {
                final GridCell cell = row.getCells().get(j);
                final CheckBox chkSelectedCell = new CheckBox(cell.getValue());
                chkSelectedCell.addStyleName(ValoTheme.CHECKBOX_SMALL);
                chkSelectedCell.setWidth(null);
                chkSelectedCell.setData(cell);
                if (!parameterMap.isEmpty() && rowList.contains(i) && columnTag.equals(cell.getTag())) {
                    chkSelectedCell.setValue(true);
                }
                int rowIndex = i + 1;
                final String keyPrefix = selectedGridTag + "/row" + rowIndex + "/columnTag" + cell.getTag();
                // if (listOfParameterComponents.get(keyPrefix) != null) {
                // if ("true".equals(((GridCell) listOfParameterComponents.get(keyPrefix)).getValue())) {
                // chkSelectedCell.setValue(true);
                // } else {
                // chkSelectedCell.setValue(false);
                // }
                // } else if (listOfParameterComponents.get(keyPrefix) == null && chkSelectedCell.getValue()) {
                // cell.setValue(chkSelectedCell.getValue().toString());
                // listOfParameterComponents.put(keyPrefix, cell);
                // }
                chkSelectedCell.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = 4385406809354460016L;

                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        cell.setValue(chkSelectedCell.getValue().toString());
                        if (chkSelectedCell.getValue()) {
                            updateCheckBoxesInSameRow(chkSelectedCell);
                            // listOfParameterComponents.put(keyPrefix, cell);
                        }
                    }
                });
                addComponent(chkSelectedCell);
            }
        }
    }

    /**
     * For RowSelect, it prepares the UI according to the incoming parameters.
     *
     * @param selectedGridTag
     * @param listOfParameterComponents
     * @param gridSelectType
     * @param parameterMap
     */
    @SuppressWarnings("unchecked")
    public void buildRowSelectLayout(String selectedGridTag, final Map<String, Object> listOfParameterComponents, final TestStepType gridSelectType,
                                     Map<ScriptParameters, Object> parameterMap) {
        List<Integer> rowList = (List<Integer>) parameterMap.get(ScriptParameters.ROW_INDEX_LIST);
        setColumns(getColumns() + 1);
        addComponent(buildHeaderElement("Seç"));

        for (int i = 0; i < gridRowList.get(0).getSize(); i++) {
            addComponent(buildHeaderElement(gridRowList.get(0).getCells().get(i).getCaption()));
        }

        for (int i = 0; i < gridRowList.size(); i++) {
            GridRow row = gridRowList.get(i);
            boolean rowHeader = true;
            for (int j = 0; j < row.getSize(); j++) {
                final GridCell cell = row.getCells().get(j);
                if (rowHeader) {
                    final CheckBox chkSelectedCell = new CheckBox();
                    chkSelectedCell.addStyleName(ValoTheme.CHECKBOX_SMALL);
                    chkSelectedCell.setData(cell);
                    chkSelectedCell.setWidth(null);

                    int rowIndex = i + 1;
                    final String keyPrefix = selectedGridTag + "/row" + rowIndex;

                    if (!parameterMap.isEmpty() && rowList.contains(i)) {
                        chkSelectedCell.setValue(true);
                        // cell.setValue(chkSelectedCell.getValue().toString());
                    }
                    if (listOfParameterComponents.get(keyPrefix) != null) {
                        if ("true".equals(((GridCell) listOfParameterComponents.get(keyPrefix)).getValue())) {
                            chkSelectedCell.setValue(true);
                        } else {
                            chkSelectedCell.setValue(false);
                        }
                    } else if (listOfParameterComponents.get(keyPrefix) == null && chkSelectedCell.getValue()) {

                        listOfParameterComponents.put(keyPrefix, cell);
                    }
                    chkSelectedCell.addValueChangeListener(new ValueChangeListener() {

                        private static final long serialVersionUID = 7806835512683014113L;

                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            if (chkSelectedCell.getValue()) {
                                if (gridSelectType == TestStepType.GRID_DELETE || gridSelectType == TestStepType.GRID_DOUBLE_CLICK) {
                                    updateCheckRow(chkSelectedCell);
                                    listOfParameterComponents.clear();
                                }
                                cell.setValue(chkSelectedCell.getValue().toString());
                                listOfParameterComponents.put(keyPrefix, cell);
                            }
                        }
                    });
                    addComponent(chkSelectedCell);
                    rowHeader = false;
                    j--;
                } else {
                    addComponent(buildCellElement(cell.getValue()));
                }
            }
            setColumnExpandRatio(1, 1);
            setColumnExpandRatio(2, 5);
            setColumnExpandRatio(3, 5);
            setColumnExpandRatio(4, 5);
        }
    }

    /**
     * Used to construct the cell element.
     *
     * @param value
     * @return
     */
    public Component buildCellElement(String value) {
        Label check = new Label(value);
        check.addStyleName(ValoTheme.LABEL_SMALL);
        check.setWidthUndefined();
        return check;
    }

    /**
     * It is used to set the header part.
     *
     * @param title
     * @return
     */
    public Component buildHeaderElement(String title) {
        Label check = new Label(title);
        check.addStyleName(ValoTheme.LABEL_COLORED);
        check.addStyleName(ValoTheme.LABEL_SMALL);
        check.addStyleName(ValoTheme.LABEL_BOLD);
        check.setWidth(null);
        return check;
    }

    /**
     * Makes the value of all checkBoxes in gridToTable false.
     *
     * @param current
     */
    public void updateCheckRow(CheckBox current) {
        GridToTableUtil.updateCheckRow(current, this);
    }

    /**
     * Makes the value of the checkboxes on the same line false.
     *
     * @param current
     */
    public void updateCheckBoxesInSameRow(CheckBox current) {
        GridToTableUtil.updateCheckBoxesInSameRow(current, this);
    }

    /**
     * Checks whether value is set in gridToTable.
     *
     * @param gridToTable
     * @return
     */
    public boolean checkWizardReady() {
        return GridToTableUtil.checkWizardReady(this);
    }

}
