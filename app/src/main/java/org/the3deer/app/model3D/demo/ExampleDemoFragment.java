package org.the3deer.app.model3D.demo;

import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.model.Scene;
import org.the3deer.android_3d_model_engine.objects.Cube;
import org.the3deer.android_3d_model_engine.services.LoadListenerAdapter;
import org.the3deer.android_3d_model_engine.services.collada.ColladaLoader;
import org.the3deer.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.the3deer.android_3d_model_engine.util.Exploder;
import org.the3deer.android_3d_model_engine.util.Rescaler;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.io.IOUtils;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Demo example activity. Load distinct models in the scene.
 * It also attaches 1 more demo Scene (selectable from the Preferences)
 *
 * @author andresoviedo
 */
public class ExampleDemoFragment extends ModelFragment {

    private final static String TAG = ExampleDemoFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check
        if (handler == null) {
            Log.e(TAG, "Handler is null");
            throw new IllegalStateException("Handler is null");
        }

        handler.post(this::setUp);
    }

    private void setUp() {
        Log.i(TAG, "Starting up...");

        ContentUtils.provideAssets(getActivity());

        final Scene scene = modelEngine.getBeanFactory().find(Scene.class);

        // load model
        Log.i(TAG, "Loading demo...");

        // list of errors found
        final List<Exception> errors = new ArrayList<>();

        // test cube made of arrays
        Object3DData obj10 = Cube.buildCubeV1();
        obj10.setColor(new float[] { 1f, 0f, 0f, 0.5f });
        obj10.setLocation(new float[] { -2f, 2f, 0f });
        obj10.setScale(0.5f, 0.5f, 0.5f);
        scene.addObject(obj10);

        // test cube made of wires (I explode it to see the faces better)
        Object3DData obj11 = Cube.buildCubeV1();
        obj11.setColor(new float[]{1f, 1f, 0f, 0.5f});
        obj11.setLocation(new float[]{0f, 2f, 0f});
        Exploder.centerAndScaleAndExplode(obj11, 2.0f, 1.5f);
        obj11.setId(obj11.getId() + "_exploded");
        obj11.setScale(0.5f, 0.5f, 0.5f);
        scene.addObject(obj11);

        // test cube made of wires (I explode it to see the faces better)
        Object3DData obj12 = Cube.buildCubeV1_with_normals();
        obj12.setColor(new float[]{1f, 0f, 1f, 1f});
        obj12.setLocation(new float[]{0f, 0f, -2f});
        obj12.setScale(0.5f, 0.5f, 0.5f);
        scene.addObject(obj12);

        // test cube made of indices
        Object3DData obj20 = Cube.buildCubeV2();
        obj20.setColor(new float[]{0f, 1f, 0, 0.25f});
        obj20.setLocation(new float[]{2f, 2f, 0f});
        obj20.setScale(0.5f, 0.5f, 0.5f);
        scene.addObject(obj20);

        // test cube with texture
        try {
            InputStream open = ContentUtils.getInputStream("penguin.bmp");
            Object3DData obj3 = Cube.buildCubeV3(IOUtils.read(open));
            open.close();
            obj3.setColor(new float[] { 1f, 1f, 1f, 1f });
            obj3.setLocation(new float[] { -2f, -2f, 0f });
            obj3.setScale(0.5f, 0.5f, 0.5f);
            scene.addObject(obj3);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test cube with texture & colors
        try {
            InputStream open =  ContentUtils.getInputStream("cube.bmp");
            Object3DData obj4 = Cube.buildCubeV4(IOUtils.read(open));
            open.close();
            obj4.setColor(new float[] { 1f, 1f, 1f, 1f });
            obj4.setLocation(new float[] { 0f, -2f, 0f });
            obj4.setScale(0.5f, 0.5f, 0.5f);
            scene.addObject(obj4);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object
        try {
            // this has no color array
            Object3DData obj51 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoad(Object3DData obj53) {
                    obj53.setLocation(new float[] { -2f, 0f, 0f });
                    obj53.setColor(new float[] { 1.0f, 1.0f, 0f, 1.0f });
                    Rescaler.rescale(obj53, 2f);
                    scene.addObject(obj53);
                }
            }).load(new URI("android://org.the3deer.dddmodel2/assets/models/teapot.obj")).get(0);

            //obj51.setScale(2f,2f,2f);
            //obj51.setSize(0.5f);
            //scene.addObject(obj51);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object with materials
        try {
            // this has color array
            Object3DData obj52 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoad(Object3DData obj53) {
                    obj53.setLocation(new float[] { 1.5f, -2.5f, -0.5f });
                    obj53.setColor(new float[] { 0.0f, 1.0f, 1f, 1.0f });
                    scene.addObject(obj53);
                }
            }).load(new URI("android://org.the3deer.dddmodel2/assets/models/cube.obj")).get(0);

            //obj52.setScale(0.5f, 0.5f, 0.5f);
            //scene.addObject(obj52);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object made of polygonal faces
        try {
            // this has heterogeneous faces
            Object3DData obj53 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoad(Object3DData obj53) {
                    obj53.setColor(new float[] { 1.0f, 1.0f, 1f, 1.0f });
                    Rescaler.rescale(obj53, 2f);
                    obj53.setLocation(new float[] { 2f, 0f, 0f });
                    scene.addObject(obj53);
                }
            }).load(new URI("android://org.the3deer.dddmodel2/assets/models/ToyPlane.obj")).get(0);

            //scene.addObject(obj53);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object made of polygonal faces
        try {
            // this has heterogeneous faces
            Object3DData obj53 = new ColladaLoader().load(new URI("android://org.the3deer.dddmodel2/assets/models/cowboy.dae"), new LoadListenerAdapter(){
                @Override
                public void onLoad(Object3DData obj53) {
                    obj53.setColor(new float[] { 1.0f, 1.0f, 1f, 1.0f });
                    Rescaler.rescale(obj53, 2f);
                    obj53.setLocation(new float[] { 0f, 0f, 2f});
                    obj53.setCentered(true);
                    scene.addObject(obj53);
                }
            }).get(0);

            //scene.addObject(obj53);
        } catch (Exception ex) {
            errors.add(ex);
        }


        // test loading object without normals
                    /*try {
                        Object3DData obj = Object3DBuilder.loadV5(parent, Uri.parse("android://assets/models/cube4.obj"));
                        obj.setPosition(new float[] { 0f, 2f, -2f });
                        obj.setColor(new float[] { 0.3f, 0.52f, 1f, 1.0f });
                        addObject(obj);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }*/

        // more test to check right position
        {
            Object3DData obj111 = Cube.buildCubeV1();
            obj111.setColor(new float[]{1f, 0f, 0f, 0.25f});
            obj111.setLocation(new float[]{-1f, -2f, -1f});
            obj111.setScale(0.5f, 0.5f, 0.5f);
            scene.addObject(obj111);

            // more test to check right position
            Object3DData obj112 = Cube.buildCubeV1();
            obj112.setColor(new float[]{1f, 0f, 1f, 0.25f});
            obj112.setLocation(new float[]{1f, -2f, -1f});
            obj112.setScale(0.5f, 0.5f, 0.5f);
            scene.addObject(obj112);

        }
        {
            // more test to check right position
            Object3DData obj111 = Cube.buildCubeV1();
            obj111.setColor(new float[] { 1f, 1f, 0f, 0.25f });
            obj111.setLocation(new float[] { -1f, -2f, 1f });
            obj111.setScale(0.5f, 0.5f, 0.5f);
            scene.addObject(obj111);

            // more test to check right position
            Object3DData obj112 = Cube.buildCubeV1();
            obj112.setColor(new float[] { 0f, 1f, 1f, 0.25f });
            obj112.setLocation(new float[] { 1f, -2f, 1f });
            obj112.setScale(0.5f, 0.5f, 0.5f);
            scene.addObject(obj112);

            scene.onLoadComplete();


        }

        if (!errors.isEmpty()){
            Toast.makeText(getContext(), "Errors: " + errors,
                    Toast.LENGTH_LONG).show();
        }

        ContentUtils.clearDocumentsProvided();
        ContentUtils.setThreadActivity(null);
    }
}
