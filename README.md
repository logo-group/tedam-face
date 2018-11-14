# TEDAM-Face
<a href="http://www.logo.com.tr"><img src="https://www.logo.com.tr/img/logo.png"/></a>

[![Build Status](https://travis-ci.com/logobs/tedam-face.svg?branch=master)](https://travis-ci.com/logobs/tedam-face)
[![sonar-quality-gate][sonar-quality-gate]][sonar-url] [![sonar-bugs][sonar-bugs]][sonar-url] [![sonar-vulnerabilities][sonar-vulnerabilities]][sonar-url] [![sonar-duplicated-lines][sonar-dublicated-lines]][sonar-url]

[sonar-url]: https://sonarcloud.io/dashboard?id=com.lbs.tedam%3ATEDAMFaceV2
[sonar-quality-gate]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=alert_status
[sonar-bugs]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=bugs
[sonar-vulnerabilities]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=vulnerabilities
[sonar-dublicated-lines]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=duplicated_lines_density


## Test Scenarios
This is the screen where the user can add, delete and view the test scenarios.<br>
Click the + button in order to access the screen to add scenerios.<br>
Enter the scenerio name and description.<br>
Use “Add row” button to add test steps.<br>
By double clicking on the added lines write the Test Steps Description.<br>
By double ckicking on the added lines select Test scenario Tip such as Button, Form Open, Form Fill, Verify, Delete, Wait ect.<br>

## Upload File - Associate
Select the snapshot files related to the test scenario and upload to the system. Associate the test scenario steps  with the related snapshot file.  The parameter field is filled by selecting the element in the snapshot file according to the test scenario step.

## Job Parameters
It is the screen where the parameters required for the operation of a job are defined, modified and deleted.<br>
The job parameter screen is accessed by clicking the ”+” button.<br>
Enter the job parameter name.<br>
Enter the values of the parameter with the Add row button.<br>
The parameter is saved with the Save button.<br>

## Environments
The environments in which test scenarios will run are defined, modified and deleted.<br>
The environments screen is accessed by clicking the ”+” button.<br>
Enter the environment name.<br>
The parameters grid lists the defined parameters for the run of the test scenarios. The parameters to be used in the defined environment are selected.<br>
The environment is saved with the Save button.<br>

## Clients
**Test senaryolarının koşacağı istemcinin tanımlandığı, değiştirildiği ve silindiği ekrandır.**
The client screen is accessed by clicking the “+” button.<br>
Enter the client name.<br>
The client is saved with the Save button.<br>

## Jobs
It is the screen where the test sets are classified and the clients to which they will run are added.<br>
The job screen is accessed by clicking the “+” button.<br>
Enter the job name.<br>
The environment in which the job runs is selected from the environments defined in the "Environment Select" field.<br>
The "Test Set Add" button opens the Test sets screen. Select the relevant test sets and click “Okey” button.<br>
The test sets selected on the relevant job page are listed.<br>
The "Client Add" button opens the Clients screen. Select the relevant client and click "Okey" button.<br>
The job is transferred to the Job Manager page with the "Send to Active" button.<br>

## Job Manager
It is the screen where the active jobs can be started and stopped. Whether jobs run or not are monitored currently.


