precision highp float;

// color
uniform vec4 vColor;
uniform vec4 vColorMask;

// colors
uniform bool u_Coloured;
varying vec4 v_Color;

void main(){

  // color
  vec4 color;
  if (u_Coloured){
    color = v_Color;
  } else {
    color = vColor;
  }

  gl_FragColor = color * vColorMask;
  gl_FragColor[3] = color[3] * vColorMask[3];
}