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

package com.lbs.tedam.ui.view.jobmanager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.annotation.PrototypeScope;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.JobGroupService;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.model.JobGroup;
import com.lbs.tedam.ui.view.AbstractDataProvider;
import com.vaadin.spring.annotation.SpringComponent;

@SpringComponent
@PrototypeScope
public class TedamManagerJobGroupDataProvider extends AbstractDataProvider<JobGroup> {

    private static final long serialVersionUID = 1L;

	private final JobGroupService jobGroupService;

    @Autowired
	public TedamManagerJobGroupDataProvider(JobGroupService jobGroupService) throws LocalizedException {
		this.jobGroupService = jobGroupService;
		buildListDataProvider(getJobGroups());
    }

    public void refresh() throws LocalizedException {
		refreshDataProviderByItems(getJobGroups());
    }

	public List<JobGroup> getJobGroups() throws LocalizedException {
		return jobGroupService.getRunnableJobGroupListByProject(SecurityUtils.getUserSessionProject());
    }
}
