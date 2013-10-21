package practica4.objects.room;

import javax.media.opengl.GL;

import practica4.objects.Object3D;

import com.sun.opengl.util.GLUT;

public class LightBulb extends Object3D {

	public LightBulb() {
		super();
		vertices.add(new float[] { -0.25F, 0.25F, 0.25F });
		vertices.add(new float[] { 0.25F, 0.25F, 0.25F });
		vertices.add(new float[] { 0.25F, -0.25F, 0.25F });
		vertices.add(new float[] { -0.25F, -0.25F, 0.25F });

		vertices.add(new float[] { -0.25F, 0.25F, -0.25F });
		vertices.add(new float[] { 0.25F, 0.25F, -0.25F });
		vertices.add(new float[] { 0.25F, -0.25F, -0.25F });
		vertices.add(new float[] { -0.25F, -0.25F, -0.25F });
	}

	public int createCallList(GL gl) {
		int light = gl.glGenLists(1);
		gl.glNewList(light, GL.GL_COMPILE);
		
		// gl.glDisable(GL.GL_LIGHTING);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE,
				new float[] { 1.0F, 1.0F, 0.0F, 1.0F }, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, new float[] {
				1.0F, 1.0F, 0.0F, 1.0F }, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,
				new float[] { 128 }, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, new float[] {
				1.0F, 1.0F, 0.0F, 1.0F }, 0);
		gl.glColor3f(1.0F, 1.0F, 0.0F);
		GLUT glut = new GLUT();
		glut.glutSolidSphere(0.25F, 20, 16);
		// gl.glEnable(GL.GL_LIGHTING);
		
		gl.glEndList();
		return light;
	}
}
