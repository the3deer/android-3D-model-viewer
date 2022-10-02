package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class SimpleCloth extends Cloth3D {

    private final Point[][] points;
    private final List<Stick> sticks = new ArrayList<>();

    public SimpleCloth(FloatBuffer vertexBuffer, int xSize, int ySize, int zSize) {
        super(vertexBuffer);
        this.points = new Point[xSize][zSize];
    }

    public void update(Object3DData obj) {

        BoundingBox bbox = obj.getBoundingBox();

        Log.v("SimpleCloth", "Updating points: "+points.length);
        for (int z=0; z<points.length;z++) {
            for (int x = 0; x < points[0].length; x++) {
                points[z][x].update();
            }
        }

        for (int i = 0; i < 1; i++) {
            Log.v("SimpleCloth", "Updating sticks: "+sticks.size());
            for (int z=0; z<sticks.size();z++) {
                Stick stick = sticks.get(z);
                //stick.update();
            }
            Log.v("SimpleCloth", "Processing collisions...");
            for (int z=0; z<points.length;z++) {
                for (int x = 0; x < points[0].length; x++) {
                    points[z][x].updateCollision(obj, bbox);
                    updateVertexBuffer(this.vertexBuffer, this);
                }
            }
        }
        Log.v("SimpleCloth", "Updating vertex buffer...");
        updateVertexBuffer(this.vertexBuffer, this);
    }

    public static SimpleCloth build(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd,
                                     float step) {
        int xPoints = (int) (((xEnd - xStart) / step) + 1);
        int zPoints = (int) (((zEnd - zStart) / step) + 1);
        int yPoints = (int) (((yEnd - yStart) / step) + 1);


        int nbPoints = (int) Math.max(xPoints, 1) * (int) Math.max(yPoints, 1) * (int) Math.max(zPoints, 1);
        int nbVertex = nbPoints;

        Log.i("SimpleCloth", "Number of vertex: "+nbVertex);

        FloatBuffer vertexBuffer = IOUtils.createFloatBuffer(nbVertex * 3 * 2);

        SimpleCloth cloth = new SimpleCloth(vertexBuffer, xPoints, yPoints, zPoints);

        if (yPoints < 2) {
            int idx = 0;
            float y = yStart;
            for (int z = 0; z < zPoints; z++) {
                for (int x = 0; x < xPoints; x++) {
                    cloth.points[z][x] = new Point(xStart + (step * x), y, zStart + (step * z));
                    cloth.points[z][x].idx = idx++;
                }
            }
        }

        for (int z = 0; z < cloth.points.length; z++) {
            for (int x = 0; x < cloth.points[0].length - 1; x++) {
                Stick stick = new Stick(cloth.points[z][x], cloth.points[z][x + 1], true);
                cloth.sticks.add(stick);
            }
        }

        for (int z = 0; z < cloth.points.length - 1; z++) {
            for (int x = 0; x < cloth.points[0].length; x++) {
                Stick stick = new Stick(cloth.points[z][x], cloth.points[z + 1][x], true);
                cloth.sticks.add(stick);
            }
        }

        //---------------------------------

        updateVertexBuffer(vertexBuffer, cloth);

        int totalIdx = xPoints * yPoints * zPoints * 6;

        int idx = 0;
        IntBuffer idxBuffer = IOUtils.createIntBuffer(totalIdx);
        for (int z=0; z<cloth.points.length-1; z++) {
            for (int x = 0; x < cloth.points[0].length-1; x++) {
                idxBuffer.put(cloth.points[z][x].getVertexIndex());
                idxBuffer.put(cloth.points[z][x+1].getVertexIndex());
                idxBuffer.put(cloth.points[z+1][x+1].getVertexIndex());

                idxBuffer.put(cloth.points[z+1][x+1].getVertexIndex());
                idxBuffer.put(cloth.points[z+1][x].getVertexIndex());
                idxBuffer.put(cloth.points[z][x].getVertexIndex());


            }
        }

        cloth.setDrawOrder(idxBuffer);
        cloth.setDrawMode(GLES20.GL_TRIANGLES);
        cloth.setDrawUsingArrays(false);

//        if (yPoints < 2) {
//            for (float z = zStart; z < zEnd - step; z += step) {
//                vertexBuffer.put(xStart).put(yStart).put(z);
//                for (float x = xStart + step; x < xEnd; x += step) {
//                    vertexBuffer.put(x).put(yStart).put(z);
//                    vertexBuffer.put(x).put(yStart).put(z+step);
//                }
//                if (z < zEnd - step)
//                    vertexBuffer.put(xEnd).put(yStart).put(z+step);
//            }
//        }
            return cloth;

    }

    private static void updateVertexBuffer(FloatBuffer vertexBuffer, SimpleCloth cloth) {
        vertexBuffer.position(0);
        final int stride = cloth.points[0].length * 3;
        for (int z=0; z<cloth.points.length; z++){
            for (int x=0; x<cloth.points[z].length; x++){
                float[] position = cloth.points[z][x].getPosition();
                vertexBuffer.put(z*stride+x*3, position[0]);
                vertexBuffer.put(z*stride+x*3+1, position[1]);
                vertexBuffer.put(z*stride+x*3+2, position[2]);
            }
        }
    }
}
