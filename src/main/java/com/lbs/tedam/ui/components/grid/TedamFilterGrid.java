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

import com.lbs.tedam.ui.components.grid.GridColumns.DataType;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Extension of native Vaadin Grid.
 *
 * @param <T> Class type for grid objects.
 */
public class TedamFilterGrid<T> extends TedamGrid<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Filters.
     */
    private TedamGridCellFilter<T> filter = null;

    /**
     * One parameter constructor.
     *
     * @param config       Grid config instance to build grid.
     * @param dataProvider Data provider.
     */
    public TedamFilterGrid(TedamGridConfig<T> config, AbstractDataProvider<T> dataProvider, SelectionMode selectionMode) {
        super(config, dataProvider, selectionMode);
        initFilters();
    }

    public TedamFilterGrid(TedamGridConfig<T> config, SelectionMode selectionMode) {
        super(config, selectionMode);
    }

    public void initFilters() {
        if (filter != null) {
            this.removeHeaderRow(filter.getFilterRow());
        }
        setFilter(new TedamGridCellFilter<>(this, getConfig().getBeanType()));
        buildFilters(getConfig());
    }

    /**
     * @return the filter
     */
    public TedamGridCellFilter<T> getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    private void setFilter(TedamGridCellFilter<T> filter) {
        this.filter = filter;
    }

    /**
     * Inits grid filters by config parameter.
     *
     * @param config To get filter columns and types.
     */
    private void buildFilters(TedamGridConfig<T> config) {
        List<GridColumn> columnNames = config.getColumnList();
        for (GridColumn column : columnNames) {
            switch (column.getDataType()) {
                case TEXT:
                    getFilter().setTextFilter(column.getColumnName(), true, false);
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case SELECT_ENUM:
                    getFilter().setTedamComboBoxFilter(column.getColumnName(), column.getFilterBeanType());
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case BOOLEAN:
                    getFilter().setTedamBooleanFilter(column.getColumnName());
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case DATE:
                    getFilter().setLocalDateFilter(column.getColumnName());
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case INTEGER:
                    getFilter().setNumberFilter(column.getColumnName(), Integer.class);
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case TEXT_AREA:
                    getFilter().setTextFilter(column.getColumnName(), true, false);
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case DATE_TIME:
                    getFilter().setLocalDateTimeFilter(column.getColumnName());
                    getFilter().getCellFilter(column.getColumnName()).getComponent().setId(column.getColumnName());
                    break;
                case NONE:
                    break;
                default:
                    break;
            }
        }
    }

    public GridFilterValue saveFilterValues() {
        GridFilterValue gridFilterValue = new GridFilterValue();
        gridFilterValue.setGridId(this.getId());
        for (Column<T, ?> column : getColumns()) {
            String columnId = column.getId();
            if (columnId == null)
                continue;
            GridColumn gridColumn = getConfig().getColumnMap().get(columnId);
            DataType dataType = gridColumn.getDataType();
            Object filterValue = getColumnFilterValue(columnId, dataType);
            ColumnFilterValue columnFilterValue = new ColumnFilterValue(columnId, filterValue, dataType);
            gridFilterValue.getColumnFiltreValues().add(columnFilterValue);
        }
        return gridFilterValue;
    }

    public void laodFilterValues(GridFilterValue gridFilterValue) {
        if (gridFilterValue != null) {
            if (gridFilterValue.getGridId().equals(this.getId())) {
                List<ColumnFilterValue> columnFiltreValues = gridFilterValue.getColumnFiltreValues();
                for (ColumnFilterValue columnFilterValue : columnFiltreValues) {
                    setColumnFilterValue(columnFilterValue);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object getColumnFilterValue(String columnId, DataType dataType) {
        switch (dataType) {
            case TEXT:
            case TEXT_AREA:
                return ((TextField) getFilter().getCellFilter(columnId).getComponent()).getValue();
            case SELECT_ENUM:
            case BOOLEAN:
                return ((ComboBox<T>) getFilter().getCellFilter(columnId).getComponent()).getValue();
            case INTEGER:
                String startIntValue = ((TextField) ((HorizontalLayout) getFilter().getCellFilter(columnId).getComponent())
                        .getComponent(0)).getValue();
                String endIntValue = ((TextField) ((HorizontalLayout) getFilter().getCellFilter(columnId).getComponent())
                        .getComponent(1)).getValue();
                return new Object[]{startIntValue, endIntValue};
            case DATE:
            case DATE_TIME:
                LocalDateTime startDateValue = ((DateTimeField) ((HorizontalLayout) getFilter().getCellFilter(columnId)
                        .getComponent()).getComponent(0)).getValue();
                LocalDateTime endDateValue = ((DateTimeField) ((HorizontalLayout) getFilter().getCellFilter(columnId)
                        .getComponent()).getComponent(1)).getValue();
                return new Object[]{startDateValue, endDateValue};
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void setColumnFilterValue(ColumnFilterValue columnFilterValue) {
        switch (columnFilterValue.getDataType()) {
            case TEXT:
            case TEXT_AREA:
                ((TextField) getFilter().getCellFilter(columnFilterValue.getColumnId()).getComponent())
                        .setValue((String) columnFilterValue.getFilterValue());
                break;
            case SELECT_ENUM:
            case BOOLEAN:
                ((ComboBox<T>) getFilter().getCellFilter(columnFilterValue.getColumnId()).getComponent())
                        .setValue((T) columnFilterValue.getFilterValue());
                break;
            case INTEGER:
                ((TextField) ((HorizontalLayout) getFilter().getCellFilter(columnFilterValue.getColumnId()).getComponent())
                        .getComponent(0)).setValue((String) ((Object[]) columnFilterValue.getFilterValue())[0]);
                ((TextField) ((HorizontalLayout) getFilter().getCellFilter(columnFilterValue.getColumnId()).getComponent())
                        .getComponent(1)).setValue((String) ((Object[]) columnFilterValue.getFilterValue())[1]);
                break;
            case DATE:
            case DATE_TIME:
                ((DateTimeField) ((HorizontalLayout) getFilter().getCellFilter(columnFilterValue.getColumnId())
                        .getComponent()).getComponent(0))
                        .setValue((LocalDateTime) ((Object[]) columnFilterValue.getFilterValue())[0]);
                ((DateTimeField) ((HorizontalLayout) getFilter().getCellFilter(columnFilterValue.getColumnId())
                        .getComponent()).getComponent(1))
                        .setValue((LocalDateTime) ((Object[]) columnFilterValue.getFilterValue())[1]);
                break;
            default:
                break;
        }
    }

}
