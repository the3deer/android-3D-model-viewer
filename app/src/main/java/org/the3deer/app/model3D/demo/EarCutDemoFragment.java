package org.the3deer.app.model3D.demo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.android_3d_model_engine.gui.Text;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.model.Scene;
import org.the3deer.android_3d_model_engine.services.LoadListener;
import org.the3deer.android_3d_model_engine.services.wavefront.WavefrontLoaderTask;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.geometry.UnionTri;

import java.net.URI;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class EarCutDemoFragment extends ModelFragment {

    private Scene scene;
    private Camera camera;

    private Text abcd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scene = modelEngine.getBeanFactory().find(Scene.class);
        camera = modelEngine.getBeanFactory().get("gui.camera", Camera.class);
        //camera.getPos()[2]=0;

        try {
            // Create our 3D scenario
            //scene.addListener(this);

            WavefrontLoaderTask task = new WavefrontLoaderTask(getActivity(),
                    URI.create("android://org.the3deer.dddmodel2/assets/models/triangle1.obj"), new LoadListener() {
                @Override
                public void onStart() {
                    // provide context to allow reading resources
                    ContentUtils.setThreadActivity(getActivity());
                }

                @Override
                public void onProgress(String progress) {

                }

                @Override
                public void onLoadError(Exception ex) {

                }

                @Override
                public void onLoad(Object3DData data) {
                    Log.i("PolyBoolDemoActivity", "Adding object...");
                    float value = 20f;
                    data.setScale(new float[]{value, value, value});
                    data.setColor(Constants.COLOR_RED);
                    data.setLocation(new float[]{0, -0.25f, 0});
                    scene.addObject(data);
                }

                @Override
                public void onLoadComplete() {

                }
            });
            task.execute();

            WavefrontLoaderTask task2 = new WavefrontLoaderTask(getActivity(),
                    URI.create("android://org.the3deer.dddmodel2/assets/models/triangle2.obj"),
                    new LoadListener() {
                @Override
                public void onStart() {
                    // provide context to allow reading resources
                    ContentUtils.setThreadActivity(getActivity());
                }

                @Override
                public void onProgress(String progress) {

                }

                @Override
                public void onLoadError(Exception ex) {

                }

                @Override
                public void onLoad(Object3DData data) {
                    Log.i("PolyBoolDemoActivity", "Adding object...");
                    float value = 20f;
                    data.setScale(new float[]{value, value, value});
                    data.setColor(Constants.COLOR_BLUE);
                    data.setLocation(new float[]{0, -0.25f, 0});
                    scene.addObject(data);
                }

                @Override
                public void onLoadComplete() {
                    try {
                        /*Thread.sleep(2000);

                        float[] matrix;

                        int c = 0;
                        float[] data = new float[
                                obj1.getVertexBuffer().capacity() / 3 * 2 +
                                        obj2.getVertexBuffer().capacity() / 3 * 2];
                        List<TriangulationPoint> points = new ArrayList<>();

                        for (int i = 0, j = 0; j < obj1.getVertexBuffer().capacity(); i += 2, j += 3) {
                            data[c++] = obj1.getVertexBuffer().get(j);
                            data[c++] = obj1.getVertexBuffer().get(j + 1);
                            points.add(new TPoint(obj1.getVertexBuffer().get(j), obj1.getVertexBuffer().get(j + 1)));
                        }

                        for (int i = 0, j = 0; j < obj2.getVertexBuffer().capacity(); i += 2, j += 3) {
                            data[c++] = obj2.getVertexBuffer().get(j);
                            data[c++] = obj2.getVertexBuffer().get(j + 1);
                            points.add(new TPoint(obj2.getVertexBuffer().get(j), obj2.getVertexBuffer().get(j + 1)));
                        }
                        // int[] holes = new int[]{4};


                        ConstrainedPointSet cps = new ConstrainedPointSet(points, new int[]{3, 4, 5, 6, 7, 8});
                        Poly2Tri.triangulate(cps);
                        List<DelaunayTriangle> triangles = cps.getTriangles();

                        FloatBuffer vertex = IOUtils.createFloatBuffer(
                                triangles.size() * 3 * 3);

                        //IntBuffer index = IOUtils.createIntBuffer(triangles.size()*3);
                        triangles.stream().forEach(triangle -> {
                            for (TriangulationPoint tp : triangle.points) {
                                vertex.put(tp.getXf());
                                vertex.put(tp.getYf());
                                vertex.put(tp.getZf());
                            }
                        });

                        Object3DData data2 = new Object3DData(vertex);
                        data2.setDrawMode(GLES20.GL_TRIANGLES);
                        data2.setScale(new float[]{50f, 50f, 50f});
                        data2.setColor(Constants.COLOR_GREEN);
                        data2.setLocation(new float[]{10, 0, 0});*/

                        //scene.addObject(data2);


                        //List<Integer> indices = EarCut.earcut(data, holes, 2);

                        /*Log.d("PolyBoolDemoActivity","array: "+ Arrays.toString(data));
                        Log.d("PolyBoolDemoActivity","array: "+ indices);

                        FloatBuffer vertex = IOUtils.createFloatBuffer(
                                obj1.getVertexBuffer().capacity()+
                                        obj2.getVertexBuffer().capacity());
                        vertex.put(obj1.getVertexBuffer());
                        vertex.put(obj2.getVertexBuffer());
                        IntBuffer index = IOUtils.createIntBuffer(indices.size());
                        index.put(indices.stream().mapToInt(Integer::intValue).toArray());

                        Object3DData data2 = new Object3DData(vertex, index);

                        data2.setDrawMode(GLES20.GL_TRIANGLES);
                        data2.setScale(new float[]{50f,50f,50f});
                        data2.setColor(Constants.COLOR_GREEN);
                        data2.setLocation(new float[]{10,0,0});

                        scene.addObject(data2);*/


                        Object3DData merge = UnionTri.merge(scene.getObjects());

                        merge = UnionTri.triangulate(merge);

                        merge.setColor(Constants.COLOR_GREEN);
                        merge.setScale(new float[]{50f,50f,50f});
                        merge.setLocation(new float[]{0f,0f,-50f});

                        scene.addObject(merge);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            task2.execute();


        } catch (Exception e) {
            Log.e("EarCutDemoFragment", e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading OpenGL view:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
