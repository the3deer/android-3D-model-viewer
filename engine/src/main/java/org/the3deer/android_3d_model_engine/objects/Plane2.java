package org.the3deer.android_3d_model_engine.objects;

import android.opengl.GLES20;

import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.shadow.RenderConstants;
import org.the3deer.util.android.GLUtil;
import org.the3deer.util.io.IOUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Plane2 {
	private final FloatBuffer planePosition;
	private final FloatBuffer planeNormal;
	private final FloatBuffer planeColor;

	//TODO: remove
	static int translateY = 0;
	static int translateZ = 0;

	static float[] planePositionData = {
			// X, Y, Z,
			-200.0f, -50f,  -200.0f + translateZ,
			-200.0f, -50f,  200.0f + translateZ,
			200.0f, -50f,  -200.0f + translateZ,
			-200.0f, -50f,  200.0f + translateZ,
			200.0f, -50f,  200.0f + translateZ,
			200.0f, -50f,  -200.0f + translateZ
			};

	static float[] planeNormalData = {
			// nX, nY, nZ
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f
			};

	float[] planeColorData = {
			// R, G, B, A
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f
		};

	public Plane2() {
		// Buffer initialization
		ByteBuffer bPos = ByteBuffer.allocateDirect(planePositionData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
		bPos.order(ByteOrder.nativeOrder());
		planePosition = bPos.asFloatBuffer();
		
		ByteBuffer bNormal = ByteBuffer.allocateDirect(planeNormalData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
		bNormal.order(ByteOrder.nativeOrder());
		planeNormal = bNormal.asFloatBuffer();
		
		ByteBuffer bColor = ByteBuffer.allocateDirect(planeColorData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
		bColor.order(ByteOrder.nativeOrder());
		planeColor = bColor.asFloatBuffer();
					
		planePosition.put(planePositionData).position(0);
		planeNormal.put(planeNormalData).position(0);
		planeColor.put(planeColorData).position(0);
	}

	public static Object3DData build() {
		FloatBuffer vertexBuffer = IOUtils.createFloatBuffer(planePositionData.length ).put(planePositionData);
		FloatBuffer normalBuffer = IOUtils.createFloatBuffer(planeNormalData.length ).put(planeNormalData);
		return new Object3DData(vertexBuffer).setNormalsBuffer(normalBuffer).setDrawMode(GLES20.GL_TRIANGLES).setId("plane2");
	}
	
	public void render(int mProgram, int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {
		
		// Pass position information to shader
		planePosition.position(0);		
        GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
        		0, planePosition);        
                
        GLES20.glEnableVertexAttribArray(positionAttribute);                       
        
        if (!onlyPosition)
        {
        	// Pass normal information to shader
	        planeNormal.position(0);
	        GLES20.glVertexAttribPointer(normalAttribute, 3, GLES20.GL_FLOAT, false, 
	        		 0, planeNormal);
	        
	        GLES20.glEnableVertexAttribArray(normalAttribute);  
	        
	     // Pass color information to shader
	        /*planeColor.position(0);
	        GLES20.glVertexAttribPointer(colorAttribute, 4, GLES20.GL_FLOAT, false, 
	        		0, planeColor);*/

	        //GLES20.glEnableVertexAttribArray(colorAttribute);

			int handle = GLES20.glGetUniformLocation(mProgram, "vColor");
			GLUtil.checkGlError("glGetUniformLocation");

			GLES20.glUniform4fv(handle, 1, Constants.COLOR_GREEN, 0);
			GLUtil.checkGlError("glUniform4fv");
        }
        
        // Draw the plane
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6); 
	}
}