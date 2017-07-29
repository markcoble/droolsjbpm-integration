/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.simulation.core.fluent;

import org.kie.api.command.Command;
import org.kie.simulation.core.command.GetKieContainerCommand;
import org.kie.simulation.core.command.NewKieSessionCommand;
import org.kie.simulation.core.command.OutCommand;
import org.kie.simulation.core.command.runtime.FireAllRulesCommand;
import org.kie.simulation.core.command.runtime.GetGlobalCommand;
import org.kie.simulation.core.command.runtime.InsertObjectCommand;
import org.kie.simulation.core.command.runtime.SetGlobalCommand;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "batch-fluent")
@XmlAccessorType(XmlAccessType.NONE)
public class BatchImpl implements Batch {

    @XmlAttribute(required = true)
    private final long distance;

    @XmlElements({
                  @XmlElement(name = "stack-item", type = BatchImpl.class),
                  @XmlElement(name = "new-application-context", type = NewContextCommand.class),
                  @XmlElement(name = "new-session", type = NewKieSessionCommand.class),
                  @XmlElement(name = "get-kie-container", type = GetKieContainerCommand.class),
                  @XmlElement(name = "get-global", type = GetGlobalCommand.class),
                  @XmlElement(name = "set-global", type = SetGlobalCommand.class),
                  @XmlElement(name = "insert", type = InsertObjectCommand.class),
                  @XmlElement(name = "fire-all-rules", type = FireAllRulesCommand.class),
                  @XmlElement(name = "out", type = OutCommand.class),

    })
    private List<Command> commands = new ArrayList<Command>();

    public BatchImpl() {
        this( 0L );
    }

    public BatchImpl( long distance ) {
        this.distance = distance;
    }

    public long getDistance() {
        return distance;
    }

    public BatchImpl addCommand( Command cmd ) {
        this.commands.add( cmd );
        return this;
    }

    @Override
    public List<Command> getCommands() {
        return commands;
    }
}
