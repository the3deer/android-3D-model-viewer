package org.andresoviedo.app.model3D.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;

/**
 * This is the basic 3D data necessary to build the 3D object
 * 
 * @author andres
 *
 */
public class Object3DData implements Serializable {

	private static final long serialVersionUID = -6772673057499511238L;

	private final ArrayList<Tuple3> verts;
	private final ArrayList<Tuple3> normals;
	private final ArrayList<Tuple3> texCoords;
	private final Faces faces;
	private final FaceMaterials faceMats;
	private final Materials materials;

	public Object3DData(ArrayList<Tuple3> verts, ArrayList<Tuple3> normals, ArrayList<Tuple3> texCoords, Faces faces,
			FaceMaterials faceMats, Materials materials) {
		super();
		this.verts = verts;
		this.normals = normals;
		this.texCoords = texCoords;
		this.faces = faces;
		this.faceMats = faceMats;
		this.materials = materials;
	}

	public ArrayList<Tuple3> getVerts() {
		return verts;
	}

	public ArrayList<Tuple3> getNormals() {
		return normals;
	}

	public ArrayList<Tuple3> getTexCoords() {
		return texCoords;
	}

	public Faces getFaces() {
		return faces;
	}

	public FaceMaterials getFaceMats() {
		return faceMats;
	}

	public Materials getMaterials() {
		return materials;
	}

}
