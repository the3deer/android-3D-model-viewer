package org.andresoviedo.android_3d_model_engine.model;

/**
 *
 * @author leo
 */
public class Ball {

    public Vec3 position = new Vec3();
    public float radius;

    public Ball(float radius, float x, float y, float z) {
        this.radius = radius;
        position.set(x, y, z);
    }
    
    private final Vec3 vTmp = new Vec3();
    
    public Vec3 getCollisionNormalPenetration(Point p) {
        vTmp.set(p.position);
        vTmp.sub(position);
        float size = vTmp.getSize();
        if (size <= radius) {
            float dif = radius - size;
            vTmp.normalize();
            vTmp.scale(dif);
            return vTmp;
        }
        else {
            return null;
        }
    }
}
