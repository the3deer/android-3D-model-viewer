package practica4.objects;

import java.util.Hashtable;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

public class Light {

	private static final Map<Integer, String> LIGHT_NAMES = new Hashtable<Integer, String>();
	static {
		LIGHT_NAMES.put(GL10.GL_LIGHT0, "GL_LIGHT0");
		LIGHT_NAMES.put(GL10.GL_LIGHT1, "GL_LIGHT1");
		LIGHT_NAMES.put(GL10.GL_LIGHT2, "GL_LIGHT2");
		LIGHT_NAMES.put(GL10.GL_LIGHT3, "GL_LIGHT3");
		LIGHT_NAMES.put(GL10.GL_LIGHT4, "GL_LIGHT4");
		LIGHT_NAMES.put(GL10.GL_LIGHT5, "GL_LIGHT5");
		LIGHT_NAMES.put(GL10.GL_LIGHT6, "GL_LIGHT6");
		LIGHT_NAMES.put(GL10.GL_LIGHT7, "GL_LIGHT7");
	}

	// Light id within OpenGL (LIGHT0, LIGHT1, ... LIGHT8)
	public int id;
	public int glName;
	public boolean enabled;

	// lightning parameters
	public float[] light_position = new float[]{0F, 5F, 0F, 1.0F};
	// float[] spot_direction = new float[]{-light_position[0],
	// -light_position[1], -light_position[2], 1.0F};
	public float[] spot_direction = new float[]{0, -1.0F, 0};
	public float[] light_ambient = new float[]{0.1F, 0.1F, 0.1F, 1.0F};
	public float[] light_diffuse = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
	public float[] light_specular = new float[]{1.0F, 1.0F, 1.0F, 1.0F};

	public Light(int id) {
		this.id = id;
		glName = 1000+this.id;
	}

	public String getName() {
		return LIGHT_NAMES.get(id);
	}

	public void setPosition(String param) {
		String[] paramArray = param.split(",");
		light_position[0] = Float.parseFloat(paramArray[0]);
		light_position[1] = Float.parseFloat(paramArray[1]);
		light_position[2] = Float.parseFloat(paramArray[2]);
	}

	public String spotDirectionToString() {
		return spot_direction[0] + "," + spot_direction[1] + ","
				+ spot_direction[2];
	}

	public void setSpotDirection(String param) {
		String[] paramArray = param.split(",");
		spot_direction[0] = Float.parseFloat(paramArray[0]);
		spot_direction[1] = Float.parseFloat(paramArray[1]);
		spot_direction[2] = Float.parseFloat(paramArray[2]);
	}

	public String lightAmbientToString() {
		return light_ambient[0] + "," + light_ambient[1] + ","
				+ light_ambient[2] + "," + light_ambient[3];
	}

	public void setlightAmbient(String param) {
		String[] paramArray = param.split(",");
		light_ambient[0] = Float.parseFloat(paramArray[0]);
		light_ambient[1] = Float.parseFloat(paramArray[1]);
		light_ambient[2] = Float.parseFloat(paramArray[2]);
		light_ambient[3] = Float.parseFloat(paramArray[3]);
	}

	public String lightDiffuseToString() {
		return light_diffuse[0] + "," + light_diffuse[1] + ","
				+ light_diffuse[2] + "," + light_diffuse[3];
	}

	public void setlightDiffuse(String param) {
		String[] paramArray = param.split(",");
		light_diffuse[0] = Float.parseFloat(paramArray[0]);
		light_diffuse[1] = Float.parseFloat(paramArray[1]);
		light_diffuse[2] = Float.parseFloat(paramArray[2]);
		light_diffuse[3] = Float.parseFloat(paramArray[3]);
	}

	public String lightSpecularToString() {
		return light_specular[0] + "," + light_specular[1] + ","
				+ light_specular[2] + "," + light_specular[3];
	}

	public void setlightSpecular(String param) {
		String[] paramArray = param.split(",");
		light_specular[0] = Float.parseFloat(paramArray[0]);
		light_specular[1] = Float.parseFloat(paramArray[1]);
		light_specular[2] = Float.parseFloat(paramArray[2]);
		light_specular[3] = Float.parseFloat(paramArray[3]);
	}
}
