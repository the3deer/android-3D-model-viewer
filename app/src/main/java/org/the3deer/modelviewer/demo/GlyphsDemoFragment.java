package org.the3deer.modelviewer.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.android_3d_model_engine.gui.GUIDefault;
import org.the3deer.android_3d_model_engine.gui.Label;
import org.the3deer.android_3d_model_engine.gui.Text;
import org.the3deer.android_3d_model_engine.gui.Widget;
import org.the3deer.android_3d_model_engine.model.Camera;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class GlyphsDemoFragment extends ModelFragment {

    private final static String TAG = GlyphsDemoFragment.class.getSimpleName();

    private GUIDefault gui;
    private Camera camera;

    private Text abcd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler.post(this::setUp);
    }

    private void setUp() {
        Log.i(TAG, "Starting up...");

        gui = modelEngine.getBeanFactory().find(GUIDefault.class);
        //camera = modelEngine.getBeanFactory().get("80.gui.camera", Camera.class);
        //camera.getPos()[2]=0;


        abcd = Text.allocate(null,10, 6);
        abcd.setMargin(Widget.PADDING_01);
        abcd.update("0123456789\n" +
                "abcdefghij\n" +
                "klmnopqrst\n" +
                "uvwxyz\n" +
                "ABCDEFGHIJ\n" +
                "KLMNOPQRST\n" +
                "UVWXYZ");
        gui.addChild(abcd);

        abcd.setRelativeScale(new float[]{0.25f,0.25f,0.25f});
        abcd.setRelativeLocation(Widget.POSITION_MIDDLE);
        abcd.setVisible(true);

        Label label = new Label("0123456789\nabcdefghij");
        label.setVisible(true);
        gui.addChild(label);

        label.setRelativeScale(new float[]{1.0f, 1.0f, 1.0f});
        label.setRelativeLocation(Widget.POSITION_BOTTOM);
    }
}
