// WavefrontLoader.java
// Andrew Davison, February 2007, ad@fivedots.coe.psu.ac.th

/* Load the OBJ model from MODEL_DIR, centering and scaling it.
 The scale comes from the sz argument in the constructor, and
 is implemented by changing the vertices of the loaded model.

 The model can have vertices, normals and tex coordinates, and
 refer to materials in a MTL file.

 The OpenGL commands for rendering the model are stored in 
 a display list (modelDispList), which is drawn by calls to
 draw().

 Information about the model is printed to stdout.

 Based on techniques used in the OBJ loading code in the
 JautOGL multiplayer racing game by Evangelos Pournaras 
 (http://today.java.net/pub/a/today/2006/10/10/
 development-of-3d-multiplayer-racing-game.html 
 and https://jautogl.dev.java.net/), and the 
 Asteroids tutorial by Kevin Glass 
 (http://www.cokeandcode.com/asteroidstutorial)

 CHANGES (Feb 2007)
 - a global flipTexCoords boolean
 - drawToList() sets and uses flipTexCoords
 */

package org.andresoviedo.android_3d_model_engine.services.wavefront;

import android.net.Uri;
import android.opengl.GLES20;
import android.support.annotation.Nullable;
import android.util.Log;

import org.andresoviedo.util.android.ContentUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class WavefrontLoader {

	private static final float DUMMY_Z_TC = -5.0f;
	static final boolean INDEXES_START_AT_1 = true;
	private boolean hasTCs3D = false;

	private ArrayList<Tuple3> texCoords;
	// whether the model uses 3D or 2D tex coords
	// whether tex coords should be flipped around the y-axis

	private Faces faces; // model faces
	private FaceMaterials faceMats; // materials used by faces
	private Materials materials; // materials defined in MTL file
	private ModelDimensions modelDims; // model dimensions

	private String modelNm; // without path or ".OBJ" extension
	private float maxSize; // for scaling the model

	// metadata
	int numVerts = 0;
	int numTextures = 0;
	int numNormals = 0;
	int numFaces = 0;
	int numVertsReferences = 0;

	// buffers
	private FloatBuffer vertsBuffer;
	private FloatBuffer normalsBuffer;
	// TODO: build texture data directly into this buffer
	private FloatBuffer textureCoordsBuffer;

	// flags
	private static final int triangleMode = GLES20.GL_TRIANGLE_FAN;

	public WavefrontLoader(String nm) {
		modelNm = nm;
		maxSize = 1.0F;

		texCoords = new ArrayList<Tuple3>();


		faceMats = new FaceMaterials();
		modelDims = new ModelDimensions();
	} // end of initModelData()

	public FloatBuffer getVerts() {
		return vertsBuffer;
	}

	public FloatBuffer getNormals() {
		return normalsBuffer;
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

	public ModelDimensions getDimensions() {
		return modelDims;
	}

	@Nullable
	public static String getMaterialLib(Uri uri){
		return getParameter(uri, "mtllib ");
	}

	@Nullable
	public static String getTextureFile(Uri uri){
		return getParameter(uri, "map_Kd ");
	}

	@Nullable
	private static String getParameter(Uri uri, String parameter) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(ContentUtils.getInputStream(uri)))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith(parameter)) {
					return line.substring(parameter.length());
				}
			}
		} catch (IOException e) {
			Log.e("WavefrontLoader", "Problem reading file '" + uri + "': " + e.getMessage(), e);
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * Count verts, normals, faces etc and reserve buffers to save the data.
	 * @param is data source
	 */
	public void analyzeModel(InputStream is) {
		int lineNum = 0;
		String line;


		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));

			while ((line = br.readLine()) != null) {
				lineNum++;
				line = line.trim();
				if (line.length() > 0) {

					if (line.startsWith("v ")) { // vertex
						numVerts++;
					} else if (line.startsWith("vt")) { // tex coord
						numTextures++;
					} else if (line.startsWith("vn")) {// normal
						numNormals++;
					} else if (line.startsWith("f ")) { // face
						final int faceSize;
						if (line.contains("  ")) {
							faceSize = line.split(" +").length - 1;
						} else {
							faceSize = line.split(" ").length - 1;
						}
						numFaces += (faceSize - 2);
						// (faceSize-2)x3 = converting polygon to triangles
						numVertsReferences += (faceSize - 2) * 3;
					} else if (line.startsWith("mtllib ")) // build material
					{
						materials = new Materials(line.substring(7));
					} else if (line.startsWith("usemtl ")) {// use material
					} else if (line.charAt(0) == 'g') { // group name
						// not implemented
					} else if (line.charAt(0) == 's') { // smoothing group
						// not implemented
					} else if (line.charAt(0) == '#') // comment line
						continue;
					else if (line.charAt(0) == 'o') // object group
						continue;
					else
						System.out.println("Ignoring line " + lineNum + " : " + line);
				}
			}
		} catch (IOException e) {
			Log.e("WavefrontLoader", "Problem reading line '" + (++lineNum) + "'");
			Log.e("WavefrontLoader", e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.e("WavefrontLoader", e.getMessage(), e);
				}
			}
		}

		Log.i("WavefrontLoader","Number of vertices:"+numVerts);
		Log.i("WavefrontLoader","Number of faces:"+numFaces);
	}

	/**
	 * Allocate buffers for pushing the model data
	 * TODO: use textureCoordsBuffer
	 */
	public void allocateBuffers() {
		// size = 3 (x,y,z) * 4 (bytes per float)
		vertsBuffer = createNativeByteBuffer(numVerts*3*4).asFloatBuffer();
		if (numNormals > 0) {
			normalsBuffer = createNativeByteBuffer(numNormals * 3 * 4).asFloatBuffer();
		}
		textureCoordsBuffer = createNativeByteBuffer(numTextures*3*4).asFloatBuffer();
		if (numFaces > 0) {
			IntBuffer buffer = createNativeByteBuffer(numFaces * 3 * 4).asIntBuffer();
			faces = new Faces(numFaces, buffer, vertsBuffer, normalsBuffer, texCoords);
		}
	}

	public void loadModel(InputStream is) {
		// String fnm = MODEL_DIR + modelNm + ".obj";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			readModel(br);
		} finally{
			if (br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	private void readModel(BufferedReader br)
	// parse the OBJ file line-by-line
	{
		boolean isLoaded = true; // hope things will go okay

		int lineNum = 0;
		String line;
		boolean isFirstCoord = true;
		boolean isFirstTC = true;
		int numFaces = 0;

		int vertNumber = 0;
		int normalNumber = 0;


		try {
			while (((line = br.readLine()) != null)) {
				lineNum++;
				line = line.trim();
				if (line.length() > 0) {

					if (line.startsWith("v ")) { // vertex
						isLoaded = addVert(vertsBuffer, vertNumber++ * 3, line, isFirstCoord, modelDims) && isLoaded;
						if (isFirstCoord)
							isFirstCoord = false;
					} else if (line.startsWith("vt")) { // tex coord
						isLoaded = addTexCoord(line, isFirstTC) && isLoaded;
						if (isFirstTC)
							isFirstTC = false;
					} else if (line.startsWith("vn")) // normal
						isLoaded = addVert(normalsBuffer, normalNumber++ * 3,line, isFirstCoord, null) && isLoaded;
					else if (line.startsWith("f ")) { // face
						isLoaded = faces.addFace(line) && isLoaded;
						numFaces++;
					} else if (line.startsWith("mtllib ")) // build material
					{
						// materials = new Materials(new File(modelFile.getParent(),
						// line.substring(7)).getAbsolutePath());
						// materials = new Materials(line.substring(7));
					} else if (line.startsWith("usemtl ")) // use material
						faceMats.addUse(numFaces, line.substring(7));
					else if (line.charAt(0) == 'g') { // group name
						// not implemented
					} else if (line.charAt(0) == 's') { // smoothing group
						// not implemented
					} else if (line.charAt(0) == '#') // comment line
						continue;
					else if (line.charAt(0) == 'o') // object group
						continue;
					else
						System.out.println("Ignoring line " + lineNum + " : " + line);
				}
			}
		} catch (IOException e) {
			Log.e("WavefrontLoader",e.getMessage(),e);
			throw new RuntimeException(e);
		}

		if (!isLoaded) {
			Log.e("WavefrontLoader","Error loading model");
			// throw new RuntimeException("Error loading model");
		}
	} // end of readModel()

	/**
	 * Parse the vertex and add it to the buffer. If the vertex cannot be parsed,
	 * then a default (0,0,0) vertex is added instead.
	 *
	 * @param buffer the buffer where the vertex is to be added
	 * @param offset the offset of the buffer
	 * @param line the vertex to parse
	 * @param isFirstCoord if this is the first vertex to be parsed
	 * @param dimensions the model dimesions so they are updated (TODO move this out of this method)
	 * @return <code>true</code> if the vertex could be parsed, <code>false</code> otherwise
	 */
	private boolean addVert(FloatBuffer buffer, int offset, String line, boolean isFirstCoord, ModelDimensions dimensions)
	/*
	 * Add vertex from line "v x y z" to vert ArrayList, and update the model dimension's info.
	 */
	{
		float x=0,y=0,z=0;
		try{
			String[] tokens = null;
			if (line.contains("  ")){
				tokens = line.split(" +");
			}
			else{
				tokens = line.split(" ");
			}
			x = Float.parseFloat(tokens[1]);
			y = Float.parseFloat(tokens[2]);
			z = Float.parseFloat(tokens[3]);

			if (dimensions != null) {
				if (isFirstCoord)
					modelDims.set(x, y, z);
				else
					modelDims.update(x, y, z);
			}

			return true;

		}catch(NumberFormatException ex){
			Log.e("WavefrontLoader",ex.getMessage());
		} finally{
			// try to build even with errors
			buffer.put(offset, x).put(offset+1, y).put(offset+2, z);
		}

		return false;
	} // end of addVert()

	private boolean addTexCoord(String line, boolean isFirstTC)
	/*
	 * Add the texture coordinate from the line "vt u v w" to the texCoords ArrayList. There may only be two tex coords
	 * on the line, which is determined by looking at the first tex coord line.
	 */
	{
		if (isFirstTC) {
			hasTCs3D = checkTC3D(line);
			System.out.println("Using 3D tex coords: " + hasTCs3D);
		}

		Tuple3 texCoord = readTCTuple(line);
		if (texCoord != null) {
			texCoords.add(texCoord);
			return true;
		}

		return false;
	} // end of addTexCoord()

	private boolean checkTC3D(String line)
	/*
	 * Check if the line has 4 tokens, which will be the "vt" token and 3 tex coords in this case.
	 */
	{
		String[] tokens = line.split("\\s+");
		return (tokens.length == 4);
	} // end of checkTC3D()

	private Tuple3 readTCTuple(String line)
	/*
	 * The line starts with a "vt" OBJ word and two or three floats (x, y, z) for the tex coords separated by spaces. If
	 * there are only two coords, then the z-value is assigned a dummy value, DUMMY_Z_TC.
	 */
	{
		StringTokenizer tokens = new StringTokenizer(line, " ");
		tokens.nextToken(); // skip "vt" OBJ word

		try {
			float x = Float.parseFloat(tokens.nextToken());
			float y = Float.parseFloat(tokens.nextToken());

			float z = DUMMY_Z_TC;
			if (hasTCs3D)
				z = Float.parseFloat(tokens.nextToken());
			return new Tuple3(x, y, z);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}

		return null; // means an error occurred
	} // end of readTCTuple()

	public void reportOnModel() {
		Log.i("WavefrontLoader","No. of vertices: " + vertsBuffer.capacity()/3);
		Log.i("WavefrontLoader","No. of normal coords: " + numNormals);
		Log.i("WavefrontLoader","No. of tex coords: " + numTextures);
		Log.i("WavefrontLoader","No. of faces: " + numFaces);

		modelDims.reportDimensions();
		// dimensions of model (before centering and scaling)

		if (materials != null)
			materials.showMaterials(); // list defined materials
		faceMats.showUsedMaterials(); // show what materials have been used by
		// faces
	} // end of reportOnModel()

	public static class Tuple3 {
		private float x, y, z;

		public Tuple3(float xc, float yc, float zc) {
			x = xc;
			y = yc;
			z = zc;
		}

		public String toString() {
			return "( " + x + ", " + y + ", " + z + " )";
		}

		public void setX(float xc) {
			x = xc;
		}

		public float getX() {
			return x;
		}

		public void setY(float yc) {
			y = yc;
		}

		public float getY() {
			return y;
		}

		public void setZ(float zc) {
			z = zc;
		}

		public float getZ() {
			return z;
		}

	} // end of Tuple3 class

	public static class ModelDimensions {
		// edge coordinates
		public float leftPt, rightPt; // on x-axis
		public float topPt, bottomPt; // on y-axis
		public float farPt, nearPt; // on z-axis

		// for reporting
		private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

		public ModelDimensions() {
			leftPt = 0.0f;
			rightPt = 0.0f;
			topPt = 0.0f;
			bottomPt = 0.0f;
			farPt = 0.0f;
			nearPt = 0.0f;
		} // end of ModelDimensions()

		public void set(float x, float y, float z)
		// initialize the model's edge coordinates
		{
			rightPt = x;
			leftPt = x;

			topPt = y;
			bottomPt = y;

			nearPt = z;
			farPt = z;
		} // end of set()

		public void update(float x, float y, float z)
		// update the edge coordinates using vert
		{
			if (x > rightPt)
				rightPt = x;
			if (x < leftPt)
				leftPt = x;

			if (y > topPt)
				topPt = y;
			if (y < bottomPt)
				bottomPt = y;

			if (z > nearPt)
				nearPt = z;
			if (z < farPt)
				farPt = z;
		} // end of update()

		// ------------- use the edge coordinates ----------------------------

		public float getWidth() {
			return (rightPt - leftPt);
		}

		public float getHeight() {
			return (topPt - bottomPt);
		}

		public float getDepth() {
			return (nearPt - farPt);
		}

		public float getLargest() {
			float height = getHeight();
			float depth = getDepth();

			float largest = getWidth();
			if (height > largest)
				largest = height;
			if (depth > largest)
				largest = depth;

			return largest;
		} // end of getLargest()

		public Tuple3 getCenter() {
			float xc = (rightPt + leftPt) / 2.0f;
			float yc = (topPt + bottomPt) / 2.0f;
			float zc = (nearPt + farPt) / 2.0f;
			return new Tuple3(xc, yc, zc);
		} // end of getCenter()

		public void reportDimensions() {
			Tuple3 center = getCenter();

			System.out.println("x Coords: " + df.format(leftPt) + " to " + df.format(rightPt));
			System.out.println("  Mid: " + df.format(center.getX()) + "; Width: " + df.format(getWidth()));

			System.out.println("y Coords: " + df.format(bottomPt) + " to " + df.format(topPt));
			System.out.println("  Mid: " + df.format(center.getY()) + "; Height: " + df.format(getHeight()));

			System.out.println("z Coords: " + df.format(nearPt) + " to " + df.format(farPt));
			System.out.println("  Mid: " + df.format(center.getZ()) + "; Depth: " + df.format(getDepth()));
		} // end of reportDimensions()

	} // end of ModelDimensions class

	public static class Materials {

		public Map<String, Material> materials;
		// stores the Material objects built from the MTL file data

		// private File file;
		public String mfnm;

		private Materials(String mtlFnm) {
			// TODO: this map is now linked because we want to get only the first texture
			// when multiple textures are supported, change this to be a simple HashMap
			materials = new LinkedHashMap<>();

			this.mfnm = mtlFnm;
			// file = new File(mtlFnm);
		}

		public void readMaterials(BufferedReader br)
		/*
		 * Parse the MTL file line-by-line, building Material objects which are collected in the materials ArrayList.
		 */
		{
			Log.v("materials", "Reading material...");
			try {
				String line;
				Material currMaterial = null; // current material

				while (((line = br.readLine()) != null)) {
					line = line.trim();
					if (line.length() == 0)
						continue;

					if (line.startsWith("newmtl ")) { // new material
						if (currMaterial != null) // save previous material
							materials.put(currMaterial.getName(), currMaterial);

						// start collecting info for new material
						String name = line.substring(7);
						Log.d("Loader", "New material found: " + name);
						currMaterial = new Material(name);
					} else if (line.startsWith("map_Kd ")) { // texture filename
						// String fileName = new File(file.getParent(), line.substring(7)).getAbsolutePath();
						String textureFilename = line.substring(7);
						Log.d("Loader", "New texture found: " + textureFilename);
						currMaterial.setTexture(textureFilename);
					} else if (line.startsWith("Ka ")) // ambient colour
						currMaterial.setKa(readTuple3(line));
					else if (line.startsWith("Kd ")) // diffuse colour
						currMaterial.setKd(readTuple3(line));
					else if (line.startsWith("Ks ")) // specular colour
						currMaterial.setKs(readTuple3(line));
					else if (line.startsWith("Ns ")) { // shininess
						float val = Float.valueOf(line.substring(3)).floatValue();
						currMaterial.setNs(val);
					} else if (line.charAt(0) == 'd') { // alpha
						float val = Float.valueOf(line.substring(2)).floatValue();
						currMaterial.setD(val);
					} else if (line.startsWith("Tr ")) { // Transparency (inverted)
						float val = Float.valueOf(line.substring(3)).floatValue();
						currMaterial.setD(1 - val);
					} else if (line.startsWith("illum ")) { // illumination model
						// not implemented
					} else if (line.charAt(0) == '#') // comment line
						continue;
					else
						System.out.println("Ignoring MTL line: " + line);

				}
				if (currMaterial != null) {
					materials.put(currMaterial.getName(), currMaterial);
				}
			} catch (Exception e) {
				Log.e("materials", e.getMessage(), e);
			}
		} // end of readMaterials()

		private Tuple3 readTuple3(String line)
		/*
		 * The line starts with an MTL word such as Ka, Kd, Ks, and the three floats (x, y, z) separated by spaces
		 */
		{
			StringTokenizer tokens = new StringTokenizer(line, " ");
			tokens.nextToken(); // skip MTL word

			try {
				float x = Float.parseFloat(tokens.nextToken());
				float y = Float.parseFloat(tokens.nextToken());
				float z = Float.parseFloat(tokens.nextToken());

				return new Tuple3(x, y, z);
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			}

			return null; // means an error occurred
		} // end of readTuple3()

		// list all the Material objects
		public void showMaterials(){
			if (materials == null || materials.isEmpty()){
				Log.i("WavefrontLoader","No materials available");
				return;
			}
			Log.i("WavefrontLoader","No. of materials: " + materials.size());
			for (Material m : materials.values()) {
				m.showMaterial();
				// System.out.println();
			}
		} // end of showMaterials()

		public Material getMaterial(String name) {
			return materials.get(name);
		}

	} // end of Materials class

	public static class Material {
		private String name;

		// colour info
		private Tuple3 ka, kd, ks; // ambient, diffuse, specular colours
		private float ns; // shininess
		private float d; // alpha

		// texture info
		private String texFnm;
		private String texture;

		public Material(String nm) {
			name = nm;

			d = 1.0f;
			ns = 0.0f;
			ka = null;
			kd = null;
			ks = null;

			texFnm = null;
			texture = null;
		} // end of Material()

		public void showMaterial() {
			System.out.println(name);
			if (ka != null)
				System.out.println("  Ka: " + ka.toString());
			if (kd != null)
				System.out.println("  Kd: " + kd.toString());
			if (ks != null)
				System.out.println("  Ks: " + ks.toString());
			if (ns != 0.0f)
				System.out.println("  Ns: " + ns);
			if (d != 1.0f)
				System.out.println("  d: " + d);
			if (texFnm != null)
				System.out.println("  Texture file: " + texFnm);
		} // end of showMaterial()

		public boolean hasName(String nm) {
			return name.equals(nm);
		}

		// --------- set/get methods for colour info --------------

		public void setD(float val) {
			d = val;
		}

		public float getD() {
			return d;
		}

		public void setNs(float val) {
			ns = val;
		}

		public float getNs() {
			return ns;
		}

		public void setKa(Tuple3 t) {
			ka = t;
		}

		public Tuple3 getKa() {
			return ka;
		}

		public void setKd(Tuple3 t) {
			kd = t;
		}

		public Tuple3 getKd() {
			return kd;
		}

		public float[] getKdColor() {
			if (kd == null) {
				return null;
			}
			return new float[] { kd.getX(), kd.getY(), kd.getZ(), getD() };
		}

		public void setKs(Tuple3 t) {
			ks = t;
		}

		public Tuple3 getKs() {
			return ks;
		}

		public void setMaterialColors(GLES20 gl)
		// start rendering using this material's colour information
		{
			// if (ka != null) { // ambient color
			// float[] colorKa = {ka.getX(), ka.getY(), ka.getZ(), 1.0f};
			// gl.glMaterialfv(GLES20.GL_FRONT_AND_BACK, GLES20.GL_AMBIENT, colorKa, 0);
			// }
			// if (kd != null) { // diffuse color
			// float[] colorKd = {kd.getX(), kd.getY(), kd.getZ(), 1.0f};
			// gl.glMaterialfv(GLES20.GL_FRONT_AND_BACK, GLES20.GL_DIFFUSE, colorKd, 0);
			// }
			// if (ks != null) { // specular color
			// float[] colorKs = {ks.getX(), ks.getY(), ks.getZ(), 1.0f};
			// gl.glMaterialfv(GLES20.GL_FRONT_AND_BACK, GLES20.GL_SPECULAR, colorKs, 0);
			// }
			//
			// if (ns != 0.0f) { // shininess
			// gl.glMaterialf(GLES20.GL_FRONT_AND_BACK, GLES20.GL_SHININESS, ns);
			// }
			//
			// if (d != 1.0f) { // alpha
			// // not implemented
			// }
		} // end of setMaterialColors()

		// --------- set/get methods for texture info --------------

		public void setTexture(String t) {
			texture = t;
		}

		public String getTexture() {
			return texture;
		}

		String getName() {
			return name;
		}

	} // end of Material class

	public static class Faces {
		private static final float DUMMY_Z_TC = -5.0f;

		public final int totalFaces;
		/**
		 * indices for verticesused by each face
		 */
		public IntBuffer facesVertIdxs;
		/**
		 * indices for tex coords used by each face
		 */
		public ArrayList<int[]> facesTexIdxs;
		/**
		 * indices for normals used by each face
		 */
		public ArrayList<int[]> facesNormIdxs;

		private FloatBuffer normals;
		private ArrayList<Tuple3> texCoords;

		// Total number of vertices references. That is, each face references 3 or more vectors. This is the sum for all
		// faces
		private int facesLoadCounter;
		private int faceVertexLoadCounter = 0;
		private int verticesReferencesCount;

		// for reporting
		// private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

		public Faces(int numFaces){
			this.totalFaces = numFaces;
			this.facesLoadCounter = numFaces;
		}

		Faces(int totalFaces, IntBuffer buffer, FloatBuffer vs, FloatBuffer ns, ArrayList<Tuple3> ts) {
			this.totalFaces = totalFaces;
			normals = ns;
			texCoords = ts;

			facesVertIdxs = buffer;
			facesTexIdxs = new ArrayList<int[]>();
			facesNormIdxs = new ArrayList<int[]>();
		} // end of Faces()

		public int getSize(){
			return totalFaces;
		}

		/**
		 * @return <code>true</code> if all faces are loaded
		 */
		public boolean loaded(){
			return facesLoadCounter == totalFaces;
		}

		/**
		 * get this face's indicies from line "f v/vt/vn ..." with vt or vn index values perhaps being absent.
		 */
		public boolean addFace(String line) {
			try {
				line = line.substring(2); // skip the "f "
				String[] tokens = null;

				// cpu optimization
				if (line.contains("  ")){
					tokens = line.split(" +");
				}
				else{
					tokens = line.split(" ");
				}

				int numTokens = tokens.length; // number of v/vt/vn tokens
				// create arrays to hold the v, vt, vn indicies

				int vt[] = null;
				int vn[] = null;


				for (int i = 0, faceIndex = 0; i < numTokens; i++, faceIndex++) {

					// convert to triangles all polygons
					if (faceIndex > 2){
						// Converting polygon to triangle
						faceIndex = 0;

						facesLoadCounter++;
						verticesReferencesCount += 3;
						if (vt != null)  facesTexIdxs.add(vt);
						if (vn != null) facesNormIdxs.add(vn);

						vt = null;
						vn = null;

						i -= 2;
					}

					// convert to triangles all polygons
					String faceToken = null;
					if (WavefrontLoader.triangleMode == GLES20.GL_TRIANGLE_FAN) {
						if (faceIndex == 0){
							// In FAN mode all faces shares the initial vertex
							faceToken = tokens[0];// get a v/vt/vn
						}else{
							faceToken = tokens[i]; // get a v/vt/vn
						}
					}
					else {
						// GL.GL_TRIANGLES | GL.GL_TRIANGLE_STRIP
						faceToken = tokens[i]; // get a v/vt/vn
					}
					// token
					// System.out.println(faceToken);

					String[] faceTokens = faceToken.split("/");
					int numSeps = faceTokens.length; // how many '/'s are there in
					// the token

					int vertIdx = Integer.parseInt(faceTokens[0]);
					/*if (vertIdx > 65535){
						Log.e("WavefrontLoader","Ignoring face because its out of range (>65535)");
						continue;
					}*/
					if (numSeps > 1){
						if (vt == null)	vt = new int[3];
						try{
							vt[faceIndex] = Integer.parseInt(faceTokens[1]);
						}catch(NumberFormatException ex){
							vt[faceIndex] = 0;
						}
					}
					if (numSeps > 2){
						if (vn == null)	vn = new int[3];
						try{
							vn[faceIndex] = Integer.parseInt(faceTokens[2]);
						}catch(NumberFormatException ex){
							vn[faceIndex] = 0;
						}
					}
					// add 0's if the vt or vn index values are missing;
					// 0 is a good choice since real indices start at 1

					if (WavefrontLoader.INDEXES_START_AT_1) {
						vertIdx--;
						if (vt != null)	vt[faceIndex] = vt[faceIndex] - 1;
						if (vn != null) vn[faceIndex] = vn[faceIndex] - 1;
					}
					// store the indices for this face
					facesVertIdxs.put(faceVertexLoadCounter++,vertIdx);
				}
				if (vt != null)  facesTexIdxs.add(vt);
				if (vn != null) facesNormIdxs.add(vn);

				facesLoadCounter++;
				verticesReferencesCount += 3;

			} catch (NumberFormatException e) {
				Log.e("WavefrontLoader",e.getMessage(),e);
				return false;
			}
			return true;
		}


		public int getVerticesReferencesCount() {
			// we have only triangles
			return getSize()*3;
		}

		public IntBuffer getIndexBuffer(){return facesVertIdxs;}

	} // end of Faces class

	public static class FaceMaterials {
		// the face index (integer) where a material is first used
		private HashMap<Integer, String> faceMats;

		// for reporting
		private HashMap<String, Integer> matCount;

		// how many times a material (string) is used

		public FaceMaterials() {
			faceMats = new HashMap<Integer, String>();
			matCount = new HashMap<String, Integer>();
		} // end of FaceMaterials()

		public void addUse(int faceIdx, String matName) {
			// store the face index and the material it uses
			if (faceMats.containsKey(faceIdx)) // face index already present
				System.out.println("Face index " + faceIdx + " changed to use material " + matName);
			faceMats.put(faceIdx, matName);

			// store how many times matName has been used by faces
			if (matCount.containsKey(matName)) {
				int i = (Integer) matCount.get(matName) + 1;
				matCount.put(matName, i);
			} else
				matCount.put(matName, 1);
		} // end of addUse()

		public String findMaterial(int faceIdx) {
			return (String) faceMats.get(faceIdx);
		}

		public void showUsedMaterials()
		/*
		 * List all the materials used by faces, and the number of faces that have used them.
		 */
		{
			System.out.println("No. of materials used: " + faceMats.size());

			// build an iterator of material names
			Set<String> keys = matCount.keySet();
			Iterator<String> iter = keys.iterator();

			// cycle through the hashmap showing the count for each material
			String matName;
			int count;
			while (iter.hasNext()) {
				matName = iter.next();
				count = (Integer) matCount.get(matName);

				System.out.print(matName + ": " + count);
				System.out.println();
			}
		} // end of showUsedMaterials()

		public boolean isEmpty() {
			return faceMats.isEmpty() || this.matCount.isEmpty();
		}

	} // end of FaceMaterials class

} // end of WavefrontLoader class
