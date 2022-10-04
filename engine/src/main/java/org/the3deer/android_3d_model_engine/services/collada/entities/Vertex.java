package org.the3deer.android_3d_model_engine.services.collada.entities;

import androidx.annotation.NonNull;

public class Vertex implements Cloneable {

    private static final int NO_INDEX = -1;

    private final int vertexIndex;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private int colorIndex = NO_INDEX;

    private VertexSkinData weightsData;

    public Vertex(int vertexIndex) {
        this.vertexIndex = vertexIndex;
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

    public int getTextureIndex() {
        return textureIndex;
    }

    public int getNormalIndex() {
        return normalIndex;
    }

    public void setWeightsData(VertexSkinData weightsData) {
        this.weightsData = weightsData;
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

    @NonNull
    @Override
    protected Vertex clone() throws CloneNotSupportedException {
        return (Vertex) super.clone();
    }
}
