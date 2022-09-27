precision highp float;

// MVP matrices
uniform mat4 u_MMatrix;
uniform mat4 u_VMatrix;
uniform mat4 u_PMatrix;

// mesh
attribute vec4 a_Position;

// colouring
uniform bool u_Coloured;
attribute vec4 a_Color;
varying vec4 v_Color;

void main(){

    // calculate MVP matrix
    mat4 u_MVMatrix = u_VMatrix * u_MMatrix;
    mat4 u_MVPMatrix = u_PMatrix * u_MVMatrix;

    // calculate rendered position
    gl_Position = u_MVPMatrix * a_Position;
    gl_PointSize = 15.0;

    // pass color to fragment shader
    if (u_Coloured){
        v_Color = a_Color;
    }

}