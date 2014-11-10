package com.thisisnoble.javatest.events;

import com.thisisnoble.javatest.interfaces.Event;
import com.thisisnoble.javatest.interfaces.IRiskEvent;

public class RiskEvent implements IRiskEvent {

    private final String id;
    private final String parentId;
    private final double riskValue;

    public RiskEvent(String id, String parentId, double riskValue) {
        this.id = id;
        this.parentId = parentId;
        this.riskValue = riskValue;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public double getRiskValue() {
        return riskValue;
    }
}
