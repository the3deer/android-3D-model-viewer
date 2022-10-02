package org.andresoviedo.android_3d_model_engine.collision;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.BoundingBox;
import org.andresoviedo.android_3d_model_engine.model.BoundingSphere;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.model.Triangle;
import org.andresoviedo.util.math.Math3DUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Andres on 21/12/2017.
 */

public final class Octree {

    // The minimum size of the 3D space for individual boxes
    // if the model is 100 size, then 10 for boxes is OK
    public static final double BOX_SIZE = 10;

    final BoundingBox boundingBox;
    final BoundingSphere bsphere;
    //final BoundingSphere boundingSphere;
    private final List<Triangle> pending = new ArrayList<>();
    public final List<Triangle> triangles = new ArrayList<>();
    public Octree[] children = null;

    private Octree(BoundingBox box, BoundingSphere bSphere){
        this.boundingBox = box;
        this.bsphere = bSphere;
    }

    public Octree[] getChildren(){
        return children;
    }

    public List<Triangle> getTriangles(){
        return triangles;
    }

    private void subdivide(){
        Log.v("Octree", "Subdividing octree...");
        for (Octree child : children){
            subdivide(child);
        }
    }

    static Octree build(Object3DData object){
        Log.i("Octree", "Building octree for "+object.getId());
        final BoundingBox boundingBox = object.getBoundingBox();
        float[] min = boundingBox.getMin();
        float[] max = boundingBox.getMax();
        float[] centroid = Math3DUtils.divide(Math3DUtils.add(min, max), 2);
        float radius = Math3DUtils.length(Math3DUtils.substract(max, centroid));
        final BoundingSphere bsphere = new BoundingSphere("bsphere", centroid, radius);
        final Octree ret = new Octree(boundingBox, bsphere);
        if (object.getDrawOrder() == null) {
            // vertex array contains vertex in sequence
            final FloatBuffer buffer = object.getVertexBuffer().asReadOnlyBuffer();
            final List<Triangle> triangles = new ArrayList<>(buffer.capacity() / 3 * 4);
            //final float[] modelMatrix = object.getModelMatrix();
            //final float[] modelMatrix = Math3DUtils.IDENTITY_MATRIX;
            buffer.position(0);
            for (int i = 0; i < buffer.capacity(); i += 9) {
                float[] v1 = new float[]{buffer.get(), buffer.get(), buffer.get(), 1};
                float[] v2 = new float[]{
                        buffer.get(), buffer.get(), buffer.get(), 1};
                float[] v3 = new float[]{
                        buffer.get(), buffer.get(), buffer.get(), 1};
                //Matrix.multiplyMV(triangle, 0, modelMatrix, 0, triangle, 0);
                //Matrix.multiplyMV(triangle, 4, modelMatrix, 0, triangle, 4);
                //Matrix.multiplyMV(triangle, 8, modelMatrix, 0, triangle, 8);
                triangles.add(new Triangle(0, v1, v2, v3));
            }
            ret.pending.addAll(triangles);
        } else {
            // faces are built
            final IntBuffer drawOrder = object.getDrawOrder().asReadOnlyBuffer();
            final FloatBuffer buffer = object.getVertexBuffer().asReadOnlyBuffer();
            final List<Triangle> triangles = new ArrayList<>(drawOrder.capacity() / 3 * 4);
            //final float[] modelMatrix = object.getModelMatrix();
            //final float[] modelMatrix = Math3DUtils.IDENTITY_MATRIX;
            for (int i = 0; i < drawOrder.capacity(); i += 3) {
                float[] v1 = new float[]{
                        buffer.get(drawOrder.get(i)), buffer.get(drawOrder.get(i)+1), buffer.get(drawOrder.get(i)+2), 1};
                float[] v2 = new float[]{
                        buffer.get(drawOrder.get(i+1)), buffer.get(drawOrder.get(i+1)+1), buffer.get(drawOrder.get(i+1)+2), 1};
                float[] v3 = new float[]{
                        buffer.get(drawOrder.get(i+2)), buffer.get(drawOrder.get(i+2)+1), buffer.get(drawOrder.get(i+2)+2), 1};
                //Matrix.multiplyMV(triangle, 0, modelMatrix, 0, triangle, 0);
                //Matrix.multiplyMV(triangle, 4, modelMatrix, 0, triangle, 4);
                //Matrix.multiplyMV(triangle, 8, modelMatrix, 0, triangle, 8);
                triangles.add(new Triangle(1, v1,v2,v3));
            }
            ret.pending.addAll(triangles);
        }
        subdivide(ret);
        Log.i("Octree", "Octree built for "+object.getId());
        return ret;
    }

