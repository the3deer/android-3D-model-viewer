package org.andresoviedo.android_3d_model_engine.services;

import android.opengl.GLES20;

import org.andresoviedo.android_3d_model_engine.model.BoundingBox;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class BoundingBoxBuilder {

	// number of coordinates per vertex in this array
	protected static final int COORDS_PER_VERTEX = 3;

	public FloatBuffer vertices;
	public IntBuffer drawOrder;

	/**
	 * Build a bounding box for the specified 3D object vertex buffer.
	 *
	 * @param color        the color of the bounding box
	 */
	public BoundingBoxBuilder(BoundingBox box, float[] color) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				8 * COORDS_PER_VERTEX * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asFloatBuffer();

		ByteBuffer bb2 = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per int)
				(6 * 4) * 4);
		// use the device hardware's native byte order
		bb2.order(ByteOrder.nativeOrder());
		drawOrder = bb2.asIntBuffer();

		// back-face
		drawOrder.put( 0);
		drawOrder.put( 1);
		drawOrder.put( 2);
		drawOrder.put( 3);

		// front-face
		drawOrder.put( 4);
		drawOrder.put( 5);
		drawOrder.put( 6);
		drawOrder.put( 7);

		// left-face
		drawOrder.put( 4);
		drawOrder.put( 5);
		drawOrder.put( 1);
		drawOrder.put( 0);

		// right-face
		drawOrder.put( 3);
		drawOrder.put( 2);
		drawOrder.put( 6);
		drawOrder.put( 7);

		// top-face
		drawOrder.put( 1);
		drawOrder.put( 2);
		drawOrder.put( 6);
		drawOrder.put( 5);

		// bottom-face
		drawOrder.put( 0);
		drawOrder.put( 3);
		drawOrder.put( 7);
		drawOrder.put( 4);

		calculateVertex(box);
	}

	public IntBuffer getDrawOrder() {
		return drawOrder;
	}

	public int getDrawMode() {
		return GLES20.GL_LINE_LOOP;
	}

	public int getDrawSize() {
		return 4;
	}

	public List<int[]> getDrawModeList() {
		List<int[]> ret = new ArrayList<int[]>();
		int drawOrderPos = 0;
		for (int i = 0; i < drawOrder.capacity(); i += 4) {
			ret.add(new int[]{GLES20.GL_LINE_LOOP, drawOrderPos, 4});
			drawOrderPos += 4;
		}
		return ret;
	}

	private void calculateVertex(BoundingBox box) {
		//@formatter:off
		vertices.put(box.getxMin()).put(box.getyMin()).put(box.getzMin());  // down-left (far)
		vertices.put(box.getxMin()).put(box.getyMax()).put(box.getzMin());  // up-left (far)
		vertices.put(box.getxMax()).put(box.getyMax()).put(box.getzMin());  // up-right (far)
		vertices.put(box.getxMax()).put(box.getyMin()).put(box.getzMin());  // down-right  (far)
		vertices.put(box.getxMin()).put(box.getyMin()).put(box.getzMax());  // down-left (near)
		vertices.put(box.getxMin()).put(box.getyMax()).put(box.getzMax());  // up-left (near)
		vertices.put(box.getxMax()).put(box.getyMax()).put(box.getzMax());  // up-right (near)
		vertices.put(box.getxMax()).put(box.getyMin()).put(box.getzMax());  // down-right (near)
		//@formatter:on
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
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

}
