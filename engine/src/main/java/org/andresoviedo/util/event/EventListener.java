package org.andresoviedo.util.event;

import java.util.EventObject;

public interface EventListener {
    boolean onEvent(EventObject event);
}
