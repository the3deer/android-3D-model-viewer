package org.the3deer.android_3d_model_engine.drawer;


import org.the3deer.android_3d_model_engine.R;

public enum Shader {

    SKYBOX("skybox", R.raw.shader_skybox_vert, R.raw.shader_skybox_frag),
    BASIC("basic", R.raw.shader_basic_vert, R.raw.shader_basic_frag),
    ANIMATED("animated", R.raw.shader_animated_vert, R.raw.shader_animated_frag),
    SHADOW("shadow", R.raw.shader_v_depth_map, R.raw.shader_f_depth_map),
    SHADOWED("shadowed", R.raw.shader_v_with_shadow, R.raw.shader_f_with_simple_shadow);

    String id;
    int vertexShaderResourceId = -1;
    int fragmentShaderResourceId = -1;
    
    Shader (String id, int vertexShaderCode, int fragmentShaderCode){
        this.id = id;
        this.vertexShaderResourceId = vertexShaderCode;
        this.fragmentShaderResourceId = fragmentShaderCode;
    }
}
