package org.kie.simulation.api;

import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.RequestContext;
import org.kie.simulation.model.SimulationCommand;
import org.kie.simulation.model.SimulationRequest;
import org.kie.simulation.model.SimulationRequestImpl;
import org.kie.simulation.model.SimulationResponse;
import org.kie.simulation.test.SimulationTestBase;
import org.kie.simulation.test.model.Address;
import org.kie.simulation.test.model.Person;

import static org.junit.Assert.assertNotNull;

import org.drools.core.command.GetKieContainerCommand;
import org.drools.core.command.NewKieSessionCommand;
import org.drools.core.command.OutCommand;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.fluent.impl.NewContextCommand;

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

        Simulation simulation = new SimulationImpl();
        
        // Sample Scenario XML
        String requestXml = SimulationTestBase.readFile("scenario_out.xml");

        // Mock reading of XML scenario file
        SimulationRequest request = SimulationTestBase.convertXMLToObject(requestXml,
                                                                          SimulationRequestImpl.class,
                                                                          SimulationCommand.class,
                                                                          Person.class);

        SimulationResponse response = simulation.execute(request);

        Person person = (Person) ((RequestContext) response.getResult()).get("person");

        assertNotNull(person);

    }

    @Test
    public void testOutWithPriorSetAndNoName() {

        //      assertEquals(p,
        //                   requestContext.get("person"));

    }

    @Test
    public void testSetAndGetWithCommandRegisterWithEnds() {

        // Check that nothing went to the 'out'
        //    assertEquals(p1,
        //              requestContext.get("p1"));
        //     assertEquals(p2,
        //                 requestContext.get("p2"));
    }

}
