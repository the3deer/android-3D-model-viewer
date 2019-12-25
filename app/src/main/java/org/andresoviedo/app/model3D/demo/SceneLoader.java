package org.andresoviedo.app.model3D.demo;

import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.andresoviedo.android_3d_model_engine.animation.Animator;
import org.andresoviedo.android_3d_model_engine.collision.CollisionDetection;
import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.android_3d_model_engine.services.collada.ColladaLoaderTask;
import org.andresoviedo.android_3d_model_engine.services.stl.STLLoaderTask;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoaderTask;
import org.andresoviedo.app.model3D.view.ModelActivity;
import org.andresoviedo.app.model3D.view.ModelRenderer;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 *
 * @author andresoviedo
 */
public class SceneLoader implements LoaderTask.Callback {

    /**
     * Parent component
     */
    protected final ModelActivity parent;
    /**
     * List of data objects containing info for building the opengl objects
     */
    private List<Object3DData> objects = new ArrayList<>();
    /**
     * Show axis or not
     */
    private boolean drawAxis = false;
    /**
     * Point of view camera
     */
    private Camera camera;
    /**
     * Enable or disable blending (transparency)
     */
    private boolean isBlendingEnabled = true;
    /**
     * Force transparency
     */
    private boolean isBlendingForced = false;
    /**
     * Whether to draw objects as wireframes
     */
    private boolean drawWireframe = false;
    /**
     * Whether to draw using points
     */
    private boolean drawingPoints = false;
    /**
     * Whether to draw bounding boxes around objects
     */
    private boolean drawBoundingBox = false;
    /**
     * Whether to draw face normals. Normally used to debug models
     */
    // TODO: toggle feature this
    private boolean drawNormals = false;
    /**
     * Whether to draw using textures
     */
    private boolean drawTextures = true;
    /**
     * Whether to draw using colors or use default white color
     */
    private boolean drawColors = true;
    /**
     * Light toggle feature: we have 3 states: no light, light, light + rotation
     */
    private boolean rotatingLight = true;
    /**
     * Light toggle feature: whether to draw using lights
     */
    private boolean drawLighting = true;
    /**
     * Animate model (dae only) or not
     */
    private boolean doAnimation = true;
    /**
     * show bind pose only
     */
    private boolean showBindPose = false;
    /**
     * Draw skeleton or not
     */
    private boolean drawSkeleton = false;
    /**
     * Toggle collision detection
     */
    private boolean isCollision = false;
    /**
     * Toggle 3d
     */
    private boolean isStereoscopic = false;
    /**
     * Toggle 3d anaglyph (red, blue glasses)
     */
    private boolean isAnaglyph = false;
    /**
     * Toggle 3d VR glasses
     */
    private boolean isVRGlasses = false;
    /**
     * Object selected by the user
     */
    private Object3DData selectedObject = null;
    /**
     * Initial light position
     */
    private final float[] lightPosition = new float[]{0, 0, 6, 1};
    /**
     * Light bulb 3d data
     */
    private final Object3DData lightPoint = Object3DBuilder.buildPoint(lightPosition).setId("light");
    /**
     * Animator
     */
    private Animator animator = new Animator();
    /**
     * Did the user touched the model for the first time?
     */
    private boolean userHasInteracted;
    /**
     * time when model loading has started (for stats)
     */
    private long startTime;

    public SceneLoader(ModelActivity main) {
        this.parent = main;
    }

    public void init() {

        // Camera to show a point of view
        camera = new Camera();
        camera.setChanged(true); // force first draw

        if (parent.getParamUri() == null){
            return;
        }

        startTime = SystemClock.uptimeMillis();
        Uri uri = parent.getParamUri();
        Log.i("Object3DBuilder", "Loading model " + uri + ". async and parallel..");
        if (uri.toString().toLowerCase().endsWith(".obj") || parent.getParamType() == 0) {
            new WavefrontLoaderTask(parent, uri, this).execute();
        } else if (uri.toString().toLowerCase().endsWith(".stl") || parent.getParamType() == 1) {
            Log.i("Object3DBuilder", "Loading STL object from: "+uri);
            new STLLoaderTask(parent, uri, this).execute();
        } else if (uri.toString().toLowerCase().endsWith(".dae") || parent.getParamType() == 2) {
            Log.i("Object3DBuilder", "Loading Collada object from: "+uri);
            new ColladaLoaderTask(parent, uri, this).execute();
        }
    }

