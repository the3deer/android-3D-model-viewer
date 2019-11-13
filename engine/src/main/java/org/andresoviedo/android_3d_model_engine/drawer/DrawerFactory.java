package org.andresoviedo.android_3d_model_engine.drawer;

import android.content.Context;
import android.support.annotation.NonNull;
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

    private final String[] shaderIdTemp = new String[3];

    public DrawerFactory(Context context) throws IllegalAccessException, IOException {

        Log.i("DrawerFactory", "Discovering shaders...");
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            String shaderId = fields[count].getName();
            Log.d("DrawerFactory", "Loading shader... " + shaderId);
            int shaderResId = fields[count].getInt(fields[count]);
            byte[] shaderBytes = IOUtils.read(context.getResources().openRawResource(shaderResId));
            String shaderCode = new String(shaderBytes);
            shadersCode.put(shaderId, shaderCode);
        }
        Log.i("DrawerFactory", "Shaders loaded: " + shadersCode.size());
    }

    public Object3D getDrawer(Object3DData obj, boolean usingTextures, boolean usingLights, boolean usingAnimation, boolean drawColors) {

        // double check features
        boolean isAnimated = usingAnimation && obj instanceof AnimatedModel && (((AnimatedModel) obj).getAnimation() != null);
        boolean isUsingLights = usingLights && (obj.getNormals() != null || obj.getVertexNormalsArrayBuffer() != null);
        boolean isTextured = usingTextures && obj.getTextureData() != null && obj.getTextureCoordsArrayBuffer() != null;
        boolean isColoured = drawColors && obj != null && obj.getVertexColorsArrayBuffer() != null;

        final String[] shaderId = getShaderId(isAnimated, isUsingLights, isTextured, isColoured);

        // get cached drawer
        DrawerImpl drawer = drawers.get(shaderId[0]);
        if (drawer != null) return drawer;

        // build drawer
        String vertexShaderCode = shadersCode.get(shaderId[1]);
        String fragmentShaderCode = shadersCode.get(shaderId[2]);
        if (vertexShaderCode == null || fragmentShaderCode == null) {
            Log.e("DrawerFactory", "Shaders not found for " + shaderId[0]);
            return null;
        }

        // experimental: inject glPointSize
        vertexShaderCode = vertexShaderCode.replace("void main(){", "void main(){\n\tgl_PointSize = 5.0;");

        // create drawer
        Log.v("DrawerFactory", "\n---------- Vertex shader ----------\n");
        Log.v("DrawerFactory", vertexShaderCode);
        Log.v("DrawerFactory", "---------- Fragment shader ----------\n");
        Log.v("DrawerFactory", fragmentShaderCode);
        Log.v("DrawerFactory", "-------------------------------------\n");
        drawer = DrawerImpl.getInstance(shaderId[0], vertexShaderCode, fragmentShaderCode);

        // cache drawer
        drawers.put(shaderId[0], drawer);

        // return drawer
        return drawer;
    }

    @NonNull
    private String[] getShaderId(boolean isAnimated, boolean isUsingLights, boolean isTextured, boolean
            isColoured) {
        if (isAnimated){
            if (isUsingLights){
                if (isTextured){
                    if (isColoured){
                        shaderIdTemp[0]="shader_anim_light_texture_colors_";
                        shaderIdTemp[1]="shader_anim_light_texture_colors_vert";
                        shaderIdTemp[2]="shader_anim_light_texture_colors_frag";
                    } else {
                        shaderIdTemp[0]="shader_anim_light_texture_";
                        shaderIdTemp[1]="shader_anim_light_texture_vert";
                        shaderIdTemp[2]="shader_anim_light_texture_frag";
                    }
                } else{
                    if (isColoured){
                        shaderIdTemp[0]="shader_anim_light_colors_";
                        shaderIdTemp[1]="shader_anim_light_colors_vert";
                        shaderIdTemp[2]="shader_anim_light_colors_frag";
                    } else {
                        shaderIdTemp[0]="shader_anim_light_";
                        shaderIdTemp[1]="shader_anim_light_vert";
                        shaderIdTemp[2]="shader_anim_light_frag";
                    }
                }
            } else{
                if (isTextured){
                    if (isColoured){
                        shaderIdTemp[0]="shader_anim_texture_colors_";
                        shaderIdTemp[1]="shader_anim_texture_colors_vert";
                        shaderIdTemp[2]="shader_anim_texture_colors_frag";
                    } else {
                        shaderIdTemp[0]="shader_anim_texture_";
                        shaderIdTemp[1]="shader_anim_texture_vert";
                        shaderIdTemp[2]="shader_anim_texture_frag";
                    }
                } else{
                    if (isColoured){
                        shaderIdTemp[0]="shader_anim_colors_";
                        shaderIdTemp[1]="shader_anim_colors_vert";
                        shaderIdTemp[2]="shader_anim_colors_frag";
                    } else {
                        shaderIdTemp[0]="shader_anim_";
                        shaderIdTemp[1]="shader_anim_vert";
                        shaderIdTemp[2]="shader_anim_frag";
                    }
                }
            }
        } else {
            if (isUsingLights){
                if (isTextured){
                    if (isColoured){
                        shaderIdTemp[0]="shader_light_texture_colors_";
                        shaderIdTemp[1]="shader_light_texture_colors_vert";
                        shaderIdTemp[2]="shader_light_texture_colors_frag";
                    } else {
                        shaderIdTemp[0]="shader_light_texture_";
                        shaderIdTemp[1]="shader_light_texture_vert";
                        shaderIdTemp[2]="shader_light_texture_frag";
                    }
                } else{
                    if (isColoured){
                        shaderIdTemp[0]="shader_light_colors_";
                        shaderIdTemp[1]="shader_light_colors_vert";
                        shaderIdTemp[2]="shader_light_colors_frag";
                    } else {
                        shaderIdTemp[0]="shader_light_";
                        shaderIdTemp[1]="shader_light_vert";
                        shaderIdTemp[2]="shader_light_frag";
                    }
                }
            } else{
                if (isTextured){
                    if (isColoured){
                        shaderIdTemp[0]="shader_texture_colors_";
                        shaderIdTemp[1]="shader_texture_colors_vert";
                        shaderIdTemp[2]="shader_texture_colors_frag";
                    } else{
                        shaderIdTemp[0]="shader_texture_";
                        shaderIdTemp[1]="shader_texture_vert";
                        shaderIdTemp[2]="shader_texture_frag";
                    }
                } else{
                    if (isColoured){
                        shaderIdTemp[0]="shader_colors_";
                        shaderIdTemp[1]="shader_colors_vert";
                        shaderIdTemp[2]="shader_colors_frag";
                    } else{
                        shaderIdTemp[0]="shader_";
                        shaderIdTemp[1]="shader_vert";
                        shaderIdTemp[2]="shader_frag";
                    }
                }
            }
        }
        return shaderIdTemp;
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
