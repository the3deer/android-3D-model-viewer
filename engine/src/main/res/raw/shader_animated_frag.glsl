precision highp float;

// data
uniform mat4 u_MMatrix;
uniform vec3 u_cameraPos;
varying vec3 v_Position;

// color
uniform vec4 vColor;
uniform vec4 vColorMask;

// colors
uniform bool u_Coloured;
varying vec4 v_Color;

// texture
uniform bool u_Textured;
uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;

// light
uniform bool u_Lighted;
uniform vec3 u_LightPos;
varying vec3 v_Normal;

// normalMap
uniform bool u_NormalTextured;
uniform sampler2D u_NormalTexture;

// emissiveMap
uniform bool u_EmissiveTextured;
uniform sampler2D u_EmissiveTexture;


void main(){

    // colours
    vec4 color;
    if (u_Coloured){
        color = v_Color;
    } else {
        color = vColor;
    }

    // textures
    vec4 tex = vec4(1.0,1.0,1.0,1.0);
    if (u_Textured){
        tex = texture2D(u_Texture, v_TexCoordinate);
    }

    // light
    float diffuse = 1.0;
    float specular = 1.0;
    vec3 nmap = vec3(0.0,0.0,0.0);
    if (u_Lighted) {

    // Transform the vertex into eye space.
        vec3 modelVertex = vec3(u_MMatrix * vec4(v_Position,1.0));

    // Transform the normal's orientation into eye space.
        vec3 modelNormal = normalize(vec3(u_MMatrix * vec4(v_Normal,1.0)));

        // normal map
        if (u_NormalTextured){

            // obtain normal from normal map in range [0,1]
            nmap = texture2D(u_NormalTexture, v_TexCoordinate).rgb;

            // transform normal vector to range [-1,1]
            modelNormal = normalize(modelNormal + normalize(nmap * 2.0 - 1.0));

            // normal = normalize(nmap * 2.0 - 1.0);
        }

        // Get a lighting direction vector from the light to the vertex.
        vec3 lightVector = normalize(u_LightPos - modelVertex);

        // Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
        // pointing in the same direction then it will get max illumination.
        // float diffuse = max(dot(lightVector, modelNormal),0.0); // --> lights only on camera in front of face
        float diff = max(dot(lightVector, modelNormal), 0.0);

        // Attenuate the light based on distance.
        float dist = distance(u_LightPos, modelVertex);
        dist = 1.0 / (1.0 + dist * 0.005);
        diffuse = diff * dist;

        // specular light
        vec3 viewDir = normalize(u_cameraPos - modelVertex);
        vec3 reflectDir = reflect(-lightVector, modelNormal);
        specular = pow(max(dot(reflectDir, viewDir),0.0),32.0);
    }

    // ambient light
    float ambient = 0.3;

    // light
    float light = min((diffuse + specular + ambient),1.0);

    // emissive texture
    if (u_EmissiveTextured){
        color = color + texture2D(u_EmissiveTexture, v_TexCoordinate);
    }

    // calculate final color
    gl_FragColor = color * tex * vColorMask * light;
    gl_FragColor[3] = color[3] * vColorMask[3];
}
