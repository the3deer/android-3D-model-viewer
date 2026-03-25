package org.the3deer.android.viewer.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.gui.GUI;
import org.the3deer.android.engine.gui.Label;
import org.the3deer.android.engine.gui.Text;
import org.the3deer.android.engine.gui.Widget;
import org.the3deer.android.engine.model.Camera;
import org.the3deer.android.engine.model.Model;
import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.android.viewer.ui.home.HomeFragment;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class GlyphsDemoFragment extends HomeFragment {

    private final static String TAG = GlyphsDemoFragment.class.getSimpleName();

    private GUI gui;
    private Camera camera;

    private Text abcd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getHandler().post(this::setUp);
    }

    private void setUp() {
        Log.i(TAG, "Starting up...");

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        @NotNull Model model = sharedViewModel.createModel("demo.glyphs");
        ModelEngine modelEngine = sharedViewModel.loadEngine("demo.glyphs", model, requireActivity());

        gui = modelEngine.getBeanFactory().find(GUI.class);
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
