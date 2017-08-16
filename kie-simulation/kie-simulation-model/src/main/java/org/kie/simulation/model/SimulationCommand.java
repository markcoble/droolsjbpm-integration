package org.kie.simulation.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.drools.core.command.GetKieContainerCommand;
import org.drools.core.command.NewKieSessionCommand;
import org.drools.core.command.OutCommand;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.drools.core.command.runtime.SetGlobalCommand;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.command.runtime.rule.InsertObjectCommand;
import org.drools.core.fluent.impl.NewContextCommand;
import org.drools.core.fluent.impl.SetCommand;
import org.drools.core.fluent.impl.SetVarAsRegistryEntry;
import org.kie.api.command.Command;

@XmlType(name = "simulation-command")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimulationCommand {

    @XmlElements({
                  @XmlElement(name = "new-application-context", type = NewContextCommand.class),
                  @XmlElement(name = "new-session", type = NewKieSessionCommand.class),
                  @XmlElement(name = "get-kie-container", type = GetKieContainerCommand.class),
                  @XmlElement(name = "get-global", type = GetGlobalCommand.class),
                  @XmlElement(name = "set-global", type = SetGlobalCommand.class),
                  @XmlElement(name = "insert", type = InsertObjectCommand.class),
                  @XmlElement(name = "insert-elements", type = InsertElementsCommand.class),
                  @XmlElement(name = "fire-all-rules", type = FireAllRulesCommand.class),
                  @XmlElement(name = "register-var", type = SetVarAsRegistryEntry.class),
                  @XmlElement(name = "set", type = SetCommand.class),
                  @XmlElement(name = "dispose", type = DisposeCommand.class),
                  @XmlElement(name = "out", type = OutCommand.class),

    })
    Command executeCommand;

    @XmlAttribute(required = true)
    long distance;

    public SimulationCommand() {

    }

    public SimulationCommand(Command command,
                             long distance) {
        this.executeCommand = command;
        this.distance = distance;
    }

    public Command command() {
        return this.executeCommand;
    }

    public long after() {
        return this.distance;
    }

}
