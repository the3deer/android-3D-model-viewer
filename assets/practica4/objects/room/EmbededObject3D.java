package practica4.objects.room;

import javax.media.opengl.GL;

import practica4.objects.Object3D;

public abstract class EmbededObject3D extends Object3D {
	public abstract int createCallList(GL gl, boolean drawSolid,
			boolean lightOn, boolean textureOn);

	public String toString() {
		return this.getClass().getName().substring(
				this.getClass().getPackage().getName().length() + 1);
	}
}
