precision highp float;

// colors
varying vec4 v_Color;
uniform vec4 vColorMask;

void main(){
    gl_FragColor = v_Color * vColorMask;
}