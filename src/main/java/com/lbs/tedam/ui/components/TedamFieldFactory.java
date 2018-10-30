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

package com.lbs.tedam.ui.components;

import com.vaadin.data.Binder;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.FieldFactory;

import static org.vaadin.gridutil.cell.GridCellFilter.STYLENAME_GRIDCELLFILTER;

public class TedamFieldFactory extends FieldFactory {

    public static <T> DateTimeField genDateTimeField(Binder<T> binder, String propertyId, final java.text.SimpleDateFormat dateFormat) {
        DateTimeField dateField = new DateTimeField();

        binder.bind(dateField, propertyId);
        if (dateFormat != null) {
            dateField.setDateFormat(dateFormat.toPattern());
        }
        dateField.setWidth("100%");

        dateField.setResolution(DateTimeResolution.MINUTE);
        dateField.addStyleName(STYLENAME_GRIDCELLFILTER);
        dateField.addStyleName(ValoTheme.DATEFIELD_TINY);
        dateField.addValueChangeListener(e -> {
            if (binder.isValid()) {
                dateField.setComponentError(null);
            }
        });
        return dateField;
    }
}
