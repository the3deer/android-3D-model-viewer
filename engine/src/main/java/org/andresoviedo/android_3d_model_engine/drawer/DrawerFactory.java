package org.andresoviedo.android_3d_model_engine.drawer;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;

import java.io.IOException;

public class DrawerFactory {

    private Object3D object3dv1;
    private Object3D object3dv2;
    private Object3D object3dv3;
    private Object3D object3dv4;
    private Object3D object3dv5;
    private Object3D object3dv6;
    private Object3D object3dv7;
    private Object3D object3dv8;
    private Object3D object3dv9;
    private Object3D object3dv91;
    private Object3D object3dv10;
    private Object3D object3dv11;
    private Object3D object3dv12;

    public Object3D getBoundingBoxDrawer() {
        return object3dv2;
    }

    public Object3D getFaceNormalsDrawer() {
        return object3dv1;
    }

    public Object3D getPointDrawer() {
        if (object3dv1 == null) {
            object3dv1 = new Object3DV1();
        }
        return object3dv1;
    }

    public Object3D getDrawer(Object3DData obj, boolean usingTextures, boolean usingLights, boolean usingAnimation) throws IOException {

        if (object3dv2 == null) {
            try {
                getPointDrawer();
                object3dv2 = new Object3DV2();
                object3dv3 = new Object3DV3();
                object3dv4 = new Object3DV4();
                object3dv5 = new Object3DV5();
                object3dv6 = new Object3DV6();
                object3dv7 = new Object3DV7();
                object3dv8 = new Object3DV8();
                object3dv9 = new Object3DV9();
                object3dv91 = new Object3DV91();
                object3dv10 = new Object3DV10();
                object3dv11 = new Object3DV11();
                object3dv12 = new Object3DV12();
            } catch (Exception e) {
                Log.e("Object3DBuilder", "Error creating drawer: " + e.getMessage(), e);
            }
        }

        boolean isAnimated = usingAnimation && obj instanceof AnimatedModel && ((AnimatedModel) obj).getAnimation() != null;
        boolean isUsingLights = usingLights && (obj.getNormals() != null || obj.getVertexNormalsArrayBuffer() != null);
        boolean isTextured = usingTextures && obj.getTextureData() != null && obj.getTextureCoordsArrayBuffer() != null;
        boolean isColoured = obj.getVertexColorsArrayBuffer() != null;

        if (isAnimated) {
            if (isUsingLights) {
                if (isTextured) {
                    if (!isColoured) {
                        return object3dv9;
                    } else {
                        Log.w("Object3DBuilder", "No drawer for this object");
                        return null;
                    }
                } else {
                    if (isColoured) {
                        return object3dv11;
                    } else {
                        return object3dv10;
                    }
                }
            } else {
                if (isTextured) {
                    if (!isColoured) {
                        return object3dv91;
                    } else {
                        Log.w("Object3DBuilder", "No drawer for this object");
                        return null;
                    }
                } else {
                    if (!isColoured) {
                        return object3dv12;
                    } else {
                        Log.w("Object3DBuilder", "No drawer for this object");
                        return null;
                    }
                }
            }
        } else {
            if (isUsingLights) {
                if (isTextured) {
                    if (isColoured) {
                        return object3dv6;
                    } else {
                        return object3dv8;
                    }
                } else {
                    if (isColoured) {
                        return object3dv5;
                    } else {
                        return object3dv7;
                    }
                }
            } else {
                if (isTextured) {
                    if (isColoured) {
                        return object3dv4;
                    } else {
                        return object3dv3;
                    }
                } else {
                    if (isColoured) {
                        return object3dv2;
                    } else {
                        return object3dv1;
                    }
                }
            }
        }
    }
}
