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
package com.lbs.tedam.ui.components.window.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.DTO.ClientDTO;
import com.lbs.tedam.ui.components.CustomExceptions.TedamWindowNotAbleToOpenException;
import com.lbs.tedam.ui.components.basic.TedamWindow;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.WindowSize;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.UI;

/**
 * @author Ahmet.Izgi
 */
@SpringComponent
@PrototypeScope
public class WindowClient extends TedamWindow {

    private static final long serialVersionUID = 1L;

    private TedamFilterGrid<ClientDTO> gridClient;
    private List<ClientDTO> clientDTOList;

    @Autowired
    public WindowClient(ViewEventBus viewEventBus, PropertyService propertyService) {
        super(WindowSize.MEDIUM, viewEventBus, propertyService);
    }

    @Override
    protected Component buildContent() {
        TedamGridConfig<ClientDTO> gridConfigFiles = new TedamGridConfig<ClientDTO>() {

            @Override
            public List<RUDOperations> getRUDOperations() {
                List<RUDOperations> operations = new ArrayList<RUDOperations>();
                operations.add(RUDOperations.NONE);
                return operations;
            }

            @Override
            public List<GridColumn> getColumnList() {
                return GridColumn.CLIENT_DTO_COLUMNS;
            }

            @Override
            public Class<ClientDTO> getBeanType() {
                return ClientDTO.class;
            }
        };
        gridClient = new TedamFilterGrid<>(gridConfigFiles, SelectionMode.MULTI);
		gridClient.setId("ClientWindow");
        organizeDataProvider();
        return gridClient;
    }

    private void organizeDataProvider() {
        AbstractDataProvider<ClientDTO> dataProvider = new AbstractDataProvider<>();
        dataProvider.buildListDataProvider(clientDTOList);
        gridClient.setGridDataProvider(dataProvider);
    }

    @Override
    protected String getHeader() {
        return getLocaleValue("window.client.header");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void open(Map<UIParameter, Object> parameters) throws TedamWindowNotAbleToOpenException, LocalizedException {
        clientDTOList = (List<ClientDTO>) parameters.get(UIParameter.ITEMS);
        UI.getCurrent().addWindow(this);
        center();
        setModal(true);
        focus();
        initWindow();
    }

    @Override
    protected void windowClose() {
    }

    @Override
    protected boolean readyToClose() {
        return true;
    }

    @Override
    public void publishCloseSuccessEvent() {
    }

	@Override
	public TedamGrid<?> getWindowGrid() {
		return gridClient;
	}

}
