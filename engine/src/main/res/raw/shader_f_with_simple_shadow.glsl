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
uniform mat4 uNormalMatrix;
uniform vec3 u_LightPos;
varying vec3 v_Normal;

// normalMap
uniform bool u_NormalTextured;
uniform sampler2D u_NormalTexture;

// emissiveMap
uniform bool u_EmissiveTextured;
uniform sampler2D u_EmissiveTexture;

// shadow
uniform sampler2D uShadowTexture;
varying vec4 vShadowCoord;

// unpack colour to depth value
float unpack (vec4 colour)
{
    const vec4 bitShifts = vec4(1.0 / (256.0 * 256.0 * 256.0),
                                1.0 / (256.0 * 256.0),
                                1.0 / 256.0,
                                1);
    return dot(colour , bitShifts);
}

//Simple shadow mapping
float shadowSimple()
{
	vec4 shadowMapPosition = vShadowCoord / vShadowCoord.w;

	shadowMapPosition = (shadowMapPosition + 1.0) /2.0;

	vec4 packedZValue = texture2D(uShadowTexture, shadowMapPosition.st);

	float distanceFromLight = unpack(packedZValue);

	//add bias to reduce shadow acne (error margin)
	float bias = 0.0005;

	//1.0 = not in shadow (fragmant is closer to light than the value stored in shadow map)
	//0.0 = in shadow
	return float(distanceFromLight > (shadowMapPosition.z - bias));
}

void main()
{
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
	float shadow = 1.0;
	vec3 nmap = vec3(0.0,0.0,0.0);
	if (u_Lighted) {

		// Transform the vertex into eye space.
		vec3 modelVertex = vec3(u_MMatrix * vec4(v_Position,1.0));

		// Transform the normal's orientation into eye space.
		//vec3 modelNormal = normalize(vec3(u_MMatrix * vec4(v_Normal,1.0)));
		vec3 modelNormal = vec3(uNormalMatrix * vec4(v_Normal, 0.0));

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

		//if the fragment is not behind light view frustum
		if (vShadowCoord.w > 0.0) {

			shadow = shadowSimple();

			//scale 0.0-1.0 to 0.2-1.0
			//otherways everything in shadow would be black
			shadow = (shadow * 0.5) + 0.5;

		}
	}

	// ambient light
	float ambient = 0.5;

	// light
	float light = min((diffuse + specular + ambient),1.0);

	// emissive texture
	if (u_EmissiveTextured){
		color = color + texture2D(u_EmissiveTexture, v_TexCoordinate);
	}

	// calculate final color
	gl_FragColor = color * tex * vColorMask * (light * shadow);
	gl_FragColor[3] = color[3] * vColorMask[3];
}                                                                     	
