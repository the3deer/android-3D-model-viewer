precision highp float;

const int MAX_JOINTS = 60;
//const int MAX_WEIGHTS = 3;

// data
uniform mat4 u_MMatrix;
uniform mat4 u_VMatrix;
uniform mat4 u_PMatrix;

// mesh
attribute vec3 a_Position;
varying vec3 v_Position;

// colors
uniform bool u_Coloured;
attribute vec4 a_Color;
varying vec4 v_Color;

// texture
uniform bool u_Textured;
attribute vec2 a_TexCoordinate;
varying vec2 v_TexCoordinate;

// light
uniform bool u_Lighted;
attribute vec3 a_Normal;
varying vec3 v_Normal;

// normalMap
uniform bool u_NormalTextured;
uniform sampler2D u_NormalTexture;

// emissiveMap
uniform bool u_EmissiveTextured;
uniform sampler2D u_EmissiveTexture;

// animation
uniform bool u_Animated;
attribute vec3 in_jointIndices;
attribute vec3 in_weights;
uniform mat4 u_BindShapeMatrix;
uniform mat4 jointTransforms[MAX_JOINTS];

// shadow
uniform mat4 uShadowProjMatrix;
varying vec4 vShadowCoord;

void main(){

    vec4 animatedPos = vec4(a_Position,1.0);
    if (u_Animated) {
        vec4 bindPos = u_BindShapeMatrix * vec4(a_Position, 1.0);
        vec4 posePosition = jointTransforms[int(in_jointIndices[0])] * bindPos;
        animatedPos = posePosition * in_weights[0];
        posePosition = jointTransforms[int(in_jointIndices[1])] * bindPos;
        animatedPos += posePosition * in_weights[1];
        posePosition = jointTransforms[int(in_jointIndices[2])] * bindPos;
        animatedPos += posePosition * in_weights[2];
    }

	// calculate MVP matrix
	mat4 u_MVMatrix = u_VMatrix * u_MMatrix;
	mat4 u_MVPMatrix = u_PMatrix * u_MVMatrix;

    // calculate rendered position
    gl_Position = u_MVPMatrix * animatedPos;
    v_Position = vec3(animatedPos);

	// colours
	if (u_Coloured){
		v_Color = a_Color;
	}

	// normals
	if (u_Lighted){
		// Normal = mat3(transpose(inverse(model))) * aNormal;
		//v_Normal = u_MMatrix_Normal * a_Normal;
        v_Normal = a_Normal;
        vShadowCoord = uShadowProjMatrix * animatedPos;
    }

	// textures
    if (u_Textured) {
		v_TexCoordinate = a_TexCoordinate;
	}
}