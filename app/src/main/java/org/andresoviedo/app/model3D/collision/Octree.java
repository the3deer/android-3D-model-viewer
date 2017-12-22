package org.andresoviedo.app.model3D.collision;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.util.math.Math3DUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Andres on 21/12/2017.
 */

public class Octree {
    final BoundingBox boundingBox;
    final List<float[]> pending = new ArrayList<float[]>();
    final List<float[]> triangles = new ArrayList<float[]>();
    final Octree[] children = new Octree[8];

    Octree(BoundingBox box){
        this.boundingBox = box;
    }

    void addChild(int octant, BoundingBox boundingBox, float[] triangle){
        if (children[octant] == null){
            children[octant] = new Octree(boundingBox);
        }
        children[octant].pending.add(triangle);
    }

    void subdivide(){
        for (Octree child : children){
            if (child != null){
                subdivide(child);
            }
        }
    }

    static Octree build(Object3DData object){
        Log.i("Octree", "Building octree for "+object.getId());
        Octree ret = new Octree(object.getBoundingBox());
        FloatBuffer buffer = object.getVertexArrayBuffer().asReadOnlyBuffer();
        List<float[]> triangles = new ArrayList<float[]>(buffer.capacity()/3*4);
        float[] modelMatrix = object.getModelMatrix();
        for (int i=0; i<buffer.capacity(); i+=9){
            float[] triangle = new float[]{buffer.get(), buffer.get(), buffer.get(), 1,
                    buffer.get(), buffer.get(), buffer.get(), 1,
                    buffer.get(), buffer.get(), buffer.get(), 1
            };
            Matrix.multiplyMV(triangle, 0, modelMatrix, 0, triangle, 0);
            Matrix.multiplyMV(triangle, 4, modelMatrix, 0, triangle, 4);
            Matrix.multiplyMV(triangle, 8, modelMatrix, 0, triangle, 8);
            triangles.add(triangle);
        }
        ret.pending.addAll(triangles);
        subdivide(ret);
        return ret;
    }

    static void subdivide(Octree octree){
        Log.d("Octree", "Subdividing octree ("+octree.boundingBox+"): "+octree.pending.size());
        float[] min = octree.boundingBox.getMin();
        float[] max = octree.boundingBox.getMax();
        float[] mid = Math3DUtils.divide(Math3DUtils.add(max,min),2);
        BoundingBox[] octant = new BoundingBox[8];
        float xMin,yMin,zMin,xMax,yMax,zMax;
        xMin = min[0]; yMin = min[1]; zMin = min[2];
        xMax = mid[0]; yMax = mid[1]; zMax = mid[2];
        octant[0] = new BoundingBox("octree0",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = mid[0]; yMin = min[1]; zMin = min[2];
        xMax = max[0]; yMax = mid[1]; zMax = mid[2];
        octant[1] = new BoundingBox("octree1",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = min[0]; yMin = mid[1]; zMin = min[2];
        xMax = mid[0]; yMax = max[1]; zMax = mid[2];
        octant[2] = new BoundingBox("octree2",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = mid[0]; yMin = mid[1]; zMin = min[2];
        xMax = max[0]; yMax = max[1]; zMax = mid[2];
        octant[3] = new BoundingBox("octree3",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = min[0]; yMin = min[1]; zMin = mid[2];
        xMax = mid[0]; yMax = mid[1]; zMax = max[2];
        octant[4] = new BoundingBox("octree4",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = mid[0]; yMin = min[1]; zMin = mid[2];
        xMax = max[0]; yMax = mid[1]; zMax = max[2];
        octant[5] = new BoundingBox("octree5",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = min[0]; yMin = mid[1]; zMin = mid[2];
        xMax = mid[0]; yMax = max[1]; zMax = max[2];
        octant[6] = new BoundingBox("octree6",xMin,xMax,yMin,yMax,zMin,zMax);
        xMin = mid[0]; yMin = mid[1]; zMin = mid[2];
        xMax = max[0]; yMax = max[1]; zMax = max[2];
        octant[7] = new BoundingBox("octree7",xMin,xMax,yMin,yMax,zMin,zMax);
        boolean anyInoctant = false;
        for (Iterator<float[]> it = octree.pending.iterator(); it.hasNext(); ) {
            float[] triangle = it.next();
            boolean inoctant = false;
            for (int i = 0; i < 8; i++) {
                int inside = octant[i].insideBounds(triangle[0], triangle[1], triangle[2]) ? 1 : 0;
                inside += octant[i].insideBounds(triangle[4], triangle[5], triangle[6]) ? 1 : 0;
                inside += octant[i].insideBounds(triangle[8], triangle[9], triangle[10]) ? 1 : 0;
                if (inside == 3) {
                    inoctant = true;
                    octree.addChild(i, octant[i], triangle);
                    anyInoctant = true;
                    continue;
                }
            }
            if (!inoctant){
                octree.triangles.add(triangle);
            }
            it.remove();
        }
        if (anyInoctant){
            // subdivide if big enough (>=0.02)
            if ((mid[0]+min[0])/2 > 0.01 && (mid[1]+min[1])/2 > 0.01 && (mid[2]+min[2])/2 > 0.01) {
                octree.subdivide();
            }
            else{
                for (Octree child : octree.children) {
                    if (child == null) continue;
                    child.triangles.addAll(child.pending);
                }
            }
        }
    }
}