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

import java.time.LocalDateTime;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.NotificationGroupService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.NotificationGroup;
import com.lbs.tedam.model.Project;
import com.lbs.tedam.model.Recipient;
import com.lbs.tedam.ui.components.grid.TedamFilterGrid;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.Enums.ViewMode;
import com.lbs.tedam.ui.view.AbstractEditPresenter;
import com.vaadin.navigator.View;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class NotificationEditPresenter extends
		AbstractEditPresenter<NotificationGroup, NotificationGroupService, NotificationEditPresenter, NotificationEditView> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final RecipientDataProvider recipientDataProvider;

	@Autowired
	public NotificationEditPresenter(ViewEventBus viewEventBus, NavigationManager navigationManager,
			NotificationGroupService service, TedamUserService userService, BeanFactory beanFactory,
			PropertyService propertyService, RecipientDataProvider recipientDataProvider) {
		super(viewEventBus, navigationManager, service, NotificationGroup.class, beanFactory, userService,
				propertyService);
		this.recipientDataProvider = recipientDataProvider;
	}

	@Override
	protected void enterView(Map<UIParameter, Object> windowParameters) throws LocalizedException {
		NotificationGroup notificationGroup;
		Integer id = (Integer) windowParameters.get(UIParameter.ID);
		ViewMode mode = (ViewMode) windowParameters.get(UIParameter.MODE);
		if (id == 0) {
			notificationGroup = new NotificationGroup();
			notificationGroup.setProject(SecurityUtils.getCurrentUser(getUserService()).getProject());
		} else {
			notificationGroup = getService().getById(id);
			if (notificationGroup == null) {
				getView().showNotFound();
				return;
			}
			isAuthorized(notificationGroup);
		}
		refreshView(notificationGroup, mode);
		recipientDataProvider.provideRecipients(notificationGroup);
		getView().organizeRecipientsGrid(recipientDataProvider);
		organizeComponents(getView().getAccordion(), mode == ViewMode.VIEW);
		setGridEditorAttributes(getView().getGridRecipients(), mode != ViewMode.VIEW);
	}

	@Override
	protected Class<? extends View> getGridView() {
		return NotificationEditView.class;
	}

	@PostConstruct
	public void init() {
		subscribeToEventBus();
	}

	@Override
	protected Project getProjectByEntity(NotificationGroup entity) {
		return entity.getProject();
	}

	public void addRecipientRow() throws LocalizedException {
		Recipient recipient = new Recipient();
		TedamFilterGrid<Recipient> activeTabsGrid = getView().getGridRecipients();
		recipient.setDateCreated(LocalDateTime.now());
		recipient.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getTedamUser().getUserName());
		activeTabsGrid.getGridDataProvider().getListDataProvider().getItems().add(recipient);
		activeTabsGrid.refreshAll();
		getView().getGridRecipients().deselectAll();
		getView().getGridRecipients().scrollToEnd();
		setHasChanges(true);
	}

	public void removeRecipientpRow() {
		TedamFilterGrid<Recipient> recipientsGrid = getView().getGridRecipients();
		if (recipientsGrid.getSelectedItems().isEmpty()) {
			getView().showGridRowNotSelected();
			return;
		}
		recipientsGrid.getSelectedItems()
				.forEach(recipient -> recipientsGrid.getGridDataProvider().removeItem(recipient));
		recipientsGrid.deselectAll();
		recipientsGrid.refreshAll();
		setHasChanges(true);
	}

}
