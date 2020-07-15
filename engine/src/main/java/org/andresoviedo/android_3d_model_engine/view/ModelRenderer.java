package org.andresoviedo.android_3d_model_engine.view;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animator;
import org.andresoviedo.android_3d_model_engine.drawer.RendererFactory;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.drawer.Renderer;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.objects.Axis;
import org.andresoviedo.android_3d_model_engine.objects.BoundingBox;
import org.andresoviedo.android_3d_model_engine.objects.Grid;
import org.andresoviedo.android_3d_model_engine.objects.Line;
import org.andresoviedo.android_3d_model_engine.objects.Normals;
import org.andresoviedo.android_3d_model_engine.objects.Skeleton;
import org.andresoviedo.android_3d_model_engine.objects.Wireframe;
import org.andresoviedo.android_3d_model_engine.services.SceneLoader;
import org.andresoviedo.util.android.AndroidUtils;
import org.andresoviedo.util.android.GLUtil;
import org.andresoviedo.util.event.EventListener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ModelRenderer implements GLSurfaceView.Renderer {

    public static class ViewEvent extends EventObject {

        private final Code code;
        private final int width;
        private final int height;

        public enum Code {SURFACE_CREATED, SURFACE_CHANGED}

        public ViewEvent(Object source, Code code, int width, int height) {
            super(source);
            this.code = code;
            this.width = width;
            this.height = height;
        }

        public Code getCode() {
            return code;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class FPSEvent extends EventObject {

        private final int fps;

        public FPSEvent(Object source, int fps) {
            super(source);
            this.fps = fps;
        }

        public int getFps() {
            return fps;
        }
    }

    private final static String TAG = ModelRenderer.class.getSimpleName();

    // grid
    private static final float GRID_WIDTH = 100f;
    private static final float GRID_SIZE = 1f;
    private static final float[] GRID_COLOR = {0.25f, 0.25f, 0.25f, 0.5f};

    // blending
    private static final float[] BLENDING_MASK_DEFAULT = {1.0f, 1.0f, 1.0f, 1.0f};
    // Add 0.5f to the alpha component to the global shader so we can see through the skin
    private static final float[] BLENDING_MASK_FORCED = {1.0f, 1.0f, 1.0f, 0.5f};

    // frustrum - nearest pixel
    private static final float near = 1f;
    // frustrum - fartest pixel
    private static final float far = 2000f;

    // stereoscopic variables
    private static float EYE_DISTANCE = 0.64f;
    private static final float[] COLOR_RED = {1.0f, 0.0f, 0.0f, 1f};
    private static final float[] COLOR_BLUE = {0.0f, 1.0f, 0.0f, 1f};
    private static final float[] COLOR_WHITE = {1f, 1f, 1f, 1f};

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
    private Map<Object, Integer> textures = new HashMap<>();
    // The corresponding opengl bounding boxes and drawer
    private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<>();
    // The corresponding opengl bounding boxes
    private Map<Object3DData, Object3DData> normals = new HashMap<>();
    private Map<Object3DData, Object3DData> skeleton = new HashMap<>();

    // 3D matrices to project our 3D world
    private final float[] viewMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    // light
    private final float[] tempVector4 = new float[4];
    private final float[] lightPosInWorldSpace = new float[3];
    private final float[] cameraPosInWorldSpace = new float[3];
    private final float[] lightPosition = new float[]{0,0,0,1};

    // Decoration
    private final List<Object3DData> extras = new ArrayList<>();
    private final Object3DData axis = Axis.build().setId("axis").setSolid(false).setScale(new float[]{10,10,10});
    private final Object3DData gridx = Grid.build(-GRID_WIDTH, 0f, -GRID_WIDTH, GRID_WIDTH, 0f, GRID_WIDTH, GRID_SIZE)
            .setColor(GRID_COLOR).setId("grid-x").setSolid(false);
    private final Object3DData gridy = Grid.build(-GRID_WIDTH, -GRID_WIDTH, 0f, GRID_WIDTH, GRID_WIDTH, 0f, GRID_SIZE)
            .setColor(GRID_COLOR).setId("grid-y").setSolid(false);
    private final Object3DData gridz = Grid.build(0,-GRID_WIDTH,-GRID_WIDTH,0,GRID_WIDTH, GRID_WIDTH, GRID_SIZE)
            .setColor(GRID_COLOR).setId("grid-z").setSolid(false);
    {
        //extras.add(axis);
        //extras.add(gridx);
        //extras.add(gridy);
        //extras.add(gridz);
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

    /**
     * Whether the info of the model has been written to console log
     */
    private Map<String, Boolean> infoLogged = new HashMap<>();
    /**
     * Switch to akternate drawing of right and left image
     */
    private boolean anaglyphSwitch = false;

    /**
     * Skeleton Animator
     */
    private Animator animator = new Animator();
    /**
     * Did the application explode?
     */
    private boolean fatalException = false;

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
    }

    public ModelRenderer addListener(EventListener listener) {
        this.listeners.add(listener);
        return this;
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    public void toggleLights() {
        lightsEnabled = !lightsEnabled;
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
        Log.d(TAG, "onSurfaceCreated. config: "+config);

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
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        this.width = width;
        this.height = height;

        // Adjust the viewport based on geometry changes, such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        // the projection matrix is the 3D virtual space (cube) that we want to project
        this.ratio = (float) width / height;
        Log.d(TAG, "onSurfaceChanged: projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());
        Matrix.frustumM(projectionMatrixRight, 0, -ratio, ratio, -1, 1, getNear(), getFar());
        Matrix.frustumM(projectionMatrixLeft, 0, -ratio, ratio, -1, 1, getNear(), getFar());

        AndroidUtils.fireEvent(listeners, new ViewEvent(this, ViewEvent.Code.SURFACE_CHANGED, width, height));
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

            if (scene == null) {
                // scene not ready
                return;
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

            // animate scene
            scene.onDrawFrame();

            // recalculate mvp matrix according to where we are looking at now
            Camera camera = scene.getCamera();
            cameraPosInWorldSpace[0] = camera.xPos;
            cameraPosInWorldSpace[1] = camera.yPos;
            cameraPosInWorldSpace[2] = camera.zPos;
            if (camera.hasChanged()) {
                // INFO: Set the camera position (View matrix)
                // The camera has 3 vectors (the position, the vector where we are looking at, and the up position (sky)

                // the projection matrix is the 3D virtual space (cube) that we want to project
                float ratio = (float) width / height;
                // Log.v(TAG, "Camera changed: projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10], ");

                if (!scene.isStereoscopic()) {
                    Matrix.setLookAtM(viewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
                            camera.zView, camera.xUp, camera.yUp, camera.zUp);
                    Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
                } else {
                    Camera[] stereoCamera = camera.toStereo(EYE_DISTANCE);
                    Camera leftCamera = stereoCamera[0];
                    Camera rightCamera = stereoCamera[1];

                    // camera on the left for the left eye
                    Matrix.setLookAtM(viewMatrixLeft, 0, leftCamera.xPos, leftCamera.yPos, leftCamera.zPos, leftCamera
                                    .xView,
                            leftCamera.yView, leftCamera.zView, leftCamera.xUp, leftCamera.yUp, leftCamera.zUp);
                    // camera on the right for the right eye
                    Matrix.setLookAtM(viewMatrixRight, 0, rightCamera.xPos, rightCamera.yPos, rightCamera.zPos, rightCamera
                                    .xView,
                            rightCamera.yView, rightCamera.zView, rightCamera.xUp, rightCamera.yUp, rightCamera.zUp);

                    if (scene.isAnaglyph()) {
                        Matrix.frustumM(projectionMatrixRight, 0, -ratio, ratio, -1, 1, getNear(), getFar());
                        Matrix.frustumM(projectionMatrixLeft, 0, -ratio, ratio, -1, 1, getNear(), getFar());
                    } else if (scene.isVRGlasses()) {
                        float ratio2 = (float) width / 2 / height;
                        Matrix.frustumM(projectionMatrixRight, 0, -ratio2, ratio2, -1, 1, getNear(), getFar());
                        Matrix.frustumM(projectionMatrixLeft, 0, -ratio2, ratio2, -1, 1, getNear(), getFar());
                    }
                    // Calculate the projection and view transformation
                    Matrix.multiplyMM(viewProjectionMatrixLeft, 0, projectionMatrixLeft, 0, viewMatrixLeft, 0);
                    Matrix.multiplyMM(viewProjectionMatrixRight, 0, projectionMatrixRight, 0, viewMatrixRight, 0);

                }

                camera.setChanged(false);

            }


            if (!scene.isStereoscopic()) {
                this.onDrawFrame(viewMatrix, projectionMatrix, viewProjectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                return;
            }


            if (scene.isAnaglyph()) {
                // INFO: switch because blending algorithm doesn't mix colors
                if (anaglyphSwitch) {
                    this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft, viewProjectionMatrixLeft, lightPosInWorldSpace,
                            COLOR_RED, cameraPosInWorldSpace);
                } else {
                    this.onDrawFrame(viewMatrixRight, projectionMatrixRight, viewProjectionMatrixRight, lightPosInWorldSpace,
                            COLOR_BLUE, cameraPosInWorldSpace);
                }
                anaglyphSwitch = !anaglyphSwitch;
                return;
            }

            if (scene.isVRGlasses()) {

                // draw left eye image
                GLES20.glViewport(0, 0, width / 2, height);
                GLES20.glScissor(0, 0, width / 2, height);
                this.onDrawFrame(viewMatrixLeft, projectionMatrixLeft, viewProjectionMatrixLeft, lightPosInWorldSpace,
                        null, cameraPosInWorldSpace);

                // draw right eye image
                GLES20.glViewport(width / 2, 0, width / 2, height);
                GLES20.glScissor(width / 2, 0, width / 2, height);
                this.onDrawFrame(viewMatrixRight, projectionMatrixRight, viewProjectionMatrixRight, lightPosInWorldSpace,
                        null, cameraPosInWorldSpace);
            }


        } catch (Exception ex) {
            Log.e("ModelRenderer", "Fatal exception: " + ex.getMessage(), ex);
            fatalException = true;
        } catch (Error err){
            Log.e("ModelRenderer", "Fatal error: " + err.getMessage(), err);
            fatalException = true;
        }
    }

    private void onDrawFrame(float[] viewMatrix, float[] projectionMatrix, float[] viewProjectionMatrix,
                             float[] lightPosInWorldSpace, float[] colorMask, float[] cameraPosInWorldSpace) {

        // draw light
        boolean doAnimation = scene.isDoAnimation() && animationEnabled;
        boolean drawLighting = scene.isDrawLighting() && isLightsEnabled();
        boolean drawWireframe = scene.isDrawWireframe() || wireframeEnabled;
        boolean drawTextures = scene.isDrawTextures() && texturesEnabled;
        boolean drawColors = scene.isDrawColors() && colorsEnabled;

        if (drawLighting) {

            Renderer basicShader = drawer.getBasicShader();

            // Calculate position of the light in world space to support lighting
            if (scene.isRotatingLight()) {
                Matrix.multiplyMV(tempVector4, 0, scene.getLightBulb().getModelMatrix(), 0, lightPosition, 0);
                lightPosInWorldSpace[0] = tempVector4[0];
                lightPosInWorldSpace[1] = tempVector4[1];
                lightPosInWorldSpace[2] = tempVector4[2];

                // Draw a point that represents the light bulb
                basicShader.draw(scene.getLightBulb(), projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                //basicShader.draw(Point.build(lightPosInWorldSpace), projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
            } else {
                lightPosInWorldSpace[0] = cameraPosInWorldSpace[0];
                lightPosInWorldSpace[1] = cameraPosInWorldSpace[1];
                lightPosInWorldSpace[2] = cameraPosInWorldSpace[2];
            }

            // FIXME: memory leak
            if (scene.isDrawNormals()) {
                basicShader.draw(Line.build(new float[]{lightPosInWorldSpace[0],
                                lightPosInWorldSpace[1], lightPosInWorldSpace[2], 0, 0, 0}).setId("light_line"), projectionMatrix,
                        viewMatrix, -1,
                        lightPosInWorldSpace,
                        colorMask, cameraPosInWorldSpace);
            }
        }

        // draw all available objects
        List<Object3DData> objects = scene.getObjects();
        for (int i = 0; i < objects.size(); i++) {
            drawObject(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, doAnimation, drawLighting, drawWireframe, drawTextures, drawColors, objects, i);
        }

        // draw all GUI objects
        List<Object3DData> guiObjects = scene.getGUIObjects();
        for (int i = 0; i < guiObjects.size(); i++) {
            drawObject(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, doAnimation, drawLighting, drawWireframe, drawTextures, drawColors, guiObjects, i);
        }

        // draw all extra objects
        for (int i = 0; i < extras.size(); i++) {
            drawObject(viewMatrix, projectionMatrix, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace, doAnimation, drawLighting, drawWireframe, drawTextures, drawColors, extras, i);
        }

        if (framesPerSecondTime == -1) {
            framesPerSecondTime = SystemClock.elapsedRealtime();
            framesPerSecondCounter++;
        } else if (SystemClock.elapsedRealtime() > framesPerSecondTime + 1000) {
            framesPerSecond = framesPerSecondCounter;
            framesPerSecondCounter = 1;
            framesPerSecondTime = SystemClock.elapsedRealtime();
            AndroidUtils.fireEvent(listeners,new FPSEvent(this, framesPerSecond));
        } else {
            framesPerSecondCounter++;
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


            Renderer drawerObject = drawer.getDrawer(objData, drawTextures, drawLighting, doAnimation, drawColors);
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
            Integer textureId = null;
            if (drawTextures) {

                // TODO: move texture loading to Renderer
                if (objData.getElements() != null) {

                    for (int e = 0; e < objData.getElements().size(); e++) {
                        Element element = objData.getElements().get(e);

                        // check required info
                        if (element.getMaterial() == null || element.getMaterial().getTextureData() == null)
                            continue;

                        // check if texture is already binded
                        textureId = textures.get(element.getMaterial().getTextureData());
                        if (textureId != null) continue;

                        // bind texture
                        Log.i("ModelRenderer", "Loading material texture for element... '" + element);
                        textureId = GLUtil.loadTexture(element.getMaterial().getTextureData());
                        element.getMaterial().setTextureId(textureId);

                        // cache texture
                        textures.put(element.getMaterial().getTextureData(), textureId);

                        // log event
                        Log.i("ModelRenderer", "Loaded material texture for element. id: " + textureId);

                        // FIXME: we have to set this, otherwise the RendererFactory won't return textured shader
                        objData.setTextureData(element.getMaterial().getTextureData());
                    }
                } else {
                    textureId = textures.get(objData.getTextureData());
                    if (textureId == null && objData.getTextureData() != null) {
                        Log.i("ModelRenderer", "Loading texture for obj: '" + objData.getId() + "'... bytes: " + objData.getTextureData().length);
                        ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
                        textureId = GLUtil.loadTexture(textureIs);
                        textureIs.close();
                        textures.put(objData.getTextureData(), textureId);
                        objData.getMaterial().setTextureId(textureId);

                        Log.i("ModelRenderer", "Loaded texture OK. id: " + textureId);
                    }
                }
            }
            if (textureId == null) {
                textureId = -1;
            }

            // draw points
            if (objData.getDrawMode() == GLES20.GL_POINTS) {
                Renderer basicDrawer = drawer.getBasicShader();
                basicDrawer.draw(objData, projectionMatrix, viewMatrix, GLES20.GL_POINTS, lightPosInWorldSpace, cameraPosInWorldSpace);
            }

            else {

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
                            wireframe.setColor(objData.getColor());
                            wireframes.put(objData, wireframe);
                            Log.i("ModelRenderer", "Wireframe build: "+wireframe);
                        }
                        animator.update(wireframe, scene.isShowBindPose());
                        drawerObject.draw(wireframe, projectionMatrix, viewMatrix, wireframe.getDrawMode(), wireframe.getDrawSize(), textureId, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                        //objData.render(drawer, lightPosInWorldSpace, colorMask);
                    } catch (Error e) {
                        Log.e("ModelRenderer", e.getMessage(), e);
                    }
                }

                // draw points
                else if (scene.isDrawPoints()) {
                    drawerObject.draw(objData, projectionMatrix, viewMatrix
                            , GLES20.GL_POINTS, objData.getDrawSize(),
                            textureId, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                    objData.render(drawer, lightPosInWorldSpace, colorMask);
                }

                // draw skeleton
                else if (scene.isDrawSkeleton() && objData instanceof AnimatedModel && ((AnimatedModel) objData)
                        .getAnimation() != null) {
                    Object3DData skeleton = this.skeleton.get(objData);
                    if (skeleton == null || changed) {
                        skeleton = Skeleton.build((AnimatedModel) objData);
                        this.skeleton.put(objData, skeleton);
                    }
                    animator.update(skeleton, scene.isShowBindPose());
                    drawerObject = drawer.getDrawer(skeleton, false, drawLighting, doAnimation, drawColors);
                    drawerObject.draw(skeleton, projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                }

                // draw solids
                else {
                    if (!infoLogged.containsKey(objData.getId() + "render")) {
                        Log.i("ModelRenderer", "Rendering object... "+objData.getId());
                        Log.d("ModelRenderer", objData.toString());
                        Log.d("ModelRenderer", drawerObject.toString());
                        infoLogged.put(objData.getId() + "render", true);
                    }
                    drawerObject.draw(objData, projectionMatrix, viewMatrix,
                            textureId, lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
                    objData.render(drawer, lightPosInWorldSpace, colorMask);
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
                        normalData.setId(objData.getId()+"_normals");
                        // it can be null if object isnt made of triangles
                        normals.put(objData, normalData);
                    }
                }
                if (normalData != null) {
                    Renderer normalsDrawer = drawer.getDrawer(normalData, false, false, doAnimation,
                            false);
                    animator.update(normalData, scene.isShowBindPose());
                    normalsDrawer.draw(normalData, projectionMatrix, viewMatrix, -1, lightPosInWorldSpace,colorMask
                            , cameraPosInWorldSpace);
                }
            }

        } catch (Exception ex) {
            Log.e("ModelRenderer", "There was a problem rendering the object '" + objData.getId() + "':" + ex.getMessage(), ex);
        } catch (Error ex) {
            Log.e("ModelRenderer", "There was a problem rendering the object '" + objData.getId() + "':" + ex.getMessage(), ex);
        }
    }

    private void drawBoundingBox(float[] viewMatrix, float[] projectionMatrix, float[] lightPosInWorldSpace, float[] colorMask, float[] cameraPosInWorldSpace, Object3DData objData, boolean changed) {
        Object3DData boundingBoxData = boundingBoxes.get(objData);
        if (boundingBoxData == null || changed) {
            Log.i("ModelRenderer", "Building bounding box... id: " + objData.getId());
            boundingBoxData = BoundingBox.build(objData);
            boundingBoxData.setColor(COLOR_WHITE);
            boundingBoxes.put(objData, boundingBoxData);
            Log.i("ModelRenderer", "Bounding box: " + boundingBoxData);
        }
        Renderer boundingBoxDrawer = drawer.getBoundingBoxDrawer();
        boundingBoxDrawer.draw(boundingBoxData, projectionMatrix, viewMatrix, -1,
                lightPosInWorldSpace, colorMask, cameraPosInWorldSpace);
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
}