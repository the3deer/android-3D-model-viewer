package org.the3deer.android_3d_model_engine.view;

import java.util.EventObject;

public class FPSEvent extends EventObject {

    private final int fps;

    public FPSEvent(Object source, int fps) {
        super(source);
        this.fps = fps;
    }

    public int getFps() {
        return fps;
    }
}
