package org.the3deer.android_3d_model_engine.drawer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.the3deer.android_3d_model_engine.R;
import org.the3deer.android_3d_model_engine.model.AnimatedModel;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.util.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2013-2020 the3deer.org
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class RendererFactory {

    /**
     * shader code loaded from raw resources
     * resources are cached on activity thread
     */
    private Map<Integer, String> shadersIds = new HashMap<>();
    /**
     * list of opengl drawers
     */
    private Map<Shader, GLES20Renderer> drawers = new HashMap<>();

    public RendererFactory(Context context) throws IllegalAccessException, IOException {

        Log.i("RendererFactory", "Discovering shaders...");
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {
            String shaderId = fields[count].getName();
            Log.v("RendererFactory", "Loading shader... " + shaderId);
            int shaderResId = fields[count].getInt(fields[count]);
            byte[] shaderBytes = IOUtils.read(context.getResources().openRawResource(shaderResId));
            String shaderCode = new String(shaderBytes);
            shadersIds.put(shaderResId, shaderCode);
        }
        Log.i("RendererFactory", "Shaders loaded: " + shadersIds.size());
    }

    public Renderer getDrawer(Object3DData obj, boolean usingSkyBox,
                              boolean usingTextures, boolean usingLights,
                              boolean usingAnimation, boolean isShadow, boolean isUsingShadows) {

        // double check features
        final boolean animationOK = obj instanceof AnimatedModel
                && ((AnimatedModel) obj).getAnimation() != null
                && (((AnimatedModel) obj).getAnimation()).isInitialized();
        final boolean isAnimated = usingAnimation && (obj == null || animationOK);
        final boolean isLighted = usingLights && obj != null && obj.getNormalsBuffer() != null;
        final boolean isTextured = usingTextures && obj != null && obj.getTextureBuffer() != null;

        // match shaders
        final Shader shader;
        if (usingSkyBox){
            shader = Shader.SKYBOX;
        } else {
            shader = getShader(isTextured, isLighted, isAnimated, isShadow, isUsingShadows);
        }

        // get cached shaders
        GLES20Renderer renderer = drawers.get(shader);
        if (renderer != null) {
            renderer.setTexturesEnabled(isTextured);
            renderer.setLightingEnabled(isLighted);
            renderer.setAnimationEnabled(isAnimated);
            return renderer;
        }

        // build drawer
        String vertexShaderCode;

        // experimental: inject glPointSize
        vertexShaderCode = shadersIds.get(shader.vertexShaderResourceId).replace("void main(){", "void main(){\n\tgl_PointSize = 5.0;");

        // use opengl constant to dynamically set up array size in shaders. That should be >=120
        vertexShaderCode = vertexShaderCode.replace("const int MAX_JOINTS = 60;", "const int MAX_JOINTS = gl_MaxVertexUniformVectors > 60 ? 60 : gl_MaxVertexUniformVectors;");

        // create drawer
        /*Log.v("RendererFactory", "\n---------- Vertex shader ----------\n");
        Log.v("RendererFactory", vertexShaderCode);
        Log.v("RendererFactory", "---------- Fragment shader ----------\n");
        Log.v("RendererFactory", fragmentShaderCode);
        Log.v("RendererFactory", "-------------------------------------\n");*/
        renderer = GLES20Renderer.getInstance(shader.id, vertexShaderCode, shadersIds.get(shader.fragmentShaderResourceId));
        renderer.setTexturesEnabled(isTextured);
        renderer.setLightingEnabled(isLighted);
        renderer.setAnimationEnabled(isAnimated);

        // cache drawer
        drawers.put(shader, renderer);

        // return drawer
        return renderer;
    }

    @NonNull
    private Shader getShader(boolean isTextured, boolean isLighted, boolean isAnimated, boolean isShadow, boolean isUsingShadows) {

        final Shader ret;
        if (isShadow) {
            return Shader.SHADOW;
        } else if (isUsingShadows){
            return Shader.SHADOWED;
        } else if (isAnimated || isTextured || isLighted){
            ret = Shader.ANIMATED;
        } else {
            ret = Shader.BASIC;
        }
        return ret;
    }

    public Renderer getBoundingBoxDrawer() {
        return getDrawer(null, false, false, false, false, false, false);
    }

    public Renderer getFaceNormalsDrawer() {
        return getDrawer(null, false, false, false, false, false, false);
    }

    public Renderer getBasicShader() {
        return getDrawer(null, false, false, false, false, false, false);
    }

    public Renderer getSkyBoxDrawer() {
        return getDrawer(null, true, false, false, false, false, false);
    }

    public Renderer getShadowRenderer(){
        return getDrawer(null, false, false, false, true, true, false);
    }

    public Renderer getShadowRenderer2(Object3DData obj){
        return getDrawer(obj, false, true, true, true, false, true);
    }

}
