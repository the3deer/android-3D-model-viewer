package org.andresoviedo.android_3d_model_engine.model;

import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.collision.Octree;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.FaceMaterials;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Faces;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Materials;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Tuple3;
import org.andresoviedo.util.math.Math3DUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the basic 3D data necessary to build the 3D object
 * 
 * @author andres
 *
 */
public class Object3DData {

	// opengl version to use to draw this object
	private int version = 5;
	/**
	 * The directory where the files reside so we can build referenced files in the model like material and textures
	 * files
	 */
	private Uri uri;
	/**
	 * The assets directory where the files reside so we can build referenced files in the model like material and
	 * textures files
	 */
	// private String assetsDir;
	private String id;
	private boolean drawUsingArrays = false;
	private boolean flipTextCoords = true;

	// Model data for the simplest object

	private boolean isVisible = true;

	private float[] color;
	/**
	 * The minimum thing we can draw in space is a vertex (or point).
	 * This drawing mode uses the vertexBuffer
	 */
	private int drawMode = GLES20.GL_POINTS;
	private int drawSize;

	// Model data
	private FloatBuffer vertexBuffer = null;
	private FloatBuffer vertexNormalsBuffer = null;
	private IntBuffer drawOrderBuffer = null;
	private ShortBuffer shortDrawOrderBuffer = null;  // in case system doesn't support ints
	private ArrayList<Tuple3> texCoords;
	private Faces faces;
	private FaceMaterials faceMats;
	private Materials materials;
	private String textureFile;

	// Processed arrays
	private FloatBuffer vertexArrayBuffer = null;
	private FloatBuffer vertexColorsArrayBuffer = null;
	private FloatBuffer vertexNormalsArrayBuffer = null;
	private FloatBuffer textureCoordsArrayBuffer = null;
	private List<int[]> drawModeList = null;
	private byte[] textureData = null;
	private List<InputStream> textureStreams = null;

	// derived data
	private BoundingBox boundingBox;

	// Transformation data
	protected float[] position = new float[] { 0f, 0f, 0f };
	protected float[] rotation = new float[] { 0f, 0f, 0f };
	protected float[] scale = new float[] { 1, 1, 1 };
	protected float[] modelMatrix = new float[16];

	{
		Matrix.setIdentityM(modelMatrix,0);
	}

	// whether the object has changed
	private boolean changed;

	// Async Loader
	private WavefrontLoader.ModelDimensions modelDimensions;
	private WavefrontLoader loader;

	// collision detection
	private Octree octree = null;

	// errors detected
	private List<String> errors = new ArrayList<>();

