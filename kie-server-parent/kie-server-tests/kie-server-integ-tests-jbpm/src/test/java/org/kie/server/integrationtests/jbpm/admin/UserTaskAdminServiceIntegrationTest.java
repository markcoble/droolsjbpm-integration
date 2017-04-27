/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.integrationtests.jbpm.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.server.api.exception.KieServicesException;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.admin.EmailNotification;
import org.kie.server.api.model.admin.OrgEntities;
import org.kie.server.api.model.admin.TaskNotification;
import org.kie.server.api.model.admin.TaskReassignment;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
//import org.kie.server.client.KieServicesException;
import org.kie.server.integrationtests.config.TestConfig;
import org.kie.server.integrationtests.jbpm.JbpmKieServerBaseIntegrationTest;
import org.kie.server.integrationtests.shared.KieServerDeployer;
import org.kie.server.integrationtests.shared.KieServerSynchronization;

public class UserTaskAdminServiceIntegrationTest extends JbpmKieServerBaseIntegrationTest {

    private static ReleaseId releaseId = new ReleaseId("org.kie.server.testing", "definition-project",
            "1.0.0.Final");

    @BeforeClass
    public static void buildAndDeployArtifacts() {

        KieServerDeployer.buildAndDeployCommonMavenParent();
        KieServerDeployer.buildAndDeployMavenProject(ClassLoader.class.getResource("/kjars-sources/definition-project").getFile());

        kieContainer = KieServices.Factory.get().newKieContainer(releaseId);

        createContainer(CONTAINER_ID, releaseId, CONTAINER_ID_ALIAS, null);
    }

    @Override
    protected void addExtraCustomClasses(Map<String, Class<?>> extraClasses) throws Exception {
        extraClasses.put(PERSON_CLASS_NAME, Class.forName(PERSON_CLASS_NAME, true, kieContainer.getClassLoader()));
    }

    @After
    public void resetUser() throws Exception {
        changeUser(TestConfig.getUsername());
    }

