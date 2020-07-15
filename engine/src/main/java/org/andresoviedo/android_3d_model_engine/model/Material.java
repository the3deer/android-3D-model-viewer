package org.andresoviedo.android_3d_model_engine.model;

import java.util.Arrays;

public class Material {

    // constants
    private static final float[] COLOR_WHITE = {1f, 1f, 1f, 1f};

    // material name
    private String name;

    // colour info
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;
    private float shininess;
    private float alpha = 1.0f;

    // texture info
    private String textureFile;
    private byte[] textureData;

    // // Loaded by ModelRenderer (GLThread)
    private int textureId = -1;
    private float[] color;

    public Material() {
    }

    public Material(String nm) {
        name = nm;
    }

    // --------- set/get methods for colour info --------------

    public void setAlpha(float val) {
        alpha = val;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setShininess(float val) {
        shininess = val;
    }

    public float getShininess() {
        return shininess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float[] getAmbient() {
        return ambient;
    }

    public void setAmbient(float[] ambient) {
        this.ambient = ambient;
    }

    public float[] getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float[] diffuse) {
        this.diffuse = diffuse;
    }

    public float[] getSpecular() {
        return specular;
    }

    public void setSpecular(float[] specular) {
        this.specular = specular;
    }

    public String getTextureFile() {
        return textureFile;
    }

    public void setTextureFile(String textureFile) {
        this.textureFile = textureFile;
    }

    public void setTextureData(byte[] data) {
        this.textureData = data;
    }

    public byte[] getTextureData() {
        return this.textureData;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public float[] getColor(){

        // if there is texture, we don't take into account color
        // some models have color black so we need to x 1.0f
        if (this.getTextureData() != null){
            return COLOR_WHITE;
        }
        if (this.color == null && this.diffuse != null){
            this.color = new float[4];
            this.color[0] = this.diffuse[0];
            this.color[1] = this.diffuse[1];
            this.color[2] = this.diffuse[2];
            this.color[3] = this.alpha;
        }
        return color;
    }

    @Override
    public String toString() {
        return "Material{" +
                "name='" + name + '\'' +
                ", ambient=" + Arrays.toString(ambient) +
                ", diffuse=" + Arrays.toString(diffuse) +
                ", specular=" + Arrays.toString(specular) +
                ", shininess=" + shininess +
                ", alpha=" + alpha +
                ", textureFile='" + textureFile + '\'' +
                ", textureData="+(textureData != null? textureData.length+" (bytes)":null)+
                ", textureId=" + textureId +
                '}';
    }
}
