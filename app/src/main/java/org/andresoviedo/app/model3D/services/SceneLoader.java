package org.andresoviedo.app.model3D.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.animation.Animator;
import org.andresoviedo.app.model3D.collision.CollisionDetection;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DBuilder.Callback;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.view.ModelActivity;
import org.andresoviedo.app.util.url.android.Handler;
import org.apache.commons.io.IOUtils;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class SceneLoader {

	/**
	 * Default model color: yellow
	 */
	private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};
	/**
	 * Parent component
	 */
	protected final ModelActivity parent;
	/**
	 * List of data objects containing info for building the opengl objects
	 */
	private List<Object3DData> objects = new ArrayList<Object3DData>();
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
	private boolean drawNormals = false;
	/**
	 * Whether to draw using textures
	 */
	private boolean drawTextures = true;
	/**
	 * Light toggle feature: we have 3 states: no light, light, light + rotation
	 */
	private boolean rotatingLight = true;
	/**
	 * Light toggle feature: whether to draw using lights
	 */
	private boolean drawLighting = true;
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

	public SceneLoader(ModelActivity main) {
		this.parent = main;
	}

	public void init() {

		// Load object
		if (parent.getParamFile() != null || parent.getParamAssetDir() != null) {

			// Initialize assets url handler
			Handler.assets = parent.getAssets();
			// Handler.classLoader = parent.getClassLoader(); (optional)
			// Handler.androidResources = parent.getResources(); (optional)

			// Create asset url
			final URL url;
			try {
				if (parent.getParamFile() != null) {
					url = parent.getParamFile().toURI().toURL();
				} else {
					url = new URL("android://org.andresoviedo.dddmodel2/assets/" + parent.getParamAssetDir() + File.separator + parent.getParamAssetFilename());

				}
			} catch (MalformedURLException e) {
				Log.e("SceneLoader", e.getMessage(), e);
				throw new RuntimeException(e);
			}

			Object3DBuilder.loadV6AsyncParallel(parent, url, parent.getParamFile(), parent.getParamAssetDir(),
					parent.getParamAssetFilename(), new Callback() {

						long startTime = SystemClock.uptimeMillis();

						@Override
						public void onBuildComplete(List<Object3DData> datas) {
							for (Object3DData data : datas) {
								loadTexture(data, parent.getParamFile(), parent.getParamAssetDir());
							}
							final String elapsed = (SystemClock.uptimeMillis() - startTime)/1000+" secs";
							makeToastText("Load complete ("+elapsed+")", Toast.LENGTH_LONG);
						}

						@Override
						public void onLoadComplete(List<Object3DData> datas) {
							for (Object3DData data : datas) {
								addObject(data);
							}
						}

						@Override
						public void onLoadError(Exception ex) {
							Log.e("SceneLoader",ex.getMessage(),ex);
							Toast.makeText(parent.getApplicationContext(),
									"There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG)
									.show();
						}
					});
		}
	}

	private void makeToastText(final String text, final int toastDuration) {
		parent.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(parent.getApplicationContext(), text, toastDuration).show();
			}
		});
	}

	public Object3DData getLightBulb() {
		return lightPoint;
	}

	public float[] getLightPosition(){
		return lightPosition;
	}

	/**
	 * Hook for animating the objects before the rendering
	 */
	public void onDrawFrame(){

		animateLight();

		if (objects.isEmpty()) return;

		for (Object3DData obj : objects) {
			animator.update(obj);
		}
	}

	private void animateLight() {
		if (!rotatingLight) return;

		// animate light - Do a complete rotation every 5 seconds.
		long time = SystemClock.uptimeMillis() % 5000L;
		float angleInDegrees = (360.0f / 5000.0f) * ((int) time);
		lightPoint.setRotationY(angleInDegrees);
	}

	protected synchronized void addObject(Object3DData obj) {
		List<Object3DData> newList = new ArrayList<Object3DData>(objects);
		newList.add(obj);
		this.objects = newList;
		requestRender();
	}

	private void requestRender() {
		parent.getgLView().requestRender();
	}

	public synchronized List<Object3DData> getObjects() {
		return objects;
	}

	public void toggleWireframe() {
		if (this.drawWireframe && !this.drawingPoints) {
			this.drawWireframe = false;
			this.drawingPoints = true;
			makeToastText("Points", Toast.LENGTH_SHORT);
		}
		else if (this.drawingPoints){
			this.drawingPoints = false;
			makeToastText("Faces", Toast.LENGTH_SHORT);
		}
		else {
			makeToastText("Wireframe", Toast.LENGTH_SHORT);
			this.drawWireframe = true;
		}
		requestRender();
	}

	public boolean isDrawWireframe() {
		return this.drawWireframe;
	}

	public boolean isDrawPoints() {
		return this.drawingPoints;
	}

	public void toggleBoundingBox() {
		this.drawBoundingBox = !drawBoundingBox;
		requestRender();
	}

	public boolean isDrawBoundingBox() {
		return drawBoundingBox;
	}

	public boolean isDrawNormals() {
		return drawNormals;
	}

	public void toggleTextures() {
		this.drawTextures = !drawTextures;
	}

	public void toggleLighting() {
		if (this.drawLighting && this.rotatingLight){
			this.rotatingLight = false;
			makeToastText("Light stopped", Toast.LENGTH_SHORT);
		}
		else if (this.drawLighting && !this.rotatingLight){
			this.drawLighting = false;
			makeToastText("Lights off", Toast.LENGTH_SHORT);
		}
		else {
			this.drawLighting = true;
			this.rotatingLight = true;
			makeToastText("Light on", Toast.LENGTH_SHORT);
		}
		requestRender();
	}

	public boolean isDrawTextures() {
		return drawTextures;
	}

	public boolean isDrawLighting() {
		return drawLighting;
	}

	public Object3DData getSelectedObject() {
		return selectedObject;
	}

	public void setSelectedObject(Object3DData selectedObject) {
		this.selectedObject = selectedObject;
	}

	public void loadTexture(Object3DData data, File file, String parentAssetsDir){
		if (data.getTextureData() == null && data.getTextureFile() != null){
			try {
				Log.i("SceneLoader","Loading texture '"+data.getTextureFile()+"'...");
				InputStream stream = null;
				if (file != null){
					File textureFile = new File(file.getParent(),data.getTextureFile());
					stream = new FileInputStream(textureFile);
				}
				else{
					stream = parent.getAssets().open(parentAssetsDir + "/" + data.getTextureFile());
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				IOUtils.copy(stream,bos);
				stream.close();

				data.setTextureData(bos.toByteArray());
			} catch (IOException ex) {
				makeToastText("Problem loading texture "+data.getTextureFile(), Toast.LENGTH_SHORT);
			}
		}
	}

	public void loadTexture(Object3DData obj, URL path){
		if (obj == null && objects.size() != 1) {
			makeToastText("Unavailable", Toast.LENGTH_SHORT);
			return;
		}

		try {
			InputStream is = path.openStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOUtils.copy(is,bos);
			is.close();

			obj = obj != null? obj : objects.get(0);
			obj.setTextureData(bos.toByteArray());
		} catch (IOException ex) {
			makeToastText("Problem loading texture: "+ex.getMessage(), Toast.LENGTH_SHORT);
		}
	}

	public void processTouch(float x, float y) {
		Object3DData objectToSelect = CollisionDetection.getBoxIntersection(getObjects(), parent.getgLView().getModelRenderer(), x, y);
		if (objectToSelect != null) {
			if (getSelectedObject() == objectToSelect) {
				Log.i("SceneLoader", "Unselected object " + objectToSelect.getId());
				setSelectedObject(null);
			} else {
				Log.i("SceneLoader", "Selected object " + objectToSelect.getId());
				setSelectedObject(objectToSelect);
			}
			float[] point = CollisionDetection.getTriangleIntersection2(getObjects(), parent.getgLView().getModelRenderer(), x, y);
			if (point != null) {
				addObject(Object3DBuilder.buildPoint(point).setColor(new float[]{1.0f,0f,0f,1f}));
			}
		}
	}
}
