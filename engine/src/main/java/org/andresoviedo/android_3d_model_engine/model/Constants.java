package org.andresoviedo.android_3d_model_engine.model;

public class Constants {
    /**
     * The nominal frames per second
     */
    public static final int TOTAL_ANIMATION_FRAMES = 60;
    /**
     * If we need to approximate the vector to some discrete position,
     * then we use this threshold to test if should
     */
    public static final float SNAP_TO_GRID_THRESHOLD = 0.015f;
    public static final float UNIT_SIN_1 = (float)Math.sqrt(1f/3f); // 0.58f
    public static final float UNIT_SIN_2 = UNIT_SIN_1 + UNIT_SIN_1;
    public static final float UNIT_SIN_3 = UNIT_SIN_2 * UNIT_SIN_1;
    public static final float UNIT_SIN_5 = UNIT_SIN_3 * UNIT_SIN_2;
    public static final float UNIT_0 = 0f;
    public static final float UNIT_1 = 1f;
    public static final float UNIT_2 = UNIT_1 + UNIT_1;
    public static final float UNIT_3 = UNIT_2 + UNIT_1;
    public static final float UNIT_5 = UNIT_3 + UNIT_2;
    static final float ROOM_CENTER_SIZE = 0.01f;
    static final float ROOM_SIZE = 1000;
}
