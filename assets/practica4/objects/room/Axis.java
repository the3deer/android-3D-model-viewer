package practica4.objects.room;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class Axis extends EmbededObject3D {

	public Axis() {
		vertices.add(new float[] { 0, 0, 0 });
		vertices.add(new float[] { 1, 0, 0 });
		vertices.add(new float[] { 0, 0, 0 });
		vertices.add(new float[] { 0, 1, 0 });
		vertices.add(new float[] { 0, 0, 0 });
		vertices.add(new float[] { 0, 0, 1 });
	}

	public int createCallList(GL10 gl, boolean drawSolid, boolean lightOn,
			boolean textureOn) {
		int cl = gl.glGenLists(1);
		gl.glNewList(cl, GL10.GL_COMPILE);
		gl.glColor3f(0F, 0F, 0F);
		gl.glBegin(GL10.GL_LINES);
		gl.glColor3fv(new float[] { 0F, 0F, 0F }, 0);
		gl.glVertex3fv(new float[] { 0, 0, 0 }, 0);
		gl.glVertex3fv(new float[] { 1, 0, 0 }, 0);
		gl.glVertex3fv(new float[] { 0, 0, 0 }, 0);
		gl.glVertex3fv(new float[] { 0, 1, 0 }, 0);
		gl.glVertex3fv(new float[] { 0, 0, 0 }, 0);
		gl.glVertex3fv(new float[] { 0, 0, 1 }, 0);

		// Arrow X>
		gl.glVertex3fv(new float[] { 0.95F, 0.05F, 0 }, 0);
		gl.glVertex3fv(new float[] { 1, 0, 0 }, 0);
		gl.glVertex3fv(new float[] { 0.95F, -0.05F, 0 }, 0);
		gl.glVertex3fv(new float[] { 1, 0, 0 }, 0);

		// Arrow Y^
		gl.glVertex3fv(new float[] { -0.05F, 0.95F, 0 }, 0);
		gl.glVertex3fv(new float[] { 0, 1, 0 }, 0);
		gl.glVertex3fv(new float[] { 0.05F, 0.95F, 0 }, 0);
		gl.glVertex3fv(new float[] { 0, 1, 0 }, 0);

		// Arrow Zv
		gl.glVertex3fv(new float[] { -0.05F, 0, 0.95F }, 0);
		gl.glVertex3fv(new float[] { 0, 0, 1 }, 0);
		gl.glVertex3fv(new float[] { 0.05F, 0, 0.95F }, 0);
		gl.glVertex3fv(new float[] { 0, 0, 1 }, 0);

		// Letter X
		gl.glVertex3fv(new float[] { 1.05F, 0.05F, 0 }, 0);
		gl.glVertex3fv(new float[] { 1.10F, -0.05F, 0 }, 0);
		gl.glVertex3fv(new float[] { 1.05F, -0.05F, 0 }, 0);
		gl.glVertex3fv(new float[] { 1.10F, 0.05F, 0 }, 0);

		// Letter Y
		gl.glVertex3fv(new float[] { -0.05F, 1.05F, 0 }, 0);
		gl.glVertex3fv(new float[] { 0.05F, 1.10F, 0 }, 0);
		gl.glVertex3fv(new float[] { -0.05F, 1.10F, 0 }, 0);
		gl.glVertex3fv(new float[] { 0.0F, 1.075F, 0 }, 0);

		// Letter Z
		gl.glVertex3fv(new float[] { -0.05F, 0.05F, 1.05F }, 0);
		gl.glVertex3fv(new float[] { 0.05F, 0.05F, 1.05F }, 0);
		gl.glVertex3fv(new float[] { 0.05F, 0.05F, 1.05F }, 0);
		gl.glVertex3fv(new float[] { -0.05F, -0.05F, 1.05F }, 0);
		gl.glVertex3fv(new float[] { -0.05F, -0.05F, 1.05F }, 0);
		gl.glVertex3fv(new float[] { 0.05F, -0.05F, 1.05F }, 0);

		gl.glEnd();
		gl.glEndList();
		return cl;
	}

	@Override
	public int createCallList(GL gl, boolean drawSolid, boolean lightOn,
			boolean textureOn) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int createCallList(practica4.objects.room.GL gl, boolean drawSolid,
			boolean lightOn, boolean textureOn) {
		// TODO Auto-generated method stub
		return 0;
	}
}
