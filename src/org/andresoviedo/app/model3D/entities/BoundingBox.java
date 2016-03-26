package org.andresoviedo.app.model3D.entities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;

/**
 * Calculates the bounding box around a 3D Object.
 * 
 * @author Andres Oviedo
 * 
 */
public class BoundingBox {

	// number of coordinates per vertex in this array
	protected static final int COORDS_PER_VERTEX = 3;
	protected static final int COORDS_PER_COLOR = 4;

	public FloatBuffer vertices;
	public FloatBuffer vertexArray;
	public FloatBuffer colors;
	public ShortBuffer drawOrder;

	public float xMin;
	public float xMax;
	public float yMin;
	public float yMax;
	public float zMin;
	public float zMax;

	public float[] center;
	public float[] sizes;
	public float radius;

	/**
	 * Build a bounding box for the specified 3D object vertex buffer.
	 * 
	 * @param vertexBuffer
	 *            the 3D object vertex buffer
	 * @param color
	 *            the color of the bounding box
	 */
	public BoundingBox(FloatBuffer vertexBuffer, float[] color) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				8 * COORDS_PER_VERTEX * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asFloatBuffer();

		ByteBuffer bb2 = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				(6 * 4) * 2);
		// use the device hardware's native byte order
		bb2.order(ByteOrder.nativeOrder());
		drawOrder = bb2.asShortBuffer();
		drawOrder.position(0);

		// vertex colors
		ByteBuffer bb3 = ByteBuffer.allocateDirect(24 * COORDS_PER_COLOR * 4);
		// use the device hardware's native byte order
		bb3.order(ByteOrder.nativeOrder());
		colors = bb3.asFloatBuffer();

		colors.position(0);
		for (int i = 0; i < colors.capacity() / 4; i++) {
			if (color != null && color.length == 4) {
				colors.put(color);
			} else {
				colors.put(1.0f).put(0.0f).put(1.0f).put(1.0f);
			}
		}

		// back-face
		drawOrder.put((short) 0);
		drawOrder.put((short) 1);
		drawOrder.put((short) 2);
		drawOrder.put((short) 3);

		// front-face
		drawOrder.put((short) 4);
		drawOrder.put((short) 5);
		drawOrder.put((short) 6);
		drawOrder.put((short) 7);

		// left-face
		drawOrder.put((short) 4);
		drawOrder.put((short) 5);
		drawOrder.put((short) 1);
		drawOrder.put((short) 0);

		// right-face
		drawOrder.put((short) 3);
		drawOrder.put((short) 2);
		drawOrder.put((short) 6);
		drawOrder.put((short) 7);

		// top-face
		drawOrder.put((short) 1);
		drawOrder.put((short) 2);
		drawOrder.put((short) 6);
		drawOrder.put((short) 5);

		// bottom-face
		drawOrder.put((short) 0);
		drawOrder.put((short) 3);
		drawOrder.put((short) 7);
		drawOrder.put((short) 4);

		recalculate(vertexBuffer);
	}

	public ShortBuffer getDrawOrder() {
		return drawOrder;
	}

	public FloatBuffer getColors() {
		return colors;
	}

	public int getDrawMode() {
		return GLES20.GL_LINE_LOOP;
	}

	public int getDrawSize() {
		return 4;
	}

	public List<int[]> getDrawModeList() {
		List<int[]> ret = new ArrayList<int[]>();
		int vertexPos = 0;
		for (int i = 0; i < drawOrder.capacity() / 4; i++) {
			ret.add(new int[] { GLES20.GL_LINE_LOOP, vertexPos, 4 });
			vertexPos += 4;
		}
		return ret;
	}

	BoundingBox(FloatBuffer vertexBuffer, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		calculateVertex();
		calculateOther(vertexBuffer);
	}

	public void recalculate(FloatBuffer vertexBuffer) {

		calculateMins(vertexBuffer);
		calculateVertex();
		calculateOther(vertexBuffer);
	}

	/**
	 * This works only when COORDS_PER_VERTEX = 3
	 * 
	 * @param vertexBuffer
	 */
	private void calculateMins(FloatBuffer vertexBuffer) {
		vertexBuffer.position(0);
		while (vertexBuffer.hasRemaining()) {
			float vertexx = vertexBuffer.get();
			float vertexy = vertexBuffer.get();
			float vertexz = vertexBuffer.get();
			if (vertexx < xMin) {
				xMin = vertexx;
			} else if (vertexx > xMax) {
				xMax = vertexx;
			}
			if (vertexy < yMin) {
				yMin = vertexy;
			} else if (vertexy > yMax) {
				yMax = vertexy;
			}
			if (vertexz < zMin) {
				zMin = vertexz;
			} else if (vertexz > zMax) {
				zMax = vertexz;
			}
		}
	}

	private void calculateVertex() {
		vertices.position(0);
		//@formatter:off
		vertices.put(xMin).put(yMin).put(zMin);  // down-left (far)
		vertices.put(xMin).put(yMax).put(zMin);  // up-left (far)
		vertices.put(xMax).put(yMax).put(zMin);  // up-right (far)
		vertices.put(xMax).put(yMin).put(zMin);  // down-right  (far)
		vertices.put(xMin).put(yMin).put(zMax);  // down-left (near)
		vertices.put(xMin).put(yMax).put(zMax);  // up-left (near)
		vertices.put(xMax).put(yMax).put(zMax);  // up-right (near)
		vertices.put(xMax).put(yMin).put(zMax);  // down-right (near)
		//@formatter:on
	}

	private void calculateOther(FloatBuffer vertexBuffer) {
		center = new float[] { (xMax + xMin) / 2, (yMax + yMin) / 2, (zMax + zMin) / 2 };
		sizes = new float[] { xMax - xMin, yMax - yMin, zMax - zMin };

		vertexBuffer.position(0);

		// calculated bounding sphere
		double radius = 0;
		double radiusTemp;
		vertexBuffer.position(0);
		while (vertexBuffer.hasRemaining()) {
			float vertexx = vertexBuffer.get();
			float vertexy = vertexBuffer.get();
			float vertexz = vertexBuffer.get();
			radiusTemp = Math.sqrt(Math.pow(vertexx - center[0], 2) + Math.pow(vertexy - center[1], 2)
					+ Math.pow(vertexz - center[2], 2));
			if (radiusTemp > radius) {
				radius = radiusTemp;
			}
		}
		this.radius = (float) radius;
	}

	public FloatBuffer getVertices() {
		return vertices;
	}

	public FloatBuffer getVertexArray() {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				drawOrder.capacity() * COORDS_PER_VERTEX * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer ret = bb.asFloatBuffer();
		ret.position(0);
		for (int i = 0; i < drawOrder.capacity(); i++) {
			ret.put(vertices.get(drawOrder.get(i) * 3)); // x
			ret.put(vertices.get(drawOrder.get(i) * 3 + 1)); // y
			ret.put(vertices.get(drawOrder.get(i) * 3 + 2)); // z
		}
		return ret;
	}

	public String sizeToString() {
		return "x[" + sizes[0] + "],y[" + sizes[1] + "],z[" + sizes[2] + "]";
	}

	public String centerToString() {
		return "x[" + center[0] + "],y[" + center[1] + "],z[" + center[2] + "]";
	}

	public String limitsToString() {
		StringBuffer ret = new StringBuffer();
		ret.append("xMin[" + xMin + "], xMax[" + xMax + "], yMin[" + yMin + "], yMax[" + yMax + "], zMin[" + zMin
				+ "], zMax[" + zMax + "]");
		return ret.toString();
	}

	public float[] getCenter() {
		return center;
	}

	public void setCenter(float[] center) {
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public FloatBuffer getNormals() {
		return createEmptyNormalsFloatBuffer(getVertices().capacity());
	}

	private static FloatBuffer createEmptyNormalsFloatBuffer(int size) {
		FloatBuffer buffer = createNativeByteBuffer(size * 3 * 4).asFloatBuffer();
		buffer.position(0);
		for (int i = 0; i < size; i++) {
			buffer.put(0.0f).put(1.0f).put(0.0f);
		}
		return buffer;
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

}