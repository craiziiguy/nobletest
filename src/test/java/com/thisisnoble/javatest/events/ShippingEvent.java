package com.thisisnoble.javatest.events;

import com.thisisnoble.javatest.interfaces.Event;
import com.thisisnoble.javatest.interfaces.IShippingEvent;

public class ShippingEvent implements IShippingEvent {

    private final String id;
    private final String parentId;
    private final double shippingCost;

    public ShippingEvent(String id, double shippingCost) {
        this(id, null, shippingCost);
    }

    public ShippingEvent(String id, String parentId, double shippingCost) {
        this.id = id;
        this.parentId = parentId;
        this.shippingCost = shippingCost;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public double getShippingCost() {
        return shippingCost;
    }
}
