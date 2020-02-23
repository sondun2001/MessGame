package com.rebelo.messgame.services;

// See https://square.github.io/otto/ for usage
// TODO: Replace with RXJava https://github.com/ReactiveX/RxJava
public class EventBus {

    private static EventBus _instance;
    //Bus bus;

    public EventBus() {
        //bus = new Bus(ThreadEnforcer.ANY);
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
        //bus.post(event);
    }

    /** Register a subscriber or producer. Producer will produce an initial value so subscribers are notified
     * instantly of the last known value
     * @param
     */

    public void register(Object object) {
       // bus.register(object);
    }
}
