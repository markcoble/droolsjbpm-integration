package org.kie.simulation.core.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.RequestContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OutCommand<T> implements ExecutableCommand<T> {

    private static final long serialVersionUID = 4661984339654958764L;
    @XmlAttribute(required = true)
    private String name;

    public OutCommand() {
    }

    public OutCommand( String name ) {
        this.name = name;
    }

    @Override
    public T execute( Context context ) {
        T returned = (T) ((RequestContext) context).getResult();

        String actualName;
        if ( this.name != null ) {
            actualName = this.name;
        } else {
            actualName = ((RequestContextImpl) context).getLastSet();
            if ( actualName == null ) {
                throw new RuntimeException( "Name was null and there was no last set name either" );
            }
        }

        context.set( actualName,
                     returned );

        return returned;
    }
}
