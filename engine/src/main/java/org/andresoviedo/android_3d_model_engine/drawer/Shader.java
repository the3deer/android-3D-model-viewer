package org.andresoviedo.android_3d_model_engine.drawer;


import org.andresoviedo.android_3d_model_engine.R;

public enum Shader {

    SKYBOX("skybox_", R.raw.shader_skybox_vert, R.raw.shader_skybox_frag),
    ANIM_LIGHT_TEXTURE_COLORS("anim_light_texture_colors_", R.raw.shader_anim_light_texture_colors_vert, R.raw.shader_anim_light_texture_colors_frag),
    ANIM_LIGHT_TEXTURE("anim_light_texture_", R.raw.shader_anim_light_texture_vert, R.raw.shader_anim_light_texture_frag),
    ANIM_LIGHT_COLORS("anim_light_colors_", R.raw.shader_anim_light_colors_vert, R.raw.shader_anim_light_colors_frag),
    ANIM_LIGHT("anim_light_", R.raw.shader_anim_light_vert, R.raw.shader_anim_light_frag),
    ANIM_TEXTURE_COLORS("anim_texture_colors_", R.raw.shader_anim_texture_colors_vert, R.raw.shader_anim_texture_colors_frag),
    ANIM_TEXTURE("anim_texture_", R.raw.shader_anim_texture_vert, R.raw.shader_anim_texture_frag),
    ANIM_COLORS("anim_colors_", R.raw.shader_anim_colors_vert, R.raw.shader_anim_colors_frag),
    ANIM("anim_", R.raw.shader_anim_vert, R.raw.shader_anim_frag),
    LIGHT_TEXTURE_COLORS("light_texture_colors_", R.raw.shader_light_texture_colors_vert, R.raw.shader_light_texture_colors_frag),
    LIGHT_TEXTURE("light_texture_", R.raw.shader_light_texture_vert, R.raw.shader_light_texture_frag),
    LIGHT_COLORS("light_colors_", R.raw.shader_light_colors_vert, R.raw.shader_light_colors_frag),
    LIGHT("light_", R.raw.shader_light_vert, R.raw.shader_light_frag),
    TEXTURE_COLORS("texture_colors_", R.raw.shader_texture_colors_vert, R.raw.shader_texture_colors_frag),
    TEXTURE("texture_", R.raw.shader_texture_vert, R.raw.shader_texture_frag),
    COLORS("colors_", R.raw.shader_colors_vert, R.raw.shader_colors_frag),
    SHADER("default", R.raw.shader_vert, R.raw.shader_frag);
    
    String id;
    int vertexShaderResourceId = -1;
    int fragmentShaderResourceId = -1;
    
    Shader (String id, int vertexShaderCode, int fragmentShaderCode){
        this.id = id;
        this.vertexShaderResourceId = vertexShaderCode;
        this.fragmentShaderResourceId = fragmentShaderCode;
    }
}
