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

package com.lbs.tedam.ui.view.testset;

import com.lbs.tedam.app.security.SecurityUtils;
import com.lbs.tedam.data.service.*;
import com.lbs.tedam.exception.localized.LocalizedException;
import com.lbs.tedam.localization.LocaleConstants;
import com.lbs.tedam.model.*;
import com.lbs.tedam.ui.TedamFaceEvents.EnvironmentSelectEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TedamFolderEvent;
import com.lbs.tedam.ui.TedamFaceEvents.TedamFolderSelectEvent;
import com.lbs.tedam.ui.components.window.folder.TedamFolderDataProvider;
import com.lbs.tedam.ui.navigation.NavigationManager;
import com.lbs.tedam.ui.util.DateTimeFormatter;
import com.lbs.tedam.ui.util.Enums.UIParameter;
import com.lbs.tedam.ui.util.TedamStatic;
import com.lbs.tedam.ui.view.AbstractGridPresenter;
import com.lbs.tedam.util.Constants;
import com.lbs.tedam.util.EnumsV2.JobType;
import com.lbs.tedam.util.EnumsV2.TedamFolderType;
import com.lbs.tedam.util.TedamStringUtils;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.ViewEventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringComponent
@ViewScope
public class TestSetGridPresenter extends AbstractGridPresenter<TestSet, TestSetService, TestSetGridPresenter, TestSetGridView> {

