package com.thisisnoble.javatest.impl;

import com.thisisnoble.javatest.interfaces.Event;
import com.thisisnoble.javatest.interfaces.Orchestrator;
import com.thisisnoble.javatest.interfaces.Processor;
import com.thisisnoble.javatest.interfaces.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chintanshah on 11/2/14.
 */
public class EventOrchestrator implements Orchestrator {

    private List<Processor> processors = new ArrayList<>();
    private List<Publisher> publishers = new ArrayList<>();
    private Map<String, CompositeEvent> compositeEvents = new HashMap<>();

    // mapping of id to parent id, used to find root id of a given id
    private Map<String, String> parents = new HashMap<>();

    @Override
    public void register(Processor processor) {
        synchronized (processors) {
            processors.add(processor);
        }
    }

    private String findRoot(String id) {
        if(id == null) {
            return null;
        } else if(parents.get(id) == null) {
            return id;
        }
        return findRoot(parents.get(id));
    }

    /*
    * Following KISS to ensure thread safety. Synchronization on the orchestrator itself
    * To make it more efficient, we could individualize synchronization blocks such as synchronized(processors) etc.
     */
    @Override
    public void receive(Event event) {
        synchronized (this) {
            for (Processor processor : processors) {
                processor.process(event);
            }
            parents.put(event.getId(), event.getParentId());
            if(event.getParentId() == null) {
                CompositeEvent compositeEvent = new CompositeEvent(event.getId(), event);
                compositeEvents.put(event.getId(), compositeEvent);
            } else {
                String rootEventId = findRoot(event.getId());
                compositeEvents.get(rootEventId).addChild(event);
            }
            for(Publisher publisher : publishers) {
                for(Map.Entry<String, CompositeEvent> entry : compositeEvents.entrySet()) {
                    publisher.publish(entry.getValue());
                }
            }
        }
    }

    @Override
    public void setup(Publisher publisher) {
        synchronized (publishers) {
            publishers.add(publisher);
        }
    }
}
