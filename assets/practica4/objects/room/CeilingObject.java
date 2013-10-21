package practica4.objects.room;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class CeilingObject extends EmbededObject3D {
	private File textureCeilingFile = new File(
			"C:\\TEGR\\practica1\\textures\\ceiling_white.bmp");

	static final int CEILING_SIZE = 10;

	private Texture textureCeiling;

	public CeilingObject() {
		super();
		setScale(new float[]{1000, 500, 1000});
	}

	public int createCallList(GL gl, boolean drawSolid, boolean lightOn,
			boolean textureOn) {
		int room = gl.glGenLists(1);
		gl.glNewList(room, GL.GL_COMPILE);

		if (lightOn) {
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);
			// Set material
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE,
					new float[]{1.0F, 1.0F, 1.0F}, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, new float[]{1.0F,
					1.0F, 1.0F}, 0);
			gl
					.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS,
							new float[]{50.0F}, 0);
		} else {
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDisable(GL.GL_LIGHT0);
		}
		// Draw walls
		if (textureOn) {
			try {
				textureCeiling = TextureIO.newTexture(textureCeilingFile, true);
				textureCeiling.enable();
				textureCeiling.bind();
				if (CEILING_SIZE > 1) {
					gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
							GL.GL_REPEAT);
					gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
							GL.GL_REPEAT);
				}
				// Replace color with texture
				gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE,
						GL.GL_REPLACE);
			} catch (GLException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		gl.glBegin(GL.GL_QUADS);
		gl.glColor3f(1.0F, 1.0F, 0.0F);
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, CEILING_SIZE);
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(0, 1.0F, 0);
		gl.glTexCoord2f(CEILING_SIZE, CEILING_SIZE);
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(1.0F, 1.0F, 0);
		gl.glTexCoord2f(CEILING_SIZE, 0);
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(1.0F, 1.0F, 1.0F);
		gl.glTexCoord2f(0, 0);
		gl.glNormal3f(0, -1, 0);
		gl.glVertex3f(0, 1.0F, 1.0F);
		gl.glEnd();
		gl.glLineWidth(1.0F);
		if (textureOn && textureCeiling != null) {
			textureCeiling.disable();
		}
		gl.glEndList();
		return room;
	}
}
