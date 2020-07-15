package org.andresoviedo.android_3d_model_engine.services.collada.entities;

public class Vertex {

    private static final int NO_INDEX = -1;

    private int vertexIndex = 0;
    private float[] position;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private int colorIndex = NO_INDEX;

    private VertexSkinData weightsData;

    public Vertex(int vertexIndex) {
        this.vertexIndex = vertexIndex;
    }

    @Deprecated
    public Vertex(float[] position) {
        this.position = position;
    }

    public int getVertexIndex() {
        return vertexIndex;
    }

    public VertexSkinData getWeightsData() {
        return weightsData;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        if (vertexIndex != vertex.vertexIndex) return false;
        if (textureIndex != vertex.textureIndex) return false;
        return normalIndex == vertex.normalIndex;
    }

    @Override
    public int hashCode() {
        int result = vertexIndex;
        result = 31 * result + textureIndex;
        result = 31 * result + normalIndex;
        return result;
    }
}
