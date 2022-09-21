precision highp float;

// colors
uniform vec4 vColor;
uniform vec4 vColorMask;

void main(){
  gl_FragColor = vColor * vColorMask;
}