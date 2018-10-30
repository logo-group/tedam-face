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

/**
 *
 */
package com.lbs.tedam.ui.components.gridtotable;

import com.lbs.tedam.util.Enums.Regex;
import com.lbs.tedam.util.Enums.ScriptParameters;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It contains the helper methods of the GridToTable class.
 *
 * @author Ahmet.Izgi
 */
public class GridToTableUtil {

    /**
     * Makes the values of checkboxes on the same line false.
     *
     * @param current
     * @param gridToTable
     */
    public static void updateCheckBoxesInSameRow(CheckBox current, GridToTable gridToTable) {
        for (Component temp : gridToTable) {
            CheckBox chkTemp;
            if (temp instanceof CheckBox) {
                chkTemp = (CheckBox) temp;
            } else {
                continue;
            }
            if (!(current.getCaption().equals(temp.getCaption()) && current.getData().equals(chkTemp.getData()))) {
                chkTemp.setValue(false);
            }
        }
    }

    /**
     * Checks whether value is set in gridToTable.
     *
     * @param gridToTable
     * @return
     */
    public static boolean checkWizardReady(GridToTable gridToTable) {
        for (Component temp : gridToTable) {
            CheckBox chkTemp;
            if (temp instanceof CheckBox) {
                chkTemp = (CheckBox) temp;
                if (chkTemp.getValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Makes the value of all checkBoxes in the gridToTable false.
     *
     * @param current
     * @param gridToTable
     */
    public static void updateCheckRow(CheckBox current, GridToTable gridToTable) {
        for (Component temp : gridToTable) {
            CheckBox chkTemp;
            if (temp instanceof CheckBox) {
                chkTemp = (CheckBox) temp;
            } else {
                continue;
            }
            if (current.getData() != chkTemp.getData()) {
                chkTemp.setValue(false);
            }
        }
    }

    /**
     * Used to generate the required map according to incoming values.
     *
     * @param selectedGridTag
     * @param parameter
     * @return
     */
    public static Map<ScriptParameters, Object> decomposeGridComponentParameter(String selectedGridTag, String parameter) {
        Map<ScriptParameters, Object> returnMap = new HashMap<>();
        List<Integer> rowList = new ArrayList<>();
        String[] parameterComponents = parameter.split(Regex.PARAMETER_SPLITTER.getRegex());
        if (parameterComponents[0].equals(selectedGridTag)) {
            parameterComponents[1] = parameterComponents[1].replace("[", "").replace("]", "");
            String[] rows = parameterComponents[1].split(",");
            for (int i = 0; i < rows.length; i++) {
                rowList.add(Integer.parseInt(rows[i]));
            }
            returnMap.put(ScriptParameters.ROW_INDEX_LIST, rowList);

            if (parameterComponents.length > 2) {
                returnMap.put(ScriptParameters.COLUMN_TAG, parameterComponents[2]);
            }
        }
        return returnMap;
    }

}
