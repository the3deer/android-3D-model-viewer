package org.andresoviedo.android_3d_model_engine.cloth;

import android.app.Activity;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.BoundingBox;
import org.andresoviedo.android_3d_model_engine.model.Cloth3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.model.SimpleCloth;
import org.andresoviedo.android_3d_model_engine.model.Stick;
import org.andresoviedo.android_3d_model_engine.services.SceneLoader;
import org.andresoviedo.util.android.ContentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class ClothController implements Runnable {

    private static final float[] COLOR_RED = {1.0f, 0.0f, 0.0f, 1f};

    private final List<Cloth3D> cloths = new ArrayList<>();
    private final SceneLoader scene;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Activity parent;

    // testing
    private Cloth3D cloth;

    public ClothController(Activity parent, SceneLoader scene) {
        this.parent = parent;
        this.scene = scene;
    }

    public void start(){
        scheduler.scheduleAtFixedRate(this, 1000, 500, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    public void run() {

        Log.v("ClothController","Running...");
        if (cloth == null){
            // cloth
            ContentUtils.setThreadActivity(parent);
            //loth = (Cloth3D) Cloth3D.build();
            cloth = (Cloth3D) SimpleCloth.build(-40, 55f, -40, 40, 55f, 40, 20f).setColor(COLOR_RED).setId("grid-cloth").setSolid(false);
            //cloth = Cloth.build(-40, 50f, -40, 40, 50f, 40, 5f).setColor(COLOR_RED).setId("grid-cloth").setSolid(false);
            cloths.add(cloth);
            scene.addObject(cloth);
        }

        for (int i=0; i<scene.getObjects().size(); i++){
            if (scene.getObjects().get(i) instanceof Cloth3D) continue;
            cloth.update(scene.getObjects().get(i));
        }
    }

}
