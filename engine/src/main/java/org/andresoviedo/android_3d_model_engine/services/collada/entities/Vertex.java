package org.andresoviedo.android_3d_model_engine.services.collada.entities;

public class Vertex {

    private static final int NO_INDEX = -1;

    private float[] position;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private int colorIndex = NO_INDEX;
    private float length;


    private VertexSkinData weightsData;

    public Vertex(float[] position) {
        this.position = position;
        this.length = position.length;
    }

    public VertexSkinData getWeightsData() {
        return weightsData;
    }

    public float getLength() {
        return length;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex) {
        this.normalIndex = normalIndex;
    }

    public float[] getPosition() {
        return position;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }

    public void setWeightsData(VertexSkinData weightsData) {
        this.weightsData = weightsData;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public int getColorIndex() {
        return colorIndex;
    }
}