    @Test
    public void testAddPotentialOwnersToNonExistentTask() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        OrgEntities add = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
        assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.addPotentialOwners(CONTAINER_ID, BAD_TASK_ID, false, add)
                                            , "Error code: 404", "Task with id " + BAD_TASK_ID + " not found");
    }

    @Test
    public void testRemovePotentialOwnersToNonExistentTask() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.removePotentialOwnerUsers(CONTAINER_ID, BAD_TASK_ID, USER_YODA)
                                            , "Error code: 404", "Task with id " + BAD_TASK_ID + " not found");
    }

    @Test
    public void testAddPotentialOwnersWithBadContainerId() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            TaskSummary task = tasks.get(0);
            OrgEntities add = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
            assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.addPotentialOwners(BAD_CONTAINER_ID, task.getId(), false, add)
                                                , "Error code: 404", "Could not find container with ID: " + BAD_CONTAINER_ID );
        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }
    
    @Test
    public void testRemovePotentialOwnersWithBadContainerId() throws Exception{
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            TaskSummary task = tasks.get(0);
            assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.removePotentialOwnerUsers(BAD_CONTAINER_ID, task.getId(),  USER_YODA)
                                              , "Error code: 404", "Could not find container with ID: " + BAD_CONTAINER_ID );
        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testAddRemovePotOwnersWithContainerAlias() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID_ALIAS, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);

            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);

            TaskSummary task = tasks.get(0);

            TaskInstance instance = taskClient.getTaskInstance(CONTAINER_ID_ALIAS, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();

            List<String> potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_YODA, "PM", "HR");

            OrgEntities add = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();

            userTaskAdminClient.addPotentialOwners(CONTAINER_ID_ALIAS, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID_ALIAS, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(4);
            Assertions.assertThat(potOwners).contains(USER_YODA, USER_JOHN, "PM", "HR");

            userTaskAdminClient.removePotentialOwnerUsers(CONTAINER_ID_ALIAS, task.getId(), USER_YODA);

            instance = taskClient.getTaskInstance(CONTAINER_ID_ALIAS, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_JOHN, "PM", "HR");

            userTaskAdminClient.removePotentialOwnerGroups(CONTAINER_ID_ALIAS, task.getId(), "PM", "HR");
            instance = taskClient.getTaskInstance(CONTAINER_ID_ALIAS, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(1);
            Assertions.assertThat(potOwners).contains(USER_JOHN);

            add = OrgEntities.builder().users(Arrays.asList(USER_YODA)).groups(Arrays.asList("PM")).build();

            userTaskAdminClient.addPotentialOwners(CONTAINER_ID_ALIAS, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID_ALIAS, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_YODA, USER_JOHN, "PM");

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID_ALIAS, processInstanceId);
            }
        }
    }
    @Test
    public void testReassignNotCompletedOnNonExistentTask() throws Exception {
        OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
        assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.reassignWhenNotCompleted(CONTAINER_ID, BAD_TASK_ID, "2s", reassign)
                                            , "Error code: 404", "Task with id " + BAD_TASK_ID + " not found");
    }

    @Test
    public void testReassignNotStartedOnNonExistentTask() throws Exception {
        OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
        assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.reassignWhenNotCompleted(CONTAINER_ID, BAD_TASK_ID, "2s", reassign)
                                            , "Error code: 404", "Task with id " + BAD_TASK_ID + " not found");
    }

    @Test
    public void testReassignNotCompletedWithBadTimeFormat() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            TaskSummary task = tasks.get(0);
            OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
            assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.reassignWhenNotCompleted(CONTAINER_ID, task.getId(), "2sssss", reassign)
                                                , "Error code: 400", "Error parsing time string:");
        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testReassignNotStartedWithBadTimeFormat() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            TaskSummary task = tasks.get(0);
            OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
            assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.reassignWhenNotStarted(CONTAINER_ID, task.getId(), "2ssss", reassign)
                                              ,"Error code: 400", "Error parsing time string:");
        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testReassignNotStartedWithBadContainerId() throws Exception{
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            TaskSummary task = tasks.get(0);
            OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
            assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.reassignWhenNotStarted(BAD_CONTAINER_ID, task.getId(), "2s", reassign)
                                                , "Error code: 404", "Could not find container with ID: " + BAD_CONTAINER_ID );
        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testReassignNotCompletedWithBadContainerId() throws Exception{
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            TaskSummary task = tasks.get(0);
            OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
            assertExceptionContainsCorrectMessage(() -> userTaskAdminClient.reassignWhenNotCompleted(BAD_CONTAINER_ID, task.getId(), "2s", reassign)
                    , "Error code: 404", "Could not find container with ID: " + BAD_CONTAINER_ID );
        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    private void assertExceptionContainsCorrectMessage(ThrowableAssert.ThrowingCallable callable, String restMessage, String jmsMessage) throws Exception {
        if (configuration.isRest()) {
            Assertions.assertThatThrownBy(callable)
                    .isInstanceOf(KieServicesException.class)
                    .hasMessageContaining(restMessage);
        } else {
            Assertions.assertThatThrownBy(callable)
                    .isInstanceOf(KieServicesException.class)
                    .hasMessageContaining(jmsMessage);
        }
    }

    @Test
    public void testAddRemovePotOwners() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);

            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);

            TaskSummary task = tasks.get(0);

            TaskInstance instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();

            List<String> potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_YODA, "PM", "HR");

            OrgEntities add = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();

            userTaskAdminClient.addPotentialOwners(CONTAINER_ID, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(4);
            Assertions.assertThat(potOwners).contains(USER_YODA, USER_JOHN, "PM", "HR");

            userTaskAdminClient.removePotentialOwnerUsers(CONTAINER_ID, task.getId(), USER_YODA);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_JOHN, "PM", "HR");

            userTaskAdminClient.removePotentialOwnerGroups(CONTAINER_ID, task.getId(), "PM", "HR");
            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(1);
            Assertions.assertThat(potOwners).contains(USER_JOHN);

            add = OrgEntities.builder().users(Arrays.asList(USER_YODA)).groups(Arrays.asList("PM")).build();

            userTaskAdminClient.addPotentialOwners(CONTAINER_ID, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_YODA, USER_JOHN, "PM");

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testAddRemoveExcludedOwners() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);

            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);

            TaskSummary task = tasks.get(0);

            TaskInstance instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();

            List<String> excludedOwners = instance.getExcludedOwners();
            Assertions.assertThat(excludedOwners).hasSize(0);

            OrgEntities add = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();

            userTaskAdminClient.addExcludedOwners(CONTAINER_ID, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            excludedOwners = instance.getExcludedOwners();
            Assertions.assertThat(excludedOwners).hasSize(1);
            Assertions.assertThat(excludedOwners).contains(USER_JOHN);

            userTaskAdminClient.removeExcludedOwnerUsers(CONTAINER_ID, task.getId(), USER_JOHN);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            excludedOwners = instance.getExcludedOwners();
            Assertions.assertThat(excludedOwners).hasSize(0);

            add = OrgEntities.builder().users(Arrays.asList(USER_YODA)).groups(Arrays.asList("PM")).build();

            userTaskAdminClient.addExcludedOwners(CONTAINER_ID, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            excludedOwners = instance.getExcludedOwners();
            Assertions.assertThat(excludedOwners).hasSize(2);
            Assertions.assertThat(excludedOwners).contains(USER_YODA, "PM");

            userTaskAdminClient.removeExcludedOwnerGroups(CONTAINER_ID, task.getId(), "PM");

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            excludedOwners = instance.getExcludedOwners();
            Assertions.assertThat(excludedOwners).hasSize(1);
            Assertions.assertThat(excludedOwners).contains(USER_YODA);

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testAddRemoveBusinessAdmins() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);

            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);

            TaskSummary task = tasks.get(0);

            TaskInstance instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();

            List<String> businessAdmins = instance.getBusinessAdmins();
            Assertions.assertThat(businessAdmins).hasSize(2);
            Assertions.assertThat(businessAdmins).contains(USER_ADMINISTRATOR, "Administrators");

            OrgEntities add = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();

            userTaskAdminClient.addBusinessAdmins(CONTAINER_ID, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            businessAdmins = instance.getBusinessAdmins();
            Assertions.assertThat(businessAdmins).hasSize(3);
            Assertions.assertThat(businessAdmins).contains(USER_ADMINISTRATOR, "Administrators", USER_JOHN);

            userTaskAdminClient.removeBusinessAdminUsers(CONTAINER_ID, task.getId(), USER_JOHN);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            businessAdmins = instance.getBusinessAdmins();
            Assertions.assertThat(businessAdmins).hasSize(2);
            Assertions.assertThat(businessAdmins).contains(USER_ADMINISTRATOR, "Administrators");

            add = OrgEntities.builder().users(Arrays.asList(USER_YODA)).groups(Arrays.asList("Administrators2")).build();

            userTaskAdminClient.addBusinessAdmins(CONTAINER_ID, task.getId(), false, add);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            businessAdmins = instance.getBusinessAdmins();
            Assertions.assertThat(businessAdmins).hasSize(4);
            Assertions.assertThat(businessAdmins).contains(USER_YODA, USER_ADMINISTRATOR, "Administrators", "Administrators2");


            userTaskAdminClient.removeBusinessAdminGroups(CONTAINER_ID, task.getId(), "Administrators2");
            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, false, true);
            Assertions.assertThat(instance).isNotNull();
            businessAdmins = instance.getBusinessAdmins();
            Assertions.assertThat(businessAdmins).hasSize(3);
            Assertions.assertThat(businessAdmins).contains(USER_YODA, USER_ADMINISTRATOR, "Administrators");

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testManageTaskInputAndOutput() throws Exception {
        changeUser(USER_ADMINISTRATOR);
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);

            List<TaskSummary> tasks = taskClient.findTasksAssignedAsBusinessAdministrator(USER_ADMINISTRATOR, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);

            TaskSummary task = tasks.get(0);

            TaskInstance instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), true, false, false);
            Assertions.assertThat(instance).isNotNull();

            Map<String, Object> input = instance.getInputData();
            Assertions.assertThat(input).isNotNull();
            Assertions.assertThat(input).hasSize(4);
            Assertions.assertThat(input).doesNotContainKey("new content");

            Map<String, Object> data = new HashMap<>();
            data.put("new content", "test");

            userTaskAdminClient.addTaskInputs(CONTAINER_ID, task.getId(), data);

            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), true, false, false);
            Assertions.assertThat(instance).isNotNull();

            input = instance.getInputData();
            Assertions.assertThat(input).isNotNull();
            Assertions.assertThat(input).hasSize(5);
            Assertions.assertThat(input.get("new content")).isEqualTo("test");

            userTaskAdminClient.removeTaskInputs(CONTAINER_ID, task.getId(), "new content");
            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), true, false, false);
            Assertions.assertThat(instance).isNotNull();

            input = instance.getInputData();
            Assertions.assertThat(input).isNotNull();
            Assertions.assertThat(input).hasSize(4);
            Assertions.assertThat(input).doesNotContainKey("new content");

            taskClient.saveTaskContent(CONTAINER_ID, task.getId(), data);
            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, true, false);
            Assertions.assertThat(instance).isNotNull();

            Map<String, Object> output = instance.getOutputData();
            Assertions.assertThat(output).isNotNull();
            Assertions.assertThat(output).hasSize(1);
            Assertions.assertThat(output.get("new content")).isEqualTo("test");

            userTaskAdminClient.removeTaskOutputs(CONTAINER_ID, task.getId(), "new content");
            instance = taskClient.getTaskInstance(CONTAINER_ID, task.getId(), false, true, false);
            Assertions.assertThat(instance).isNotNull();

            output = instance.getOutputData();
            Assertions.assertThat(output).isNotNull();
            Assertions.assertThat(output).hasSize(0);

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testReassignmentWhenNotStarted() throws Exception {
        testReassignment(true);
    }

    @Test
    public void testReassignmentWhenNotCompleted() throws Exception {

        testReassignment(false);
    }


    private void testReassignment(boolean whenNotStarted) throws Exception {

        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);
            changeUser(USER_YODA);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsPotentialOwner(USER_YODA, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);
            long taskId = tasks.get(0).getId();
            taskClient.claimTask(CONTAINER_ID, taskId, USER_YODA);

            changeUser(USER_ADMINISTRATOR);

            TaskInstance instance = taskClient.getTaskInstance(CONTAINER_ID, taskId, false, false, true);
            Assertions.assertThat(instance).isNotNull();
            Assertions.assertThat(instance.getStatus()).isEqualTo("Reserved");

            List<String> potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(3);
            Assertions.assertThat(potOwners).contains(USER_YODA, "PM", "HR");

            OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();

            if (whenNotStarted) {
                userTaskAdminClient.reassignWhenNotStarted(CONTAINER_ID, taskId, "2s", reassign);
            } else {
                userTaskAdminClient.reassignWhenNotCompleted(CONTAINER_ID, taskId, "2s", reassign);
            }


            KieServerSynchronization.waitForTaskStatus(taskClient, taskId, "Ready");

            instance = taskClient.getTaskInstance(CONTAINER_ID, taskId, false, false, true);
            Assertions.assertThat(instance).isNotNull();
            Assertions.assertThat(instance.getStatus()).isEqualTo("Ready");

            potOwners = instance.getPotentialOwners();
            Assertions.assertThat(potOwners).hasSize(1);
            Assertions.assertThat(potOwners).contains(USER_JOHN);


        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testCancelReassignWhenNotStarted() throws Exception {
        testCancelReassign(true);
    }

    @Test
    public void testCancelReassignWhenNotCompleted() throws Exception {

        testCancelReassign(false);
    }

    private void testCancelReassign(boolean whenNotStarted) throws Exception{
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);
            changeUser(USER_YODA);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsPotentialOwner(USER_YODA, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);
            long taskId = tasks.get(0).getId();
            taskClient.claimTask(CONTAINER_ID, taskId, USER_YODA);

            changeUser(USER_ADMINISTRATOR);

            List<TaskReassignment> reassignments = userTaskAdminClient.getTaskReassignments(CONTAINER_ID, taskId, true);
            Assertions.assertThat(reassignments).isNotNull();
            Assertions.assertThat(reassignments).hasSize(0);

            OrgEntities reassign = OrgEntities.builder().users(Arrays.asList(USER_JOHN)).build();
            Long reassignmentId = null;
            if (whenNotStarted) {
                reassignmentId = userTaskAdminClient.reassignWhenNotStarted(CONTAINER_ID, taskId, "10s", reassign);
            } else {
                reassignmentId = userTaskAdminClient.reassignWhenNotCompleted(CONTAINER_ID, taskId, "10s", reassign);
            }

            reassignments = userTaskAdminClient.getTaskReassignments(CONTAINER_ID, taskId, true);
            Assertions.assertThat(reassignments).isNotNull();
            Assertions.assertThat(reassignments).hasSize(1);

            userTaskAdminClient.cancelReassignment(CONTAINER_ID, taskId, reassignmentId);

            reassignments = userTaskAdminClient.getTaskReassignments(CONTAINER_ID, taskId, true);
            Assertions.assertThat(reassignments).isNotNull();
            Assertions.assertThat(reassignments).hasSize(0);

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }

    @Test
    public void testCancelNotifyWhenNotStarted() throws Exception {
        testCancelNotify(true);
    }

    @Test
    public void testCancelNotifyWhenNotCompleted() throws Exception {

        testCancelNotify(false);
    }

    private void testCancelNotify(boolean whenNotStarted) throws Exception{
        Map<String, Object> parameters = new HashMap<String, Object>();

        Long processInstanceId = null;
        try {
            processInstanceId = processClient.startProcess(CONTAINER_ID, PROCESS_ID_EVALUATION, parameters);
            Assertions.assertThat(processInstanceId).isNotNull();
            Assertions.assertThat(processInstanceId.longValue()).isGreaterThan(0);
            changeUser(USER_YODA);
            List<TaskSummary> tasks = taskClient.findTasksAssignedAsPotentialOwner(USER_YODA, 0, 10);
            Assertions.assertThat(tasks).hasSize(1);
            long taskId = tasks.get(0).getId();
            taskClient.claimTask(CONTAINER_ID, taskId, USER_YODA);

            changeUser(USER_ADMINISTRATOR);

            List<TaskNotification> notifications = userTaskAdminClient.getTaskNotifications(CONTAINER_ID, taskId, true);
            Assertions.assertThat(notifications).isNotNull();
            Assertions.assertThat(notifications).hasSize(0);

            EmailNotification emailNotification = EmailNotification.builder()
                    .from("test@jbpm.org")
                    .replyTo("no-reply@jbpm.org")
                    .subject("reminder")
                    .body("my test content")
                    .users(Arrays.asList(USER_JOHN))
                    .build();
            Long notificationId = null;
            if (whenNotStarted) {
                notificationId = userTaskAdminClient.notifyWhenNotStarted(CONTAINER_ID, taskId, "10s", emailNotification);
            } else {
                notificationId = userTaskAdminClient.notifyWhenNotCompleted(CONTAINER_ID, taskId, "10s", emailNotification);
            }

            notifications = userTaskAdminClient.getTaskNotifications(CONTAINER_ID, taskId, true);
            Assertions.assertThat(notifications).isNotNull();
            Assertions.assertThat(notifications).hasSize(1);

            userTaskAdminClient.cancelNotification(CONTAINER_ID, taskId, notificationId);

            notifications = userTaskAdminClient.getTaskNotifications(CONTAINER_ID, taskId, true);
            Assertions.assertThat(notifications).isNotNull();
            Assertions.assertThat(notifications).hasSize(0);

        } finally {
            if (processInstanceId != null) {
                processClient.abortProcessInstance(CONTAINER_ID, processInstanceId);
            }
        }
    }
}