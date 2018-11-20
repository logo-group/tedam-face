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

import java.util.Arrays;
import java.util.List;

import com.lbs.tedam.util.EnumsV2.ClientStatus;
import com.lbs.tedam.util.EnumsV2.CommandStatus;
import com.lbs.tedam.util.EnumsV2.ExecutionStatus;
import com.lbs.tedam.util.EnumsV2.JobStatus;
import com.lbs.tedam.util.EnumsV2.JobType;
import com.lbs.tedam.util.EnumsV2.TedamUserFavoriteType;
import com.lbs.tedam.util.EnumsV2.TedamUserRole;
import com.lbs.tedam.util.EnumsV2.TestRunType;
import com.lbs.tedam.util.EnumsV2.TestStepType;

/**
 * All grid columns must be implemented in this class.
 */
public class GridColumns {

    private GridColumns() {

    }

    public static enum GridColumn {

        /**
         * columnName; resourceName; dataType; filterBeanType; collapsable; editable; hidden;
         *
         */
        /**
         * SNAPSHOT_VALUE
         */
        SNAPSHOT_VALUE_CAPTION("caption", "column.snapshotvalue.caption", DataType.TEXT, null, false, false, false),
        SNAPSHOT_VALUE_VALUE("value", "column.snapshotvalue.value", DataType.TEXT, null, true, false, false),
        SNAPSHOT_VALUE_TAG("tag", "column.snapshotvalue.tag", DataType.TEXT, null, false, false, false),
        SNAPSHOT_VALUE_ROWINDEX("rowIndex", "column.snapshotvalue.rowindex", DataType.INTEGER, null, false, false, false),
        SNAPSHOT_VALUE_TYPE("type", "column.snapshotvalue.type", DataType.TEXT, null, false, false, false),
        SNAPSHOT_VALUE_RUNORDER("order", "column.snapshotvalue.order", DataType.TEXT, null, false, false, false),
        SNAPSHOT_VALUE_CONTINUE_ON_ERROR("continueOnError", "column.snapshotvalue.continueOnError", DataType.BOOLEAN, null, true, false, false),
        SNAPSHOT_VALUE_LOOKUP_PARAMETER("lookUpParameter", "column.snapshotvalue.lookUpParameter", DataType.TEXT, null, false, false, false),
        SNAPSHOT_VALUE_HAS_LOOKUP("lookUp", "column.snapshotvalue.lookUp", DataType.BOOLEAN, null, false, false, false),

        UPLOADED_FILES_NAME("name", "column.uploadedfiles.name", DataType.TEXT, null, false, false, true),
        /**
         * TEST_STEP_COLUMNS
         */
        TEST_STEP_DESCRIPTION("description", "column.teststep.description", DataType.TEXT_AREA, null, true, false, false), //
        TEST_STEP_FILENAME("filename", "column.teststep.filename", DataType.TEXT, null, false, false, false),
        TEST_STEP_TYPE("type", "column.teststep.type", DataType.SELECT_ENUM, TestStepType.class, true, false, false),
        TEST_STEP_PARAMETER("parameter", "column.teststep.parameter", DataType.TEXT, TestStepType.class, false, false, false),
        /**
         * TEST_SET_COLUMNS
         */
        TEST_SET_NAME("name", "column.testset.name", DataType.TEXT, null, true, false, true), //
        TEST_SET_STATUS("testSetStatus", "column.testset.testSetStatus", DataType.SELECT_ENUM, CommandStatus.class, true, false, true), //
        TEST_SET_JOB_LIST("jobListAsString", "column.testset.jobListAsString", DataType.TEXT, null, true, false, true),
        TEST_SET_IS_AUTOMATED("automated", "column.testset.automated", DataType.BOOLEAN, null, true, true, true),
        TEST_SET_FAILED_COUNT("failedTestCaseCount", "column.testset.failedCount", DataType.INTEGER, null, false, false, true), //
        TEST_SET_SUCCEEDED_COUNT("succeededTestCaseCount", "column.testset.succeededCount", DataType.INTEGER, null, false, false, true), //
        TEST_SET_NOTRUN_COUNT("notRunTestCaseCount", "column.testset.notRunCount", DataType.INTEGER, null, false, false, true), //
        TEST_SET_BLOCKED_COUNT("blockedTestCaseCount", "column.testset.blockedCount", DataType.INTEGER, null, false, false, true), //
        TEST_SET_CAUTION_COUNT("cautionTestCaseCount", "column.testset.cautionCount", DataType.INTEGER, null, false, false, true), //
        TEST_SET_EXECUTION_DATE_TIME("executionDateTime", "column.testcase.executionDateTime", DataType.DATE_TIME, null, false, false, true), //
        /**
         * TEST_CASE_COLUMNS
         */
        TEST_CASE_NAME("name", "column.testcase.name", DataType.TEXT, null, true, false, true), //
        TEST_CASE_IS_AUTOMATED("automated", "column.testcase.automated", DataType.BOOLEAN, null, true, true, true), //
        TEST_CASE_EXECUTION_STATUS("executionStatus", "column.testcase.executionstatus", DataType.SELECT_ENUM, ExecutionStatus.class, false, false, true), //
        TEST_CASE_EXECUTION_DATE_TIME("executionDateTime", "column.testcase.executionDateTime", DataType.DATE_TIME, null, false, false, true), //
        TEST_CASE_VERSION("version", "column.testcase.version", DataType.TEXT, null, false, false, true), //

