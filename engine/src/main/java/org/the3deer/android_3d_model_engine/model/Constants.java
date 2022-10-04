package org.the3deer.android_3d_model_engine.model;

public class Constants {
    /**
     * Default unit factor for dimension on any axis
     */
    public static final float UNIT = 1 * 100f;
    /**
     * Perspective camera. Near clipping panel
     */
    public static final float near = UNIT / 100;
    /**
     * Perspective camera. Far clipping panel
     */
    public static final float far = UNIT * UNIT * UNIT;
    /**
     * Default camera position on Z axis
     */
    public static final float DEFAULT_CAMERA_POSITION = UNIT * 2;

    public static final float SKYBOX_SIZE = UNIT * 100;
    public static final float[] LIGHT_BULB_LOCATION = {0, 0, 0, 1};
    public static final float[] Z_NORMAL = {0, 0, 1};


    static final float ROOM_CENTER_SIZE = 0.01f;

    static final float ROOM_SIZE = SKYBOX_SIZE * 100;
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

    // grid
    public static final float GRID_WIDTH = 1f;
    public static final float GRID_SIZE = 0.1f;
    public static final float[] GRID_COLOR = {0.25f, 0.25f, 0.25f, 0.5f};

    public static final float[] COLOR_RED = {1,0,0,1};
    public static final float[] COLOR_RED_TRANSLUCENT = {1,0,0,0.25f};
    public static final float[] COLOR_GREEN = {0,1,0,1};
    public static final float[] COLOR_GREEN_TRANSLUCENT = {0,1,0,0.25f};
    public static final float[] COLOR_BLUE = {0,0,1,1f};
    public static final float[] COLOR_BLUE_TRANSLUCENT = {0,0,1,0.25f};

    public static final float[] COLOR_WHITE = {1, 1, 1, 1};
    public static final float[] COLOR_BLACK = {0, 0, 0, 1};
    public static final float[] COLOR_GRAY = {0.5f, 0.5f, 0.5f, 1f};
    public static final float[] COLOR_HALF_TRANSPARENT = {1f, 1f, 1f, 0.5f};
    public static final float[] COLOR_ALMOST_TRANSPARENT = {1f, 1f, 1f, 0.1f};
    public static final float[] COLOR_BIT_TRANSPARENT = {1f, 1f, 1f, 0.9f};
    // stereoscopic variables
    public static float EYE_DISTANCE = 6.4f;
}
