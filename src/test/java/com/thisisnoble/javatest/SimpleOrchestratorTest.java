package com.thisisnoble.javatest;

import com.thisisnoble.javatest.events.MarginEvent;
import com.thisisnoble.javatest.events.RiskEvent;
import com.thisisnoble.javatest.events.ShippingEvent;
import com.thisisnoble.javatest.events.TradeEvent;
import com.thisisnoble.javatest.impl.CompositeEvent;
import com.thisisnoble.javatest.impl.EventOrchestrator;
import com.thisisnoble.javatest.interfaces.Event;
import com.thisisnoble.javatest.interfaces.Orchestrator;
import com.thisisnoble.javatest.processors.MarginProcessor;
import com.thisisnoble.javatest.processors.RiskProcessor;
import com.thisisnoble.javatest.processors.ShippingProcessor;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.thisisnoble.javatest.util.TestIdGenerator.tradeEventId;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class SimpleOrchestratorTest {

    @Test
    public void tradeEventShouldTriggerAllProcessors() {
        TestPublisher testPublisher = new TestPublisher();
        Orchestrator orchestrator = setupOrchestrator();
        orchestrator.setup(testPublisher);

        TradeEvent te = new TradeEvent(tradeEventId(), 1000.0);
        orchestrator.receive(te);
        safeSleep(100);
        CompositeEvent ce = (CompositeEvent) testPublisher.getLastEvent();
        assertEquals(te, ce.getParent());
        assertEquals(5, ce.size());
        RiskEvent re1 = ce.getChildById("tradeEvt-riskEvt");
        assertNotNull(re1);
        assertEquals(50.0, re1.getRiskValue(), 0.01);
        MarginEvent me1 = ce.getChildById("tradeEvt-marginEvt");
        assertNotNull(me1);
        assertEquals(10.0, me1.getMargin(), 0.01);
        ShippingEvent se1 = ce.getChildById("tradeEvt-shipEvt");
        assertNotNull(se1);
        assertEquals(200.0, se1.getShippingCost(), 0.01);
        RiskEvent re2 = ce.getChildById("tradeEvt-shipEvt-riskEvt");
        assertNotNull(re2);
        assertEquals(10.0, re2.getRiskValue(), 0.01);
        MarginEvent me2 = ce.getChildById("tradeEvt-shipEvt-marginEvt");
        assertNotNull(me2);
        assertEquals(2.0, me2.getMargin(), 0.01);
    }

    @Test
    // bug/typo fix somewhere in the test
    public void shippingEventShouldTriggerOnly2Processors() {
        TestPublisher testPublisher = new TestPublisher();
        Orchestrator orchestrator = setupOrchestrator();
        orchestrator.setup(testPublisher);

        ShippingEvent se = new ShippingEvent("ship2", 1000.0);
        orchestrator.receive(se);
        safeSleep(100);
        CompositeEvent ce = (CompositeEvent) testPublisher.getLastEvent();
        assertEquals(se, ce.getParent());
        assertEquals(2, ce.size());
        RiskEvent re2 = ce.getChildById("ship2-riskEvt");
        assertNotNull(re2);
        assertEquals(50.00, re2.getRiskValue(), 0.01);
        MarginEvent me2 = ce.getChildById("ship2-marginEvt");
        assertNotNull(me2);
        assertEquals(10.0, me2.getMargin(), 0.01);
    }

    @Test
    public void testMultipleEvents() {
        TestPublisher testPublisher = new TestPublisher();
        Orchestrator orchestrator = setupOrchestrator();
        orchestrator.setup(testPublisher);

        ShippingEvent shippingEvent = new ShippingEvent("ship2", 1000.00);

        final ExecutorService eventSourcerThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        eventSourcerThreadPool.submit(new EventSourcer(orchestrator, shippingEvent));
        safeSleep(100);

        CompositeEvent ce = (CompositeEvent) testPublisher.getLastEvent();
        assertEquals(shippingEvent, ce.getParent());
        assertEquals(2, ce.size());
        RiskEvent re2 = ce.getChildById("ship2-riskEvt");
        assertNotNull(re2);
        assertEquals(50.00, re2.getRiskValue(), 0.01);
        MarginEvent me2 = ce.getChildById("ship2-marginEvt");
        assertNotNull(me2);
        assertEquals(10.0, me2.getMargin(), 0.01);


        // these lines could be mingled with shipping event submission to have another test on thread safety
        TradeEvent tradeEvent = new TradeEvent(tradeEventId(), 1000.00);
        eventSourcerThreadPool.submit(new EventSourcer(orchestrator, tradeEvent));
        safeSleep(100);

        CompositeEvent ce2 = (CompositeEvent) testPublisher.getLastEvent();
        assertEquals(tradeEvent, ce2.getParent());
        assertEquals(5, ce2.size());
        RiskEvent re1 = ce2.getChildById("tradeEvt-riskEvt");
        assertNotNull(re1);
        assertEquals(50.0, re1.getRiskValue(), 0.01);
        MarginEvent me1 = ce2.getChildById("tradeEvt-marginEvt");
        assertNotNull(me1);
        assertEquals(10.0, me1.getMargin(), 0.01);
        ShippingEvent se1 = ce2.getChildById("tradeEvt-shipEvt");
        assertNotNull(se1);
        assertEquals(200.0, se1.getShippingCost(), 0.01);
        re2 = ce2.getChildById("tradeEvt-shipEvt-riskEvt");
        assertNotNull(re2);
        assertEquals(10.0, re2.getRiskValue(), 0.01);
        me2 = ce2.getChildById("tradeEvt-shipEvt-marginEvt");
        assertNotNull(me2);
        assertEquals(2.0, me2.getMargin(), 0.01);


        safeSleep(1000);

    }

    private Orchestrator setupOrchestrator() {
        Orchestrator orchestrator = createOrchestrator();
        orchestrator.register(new RiskProcessor(orchestrator));
        orchestrator.register(new MarginProcessor(orchestrator));
        orchestrator.register(new ShippingProcessor(orchestrator));
        return orchestrator;
    }

    private void safeSleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    private Orchestrator createOrchestrator() {
        return new EventOrchestrator();
    }

    /*
    Thought of playing with timing of receive calls on orchestrator and so embedding those calls inside runnable
     */
    private class EventSourcer implements Runnable {
        Orchestrator eventOrchestrator;
        Event event;


        public EventSourcer(Orchestrator eventOrchestrator, Event event) {
            this.event = event;
            this.eventOrchestrator = eventOrchestrator;
        }

        public void run() {
            eventOrchestrator.receive(event);
        }
    }
}
