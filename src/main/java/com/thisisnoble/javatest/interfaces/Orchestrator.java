package com.thisisnoble.javatest.interfaces;

public interface Orchestrator {

    void register(Processor processor);

    void receive(Event event);

    void setup(Publisher publisher);
}
