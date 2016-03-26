package org.andresoviedo.app.util.math;

import android.opengl.Matrix;

/**
 * Utility class to calculate 3D stuff
 * 
 * @author andresoviedo
 *
 */
public class Math3DUtils {

	/**
	 * Calculate the 2 vectors, that is a line (x1,y1,z1-x2,y2,z2} corresponding to the normal of the specified face.
	 * The calculated line will be positioned exactly in the middle of the face
	 * 
	 * @param v0
	 *            the first vector of the face
	 * @param v1
	 *            the second vector of the face
	 * @param v2
	 *            the third vector of the face
	 * @return the 2 vectors (line) corresponding to the face normal
	 */
	public static float[][] calculateFaceNormal(float[] v0, float[] v1, float[] v2) {

		// calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
		float[] va = new float[] { v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2] };
		float[] vb = new float[] { v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2] };
		float[] n = new float[] { va[1] * vb[2] - va[2] * vb[1], va[2] * vb[0] - va[0] * vb[2],
				va[0] * vb[1] - va[1] * vb[0] };
		float modul = Matrix.length(n[0], n[1], n[2]);
		float[] vn = new float[] { n[0] / modul, n[1] / modul, n[2] / modul };

		// calculate center of the face
		float[] faceCenter = new float[] { (v0[0] + v1[0] + v2[0]) / 3, (v0[1] + v1[1] + v2[1]) / 3,
				(v0[2] + v1[2] + v2[2]) / 3 };
		float[] vn2 = new float[] { faceCenter[0] + vn[0], faceCenter[1] + vn[1], faceCenter[2] + vn[2] };
		@SuppressWarnings("unused")
		String msg = "fNormal(" + v0[0] + "," + v0[1] + "," + v0[2] + "#" + v1[0] + "," + v1[1] + "," + v1[2] + "#"
				+ v2[0] + "," + v2[1] + "," + v2[2] + ")#normal(" + vn[0] + "," + vn[1] + "," + vn[2] + ") center("
				+ faceCenter[0] + "," + faceCenter[1] + "," + faceCenter[2] + ") to(" + vn2[0] + "," + vn2[1] + ","
				+ vn2[2] + ")";
		// Log.d("ObjectV4", msg);
		return new float[][] { faceCenter, vn2 };
	}

}
