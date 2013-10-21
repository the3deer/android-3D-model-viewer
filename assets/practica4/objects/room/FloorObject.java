package practica4.objects.room;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class FloorObject extends EmbededObject3D {
	private File textureFloorFile = new File(
			"C:\\TEGR\\practica1\\textures\\panton_carpet.bmp");

	static final int FLOOR_SIZE = 1;

	private Texture floorTexture;

	public FloorObject() {
		super();
		setScale(new float[]{1000, 1000, 1000});
	}

	public int createCallList(GL gl, boolean drawSolid, boolean lightOn,
			boolean textureOn) {
		int room = gl.glGenLists(1);
		gl.glNewList(room, GL.GL_COMPILE);
		try {
			floorTexture = TextureIO.newTexture(textureFloorFile, true);
		} catch (GLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// Draw walls
		if (lightOn) {
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, new float[]{128.0F},
					0);
		}
		if (textureOn) {
			floorTexture.enable();
			floorTexture.bind();
			if (FLOOR_SIZE > 1) {
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
						GL.GL_REPEAT);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
						GL.GL_REPEAT);
			}
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
					GL.GL_REPLACE);
		}
		gl.glColor3f(0.7F, 0.7F, 0.7F);
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, FLOOR_SIZE);
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glTexCoord2f(FLOOR_SIZE, FLOOR_SIZE);
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(1.0F, 0, 0);
		gl.glTexCoord2f(FLOOR_SIZE, 0);
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(1.0F, 0, 1.0F);
		gl.glTexCoord2f(0, 0);
		gl.glNormal3f(0, 1, 0);
		gl.glVertex3f(0, 0, 1.0F);
		gl.glEnd();
		if (textureOn && floorTexture != null) {
			floorTexture.disable();
		}
		gl.glEndList();
		return room;
	}
}
