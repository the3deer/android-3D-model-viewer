package org.andresoviedo.app.model3D.services;

import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DBuilder.Callback;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.view.ModelActivity;

import android.widget.Toast;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class SceneLoader {

	/**
	 * Parent component
	 */
	protected final ModelActivity parent;
	/**
	 * List of data objects containing info for building the opengl objects
	 */
	private List<Object3DData> objects = new ArrayList<Object3DData>();

	public SceneLoader(ModelActivity main) {
		this.parent = main;
	}

	public void init() {
		Object3DBuilder.loadV5Async(parent, parent.getParamFile(), parent.getParamAssetDir(),
				parent.getParamAssetFilename(), new Callback() {

					@Override
					public void onLoadComplete(Object3DData data) {
						addObject(data);

					}

					@Override
					public void onLoadError(Exception ex) {
						Toast.makeText(parent.getApplicationContext(),
								"There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
	}

	protected void addObject(Object3DData obj) {
		synchronized (objects) {
			objects.add(obj);
		}
		parent.getgLView().requestRender();
	}

	public List<Object3DData> getObjects() {
		synchronized (objects) {
			return new ArrayList<Object3DData>(objects);
		}
	}
}
