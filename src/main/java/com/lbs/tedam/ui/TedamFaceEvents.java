
package com.lbs.tedam.ui;

import com.lbs.tedam.model.*;
import com.lbs.tedam.util.EnumsV2.TestStepType;

import java.util.List;

public class TedamFaceEvents {

    private TedamFaceEvents() {
    }

    public static final class FileAttachEvent {
        private final TedamFile uploadedFileName;

        public FileAttachEvent(TedamFile uploadedFileName) {
            this.uploadedFileName = uploadedFileName;
        }

        public TedamFile getUploadedFileName() {
            return uploadedFileName;
        }
    }

    public static final class FileUploadEvent {
        private final TedamFile uploadedFileName;

        public FileUploadEvent(TedamFile uploadedFileName) {
            this.uploadedFileName = uploadedFileName;
        }

        public TedamFile getUploadedFileName() {
            return uploadedFileName;
        }
    }

    public static final class TestStepSelectedEvent {
        private final TestStep testStep;

        public TestStepSelectedEvent(TestStep testStep) {
            this.testStep = testStep;
        }

        public TestStep getTestStep() {
            return testStep;
        }
    }

    public static final class LookUpSelectedEvent {
        private final TestStep lookUp;

        public LookUpSelectedEvent(TestStep lookUp) {
            this.lookUp = lookUp;
        }

        public TestStep getLookUp() {
            return lookUp;
        }
    }

    public static final class TestStepTypeParameterPreparedEvent {
        private final TestStep testStep;

        public TestStepTypeParameterPreparedEvent(TestStep testStep) {
            this.testStep = testStep;
        }

        public TestStep getTestStep() {
            return testStep;
        }
    }

    public static final class JobEditedEvent {
        private final Job job;

        public JobEditedEvent(Job job) {
            this.job = job;
        }

        public Job getJob() {
            return job;
        }

    }

    public static final class SnapshotValuesSelectedEvent {

        private final List<SnapshotValue> snapshotValues;
        private TestStepType testStepType;

        public SnapshotValuesSelectedEvent(List<SnapshotValue> snapshotValues, TestStepType testStepType) {
            this.snapshotValues = snapshotValues;
            this.testStepType = testStepType;
        }

        public List<SnapshotValue> getSnapshotValues() {
            return snapshotValues;
        }

        public TestStepType getTestStepType() {
            return testStepType;
        }

    }

    public static final class ProjectEvent {
        private final List<Project> projectList;

        public ProjectEvent(List<Project> projectList) {
            this.projectList = projectList;
        }

        public List<Project> getProjectList() {
            return projectList;
        }

    }

    public static final class TestCaseSelectEvent {
        private final List<TestCase> testCaseList;

        public TestCaseSelectEvent(List<TestCase> testCaseList) {
            this.testCaseList = testCaseList;
        }

        public List<TestCase> getTestCaseList() {
            return testCaseList;
        }
    }

    public static final class JobParameterValueSelectEvent {
        private final JobParameterValue jobParameterValue;

        public JobParameterValueSelectEvent(JobParameterValue jobParameterValue) {
            this.jobParameterValue = jobParameterValue;
        }

        public JobParameterValue getJobParameterValue() {
            return jobParameterValue;
        }

    }

    public static final class ClientSelectEvent {
        private final List<Client> clientList;

        public ClientSelectEvent(List<Client> clientList) {
            this.clientList = clientList;
        }

        public List<Client> getClientList() {
            return clientList;
        }

    }

    public static final class EnvironmentSelectEvent {
        private final Environment environment;

        public EnvironmentSelectEvent(Environment environment) {
            this.environment = environment;
        }

        public Environment getEnvironment() {
            return environment;
        }
    }

    public static final class FavoriteEnvironmentSelectEvent {
        private final List<Environment> environmentList;

        public FavoriteEnvironmentSelectEvent(List<Environment> environmentList) {
            this.environmentList = environmentList;
        }

        public List<Environment> getEnvironmentList() {
            return environmentList;
        }
    }

    public static final class JobSelectEvent {
        private final List<Job> jobList;

        public JobSelectEvent(List<Job> jobList) {
            this.jobList = jobList;
        }

        public List<Job> getJobList() {
            return jobList;
        }

    }

    public static final class TestSetEvent {
        private final List<TestSet> testSetList;

        public TestSetEvent(List<TestSet> testSetList) {
            this.testSetList = testSetList;
        }

        public List<TestSet> getTestSetList() {
            return testSetList;
        }
    }

    public static final class TestSetSelectEvent {
        private final TestSet testSet;

        public TestSetSelectEvent(TestSet testSet) {
            this.testSet = testSet;
        }

        public TestSet getTestSet() {
            return testSet;
        }
    }

    public static final class TedamFolderEvent {
        private final TedamFolder tedamFolder;

        public TedamFolderEvent(TedamFolder tedamFolder) {
            this.tedamFolder = tedamFolder;
        }

        public TedamFolder getTedamFolder() {
            return tedamFolder;
        }
    }

    public static final class TedamFolderSelectEvent {
        private final TedamFolder tedamFolder;

        public TedamFolderSelectEvent(TedamFolder tedamFolder) {
            this.tedamFolder = tedamFolder;
        }

        public TedamFolder getTedamFolder() {
            return tedamFolder;
        }
    }

    public static final class UnsavedChangesEvent {
        private final Runnable runnable;

        public UnsavedChangesEvent(Runnable runnable) {
            this.runnable = runnable;
        }

        public Runnable getRunnable() {
            return runnable;
        }
    }

}