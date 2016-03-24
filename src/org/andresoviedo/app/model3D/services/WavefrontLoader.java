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

package org.andresoviedo.app.model3D.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.ObjectV3;
import org.andresoviedo.app.model3D.model.ObjectV5;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

public class WavefrontLoader {

	private static final float DUMMY_Z_TC = -5.0f;
	static final boolean INDEXES_START_AT_1 = true;
	static final boolean DRAW_ARRAYS = true;
	private boolean flipTexCoords = true;
	private boolean hasTCs3D = false;
	private static float[] DEFAULT_COLOR = { 1.0f, 1.0f, 0, 1.0f };

	// collection of vertices, normals and texture coords for the model
	private ArrayList<Tuple3> verts;
	private ArrayList<Tuple3> normals;
	private ArrayList<Tuple3> texCoords;
	// whether the model uses 3D or 2D tex coords
	// whether tex coords should be flipped around the y-axis

	private Faces faces; // model faces
	private FaceMaterials faceMats; // materials used by faces
	private Materials materials; // materials defined in MTL file
	private ModelDimensions modelDims; // model dimensions

	private String modelNm; // without path or ".OBJ" extension
	private float maxSize; // for scaling the model

	private static int lastId = 0;

	public int id = (lastId = lastId + 1);

	public boolean redraw;

	public boolean isVisible;

	// public boolean showBoundingBox = true;
	public boolean invertedNormals = false;

	public float transX, transY, transZ;

	private float[] scale = new float[] { 1.0F, 1.0F, 1.0F };

	public float rotX, rotY, rotZ;

	public float[] rot2 = new float[] { 0F, 0F, 0F };

	public float[] currentSize;

	public boolean drawSolid;

	public boolean enableLighting;

	public boolean enableTextures;

	public float[] mat_ambient = new float[] { 1.0F, 0.2F, 0.2F, 1.0F };

	public float[] mat_diffuse = new float[] { 1.0F, 1.0F, 0.0F, 1.0F };

	public float[] mat_specular = new float[] { 1.0F, 1.0F, 1.0F, 1.0F };

	public float[] mat_shininess = new float[] { 64.0F };

	public float[] mat_emission = new float[] { 0.0F, 0.0F, 0.0F, 0.0F };

	public float[] color = new float[] { 0.0F, 1.0F, 0.0F, 1.0F };

	protected List<float[]> vertices = new Vector<float[]>();

	protected BoundingBox boundingBox;

	public float[] ani_rotation = new float[] { 0F, 0F, 0F };

	// public float[] ani_rotation_current = new float[]{0F, 0F, 0F};
	public float[] ani_translation = new float[] { 0F, 0F, 0F };

	// public float[] ani_translation_current = new float[]{0F, 0F, 0F};
	public float[] ani_position = new float[] { 0F, 0F, 0F };

	// public float[] ani_position_current = new float[]{0F, 0F, 0F};
	public boolean ani_enabled = false;

	private short linePattern = (short) 0xAAAA;

	public double[] currentCenter = new double[] { 0, 0, 0 };

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public WavefrontLoader(String nm) {
		modelNm = nm;
		maxSize = 10.0F;

		verts = new ArrayList<Tuple3>();
		normals = new ArrayList<Tuple3>();
		texCoords = new ArrayList<Tuple3>();

		faces = new Faces(verts, normals, texCoords);
		faceMats = new FaceMaterials();
		modelDims = new ModelDimensions();
	} // end of initModelData()

	// public void loadModel(boolean showDetails) {
	// modelFile = new File(modelNm);
	// loadModel(modelNm);
	// centerScale();
	// if (showDetails)
	// reportOnModel();
	// } // end of WavefrontLoader()
	//
	// private void loadModel(String modelNm) {
	// // String fnm = MODEL_DIR + modelNm + ".obj";
	// String fnm = modelNm;
	// try {
	// System.out.println("Loading model from " + new File(fnm).getAbsolutePath() + " ...");
	// BufferedReader br = new BufferedReader(new FileReader(fnm));
	// readModel(br);
	// br.close();
	// } catch (IOException e) {
	// System.out.println(e.getMessage());
	// System.exit(1);
	// }
	// } // end of loadModel()

