package org.the3deer.android_3d_model_engine.view;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import org.the3deer.android_3d_model_engine.animation.Animator;
import org.the3deer.android_3d_model_engine.drawer.Renderer;
import org.the3deer.android_3d_model_engine.drawer.RendererFactory;
import org.the3deer.android_3d_model_engine.model.AnimatedModel;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.android_3d_model_engine.model.Material;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.model.Projection;
import org.the3deer.android_3d_model_engine.objects.Axis;
import org.the3deer.android_3d_model_engine.objects.BoundingBox;
import org.the3deer.android_3d_model_engine.objects.Grid;
import org.the3deer.android_3d_model_engine.objects.Line;
import org.the3deer.android_3d_model_engine.objects.Normals;
import org.the3deer.android_3d_model_engine.objects.Plane2;
import org.the3deer.android_3d_model_engine.objects.Skeleton;
import org.the3deer.android_3d_model_engine.objects.SkyBox;
import org.the3deer.android_3d_model_engine.objects.Wireframe;
import org.the3deer.android_3d_model_engine.services.SceneLoader;
import org.the3deer.android_3d_model_engine.shadow.ShadowsRenderer;
import org.the3deer.android_3d_model_engine.util.Rescaler;
import org.the3deer.util.android.AndroidUtils;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.android.GLUtil;
import org.the3deer.util.event.EventListener;
import org.the3deer.util.math.Quaternion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelRenderer implements GLSurfaceView.Renderer {

    private final static String TAG = ModelRenderer.class.getSimpleName();

    // blending
    private static final float[] BLENDING_MASK_DEFAULT = {1.0f, 1.0f, 1.0f, 1.0f};
    // Add 0.5f to the alpha component to the global shader so we can see through the skin
    private static final float[] BLENDING_MASK_FORCED = {1.0f, 1.0f, 1.0f, 0.5f};

    private final float[] backgroundColor;
    private final SceneLoader scene;

    private final List<EventListener> listeners = new ArrayList<>();

    // 3D window (parent component)
    private GLSurfaceView main;
    // width of the screen
    private int width;
    // height of the screen
    private int height;
    private float ratio;
    // zoom
    private float zoom = 1f;

    /**
     * Drawer factory to get right renderer/shader based on object attributes
     */
    private final RendererFactory drawer;

    // frames per second
    private long framesPerSecondTime = -1;
    private int framesPerSecond = 0;
    private int framesPerSecondCounter = 0;


    // The wireframe associated shape (it should be made of lines only)
    private Map<Object3DData, Object3DData> wireframes = new HashMap<>();
    // The loaded textures
    private Map<Material, Integer> textures = new HashMap<>();
    // The corresponding opengl bounding boxes and drawer
    private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<>();
    // The corresponding opengl bounding boxes
    private Map<Object3DData, Object3DData> normals = new HashMap<>();

    // skeleton
    private Map<Object3DData, Object3DData> skeleton = new HashMap<>();
    private boolean debugSkeleton = false;


    // 3D matrices to project our 3D world
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    {
        Matrix.setIdentityM(viewMatrix,0);
        Matrix.setIdentityM(projectionMatrix,0);
    }

    // light
    private final float[] tempVector4 = new float[4];
    private final float[] lightPosInWorldSpace = new float[4];
    private final float[] cameraPosInWorldSpace = new float[4];
    // private final float[] lightPosition = new float[]{0, 0, 0, 1};

    // Decoration
    private final List<Object3DData> extras = new ArrayList<>();
    private final Object3DData axis = Axis.build().setId("axis").setSolid(false)
            .setScale(Constants.UNIT,Constants.UNIT,Constants.UNIT);
    private final Object3DData gridx = Grid.build(-Constants.GRID_WIDTH, 0f, -Constants.GRID_WIDTH, Constants.GRID_WIDTH, 0f, Constants.GRID_WIDTH, Constants.GRID_SIZE)
            .setColor(Constants.COLOR_RED_TRANSLUCENT).setId("grid-x").setSolid(false)
            .setScale(Constants.UNIT,Constants.UNIT,Constants.UNIT);
    private final Object3DData gridy = Grid.build(-Constants.GRID_WIDTH, -Constants.GRID_WIDTH, 0f, Constants.GRID_WIDTH, Constants.GRID_WIDTH, 0f, Constants.GRID_SIZE)
            .setColor(Constants.COLOR_GREEN_TRANSLUCENT).setId("grid-y").setSolid(false)
            .setScale(Constants.UNIT,Constants.UNIT,Constants.UNIT);
    private final Object3DData gridz = Grid.build(0, -Constants.GRID_WIDTH, -Constants.GRID_WIDTH, 0, Constants.GRID_WIDTH, Constants.GRID_WIDTH, Constants.GRID_SIZE)
            .setColor(Constants.COLOR_BLUE_TRANSLUCENT).setId("grid-z").setSolid(false)
            .setScale(Constants.UNIT,Constants.UNIT,Constants.UNIT);

    {
        extras.add(axis);
        extras.add(gridx);
        extras.add(gridy);
        extras.add(gridz);
    }

    // 3D stereoscopic matrix (left & right camera)
    private final float[] viewMatrixLeft = new float[16];
    private final float[] projectionMatrixLeft = new float[16];
    private final float[] viewProjectionMatrixLeft = new float[16];
    private final float[] viewMatrixRight = new float[16];
    private final float[] projectionMatrixRight = new float[16];
    private final float[] viewProjectionMatrixRight = new float[16];

    // settings
    private boolean lightsEnabled = true;
    private boolean wireframeEnabled = false;
    private boolean texturesEnabled = true;
    private boolean colorsEnabled = true;
    private boolean animationEnabled = true;

    // skybox
    private boolean isDrawSkyBox = true;
    private int isUseskyBoxId = 0;
    private final float[] projectionMatrixSkyBox = new float[16];
    private final float[] viewMatrixSkyBox = new float[16];
    private SkyBox[] skyBoxes = null;
    private Object3DData[] skyBoxes3D = null;
    private Quaternion orientation = new Quaternion(0,0,0,1);

    /**
     * Whether the info of the model has been written to console log
     */
    private Map<String, Boolean> infoLogged = new HashMap<>();
    /**
     * Switch to akternate drawing of right and left image
     */
    private boolean anaglyphSwitch = false;
    /**
     * Switch to alternate projection
     */
    private Projection projection = Projection.PERSPECTIVE;

    /**
     * Skeleton Animator
     */
    private Animator animator = new Animator();
    /**
     * Did the application explode?
     */
    private boolean fatalException = false;

    // shadowing
    private boolean doShadowing = false;
    private ShadowsRenderer shadowsRenderer;
    final Object3DData plane2 = Plane2.build();
    final Object3DData plane3 = Plane2.build();
    private final float[] lightViewMatrix = new float[16];
    /**
     * Construct a new renderer for the specified surface view
     *
     * @param modelSurfaceView the 3D window
     */
    public ModelRenderer(Activity parent, ModelSurfaceView modelSurfaceView,
                         float[] backgroundColor, SceneLoader scene) throws IOException, IllegalAccessException {
        this.main = modelSurfaceView;
        this.backgroundColor = backgroundColor;
        this.scene = scene;
        this.drawer = new RendererFactory(parent);
        if (doShadowing) {
            this.shadowsRenderer = new ShadowsRenderer(parent);
        }
    }

    public ModelRenderer addListener(EventListener listener) {
        this.listeners.add(listener);
        return this;
    }

    public float getNear() {
        return Constants.near;
    }

    public float getFar() {
        return Constants.far;
    }

    public void toggleProjection() {
        switch (this.projection) {
            case PERSPECTIVE:
                setProjection(Projection.ISOMETRIC);
                break;
            case ISOMETRIC:
                setProjection(Projection.ORTHOGRAPHIC);
                break;
            case ORTHOGRAPHIC:
                setProjection(Projection.FREE);
                break;
            case FREE:
                setProjection(Projection.PERSPECTIVE);
                break;
        }
    }

    public Projection getProjection(){
        return this.projection;
    }

    public void setProjection(Projection projection){
        Log.d(TAG, "setProjection: projection: [" + projection + "]");
        this.projection = projection;

        // fire event
        final ViewEvent eventObject = new ViewEvent(this, ViewEvent.Code.PROJECTION_CHANGED, width, height);
        eventObject.setProjection(this.projection);
        AndroidUtils.fireEvent(listeners, eventObject);
    }

    public void refreshMatrices(){

        if (ratio == 0) return;

        // initialize skybox
        //Matrix.frustumM(projectionMatrixSkyBox, 0, -ratio, ratio, -1, 1, 2f, getFar());
        Matrix.frustumM(projectionMatrixSkyBox, 0, -ratio, ratio, -1, 1, getNear(), getFar());

        switch (getProjection()){
            case ORTHOGRAPHIC:
            case ISOMETRIC:
                Matrix.orthoM(projectionMatrix, 0, -ratio * Constants.UNIT /getZoom(), ratio* Constants.UNIT /getZoom(), -Constants.UNIT /getZoom(), Constants.UNIT /getZoom(), getNear(), getFar());
                //Matrix.orthoM(projectionMatrixSkyBox, 0, -ratio * Constants.UNIT /getZoom(), ratio* Constants.UNIT /getZoom(), -Constants.UNIT /getZoom(), Constants.UNIT /getZoom(), getNear(), getFar());
                Matrix.frustumM(projectionMatrixSkyBox, 0, -ratio * getNear(), ratio * getNear(), -1 * getNear(), 1 * getNear(), getNear(),  getFar());
                Matrix.orthoM(projectionMatrixRight, 0, -ratio * Constants.UNIT /getZoom(), ratio* Constants.UNIT /getZoom(), -Constants.UNIT /getZoom(), Constants.UNIT /getZoom(), getNear(), getFar());
                Matrix.orthoM(projectionMatrixLeft, 0, -ratio * Constants.UNIT /getZoom(), ratio* Constants.UNIT /getZoom(), -Constants.UNIT /getZoom(), Constants.UNIT /getZoom(), getNear(), getFar());
                break;
            default:
                // Each individual eye has a horizontal FOV of about 135 degrees
                // and a vertical FOV of just over 180 degrees.
                Matrix.frustumM(projectionMatrix, 0, -ratio * getNear(), ratio * getNear(), -1 * getNear(), 1 * getNear(), getNear(),  getFar());
                Matrix.frustumM(projectionMatrixSkyBox, 0, -ratio * getNear(), ratio * getNear(), -1 * getNear(), 1 * getNear(), getNear(),  getFar());
                Matrix.frustumM(projectionMatrixRight, 0, -ratio * getNear(), ratio * getNear(), -1 * getNear(), 1 * getNear(), getNear(),  getFar());
                Matrix.frustumM(projectionMatrixLeft, 0, -ratio * getNear(), ratio * getNear(), -1 * getNear(), 1 * getNear(), getNear(),  getFar());
                break;
        }
    }

    public void toggleLights() {
        lightsEnabled = !lightsEnabled;
    }

    public void setSkyBoxId(int skyBoxId) {
        isUseskyBoxId = skyBoxId;
    }

    public int getSkyBoxId(){
        return isUseskyBoxId;
    }

    public void toggleSkyBox() {
        isUseskyBoxId++;
        if (isUseskyBoxId > 1) {
            isUseskyBoxId = -3;
        }
        Log.i("ModelRenderer", "Toggled skybox. Idx: " + isUseskyBoxId);
    }

    public boolean isLightsEnabled() {
        return lightsEnabled;
    }

    public void toggleWireframe() {
        this.wireframeEnabled = !wireframeEnabled;
    }

    public void toggleTextures() {
        this.texturesEnabled = !texturesEnabled;
    }

    public void toggleColors() {
        this.colorsEnabled = !colorsEnabled;
    }

    public void toggleAnimation() {
        this.animationEnabled = !animationEnabled;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // log event
        Log.d(TAG, "onSurfaceCreated. config: " + config);

        // Set the background frame color
        GLES20.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);

        // Use culling to remove back faces.
        // Don't remove back faces so we can see them
        // GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing for hidden-surface elimination.
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Enable not drawing out of view port
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

        AndroidUtils.fireEvent(listeners, new ViewEvent(this, ViewEvent.Code.SURFACE_CREATED, 0, 0));

        // init variables having android context
        ContentUtils.setThreadActivity(main.getContext());
        skyBoxes = new SkyBox[]{SkyBox.getSkyBox1(), SkyBox.getSkyBox2()};
        skyBoxes3D = new Object3DData[skyBoxes.length];
        // setup shadow rendering
        if (doShadowing) {
            shadowsRenderer.onSurfaceCreated(unused, config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        this.width = width;
        this.height = height;

        // Adjust the viewport based on geometry changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        // the projection matrix is the 3D virtual space (cube) that we want to project
        this.ratio = (float) width / height;

        // initialize projection
        refreshMatrices();

        // fire event
        AndroidUtils.fireEvent(listeners, new ViewEvent(this, ViewEvent.Code.SURFACE_CHANGED, width, height));
        // setup shadow rendering
        if (doShadowing) {
            shadowsRenderer.onSurfaceChanged(unused, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if (fatalException) {
            return;
        }
        try {

            GLES20.glViewport(0, 0, width, height);
            GLES20.glScissor(0, 0, width, height);

            // Draw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glColorMask(true, true, true, true);
            GLES20.glLineWidth((float) Math.PI);

            if (scene == null) {
                // scene not ready
                return;
            }

            // shadows
            if (doShadowing && scene.getObjects().size() > 0 && !scene.getObjects().contains(plane2)) {
                scene.getLightBulb().setLocation(new float[]{25f, 200f, 0f});
                plane2.setColor(Constants.COLOR_GRAY);
                plane2.setLocation(new float[]{0f, -50f, 0f});
                plane2.setPinned(true);
                scene.addObject(plane2);
            }

            float[] colorMask = BLENDING_MASK_DEFAULT;
            if (scene.isBlendingEnabled()) {
                // Enable blending for combining colors when there is transparency
                GLES20.glEnable(GLES20.GL_BLEND);
                GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
                if (scene.isBlendingForced()) {
                    colorMask = BLENDING_MASK_FORCED;
                }
            } else {
                GLES20.glDisable(GLES20.GL_BLEND);
            }

            // refresh matrices
            refreshMatrices();

            // animate scene
            scene.onDrawFrame();

            // recalculate mvp matrix according to where we are looking at now
            Camera camera = scene.getCamera();
            cameraPosInWorldSpace[0] = camera.getxPos();
            cameraPosInWorldSpace[1] = camera.getyPos();
            cameraPosInWorldSpace[2] = camera.getzPos();
            if (camera.hasChanged()) {

                // INFO: Set the camera position (View matrix)
                // The camera has 3 vectors (the position, the vector where we are looking at, and the up position (sky)

                // the projection matrix is the 3D virtual space (cube) that we want to project
                float ratio = (float) width / height;
                // Log.v(TAG, "Camera changed: projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10], ");

                Matrix.setLookAtM(viewMatrix, 0, camera.getxPos(), camera.getyPos(), camera.getzPos(), camera.getxView(), camera.getyView(),
                        camera.getzView(), camera.getxUp(), camera.getyUp(), camera.getzUp());
                Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

                if (scene.isStereoscopic()) {
                    Camera[] stereoCamera = camera.toStereo(Constants.EYE_DISTANCE);
                    Camera leftCamera = stereoCamera[0];
                    Camera rightCamera = stereoCamera[1];

                    // camera on the left for the left eye
                    Matrix.setLookAtM(viewMatrixLeft, 0, leftCamera.getxPos(), leftCamera.getyPos(), leftCamera.getzPos(), leftCamera
                                    .getxView(),
                            leftCamera.getyView(), leftCamera.getzView(), leftCamera.getxUp(), leftCamera.getyUp(), leftCamera.getzUp());
                    // camera on the right for the right eye
                    Matrix.setLookAtM(viewMatrixRight, 0, rightCamera.getxPos(), rightCamera.getyPos(), rightCamera.getzPos(), rightCamera
                                    .getxView(),
                            rightCamera.getyView(), rightCamera.getzView(), rightCamera.getxUp(), rightCamera.getyUp(), rightCamera.getzUp());
                }

                //camera.setChanged(false);
            }

            final Renderer basicShader = drawer.getBasicShader();

            // calculate light position
            Matrix.multiplyMV(tempVector4, 0, scene.getLightBulb().getModelMatrix(), 0,
                    Constants.LIGHT_BULB_LOCATION, 0);
            lightPosInWorldSpace[0] = tempVector4[0];
            lightPosInWorldSpace[1] = tempVector4[1];
            lightPosInWorldSpace[2] = tempVector4[2];

            // Calculate position of the light in world space to support lighting
            if (!scene.isRotatingLight()) {
                lightPosInWorldSpace[0] = cameraPosInWorldSpace[0];
                lightPosInWorldSpace[1] = cameraPosInWorldSpace[1];
                lightPosInWorldSpace[2] = cameraPosInWorldSpace[2];
            }

            // render shadow
            if (doShadowing){
                shadowsRenderer.onPrepareFrame(unused, projectionMatrix, viewMatrix, lightPosInWorldSpace, scene);
            }

            drawSkyBox(viewMatrix, projectionMatrix, cameraPosInWorldSpace, colorMask);


            if (scene.isDrawLighting()) {
                if (scene.isRotatingLight()) {
                    // Draw a point that represents the light bulb
                    basicShader.draw(scene.getLightBulb(), projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, scene.getLightBulb().getDrawMode(), scene.getLightBulb().getDrawSize());
                    //basicShader.draw(Point.build(lightPosInWorldSpace), projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                }
            }

            // render with shadows
            if (doShadowing && scene.getObjects().size() > 0) {
                // shadowsRenderer.onPrepareFrame(unused, projectionMatrix, viewMatrix, lightPosInWorldSpace, scene);
                shadowsRenderer.onDrawFrame(unused, projectionMatrix, viewMatrix, lightPosInWorldSpace, scene);
                return;
            }

            // render
            if (!scene.isStereoscopic()) {
                this.onDrawFrame(viewMatrix, projectionMatrix, colorMask);
                if(camera.hasChanged()) camera.setChanged(false);
                return;
            }


            if (scene.isAnaglyph()) {
                // INFO: switch because blending algorithm doesn't mix colors
                int correction = -0;
                if (anaglyphSwitch) {

                   GLES20.glColorMask(false, true, true, true);
                    GLES20.glViewport(-correction, 0, width-correction, height);
                    this.onDrawFrame(viewMatrixRight, projectionMatrixRight,
                            colorMask);

                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
                    GLES20.glColorMask(true, false, false, true);
                    GLES20.glViewport(correction, 0, width+correction, height);
                    this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft,
                            colorMask);

                } else {

                    GLES20.glColorMask(true, false, false, true);
                    GLES20.glViewport(correction, 0, width+correction, height);
                    this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft,
                            colorMask);

                    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
                    GLES20.glColorMask(false, true, true, true);
                    GLES20.glViewport(-correction, 0, width-correction, height);
                    this.onDrawFrame(viewMatrixRight, projectionMatrixRight,
                            colorMask);
                }
                anaglyphSwitch = !anaglyphSwitch;
                return;
            }

            if (scene.isVRGlasses()) {

                // draw left eye image
                GLES20.glViewport(0, 0, width / 2, height);
                GLES20.glScissor(0, 0, width / 2, height);
                this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft,
                        null);

                // draw right eye image
                GLES20.glViewport(width / 2, 0, width / 2, height);
                GLES20.glScissor(width / 2, 0, width / 2, height);
                this.onDrawFrame(viewMatrixRight, projectionMatrixRight,
                        null);
            }


        } catch (Exception ex) {
            Log.e("ModelRenderer", "Fatal exception: " + ex.getMessage(), ex);
            fatalException = true;
        } catch (Error err) {
            Log.e("ModelRenderer", "Fatal error: " + err.getMessage(), err);
            fatalException = true;
        } finally {
            if (framesPerSecondTime == -1) {
                framesPerSecondTime = SystemClock.elapsedRealtime();
                framesPerSecondCounter++;
            } else if (SystemClock.elapsedRealtime() > framesPerSecondTime + 1000) {
                framesPerSecond = framesPerSecondCounter;
                framesPerSecondCounter = 1;
                framesPerSecondTime = SystemClock.elapsedRealtime();
                AndroidUtils.fireEvent(listeners, new FPSEvent(this, framesPerSecond));
            } else {
                framesPerSecondCounter++;
            }
        }
    }

    private void onDrawFrame(float[] viewMatrix, float[] projectionMatrix,
                             float[] colorMask) {

        // draw light
        boolean doAnimation = scene.isDoAnimation() && animationEnabled;
        boolean drawLighting = scene.isDrawLighting() && isLightsEnabled();
        boolean drawWireframe = scene.isDrawWireframe() || wireframeEnabled;
        boolean drawTextures = scene.isDrawTextures() && texturesEnabled;
        boolean drawColors = scene.isDrawColors() && colorsEnabled;

        if (drawLighting) {

            Renderer basicShader = drawer.getBasicShader();

            /*// Calculate position of the light in world space to support lighting
            if (scene.isRotatingLight()) {
                Matrix.multiplyMV(tempVector4, 0, scene.getLightBulb().getModelMatrix(), 0,
                        Constants.LIGHT_BULB_LOCATION, 0);
                lightPosInWorldSpace[0] = tempVector4[0];
                lightPosInWorldSpace[1] = tempVector4[1];
                lightPosInWorldSpace[2] = tempVector4[2];

                // Draw a point that represents the light bulb
                basicShader.draw(scene.getLightBulb(), projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace,scene.getLightBulb().getDrawMode(), scene.getLightBulb().getDrawSize());
                //basicShader.draw(Point.build(lightPosInWorldSpace), projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
            } else {
                lightPosInWorldSpace[0] = cameraPosInWorldSpace[0];
                lightPosInWorldSpace[1] = cameraPosInWorldSpace[1];
                lightPosInWorldSpace[2] = cameraPosInWorldSpace[2];
            }*/

            // FIXME: memory leak
            if (scene.isDrawNormals()) {
                Object3DData light_line = Line.build(new float[]{lightPosInWorldSpace[0],
                        lightPosInWorldSpace[1], lightPosInWorldSpace[2], 0, 0, 0}).setId("light_line");
                basicShader.draw(light_line, projectionMatrix,
                        viewMatrix, -1,
                        lightPosInWorldSpace,
                        colorMask, cameraPosInWorldSpace, light_line.getDrawMode(), light_line.getDrawSize());
            }
        }

        // draw all available objects
        List<Object3DData> objects = scene.getObjects();
        for (int i = 0; i < objects.size(); i++) {
            drawObject(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, doAnimation, drawLighting, drawWireframe, drawTextures, drawColors, objects, i);
        }

        // draw all GUI objects
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        List<Object3DData> guiObjects = scene.getGUIObjects();
        for (int i = 0; i < guiObjects.size(); i++) {
            drawObject(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, doAnimation, drawLighting, drawWireframe, drawTextures, drawColors, guiObjects, i);
        }

        debugSkeleton = !debugSkeleton;
    }

    private void drawSkyBox(float[] viewMatrix, float[] projectionMatrix, float[] cameraPosInWorldSpace, float[] colorMask) {

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // draw environment
        int skyBoxId = getSkyBoxId();
        if (skyBoxId == -3){
            GLES20.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);
            // draw all extra objects
            for (int i = 0; i < extras.size(); i++) {
                drawObject(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, false, false, false, false, true, extras, i);
            }
        }
        else if (skyBoxId == -2){
            GLES20.glClearColor(backgroundColor[0], backgroundColor[1], backgroundColor[2], backgroundColor[3]);
            // Draw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        }
        else if (skyBoxId == -1){
            // invert background color
            GLES20.glClearColor(1-backgroundColor[0], 1-backgroundColor[1], 1-backgroundColor[2], 1-backgroundColor[3]);
            // Draw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        }
        else if (isDrawSkyBox && skyBoxId >= 0 && skyBoxId < skyBoxes3D.length) {
            // GLES20.glDepthMask(false);
            GLES20.glClearColor(0, 0, 0, 1);
            try {
                //skyBoxId = 1;
                // lazy building of the 3d object
                if (skyBoxes3D[skyBoxId] == null) {
                    Log.i("ModelRenderer", "Loading sky box textures to GPU... skybox: " + skyBoxId);
                    int textureId = GLUtil.loadCubeMap(skyBoxes[skyBoxId].getCubeMap());
                    Log.d("ModelRenderer", "Loaded textures to GPU... id: " + textureId);
                    if (textureId != -1) {
                        skyBoxes3D[skyBoxId] = SkyBox.build(skyBoxes[skyBoxId]);
                        Rescaler.rescale(skyBoxes3D[skyBoxId], 1f);
                        final float scale = Constants.SKYBOX_SIZE; //getFar()/skyBoxes3D[skyBoxId].getDimensions().getLargest()/20;
                        skyBoxes3D[skyBoxId].setScale(scale, scale, scale);
                        skyBoxes3D[skyBoxId].setColor(Constants.COLOR_BIT_TRANSPARENT);
                    } else {
                        Log.e("ModelRenderer", "Error loading sky box textures to GPU. ");
                        isDrawSkyBox = false;
                    }

                }
                Renderer basicShader = drawer.getSkyBoxDrawer();
                skyBoxes3D[skyBoxId].setColor(Constants.COLOR_BIT_TRANSPARENT);
                basicShader.draw(skyBoxes3D[skyBoxId], projectionMatrixSkyBox, viewMatrix, skyBoxes3D[skyBoxId].getMaterial().getTextureId(), null, null, cameraPosInWorldSpace, skyBoxes3D[skyBoxId].getDrawMode(), skyBoxes3D[skyBoxId].getDrawSize());

                // sensor stuff
                /*this.orientation.toRotationMatrix(viewMatrixSkyBox);
                float[] rot = new float[16];
                Matrix.setRotateM(rot,0,90,1,0,0);
                float[] mat = new float[16];
                Matrix.multiplyMM(mat,0,viewMatrixSkyBox,0, rot,0);
                Renderer basicShader = drawer.getSkyBoxDrawer();
                basicShader.draw(skyBoxes3D[skyBoxId], projectionMatrixSkyBox, mat, skyBoxes3D[skyBoxId].getMaterial().getTextureId(), null, cameraPosInWorldSpace);*/
            } catch (Throwable ex) {
                Log.e("ModelRenderer", "Error rendering sky box. " + ex.getMessage(), ex);
                isDrawSkyBox = false;
            }
            //GLES20.glDepthMask(true);
        }
    }

    private void drawObject(float[] viewMatrix, float[] projectionMatrix, float[] lightPosInWorldSpace, float[] colorMask, float[] cameraPosInWorldSpace, boolean doAnimation, boolean drawLighting, boolean drawWireframe, boolean drawTextures, boolean drawColors, List<Object3DData> objects, int i) {
        Object3DData objData = null;
        try {
            objData = objects.get(i);
            if (!objData.isVisible()) {
                return;
            }

            if (!infoLogged.containsKey(objData.getId())) {
                Log.i("ModelRenderer", "Drawing model: " + objData.getId() + ", " + objData.getClass().getSimpleName());
                infoLogged.put(objData.getId(), true);
            }


            Renderer drawerObject = drawer.getDrawer(objData, false, drawTextures, drawLighting, doAnimation, false, false);
            if (drawerObject == null) {
                if (!infoLogged.containsKey(objData.getId() + "drawer")) {
                    Log.e("ModelRenderer", "No drawer for " + objData.getId());
                    infoLogged.put(objData.getId() + "drawer", true);
                }
                return;
            }

            boolean changed = objData.isChanged();
            objData.setChanged(false);

            // load textures
            /*Integer textureId = null;
            if (drawTextures) {

                // TODO: move texture loading to Renderer
                if (objData.getElements() != null) {

                    for (int e = 0; e < objData.getElements().size(); e++) {

                        // element
                        Element element = objData.getElements().get(e);

                        // pre-conditions
                        if (element.getMaterial() == null) continue;

                        // load normal map
                        if (element.getMaterial().getNormalTextureId() == -1 &&
                                element.getMaterial().getNormalTexture() != null){

                            // log event
                            Log.i("ModelRenderer", "Binding normal map... " + element.getMaterial().getName());

                            // bind bitmap
                            int handler = GLUtil.loadTexture(element.getMaterial().getNormalTexture());

                            element.getMaterial().setNormalTextureId(handler);
                        }

                        // load normal map
                        if (element.getMaterial().getEmissiveTextureId() == -1 &&
                                element.getMaterial().getEmissiveTexture() != null){

                            // log event
                            Log.i("ModelRenderer", "Binding normal map... " + element.getMaterial().getName());

                            // bind bitmap
                            int handler = GLUtil.loadTexture(element.getMaterial().getEmissiveTexture());

                            element.getMaterial().setEmissiveTextureId(handler);
                        }

                        // check if the texture was already bound
                        textureId = textures.get(element.getMaterial());
                        if (textureId != null) continue;

                        if (element.getMaterial().getColorTexture() != null){

                            // log event
                            Log.i("ModelRenderer", "Binding material... " + element.getMaterial());

                            // bind bitmap
                            textureId = GLUtil.loadTexture(element.getMaterial().getColorTexture());
                        }
                        else if (element.getMaterial().getTextureData() != null){

                            // log event
                            Log.i("ModelRenderer", "Binding material... " + element.getMaterial());


                            // parse bitmap + bind bitmap
                            textureId = GLUtil.loadTexture(element.getMaterial().getTextureData());
                        } else {
                            continue;
                        }

                        // update material
                        element.getMaterial().setTextureId(textureId);

                        // cache texture
                        textures.put(element.getMaterial(), textureId);

                        // log event
                        Log.i("ModelRenderer", "Material bound... " + textureId);
                    }
                } else {
                    // DEPRECATED
                    textureId = textures.get(objData.getMaterial());
                    if (textureId == null && objData.getTextureData() != null) {
                        Log.i("ModelRenderer", "Loading texture for obj: '" + objData.getId() + "'... bytes: " + objData.getTextureData().length);
                        ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
                        textureId = GLUtil.loadTexture(textureIs);
                        textureIs.close();
                        objData.getMaterial().setTextureId(textureId);
                        textures.put(objData.getMaterial(), textureId);

                        Log.i("ModelRenderer", "Loaded texture OK. id: " + textureId);
                    }
                }
            }*/
            Integer textureId = -1;

            // draw points
            if (objData.getDrawMode() == GLES20.GL_POINTS) {
                Renderer basicDrawer = drawer.getBasicShader();
                basicDrawer.draw(objData, projectionMatrix, viewMatrix, GLES20.GL_POINTS, lightPosInWorldSpace, null, cameraPosInWorldSpace, objData.getDrawMode(), objData.getDrawSize());
            } else {

                // draw wireframe
                if (drawWireframe && objData.getDrawMode() != GLES20.GL_POINTS
                        && objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP
                        && objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
                    // Log.d("ModelRenderer","Drawing wireframe model...");
                    try {
                        // Only draw wireframes for objects having faces (triangles)
                        Object3DData wireframe = wireframes.get(objData);
                        if (wireframe == null || changed) {
                            Log.i("ModelRenderer", "Building wireframe model...");
                            wireframe = Wireframe.build(objData);
                            wireframes.put(objData, wireframe);
                            Log.i("ModelRenderer", "Wireframe build: " + wireframe);
                        }
                        animator.update(wireframe, scene.isShowBindPose());
                        drawerObject.draw(wireframe, projectionMatrix, viewMatrix, textureId, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, wireframe.getDrawMode(), wireframe.getDrawSize());
                        //objData.render(drawer, lightPosInWorldSpace, colorMask);
                    } catch (Error e) {
                        Log.e("ModelRenderer", e.getMessage(), e);
                    }
                }

                // draw points
                else if (scene.isDrawPoints()) {
                    drawerObject.draw(objData, projectionMatrix, viewMatrix
                            , textureId, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, GLES20.GL_POINTS, objData.getDrawSize());
                    objData.render(drawer, scene.getCamera(), lightPosInWorldSpace, colorMask);
                }

                // draw skeleton
                else if (scene.isDrawSkeleton() && objData instanceof AnimatedModel && ((AnimatedModel) objData)
                        .getAnimation() != null) {



                    // draw skeleton on top of it
                   // GLES20.glDisable(GLES20.GL_DEPTH_TEST);
                    Object3DData skeleton = this.skeleton.get(objData);
                    if (skeleton == null || changed) {
                        skeleton = Skeleton.build((AnimatedModel) objData);
                        this.skeleton.put(objData, skeleton);
                    }
                    final Renderer skeletonDrawer = drawer.getDrawer(skeleton, false, false, drawLighting, doAnimation, false, false);
                    skeletonDrawer.draw(skeleton, projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, skeleton.getDrawMode(), skeleton.getDrawSize());
                    //GLES20.glEnable(GLES20.GL_DEPTH_TEST);

                    // draw the original object a bit transparent
                    drawerObject.draw(objData, projectionMatrix, viewMatrix,  textureId, lightPosInWorldSpace, Constants.COLOR_HALF_TRANSPARENT, cameraPosInWorldSpace, objData.getDrawMode(), objData.getDrawSize());
                }

                // draw solids
                else {
                    if (!infoLogged.containsKey(objData.getId() + "render")) {
                        Log.i("ModelRenderer", "Rendering object... " + objData.getId());
                        Log.d("ModelRenderer", objData.toString());
                        Log.d("ModelRenderer", drawerObject.toString());
                        infoLogged.put(objData.getId() + "render", true);
                    }
                    drawerObject.draw(objData, projectionMatrix, viewMatrix,
                            textureId, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace,
                            objData.getDrawMode(), objData.getDrawSize());
                    objData.render(drawer, scene.getCamera(), lightPosInWorldSpace, colorMask);
                }
            }

            // Draw bounding box
            if (scene.isDrawBoundingBox() && objData.isSolid() || scene.getSelectedObject() == objData) {
                drawBoundingBox(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, objData, changed);
            }

            // Draw normals
            if (scene.isDrawNormals()) {
                Object3DData normalData = normals.get(objData);
                if (normalData == null || changed) {
                    normalData = Normals.build(objData);
                    if (normalData != null) {
                        normalData.setId(objData.getId() + "_normals");
                        // it can be null if object isnt made of triangles
                        normals.put(objData, normalData);
                    }
                }
                if (normalData != null) {
                    Renderer normalsDrawer = drawer.getDrawer(normalData, false, false, false, doAnimation, false, false);
                    animator.update(normalData, scene.isShowBindPose());
                    normalsDrawer.draw(normalData, projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask
                            , cameraPosInWorldSpace, normalData.getDrawMode(), normalData.getDrawSize());
                }
            }

        } catch (Exception ex) {
            if (!infoLogged.containsKey(ex.getMessage())) {
                Log.e("ModelRenderer", "There was a problem rendering the object '" + objData.getId() + "':" + ex.getMessage(), ex);
                infoLogged.put(ex.getMessage(), true);
            }
        } catch (Error ex) {
            Log.e("ModelRenderer", "There was a problem rendering the object '" + objData.getId() + "':" + ex.getMessage(), ex);
        }
    }

    private void drawBoundingBox(float[] viewMatrix, float[] projectionMatrix, float[] lightPosInWorldSpace, float[] colorMask, float[] cameraPosInWorldSpace, Object3DData objData, boolean changed) {
        Object3DData boundingBoxData = boundingBoxes.get(objData);
        if (boundingBoxData == null) {
            Log.i("ModelRenderer", "Building bounding box... id: " + objData.getId());
            boundingBoxData = BoundingBox.build(objData);
            boundingBoxData.setModelMatrix(objData.getModelMatrix());
            boundingBoxData.setReadOnly(true);
            boundingBoxes.put(objData, boundingBoxData);
            Log.i("ModelRenderer", "Bounding box: " + boundingBoxData);
        }
        boundingBoxData.setColor(Constants.COLOR_GRAY);
        if(scene.getSelectedObject() == objData){
            boundingBoxData.setColor(Constants.COLOR_WHITE);
        }
        Renderer boundingBoxDrawer = drawer.getBoundingBoxDrawer();
        boundingBoxDrawer.draw(boundingBoxData, projectionMatrix, viewMatrix, -1,
                lightPosInWorldSpace, colorMask, cameraPosInWorldSpace,
                boundingBoxData.getDrawMode(), boundingBoxData.getDrawSize());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

    public float[] getViewMatrix() {
        return viewMatrix;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        switch (projection){
            case ORTHOGRAPHIC:
            case ISOMETRIC:
                if (zoom > 0 && zoom < 10) {
                    this.zoom = zoom;
                    setProjection(this.projection);
                }
                break;
        }
    }

    public void addZoom(float zoom) {
        this.setZoom(getZoom() + zoom);
    }

    public Quaternion getOrientation() {
        return orientation;
    }

    public void setOrientation(Quaternion orientation) {
        this.orientation = orientation;
    }
}