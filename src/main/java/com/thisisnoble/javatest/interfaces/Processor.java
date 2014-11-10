package com.thisisnoble.javatest.interfaces;

import com.thisisnoble.javatest.interfaces.Event;

public interface Processor {

    boolean interestedIn(Event event);

	void process(Event event);
}