    public final boolean isDrawAxis(){
        return drawAxis;
    }

    public final void setDrawAxis(boolean drawAxis) {
        this.drawAxis = drawAxis;
    }

    public final Camera getCamera() {
        return camera;
    }

    private final void makeToastText(final String text, final int toastDuration) {
        parent.runOnUiThread(() -> Toast.makeText(parent.getApplicationContext(), text, toastDuration).show());
    }

    public final Object3DData getLightBulb() {
        return lightPoint;
    }

    public final float[] getLightPosition() {
        return lightPosition;
    }

    /**
     * Hook for animating the objects before the rendering
     */
    public final void onDrawFrame() {

        animateLight();

        // smooth camera transition
        camera.animate();

        // initial camera animation. animate if user didn't touch the screen
        if (!userHasInteracted) {
            animateCamera();
        }

        if (objects.isEmpty()) return;

        if (doAnimation) {
            for (int i=0; i<objects.size(); i++) {
                Object3DData obj = objects.get(i);
                animator.update(obj, isShowBindPose());
            }
        }
    }

    private void animateLight() {
        if (!rotatingLight) return;

        // animate light - Do a complete rotation every 5 seconds.
        long time = SystemClock.uptimeMillis() % 5000L;
        float angleInDegrees = (360.0f / 5000.0f) * ((int) time);
        lightPoint.setRotationY(angleInDegrees);
    }

    private void animateCamera(){
        camera.translateCamera(0.0025f, 0f);
    }

    final synchronized void addObject(Object3DData obj) {
        List<Object3DData> newList = new ArrayList<>(objects);
        newList.add(obj);
        this.objects = newList;
        requestRender();
    }

    private void requestRender() {
        // request render only if GL view is already initialized
        if (parent.getGLView() != null) {
            parent.getGLView().requestRender();
        }
    }

    public final synchronized List<Object3DData> getObjects() {
        return objects;
    }

    public final void toggleWireframe() {
        if (!this.drawWireframe && !this.drawingPoints && !this.drawSkeleton){
                this.drawWireframe = true;
                makeToastText("Wireframe", Toast.LENGTH_SHORT);
        } else if (!this.drawingPoints && !this.drawSkeleton){
                this.drawWireframe = false;
                this.drawingPoints = true;
                makeToastText("Points", Toast.LENGTH_SHORT);
        } else if (!this.drawSkeleton){
                this.drawingPoints = false;
                this.drawSkeleton = true;
                makeToastText("Skeleton", Toast.LENGTH_SHORT);
        } else {
                this.drawSkeleton = false;
                makeToastText("Faces", Toast.LENGTH_SHORT);
        }
        requestRender();
    }

    public final boolean isDrawWireframe() {
        return this.drawWireframe;
    }

    public final boolean isDrawPoints() {
        return this.drawingPoints;
    }

    public final void toggleBoundingBox() {
        this.drawBoundingBox = !drawBoundingBox;
        requestRender();
    }

    public final boolean isDrawBoundingBox() {
        return drawBoundingBox;
    }

    public final boolean isDrawNormals() {
        return drawNormals;
    }

    public final void toggleTextures() {
        if (drawTextures && drawColors){
            this.drawTextures = false;
            this.drawColors = true;
            makeToastText("Texture off", Toast.LENGTH_SHORT);
        } else if (drawColors){
            this.drawTextures = false;
            this.drawColors = false;
            makeToastText("Colors off", Toast.LENGTH_SHORT);
        } else {
            this.drawTextures = true;
            this.drawColors = true;
            makeToastText("Textures on", Toast.LENGTH_SHORT);
        }
    }

