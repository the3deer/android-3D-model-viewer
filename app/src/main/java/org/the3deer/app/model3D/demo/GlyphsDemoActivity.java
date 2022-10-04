package org.the3deer.app.model3D.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.the3deer.android_3d_model_engine.gui.Text;
import org.the3deer.android_3d_model_engine.gui.Widget;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.android_3d_model_engine.model.Projection;
import org.the3deer.android_3d_model_engine.services.SceneLoader;
import org.the3deer.android_3d_model_engine.view.ModelSurfaceView;
import org.the3deer.android_3d_model_engine.view.ViewEvent;
import org.the3deer.util.event.EventListener;

import java.util.EventObject;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class GlyphsDemoActivity extends Activity implements EventListener {

    private ModelSurfaceView glView;
    private SceneLoader scene;
    private Camera camera;

    private Text abcd;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("GlyphsDemoActivity", "onCreate: Loading activity... "+savedInstanceState);
        super.onCreate(savedInstanceState);
        
        try {
            // Create our 3D scenario
            Log.i("GlyphsDemoActivity", "Creating Scene...");
            scene = new SceneLoader(this);
            scene.addListener(this);

            // Camera setup
            final Camera camera = new Camera(Constants.UNIT);
            camera.setProjection(Projection.PERSPECTIVE);
            camera.setChanged(true);
            scene.setCamera(camera);


            Log.i("GlyphsDemoActivity", "Loading GLSurfaceView...");
            glView = new ModelSurfaceView(this, Constants.COLOR_GRAY, this.scene);
            glView.addListener(this);
            glView.setProjection(Projection.PERSPECTIVE);
            setContentView(glView);


            abcd = Text.allocate(10, 6);
            abcd.setPadding(Widget.PADDING_01);
            abcd.update("abcdefghij\n" +
                    "klmnopqrst\n" +
                    "uvwxyz\n" +
                    "ABCDEFGHIJ\n" +
                    "KLMNOPQRST\n" +
                    "UVWXYZ");
            abcd.setVisible(true);
            scene.addObject(abcd);

            
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
                abcd.setScale(Constants.UNIT/5,Constants.UNIT/5,Constants.UNIT/5);
                float ratio = (float) viewEvent.getWidth() / viewEvent.getHeight();
                float x = -ratio * Constants.UNIT + abcd.getCurrentDimensions().getWidth() / 2 - abcd.getCurrentDimensions().getCenter()[0] + Constants.UNIT * 0.05f;
                float y = 1 * Constants.UNIT - abcd.getCurrentDimensions().getHeight() / 2 - abcd.getCurrentDimensions().getCenter()[1] - Constants.UNIT * 0.05f;
                abcd.setLocation( new float[]{x,y,0});
            }
        }
        return false;
    }
}
