package org.kie.simulation.api;

import java.util.List;

import org.kie.simulation.model.SimulationRequest;
import org.kie.simulation.model.SimulationResponse;

public interface Simulation {

    SimulationResponse execute(SimulationRequest request);
    
}