    public final void toggleLighting() {
        if (this.drawLighting && this.rotatingLight) {
            this.rotatingLight = false;
            makeToastText("Light stopped", Toast.LENGTH_SHORT);
        } else if (this.drawLighting && !this.rotatingLight) {
            this.drawLighting = false;
            makeToastText("Lights off", Toast.LENGTH_SHORT);
        } else {
            this.drawLighting = true;
            this.rotatingLight = true;
            makeToastText("Light on", Toast.LENGTH_SHORT);
        }
        requestRender();
    }

    public final void toggleAnimation() {
        //showAnimationsDialog();
        if (!this.doAnimation){
            this.doAnimation = true;
            this.showBindPose = false;
            makeToastText("Animation on", Toast.LENGTH_SHORT);
        } else {
            this.doAnimation = false;
            this.showBindPose = true;
            makeToastText("Bind pose", Toast.LENGTH_SHORT);
        }
    }

    /*private final void showAnimationsDialog(){


        final AnimatedModel animatedModel;
        if (objects.get(0) instanceof AnimatedModel){
            animatedModel = (AnimatedModel)objects.get(0);
        } else return;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this.parent);
        builderSingle.setTitle("Available animations:");

        // String[] items = new String[animatedModel.getAnimation().getAnimationList().size()];
        String[] items = new String[animatedModel.getAnimation().getAnimationList().size()];
        boolean[] selected = new boolean[animatedModel.getAnimation().getAnimationList().size()];
        for (int i=0; i<items.length; i++){
            String jointId = animatedModel.getAnimation().getAnimationList().get(i);
            items[i] = jointId;
            selected[i] = true;
        }

        builderSingle.setMultiChoiceItems(items, selected, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                animatedModel.getAnimation().getAnimationList().remove(items[which]);
            }
        });

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }*/

    public final boolean isDoAnimation() {
        return doAnimation;
    }

    public final boolean isShowBindPose() {
        return showBindPose;
    }

    public final void toggleCollision() {
        this.isCollision = !isCollision;
        makeToastText("Collisions: "+isCollision, Toast.LENGTH_SHORT);
    }

    public final void toggleStereoscopic() {
        if (!this.isStereoscopic){
            this.isStereoscopic = true;
            this.isAnaglyph = true;
            this.isVRGlasses = false;
            makeToastText("Stereoscopic Anaplygh", Toast.LENGTH_SHORT);
        } else if (this.isAnaglyph){
            this.isAnaglyph = false;
            this.isVRGlasses = true;
            // move object automatically cause with VR glasses we still have no way of moving object
            this.userHasInteracted = false;
            makeToastText("Stereoscopic VR Glasses", Toast.LENGTH_SHORT);
        } else {
            this.isStereoscopic = false;
            this.isAnaglyph = false;
            this.isVRGlasses = false;
            makeToastText("Stereoscopic disabled", Toast.LENGTH_SHORT);
        }
        // recalculate camera
        this.camera.setChanged(true);
    }

    public final boolean isVRGlasses() {
        return isVRGlasses;
    }

    public final boolean isDrawTextures() {
        return drawTextures;
    }

    public final boolean isDrawColors() {
        return drawColors;
    }

    public final boolean isDrawLighting() {
        return drawLighting;
    }

    public final boolean isDrawSkeleton() {
        return drawSkeleton;
    }

    public final boolean isCollision() {
        return isCollision;
    }

    public final boolean isStereoscopic() {
        return isStereoscopic;
    }

    public final boolean isAnaglyph() {
        return isAnaglyph;
    }

    public final void toggleBlending() {
        if (this.isBlendingEnabled && !this.isBlendingForced){
            makeToastText("Blending forced", Toast.LENGTH_SHORT);
            this.isBlendingEnabled = true;
            this.isBlendingForced = true;
        } else if (this.isBlendingForced){
            makeToastText("Blending disabled", Toast.LENGTH_SHORT);
            this.isBlendingEnabled = false;
            this.isBlendingForced = false;
        } else {
            makeToastText("Blending enabled", Toast.LENGTH_SHORT);
            this.isBlendingEnabled = true;
            this.isBlendingForced = false;
        }
    }

