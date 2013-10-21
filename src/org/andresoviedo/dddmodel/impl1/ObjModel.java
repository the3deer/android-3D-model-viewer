package org.andresoviedo.dddmodel.impl1;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class ObjModel {
	private boolean hasNormalCoords;
	private boolean hasTextureCoords;
	private int hasTextureW;
	private FloatBuffer normalBuffer;
	private int numFaces;
	private FloatBuffer textureBuffer;
	private int[] textures = new int[3];
	private FloatBuffer vertexBuffer;
	private boolean wireframe = false;

	public ObjModel(float[] vertices, float[] texture, float[] normals,
			int hasTextureW) {

		super();

		this.hasTextureW = hasTextureW;
		this.wireframe = false;

		this.numFaces = vertices.length;

		if (texture.length > 0) {
			this.hasTextureCoords = true; // 41 ok
			initializeTextureBuffer(texture); // 42 ok
		} else {
			this.hasTextureCoords = false; // 46 ok
		//	GLActivity.noModelNormals = true; // 47 ok
		}

		if (normals.length > 0) // 50 ok
		{
			this.hasNormalCoords = true; // 52 ok
			initializeNormalBuffer(normals); // 53 ok
		} else {
			this.hasNormalCoords = false; // 57 ok
		//	GLActivity.noModelTextureCoords = true; // 58 ok
		}

		initializeVertexBuffer(vertices); // 61 ok
		return;
	}

	private void bindTexture(GL10 paramGL10, Bitmap paramBitmap) {
		paramGL10.glGenTextures(3, this.textures, 0); // 206

		paramGL10.glBindTexture(3553, this.textures[0]);
		paramGL10.glTexParameterf(3553, 10240, 9728.0F);
		paramGL10.glTexParameterf(3553, 10241, 9728.0F);
		GLUtils.texImage2D(3553, 0, paramBitmap, 0);
		paramGL10.glBindTexture(3553, this.textures[1]);
		paramGL10.glTexParameterf(3553, 10240, 9729.0F);
		paramGL10.glTexParameterf(3553, 10241, 9729.0F);
		GLUtils.texImage2D(3553, 0, paramBitmap, 0);
		paramGL10.glBindTexture(3553, this.textures[2]);
		paramGL10.glTexParameterf(3553, 10240, 9729.0F);
		paramGL10.glTexParameterf(3553, 10241, 9985.0F);
		if ((paramGL10 instanceof GL11)) {
			paramGL10.glTexParameterf(3553, 33169, 1.0F);
			GLUtils.texImage2D(3553, 0, paramBitmap, 0);
		}
		buildMipmap(paramGL10, paramBitmap);
		paramBitmap.recycle(); // 236 ok
	}

	private void buildMipmap(GL10 paramGL10, Bitmap paramBitmap) {
		int i = 0;
		int j = paramBitmap.getHeight();
		int k = paramBitmap.getWidth();
		while (true) {
			if ((j < 1) && (k < 1)) {
				GLUtils.texImage2D(3553, i, paramBitmap, 0);
			}
			if ((j == 1) || (k == 1)) {
				return;
			}
			i++;
			j /= 2;
			k /= 2;
			Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, k, j,
					true);
			paramBitmap.recycle();
			paramBitmap = localBitmap;
		}
	}

	private void initializeNormalBuffer(float[] paramArrayOfFloat) {
		ByteBuffer.allocateDirect(4 * paramArrayOfFloat.length);
		ByteBuffer localByteBuffer = ByteBuffer
				.allocateDirect(4 * paramArrayOfFloat.length);
		localByteBuffer.order(ByteOrder.nativeOrder());
		this.normalBuffer = localByteBuffer.asFloatBuffer();
		this.normalBuffer.put(paramArrayOfFloat);
		this.normalBuffer.position(0);
	}
	
	
	
	
	
	
	public void draw(GL10 paramGL10) {
		paramGL10.glBindTexture(3553, this.textures[2]); // 112
		
		paramGL10.glFrontFace(2304); // 114
		
		paramGL10.glEnableClientState(32884); // 116
		if (this.hasTextureCoords) // 117
		{
			paramGL10.glEnableClientState(32888); // 119
			paramGL10.glTexCoordPointer(2 + this.hasTextureW, 5126, 0, // 120
					this.textureBuffer);
		}
		if (this.hasNormalCoords) // 123
		{
			paramGL10.glEnableClientState(32885); // 125
			paramGL10.glNormalPointer(5126, 0, this.normalBuffer); // 126
			
			
		}
		paramGL10.glDisable(2896); // 130
			
			
		paramGL10.glVertexPointer(3, 5126, 0, this.vertexBuffer); // 133
			
		if (!this.wireframe) // 135
		{	
			paramGL10.glDrawArrays(1, 0, this.numFaces / 3); // 137

	
		}
		paramGL10.glDrawArrays(4, 0, this.numFaces / 3); // 141
			
			
		paramGL10.glDisableClientState(32884); // 144
		paramGL10.glDisableClientState(32888); // 145
		paramGL10.glDisableClientState(32885); // 146
		return;
	}
	
	
	

	private void initializeTextureBuffer(float[] paramArrayOfFloat) {
		ByteBuffer.allocateDirect(4 * paramArrayOfFloat.length);
		ByteBuffer localByteBuffer = ByteBuffer
				.allocateDirect(4 * paramArrayOfFloat.length);
		localByteBuffer.order(ByteOrder.nativeOrder());
		this.textureBuffer = localByteBuffer.asFloatBuffer();
		this.textureBuffer.put(paramArrayOfFloat);
		this.textureBuffer.position(0);
	}

	private void initializeVertexBuffer(float[] paramArrayOfFloat) {
		ByteBuffer localByteBuffer = ByteBuffer
				.allocateDirect(4 * paramArrayOfFloat.length);
		localByteBuffer.order(ByteOrder.nativeOrder());
		this.vertexBuffer = localByteBuffer.asFloatBuffer();
		this.vertexBuffer.put(paramArrayOfFloat);
		this.vertexBuffer.position(0);
	}

	public void destroy() {
		this.vertexBuffer = null;
		this.textureBuffer = null;
		this.normalBuffer = null;
	}

	

	// ERROR //
	public int loadGLTexture(GL10 paramGL10, java.lang.String paramString) {
		// Byte code:
		// 0: new 179 java/io/FileInputStream
		// 3: dup
		// 4: aload_2
		// 5: invokespecial 182 java/io/FileInputStream:<init>
		// (Ljava/lang/String;)V
		// 8: astore_3
		// 9: aload_3
		// 10: invokestatic 188 android/graphics/BitmapFactory:decodeStream
		// (Ljava/io/InputStream;)Landroid/graphics/Bitmap;
		// 13: astore 10
		// 15: aload_3
		// 16: invokevirtual 193 java/io/InputStream:close ()V
		// 19: aload 10
		// 21: ifnonnull +53 -> 74
		// 24: iconst_4
		// 25: ireturn
		// 26: astore 13
		// 28: iconst_5
		// 29: ireturn
		// 30: astore 7
		// 32: aload_3
		// 33: invokevirtual 193 java/io/InputStream:close ()V
		// 36: iconst_1
		// 37: ireturn
		// 38: astore 9
		// 40: bipush 6
		// 42: ireturn
		// 43: astore 8
		// 45: iconst_1
		// 46: ireturn
		// 47: astore 4
		// 49: aload_3
		// 50: invokevirtual 193 java/io/InputStream:close ()V
		// 53: aload 4
		// 55: athrow
		// 56: astore 6
		// 58: bipush 6
		// 60: ireturn
		// 61: astore 5
		// 63: iconst_1
		// 64: ireturn
		// 65: astore 12
		// 67: bipush 6
		// 69: ireturn
		// 70: astore 11
		// 72: iconst_1
		// 73: ireturn
		// 74: aload_0
		// 75: aload_1
		// 76: aload 10
		// 78: invokespecial 195 net/mcmiracom/modelviewer/ObjModel:bindTexture
		// (Ljavax/microedition/khronos/opengles/GL10;Landroid/graphics/Bitmap;)V
		// 81: iconst_0
		// 82: ireturn
		//
		// Exception table:
		// from to target type
		// 0 9 26 java/io/FileNotFoundException
		// 9 15 30 java/lang/OutOfMemoryError
		// 32 36 38 java/io/IOException
		// 32 36 43 java/lang/OutOfMemoryError
		// 9 15 47 finally
		// 49 53 56 java/io/IOException
		// 49 53 61 java/lang/OutOfMemoryError
		// 15 19 65 java/io/IOException
		// 15 19 70 java/lang/OutOfMemoryError
		return 0;
	}

	public void setWireFrame(boolean paramBoolean) {
		this.wireframe = paramBoolean;
	}

	public void toggleWireframe() {
		if (this.wireframe)
			;
		for (boolean bool = false;; bool = true) {
			this.wireframe = bool;
			return;
		}
	}
}

/*
 * Location: C:\Users\Andres Oviedo\Downloads\android\3D Model
 * Viewer_unzipped_undexed\classes_dex2jar.jar Qualified Name:
 * net.mcmiracom.modelviewer.ObjModel JD-Core Version: 0.6.2
 */