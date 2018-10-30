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

import com.vaadin.spring.annotation.SpringComponent;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.Locale;

@SpringComponent
public class DateTimeFormatter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Format the given local time using the given locale.
     *
     * @param dateTime the date and time to format
     * @param locale   the locale to use to determine the format
     * @return a formatted string
     */
    public String format(LocalDateTime dateTime, Locale locale) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale);
        return dateTime.format(formatter);
    }

}
