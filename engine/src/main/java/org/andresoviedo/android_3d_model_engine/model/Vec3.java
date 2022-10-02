package org.andresoviedo.android_3d_model_engine.model;

import java.util.Objects;

/**
 *
 * @author leonardo
 */
public class Vec3 {

    public float x;
    public float y;
    public float z;

    public Vec3() {
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }
    
    public void add(Vec3 v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public void sub(Vec3 v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public void scale(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
    }
    
    public void scale(float sx, float sy, float sz) {
        this.x *= sx;
        this.y *= sy;
        this.z *= sz;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    
    public float getSize() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void normalize() {
        scale(1 / getSize());
    }

    @Override
    public String toString() {
        return "Vec3{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec3 vec3 = (Vec3) o;
        return Float.compare(vec3.x, x) == 0 &&
                Float.compare(vec3.y, y) == 0 &&
                Float.compare(vec3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