        /**
         * TESTSET_TESTCASES_COLUMN
         */
        TESTSET_TESTCASE_NAME("testSetTestCaseName", "column.testcase.name", DataType.TEXT, null, true, false, false), //
        TESTSET_TESTCASE_ID("testSetTestCaseId", "column.testcase.id", DataType.INTEGER, null, true, false, false), //
        /**
         * TEST_RUN_COLUMNS
         */
        TEST_RUN_TEST_CASE("testCaseName", "column.testrun.testcase.name", DataType.TEXT, null, false, false, true), //
        TEST_RUN_TEST_STEP_DESCRIPTION("testStepDescription", "column.testrun.teststep.description", DataType.TEXT, null, false, false, true), //
        TEST_RUN_TEST_STEP_TYPE("testStepType", "column.testrun.teststep.type", DataType.SELECT_ENUM, TestStepType.class, false, false, true), //
        TEST_RUN_TEST_STEP("testStepDescription", "column.testrun.teststep.description", DataType.TEXT, null, false, false, true), //
        TEST_RUN_CLIENT("clientName", "column.testrun.client.name", DataType.TEXT, null, false, false, true), //
        TEST_RUN_VERSION("version", "column.testrun.version", DataType.TEXT, null, false, false, true), //
        TEST_RUN_TEST_SET_NAME("testSetName", "column.testrun.testset.name", DataType.TEXT, null, false, false, true), //
        TEST_RUN_JOB_NAME("jobName", "column.testrun.job.name", DataType.TEXT, null, false, false, true), //
        TEST_RUN_TYPE("testRunType", "column.testrun.testRunType", DataType.SELECT_ENUM, TestRunType.class, false, true, true), //
        TEST_RUN_EXECUTION_STATUS("executionStatus", "column.testrun.executionstatus", DataType.SELECT_ENUM, ExecutionStatus.class, false, false, true), //
        TEST_RUN_START_DATE("startDate", "column.testrun.startdate", DataType.DATE_TIME, null, false, false, true), //
        TEST_RUN_END_DATE("endDate", "column.testrun.enddate", DataType.DATE_TIME, null, false, true, true), //
        TEST_RUN_ACTUAL_DURATION("actualDuration", "column.testrun.actualduration", DataType.INTEGER, null, false, false, true), //
        /**
         * JOB_PARAMETER_COLUMNS
         */
        JOB_PARAMETER_NAME("name", "column.jobparameter.name", DataType.TEXT, null, true, false, true), //
        /**
         * PROPERTY_COLUMNS
         */
        PROPERTY_PARAMETER("parameter", "column.property.parameter", DataType.TEXT, null, true, false, true), //
        PROPERTY_VALUE("value", "column.property.value", DataType.TEXT, null, true, false, true),

