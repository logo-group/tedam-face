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
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TedamGridConfig<T> implements TedamLocalizerWrapper {

    public abstract List<GridColumn> getColumnList();

    public abstract Class<T> getBeanType();

    public abstract List<RUDOperations> getRUDOperations();

    public Map<String, String> getColumnTitles() {
        Map<String, String> columnTitles = new HashMap<>();
        for (GridColumn gridColumn : getColumnList()) {
            columnTitles.put(gridColumn.getColumnName(), getLocaleValue(gridColumn.getResourceName()));
        }
        return columnTitles;
    }

    public Map<String, GridColumn> getColumnMap() {
        Map<String, GridColumn> columnMap = new HashMap<>();
        for (GridColumn gridColumn : getColumnList()) {
            columnMap.put(gridColumn.getColumnName(), gridColumn);
        }
        return columnMap;
    }

}
