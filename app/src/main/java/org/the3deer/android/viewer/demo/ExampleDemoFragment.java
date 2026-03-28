package org.the3deer.android.viewer.demo;

import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.model.Model;
import org.the3deer.android.engine.model.Object3D;
import org.the3deer.android.engine.model.Scene;
import org.the3deer.android.engine.objects.Cube;
import org.the3deer.android.engine.services.LoadListenerAdapter;
import org.the3deer.android.engine.services.collada.ColladaLoader;
import org.the3deer.android.engine.services.wavefront.WavefrontLoader;
import org.the3deer.android.engine.util.Exploder;
import org.the3deer.android.engine.util.Rescaler;
import org.the3deer.android.util.ContentUtils;
import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.android.viewer.ui.home.HomeFragment;
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
public class ExampleDemoFragment extends HomeFragment {

    private final static String TAG = ExampleDemoFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHandler().post(this::setUp);
    }

    private void setUp() {
        Log.i(TAG, "Starting up...");

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        @NotNull Model model = sharedViewModel.createModel("demo.gui");
        ModelEngine modelEngine = sharedViewModel.loadEngine("demo.gui", model, requireActivity());

        // preload assets
        ContentUtils.provideAssets(getActivity());

        // create new scene
        final Model sceneManager = modelEngine.getBeanFactory().find(Model.class);
        final Scene mainScene = new Scene();

        // update model
        sceneManager.addScene(mainScene);

        // load model
        Log.i(TAG, "Loading demo...");

        // list of errors found
        final List<Exception> errors = new ArrayList<>();

        // test cube made of arrays
        Object3D obj10 = Cube.buildCubeV1();
        obj10.setColor(new float[] { 1f, 0f, 0f, 0.5f });
        obj10.setLocation(new float[] { -2f, 2f, 0f });
        obj10.setScale(0.5f, 0.5f, 0.5f);
        mainScene.addObject(obj10);


        // test cube made of wires (I explode it to see the faces better)
        Object3D obj11 = Cube.buildCubeV1();
        obj11.setColor(new float[]{1f, 1f, 0f, 0.5f});
        obj11.setLocation(new float[]{0f, 2f, 0f});
        Exploder.centerAndScaleAndExplode(obj11, 2.0f, 1.5f);
        obj11.setId(obj11.getId() + "_exploded");
        obj11.setScale(0.5f, 0.5f, 0.5f);
        mainScene.addObject(obj11);

        // test cube made of wires (I explode it to see the faces better)
        Object3D obj12 = Cube.buildCubeV1_with_normals();
        obj12.setColor(new float[]{1f, 0f, 1f, 1f});
        obj12.setLocation(new float[]{0f, 0f, -2f});
        obj12.setScale(0.5f, 0.5f, 0.5f);
        mainScene.addObject(obj12);

        // test cube made of indices
        Object3D obj20 = Cube.buildCubeV2();
        obj20.setColor(new float[]{0f, 1f, 0, 0.25f});
        obj20.setLocation(new float[]{2f, 2f, 0f});
        obj20.setScale(0.5f, 0.5f, 0.5f);
        mainScene.addObject(obj20);

        // test cube with texture
        try {
            InputStream open = ContentUtils.getInputStream("penguin.bmp");
            Object3D obj3 = Cube.buildCubeV3(IOUtils.read(open));
            open.close();
            obj3.setColor(new float[] { 1f, 1f, 1f, 1f });
            obj3.setLocation(new float[] { -2f, -2f, 0f });
            obj3.setScale(0.5f, 0.5f, 0.5f);
            mainScene.addObject(obj3);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test cube with texture & colors
        try {
            InputStream open =  ContentUtils.getInputStream("cube.bmp");
            Object3D obj4 = Cube.buildCubeV4(IOUtils.read(open));
            open.close();
            obj4.setColor(new float[] { 1f, 1f, 1f, 1f });
            obj4.setLocation(new float[] { 0f, -2f, 0f });
            obj4.setScale(0.5f, 0.5f, 0.5f);
            mainScene.addObject(obj4);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object
        try {
            // this has no color array
            Object3D obj51 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoadObject(Scene scene, Object3D obj53) {
                    obj53.setLocation(new float[] { -2f, 0f, 0f });
                    obj53.setColor(new float[] { 1.0f, 1.0f, 0f, 1.0f });
                    Rescaler.rescale(obj53, 2f);
                    mainScene.addObject(obj53);
                }
            }).load(new URI("android://org.the3deer.android.viewer/assets/models/teapot.obj")).get(0);

            //obj51.setScale(2f,2f,2f);
            //obj51.setSize(0.5f);
            //scene.addObject(obj51);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object with materials
        try {
            // this has color array
            Object3D obj52 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoadObject(Scene scene, Object3D obj53) {
                    obj53.setLocation(new float[] { 1.5f, -2.5f, -0.5f });
                    obj53.setColor(new float[] { 0.0f, 1.0f, 1f, 1.0f });
                    mainScene.addObject(obj53);
                }
            }).load(new URI("android://org.the3deer.android.viewer/assets/models/cube.obj")).get(0);

            //obj52.setScale(0.5f, 0.5f, 0.5f);
            //scene.addObject(obj52);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object made of polygonal faces
        try {
            // this has heterogeneous faces
            Object3D obj53 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoadObject(Scene scene, Object3D obj53) {
                    obj53.setColor(new float[] { 1.0f, 1.0f, 1f, 1.0f });
                    Rescaler.rescale(obj53, 2f);
                    obj53.setLocation(new float[] { 2f, 0f, 0f });
                    mainScene.addObject(obj53);
                }
            }).load(new URI("android://org.the3deer.android.viewer/assets/models/ToyPlane.obj")).get(0);

            //scene.addObject(obj53);
        } catch (Exception ex) {
            errors.add(ex);
        }

        // test loading object made of polygonal faces
        try {
            // this has heterogeneous faces
            //ContentUtils.setContext(getActivity());
            ColladaLoader colladaLoader = new ColladaLoader();
            Scene obj53 = colladaLoader.load(Uri.parse("android://org.the3deer.android.viewer/assets/models/cowboy.dae"));
            /*obj53.setColor(new float[] { 1.0f, 1.0f, 1f, 1.0f });
            Rescaler.rescale(obj53, 2f);
            obj53.setLocation(new float[] { 0f, 0f, 2f});
            obj53.setCentered(true);*/
            mainScene.merge(obj53);
        } catch (Exception ex) {
            errors.add(ex);
        }


        // test loading object without normals
                    /*try {
                        Object3D obj = Object3DBuilder.loadV5(parent, Uri.parse("android://assets/models/cube4.obj"));
                        obj.setPosition(new float[] { 0f, 2f, -2f });
                        obj.setColor(new float[] { 0.3f, 0.52f, 1f, 1.0f });
                        addObject(obj);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }*/

        // more test to check right position
        {
            Object3D obj111 = Cube.buildCubeV1();
            obj111.setColor(new float[]{1f, 0f, 0f, 0.25f});
            obj111.setLocation(new float[]{-1f, -2f, -1f});
            obj111.setScale(0.5f, 0.5f, 0.5f);
            mainScene.addObject(obj111);

            // more test to check right position
            Object3D obj112 = Cube.buildCubeV1();
            obj112.setColor(new float[]{1f, 0f, 1f, 0.25f});
            obj112.setLocation(new float[]{1f, -2f, -1f});
            obj112.setScale(0.5f, 0.5f, 0.5f);
            mainScene.addObject(obj112);

        }
        {
            // more test to check right position
            Object3D obj111 = Cube.buildCubeV1();
            obj111.setColor(new float[] { 1f, 1f, 0f, 0.25f });
            obj111.setLocation(new float[] { -1f, -2f, 1f });
            obj111.setScale(0.5f, 0.5f, 0.5f);
            mainScene.addObject(obj111);

            // more test to check right position
            Object3D obj112 = Cube.buildCubeV1();
            obj112.setColor(new float[] { 0f, 1f, 1f, 0.25f });
            obj112.setLocation(new float[] { 1f, -2f, 1f });
            obj112.setScale(0.5f, 0.5f, 0.5f);
            mainScene.addObject(obj112);

            model.update();
        }

        if (!errors.isEmpty()){
            Toast.makeText(getContext(), "Errors: " + errors,
                    Toast.LENGTH_LONG).show();
        }

        //ContentUtils.clearDocumentsProvided();
        //ContentUtils.setThreadActivity(null);
    }
}
