package org.andresoviedo.app.model3D.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.view.ModelActivity;
import org.apache.commons.io.IOUtils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

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

	public void init() {
		new AsyncTask<Void, Void, Void>() {

			ProgressDialog dialog = new ProgressDialog(parent);
			List<Exception> errors = new ArrayList<Exception>();

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
					Object3DData obj1 = Object3DBuilder.buildCubeV1();
					obj1.setColor(new float[] { 1f, 0f, 0f, 0.5f });
					obj1.setPosition(new float[] { -1.5f, 1.5f, 1.5f });
					addObject(obj1);

					Object3DData obj2 = Object3DBuilder.buildSquareV2();
					obj2.setColor(new float[] { 0f, 1f, 0, 0.5f });
					obj2.setPosition(new float[] { 1.5f, 1.5f, 1.5f });
					addObject(obj2);

					InputStream open = null;
					try {
						open = parent.getAssets().open("models/penguin.bmp");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						IOUtils.copy(open, baos);
						baos.close();

						Object3DData obj3 = Object3DBuilder.buildCubeV3(baos.toByteArray());
						obj3.setColor(new float[] { 1f, 1f, 1f, 1f });
						obj3.setPosition(new float[] { -1.5f, -1.5f, 1.5f });
						addObject(obj3);
					} catch (Exception ex) {
						errors.add(ex);
					} finally {
						if (open != null) {
							try {
								open.close();
							} catch (IOException ex) {
							}
						}
					}

					open = null;
					try {
						open = parent.getAssets().open("models/cube.bmp");
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						IOUtils.copy(open, baos);
						baos.close();

						Object3DData obj4 = Object3DBuilder.buildCubeV4(baos.toByteArray());
						obj4.setColor(new float[] { 1f, 1f, 1f, 1f });
						obj4.setPosition(new float[] { 1.5f, -1.5f, 1.5f });
						addObject(obj4);
					} catch (Exception ex) {
						errors.add(ex);
					} finally {
						if (open != null) {
							try {
								open.close();
							} catch (IOException ex) {
							}
						}
					}

					try {
						// this has no color array
						Object3DData obj51 = Object3DBuilder.loadV5(parent.getAssets(), "models/", "teapot.obj");
						obj51.setPosition(new float[] { -1.5f, 0f, 0f });
						obj51.setColor(new float[] { 1.0f, 1.0f, 0f, 1.0f });
						addObject(obj51);
					} catch (Exception ex) {
						errors.add(ex);
					}

					try {
						// this has color array
						Object3DData obj52 = Object3DBuilder.loadV5(parent.getAssets(), "models/", "cube.obj");
						obj52.setPosition(new float[] { 0f, 0f, 0f });
						obj52.setColor(new float[] { 0.0f, 1.0f, 1f, 1.0f });
						addObject(obj52);
					} catch (Exception ex) {
						errors.add(ex);
					}

					try {
						// this has heterogeneous faces
						Object3DData obj53 = Object3DBuilder.loadV5(parent.getAssets(), "models/", "ToyPlane.obj");
						obj53.setPosition(new float[] { 1f, 0f, 0f });
						obj53.setColor(new float[] { 1.0f, 1.0f, 1f, 1.0f });
						addObject(obj53);
					} catch (Exception ex) {
						errors.add(ex);
					}
				} catch (Exception ex) {
					errors.add(ex);
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
						msg.append("\n" + error.getMessage());
					}
					Toast.makeText(parent.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				}
			}
		}.execute();
	}
}
