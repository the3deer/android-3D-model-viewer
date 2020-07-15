package org.andresoviedo.android_3d_model_engine.model;

import org.andresoviedo.util.io.IOUtils;

import java.nio.IntBuffer;
import java.util.List;

public class Element {

    public static class Builder {

        // polygon
        private String id;
        private List<Integer> indices;

        // materials
        private String materialId;

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public String getId() {
            return this.id;
        }


        public Builder indices(List<Integer> indices) {
            this.indices = indices;
            return this;
        }

        public Builder materialId(String materialId) {
            this.materialId = materialId;
            return this;
        }

        public String getMaterialId() {
            return this.materialId;
        }

        public Element build() {
            return new Element(id, indices, materialId);
        }



    }

    // polygon
    private final String id;
    private final List<Integer> indicesArray;
    private IntBuffer indexBuffer;

    // material
    private String materialId;
    private Material material;

    public Element(String id, List<Integer> indexBuffer, String material) {
        this.id = id;
        this.indicesArray = indexBuffer;
        this.materialId = material;
    }

    public Element(String id, IntBuffer indexBuffer, String material) {
        this.id = id;
        this.indicesArray = null;
        this.indexBuffer = indexBuffer;
        this.materialId = material;
    }

    public String getId() {
        return this.id;
    }

    public List<Integer> getIndices() {
        return this.indicesArray;
    }


    public IntBuffer getIndexBuffer() {
        if (indexBuffer == null) {
            this.indexBuffer = IOUtils.createIntBuffer(indicesArray.size());
            this.indexBuffer.position(0);
            for (int i = 0; i < indicesArray.size(); i++) {
                this.indexBuffer.put(indicesArray.get(i));
            }
        }
        return indexBuffer;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return "Element{" +
                "id='" + id + '\'' +
                ", indices="+(indicesArray != null? indicesArray.size(): null)+
                ", indexBuffer="+indexBuffer+
                ", material=" + material +
                '}';
    }
}