	public void loadModelFromFileSystem(AssetManager am, File file) {
		// String fnm = MODEL_DIR + modelNm + ".obj";
		try {
			System.out.println("Loading model from path " + file.getAbsolutePath() + " ...");
			FileInputStream is = new FileInputStream(file);
			loadModelImpl(am, is, file.getParentFile());
			is.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	public void loadModelFromClasspath(AssetManager am, String path) {

		// String fnm = MODEL_DIR + modelNm + ".obj";
		try {
			System.out.println("Loading model from classpath " + modelNm + " ...");
			InputStream is = am.open(path, AssetManager.ACCESS_STREAMING);
			loadModelImpl(am, is, null);
			is.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	} // end of loadModel()

	private void loadModelImpl(AssetManager am, InputStream is, File currentDir) {
		// String fnm = MODEL_DIR + modelNm + ".obj";
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		readModel(br, am, currentDir);
		centerScale();
		if (true)
			reportOnModel();
	}

	private void readModel(BufferedReader br, AssetManager am, File currentDir)
	// parse the OBJ file line-by-line
	{
		boolean isLoaded = true; // hope things will go okay

		int lineNum = 0;
		String line;
		boolean isFirstCoord = true;
		boolean isFirstTC = true;
		int numFaces = 0;

		try {
			while (((line = br.readLine()) != null) && isLoaded) {
				lineNum++;
				line = line.trim();
				if (line.length() > 0) {

					if (line.startsWith("v ")) { // vertex
						isLoaded = addVert(line, isFirstCoord);
						if (isFirstCoord)
							isFirstCoord = false;
					} else if (line.startsWith("vt")) { // tex coord
						isLoaded = addTexCoord(line, isFirstTC);
						if (isFirstTC)
							isFirstTC = false;
					} else if (line.startsWith("vn")) // normal
						isLoaded = addNormal(line);
					else if (line.startsWith("f ")) { // face
						isLoaded = faces.addFace(line);
						numFaces++;
					} else if (line.startsWith("mtllib ")) // load material
					{
						// materials = new Materials(new File(modelFile.getParent(),
						// line.substring(7)).getAbsolutePath());
						materials = new Materials(am, line.substring(7), currentDir);
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
			System.out.println(e.getMessage());
			System.exit(1);
		}

		if (!isLoaded) {
			System.out.println("Error loading model");
			System.exit(1);
		}
	} // end of readModel()

	private boolean addVert(String line, boolean isFirstCoord)
	/*
	 * Add vertex from line "v x y z" to vert ArrayList, and update the model dimension's info.
	 */
	{
		Tuple3 vert = readTuple3(line);
		if (vert != null) {
			verts.add(vert);
			vertices.add(new float[] { vert.getX(), vert.getY(), vert.getZ() });
			if (isFirstCoord)
				modelDims.set(vert);
			else
				modelDims.update(vert);
			return true;
		}
		return false;
	} // end of addVert()

	private Tuple3 readTuple3(String line)
	/*
	 * The line starts with an OBJ word ("v" or "vn"), followed by three floats (x, y, z) separated by spaces
	 */
	{
		StringTokenizer tokens = new StringTokenizer(line, " ");
		tokens.nextToken(); // skip the OBJ word

		try {
			float x = Float.parseFloat(tokens.nextToken());
			float y = Float.parseFloat(tokens.nextToken());
			float z = Float.parseFloat(tokens.nextToken());

			// System.out.println("Read tuple " + x + ", " + y + ", " + z);
			return new Tuple3(x, y, z);
		} catch (NumberFormatException e) {
			System.out.println(e.getMessage());
		}

		return null; // means an error occurred
	} // end of readTuple3()

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

	private boolean addNormal(String line)
	// add normal from line "vn x y z" to the normals ArrayList
	{
		Tuple3 normCoord = readTuple3(line);
		if (normCoord != null) {
			normals.add(normCoord);
			return true;
		}
		return false;
	} // end of addNormal()

	private void centerScale()
	/*
	 * Position the model so it's center is at the origin, and scale it so its longest dimension is no bigger than
	 * maxSize.
	 */
	{
		// get the model's center point
		Tuple3 center = modelDims.getCenter();

		// calculate a scale factor
		float scaleFactor = 1.0f;
		float largest = modelDims.getLargest();
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (maxSize / largest);
		System.out.println("Scale factor: " + scaleFactor);

		// modify the model's vertices
		Tuple3 vert;
		float x, y, z;
		for (int i = 0; i < verts.size(); i++) {
			vert = (Tuple3) verts.get(i);
			x = (vert.getX() - center.getX()) * scaleFactor;
			vert.setX(x);
			y = (vert.getY() - center.getY()) * scaleFactor;
			vert.setY(y);
			z = (vert.getZ() - center.getZ()) * scaleFactor;
			vert.setZ(z);
		}
	} // end of centerScale()

	public Object3D createGLES20Object(File currentDir, AssetManager am, int drawMode, int drawSize) throws IOException
	/*
	 * render the model to a display list, so it can be drawn quicker later
	 */
	{
		FloatBuffer vertexBuffer = createNativeByteBuffer(3 * verts.size() * 4).asFloatBuffer();
		vertexBuffer.position(0);
		for (Tuple3 vert : verts) {
			vertexBuffer.put(vert.getX());
			vertexBuffer.put(vert.getY());
			vertexBuffer.put(vert.getZ());
		}

		FloatBuffer vertexArrayBuffer = null;
		if (DRAW_ARRAYS) {
			vertexArrayBuffer = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
			vertexArrayBuffer.position(0);
			for (int[] face : faces.facesVertIdxs) {
				for (int i = 0; i < face.length; i++) {
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3));
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3 + 1));
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3 + 2));
				}
			}
		}

		List<int[]> drawModeList = new ArrayList<int[]>();
		int currentVertexPos = 0;
		for (int[] face : faces.facesVertIdxs) {
			if (face.length == 3) {
				drawModeList.add(new int[] { GLES20.GL_TRIANGLES, currentVertexPos, face.length });
			} else {
				drawModeList.add(new int[] { GLES20.GL_TRIANGLE_FAN, currentVertexPos, face.length });
			}
			currentVertexPos += face.length;
		}

		FloatBuffer normalsBuffer = createNativeByteBuffer(3 * normals.size() * 4).asFloatBuffer();
		normalsBuffer.position(0);
		for (Tuple3 norm : normals) {
			normalsBuffer.put(norm.getX());
			normalsBuffer.put(norm.getY());
			normalsBuffer.put(norm.getZ());
		}

		FloatBuffer normalsArray = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
		normalsArray.position(0);
		for (int[] normal : faces.facesNormIdxs) {
			for (int i = 0; i < normal.length; i++) {
				normalsArray.put(normalsBuffer.get(normal[i] * 3));
				normalsArray.put(normalsBuffer.get(normal[i] * 3 + 1));
				normalsArray.put(normalsBuffer.get(normal[i] * 3 + 2));
			}
		}

		FloatBuffer textCoordsBuffer = createNativeByteBuffer(2 * texCoords.size() * 4).asFloatBuffer();
		textCoordsBuffer.position(0);
		for (Tuple3 texCor : texCoords) {
			textCoordsBuffer.put(texCor.getX());
			textCoordsBuffer.put(flipTexCoords ? 1 - texCor.getY() : texCor.getY());
		}

		FloatBuffer textureArraysBuffer = createNativeByteBuffer(2 * faces.getVerticesReferencesCount() * 4)
				.asFloatBuffer();
		try {
			for (int[] text : faces.facesTexIdxs) {
				for (int i = 0; i < text.length; i++) {
					textureArraysBuffer.put(textCoordsBuffer.get(text[i] * 2));
					textureArraysBuffer.put(textCoordsBuffer.get(text[i] * 2 + 1));
				}
			}
		} catch (Exception ex) {
			Log.e("WavefrontLoader", "Failure to load texture coordinates");
		}

		if (materials != null) {
			materials.readMaterials();
		}

		FloatBuffer colorArrayBuffer = null;
		if (!faceMats.isEmpty()) {
			colorArrayBuffer = createNativeByteBuffer(4 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
			colorArrayBuffer.position(0);
			float[] currentColor = DEFAULT_COLOR;
			for (int i = 0; i < faces.facesVertIdxs.size(); i++) {
				if (faceMats.findMaterial(i) != null) {
					Material mat = materials.getMaterial(faceMats.findMaterial(i));
					if (mat != null) {
						currentColor = mat.getKdColor();
					}
				}
				int[] face = faces.facesVertIdxs.get(i);
				for (int j = 0; j < face.length; j++) {
					colorArrayBuffer.put(currentColor);
				}
			}
		}

		// materials = null;

		List<InputStream> textureIs = null;
		if (materials != null && !materials.materials.isEmpty()) {
			textureIs = new ArrayList<InputStream>();
			// FileInputStream is = new FileInputStream(fileName);
			String texture = null;
			for (Material mat : materials.materials.values()) {
				if (mat.getTexture() != null) {
					texture = mat.getTexture();
					break;
				}
			}
			if (texture != null) {
				if (currentDir != null) {
					File file = new File(currentDir, texture);
					Log.v("materials", "Loading texture '" + file + "'...");
					textureIs.add(new FileInputStream(file));
				} else {
					Log.v("materials", "Loading texture '" + texture + "'...");
					textureIs.add(am.open(texture));
				}
			} else {
				Log.i("Loader", "Found material(s) but no texture");
			}
		}

		// TODO: removed because i im refactoring the ObjectV3.. return new ObjectV3(vertexBuffer, indexBuffer,
		// normalsBuffer,
		// textCoordsBuffer, GLES20.GL_TRIANGLES, 3,
		// materials != null ? materials.materials.get(0).getTexture() : null);

		if (colorArrayBuffer != null) {

			return new ObjectV5(DRAW_ARRAYS ? vertexArrayBuffer : vertexBuffer, drawModeList, colorArrayBuffer,
					DRAW_ARRAYS ? null : null, normalsArray, textureArraysBuffer, drawMode, drawSize,
					textureIs != null ? textureIs : null);
		} else {
			return new ObjectV3(DRAW_ARRAYS ? vertexArrayBuffer : vertexBuffer, DRAW_ARRAYS ? null : null,
					normalsBuffer, textureArraysBuffer, drawMode, drawSize,
					textureIs != null ? textureIs.get(0) : null);
		}

		// if (materials != null) {
		// // materials.readMaterials();
		// }
		// modelDispList = gl.glGenLists(1);
		// gl.glNewList(modelDispList, GLES20.GL_COMPILE);
		//
		// // gl.glPushMatrix();
		// // render the model face-by-face
		// String faceMat;
		// for (int i = 0; i < faces.getNumFaces(); i++) {
		// faceMat = faceMats.findMaterial(i); // get material used by face i
		// if (faceMat != null)
		// flipTexCoords = materials.renderWithMaterial(faceMat, gl); // render
		// // using
		// // that
		// // material
		// faces.renderFace(i, flipTexCoords, gl); // draw face i
		// }
		// if (materials != null)
		// materials.switchOffTex(gl);
		// // gl.glPopMatrix();
		//
		// gl.glEndList();
		// return modelDispList;
	}// end of drawToList()

	private void reportOnModel() {
		System.out.println("No. of vertices: " + verts.size());
		System.out.println("No. of normal coords: " + normals.size());
		System.out.println("No. of tex coords: " + texCoords.size());
		System.out.println("No. of faces: " + faces.getNumFaces());
		System.out.println("No. of points: " + faces.facesVertIdxs.size());

		modelDims.reportDimensions();
		// dimensions of model (before centering and scaling)

		if (materials != null)
			materials.showMaterials(); // list defined materials
		faceMats.showUsedMaterials(); // show what materials have been used by
		// faces
	} // end of reportOnModel()

	public void reshape(float[] sceneCenter, float xMax, float yMax, float zMax) {

		// calculate scale to reference size
		float[] sizes = boundingBox.sizes;

		// calculate max scale
		float maxScale = Math.min(Math.min(xMax / sizes[0], yMax / sizes[1]), zMax / sizes[2]);

		setScale(new float[] { maxScale, maxScale, maxScale });

		// calculate translation to reference center
		float[] center = boundingBox.center;
		transX = (sceneCenter[0] - center[0]);
		transY = (sceneCenter[1] - center[1]);
		transZ = (sceneCenter[2] - center[2]);
	}

	public float[] getScale() {
		return scale;
	}

	public void setScale(float[] newScale) {
		scale = newScale;

		// calculate max scale
		float maxScale = Math.max(Math.max(scale[0], scale[1]), scale[2]);

		// normalize radius
		boundingBox.radius = boundingBox.radius * maxScale;
	}

	public String currentSizeToString() {
		if (boundingBox != null) {
			return "x[" + boundingBox.sizes[0] * scale[0] + "],y[" + boundingBox.sizes[1] * scale[0] + "],z["
					+ boundingBox.sizes[2] * scale[0] + "]";
		}
		return null;
	}

} // end of WavefrontLoader class

class Tuple3 {
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

class ModelDimensions {
	// edge coordinates
	private float leftPt, rightPt; // on x-axis
	private float topPt, bottomPt; // on y-axis
	private float farPt, nearPt; // on z-axis

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

