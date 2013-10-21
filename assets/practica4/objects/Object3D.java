package practica4.objects;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public abstract class Object3D {

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

	public int glList;

	public BoundingBox getBoundingBox() {
		calculateBoundingBox();
		return boundingBox;
	}

	public void reshape(float[] sceneCenter, float xMax, float yMax, float zMax) {
		calculateBoundingBox();

		// calculate scale to reference size
		float[] sizes = boundingBox.sizes;

		// calculate max scale
		float maxScale = Math.min(Math.min(xMax / sizes[0], yMax / sizes[1]),
				zMax / sizes[2]);

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

	protected void calculateBoundingBox() {
		if (boundingBox != null || vertices == null || vertices.isEmpty()) {
			return;
		}
		boundingBox = new BoundingBox(vertices.get(0));
		for (float[] vertex : vertices) {
			if (vertex[0] < boundingBox.xMin) {
				boundingBox.xMin = vertex[0];
			} else if (vertex[0] > boundingBox.xMax) {
				boundingBox.xMax = vertex[0];
			}
			if (vertex[1] < boundingBox.yMin) {
				boundingBox.yMin = vertex[1];
			} else if (vertex[1] > boundingBox.yMax) {
				boundingBox.yMax = vertex[1];
			}
			if (vertex[2] < boundingBox.zMin) {
				boundingBox.zMin = vertex[2];
			} else if (vertex[2] > boundingBox.zMax) {
				boundingBox.zMax = vertex[2];
			}
		}
		boundingBox.revalidate();

		// calculated bounding sphere
		double radius = 0;
		double radiusTemp;
		float[] center = boundingBox.center;
		for (float[] vertex : vertices) {
			radiusTemp = Math.sqrt(Math.pow(vertex[0] - center[0], 2)
					+ Math.pow(vertex[1] - center[1], 2)
					+ Math.pow(vertex[2] - center[2], 2));
			if (radiusTemp > radius) {
				radius = radiusTemp;
			}
		}
		boundingBox.radius = (float) radius;
	}

	public String currentSizeToString() {
		if (boundingBox != null) {
			return "x[" + boundingBox.sizes[0] * scale[0] + "],y["
					+ boundingBox.sizes[1] * scale[0] + "],z["
					+ boundingBox.sizes[2] * scale[0] + "]";
		}
		return null;
	}

	public void drawBoundingBox(GL10 gl, float[] color, float size) {
		calculateBoundingBox();
		List<float[]> vertices = boundingBox.vertices;
		gl.glColor3fv(color, 0);
		gl.glLineWidth(size);
		gl.glEnable(GL.GL_LINE_STIPPLE);
		// linePattern = (short) ((linePattern >> 1) | ((linePattern & 0x0001)
		// << 16));
		// System.out.println("linePattern[" + linePattern + "]");
		gl.glLineStipple(8, linePattern);
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3fv(vertices.get(0), 0);
		gl.glVertex3fv(vertices.get(1), 0);
		gl.glVertex3fv(vertices.get(2), 0);
		gl.glVertex3fv(vertices.get(3), 0);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3fv(vertices.get(4), 0);
		gl.glVertex3fv(vertices.get(5), 0);
		gl.glVertex3fv(vertices.get(6), 0);
		gl.glVertex3fv(vertices.get(7), 0);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3fv(vertices.get(0), 0);
		gl.glVertex3fv(vertices.get(1), 0);
		gl.glVertex3fv(vertices.get(5), 0);
		gl.glVertex3fv(vertices.get(4), 0);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3fv(vertices.get(3), 0);
		gl.glVertex3fv(vertices.get(2), 0);
		gl.glVertex3fv(vertices.get(6), 0);
		gl.glVertex3fv(vertices.get(7), 0);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3fv(vertices.get(1), 0);
		gl.glVertex3fv(vertices.get(2), 0);
		gl.glVertex3fv(vertices.get(6), 0);
		gl.glVertex3fv(vertices.get(5), 0);
		gl.glEnd();
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex3fv(vertices.get(0), 0);
		gl.glVertex3fv(vertices.get(3), 0);
		gl.glVertex3fv(vertices.get(7), 0);
		gl.glVertex3fv(vertices.get(4), 0);
		gl.glEnd();
		gl.glLineWidth(1F);
		gl.glDisable(GL.GL_LINE_STIPPLE);
	}

	public int createCallList(GL gl) {
		return glList;
	}

	public static void main(String[] args) {
		short linePattern = (short) 0x0101;
		for (int i = 0; i < 10; i++) {
			System.out.print(((linePattern & 0x0001) << 15) + " -> ");
			linePattern = (short) ((linePattern >> 1) + ((linePattern & 0x0001) << 15));
			System.out.println("linePattern[" + linePattern + "]");
		}

	}
}