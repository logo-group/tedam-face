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

package com.lbs.tedam.ui.view.notification.edit;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.NotificationGroupService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.NotificationGroup;
import com.lbs.tedam.model.Recipient;
import com.lbs.tedam.ui.components.basic.TedamButton;
import com.lbs.tedam.ui.components.basic.TedamTextField;
import com.lbs.tedam.ui.components.combobox.NotificationTypeComboBox;
import com.lbs.tedam.ui.components.grid.GridColumns;
import com.lbs.tedam.ui.components.grid.GridColumns.GridColumn;
import com.lbs.tedam.ui.components.grid.GridFilterValue;
import com.lbs.tedam.ui.components.grid.RUDOperations;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.components.grid.TedamGridConfig;
import com.lbs.tedam.ui.util.TedamNotification;
import com.lbs.tedam.ui.util.TedamNotification.NotifyType;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.lbs.tedam.ui.view.AbstractEditView;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;

@SpringView
public class NotificationEditView extends
		AbstractEditView<NotificationGroup, NotificationGroupService, NotificationEditPresenter, NotificationEditView> {

	private static final long serialVersionUID = 1L;

	private TedamTextField groupName;
	private NotificationTypeComboBox type;
	private TedamButton btnAddRow;
	private TedamButton btnRemoveRow;
	private TedamFilterGrid<Recipient> gridRecipients;

	@Autowired
	public NotificationEditView(NotificationEditPresenter presenter, NotificationTypeComboBox type) {
		super(presenter);
		this.type = type;
	}

	@PostConstruct
	private void initView() {
		groupName = new TedamTextField("view.notification.groupname", "half", true, true);
		buildRecipientsGrid();
		addSection(getLocaleValue("view.viewedit.section.general"), 0, null, groupName, type);
		addSection(getLocaleValue("view.notification.section.recipients"), 1, null, buildRecipientsGridButtons(),
				gridRecipients);
		getPresenter().setView(this);
	}

	@Override
	public String getHeader() {
		return getLocaleValue("view.notification.header");
	}

	@Override
	public void bindFormFields(BeanValidationBinder<NotificationGroup> binder) {
		super.bindFormFields(binder);
		binder.forField(type).asRequired().bind("type");
	}

	private TedamGridConfig<Recipient> buildRecipientsGridConfig() {
		TedamGridConfig<Recipient> recipientsGridConfig = new TedamGridConfig<Recipient>() {

			@Override
			public List<GridColumn> getColumnList() {
				return GridColumns.GridColumn.RECIPIENT_COLUMNS;
			}

			@Override
			public Class<Recipient> getBeanType() {
				return Recipient.class;
			}

			@Override
			public List<RUDOperations> getRUDOperations() {
				List<RUDOperations> operations = new ArrayList<RUDOperations>();
				return operations;
			}

		};
		return recipientsGridConfig;
	}

	private Component buildRecipientsGridButtons() {
		HorizontalLayout hLayButtons = new HorizontalLayout();

		btnAddRow = new TedamButton("view.testcaseedit.button.addrow", VaadinIcons.PLUS_CIRCLE);
		btnRemoveRow = new TedamButton("view.testcaseedit.button.removerow", VaadinIcons.MINUS_CIRCLE);

		hLayButtons.addComponents(btnAddRow, btnRemoveRow);

		btnAddRow.addClickListener(e -> {
			try {
				getPresenter().addRecipientRow();
			} catch (LocalizedException e1) {
				logError(e1);
			}
		});
		btnRemoveRow.addClickListener(e -> getPresenter().removeRecipientpRow());

		return hLayButtons;
	}

	protected void buildRecipientsGrid() {
		gridRecipients = new TedamFilterGrid<Recipient>(buildRecipientsGridConfig(), SelectionMode.MULTI) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onViewSelected(Recipient recipient) {
				getEditor().editRow(getRowIndex(recipient));
			}

		};
		gridRecipients.getColumn(GridColumn.RECIPIENT_ADDRESS.getColumnName())
				.setDescriptionGenerator(Recipient::getAddress);
		gridRecipients.setId("RecipientsGrid");
	}

	public void showGridRowNotSelected() {
		TedamNotification.showNotification(getLocaleValue("view.testcaseedit.messages.showGridRowNotSelected"),
				NotifyType.ERROR);
	}


	public TedamFilterGrid<Recipient> getGridRecipients() {
		return gridRecipients;
	}

	protected void organizeRecipientsGrid(AbstractDataProvider<Recipient> abstractDataProvider) {
		gridRecipients.setGridDataProvider(abstractDataProvider);
		gridRecipients.initFilters();
		fetchSavedFilters(gridRecipients);
	}

	private void fetchSavedFilters(TedamFilterGrid<?> grid) {
		GridFilterValue filterValues = SecurityUtils.loadFilterValue(this.getClass().getName(), grid.getId());
		grid.laodFilterValues(filterValues);
	}

}
