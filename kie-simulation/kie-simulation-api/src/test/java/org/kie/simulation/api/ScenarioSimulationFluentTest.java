package org.kie.simulation.api;

import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.builder.ExecutableBuilder;
import org.kie.api.runtime.builder.KieSessionFluent;
import org.kie.api.runtime.builder.Scope;
import org.kie.simulation.model.SimulationCommand;
import org.kie.simulation.model.SimulationRequest;
import org.kie.simulation.model.SimulationRequestImpl;
import org.kie.simulation.model.SimulationResponse;
import org.kie.simulation.test.SimulationTestBase;
import org.kie.simulation.test.model.Address;
import org.kie.simulation.test.model.Person;

import static org.junit.Assert.*;

import org.drools.core.command.GetKieContainerCommand;
import org.drools.core.command.NewKieSessionCommand;
import org.drools.core.command.OutCommand;
import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.fluent.impl.Batch;
import org.drools.core.fluent.impl.BatchImpl;
import org.drools.core.fluent.impl.ExecutableBuilderImpl;
import org.drools.core.fluent.impl.ExecutableImpl;
import org.drools.core.fluent.impl.NewContextCommand;
import org.drools.core.fluent.impl.SetCommand;

public class ScenarioSimulationFluentTest {

    String header = "package org.drools.compiler\n" +
                    "import org.kie.simulation.test.model.Person\n" +
                    "import org.kie.simulation.test.model.Address\n";

    String drl1 = "global Person person;\n" +
                  "global String outS;\n" +
                  "global Long timeNow;\n" +
                  "rule R1 when\n" + " p : Person() \n" +
                  "then\n" +
                  "    kcontext.getKnowledgeRuntime().setGlobal(\"person\", p);\n" + "    kcontext.getKnowledgeRuntime().setGlobal(\"timeNow\", kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime() );\n" +
                  "end\n";

    ReleaseId releaseId = SimulationTestBase.createKJarWithMultipleResources("org.kie",
                                                                             new String[]{header + drl1},
                                                                             new ResourceType[]{ResourceType.DRL});

    @Test
    public void testOutName() {

        String fluentXml = SimulationTestBase.readFile("testOutName.xml");
        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = ExecutableRunner.create().execute(exec);

        assertEquals("Simmy Sim",
                     ((Person) requestContext.get("outS")).getName());
    }

    //    @Test
    //    public void testAfter() {
    //
    //        Simulation simulation = new SimulationImpl();
    //
    //        // Sample Scenario XML
    //        String requestXml = SimulationTestBase.readFile("scenario_after.xml");
    //
    //        // Mock reading of XML scenario file
    //        SimulationRequest request = SimulationTestBase.convertXMLToObject(requestXml,
    //                                                                          SimulationRequestImpl.class,
    //                                                                          SimulationCommand.class,
    //                                                                          Person.class);
    //
    //        SimulationResponse response = simulation.execute(request);
    //
    //        String responseXml = SimulationTestBase.convertObjectToXML(response);
    //
    //        System.out.println(responseXml);
    //
    //        //      assertEquals(p,
    //        //                   requestContext.get("person"));
    //
    //    }

    //    @Test
    //    public void testSetAndGetWithCommandRegisterWithEnds() {
    //
    //        Simulation simulation = new SimulationImpl();
    //
    //        // Sample Scenario XML
    //        String requestXml = SimulationTestBase.readFile("scenario_two_sessions.xml");
    //
    //        // Mock reading of XML scenario file
    //        SimulationRequest request = SimulationTestBase.convertXMLToObject(requestXml,
    //                                                                          SimulationRequestImpl.class,
    //                                                                          SimulationCommand.class,
    //                                                                          Person.class);
    //
    //        SimulationResponse response = simulation.execute(request);
    //
    //        String responseXml = SimulationTestBase.convertObjectToXML(response);
    //
    //        System.out.println(responseXml);
    //
    //        Person person = (Person) ((RequestContext) response.getResult()).get("person");
    //
    //        // Check that nothing went to the 'out'
    //        //    assertEquals(p1,
    //        //              requestContext.get("p1"));
    //        //     assertEquals(p2,
    //        //                 requestContext.get("p2"));
    //    }

