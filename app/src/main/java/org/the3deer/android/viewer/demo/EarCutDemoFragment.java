package org.the3deer.android.viewer.demo;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.ModelEngineViewModel;
import org.the3deer.android.engine.gui.Text;
import org.the3deer.android.engine.model.Camera;
import org.the3deer.android.engine.model.Constants;
import org.the3deer.android.engine.Model;
import org.the3deer.android.engine.model.Object3D;
import org.the3deer.android.engine.model.Scene;
import org.the3deer.android.engine.services.LoadListenerAdapter;
import org.the3deer.android.engine.services.wavefront.WavefrontLoaderTask;
import org.the3deer.android.viewer.ui.home.HomeFragment;
import org.the3deer.util.geometry.UnionTri;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class EarCutDemoFragment extends HomeFragment {

    private final static String TAG = EarCutDemoFragment.class.getSimpleName();

    private Model sceneManager;
    private Camera camera;
    private Text abcd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHandler().post(this::setUp);
    }

    private void setUp() {
        Log.i(TAG, "Starting up...");

        ModelEngineViewModel modelEngineViewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
        @NotNull Model model = modelEngineViewModel.createModel("demo.earcut");
        ModelEngine modelEngine = modelEngineViewModel.initEngine("demo.earcut");

        sceneManager = modelEngine.getBeanFactory().find(Model.class);
        camera = modelEngine.getBeanFactory().get("gui.camera", Camera.class);
        //camera.getPos()[2]=0;

        try {

            WavefrontLoaderTask task = new WavefrontLoaderTask(
                    Uri.parse("android://org.the3deer.android.viewer/assets/models/triangle1.obj"), new LoadListenerAdapter() {

                @Override
                public void onLoadStart() {
                    super.onLoadStart();
                    // preload assets
                    //ContentUtils.setContext(getActivity());
                }

                @Override
                public void onLoadObject(Scene scene, Object3D data) {
                    Log.i("PolyBoolDemoActivity", "Adding object...");
                    float value = 20f;
                    data.setScale(new float[]{value, value, value});
                    data.setColor(Constants.COLOR_RED);
                    data.setLocation(new float[]{0, -0.25f, 0});
                    scene.addObject(data);
                }

                @Override
                public void onLoadScene(Scene scene) {
                    sceneManager.addScene(scene);
                    sceneManager.update();
                }
            });
            task.execute();

            WavefrontLoaderTask task2 = new WavefrontLoaderTask(
                    Uri.parse("android://org.the3deer.android.viewer/assets/models/triangle2.obj"),
                    new LoadListenerAdapter() {

                        @Override
                        public void onLoadStart() {
                            super.onLoadStart();
                            // preload assets
                            //ContentUtils.setContext(getActivity());
                        }

                        @Override
                        public void onLoadObject(Scene scene, Object3D data) {
                            Log.i("PolyBoolDemoActivity", "Adding object...");
                            float value = 20f;
                            data.setScale(new float[]{value, value, value});
                            data.setColor(Constants.COLOR_BLUE);
                            data.setLocation(new float[]{0, -0.25f, 0});
                            scene.addObject(data);
                        }

                        @Override
                        public void onLoadScene(Scene scene) {
                            sceneManager.addScene(scene);


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

                        Object3D data2 = new Object3D(vertex);
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

                        Object3D data2 = new Object3D(vertex, index);

                        data2.setDrawMode(GLES20.GL_TRIANGLES);
                        data2.setScale(new float[]{50f,50f,50f});
                        data2.setColor(Constants.COLOR_GREEN);
                        data2.setLocation(new float[]{10,0,0});

                        scene.addObject(data2);*/

                                List<Scene> scenes = sceneManager.getScenes();
                                List<Object3D> objects = new ArrayList<>();
                                for (Scene s : scenes) {
                                    objects.addAll(s.getObjects());
                                }

                                Object3D merge = UnionTri.merge(objects);

                                merge = UnionTri.triangulate(merge);

                                merge.setColor(Constants.COLOR_GREEN);
                                merge.setScale(new float[]{50f, 50f, 50f});
                                merge.setLocation(new float[]{0f, 0f, -50f});

                                Scene mergeScene = new Scene();
                                mergeScene.addObject(merge);

                                sceneManager.addScene(mergeScene);
                                sceneManager.setActiveScene(mergeScene);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            sceneManager.update();
                        }
                    });
            task2.execute();


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading OpenGL view:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