	public void set(Tuple3 vert)
	// initialize the model's edge coordinates
	{
		rightPt = vert.getX();
		leftPt = vert.getX();

		topPt = vert.getY();
		bottomPt = vert.getY();

		nearPt = vert.getZ();
		farPt = vert.getZ();
	} // end of set()

	public void update(Tuple3 vert)
	// update the edge coordinates using vert
	{
		if (vert.getX() > rightPt)
			rightPt = vert.getX();
		if (vert.getX() < leftPt)
			leftPt = vert.getX();

		if (vert.getY() > topPt)
			topPt = vert.getY();
		if (vert.getY() < bottomPt)
			bottomPt = vert.getY();

		if (vert.getZ() > nearPt)
			nearPt = vert.getZ();
		if (vert.getZ() < farPt)
			farPt = vert.getZ();
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

class Materials {

	public Map<String, Material> materials;
	// stores the Material objects built from the MTL file data

	// for storing the material currently being used for rendering
	private String renderMatName = null;

	private boolean usingTexture = false;
	private boolean flipTexCoords = false;
	// whether tex coords should be flipped around the y-axis

	// private File file;
	private String mfnm;

	private File currentDir;
	private AssetManager am;

	public Materials(AssetManager am, String mtlFnm, File currentDir) {
		this.am = am;
		this.currentDir = currentDir;
		// TODO: this map is now linked because we want to get only the first texture
		// when multiple textures are supported, change this to be a simple HashMap
		materials = new LinkedHashMap<String, Material>();

		this.mfnm = mtlFnm;
		// file = new File(mtlFnm);
	}

	public void readMaterials() {
		try {
			InputStream is;
			if (currentDir != null) {
				File file = new File(currentDir, mfnm);
				System.out.println("Loading material from " + file);
				is = new FileInputStream(file);
			} else {
				System.out.println("Loading material from " + mfnm);
				is = am.open(mfnm);
			}
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			readMaterials(br);
			br.close();
		} catch (IOException e) {
			Log.e("materials", e.getMessage(), e);
			throw new RuntimeException(e);
		}

	} // end of Materials()

	private void readMaterials(BufferedReader br)
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
			materials.put(currMaterial.getName(), currMaterial);
		} catch (IOException e) {
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

	public void showMaterials()
	// list all the Material objects
	{
		System.out.println("No. of materials: " + materials.size());
		Material m;
		for (int i = 0; i < materials.size(); i++) {
			m = (Material) materials.get(i);
			m.showMaterial();
			// System.out.println();
		}
	} // end of showMaterials()

	Material getMaterial(String name) {
		return materials.get(name);
	}

} // end of Materials class

class Material {
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

class Faces {
	private static final float DUMMY_Z_TC = -5.0f;

	/*
	 * indicies for vertices, tex coords, and normals used by each face
	 */
	ArrayList<int[]> facesVertIdxs;
	ArrayList<int[]> facesTexIdxs;
	ArrayList<int[]> facesNormIdxs;

	// references to the model's vertices, normals, and tex coords
	private ArrayList<Tuple3> verts;
	private ArrayList<Tuple3> normals;
	private ArrayList<Tuple3> texCoords;

	// Total number of vertices references. That is, each face references 3 or more vectors. This is the sum for all
	// faces
	private int verticesReferencesCount;

	// for reporting
	// private DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

	Faces(ArrayList<Tuple3> vs, ArrayList<Tuple3> ns, ArrayList<Tuple3> ts) {
		verts = vs;
		normals = ns;
		texCoords = ts;

		facesVertIdxs = new ArrayList<int[]>();
		facesTexIdxs = new ArrayList<int[]>();
		facesNormIdxs = new ArrayList<int[]>();
	} // end of Faces()

	/**
	 * get this face's indicies from line "f v/vt/vn ..." with vt or vn index values perhaps being absent.
	 */
	public boolean addFace(String line) {
		try {
			line = line.substring(2); // skip the "f "
			StringTokenizer st = new StringTokenizer(line, " ");
			int numTokens = st.countTokens(); // number of v/vt/vn tokens
			// create arrays to hold the v, vt, vn indicies

			int v[] = new int[numTokens];
			int vt[] = new int[numTokens];
			int vn[] = new int[numTokens];

			for (int i = 0; i < numTokens; i++) {
				String faceToken = addFaceVals(st.nextToken()); // get a v/vt/vn
				// token
				// System.out.println(faceToken);

				StringTokenizer st2 = new StringTokenizer(faceToken, "/");
				int numSeps = st2.countTokens(); // how many '/'s are there in
				// the token

				v[i] = Integer.parseInt(st2.nextToken());
				vt[i] = (numSeps > 1) ? Integer.parseInt(st2.nextToken()) : 0;
				vn[i] = (numSeps > 2) ? Integer.parseInt(st2.nextToken()) : 0;
				// add 0's if the vt or vn index values are missing;
				// 0 is a good choice since real indices start at 1

				if (WavefrontLoader.INDEXES_START_AT_1) {
					v[i] = v[i] - 1;
					vt[i] = vt[i] - 1;
					vn[i] = vn[i] - 1;
				}
			}
			// store the indicies for this face
			facesVertIdxs.add(v);
			facesTexIdxs.add(vt);
			facesNormIdxs.add(vn);

			verticesReferencesCount += numTokens;

		} catch (NumberFormatException e) {
			System.out.println("Incorrect face index");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	private String addFaceVals(String faceStr)
	/*
	 * A face token (v/vt/vn) may be missing vt or vn index values; add 0's in those cases.
	 */
	{
		char chars[] = faceStr.toCharArray();
		StringBuffer sb = new StringBuffer();
		char prevCh = 'x'; // dummy value

		for (int k = 0; k < chars.length; k++) {
			if (chars[k] == '/' && prevCh == '/') // if no char between /'s
				sb.append('0'); // add a '0'
			prevCh = chars[k];
			sb.append(prevCh);
		}
		return sb.toString();
	} // end of addFaceVals()

	public int getNumFaces() {
		return facesVertIdxs.size();
	}

	public int getVerticesReferencesCount() {
		return verticesReferencesCount;
	}

} // end of Faces class

class FaceMaterials {
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
		return faceMats.isEmpty();
	}

} // end of FaceMaterials class
