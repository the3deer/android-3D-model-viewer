package org.andresoviedo.android_3d_model_engine.drawer;


public enum Shader {

    SKYBOX("skybox_", "shader_skybox_vert", "shader_skybox_frag"),
    ANIM_LIGHT_TEXTURE_COLORS("anim_light_texture_colors_", "shader_anim_light_texture_colors_vert", "shader_anim_light_texture_colors_frag"),
    ANIM_LIGHT_TEXTURE("anim_light_texture_", "shader_anim_light_texture_vert", "shader_anim_light_texture_frag"),
    ANIM_LIGHT_COLORS("anim_light_colors_", "shader_anim_light_colors_vert", "shader_anim_light_colors_frag"),
    ANIM_LIGHT("anim_light_", "shader_anim_light_vert", "shader_anim_light_frag"),
    ANIM_TEXTURE_COLORS("anim_texture_colors_", "shader_anim_texture_colors_vert", "shader_anim_texture_colors_frag"),
    ANIM_TEXTURE("anim_texture_", "shader_anim_texture_vert", "shader_anim_texture_frag"),
    ANIM_COLORS("anim_colors_", "shader_anim_colors_vert", "shader_anim_colors_frag"),
    ANIM("anim_", "shader_anim_vert", "shader_anim_frag"),
    LIGHT_TEXTURE_COLORS("light_texture_colors_", "shader_light_texture_colors_vert", "shader_light_texture_colors_frag"),
    LIGHT_TEXTURE("light_texture_", "shader_light_texture_vert", "shader_light_texture_frag"),
    LIGHT_COLORS("light_colors_", "shader_light_colors_vert", "shader_light_colors_frag"),
    LIGHT("light_", "shader_light_vert", "shader_light_frag"),
    TEXTURE_COLORS("texture_colors_", "shader_texture_colors_vert", "shader_texture_colors_frag"),
    TEXTURE("texture_", "shader_texture_vert", "shader_texture_frag"),
    COLORS("colors_", "shader_colors_vert", "shader_colors_frag"),
    SHADER("default", "shader_vert", "shader_frag");
    
    String id;
    String vertexShaderCode;
    String fragmentShaderCode;
    
    Shader (String id, String vertexShaderCode, String fragmentShaderCode){
        this.id = id;
        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;
    }
}
