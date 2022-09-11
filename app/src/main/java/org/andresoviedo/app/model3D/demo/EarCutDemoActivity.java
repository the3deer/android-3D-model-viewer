package org.andresoviedo.app.model3D.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.andresoviedo.android_3d_model_engine.model.Camera;
import org.andresoviedo.android_3d_model_engine.model.Constants;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.model.Projection;
import org.andresoviedo.android_3d_model_engine.services.LoadListener;
import org.andresoviedo.android_3d_model_engine.services.SceneLoader;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoaderTask;
import org.andresoviedo.android_3d_model_engine.util.EarCut;
import org.andresoviedo.android_3d_model_engine.view.ModelSurfaceView;
import org.andresoviedo.android_3d_model_engine.view.ViewEvent;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.event.EventListener;

import java.net.URI;
import java.util.EventObject;
import java.util.List;

/**
 * Demo on 3D/2D boolean algebra
 *
 * @author andresoviedo
 */
public class EarCutDemoActivity extends Activity implements EventListener {

    private ModelSurfaceView glView;
    private SceneLoader scene;
    private Camera camera;

    private Object3DData obj1;
    private Object3DData obj2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("PolyBoolDemoActivity", "onCreate: Loading activity... "+savedInstanceState);
        super.onCreate(savedInstanceState);
        
        try {
            // Create our 3D scenario
            Log.i("PolyBoolDemoActivity", "Creating Scene...");
            scene = new SceneLoader(this);
            scene.addListener(this);

            // Camera setup
            final Camera camera = new Camera(Constants.UNIT);
            camera.setProjection(Projection.ISOMETRIC);
            camera.setChanged(true);
            scene.setCamera(camera);


            Log.i("PolyBoolDemoActivity", "Loading GLSurfaceView...");
            glView = new ModelSurfaceView(this, Constants.COLOR_GRAY, this.scene);
            glView.addListener(this);
            glView.setProjection(Projection.PERSPECTIVE);
            setContentView(glView);


            WavefrontLoaderTask task = new WavefrontLoaderTask(this, URI.create("android://org.andresoviedo.dddmodel2/assets/models/triangle1.obj"), new LoadListener() {
                @Override
                public void onStart() {
                    // provide context to allow reading resources
                    ContentUtils.setThreadActivity(EarCutDemoActivity.this);
                }

                @Override
                public void onProgress(String progress) {

                }

                @Override
                public void onLoadError(Exception ex) {

                }

                @Override
                public void onLoad(Object3DData data) {
                    obj1 = data;
                    Log.i("PolyBoolDemoActivity", "Adding object...");
                    float value = 50f;
                    data.setScale(new float[]{value, value, value});
                    data.setColor(Constants.COLOR_RED);
                    scene.addObject(data);
                }

                @Override
                public void onLoadComplete() {

                }
            });
            task.execute();

            WavefrontLoaderTask task2 = new WavefrontLoaderTask(this, URI.create("android://org.andresoviedo.dddmodel2/assets/models/triangle2.obj"), new LoadListener() {
                @Override
                public void onStart() {
                    // provide context to allow reading resources
                    ContentUtils.setThreadActivity(EarCutDemoActivity.this);
                }

                @Override
                public void onProgress(String progress) {

                }

                @Override
                public void onLoadError(Exception ex) {

                }

                @Override
                public void onLoad(Object3DData data) {
                    obj2 = data;
                    Log.i("PolyBoolDemoActivity", "Adding object...");
                    float value = 50f;
                    data.setScale(new float[]{value, value, value});
                    data.setColor(Constants.COLOR_GREEN);
                    scene.addObject(data);
                }

                @Override
                public void onLoadComplete() {
                    try {
                        Thread.sleep(2000);

                        float[] data = new float[
                                obj1.getVertexBuffer().capacity() / 3 *2 +
                                obj2.getVertexBuffer().capacity() / 3 *2];

                        for(int i=0, j=0; j< obj1.getVertexBuffer().capacity(); i+=2, j+=3){
                            data[i] = obj1.getVertexBuffer().get(j);
                            data[i+1] = obj1.getVertexBuffer().get(j+1);
                        }
                        for(int i=0, j=0; j< obj2.getVertexBuffer().capacity(); i+=2, j+=3){
                            data[data.length/2+i] = obj2.getVertexBuffer().get(j);
                            data[data.length/2+i+1] = obj2.getVertexBuffer().get(j+1);
                        }
                        int[] holes = new int[]{};

                        List<Integer> indices = EarCut.earcut(data, holes, 2);

                        Log.v("PolyBoolDemoActivity","array: "+ indices.toString());


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            task2.execute();


        } catch (Exception e) {
            Log.e("GlyphsDemoActivity", e.getMessage(), e);
            Toast.makeText(this, "Error loading OpenGL view:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        
    }

    @Override
    public boolean onEvent(EventObject event) {
        if (event instanceof ViewEvent) {
            ViewEvent viewEvent = (ViewEvent) event;
            if (viewEvent.getCode() == ViewEvent.Code.SURFACE_CHANGED) {
                /*obj1.setScale(Constants.UNIT/5,Constants.UNIT/5,Constants.UNIT/5);
                float ratio = (float) viewEvent.getWidth() / viewEvent.getHeight();
                float x = -ratio * Constants.UNIT + obj1.getCurrentDimensions().getWidth() / 2 - obj1.getCurrentDimensions().getCenter()[0] + Constants.UNIT * 0.05f;
                float y = 1 * Constants.UNIT - obj1.getCurrentDimensions().getHeight() / 2 - obj1.getCurrentDimensions().getCenter()[1] - Constants.UNIT * 0.05f;
                obj1.setLocation( new float[]{x,y,0});*/
            }
        }
        return false;
    }
}