    @Test
    public void testOutWithPriorSetAndNoName() {

        String fluentXml = SimulationTestBase.readFile("testOutWithPriorSetAndNoName.xml");

        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = ExecutableRunner.create().execute(exec);

    }

    @Test
    public void testOutWithoutPriorSetAndNoName() {

        try {
            String fluentXml = SimulationTestBase.readFile("testOutWithoutPriorSetAndNoName.xml");
            Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                    ExecutableImpl.class,
                                                                    Person.class);

            // Process commands and execute
            RequestContext requestContext = ExecutableRunner.create(0l).execute(exec);
            assertEquals("success",
                         requestContext.get("out1"));
            fail("Must throw Exception, as no prior set was called and no name given to out");
        } catch (Exception e) {

        }
    }

    @Test
    public void testSetAndGetWithCommandRegisterWithEnds() {

        String fluentXml = SimulationTestBase.readFile("testSetAndGetWithCommandRegisterWithEnds.xml");
        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = ExecutableRunner.create().execute(exec);

        // Check that nothing went to the 'out'
        //        assertEquals("h1", requestContext.get("outS1"));
        //        assertEquals("h2", requestContext.get("outS2"));
    }

    @Test
    public void testSetAndGetWithCommandRegisterWithoutEnds() {

        String fluentXml = SimulationTestBase.readFile("testSetAndGetWithCommandRegisterWithoutEnds.xml");
        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = ExecutableRunner.create().execute(exec);

        // Check that nothing went to the 'out'
        //        assertEquals("h1", requestContext.get("outS1"));
        //        assertEquals("h2", requestContext.get("outS2"));
    }

    @Test
    public void testDifferentConversationIds() {

        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        String fluentXml = SimulationTestBase.readFile("testDifferentConversationIds.xml");
        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = runner.execute(exec);

        String conversationId = requestContext.getConversationContext().getName();

        runner.execute(exec,
                       requestContext);

        assertNotEquals(conversationId,
                        requestContext.getConversationContext().getName());
    }

    @Test
    public void testRequestScope() {

        String fluentXml = SimulationTestBase.readFile("testRequestScope.xml");
        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = ExecutableRunner.create().execute(exec);

        // Check that nothing went to the 'out'
        assertNull(requestContext.get("outS"));
        assertNull(requestContext.getApplicationContext().get("outS1"));
        assertNull(requestContext.getConversationContext());
        assertNotNull("Person not found.",
                      requestContext.get("outS1"));
    }

    @Test
    public void testApplicationScope() {

        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        String fluentXml1 = SimulationTestBase.readFile("testApplicationScope_1.xml");
        String fluentXml2 = SimulationTestBase.readFile("testApplicationScope_2.xml");

        Executable exec1 = SimulationTestBase.convertXMLToObject(fluentXml1,
                                                                 ExecutableImpl.class,
                                                                 Person.class);
        Executable exec2 = SimulationTestBase.convertXMLToObject(fluentXml2,
                                                                 ExecutableImpl.class,
                                                                 Person.class);

        // Process commands and execute
        RequestContext requestContext = runner.execute(exec1);

        // Check that nothing went to the 'out'
        assertEquals(null,
                     requestContext.get("person"));
        assertEquals("p1",
                     ((Person) requestContext.getApplicationContext().get("outS1")).getName());

        requestContext = (RequestContextImpl) runner.execute(exec2);
        assertEquals("p1",
                     ((Person) requestContext.getApplicationContext().get("outS1")).getName());
        assertEquals("p2",
                     ((Person) requestContext.getApplicationContext().get("outS2")).getName());
    }

    @Test
    public void testConversationScope() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        String fluentXml1 = SimulationTestBase.readFile("testConversationScope_1.xml");
        String fluentXml2 = SimulationTestBase.readFile("testConversationScope_2.xml");

        Executable exec1 = SimulationTestBase.convertXMLToObject(fluentXml1,
                                                                 ExecutableImpl.class,
                                                                 Person.class);
        Executable exec2 = SimulationTestBase.convertXMLToObject(fluentXml2,
                                                                 ExecutableImpl.class,
                                                                 Person.class);

        RequestContextImpl requestContext = (RequestContextImpl) runner.execute(exec1);

        // check that nothing went to the 'out'
        assertEquals(null,
                     requestContext.get("outS"));

        String conversationId = requestContext.getConversationContext().getName();

        assertEquals("p1",
                     ((Person) requestContext.getConversationContext().get("outS1")).getName());

        //        // Make another request, add to conversation context, assert old and new values are there.
        //        requestContext = (RequestContextImpl) runner.execute(exec2);
        //        assertEquals("p1",
        //                     ((Person) requestContext.getConversationContext().get("outS1")).getName());
        //        assertEquals("p2",
        //                     ((Person) requestContext.getConversationContext().get("outS2")).getName());
        //
        // End the conversation, check it's now null
        //        f = new ExecutableBuilderImpl();
        //        f.endConversation(conversationId);
        //
        //        requestContext = (RequestContextImpl) runner.execute(exec3);
        //        assertNull(requestContext.getConversationContext());
    }

    @Test
    public void testContextScopeSearching() {
        ExecutableRunner<RequestContext> runner = ExecutableRunner.create();

        ExecutableBuilder f = ExecutableBuilder.create();

        // Check that get() will search up to Application, when no request or conversation values
        f.newApplicationContext("app1")
         .getKieContainer(releaseId).newSession()
         .insert("h1")
         .fireAllRules()
         .getGlobal("outS").set("outS1",
                                Scope.APPLICATION)
         .get("outS1").out()
         .dispose();

        System.out.println(SimulationTestBase.convertObjectToXML(f.getExecutable()));

        RequestContext requestContext = runner.execute(f.getExecutable());

        assertEquals("h1",
                     requestContext.get("outS1"));
        assertEquals("h1",
                     requestContext.getApplicationContext().get("outS1"));
        assertEquals("h1",
                     requestContext.get("outS1"));

        // Check that get() will search up to Conversation, thus over-riding Application scope and ignoring Request when it has no value
        f = new ExecutableBuilderImpl();

        f.getApplicationContext("app1").startConversation()
         .getKieContainer(releaseId).newSession()
         .insert("h2")
         .fireAllRules()
         .getGlobal("outS").set("outS1",
                                Scope.CONVERSATION)
         .get("outS1").out()
         .dispose();
        requestContext = runner.execute(f.getExecutable());

        assertEquals("h2",
                     requestContext.get("outS1"));
        assertEquals("h1",
                     requestContext.getApplicationContext().get("outS1"));
        assertEquals("h2",
                     requestContext.getConversationContext().get("outS1"));
        assertEquals("h2",
                     requestContext.get("outS1"));

        // Check that get() will search directly to Request, thus over-riding Application and Conversation scoped values
        f = new ExecutableBuilderImpl();

        f.getApplicationContext("app1").joinConversation(requestContext.getConversationContext().getName())
         .getKieContainer(releaseId).newSession()
         .insert("h3")
         .fireAllRules()
         .getGlobal("outS").set("outS1",
                                Scope.REQUEST)
         .get("outS1").out()
         .dispose();
        requestContext = runner.execute(f.getExecutable());

        assertEquals("h3",
                     requestContext.get("outS1"));
        assertEquals("h1",
                     requestContext.getApplicationContext().get("outS1"));
        assertEquals("h2",
                     requestContext.getConversationContext().get("outS1"));
        assertEquals("h3",
                     requestContext.get("outS1"));
    }

    @Test
    public void testAfter() {
        String fluentXml = SimulationTestBase.readFile("testAfter.xml");
        Executable exec = SimulationTestBase.convertXMLToObject(fluentXml,
                                                                ExecutableImpl.class,
                                                                Person.class);

        // Process commands and execute
        RequestContext requestContext = ExecutableRunner.create(0l).execute(exec);

        assertEquals(1000l,
                     requestContext.get("timeNow1"));
        assertEquals(2000l,
                     requestContext.get("timeNow2"));
    }

    public static Person createPerson(String name) {

        Person p = new Person();
        p.setName(name);

        Address[] adds = {new Address("add1",
                                      "l2",
                                      "l3"), new Address("add2",
                                                         "l2",
                                                         "l3")};
        p.setAddressArray(adds);

        return p;

    }
}
