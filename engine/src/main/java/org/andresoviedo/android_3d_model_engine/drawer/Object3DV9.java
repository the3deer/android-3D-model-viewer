package org.andresoviedo.android_3d_model_engine.drawer;

import android.opengl.GLES20;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.android.GLUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Draw using single color, texture and skeleton and light
 *
 * @author andresoviedo
 *
 */
class Object3DV9 extends Object3DImpl {

	// @formatter:off
	private final static String vertexShaderCode =
		"const int MAX_JOINTS = 60;\n"
		+ "const int MAX_WEIGHTS = 3;\n"
		+ "uniform mat4 u_MVPMatrix;      \n"
		+ "attribute vec4 a_Position;     \n"
		+ "attribute vec3 in_jointIndices;\n"
		+ "attribute vec3 in_weights;\n"
		+ "uniform mat4 jointTransforms[MAX_JOINTS];\n"
	// light variables
		+"uniform mat4 u_MVMatrix;\n"
		+"uniform vec3 u_LightPos;\n"
		+"attribute vec3 a_Normal;\n"
		+"uniform vec4 vColor;\n"
		+"varying vec4 v_Color;\n"
	// texture variables
		+"attribute vec2 a_TexCoordinate;\n"
		+"varying vec2 v_TexCoordinate;\n"
		+ "void main()                    \n"
		+ "{                              \n"
		+ "  vec4 totalLocalPos = vec4(0.0);\n"

		/*+ "  for(int i=0;i<MAX_WEIGHTS;i++){\n"
		+ "    mat4 jointTransform = jointTransforms[in_jointIndices[i]];\n"
		+ "    vec4 posePosition = jointTransform * a_Position;\n"
		+ "    totalLocalPos += posePosition * in_weights[i];\n"
		+ "  }\n"*/

		+ "    mat4 jointTransform = jointTransforms[int(in_jointIndices[0])];\n"
		+ "    vec4 posePosition = jointTransform * a_Position;\n"
		+ "    totalLocalPos += posePosition * in_weights[0];\n"

		+ "    jointTransform = jointTransforms[int(in_jointIndices[1])];\n"
		+ "    posePosition = jointTransform * a_Position;\n"
		+ "    totalLocalPos += posePosition * in_weights[1];\n"

		+ "    jointTransform = jointTransforms[int(in_jointIndices[2])];\n"
		+ "    posePosition = jointTransform * a_Position;\n"
		+ "    totalLocalPos += posePosition * in_weights[2];\n"


		+ "  gl_Position = u_MVPMatrix * totalLocalPos;\n"
		+ "  gl_PointSize = 2.5;         \n"

		+ "   vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);\n          "+
				// Get a lighting direction vector from the light to the vertex.
				"   vec3 lightVector = normalize(u_LightPos - modelViewVertex);\n    "+
				// Transform the normal's orientation into eye space.
				"   vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));\n "+
				// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
				// pointing in the same direction then it will get max illumination.
				"   float diffuse = max(dot(modelViewNormal, lightVector), 0.1);\n   " 	+
				// Attenuate the light based on distance.
				"   float distance = length(u_LightPos - modelViewVertex);\n         "+
				"   diffuse = diffuse * (1.0 / (1.0 + (0.05 * distance * distance)));\n"+
				//  Add ambient lighting
				"  diffuse = diffuse + 0.5;"+
				// Multiply the color by the illumination level. It will be interpolated across the triangle.
				"   v_Color = vColor * diffuse;\n"+
				"   v_Color[3] = vColor[3];\n" // correct alpha

	// texture
					+"  v_TexCoordinate = a_TexCoordinate;\n"
		+ "}                              \n";
	// @formatter:on

	// @formatter:off
	private final static String fragmentShaderCode =
			"precision mediump float;\n"+
			"varying vec4 v_Color;\n"+
					// textures
			"uniform sampler2D u_Texture;\n"+
			"varying vec2 v_TexCoordinate;\n"+
			"void main() {\n"+
			"  gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate);\n" +
			"}";
	// @formatter:on

	public Object3DV9() {
		super("V9", vertexShaderCode, fragmentShaderCode, "a_Position" , "in_jointIndices", "in_weights",
				"a_Normal", "a_TexCoordinate");
	}

	@Override
	public void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int drawMode, int drawSize, int textureId,
					 float[] lightPos) {


		AnimatedModel animatedModel = (AnimatedModel) obj;

		GLES20.glUseProgram(mProgram);

		int in_weightsHandle = GLES20.glGetAttribLocation(mProgram, "in_weights");
		GLUtil.checkGlError("glGetAttribLocation");
		if (in_weightsHandle < 0){
			throw new RuntimeException("handle 'in_weights' not found");
		}
		GLES20.glEnableVertexAttribArray(in_weightsHandle);
		GLUtil.checkGlError("glEnableVertexAttribArray");
		animatedModel.getVertexWeights().position(0);
		GLES20.glVertexAttribPointer(in_weightsHandle, 3, GLES20.GL_FLOAT, false, 0, animatedModel.getVertexWeights());
		GLUtil.checkGlError("glVertexAttribPointer");

		int in_jointIndicesHandle = GLES20.glGetAttribLocation(mProgram, "in_jointIndices");
		GLUtil.checkGlError("glGetAttribLocation");
		if (in_jointIndicesHandle < 0){
			throw new RuntimeException("handle 'in_jointIndicesHandle' not found");
		}
		GLES20.glEnableVertexAttribArray(in_jointIndicesHandle);
		GLUtil.checkGlError("glEnableVertexAttribArray");
		animatedModel.getJointIds().position(0);
		GLES20.glVertexAttribPointer(in_jointIndicesHandle, 3, GLES20.GL_FLOAT, false, 0, animatedModel.getJointIds());
		GLUtil.checkGlError("glVertexAttribPointer");


		float[][] jointTransformsArray = animatedModel.getJointTransforms();
		// get handle to fragment shader's vColor member

		List<Integer> handles = new ArrayList<>();
		for (int i=0; i<jointTransformsArray.length; i++){
			float[] jointTransform = jointTransformsArray[i];
			int jointTransformsHandle = GLES20.glGetUniformLocation(mProgram, "jointTransforms["+i+"]");
			if (jointTransformsHandle < 0){
				throw new RuntimeException("handle 'jointTransformsHandle["+i+"]' not found");
			}
			GLUtil.checkGlError("glGetUniformLocation");
			GLES20.glUniformMatrix4fv(jointTransformsHandle, 1, false, jointTransform, 0);
			handles.add(jointTransformsHandle);
		}

		super.draw(obj, pMatrix, vMatrix, drawMode, drawSize, textureId, lightPos);

		GLES20.glDisableVertexAttribArray(in_weightsHandle);
		GLES20.glDisableVertexAttribArray(in_jointIndicesHandle);
		for (int i:handles) {
			//GLES20.glDisableVertexAttribArray(i);
		}
	}

	@Override
	protected boolean supportsNormals() {
		return true;
	}

	@Override
	protected boolean supportsLighting() {
		return true;
	}

	@Override
	protected boolean supportsMvMatrix() {
		return true;
	}

	@Override
	protected boolean supportsTextures() {
		return true;
	}
}
