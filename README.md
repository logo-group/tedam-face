# TEDAM-Face
<a href="http://www.logo.com.tr"><img src="https://www.logo.com.tr/img/logo.png"/></a>

[![Build Status](https://travis-ci.com/logobs/tedam-face.svg?branch=master)](https://travis-ci.com/logobs/tedam-face)
[![sonar-quality-gate][sonar-quality-gate]][sonar-url] [![sonar-bugs][sonar-bugs]][sonar-url] [![sonar-vulnerabilities][sonar-vulnerabilities]][sonar-url] [![sonar-duplicated-lines][sonar-dublicated-lines]][sonar-url]

[sonar-url]: https://sonarcloud.io/dashboard?id=com.lbs.tedam%3ATEDAMFaceV2
[sonar-quality-gate]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=alert_status
[sonar-bugs]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=bugs
[sonar-vulnerabilities]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=vulnerabilities
[sonar-dublicated-lines]: https://sonarcloud.io/api/project_badges/measure?project=com.lbs.tedam%3ATEDAMFaceV2&metric=duplicated_lines_density


## Creation of test scenarios

The TEDAM automation process starts with the creation of test scenarios in the TedamFace interface. 
When creating the test scenario, it is critical that the scenario steps are atomic.
When writing a test scenario, it is important  that a single mouse movement corresponds to a single scenario step.

## Associate snapshot and test scenarios

Select the snapshot files related to the test scenario and upload to the system. Associate the test scenario steps with the related snapshot file. In this way, the user can continue the process by selecting the components to be interacted through the interface connected to the test step.<br>
Currently available test step types;<br>
ButtonClick: In snapshot file contains a list of clickable components and selected which component the user will click.<br>
Formfill: In snapshot file contains a list of text fillable components and  user value writes the value to the writable component.<br>
Filterfill: In snapshot file contains a list of filter fillable components and user writes the filter value in the filterable field.<br>
Verify: Verifies the data on the screen.<br>
Delete: Provides to delete of data.<br>
DoubleClick: In snapshot file contains a list of double clickable components and selected which component the user will double click.<br>
Wait: Provides to wait otomation according to entered parameter.<br>

## Creation of test sets

The user can combine test scenarios with TestSet in small meaningful groups.<br>
For example, when ordering, first of all, the product to be ordered may need to be create with automation in the system.<br>
In this case, it can be ensured that the product creation and ordering scenarios can be combined in a testset for successive operation.<br> 

## Creation of Job
Job is the component where the test sets are classified and the clients to which they will run added
When a job is created; it need following components: Test Sets which will be executed, Agents which run jobs , Environment which job runs on it

## Job Manager
It is the screen where the active jobs can be started and stopped. Whether jobs run or not are monitored currently.


