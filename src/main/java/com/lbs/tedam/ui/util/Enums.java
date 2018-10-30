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

package com.lbs.tedam.ui.util;

public class Enums {
    public enum ViewMode {
        EDIT, VIEW, NEW;
    }

    public enum UIParameter {
        ID,
        MODE,
        TESTCASE_ID,
        SNAPSHOT_DEFINITION,
        TEDAM_FILE_NAME,
        TESTSTEP,
        TEST_STEP_TYPE,
        SELECTED_LIST,
        TESTSTEPS,
        TESTSET,
        ITEMS,
        SELECTION_MODE,
        TESTCASE_TESTRUN,
        JOB_PARAMETER,
        TEDAM_FOLDER,
        FOLDER,
        FOLDER_TYPE,
        CREATED_USER;
    }

    public enum WindowSize {
        SMALLEST("window-smallest"), SMALL("window-small"), MEDIUM("window-medium"), BIG("window-big");
        private final String size;

        WindowSize(String size) {
            this.size = size;
        }

        public String getSize() {
            return size;
        }
    }

    public enum TedamColor {
        LIGHT_BLUE, LIGHT_GREEN, LIGHT_RED, LIGHT_YELLOW, LIGHT_ORANGE, LIGHTER_ORANGE, LIGHT_PURPLE, LIGHT_PINK, LIGHT_GRAY;
    }

}
