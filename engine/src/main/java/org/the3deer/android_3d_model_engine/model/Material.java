package org.the3deer.android_3d_model_engine.model;

import android.graphics.Bitmap;

import java.util.Arrays;

public class Material {

    // material name
    private String name;

    // colour info
    private float[] ambient;
    private float[] diffuse;
    private float[] specular;
    private float shininess;
    private float alpha = 1.0f;

    // texture info
    private Bitmap colorTexture;
    private String textureFile;
    private byte[] textureData;
    private Bitmap normalTexture;
    private Bitmap emissiveTexture;

    // // Loaded by ModelRenderer (GLThread)
    private int textureId = -1;
    private int normalTextureId = -1;
    private int emissiveTextureId = -1;
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

    public void setColorTexture(Bitmap colorTexture){
        this.colorTexture = colorTexture;
    }

    public Bitmap getColorTexture() {
        return this.colorTexture;
    }

    public Bitmap getNormalTexture() {
        return normalTexture;
    }

    public void setNormalTexture(Bitmap normalTexture) {
        this.normalTexture = normalTexture;
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

    public int getNormalTextureId() {
        return normalTextureId;
    }

    public void setNormalTextureId(int normalTextureId) {
        this.normalTextureId = normalTextureId;
    }

    public Bitmap getEmissiveTexture() {
        return emissiveTexture;
    }

    public void setEmissiveTexture(Bitmap emissiveTexture) {
        this.emissiveTexture = emissiveTexture;
    }

    public int getEmissiveTextureId() {
        return emissiveTextureId;
    }

    public void setEmissiveTextureId(int emissiveTextureId) {
        this.emissiveTextureId = emissiveTextureId;
    }

    public float[] getColor(){
        if (this.color == null){
            this.color = Constants.COLOR_WHITE.clone();
        }
        if (this.diffuse != null){
            this.color[0] = this.diffuse[0];
            this.color[1] = this.diffuse[1];
            this.color[2] = this.diffuse[2];
        }
        if (this.ambient != null){
            this.color[0] += this.ambient[0];
            this.color[1] += this.ambient[1];
            this.color[2] += this.ambient[2];
        }
        this.color[3] = this.alpha;
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
