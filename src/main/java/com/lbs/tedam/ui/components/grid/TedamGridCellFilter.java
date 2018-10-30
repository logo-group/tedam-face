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
import com.lbs.tedam.ui.components.TedamFieldFactory;
import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertySet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.CellFilterComponent;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.cell.RangeCellFilterComponent;
import org.vaadin.gridutil.cell.TwoValueObject;
import org.vaadin.gridutil.cell.filter.BetweenFilter;
import org.vaadin.gridutil.cell.filter.EqualFilter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TedamGridCellFilter<T> extends GridCellFilter<T> implements TedamLocalizerWrapper {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private static LocalDate MIN_DATE_VALUE = LocalDate.MIN;
    private static LocalDate MAX_DATE_VALUE = LocalDate.MAX;
    private static LocalDateTime MIN_DATE_TIME_VALUE = LocalDateTime.MIN;
    private static LocalDateTime MAX_DATE_TIME_VALUE = LocalDateTime.MAX;
    private PropertySet<T> propertySet;

    public TedamGridCellFilter(Grid<T> grid, Class<T> beanType) {
        super(grid, beanType);
        propertySet = BeanPropertySet.get(beanType);
    }

    public RangeCellFilterComponent<DateField, HorizontalLayout> setLocalDateFilter(String columnId) {
        return setLocalDateFilter(columnId, null);
    }

    public RangeCellFilterComponent<DateTimeField, HorizontalLayout> setLocalDateTimeFilter(String columnId) {
        return setLocalDateTimeFilter(columnId, null);
    }

    public RangeCellFilterComponent<DateField, HorizontalLayout> setLocalDateFilter(String columnId, SimpleDateFormat dateFormat) {
        Class<?> propertyType = propertySet.getProperty(columnId).get().getType();
        if (!LocalDate.class.equals(propertyType)) {
            throw new IllegalArgumentException("columnId " + columnId + " is not of type LocalDate");
        }
        RangeCellFilterComponent<DateField, HorizontalLayout> filter = new RangeCellFilterComponent<DateField, HorizontalLayout>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = -5006719263476121038L;

            private DateField smallest;

            private DateField biggest;

            @Override
            public DateField getSmallestField() {
                if (smallest == null) {
                    smallest = genDateField(SMALLEST, dateFormat);
                }
                return smallest;
            }

            @Override
            public DateField getBiggestField() {
                if (biggest == null) {
                    biggest = genDateField(BIGGEST, dateFormat);
                }
                return biggest;
            }

            private DateField genDateField(final String propertyId, final SimpleDateFormat dateFormat) {
                return TedamFieldFactory.genDateField(getBinder(), propertyId, dateFormat);
            }

            @Override
            public HorizontalLayout layoutComponent() {
                getHLayout().addComponent(getSmallestField());
                getHLayout().addComponent(getBiggestField());
                getHLayout().setExpandRatio(getSmallestField(), 1);
                getHLayout().setExpandRatio(getBiggestField(), 1);

                initBinderValueChangeHandler();

                return getHLayout();
            }

            private void initBinderValueChangeHandler() {
                getBinder().addValueChangeListener(e -> {
                    Object smallest = getBinder().getBean().getSmallest();
                    Object biggest = getBinder().getBean().getBiggest();
                    LocalDate smallestDate = checkObject(smallest);
                    LocalDate biggestDate = checkObject(biggest);
                    if (this.smallest != null || biggest != null) {
                        if (this.smallest != null && biggest != null && this.smallest.equals(biggest)) {
                            replaceFilter(new EqualFilter<>(this.smallest), columnId);
                        } else {
                            replaceFilter(new BetweenFilter<>(smallestDate != null ? smallestDate : MIN_DATE_VALUE, biggestDate != null ? biggestDate : MAX_DATE_VALUE), columnId);
                        }
                    } else {
                        removeFilter(columnId);
                    }
                });
            }

            private LocalDate checkObject(Object value) {
                if (value instanceof LocalDate) {
                    return (LocalDate) value;
                }
                return null;
            }

            @Override
            public void clearFilter() {
                getBinder().setBean(new TwoValueObject());
            }
        };

        handleFilterRow(columnId, filter);
        return filter;
    }

    public <B> CellFilterComponent<ComboBox<B>> setTedamComboBoxFilter(String columnId, Class<B> beanType) {
        List<B> beans = new ArrayList<B>(Arrays.asList(beanType.getEnumConstants()));
        CellFilterComponent<ComboBox<B>> filter = new CellFilterComponent<ComboBox<B>>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            ComboBox<B> comboBox = new ComboBox<>();

            @Override
            public void triggerUpdate() {
                if (comboBox.getValue() != null) {
                    replaceFilter(new EqualFilter<Object>(comboBox.getValue()), columnId);
                } else {
                    removeFilter(columnId);
                }
            }

            @Override
            public ComboBox<B> layoutComponent() {
                comboBox.setEmptySelectionAllowed(true);
                comboBox.setTextInputAllowed(false);
                comboBox.addStyleName(STYLENAME_GRIDCELLFILTER);
                comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                comboBox.setItems(beans);
                comboBox.addValueChangeListener(e -> triggerUpdate());
                return comboBox;
            }

            @Override
            public void clearFilter() {
                comboBox.setValue(null);
            }
        };

        handleFilterRow(columnId, filter);
        return filter;
    }

    public ComboBox<BooleanRepresentation> setTedamBooleanFilter(String columnId) {
        CellFilterComponent<ComboBox<BooleanRepresentation>> filter = new CellFilterComponent<ComboBox<BooleanRepresentation>>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = 1L;

            ComboBox<BooleanRepresentation> comboBox = new ComboBox<>();

            @Override
            public void triggerUpdate() {
                if (comboBox.getValue() != null) {
                    replaceFilter(new EqualFilter<Object>(comboBox.getValue().getValue()), columnId);
                } else {
                    removeFilter(columnId);
                }
            }

            @Override
            public ComboBox<BooleanRepresentation> layoutComponent() {

                comboBox.setItemIconGenerator(BooleanRepresentation::getIcon);
                comboBox.setItemCaptionGenerator(BooleanRepresentation::getCaption);
                comboBox.setItems(Arrays.asList(BooleanRepresentation.TRUE_VALUE, BooleanRepresentation.FALSE_VALUE));
                comboBox.setTextInputAllowed(false);
                comboBox.setEmptySelectionAllowed(true);
                comboBox.addStyleName(STYLENAME_GRIDCELLFILTER);
                comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                comboBox.addValueChangeListener(e -> triggerUpdate());
                return comboBox;
            }

            @Override
            public void clearFilter() {
                comboBox.setValue(null);
            }
        };

        handleFilterRow(columnId, filter);
        return filter.getComponent();
    }

    public RangeCellFilterComponent<DateTimeField, HorizontalLayout> setLocalDateTimeFilter(String columnId, SimpleDateFormat dateFormat) {
        Class<?> propertyType = propertySet.getProperty(columnId).get().getType();
        if (!LocalDateTime.class.equals(propertyType)) {
            throw new IllegalArgumentException("columnId " + columnId + " is not of type LocalDateTime");
        }
        RangeCellFilterComponent<DateTimeField, HorizontalLayout> filter = new RangeCellFilterComponent<DateTimeField, HorizontalLayout>() {

            /** long serialVersionUID */
            private static final long serialVersionUID = -5006719263476121038L;

            private DateTimeField smallestDateTime;

            private DateTimeField biggestDateTime;

            @Override
            public DateTimeField getSmallestField() {
                if (smallestDateTime == null) {
                    smallestDateTime = genDateField(SMALLEST, dateFormat);
                }
                return smallestDateTime;
            }

            @Override
            public DateTimeField getBiggestField() {
                if (biggestDateTime == null) {
                    biggestDateTime = genDateField(BIGGEST, dateFormat);
                }
                return biggestDateTime;
            }

            private DateTimeField genDateField(final String propertyId, final SimpleDateFormat dateFormat) {
                return TedamFieldFactory.genDateTimeField(getBinder(), propertyId, dateFormat);
            }

            @Override
            public HorizontalLayout layoutComponent() {
                getHLayout().addComponent(getSmallestField());
                getHLayout().addComponent(getBiggestField());
                getHLayout().setExpandRatio(getSmallestField(), 1);
                getHLayout().setExpandRatio(getBiggestField(), 1);

                initBinderValueChangeHandler();

                return getHLayout();
            }

            private void initBinderValueChangeHandler() {
                getBinder().addValueChangeListener(e -> {
                    Object smallest = getBinder().getBean().getSmallest();
                    Object biggest = getBinder().getBean().getBiggest();
                    LocalDateTime smallestDate = checkObject(smallest);
                    LocalDateTime biggestDate = checkObject(biggest);
                    if (this.smallestDateTime != null || biggest != null) {
                        if (this.smallestDateTime != null && biggest != null && this.smallestDateTime.equals(biggest)) {
                            replaceFilter(new EqualFilter<>(this.smallestDateTime), columnId);
                        } else {
                            replaceFilter(new BetweenFilter<>(smallestDate != null ? smallestDate : MIN_DATE_TIME_VALUE, biggestDate != null ? biggestDate : MAX_DATE_TIME_VALUE),
                                    columnId);
                        }
                    } else {
                        removeFilter(columnId);
                    }
                });
            }

            private LocalDateTime checkObject(Object value) {
                if (value instanceof LocalDateTime) {
                    return (LocalDateTime) value;
                }
                return null;
            }

            @Override
            public void clearFilter() {
                getBinder().setBean(new TwoValueObject());
            }
        };

        handleFilterRow(columnId, filter);
        return filter;
    }
}