    /**
     * long serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private JobService jobService;
    private DateTimeFormatter formatter;
    private TedamFolderDataProvider tedamFolderDataProvider;
    private TedamFolderService tedamFolderService;

    @Autowired
    public TestSetGridPresenter(TestSetsDataProvider testSetsDataProvider, NavigationManager navigationManager, TestSetService service, BeanFactory beanFactory,
                                ViewEventBus viewEventBus, PropertyService propertyService, TedamUserService userService, JobService jobService, DateTimeFormatter formatter,
                                TedamFolderDataProvider tedamFolderDataProvider, TedamFolderService tedamFolderService) {
        super(navigationManager, service, testSetsDataProvider, beanFactory, viewEventBus, propertyService, userService);
        this.jobService = jobService;
        this.formatter = formatter;
        this.tedamFolderDataProvider = tedamFolderDataProvider;
        this.tedamFolderService = tedamFolderService;
    }

    @PostConstruct
    public void init() {
        subscribeToEventBus();
    }

    public void prepareWindowEnvironment() throws LocalizedException {
        if (getView().getGrid().getSelectedItems().isEmpty()) {
            getView().showGridRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        getView().openWindowEnvironment(windowParameters);
    }

    public void prepareWindowSelectTedamFolder() throws LocalizedException {
        if (getView().getGrid().getSelectedItems().isEmpty()) {
            getView().showGridRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        windowParameters.put(UIParameter.FOLDER_TYPE, TedamFolderType.TESTSET);
        getView().openWindowSelectTedamFolder(windowParameters);
    }

    public void prepareWindowTedamFolder(boolean isEdited) throws LocalizedException {
        Set<TedamFolder> selectedItems = getView().getTedamFolderTreePanel().getTedamTree().getSelectedItems();
        if (isEdited && selectedItems.isEmpty()) {
            getView().showTreeRowNotSelected();
            return;
        }
        Map<UIParameter, Object> windowParameters = TedamStatic.getUIParameterMap();
        TedamFolder tedamFolder = new TedamFolder();
        tedamFolder.setFolderType(TedamFolderType.TESTSET);
        tedamFolder.setCreatedUser(SecurityUtils.getCurrentUser(getUserService()).getUsername());
        tedamFolder.setProject(SecurityUtils.getUserSessionProject());
        if (isEdited) {
            tedamFolder = selectedItems.iterator().next();
        }
        windowParameters.put(UIParameter.TEDAM_FOLDER, tedamFolder);
        getView().openWindowTedamFolder(windowParameters);
    }

    public void showAllTestSet() throws LocalizedException {
        TedamFolder selectedFolder = getView().getTedamFolderTreePanel().getSelectedFolder();
        if (selectedFolder != null) {
            getView().getTedamFolderTreePanel().getTedamTree().deselect(selectedFolder);
            ((TestSetsDataProvider) getDataPovider()).buildDataProvider();
        }
    }

    public void deleteFolder() throws LocalizedException {
        Set<TedamFolder> selectedItems = getView().getTedamFolderTreePanel().getTedamTree().getSelectedItems();
        if (selectedItems.isEmpty()) {
            getView().showTreeRowNotSelected();
            return;
        }
        TedamFolder tedamFolder = selectedItems.iterator().next();
        if (!tedamFolder.getChildFolders().isEmpty()) {
            getView().showFolderCanNotDelete();
            return;
        }
        List<TestSet> testSets = new ArrayList<>(getDataPovider().getListDataProvider().getItems());
        for (TestSet testSet : testSets) {
            testSet.setTestSetFolderId(tedamFolder.getParentFolder().getId());
        }
        getService().save(testSets);
        tedamFolderService.deleteByLogic(tedamFolder.getId());
        getView().organizeTedamFolderTestTreePanel(tedamFolderDataProvider);
        getView().getTedamFolderTreePanel().getTedamTree().select(tedamFolder.getParentFolder());
    }

    @EventBusListenerMethod
    public void environmentSelectedEvent(EnvironmentSelectEvent event) throws LocalizedException {
        Environment environment = event.getEnvironment();
        createQuickJob(environment);
    }

    @EventBusListenerMethod
    public void folderEditedOrAddedEvent(TedamFolderEvent event) throws LocalizedException {
        TedamFolder tedamFolder = event.getTedamFolder();
        tedamFolderService.save(tedamFolder);
        getView().organizeTedamFolderTestTreePanel(tedamFolderDataProvider);
    }

    @EventBusListenerMethod
    public void testSetMovedEvent(TedamFolderSelectEvent event) throws LocalizedException {
        TedamFolder tedamFolder = event.getTedamFolder();
        List<TestSet> selectedItems = getView().getGrid().getSelectedItems().stream().collect(Collectors.toList());
        for (TestSet testSet : selectedItems) {
            testSet.setTestSetFolderId(tedamFolder.getId());
        }
        getService().save(selectedItems);
        getView().getGrid().deselectAll();
        showAllTestSet();
    }

    public void createQuickJob(Environment environment) throws LocalizedException {
        String jobName = createJobName(getView().getGrid().getSelectedItems());
        Job job = new Job(jobName, SecurityUtils.getUserSessionProject());
        job.setType(JobType.QUICK);
        job.setActive(true);
        job.setCreatedUser(SecurityUtils.getUser().getUserName());
        job.setJobEnvironment(environment);
        List<JobDetail> jobDetails = createJobDetails();
        job.setJobDetails(jobDetails);
        List<Client> clientList = SecurityUtils.getCurrentUser(getUserService()).getTedamUser().getUserFavoritesClient();
        job.setClients(clientList);
        job = jobService.save(job);
        getView().getGrid().deselectAll();
        getView().showJobMessage(job);
    }

    private List<JobDetail> createJobDetails() {
        List<JobDetail> jobDetails = new ArrayList<>();
        for (TestSet testSet : getView().getGrid().getSelectedItems()) {
            JobDetail jobDetail = new JobDetail(null, testSet);
            jobDetails.add(jobDetail);
        }
        return jobDetails;
    }

    public String createJobName(Set<TestSet> selectedItems) {
        String jobName = "QuickJob ";
        List<String> testSetIdNameList = new ArrayList<>();
        if (selectedItems.size() < 10) {
            for (TestSet testSet : selectedItems) {
                String testSetIdName = Constants.TEDAM_TEST_SET + testSet.getId();
                testSetIdNameList.add(testSetIdName);
            }
            jobName = TedamStringUtils.getListAsStringWithSeparator(testSetIdNameList, Constants.TEXT_COMMA_WITH_SPACE);
        }
        jobName += Constants.TEXT_DASH + formatter.format(LocalDateTime.now(), LocaleConstants.LOCALE_TRTR);
        return jobName;
    }

    @Override
    protected void enterView(Map<UIParameter, Object> parameters) {
        getView().organizeTedamFolderTestTreePanel(tedamFolderDataProvider);
    }

    public void addButtonClickEvent() {
        Set<TedamFolder> selectedItems = getView().getTedamFolderTreePanel().getTedamTree().getSelectedItems();
        if (selectedItems.isEmpty()) {
            getView().showTreeRowNotSelected();
            return;
        }
        TedamFolder tedamFolder = selectedItems.iterator().next();
        if (getView().getEditView() != null) {
            getNavigationManager().navigateTo(getView().getEditView(), "new" + "?" + UIParameter.FOLDER.toString().toLowerCase() + Constants.EQUAL + tedamFolder.getId());
        }

    }

}
