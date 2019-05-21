package com.rebelo.messgame.services;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

// See https://square.github.io/otto/ for usage
public class EventBus {

    private static EventBus _instance;
    Bus bus;

    public EventBus() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    // static method to create instance of Singleton class
    public static EventBus getInstance()
    {
        if (_instance == null)
            _instance = new EventBus();

        return _instance;
    }

    /**
     * Post an event
     * @param event
     */
    public void post(Object event) {
        bus.post(event);
    }

    /** Register a subscriber or producer. Producer will produce an initial value so subscribers are notified
     * instantly of the last known value
     * @param Subscriber / Produdcer
     */

    public void register(Object object) {
        bus.register(object);
    }
}
