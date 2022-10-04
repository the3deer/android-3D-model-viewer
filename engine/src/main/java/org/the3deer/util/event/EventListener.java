package org.the3deer.util.event;

import java.util.EventObject;

public interface EventListener {

    /**
     * Process the event notification on the System
     *
     * @param event the event
     * @return <code>true</code> if the event should be further processed by other listeners, false otherwise
     */
    boolean onEvent(EventObject event);
}