    public final boolean isBlendingEnabled() {
        return isBlendingEnabled;
    }

    public final boolean isBlendingForced() {
        return isBlendingForced;
    }

    @Override
    public void onStart(){
        ContentUtils.setThreadActivity(parent);
    }

    @Override
    public void onLoad(Object3DData data){
        // load new object and rescale all together so they fit in the viewport
        addObject(data);
        Object3DData.centerAndScale(this.objects, 5, new float[]{0, 0, 0});
    }

    @Override
    public void onLoadComplete(List<Object3DData> datas) {
        // TODO: move texture load to LoaderTask
        for (Object3DData data : datas) {
            if (data.getTextureData() == null && data.getTextureFile() != null) {
                Log.i("LoaderTask","Loading texture... "+data.getTextureFile());
                try (InputStream stream = ContentUtils.getInputStream(data.getTextureFile())){
                    if (stream != null) {
                        data.setTextureData(IOUtils.read(stream));
                    }
                } catch (IOException ex) {
                    data.addError("Problem loading texture " + data.getTextureFile());
                }
            }
        }

        // TODO: move error alert to LoaderTask
        List<String> allErrors = new ArrayList<>();
        for (Object3DData data : datas) {
            addObject(data);
            allErrors.addAll(data.getErrors());
        }
        if (!allErrors.isEmpty()){
            makeToastText(allErrors.toString(), Toast.LENGTH_LONG);
        }
        final String elapsed = (SystemClock.uptimeMillis() - startTime) / 1000 + " secs";
        makeToastText("Build complete (" + elapsed + ")", Toast.LENGTH_LONG);
        ContentUtils.setThreadActivity(null);
    }

    @Override
    public void onLoadError(Exception ex) {
        Log.e("SceneLoader", ex.getMessage(), ex);
        makeToastText("There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG);
        ContentUtils.setThreadActivity(null);
    }

    public Object3DData getSelectedObject() {
        return selectedObject;
    }

    private void setSelectedObject(Object3DData selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void loadTexture(Object3DData obj, Uri uri) throws IOException {
        if (obj == null && objects.size() != 1) {
            makeToastText("Unavailable", Toast.LENGTH_SHORT);
            return;
        }
        obj = obj != null ? obj : objects.get(0);
        obj.setTextureData(IOUtils.read(ContentUtils.getInputStream(uri)));
        this.drawTextures = true;
    }

    public void processTouch(float x, float y) {
        ModelRenderer mr = parent.getGLView().getModelRenderer();
        Object3DData objectToSelect = CollisionDetection.getBoxIntersection(getObjects(), mr.getWidth(), mr.getHeight
                (), mr.getModelViewMatrix(), mr.getModelProjectionMatrix(), x, y);
        if (objectToSelect != null) {
            if (getSelectedObject() == objectToSelect) {
                Log.i("SceneLoader", "Unselected object " + objectToSelect.getId());
                setSelectedObject(null);
            } else {
                Log.i("SceneLoader", "Selected object " + objectToSelect.getId());
                setSelectedObject(objectToSelect);
            }
            if (isCollision()) {
                Log.d("SceneLoader", "Detecting collision...");

                float[] point = CollisionDetection.getTriangleIntersection(getObjects(), mr.getWidth(), mr.getHeight
                        (), mr.getModelViewMatrix(), mr.getModelProjectionMatrix(), x, y);
                if (point != null) {
                    Log.i("SceneLoader", "Drawing intersection point: " + Arrays.toString(point));
                    addObject(Object3DBuilder.buildPoint(point).setColor(new float[]{1.0f, 0f, 0f, 1f}));
                }
            }
        }
    }

    public void processMove(float dx1, float dy1) {
        userHasInteracted = true;
    }

    public final boolean isRotatingLight() {
        return rotatingLight;
    }
}
