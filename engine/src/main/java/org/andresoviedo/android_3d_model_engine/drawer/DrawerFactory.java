package org.andresoviedo.android_3d_model_engine.drawer;

import android.content.Context;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.R;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DrawerFactory {

    /**
     * shader code loaded from raw resources
     * resources are cached on activity thread
     */
    private Map<String, String> shadersCode = new HashMap<>();
    /**
     * list of opengl drawers
     */
    private Map<String, DrawerImpl> drawers = new HashMap<>();

    public DrawerFactory(Context context) throws IllegalAccessException, IOException {

        Log.i("DrawerFactory", "Discovering shaders...");
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            String shaderId = fields[count].getName();
            Log.i("DrawerFactory", "Loading shader... " + shaderId);
            int shaderResId = fields[count].getInt(fields[count]);
            byte[] shaderBytes = IOUtils.read(context.getResources().openRawResource(shaderResId));
            String shaderCode = new String(shaderBytes);
            shadersCode.put(shaderId, shaderCode);
        }
        Log.i("DrawerFactory", "Shaders loaded: " + shadersCode.size());
    }

    public Object3D getDrawer(Object3DData obj, boolean usingTextures, boolean usingLights, boolean usingAnimation, boolean drawColors) {

        // double check features
        boolean isAnimated = usingAnimation && obj instanceof AnimatedModel && ((AnimatedModel) obj).getAnimation() != null;
        boolean isUsingLights = usingLights && (obj.getNormals() != null || obj.getVertexNormalsArrayBuffer() != null);
        boolean isTextured = usingTextures && obj.getTextureData() != null && obj.getTextureCoordsArrayBuffer() != null;
        boolean isColoured = drawColors && obj != null && obj.getVertexColorsArrayBuffer() != null;

        // build shader id according to features
        StringBuilder shaderIdBuilder = new StringBuilder("shader_");
        shaderIdBuilder.append(isAnimated ? "anim_" : "");
        shaderIdBuilder.append(isUsingLights ? "light_" : "");
        shaderIdBuilder.append(isTextured ? "texture_" : "");
        shaderIdBuilder.append(isColoured ? "colors_" : "");

        // get cached drawer
        String shaderId = shaderIdBuilder.toString();
        DrawerImpl drawer = drawers.get(shaderId);
        if (drawer != null) return drawer;

        // build drawer
        String vertexShaderCode = shadersCode.get(shaderId + "vert");
        String fragmentShaderCode = shadersCode.get(shaderId + "frag");
        if (vertexShaderCode == null || fragmentShaderCode == null) {
            Log.e("DrawerFactory", "Shaders not found for " + shaderId);
            return null;
        }

        // experimental: inject glPointSize
        vertexShaderCode = vertexShaderCode.replace("void main(){", "void main(){\n\tgl_PointSize = 5.0;");

        // create drawer
        Log.i("Object3DImpl2", "\n---------- Vertex shader ----------\n");
        Log.i("Object3DImpl2", vertexShaderCode);
        Log.i("Object3DImpl2", "---------- Fragment shader ----------\n");
        Log.i("Object3DImpl2", fragmentShaderCode);
        Log.i("Object3DImpl2", "-------------------------------------\n");
        drawer = DrawerImpl.getInstance(shaderId, vertexShaderCode, fragmentShaderCode);

        // cache drawer
        drawers.put(shaderId, drawer);

        // return drawer
        return drawer;
    }

    public Object3D getBoundingBoxDrawer() {
        return getDrawer(null, false, false, false, false);
    }

    public Object3D getFaceNormalsDrawer() {
        return getDrawer(null, false, false, false, false);
    }

    public Object3D getPointDrawer() {
        return getDrawer(null, false, false, false, false);
    }
}