        /**
         * PROJECT_COLUMNS
         */
        PROJECT_NAME("name", "column.project.name", DataType.TEXT, null, true, false, true), //
        /**
         * DRAFT_COMMAND_COLUMNS
         */
        DRAFT_COMMAND_NAME("name", "column.draftcommand.name", DataType.TEXT, null, true, false, true), //
        /**
         * SINGLE_COMMAND_COLUMNS
         */
        SINGLE_COMMAND_NAME("name", "column.singlecommand.name", DataType.TEXT, null, true, false, true), //
        /**
         * CLIENT_COLUMNS
         */
        CLIENT_NAME("name", "column.client.name", DataType.TEXT, null, false, false, true), //
        /**
         * CLIENT_DTO_COLUMNS
         */
        CLIENT_DTO_NAME("clientName", "column.clientDTO.name", DataType.TEXT, null, false, false, true), //
        CLIENT_DTO_STATUS("clientStatus", "column.clientDTO.status", DataType.SELECT_ENUM, ClientStatus.class, false, false, true),
        CLIENT_DTO_TEST_SET_ID("testSetId", "column.clientDTO.testsetid", DataType.INTEGER, null, false, true, true),
        CLIENT_DTO_TEST_SET_STATUS("testSetStatus", "column.clientDTO.testsetstatus", DataType.SELECT_ENUM, CommandStatus.class, false, true, true),
        CLIENT_DTO_JOB_ID("jobId", "column.clientDTO.jobid", DataType.INTEGER, null, false, true, true),
        /**
         * TEDAM_USER_FAVORITE_COLUMNS
         */
        USER_FAVORITE_NAME("name", "column.tedamuserfavorite.name", DataType.TEXT, null, false, false, true), //
        USER_FAVORITE_TYPE("favoriteType", "column.tedamuserfavorite.type", DataType.SELECT_ENUM, TedamUserFavoriteType.class, false, false, true), //
        /**
         * JOB_COLUMNS
         */
        JOB_NAME("name", "column.job.name", DataType.TEXT, null, false, false, true), //
        JOB_TYPE("type", "column.job.type", DataType.SELECT_ENUM, JobType.class, false, true, true), //
		JOB_ENVIRONMENT_NAME("jobEnvironmentName", "column.job.environmentName", DataType.TEXT, null, true, false,
				true), //
        JOB_EXECUTION_DURATION("executionDuration", "column.job.executionDuration", DataType.TEXT, null, false, true, true), //
        JOB_LAST_EXECUTOR("lastExecutingUserName", "column.job.lastExecutor", DataType.TEXT, null, false, true, true), //
        JOB_LAST_EXECUTED_START_DATE("lastExecutedStartDate", "column.job.lastExecutedStartDate", DataType.DATE_TIME, null, false, false, true), //
        JOB_LAST_EXECUTED_END_DATE("lastExecutedEndDate", "column.job.lastExecutedEndDate", DataType.DATE_TIME, null, false, false, true), //
        JOB_IS_CI("ci", "column.job.ci", DataType.BOOLEAN, null, false, true, true), //
        JOB_STATUS("status", "column.job.status", DataType.SELECT_ENUM, JobStatus.class, false, false, true), //
        /**
         * JOB_DETAIL_COLUMNS
         */
        JOB_DETAIL_TEST_SET_ID("testSetId", "column.jobdetail.testSetId", DataType.INTEGER, null, false, false, false), //
        JOB_DETAIL_TEST_SET_NAME("testSetName", "column.jobdetail.testSetName", DataType.TEXT, null, false, false, false), //
        /**
         * JOB_PARAMETER_VALUE_COLUMNS
         */
        JOB_PARAMETER_VALUE_NAME("value", "column.jobparametervalue.name", DataType.TEXT, null, true, false, true), //
        /**
         * ENVIRONMENT_COLUMNN
         */
        ENVIRONMENT_NAME("name", "column.environment.name", DataType.TEXT, null, true, false, true), //
        /**
         * USER_COLUMN
         */
        USER_NAME("userName", "column.user.name", DataType.TEXT, null, true, false, true), //
        USER_ROLE("role", "column.user.role", DataType.SELECT_ENUM, TedamUserRole.class, true, false, true), //
        /**
         * GENERAL COLUMNS
         */
        ID("id", "column.id", DataType.INTEGER, null, false, false, true), //
        ID_NOT_SORTABLE("id", "column.id", DataType.INTEGER, null, false, false, false), //
        CREATED_USER("createdUser", "column.createduser", DataType.TEXT, null, false, true, true), //
        CREATED_USER_NOT_HIDDEN("createdUser", "column.createduser", DataType.TEXT, null, false, false, true), //
        CREATED_DATE("dateCreated", "column.createddate", DataType.DATE_TIME, null, false, true, true), //
        UPDATED_USER("updatedUser", "column.updateduser", DataType.TEXT, null, false, true, true), //
		UPDATED_DATE("dateUpdated", "column.updateddate", DataType.DATE_TIME, null, false, true, true), //

