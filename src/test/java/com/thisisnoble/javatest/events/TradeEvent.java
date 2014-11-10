package com.thisisnoble.javatest.events;

import com.thisisnoble.javatest.interfaces.Event;
import com.thisisnoble.javatest.interfaces.ITradeEvent;

public class TradeEvent implements ITradeEvent {

    private final String id;
    private final double notional;

    public TradeEvent(String id, double notional) {
        this.id = id;
        this.notional = notional;
    }

    public String getId() {
        return id;
    }

    public double getNotional() {
        return notional;
    }

    public String getParentId() {
        return null;
    }
}
