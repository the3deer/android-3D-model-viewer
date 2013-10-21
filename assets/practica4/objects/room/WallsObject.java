package practica4.objects.room;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class WallsObject extends EmbededObject3D {
	private File textureWallFile = new File(
			"C:\\TEGR\\practica1\\textures\\wall2.bmp");

	static final int WALL_SIZE = 1;

	private Texture textureWall;

	public WallsObject() {
		super();
		setScale(new float[]{1000, 1000, 1000});
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
					new float[]{0.0F, 1.0F, 1.0F}, 0);
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
				textureWall = TextureIO.newTexture(textureWallFile, true);
				textureWall.enable();
				textureWall.bind();
				if (WALL_SIZE > 1) {
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
		// Front wall
		gl.glColor3f(0.0F, 1.0F, 1.0F);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE,
				new float[]{0.0F, 1.0F, 1.0F}, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, new float[]{1.0F,
				1.0F, 1.0F}, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS,
				new float[]{128.0F}, 0);
		gl.glTexCoord2f(0, 0);
		gl.glNormal3f(0, 0, 1);
		gl.glVertex3f(0F, 0F, 0F);
		gl.glTexCoord2f(WALL_SIZE, 0);
		gl.glNormal3f(0, 0, 1);
		gl.glVertex3f(1.0F, 0F, 0F);
		gl.glTexCoord2f(WALL_SIZE, WALL_SIZE);
		gl.glNormal3f(0, 0, 1);
		gl.glVertex3f(1.0F, 1.0F, 0F);
		gl.glTexCoord2f(0, WALL_SIZE);
		gl.glNormal3f(0, 0, 1);
		gl.glVertex3f(0F, 1.0F, 0F);
		// Left wall
		gl.glColor3f(1.0F, 0.0F, 0.0F);
		gl.glNormal3f(1, 0, 0);
		gl.glTexCoord2f(WALL_SIZE, 0);
		// gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0F, 0F, 0F);
		gl.glTexCoord2f(0, 0);
		// gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0F, 0F, 1.0F);
		gl.glTexCoord2f(0, WALL_SIZE);
		// gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0F, 1.0F, 1.0F);
		gl.glTexCoord2f(WALL_SIZE, WALL_SIZE);
		// gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0F, 1.0F, 0F);
		// Right wall
		gl.glColor3f(0.0F, 0.0F, 1.0F);
		gl.glTexCoord2f(0, 0);
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(1.0F, 0F, 0F);
		gl.glTexCoord2f(0, WALL_SIZE);
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(1.0F, 1.0F, 0F);
		gl.glTexCoord2f(WALL_SIZE, WALL_SIZE);
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(1.0F, 1.0F, 1.0F);
		gl.glTexCoord2f(WALL_SIZE, 0);
		gl.glNormal3f(-1, 0, 0);
		gl.glVertex3f(1.0F, 0F, 1.0F);
		// Back wall
		gl.glColor3f(0.0F, 0.0F, 0.0F);
		gl.glTexCoord2f(0, 0);
		gl.glNormal3f(0, 0, -1);
		gl.glVertex3f(0F, 0F, 1.0F);
		gl.glTexCoord2f(WALL_SIZE, 0);
		gl.glNormal3f(0, 0, -1);
		gl.glVertex3f(1.0F, 0F, 1.0F);
		gl.glTexCoord2f(WALL_SIZE, WALL_SIZE);
		gl.glNormal3f(0, 0, -1);
		gl.glVertex3f(1.0F, 1.0F, 1.0F);
		gl.glTexCoord2f(0, WALL_SIZE);
		gl.glNormal3f(0, 0, -1);
		gl.glVertex3f(0F, 1.0F, 1.0F);
		// Floor
		gl.glEnd();
		if (textureOn && textureWall != null) {
			textureWall.disable();
		}
		gl.glEndList();
		return room;
	}
}
