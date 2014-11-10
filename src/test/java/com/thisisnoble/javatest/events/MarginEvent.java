package com.thisisnoble.javatest.events;

import com.thisisnoble.javatest.impl.CompositeEvent;
import com.thisisnoble.javatest.interfaces.Event;
import com.thisisnoble.javatest.interfaces.IMarginEvent;

public class MarginEvent implements IMarginEvent {

    private final String id;
    private final String parentId;
    private final double margin;

    public MarginEvent(String id, String parentId, double margin) {
        this.id = id;
        this.parentId = parentId;
        this.margin = margin;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public double getMargin() {
        return margin;
    }

}
