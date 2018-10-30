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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class holds grid filter value to save and reload.
 *
 * @author Faruk.Bozan
 */
public class GridFilterValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Grid id of filters.
     */
    private String gridId;

    /**
     * List of column filter values.
     */
    private List<ColumnFilterValue> columnFiltreValues = new ArrayList<>();

    /**
     * @return the gridId
     */
    public String getGridId() {
        return gridId;
    }

    /**
     * @param gridId the gridId to set
     */
    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    /**
     * @return the columnFiltreValues
     */
    public List<ColumnFilterValue> getColumnFiltreValues() {
        return columnFiltreValues;
    }

    /**
     * @param columnFiltreValues the columnFiltreValues to set
     */
    public void setColumnFiltreValues(List<ColumnFilterValue> columnFiltreValues) {
        this.columnFiltreValues = columnFiltreValues;
    }

}
