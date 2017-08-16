package org.kie.simulation.api;

import org.drools.core.fluent.impl.ExecutableBuilderImpl;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.builder.ExecutableBuilder;
import org.kie.simulation.model.SimulationRequest;
import org.kie.simulation.model.SimulationResponse;
import org.kie.simulation.model.SimulationResponseImpl;

public class SimulationImpl implements Simulation {

    @Override
    public SimulationResponse execute(SimulationRequest request) {

        ExecutableBuilderImpl f = (ExecutableBuilderImpl) ExecutableBuilder.create();

        // Process commands and execute
        request.getCommands().stream().forEach(sc -> f.addCommand(sc.command(),
                                                                  sc.after()));
        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        SimulationResponse response = createResponse(requestContext);

        return response;
    }

    private SimulationResponse createResponse(RequestContext requestContext) {
        SimulationResponse response = new SimulationResponseImpl();

        response.setResult(requestContext);
        return response;
    }

}
