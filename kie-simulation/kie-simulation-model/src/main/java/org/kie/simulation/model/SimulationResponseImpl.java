package org.kie.simulation.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "simulation-response")
@XmlAccessorType(XmlAccessType.NONE)
public class SimulationResponseImpl implements SimulationResponse {


    @XmlAnyElement(lax=true)
    private Object object;

    @Override
    public void setResult(Object result) {
        this.object = result;
    }

    @Override
    public Object getResult() {
        return this.object;
    }

}
