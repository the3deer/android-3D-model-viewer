precision highp float;

// MVP matrices
uniform mat4 u_MMatrix;
uniform mat4 u_VMatrix;
uniform mat4 u_PMatrix;

// mesh
attribute vec4 a_Position;

// colors
//uniform vec4 vColor;
//varying vec4 v_Color;

// skybox
varying vec4 v_TexCoordinate;

void main(){

    // calculate MVP matrix
    mat4 u_MVMatrix = u_VMatrix * u_MMatrix;
    mat4 u_MVPMatrix = u_PMatrix * u_MVMatrix;

    // calculate rendered position
    gl_Position = u_MVPMatrix * a_Position;

    // colors
    v_TexCoordinate = a_Position;
}