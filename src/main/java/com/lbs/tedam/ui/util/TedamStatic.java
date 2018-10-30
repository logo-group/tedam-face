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
package com.lbs.tedam.ui.util;

import com.lbs.tedam.ui.util.Enums.TedamColor;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.util.EnumsV2.JobStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ahmet.Izgi
 */
public class TedamStatic {

    /**
     * @return
     */
    public static Map<UIParameter, Object> getUIParameterMap() {
        return new HashMap<UIParameter, Object>();
    }

    /**
     * It creates a new UIParameter map and sets the frequently used id and mode values.
     *
     * @param id
     * @param mode
     * @return
     */
    public static Map<UIParameter, Object> getUIParameterMap(Integer id, ViewMode mode) {
        HashMap<UIParameter, Object> parameters = new HashMap<UIParameter, Object>();
        parameters.put(UIParameter.ID, id);
        parameters.put(UIParameter.MODE, mode);

        return parameters;
    }

    /**
     * this method getJobStatusColorMap <br>
     *
     * @return <br>
     * @author Canberk.Erkmen
     */
    public static Map<JobStatus, TedamColor> getJobStatusColorMap() {
        Map<JobStatus, TedamColor> map = new HashMap<>();
        map.put(JobStatus.NOT_STARTED, TedamColor.LIGHT_GRAY);
        map.put(JobStatus.PLANNED, TedamColor.LIGHT_BLUE);
        map.put(JobStatus.QUEUED, TedamColor.LIGHT_PURPLE);
        map.put(JobStatus.STARTED, TedamColor.LIGHT_PINK);
        map.put(JobStatus.PAUSED, TedamColor.LIGHT_GREEN);
        map.put(JobStatus.STOPPED, TedamColor.LIGHT_YELLOW);
        map.put(JobStatus.COMPLETED, TedamColor.LIGHT_RED);
        map.put(JobStatus.WAITING_STOP, TedamColor.LIGHT_ORANGE);
        map.put(JobStatus.WAITING_PAUSE, TedamColor.LIGHTER_ORANGE);
        return map;
    }

}
