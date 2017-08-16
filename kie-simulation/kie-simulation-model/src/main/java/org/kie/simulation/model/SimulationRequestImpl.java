package org.kie.simulation.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.command.Command;

@XmlRootElement(name = "simulation-request")
@XmlAccessorType(XmlAccessType.NONE)
public class SimulationRequestImpl implements SimulationRequest {

    @XmlElement(name = "scenario-command", required = true)
    List<SimulationCommand> commands;

    public SimulationRequestImpl() {}

    @Override
    public SimulationRequestImpl addCommand(Command command, long distance) {
        if (commands == null) {
            commands = new ArrayList<SimulationCommand>();
        }
        SimulationCommand simulationCommand = new SimulationCommand(command,
                                                                    distance);
        this.commands.add(simulationCommand);
        return this;
    }

    @Override
    public SimulationRequestImpl addCommand(Command command) {
        if (commands == null) {
            commands = new ArrayList<SimulationCommand>();
        }
        SimulationCommand simulationCommand = new SimulationCommand(command,
                                                                    0l);
        this.commands.add(simulationCommand);
        return this;
    }

    @Override
    public List<SimulationCommand> getCommands() {
        if (commands == null) {
            commands = new ArrayList<SimulationCommand>();
        }
        return commands;
    }

}
