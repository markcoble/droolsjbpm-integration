package org.kie.simulation.model;

import java.util.List;

import org.kie.api.command.Command;

public interface SimulationRequest {

    SimulationRequest addCommand(Command command, long distance);

    List<SimulationCommand> getCommands();

    SimulationRequestImpl addCommand(Command command);
    
}
