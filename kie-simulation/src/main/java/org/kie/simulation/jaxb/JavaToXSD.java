package org.kie.simulation.jaxb;

import java.io.IOException;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.kie.simulation.fluent.ContextFluent;


public class JavaToXSD {
    @SuppressWarnings("restriction")
    public static void main(String[] args) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(ContextFluent.class);

        jc.generateSchema(new SchemaOutputResolver() {

            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                StreamResult result = new StreamResult(System.out);
                result.setSystemId(suggestedFileName);
                return result;
            }

        });
    }
}
