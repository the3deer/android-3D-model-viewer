package org.andresoviedo.android_3d_model_engine.model;

import org.andresoviedo.util.math.Math3DUtils;

public final class Triangle {

    public final int id;
    public final float[] v1;
    public final float[] v2;
    public final float[] v3;
    public final float[] centroid;
    public final float[] normal;
    public final BoundingSphere bsphere;
    public final BoundingBox bbox;

    public Triangle(int id, float[] v1, float[] v2, float[] v3) {
        this.id = id;
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.centroid = Math3DUtils.calculateFaceCenter(v1, v2, v3);
        this.bbox = new BoundingBox("bbox-triangle-"+id, v1, v2, v3, Math3DUtils.IDENTITY_MATRIX);

        float[] v1c = Math3DUtils.substract(v1, centroid);
        float[] v2c = Math3DUtils.substract(v2, centroid);
        float[] v3c = Math3DUtils.substract(v3, centroid);

        float v1l = Math3DUtils.length(v1c);
        float v2l = Math3DUtils.length(v2c);
        float v3l = Math3DUtils.length(v3c);

        float radius = Math.max(v1l, Math.max(v2l, v3l));

        this.bsphere = new BoundingSphere("bsphere"+id, this.centroid, radius);
        this.normal = Math3DUtils.calculateNormal(v1, v2, v3);
        if (Math3DUtils.length(this.normal) != 0)
            Math3DUtils.normalize(this.normal);
    }
}
