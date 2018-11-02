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

package com.lbs.tedam.ui.view.notification;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.data.service.NotificationGroupService;
import com.lbs.tedam.model.NotificationGroup;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.view.AbstractGridView;
import com.lbs.tedam.ui.view.notification.edit.NotificationEditView;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Grid.SelectionMode;

@SpringView
public class NotificationGridView extends
		AbstractGridView<NotificationGroup, NotificationGroupService, NotificationGridPresenter, NotificationGridView> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	public NotificationGridView(NotificationGridPresenter presenter) {
		super(presenter, SelectionMode.MULTI);
	}

	@PostConstruct
	private void init() {
		getPresenter().setView(this);
		setHeader(getLocaleValue("view.notifications.header"));
	}

	private TedamGridConfig<NotificationGroup> config = new TedamGridConfig<NotificationGroup>() {

		@Override
		public List<GridColumn> getColumnList() {
			return GridColumns.GridColumn.NOTIFICATION_COLUMNS;
		}

		@Override
		public Class<NotificationGroup> getBeanType() {
			return NotificationGroup.class;
		}

		@Override
		public List<RUDOperations> getRUDOperations() {
			List<RUDOperations> operations = new ArrayList<RUDOperations>();
			operations.add(RUDOperations.DELETE);
			operations.add(RUDOperations.VIEW);
			return operations;
		}

	};

	@Override
	protected TedamGridConfig<NotificationGroup> getTedamGridConfig() {
		return config;
	}

	@Override
	protected Class<? extends View> getEditView() {
		return NotificationEditView.class;
	}

	@Override
	protected void buildGridColumnDescription() {
		getGrid().getColumn(GridColumn.NOTIFICATION_GROUP_NAME.getColumnName())
				.setDescriptionGenerator(NotificationGroup::getGroupName);

	}
}
