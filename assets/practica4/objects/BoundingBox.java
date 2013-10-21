package practica4.objects;

import java.util.List;
import java.util.Vector;

public class BoundingBox {
	public float xMin;
	public float xMax;
	public float yMin;
	public float yMax;
	public float zMin;
	public float zMax;
	public List<float[]> vertices;
	public float[] center;
	public float[] sizes;
	public float radius;

	BoundingBox(float[] vertex) {
		this(vertex[0], vertex[0], vertex[1], vertex[1], vertex[2], vertex[2]);
	}
	BoundingBox(float xMin, float xMax, float yMin, float yMax, float zMin,
			float zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
	}

	void revalidate() {
		vertices = new Vector<float[]>(6);
		vertices.add(new float[]{xMin, yMin, zMin});
		vertices.add(new float[]{xMin, yMax, zMin});
		vertices.add(new float[]{xMax, yMax, zMin});
		vertices.add(new float[]{xMax, yMin, zMin});
		vertices.add(new float[]{xMin, yMin, zMax});
		vertices.add(new float[]{xMin, yMax, zMax});
		vertices.add(new float[]{xMax, yMax, zMax});
		vertices.add(new float[]{xMax, yMin, zMax});
		center = new float[]{(xMax + xMin) / 2, (yMax + yMin) / 2,
				(zMax + zMin) / 2};
		sizes = new float[]{xMax - xMin, yMax - yMin, zMax - zMin};
	}

	public String sizeToString() {
		return "x[" + sizes[0] + "],y[" + sizes[1] + "],z[" + sizes[2] + "]";
	}

	public String centerToString() {
		return "x[" + center[0] + "],y[" + center[1] + "],z[" + center[2] + "]";
	}

	public String limitsToString() {
		StringBuffer ret = new StringBuffer();
		ret.append("xMin[" + xMin + "], xMax[" + xMax + "], yMin[" + yMin
				+ "], yMax[" + yMax + "], zMin[" + zMin + "], zMax[" + zMax
				+ "]");
		return ret.toString();
	}

}