package org.kie.simulation.core.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class StartConversationCommand<Void> implements ExecutableCommand<Void> {
    public StartConversationCommand() {
    }

    @Override
    public Void execute(Context context) {
        RequestContextImpl         reqContext = (RequestContextImpl)context;
        ConversationContextManager cvnManager = reqContext.getConversationManager();
        cvnManager.startConversation(reqContext);

        return (Void) null;
    }
}
