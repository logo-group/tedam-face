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

import java.io.Serializable;

/**
 * This class holds grid column filter value to save and reload.
 *
 * @author Faruk.Bozan
 */
public class ColumnFilterValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Column id.
     */
    private String columnId;

    /**
     * Filter value.
     */
    private Object filterValue;

    /**
     * Data type of column.
     */
    private DataType dataType;

    public ColumnFilterValue() {
    }

    public ColumnFilterValue(String columnId, Object filterValue, DataType dataType) {
        this.columnId = columnId;
        this.filterValue = filterValue;
        this.dataType = dataType;
    }

    /**
     * @return the columnId
     */
    public String getColumnId() {
        return columnId;
    }

    /**
     * @param columnId the columnId to set
     */
    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    /**
     * @return the filterValue
     */
    public Object getFilterValue() {
        return filterValue;
    }

    /**
     * @param filterValue the filterValue to set
     */
    public void setFilterValue(Object filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * @return the dataType
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

}
