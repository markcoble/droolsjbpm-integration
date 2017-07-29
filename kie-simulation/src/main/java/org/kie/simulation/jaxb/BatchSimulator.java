package org.kie.simulation.jaxb;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.drools.core.fluent.impl.ExecutableBuilderImpl;
import org.drools.core.fluent.impl.PseudoClockRunner;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;

import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.simulation.core.command.GetKieContainerCommand;
import org.kie.simulation.core.command.NewKieSessionCommand;
import org.kie.simulation.core.command.OutCommand;
import org.kie.simulation.core.command.runtime.FireAllRulesCommand;
import org.kie.simulation.core.command.runtime.GetGlobalCommand;
import org.kie.simulation.core.command.runtime.InsertObjectCommand;
import org.kie.simulation.core.fluent.Batch;
import org.kie.simulation.core.fluent.BatchImpl;
import org.kie.simulation.core.fluent.NewContextCommand;

public class BatchSimulator {

    public static <T> String convertObjectToXML( T object ) {
        try {
            StringWriter stringWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance( object.getClass(),
                                                           Person.class );

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT,
                                    Boolean.TRUE );
            marshaller.marshal( object,
                                stringWriter );
            return stringWriter.toString();
        } catch ( JAXBException e ) {
            System.err.println( String.format( "Exception while marshalling: %s",
                                               e.getMessage() ) );
        }
        return null;
    }

    public static <T> T convertXMLToObject( String xml,
                                            Class... clazz ) {
        try {
            JAXBContext context = JAXBContext.newInstance( clazz );
            Unmarshaller um = context.createUnmarshaller();
            return (T) um.unmarshal( new StringReader( xml ) );
        } catch ( JAXBException je ) {
            throw new RuntimeException( "Error interpreting XML response",
                                        je );
        }
    }

    public static ReleaseId createKJarWithMultipleResources( String id,
                                                             String[] resources,
                                                             ResourceType[] types ) {
        KieServices ks = KieServices.Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();
        KieFileSystem kfs = ks.newKieFileSystem();

        for ( int i = 0; i < resources.length; i++ ) {
            String res = resources[i];
            String type = types[i].getDefaultExtension();

            kfs.write( "src/main/resources/" + id.replaceAll( "\\.",
                                                              "/" ) + "/org/test/res" + i + "." + type,
                       res );
        }

        KieBaseModel kBase1 = kproj.newKieBaseModel( id )
                                   .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
                                   .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kBase1.newKieSessionModel( id + ".KSession1" )
                                          .setDefault( true )
                                          .setType( KieSessionModel.KieSessionType.STATEFUL )
                                          .setClockType( ClockTypeOption.get( "pseudo" ) );

        kfs.writeKModuleXML( kproj.toXML() );

        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();

        KieModule kieModule = kieBuilder.getKieModule();
        return kieModule.getReleaseId();
    }
    
    public static void main( String args[] ) {

        String header = "package org.drools.compiler\n" + "import org.kie.simulation.jaxb.Person\n" + "import org.kie.simulation.jaxb.Address\n";

        String drl1 = "global Person person;" + "\n" + "global String outS;" + "\n" + "global Long timeNow;" + "\n" + "rule R1 when\n" + "   p : Person()\n" + "then\n" + "    kcontext.getKnowledgeRuntime().setGlobal(\"person\", p);\n" + "    kcontext.getKnowledgeRuntime().setGlobal(\"timeNow\", kcontext.getKnowledgeRuntime().getSessionClock().getCurrentTime() );\n" + "end\n";

        ReleaseId releaseId = createKJarWithMultipleResources( "org.kie",
                                                               new String[]{header + drl1},
                                                               new ResourceType[]{ResourceType.DRL} );

        ExecutableBuilderImpl f = new ExecutableBuilderImpl();
        PseudoClockRunner runner = new PseudoClockRunner();

        Batch batch = new BatchImpl( 10L );

        Person p = new Person();
        p.setName( "Simmy Sim" );

        Address[] adds = {new Address( "add1",
                                       "l2",
                                       "l3" ), new Address( "add2",
                                                            "l2",
                                                            "l3" )};
        p.setAddressArray( adds );

        NewContextCommand<Object> context = new NewContextCommand<>( "app1" );
        NewKieSessionCommand newSession = new NewKieSessionCommand( releaseId );
        GetKieContainerCommand getKieContainer = new GetKieContainerCommand( releaseId );
        //  InsertObjectCommand insertString = new InsertObjectCommand( "h1" );
        InsertObjectCommand insertPerson = new InsertObjectCommand( p );
        FireAllRulesCommand fireAll = new FireAllRulesCommand();
        GetGlobalCommand getGlobal = new GetGlobalCommand( "outS" );
        OutCommand<Object> out = new OutCommand<>( "outS" );
        GetGlobalCommand getGlobalP = new GetGlobalCommand( "person" );
        OutCommand<Object> outP = new OutCommand<>( "person" );

        batch.addCommand( context )
             .addCommand( getKieContainer )
             .addCommand( newSession )
             .addCommand( insertPerson )
             .addCommand( fireAll )
             .addCommand( getGlobal )
             .addCommand( out )
             .addCommand( getGlobalP )
             .addCommand( outP );

        // Sample Scenario XML
        String batchXml = convertObjectToXML( batch );

        System.out.println( batchXml );

        // Mock reading of XML scenario file
        Batch umBatch = convertXMLToObject( batchXml,
                                            BatchImpl.class,
                                            Person.class );

        umBatch.getCommands().stream().forEach( e -> System.out.println( e.toString() ) );

        // Process commands and execute
        umBatch.getCommands().stream().forEach( e -> f.addCommand( e ) );
        RequestContext requestContext = runner.execute( f.getExecutable() );

        Object person = requestContext.get( "person" );

        System.out.println( person.toString() );
    }


}
