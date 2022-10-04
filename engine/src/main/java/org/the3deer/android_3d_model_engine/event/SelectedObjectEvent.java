package org.the3deer.android_3d_model_engine.event;

import org.the3deer.android_3d_model_engine.model.Object3DData;

import java.util.EventObject;

public class SelectedObjectEvent extends EventObject {

    private final Object3DData selected;

    public SelectedObjectEvent(Object source, Object3DData selected) {
        super(source);
        this.selected = selected;
    }

    public Object3DData getSelected() {
        return selected;
    }
}
