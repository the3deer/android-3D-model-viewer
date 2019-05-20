package org.andresoviedo.app.model3D.demo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.app.model3D.view.ModelActivity;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class loads a 3D scene as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class ExampleSceneLoader extends SceneLoader {

	public ExampleSceneLoader(ModelActivity modelActivity) {
		super(modelActivity);
	}

	// TODO: fix this warning
	@SuppressLint("StaticFieldLeak")
    public void init() {
		super.init();
		new AsyncTask<Void, Void, Void>() {

			ProgressDialog dialog = new ProgressDialog(parent);
			List<Exception> errors = new ArrayList<>();

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setCancelable(false);
				dialog.setMessage("Loading demo...");
				dialog.show();
			}

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // 3D Axis
                    setDrawAxis(true);

                    // test cube made of arrays
                    Object3DData obj10 = Object3DBuilder.buildCubeV1();
                    obj10.setColor(new float[] { 1f, 0f, 0f, 0.5f });
                    obj10.setPosition(new float[] { -2f, 2f, 0f });
                    addObject(obj10);

                    // test cube made of wires (I explode it to see the faces better)
                    Object3DData obj11 = Object3DBuilder.buildCubeV1();
                    obj11.setColor(new float[] { 1f, 1f, 0f, 0.5f });
                    obj11.setPosition(new float[] { 0f, 2f, 0f });
                    obj11.centerAndScaleAndExplode(1.0f, 1.5f);
                    obj11.setId(obj11.getId() + "_exploded");
                    addObject(obj11);

                    // test cube made of wires (I explode it to see the faces better)
                    Object3DData obj12 = Object3DBuilder.buildCubeV1_with_normals();
                    obj12.setColor(new float[] { 1f, 0f, 1f, 1f });
                    obj12.setPosition(new float[] { 0f, 0f, -2f });
                    addObject(obj12);

                    // Set up ContentUtils so referenced materials and/or textures could be find
                    ContentUtils.setThreadActivity(parent);
                    ContentUtils.provideAssets(parent);

                    // test loading object
                    /*try {
                        // this has no color array
                        Object3DData android = Object3DBuilder.loadV5(parent, Uri.parse("assets://assets/models/android.obj"));
                        android.setPosition(new float[] { 0f, 0f, 0f });
                        android.setColor(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
                        addObject(android);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }*/

                    // test cube made of indices
                    Object3DData obj20 = Object3DBuilder.buildSquareV2();
                    obj20.setColor(new float[] { 0f, 1f, 0, 0.25f });
                    obj20.setPosition(new float[] { 2f, 2f, 0f });
                    addObject(obj20);

                    // test cube with texture
                    try {
                        InputStream open = ContentUtils.getInputStream(Uri.parse("assets://assets/models/penguin.bmp"));
                        Object3DData obj3 = Object3DBuilder.buildCubeV3(IOUtils.read(open));
                        open.close();
                        obj3.setColor(new float[] { 1f, 1f, 1f, 1f });
                        obj3.setPosition(new float[] { -2f, -2f, 0f });
                        addObject(obj3);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                    // test cube with texture & colors
                    try {
                        InputStream open =  ContentUtils.getInputStream(Uri.parse("assets://assets/models/cube.bmp"));
                        Object3DData obj4 = Object3DBuilder.buildCubeV4(IOUtils.read(open));
                        open.close();
                        obj4.setColor(new float[] { 1f, 1f, 1f, 1f });
                        obj4.setPosition(new float[] { 0f, -2f, 0f });
                        addObject(obj4);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                    // test loading object
                    try {
                        // this has no color array
                        Object3DData obj51 = Object3DBuilder.loadV5(parent, Uri.parse("assets://assets/models/teapot.obj"));
                        obj51.setPosition(new float[] { -2f, 0f, 0f });
                        obj51.setColor(new float[] { 1.0f, 1.0f, 0f, 1.0f });
                        addObject(obj51);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                    // test loading object with materials
                    try {
                        // this has color array
                        Object3DData obj52 = Object3DBuilder.loadV5(parent, Uri.parse("assets://assets/models/cube.obj"));
                        obj52.setPosition(new float[] { 2f, -2f, 0f });
                        obj52.setColor(new float[] { 0.0f, 1.0f, 1f, 1.0f });
                        addObject(obj52);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                    // test loading object made of polygonal faces
                    try {
                        // this has heterogeneous faces
                        Object3DData obj53 = Object3DBuilder.loadV5(parent, Uri.parse("assets://assets/models/ToyPlane.obj"));
                        InputStream open = ContentUtils.getInputStream(Uri.parse("assets://assets/models/"+obj53.getTextureFile()));
                        obj53.setTextureData(IOUtils.read(open));
                        obj53.centerAndScale(2.0f);
                        obj53.setPosition(new float[] { 2f, 0f, 0f });
                        obj53.setColor(new float[] { 1.0f, 1.0f, 1f, 1.0f });
                        // obj53.setDrawMode(GLES20.GL_TRIANGLE_FAN);
                        addObject(obj53);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }

                    // test loading object without normals
                    /*try {
                        Object3DData obj = Object3DBuilder.loadV5(parent, Uri.parse("assets://assets/models/cube4.obj"));
                        obj.setPosition(new float[] { 0f, 2f, -2f });
                        obj.setColor(new float[] { 0.3f, 0.52f, 1f, 1.0f });
                        addObject(obj);
                    } catch (Exception ex) {
                        errors.add(ex);
                    }*/
                } catch (Exception ex) {
                    errors.add(ex);
                } finally{
                    ContentUtils.setThreadActivity(null);
                    ContentUtils.clearDocumentsProvided();
                }
                return null;
            }

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				if (!errors.isEmpty()) {
					StringBuilder msg = new StringBuilder("There was a problem loading the data");
					for (Exception error : errors) {
						Log.e("Example", error.getMessage(), error);
						msg.append("\n" + error.getMessage());
					}
					Toast.makeText(parent.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute();

        // test loading collada object
        /*try {
            // this has heterogeneous faces
            new ColladaLoaderTask(parent, Uri.parse("assets://assets/models/cowboy.dae"), this).execute();

        } catch (Exception ex) {
            Log.e("Example",ex.getMessage(),ex);
            //errors.add(ex);
        } finally {
            ContentUtils.setThreadActivity(null);
            ContentUtils.clearDocumentsProvided();
        }*/
	}
}
