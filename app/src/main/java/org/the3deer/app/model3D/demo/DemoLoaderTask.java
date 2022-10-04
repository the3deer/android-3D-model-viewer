package org.the3deer.app.model3D.demo;

import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.objects.Cube;
import org.the3deer.android_3d_model_engine.services.LoadListener;
import org.the3deer.android_3d_model_engine.services.LoadListenerAdapter;
import org.the3deer.android_3d_model_engine.services.LoaderTask;
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
 * This class loads a 3D scene as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class DemoLoaderTask extends LoaderTask {

    /**
     * Build a new progress dialog for loading the data model asynchronously
     *
     * @param parent parent activity
     * @param uri      the URL pointing to the 3d model
     * @param callback listener
     */
    public DemoLoaderTask(Activity parent, URI uri, LoadListener callback) {
        super(parent, uri, callback);
        ContentUtils.provideAssets(parent);
    }

    @Override
    protected List<Object3DData> build() throws Exception {

        // notify user
        super.publishProgress("Loading demo...");

        // list of errors found
        final List<Exception> errors = new ArrayList<>();

        try {

            // test cube made of arrays
            Object3DData obj10 = Cube.buildCubeV1();
            obj10.setColor(new float[] { 1f, 0f, 0f, 0.5f });
            obj10.setLocation(new float[] { -2f, 2f, 0f });
            obj10.setScale(0.5f, 0.5f, 0.5f);
            super.onLoad(obj10);

            // test cube made of wires (I explode it to see the faces better)
            Object3DData obj11 = Cube.buildCubeV1();
            obj11.setColor(new float[] { 1f, 1f, 0f, 0.5f });
            obj11.setLocation(new float[] { 0f, 2f, 0f });
            Exploder.centerAndScaleAndExplode(obj11, 2.0f, 1.5f);
            obj11.setId(obj11.getId() + "_exploded");
            obj11.setScale(0.5f, 0.5f, 0.5f);
            super.onLoad(obj11);

            // test cube made of wires (I explode it to see the faces better)
            Object3DData obj12 = Cube.buildCubeV1_with_normals();
            obj12.setColor(new float[] { 1f, 0f, 1f, 1f });
            obj12.setLocation(new float[] { 0f, 0f, -2f });
            obj12.setScale(0.5f, 0.5f, 0.5f);
            super.onLoad(obj12);

            // test cube made of indices
            Object3DData obj20 = Cube.buildCubeV2();
            obj20.setColor(new float[] { 0f, 1f, 0, 0.25f });
            obj20.setLocation(new float[] { 2f, 2f, 0f });
            obj20.setScale(0.5f, 0.5f, 0.5f);
            super.onLoad(obj20);

            // test cube with texture
            try {
                InputStream open = ContentUtils.getInputStream("penguin.bmp");
                Object3DData obj3 = Cube.buildCubeV3(IOUtils.read(open));
                open.close();
                obj3.setColor(new float[] { 1f, 1f, 1f, 1f });
                obj3.setLocation(new float[] { -2f, -2f, 0f });
                obj3.setScale(0.5f, 0.5f, 0.5f);
                super.onLoad(obj3);
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
                super.onLoad(obj4);
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
                        DemoLoaderTask.this.onLoad(obj53);
                    }
                }).load(new URI("android://org.the3deer.dddmodel2/assets/models/teapot.obj")).get(0);

                //obj51.setScale(2f,2f,2f);
                //obj51.setSize(0.5f);
                //super.onLoad(obj51);
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
                        DemoLoaderTask.this.onLoad(obj53);
                    }
                }).load(new URI("android://org.the3deer.dddmodel2/assets/models/cube.obj")).get(0);

                //obj52.setScale(0.5f, 0.5f, 0.5f);
                //super.onLoad(obj52);
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
                        DemoLoaderTask.this.onLoad(obj53);
                    }
                }).load(new URI("android://org.the3deer.dddmodel2/assets/models/ToyPlane.obj")).get(0);

                //super.onLoad(obj53);
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
                        DemoLoaderTask.this.onLoad(obj53);
                    }
                }).get(0);

                //super.onLoad(obj53);
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
                super.onLoad(obj111);

                // more test to check right position
                Object3DData obj112 = Cube.buildCubeV1();
                obj112.setColor(new float[]{1f, 0f, 1f, 0.25f});
                obj112.setLocation(new float[]{1f, -2f, -1f});
                obj112.setScale(0.5f, 0.5f, 0.5f);
                super.onLoad(obj112);

            }
            {
                // more test to check right position
                Object3DData obj111 = Cube.buildCubeV1();
                obj111.setColor(new float[] { 1f, 1f, 0f, 0.25f });
                obj111.setLocation(new float[] { -1f, -2f, 1f });
                obj111.setScale(0.5f, 0.5f, 0.5f);
                super.onLoad(obj111);

                // more test to check right position
                Object3DData obj112 = Cube.buildCubeV1();
                obj112.setColor(new float[] { 0f, 1f, 1f, 0.25f });
                obj112.setLocation(new float[] { 1f, -2f, 1f });
                obj112.setScale(0.5f, 0.5f, 0.5f);
                super.onLoad(obj112);

            }

        } catch (Exception ex) {
            errors.add(ex);
            if (!errors.isEmpty()) {
                StringBuilder msg = new StringBuilder("There was a problem loading the data");
                for (Exception error : errors) {
                    Log.e("Example", error.getMessage(), error);
                    msg.append("\n").append(error.getMessage());
                }
                throw new Exception(msg.toString());
            }
        }
        return null;
    }

    @Override
    public void onProgress(String progress) {
        super.publishProgress(progress);
    }
}
