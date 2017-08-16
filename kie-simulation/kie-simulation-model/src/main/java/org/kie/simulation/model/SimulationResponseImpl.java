package org.kie.simulation.model;

public class SimulationResponseImpl implements SimulationResponse {

    Object result;

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getResult() {
        return this.result;
    }

}