	public Object3DData(FloatBuffer vertexArrayBuffer) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.version = 1;
	}

	public Object3DData(FloatBuffer vertexBuffer, IntBuffer drawOrder) {
		this.vertexBuffer = vertexBuffer;
		this.drawOrderBuffer = drawOrder;
		this.version = 2;
	}

	public Object3DData(FloatBuffer vertexArrayBuffer, FloatBuffer textureCoordsArrayBuffer, byte[] texData) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		this.textureData = texData;
		this.version = 3;
	}

	public Object3DData(FloatBuffer vertexArrayBuffer, FloatBuffer vertexColorsArrayBuffer,
			FloatBuffer textureCoordsArrayBuffer, byte[] texData) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		this.textureData = texData;
		this.version = 4;
	}

	public Object3DData(FloatBuffer verts, FloatBuffer normals, ArrayList<Tuple3> texCoords, Faces faces,
			FaceMaterials faceMats, Materials materials) {
		super();
		this.vertexBuffer = verts;
		this.vertexNormalsBuffer = normals;
		this.texCoords = texCoords;
		this.faces = faces;  // parameter "faces" could be null in case of async loading
		this.faceMats = faceMats;
		this.materials = materials;
	}

	public void setLoader(WavefrontLoader loader) {
		this.loader = loader;
	}


	public WavefrontLoader getLoader() {
		return loader;
	}

	public void setDimensions(WavefrontLoader.ModelDimensions modelDimensions) {
		this.modelDimensions = modelDimensions;
	}

	public WavefrontLoader.ModelDimensions getDimensions() {
		return modelDimensions;
	}

	public void setOctree(Octree octree){
		this.octree = octree;
	}

	public Octree getOctree(){
		return octree;
	}

	/**
	 * Can be called when the faces were loaded asynchronously
	 *
	 * @param faces 3d faces
	 */
	public Object3DData setFaces(Faces faces) {
		this.faces = faces;
		this.drawOrderBuffer = faces.getIndexBuffer();
		return this;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public int getVersion() {
		return version;
	}

	public Object3DData setVersion(int version) {
		this.version = version;
		return this;
	}

	public boolean isChanged() {
		return changed;
	}

	public Object3DData setId(String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}

	public float[] getColor() {
		return color;
	}

	public float[] getColorInverted() {
		if (getColor() == null || getColor().length != 4) {
			return null;
		}
		return new float[] { 1 - getColor()[0], 1 - getColor()[1], 1 - getColor()[2], 1 };
	}

	public Object3DData setColor(float[] color) {
		this.color = color;
		return this;
	}

	public int getDrawMode() {
		return drawMode;
	}

	public Object3DData setDrawMode(int drawMode) {
		this.drawMode = drawMode;
		return this;
	}

	public int getDrawSize() {
		return drawSize;
	}

	// -----------

	public byte[] getTextureData() {
		return textureData;
	}

	public void setTextureData(byte[] textureData) {
		this.textureData = textureData;
	}

	public Object3DData setPosition(float[] position) {
		this.position = position;
		updateModelMatrix();
		return this;
	}

	public float[] getPosition() {
		return position;
	}

	public float getPositionX() {
		return position != null ? position[0] : 0;
	}

	public float getPositionY() {
		return position != null ? position[1] : 0;
	}

	public float getPositionZ() {
		return position != null ? position[2] : 0;
	}

	public float[] getRotation() {
		return rotation;
	}

	public float getRotationX(){
		return rotation[0];
	}

	public float getRotationY(){
		return rotation[1];
	}

	public float getRotationZ() {
		return rotation[2];
	}

	public Object3DData setScale(float[] scale){
		this.scale = scale;
		updateModelMatrix();
		return this;
	}

	public float[] getScale(){
		return scale;
	}

	public float getScaleX() {
		return getScale()[0];
	}

	public float getScaleY() {
		return getScale()[1];
	}

	public float getScaleZ() {
		return getScale()[2];
	}

	public Object3DData setRotation(float[] rotation) {
		this.rotation = rotation;
		updateModelMatrix();
		return this;
	}

	public Object3DData setRotationY(float rotY) {
		this.rotation[1] = rotY;
		updateModelMatrix();
		return this;
	}

	private void updateModelMatrix(){
		Matrix.setIdentityM(modelMatrix,0);
		Matrix.setRotateM(modelMatrix,0,getRotationX(),1,0,0);
		Matrix.setRotateM(modelMatrix,0,getRotationY(),0,1,0);
		Matrix.setRotateM(modelMatrix,0,getRotationY(),0,0,1);
		Matrix.scaleM(modelMatrix,0,getScaleX(),getScaleY(),getScaleZ());
		Matrix.translateM(modelMatrix,0,getPositionX(),getPositionY(),getPositionZ());
	}

	public float[] getModelMatrix(){
		return modelMatrix;
	}

	public IntBuffer getDrawOrder() {
		return drawOrderBuffer;
	}

    /**
     * In case OpenGL doesn't support using GL_UNSIGNED_INT for glDrawElements(), then use this buffer
     * @return the draw buffer as short
     */
	public ShortBuffer getDrawOrderAsShort() {
		if (shortDrawOrderBuffer == null) {
			shortDrawOrderBuffer = createNativeByteBuffer(drawOrderBuffer.capacity() * 2).asShortBuffer();
			for (int i=0; i<drawOrderBuffer.capacity(); i++){
			    shortDrawOrderBuffer.put((short)drawOrderBuffer.get(i));
            }
		}
		return shortDrawOrderBuffer;
	}

	public Object3DData setDrawOrder(IntBuffer drawBuffer) {
		this.drawOrderBuffer = drawBuffer;
		return this;
	}

	/*public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}*/

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	public Uri getUri() {
		return this.uri;
	}

	public boolean isDrawUsingArrays() {
		return drawUsingArrays;
	}

	public boolean isFlipTextCoords() {
		return flipTextCoords;
	}

	public void setFlipTextCoords(boolean flipTextCoords) {
		this.flipTextCoords = flipTextCoords;
	}

	public Object3DData setDrawUsingArrays(boolean drawUsingArrays) {
		this.drawUsingArrays = drawUsingArrays;
		return this;
	}

	public FloatBuffer getVerts() {
		return vertexBuffer;
	}

	public FloatBuffer getNormals() {
		return vertexNormalsBuffer;
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

	// -------------------- Buffers ---------------------- //

	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}

	public Object3DData setVertexBuffer(FloatBuffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
		return this;
	}

	public FloatBuffer getVertexNormalsBuffer() {
		return vertexNormalsBuffer;
	}

	public Object3DData setVertexNormalsBuffer(FloatBuffer vertexNormalsBuffer) {
		this.vertexNormalsBuffer = vertexNormalsBuffer;
		return this;
	}

	public FloatBuffer getVertexArrayBuffer() {
		return vertexArrayBuffer;
	}

	public Object3DData setVertexArrayBuffer(FloatBuffer vertexArrayBuffer) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		return this;
	}

	public FloatBuffer getVertexNormalsArrayBuffer() {
		return vertexNormalsArrayBuffer;
	}

	public Object3DData setVertexNormalsArrayBuffer(FloatBuffer vertexNormalsArrayBuffer) {
		this.vertexNormalsArrayBuffer = vertexNormalsArrayBuffer;
		return this;
	}

	public FloatBuffer getTextureCoordsArrayBuffer() {
		return textureCoordsArrayBuffer;
	}

	public Object3DData setTextureCoordsArrayBuffer(FloatBuffer textureCoordsArrayBuffer) {
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		return this;
	}

	public List<int[]> getDrawModeList() {
		return drawModeList;
	}

	public Object3DData setDrawModeList(List<int[]> drawModeList) {
		this.drawModeList = drawModeList;
		return this;
	}

	public FloatBuffer getVertexColorsArrayBuffer() {
		return vertexColorsArrayBuffer;
	}

	public Object3DData setVertexColorsArrayBuffer(FloatBuffer vertexColorsArrayBuffer) {
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
		return this;
	}

	public void setTextureFile(String textureFile) {
		this.textureFile = textureFile;
	}

	public String getTextureFile(){
		return textureFile;
	}

	public Object3DData centerAndScale(float maxSize) {
		float leftPt = Float.MAX_VALUE, rightPt = Float.MIN_VALUE; // on x-axis
		float topPt = Float.MIN_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
		float farPt = Float.MAX_VALUE, nearPt = Float.MIN_VALUE; // on z-axis

		 FloatBuffer vertexBuffer = getVertexArrayBuffer() != null ? getVertexArrayBuffer() : getVertexBuffer();
		if (vertexBuffer == null) {
			Log.v("Object3DData", "Scaling for '" + getId() + "' I found that there is no vertex data");
			return this;
		}

		Log.i("Object3DData", "Calculating dimensions for '" + getId() + "...");
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			if (vertexBuffer.get(i) > rightPt)
				rightPt = vertexBuffer.get(i);
			else if (vertexBuffer.get(i) < leftPt)
				leftPt = vertexBuffer.get(i);
			if (vertexBuffer.get(i + 1) > topPt)
				topPt = vertexBuffer.get(i + 1);
			else if (vertexBuffer.get(i + 1) < bottomPt)
				bottomPt = vertexBuffer.get(i + 1);
			if (vertexBuffer.get(i + 2) > nearPt)
				nearPt = vertexBuffer.get(i + 2);
			else if (vertexBuffer.get(i + 2) < farPt)
				farPt = vertexBuffer.get(i + 2);
		} // end
		Log.i("Object3DData", "Dimensions for '" + getId() + " (X left, X right): ("+leftPt+","+rightPt+")");
		Log.i("Object3DData", "Dimensions for '" + getId() + " (Y top, Y bottom): ("+topPt+","+bottomPt+")");
		Log.i("Object3DData", "Dimensions for '" + getId() + " (Z near, Z far): ("+nearPt+","+farPt+")");

		// calculate center of 3D object
		float xc = (rightPt + leftPt) / 2.0f;
		float yc = (topPt + bottomPt) / 2.0f;
		float zc = (nearPt + farPt) / 2.0f;

		// this.setOriginalPosition(new float[]{-xc,-yc,-zc});

		// calculate largest dimension
		float height = topPt - bottomPt;
		float depth = nearPt - farPt;
		float largest = rightPt - leftPt;
		if (height > largest)
			largest = height;
		if (depth > largest)
			largest = depth;
		Log.i("Object3DData", "Largest dimension ["+largest+"]");

		// scale object

		// calculate a scale factor
		float scaleFactor = 1.0f;
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (maxSize / largest);
		Log.i("Object3DData",
				"Centering & scaling '" + getId() + "' to (" + xc + "," + yc + "," + zc + ") scale: '" + scaleFactor + "'");

		// this.setOriginalScale(new float[]{scaleFactor,scaleFactor,scaleFactor});

		// modify the model's vertices
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			float x = vertexBuffer.get(i);
			float y = vertexBuffer.get(i + 1);
			float z = vertexBuffer.get(i + 2);
			x = (x - xc) * scaleFactor;
			y = (y - yc) * scaleFactor;
			z = (z - zc) * scaleFactor;
			vertexBuffer.put(i, x);
			vertexBuffer.put(i + 1, y);
			vertexBuffer.put(i + 2, z);
		}

		return this;
	}

	public Object3DData centerAndScaleAndExplode(float maxSize, float explodeFactor) {
		if (drawMode != GLES20.GL_TRIANGLES) {
			Log.i("Object3DData", "Cant explode '" + getId() + " because its not made of triangles...");
			return this;
		}

		float leftPt = Float.MAX_VALUE, rightPt = Float.MIN_VALUE; // on x-axis
		float topPt = Float.MIN_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
		float farPt = Float.MAX_VALUE, nearPt = Float.MIN_VALUE; // on z-axis

		FloatBuffer vertexBuffer = getVertexArrayBuffer() != null ? getVertexArrayBuffer() : getVertexBuffer();
		if (vertexBuffer == null) {
			Log.v("Object3DData", "Scaling for '" + getId() + "' I found that there is no vertex data");
			return this;
		}

		Log.i("Object3DData", "Calculating dimensions for '" + getId() + "...");
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			if (vertexBuffer.get(i) > rightPt)
				rightPt = vertexBuffer.get(i);
			else if (vertexBuffer.get(i) < leftPt)
				leftPt = vertexBuffer.get(i);
			if (vertexBuffer.get(i + 1) > topPt)
				topPt = vertexBuffer.get(i + 1);
			else if (vertexBuffer.get(i + 1) < bottomPt)
				bottomPt = vertexBuffer.get(i + 1);
			if (vertexBuffer.get(i + 2) > nearPt)
				nearPt = vertexBuffer.get(i + 2);
			else if (vertexBuffer.get(i + 2) < farPt)
				farPt = vertexBuffer.get(i + 2);
		} // end

		// calculate center of 3D object
		float xc = (rightPt + leftPt) / 2.0f;
		float yc = (topPt + bottomPt) / 2.0f;
		float zc = (nearPt + farPt) / 2.0f;

		// calculate largest dimension
		float height = topPt - bottomPt;
		float depth = nearPt - farPt;
		float largest = rightPt - leftPt;
		if (height > largest)
			largest = height;
		if (depth > largest)
			largest = depth;

		// scale object

		// calculate a scale factor
		float scaleFactor = 1.0f;
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (maxSize / largest);
		Log.i("Object3DData",
				"Exploding '" + getId() + "' to '" + xc + "," + yc + "," + zc + "' '" + scaleFactor + "'");

		// modify the model's vertices
		FloatBuffer vertexBufferNew = createNativeByteBuffer(vertexBuffer.capacity() * 4).asFloatBuffer();
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			float x = vertexBuffer.get(i);
			float y = vertexBuffer.get(i + 1);
			float z = vertexBuffer.get(i + 2);
			x = (x - xc) * scaleFactor;
			y = (y - yc) * scaleFactor;
			z = (z - zc) * scaleFactor;
			vertexBuffer.put(i, x);
			vertexBuffer.put(i + 1, y);
			vertexBuffer.put(i + 2, z);
			vertexBufferNew.put(i, x * explodeFactor);
			vertexBufferNew.put(i + 1, y * explodeFactor);
			vertexBufferNew.put(i + 2, z * explodeFactor);
		}

		if (drawOrderBuffer != null) {
			Log.e("Object3DData", "Cant explode object composed of indexes '" + getId() + "'");
			return this;
		}

		for (int i = 0; i < vertexBuffer.capacity(); i += 9) {
			float x1 = vertexBuffer.get(i);
			float y1 = vertexBuffer.get(i + 1);
			float z1 = vertexBuffer.get(i + 2);
			float x2 = vertexBuffer.get(i + 3);
			float y2 = vertexBuffer.get(i + 4);
			float z2 = vertexBuffer.get(i + 5);
			float x3 = vertexBuffer.get(i + 6);
			float y3 = vertexBuffer.get(i + 7);
			float z3 = vertexBuffer.get(i + 8);
			float[] center1 = Math3DUtils.calculateFaceCenter(new float[] { x1, y1, z1 }, new float[] { x2, y2, z2 },
					new float[] { x3, y3, z3 });

			float xe1 = vertexBufferNew.get(i);
			float ye1 = vertexBufferNew.get(i + 1);
			float ze1 = vertexBufferNew.get(i + 2);
			float xe2 = vertexBufferNew.get(i + 3);
			float ye2 = vertexBufferNew.get(i + 4);
			float ze2 = vertexBufferNew.get(i + 5);
			float xe3 = vertexBufferNew.get(i + 6);
			float ye3 = vertexBufferNew.get(i + 7);
			float ze3 = vertexBufferNew.get(i + 8);
			float[] center2 = Math3DUtils.calculateFaceCenter(new float[] { xe1, ye1, ze1 },
					new float[] { xe2, ye2, ze2 }, new float[] { xe3, ye3, ze3 });

			vertexBuffer.put(i + 0, x1 + (center2[0] - center1[0]));
			vertexBuffer.put(i + 1, y1 + (center2[1] - center1[1]));
			vertexBuffer.put(i + 2, z1 + (center2[2] - center1[2]));
			vertexBuffer.put(i + 3, x2 + (center2[0] - center1[0]));
			vertexBuffer.put(i + 4, y2 + (center2[1] - center1[1]));
			vertexBuffer.put(i + 5, z2 + (center2[2] - center1[2]));
			vertexBuffer.put(i + 6, x3 + (center2[0] - center1[0]));
			vertexBuffer.put(i + 7, y3 + (center2[1] - center1[1]));
			vertexBuffer.put(i + 8, z3 + (center2[2] - center1[2]));
		}

		return this;
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public BoundingBox getBoundingBox() {
		FloatBuffer vertexBuffer = getVertexBuffer();
		if (vertexBuffer == null){
			vertexBuffer = getVertexArrayBuffer();
		}
		if (boundingBox == null) {
			boundingBox = BoundingBox.create(getId()+"_BoundingBox", vertexBuffer, getModelMatrix());
		}
		return boundingBox;
	}

	public void center(float[] newPosition) {
		// calculate a scale factor
		Tuple3 center = modelDimensions.getCenter();
		setPosition(new float[]{-center.getX() + newPosition[0], -center.getY() + newPosition[1], -center.getZ() + newPosition[2]});
	}

	public void centerAndScale(float newScale, float[] newPosition) {
		// calculate a scale factor
		float scaleFactor = 1.0f;
		float largest = modelDimensions.getLargest();
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (1.0f / largest);
		setScale(new float[]{scaleFactor * newScale, scaleFactor * newScale, scaleFactor * newScale});

		Tuple3 center = modelDimensions.getCenter();
		setPosition(new float[]{-center.getX() + newPosition[0], -center.getY() + newPosition[1], -center.getZ() + newPosition[2]});
	}

	public static void centerAndScale(List<Object3DData> datas, float newScale, float[] newPosition){
		// calculate the global max length
		float maxRight = datas.get(0).getDimensions().rightPt;
		float maxLeft = datas.get(0).getDimensions().leftPt;
		float maxTop = datas.get(0).getDimensions().topPt;
		float maxBottom = datas.get(0).getDimensions().bottomPt;
		float maxNear = datas.get(0).getDimensions().nearPt;
		float maxFar = datas.get(0).getDimensions().farPt;
		for (int i=1; i<datas.size(); i++){
			float maxRight2 = datas.get(i).getDimensions().rightPt;
			float maxLeft2 = datas.get(i).getDimensions().leftPt;
			float maxTop2 = datas.get(i).getDimensions().topPt;
			float maxBottom2 = datas.get(i).getDimensions().bottomPt;
			float maxNear2 = datas.get(i).getDimensions().nearPt;
			float maxFar2 = datas.get(i).getDimensions().farPt;
			if (maxRight2 > maxRight) maxRight = maxRight2;
			if (maxLeft2 < maxLeft) maxLeft = maxLeft2;
			if (maxTop2 > maxTop) maxTop = maxTop2;
			if (maxBottom2 < maxBottom) maxBottom = maxBottom2;
			if (maxNear2 > maxNear) maxNear = maxNear2;
			if (maxFar2 < maxFar) maxFar = maxFar2;
		}
		float lengthX = maxRight - maxLeft;
		float lengthY = maxTop - maxBottom;
		float lengthZ = maxNear - maxFar;
		float maxLength = lengthX;
		if (lengthY > maxLength) maxLength = lengthY;
		if (lengthZ > maxLength) maxLength = lengthZ;

		// calculate the global center
		float centerX = (maxRight + maxLeft)/2;
		float centerY = (maxTop + maxBottom)/2;
		float centerZ = (maxNear + maxFar)/2;

		// calculate the scale factor
		float scaleFactor = 1.0f / maxLength * newScale;
		float translationX = -centerX + newPosition[0];
		float translationY = -centerY + newPosition[1];
		float translationZ = -centerZ + newPosition[2];

		for (Object3DData data : datas){
			data.setPosition(new float[]{translationX, translationY, translationZ});
			data.setScale(new float[]{scaleFactor, scaleFactor, scaleFactor});
		}
	}

	@Deprecated
	public void centerScale()
	/*
	 * Position the model so it's center is at the origin, and scale it so its longest dimension is no bigger than
	 * maxSize.
	 */
	{
		// calculate a scale factor
		float scaleFactor = 1.0f;
		float largest = modelDimensions.getLargest();
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (1.0f / largest);
		Log.i("Object3DData","Scaling model with factor: " + scaleFactor+". Largest: "+largest);

		// get the model's center point
		Tuple3 center = modelDimensions.getCenter();
		Log.i("Object3DData","Objects actual position: " + center.toString());

		// modify the model's vertices
		float x0, y0, z0;
		float x, y, z;
		FloatBuffer vertexBuffer = getVertexBuffer() != null? getVertexBuffer() : getVertexArrayBuffer();
		for (int i = 0; i < vertexBuffer.capacity()/3; i++) {
			x0 = vertexBuffer.get(i*3);
			y0 = vertexBuffer.get(i*3+1);
			z0 = vertexBuffer.get(i*3+2);
			x = (x0 - center.getX()) * scaleFactor;
			vertexBuffer.put(i*3,x);
			y = (y0 - center.getY()) * scaleFactor;
			vertexBuffer.put(i*3+1,y);
			z = (z0 - center.getZ()) * scaleFactor;
			vertexBuffer.put(i*3+2,z);
		}
	} // end of centerScale()

    public void addError(String error) {
		errors.add(error);
    }

    public List<String> getErrors(){
		return errors;
	}
}