    private static void subdivide(Octree octree){
        Log.d("Octree", "Subdividing octree ("+octree.boundingBox+"): "+octree.pending.size());
        float[] min = octree.boundingBox.getMin();
        float[] max = octree.boundingBox.getMax();
        float[] size = octree.boundingBox.getSize();

        // if size is mall enough, we stop subdividing
        //if (size[0] < BOX_SIZE || size[1] < BOX_SIZE || size[2] < BOX_SIZE) {
        if(octree.pending.size() < 256){
            octree.triangles.addAll(octree.pending);
            octree.pending.clear();
            return;
        }

        // split by 8
        float[] mid = Math3DUtils.divide(Math3DUtils.add(max,min),2);
        BoundingBox[] octant = new BoundingBox[8];
        BoundingSphere[] bsphere = new BoundingSphere[8];
        float xMin,yMin,zMin,xMax,yMax,zMax;
        final double radius = Math3DUtils.length(Math3DUtils.add(min, max))/4;

        // let's subdivide...
        octree.children = new Octree[8];

        xMin = min[0]; yMin = min[1]; zMin = min[2];
        xMax = mid[0]; yMax = mid[1]; zMax = mid[2];
        octant[0] = new BoundingBox("octree0",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[0] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[0] = new Octree(octant[0], bsphere[0]);

        xMin = mid[0]; yMin = min[1]; zMin = min[2];
        xMax = max[0]; yMax = mid[1]; zMax = mid[2];
        octant[1] = new BoundingBox("octree1",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[1] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[1] = new Octree(octant[1], bsphere[1]);

        xMin = min[0]; yMin = mid[1]; zMin = min[2];
        xMax = mid[0]; yMax = max[1]; zMax = mid[2];
        octant[2] = new BoundingBox("octree2",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[2] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[2] = new Octree(octant[2], bsphere[2]);

        xMin = mid[0]; yMin = mid[1]; zMin = min[2];
        xMax = max[0]; yMax = max[1]; zMax = mid[2];
        octant[3] = new BoundingBox("octree3",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[3] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[3] = new Octree(octant[3], bsphere[3]);

        xMin = min[0]; yMin = min[1]; zMin = mid[2];
        xMax = mid[0]; yMax = mid[1]; zMax = max[2];
        octant[4] = new BoundingBox("octree4",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[4] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[4] = new Octree(octant[4], bsphere[4]);

        xMin = mid[0]; yMin = min[1]; zMin = mid[2];
        xMax = max[0]; yMax = mid[1]; zMax = max[2];
        octant[5] = new BoundingBox("octree5",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[5] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[5] = new Octree(octant[5], bsphere[5]);

        xMin = min[0]; yMin = mid[1]; zMin = mid[2];
        xMax = mid[0]; yMax = max[1]; zMax = max[2];
        octant[6] = new BoundingBox("octree6",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[6] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[6] = new Octree(octant[6], bsphere[6]);

        xMin = mid[0]; yMin = mid[1]; zMin = mid[2];
        xMax = max[0]; yMax = max[1]; zMax = max[2];
        octant[7] = new BoundingBox("octree7",xMin,xMax,yMin,yMax,zMin,zMax);
        bsphere[7] = new BoundingSphere("bsphere",
                new float[]{(xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2}, radius);
        octree.children[7] = new Octree(octant[7], bsphere[7]);

        for (Iterator<Triangle> it = octree.pending.iterator(); it.hasNext(); ) {
            final Triangle triangle = it.next();
            for (int i = 0; i < octant.length; i++) {
                if (octant[i].insideBounds(triangle.v1) || octant[i].insideBounds(triangle.v2) || octant[i].insideBounds(triangle.v3)){
                    octree.children[i].pending.add(triangle);
                }
            }
            it.remove();
        }
        for (Octree child : octree.children) {
            subdivide(child);
        }
    }
}