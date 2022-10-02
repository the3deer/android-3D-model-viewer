package org.andresoviedo.android_3d_model_engine.model;

/**
 *
 * @author leonardo
 */
public class Stick {

    public Point a;
    public Point b;
    public float size;
    public boolean visible;
    private final Vec3 vTmp = new Vec3();
    
    public Stick(Point a, Point b, boolean visible) {
        this.a = a;
        this.b = b;
        this.visible = visible;

        vTmp.set(b.position);
        vTmp.sub(a.position);
        this.size = vTmp.getSize();
    }

    public Point getA() {
        return a;
    }

    public void setA(Point a) {
        this.a = a;
    }

    public Point getB() {
        return b;
    }

    public void setB(Point b) {
        this.b = b;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void update() {
        vTmp.set(b.position);
        vTmp.sub(a.position);
        float currentSize = vTmp.getSize();
        float dif = (float) ((currentSize - size) * 0.5);
        vTmp.normalize();
        vTmp.scale(dif * 1);
        if (!a.isPinned()) {
            a.position.add(vTmp);
        }
        if (!b.isPinned()) {
            b.position.sub(vTmp);
        }
    }
}
