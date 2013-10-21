package practica4.objects;

import javax.media.opengl.GL;

import com.sun.opengl.util.GLUT;

public class WaterDrop extends Object3D {

	static int uniqueGlList = -1;

	public WaterDrop() {
		vertices.add(new float[] { -0.05F, 0.05F, 0.05F });
		vertices.add(new float[] { 0.05F, 0.05F, 0.05F });
		vertices.add(new float[] { -0.05F, -0.05F, 0.05F });
		vertices.add(new float[] { 0.05F, -0.05F, 0.05F });
		vertices.add(new float[] { -0.05F, 0.05F, -0.05F });
		vertices.add(new float[] { 0.05F, 0.05F, -0.05F });
		vertices.add(new float[] { -0.05F, -0.05F, -0.05F });
		vertices.add(new float[] { 0.05F, -0.05F, -0.05F });

		// vertices.add(new float[]{-1.1F, 1.1F, 1.1F});
		// vertices.add(new float[]{1.1F, 1.1F, 1.1F});
		// vertices.add(new float[]{-1.1F, -1.1F, 1.1F});
		// vertices.add(new float[]{1.1F, -1.1F, 1.1F});
		// vertices.add(new float[]{-1.1F, 1.1F, -1.1F});
		// vertices.add(new float[]{1.1F, 1.1F, -1.1F});
		// vertices.add(new float[]{-1.1F, -1.1F, -1.1F});
		// vertices.add(new float[]{1.1F, -1.1F, -1.1F});
	}

	public int createCallList(GL gl) {
		GLUT glut = new GLUT();
		if (uniqueGlList == -1) {
			uniqueGlList = gl.glGenLists(1);
			gl.glNewList(uniqueGlList, GL.GL_COMPILE);
			gl.glPushName(id);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex3f(0F, 0F, 0F);
			gl.glEnd();
			// Set color
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE,
					new float[] { 0.9F, 0.9F, 0.9F, 1.0F }, 0);
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, mat_specular,
					0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission, 0);

			glut.glutSolidSphere(0.05, 10, 10);
			gl.glPopName();
			gl.glEndList();
			super.glList = uniqueGlList;
		}
		return uniqueGlList;
	}
}