		/**
		 * NOTIFICATIONS COLUMNS
		 */
		NOTIFICATION_GROUP_NAME("groupName", "column.notificationGroup.groupName", DataType.TEXT, null, true, false,
				true),

		RECIPIENT_ADDRESS("address", "column.Recipient.address", DataType.TEXT, null, true, false,
				true);

        public static final List<GridColumn> TEST_CASE_TEST_RUN_COLUMNS = Arrays.asList(ID, TEST_RUN_VERSION, TEST_RUN_CLIENT, TEST_RUN_JOB_NAME, TEST_RUN_TEST_SET_NAME, TEST_RUN_TEST_CASE, TEST_RUN_TYPE,
                TEST_RUN_EXECUTION_STATUS, TEST_RUN_ACTUAL_DURATION, TEST_RUN_START_DATE, TEST_RUN_END_DATE, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> TEST_STEP_TEST_CASE_TEST_RUN_COLUMNS = Arrays.asList(ID, TEST_RUN_VERSION, TEST_RUN_CLIENT, TEST_RUN_JOB_NAME, TEST_RUN_TEST_CASE, TEST_RUN_TYPE,
                TEST_RUN_EXECUTION_STATUS, TEST_RUN_ACTUAL_DURATION, TEST_RUN_START_DATE, TEST_RUN_END_DATE, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> TEST_STEP_TEST_RUN_COLUMNS = Arrays.asList(ID, TEST_RUN_VERSION, TEST_RUN_TEST_STEP_DESCRIPTION, TEST_RUN_TEST_STEP_TYPE,
                TEST_RUN_TYPE, TEST_RUN_EXECUTION_STATUS, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> JOB_PARAMETER_COLUMNS = Arrays.asList(ID, JOB_PARAMETER_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> PROJECT_COLUMNS = Arrays.asList(ID, PROJECT_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> PROPERTY_COLUMNS = Arrays.asList(ID, PROPERTY_PARAMETER, PROPERTY_VALUE, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> DRAFT_COMMAND_COLUMNS = Arrays.asList(ID, DRAFT_COMMAND_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> SINGLE_COMMAND_COLUMNS = Arrays.asList(ID, SINGLE_COMMAND_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> CLIENT_COLUMNS = Arrays.asList(ID, CLIENT_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> CLIENT_DTO_COLUMNS = Arrays.asList(CLIENT_DTO_NAME, CLIENT_DTO_STATUS, CLIENT_DTO_TEST_SET_ID, CLIENT_DTO_TEST_SET_STATUS,
                CLIENT_DTO_JOB_ID);
        public static final List<GridColumn> USER_FAVORITE_COLUMNS = Arrays.asList(ID, USER_FAVORITE_NAME, USER_FAVORITE_TYPE, CREATED_USER, CREATED_DATE, UPDATED_USER,
                UPDATED_DATE);

        public static final List<GridColumn> JOB_COLUMNS = Arrays.asList(ID, JOB_NAME, JOB_TYPE, JOB_ENVIRONMENT_NAME, JOB_EXECUTION_DURATION, JOB_LAST_EXECUTOR, JOB_LAST_EXECUTED_START_DATE, JOB_LAST_EXECUTED_END_DATE,
                JOB_IS_CI, JOB_STATUS, CREATED_USER_NOT_HIDDEN, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

		public static final List<GridColumn> NOTIFICATION_COLUMNS = Arrays.asList(ID, NOTIFICATION_GROUP_NAME);

		public static final List<GridColumn> RECIPIENT_COLUMNS = Arrays.asList(ID_NOT_SORTABLE, RECIPIENT_ADDRESS);

        public static final List<GridColumn> JOB_PARAMETER_VALUE_COLUMNS = Arrays.asList(ID, JOB_PARAMETER_VALUE_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> TEST_CASES_COLUMNS = Arrays.asList(ID, TEST_CASE_NAME, TEST_CASE_IS_AUTOMATED, TEST_CASE_EXECUTION_DATE_TIME,
                TEST_CASE_EXECUTION_STATUS, TEST_CASE_VERSION, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> TESTSET_TESTCASES_COLUMNS = Arrays.asList(ID_NOT_SORTABLE, TESTSET_TESTCASE_ID, TESTSET_TESTCASE_NAME, CREATED_USER, CREATED_DATE,
                UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> TEST_SETS_COLUMNS = Arrays.asList(ID, TEST_SET_NAME, TEST_SET_JOB_LIST, TEST_SET_STATUS, TEST_SET_EXECUTION_DATE_TIME,
                TEST_SET_IS_AUTOMATED, TEST_SET_FAILED_COUNT, TEST_SET_SUCCEEDED_COUNT, TEST_SET_NOTRUN_COUNT, TEST_SET_BLOCKED_COUNT, TEST_SET_CAUTION_COUNT, CREATED_USER,
                CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> BASIC_TEST_SETS_COLUMNS = Arrays.asList(ID, TEST_SET_NAME, TEST_SET_JOB_LIST);

        public static final List<GridColumn> JOB_DETAILS_COLUMNS = Arrays.asList(JOB_DETAIL_TEST_SET_ID, JOB_DETAIL_TEST_SET_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER,
                UPDATED_DATE);

        public static final List<GridColumn> TEST_STEPS_COLUMNS = Arrays.asList(ID_NOT_SORTABLE, TEST_STEP_DESCRIPTION, TEST_STEP_FILENAME, TEST_STEP_TYPE, TEST_STEP_PARAMETER,
                CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> ENVIRONMENT_COLUMNS = Arrays.asList(ID, ENVIRONMENT_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> USER_COLUMNS = Arrays.asList(ID, USER_NAME, CREATED_USER, CREATED_DATE, UPDATED_USER, UPDATED_DATE);

        public static final List<GridColumn> UPLOADED_FILES_COLUMNS = Arrays.asList(UPLOADED_FILES_NAME);

        public static final List<GridColumn> SNAPSHOT_VALUE_COLUMNS = Arrays.asList(ID_NOT_SORTABLE, SNAPSHOT_VALUE_TAG, SNAPSHOT_VALUE_CAPTION, SNAPSHOT_VALUE_VALUE,
                SNAPSHOT_VALUE_TYPE, SNAPSHOT_VALUE_ROWINDEX, SNAPSHOT_VALUE_LOOKUP_PARAMETER, SNAPSHOT_VALUE_HAS_LOOKUP);

        public static final List<GridColumn> SNAPSHOT_VALUE_FOR_VERIFY_COLUMNS = Arrays.asList(ID_NOT_SORTABLE, SNAPSHOT_VALUE_TAG, SNAPSHOT_VALUE_CAPTION, SNAPSHOT_VALUE_VALUE,
                SNAPSHOT_VALUE_TYPE, SNAPSHOT_VALUE_ROWINDEX, SNAPSHOT_VALUE_CONTINUE_ON_ERROR);

        private final String columnName;
        private final String resourceName;
        private final DataType dataType;
        private final Class<?> filterBeanType;
        private final boolean editable;
        private final boolean hidden;
        private final boolean sortable;

        private GridColumn(String columnName, String resourceName, DataType filterType, Class<?> filterBeanType, boolean editable, boolean hidden, boolean sortable) {
            this.columnName = columnName;
            this.resourceName = resourceName;
            this.dataType = filterType;
            this.filterBeanType = filterBeanType;
            this.editable = editable;
            this.hidden = hidden;
            this.sortable = sortable;
        }

        /**
         * @return the columnName
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * @return the resourceName
         */
        public String getResourceName() {
            return resourceName;
        }

        /**
         * @return the columnType
         */
        public DataType getDataType() {
            return dataType;
        }

        public Class<?> getFilterBeanType() {
            return filterBeanType;
        }

        /**
         * @return the editable
         */
        public boolean isEditable() {
            return editable;
        }

        /**
         * @return the hidden
         */
        public boolean isHidden() {
            return hidden;
        }

        /**
         * @return the sortable
         */
        public boolean isSortable() {
            return sortable;
        }

    }

    public static enum DataType {
        NONE, TEXT, INTEGER, DATE, SELECT_ENUM, BOOLEAN, DATE_TIME, TEXT_AREA;
    }

}
