package org.the3deer.android_3d_model_engine.camera;

import android.util.Log;

import org.the3deer.android_3d_model_engine.model.Animation;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.util.math.Math3DUtils;

import java.util.Arrays;

public class CameraAnimation extends Animation<Camera> {

    private final Object[] args;
    private int animationCounter;

    public CameraAnimation(Camera camera, Object[] args) {
        super(camera);
        this.args = args;
    }

    public synchronized void animate() {
        if (animationCounter == 0){
            Log.v("CameraAnimation","Starting animation: "+ Arrays.toString(args));
        }
        else if (animationCounter == -1 || animationCounter > Constants.TOTAL_ANIMATION_FRAMES) {
            animationCounter = -1;
            finished = true;
            return;
        }

        String method = (String) args[0];
        if (method.equals("moveTo")) {
            float progress = animationCounter;
            float x0 = (Float) args[1];
            float y0 = (Float) args[2];
            float z0 = (Float) args[3];
            float xUp0 = (Float) args[4];
            float yUp0 = (Float) args[5];
            float zUp0 = (Float) args[6];

            float x2 = (Float) args[7];
            float y2 = (Float) args[8];
            float z2 = (Float) args[9];

            float xUp2 = (float) args[10];
            float yUp2 = (float) args[11];
            float zUp2 = (float) args[12];

            progress = (float)Math.sin(Math.toRadians(progress * 90f / Constants.TOTAL_ANIMATION_FRAMES));
            //progress = progress / 100f;



            // default UP vector does not change
            float xUp = xUp0;
            float yUp = yUp0;
            float zUp = zUp0;

            /*if (lastAction.length >= 11){
                // UP vector was forced
                xUp = xUp0 + ((float)(lastAction[10]) - xUp0)*progress;
                yUp = yUp0 + ((float)(lastAction[11]) - yUp0)*progress;
                zUp = zUp0 + ((float)(lastAction[12]) - zUp0)*progress;

            } else {
                // UP vector must be recalculated
                // cross
                float[] right = Math3DUtils.crossProduct(-x0, -y0, -z0, xUp0, yUp0, zUp0);
                Math3DUtils.normalize(right);

                float[] cross = Math3DUtils.crossProduct(right[0], right[1], right[2], -x2, -y2, -z2);
                if (Math3DUtils.length(cross) > 0f){
                    Math3DUtils.normalize(cross);
                    xUp = xUp0 + (cross[0]-xUp0)*progress;
                    yUp = yUp0 + (cross[1]-yUp0)*progress;
                    zUp = zUp0 + (cross[2]-zUp0)*progress;
                }
            }*/

            if (Math3DUtils.length(x2,y2,z2) > 0 && Math3DUtils.length(xUp,yUp,zUp) > 0) {

                float xPos = x0 + (x2 - x0) * progress;
                float yPos = y0 + (y2 - y0) * progress;
                float zPos = z0 + (z2 - z0) * progress;

                // UP vector was forced
                xUp = xUp0 + (xUp2 - xUp0)*progress;
                yUp = yUp0 + (yUp2 - yUp0)*progress;
                zUp = zUp0 + (zUp2 - zUp0)*progress;

                float xView = getTarget().getxView();
                float yView = getTarget().getyView();
                float zView = getTarget().getzView();

                if (args.length > 13) {
                    float xView0 = (float) args[13];
                    float yView0 = (float) args[14];
                    float zView0 = (float) args[15];

                    float xView2 = (float) args[16];
                    float yView2 = (float) args[17];
                    float zView2 = (float) args[18];

                    xView = xView0 + (xView2 - xView0)*progress;
                    yView = yView0 + (yView2 - yView0)*progress;
                    zView = zView0 + (zView2 - zView0)*progress;
                }

                getTarget().set(xPos, yPos, zPos,
                        xView, yView, zView,
                        xUp, yUp, zUp);
            }
        }
        /*else if (method.equals("translate")) {
            float progress = (float)Math.sin(Math.toRadians(90+ animationCounter * (90f / TOTAL_ANIMATION_FRAMES)));
            float dX = (Float) lastAction[1] * progress;
            float dY = (Float) lastAction[2] * progress;
            translateCameraImpl(dX, dY);
        } else if (method.equals("rotate")) {
            float rotZ = (Float) lastAction[1];
            RotateImpl(rotZ / TOTAL_ANIMATION_FRAMES * animationCounter);
        }*/
        animationCounter++;
    }
}
