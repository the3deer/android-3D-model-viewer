package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.LoadListenerAdapter;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.andresoviedo.android_3d_model_engine.util.Rescaler;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.net.URI;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Cloth3D extends Object3DData {

    private final List<Point> points = new ArrayList<>();
    private final List<Stick> sticks = new ArrayList<>();

    public Cloth3D() {
        super();
    }

    public Cloth3D(FloatBuffer vertexBuffer) {
        super(vertexBuffer);
    }

    public void update(Object3DData obj) {

        BoundingBox bbox = obj.getBoundingBox();

        for (int z=0; z<points.size();z++) {
            points.get(z).update();
        }
        for (int i = 0; i < 1; i++) {
            for (Stick stick : sticks) {
                //stick.update();
            }
            //if (ball.getBoundingBox().insideBounds(this.getBoundingBox())) {
                for (int z = 0; z < points.size(); z++) {
                    points.get(z).updateCollision(obj, bbox);
                }
            //}
        }
        updateVertexBuffer();
    }

    public static Cloth3D build() {

        final Cloth3D cloth3D = new Cloth3D();

        try {
            // this has no color array

            Object3DData obj51 = new WavefrontLoader(GLES20.GL_TRIANGLE_FAN, new LoadListenerAdapter(){
                @Override
                public void onLoad(Object3DData obj53) {
                }
            }).load(new URI("android://org.andresoviedo.dddmodel2/assets/models/Top%201.obj")).get(0);

            // testing - too big
            Rescaler.resize(obj51, 0.1f);

            //Rescaler.rescale(obj53, 2f);
            cloth3D.setVertexBuffer(obj51.getVertexBuffer());
            cloth3D.setNormalsBuffer(obj51.getNormalsBuffer());
            cloth3D.setDrawOrder(obj51.getDrawOrder());
            cloth3D.setDrawMode(obj51.getDrawMode());
            cloth3D.setElements(obj51.getElements());
            cloth3D.setMaterial(obj51.getMaterial());
            cloth3D.setDrawUsingArrays(obj51.isDrawUsingArrays());

            //cloth3D.setLocation(new float[] { 0f, -30f, 0f });
            //cloth3D.setColor(new float[] { 1.0f, 1.0f, 0f, 1.0f });
            //cloth3D.setScale(new float[]{0.1f,0.1f,0.1f});

            cloth3D.setColor(new float[]{1f,0f,0f,1f}).setId("grid-cloth").setSolid(false);

            final float[] modelMatrix = cloth3D.getModelMatrix();

            final FloatBuffer vertexBuffer = cloth3D.getVertexBuffer();
            final List<Point> points = cloth3D.points;
            final List<Stick> sticks  = cloth3D.sticks;
            for (int i = 0; i<vertexBuffer.capacity(); i+=3){
                final float[] v = {vertexBuffer.get(i), vertexBuffer.get(i+1), vertexBuffer.get(i+2), 1};
                final float[] r = new float[4];
                Matrix.multiplyMV(r, 0, modelMatrix, 0, v, 0);
                points.add(new Point(r[0], r[1]-120f, r[2]));
            }

            for (Element e : cloth3D.getElements()){
                final IntBuffer indexBuffer = e.getIndexBuffer();
                for (int i=0; i<indexBuffer.capacity(); i+=3){
                    final Point p1 = points.get(indexBuffer.get(i));
                    final Point p2 = points.get(indexBuffer.get(i+1));
                    final Point p3 = points.get(indexBuffer.get(i+2));
                    sticks.add(new Stick(p1, p2, true));
                    sticks.add(new Stick(p2, p3, true));
                    sticks.add(new Stick(p3, p1, true));
                }
            }

            cloth3D.updateVertexBuffer();

            Log.i("Cloth3D","Loaded cloth model. dim: "+cloth3D.getDimensions());
            Log.i("Cloth3D","Loaded cloth model. dim: "+cloth3D.getCurrentDimensions());

            //obj51.setScale(2f,2f,2f);
            //obj51.setSize(0.5f);
            //super.onLoad(obj51);

            return cloth3D;

        } catch (Exception ex) {
            Log.e("Cloth3D","Issue loading cloth model", ex);
            return null;
        }
    }

    private void updateVertexBuffer() {
        final Dimensions dimensions = new Dimensions();
        for (int z=0; z<points.size(); z++){
            final float[] position = points.get(z).getPosition();
            vertexBuffer.put(z*3, position[0]);
            vertexBuffer.put(z*3+1, position[1]);
            vertexBuffer.put(z*3+2, position[2]);
            dimensions.update(position[0], position[1], position[2]);
        }
        setDimensions(dimensions);
    }
}
