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

package com.lbs.tedam.ui.view.jobgroup;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;

import com.lbs.tedam.data.service.JobGroupService;
import com.lbs.tedam.data.service.PropertyService;
import com.lbs.tedam.data.service.TedamUserService;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.view.AbstractGridPresenter;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

@SpringComponent
@ViewScope
public class JobGroupGridPresenter extends AbstractGridPresenter<JobGroup, JobGroupService, JobGroupGridPresenter, JobGroupGridView> {

	/**
	 * long serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	public JobGroupGridPresenter(JobGroupDataProvider jobGroupDataProvider, NavigationManager navigationManager, JobGroupService service, BeanFactory beanFactory,
			ViewEventBus viewEventBus, PropertyService propertyService, TedamUserService userService) {
		super(navigationManager, service, jobGroupDataProvider, beanFactory, viewEventBus, propertyService, userService);
	}

	@PostConstruct
	public void init() {
		subscribeToEventBus();
	}

	@Override
	protected void enterView(Map<UIParameter, Object> parameters) {
	}
}
