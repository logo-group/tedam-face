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

package com.lbs.tedam.ui.components.window.teststep.griddelete;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.generator.steptype.GridDeleteGenerator;
import com.lbs.tedam.model.DTO.GridCell;
import com.lbs.tedam.model.DTO.GridRow;
import com.lbs.tedam.model.TestStep;
import com.lbs.tedam.ui.components.combobox.TedamGridTagComboBoxDataProvider;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractWindowPresenter;
import com.lbs.tedam.util.TedamXPathUtils;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

;

@SpringComponent
@ViewScope
public class GridDeleteWindowPresenter extends AbstractWindowPresenter<WindowTestStepTypeGridDelete> {

    private static final long serialVersionUID = 1L;
    private final TedamGridTagComboBoxDataProvider tedamGridComboBoxDataProvider;
    private TestStep testStep;

    @Autowired
    public GridDeleteWindowPresenter(TedamGridTagComboBoxDataProvider tedamGridComboBoxDataProvider,
                                     PropertyService propertyService) {
        super(propertyService);
        this.tedamGridComboBoxDataProvider = tedamGridComboBoxDataProvider;
    }

    public void enterWindow(Map<UIParameter, Object> parameters) {
        setTestStep((TestStep) parameters.get(UIParameter.TESTSTEP));
        getWindow().getDynamicGrid().resetGrid();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fillComponentsWithValues() throws LocalizedException {
        buildTedamGridComboBoxDataProvider();

        GridDeleteGenerator generator = (GridDeleteGenerator) testStep.getGenerator();
        generator.degenerate(testStep.getParameter());

        if (generator.getGridTag() != null) {
            getWindow().getTedamGridTagComboBox().setValue(generator.getGridTag());
        }
        if (generator.getRowIndexes() != null && !generator.getRowIndexes().isEmpty()) {
            // If all items are selected in grid and match the selected ones, then select
            // is done on the grid.
            Collection<List<GridCell>> rows = ((ListDataProvider<List<GridCell>>) getWindow().getDynamicGrid()
                    .getDataProvider()).getItems();
            for (List<GridCell> row : rows) {
                if (generator.getRowIndexes().contains(row.get(0).getRowIndex())) {
                    getWindow().getDynamicGrid().select(row);
                }
            }
        }
    }

    private void buildTedamGridComboBoxDataProvider() throws LocalizedException {
        tedamGridComboBoxDataProvider.setTestStep(testStep);
        getWindow().getTedamGridTagComboBox().setValue(null);
        getWindow().getTedamGridTagComboBox().setDataProvider(tedamGridComboBoxDataProvider.getListDataProvider());
    }

    public TestStep getTestStep() {
        return testStep;
    }

    private void setTestStep(TestStep testStep) {
        this.testStep = testStep;
    }

    public List<GridRow> getGridRowList(String gridTagId) throws LocalizedException {
        Node gridRootNode = TedamXPathUtils.getGridNode(gridTagId,
                getPropertyService().getTestcaseFolder(testStep.getTestCaseId()) + File.separator
                        + testStep.getFilename());
        return TedamXPathUtils.getGridMatrix(gridRootNode);
    }

    public void onGridTagChanged(String gridTag) throws LocalizedException {
        getWindow().fillDynamicGrid(gridTag);

    }

    public List<List<GridCell>> getGridItems(String gridTag) throws LocalizedException {
        List<List<GridCell>> rowList = new ArrayList<>();

        for (GridRow gridRow : getGridRowList(gridTag)) {
            List<GridCell> row = new ArrayList<>();
            for (GridCell gridCell : gridRow.getCells()) {
                row.add(gridCell);
            }
            rowList.add(row);
        }
        return rowList;
    }
}
