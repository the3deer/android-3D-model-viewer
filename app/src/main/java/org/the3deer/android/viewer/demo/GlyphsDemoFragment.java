package org.the3deer.android.viewer.demo;

import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.ModelEngineViewModel;
import org.the3deer.android.engine.gui.GUI;
import org.the3deer.android.engine.gui.Label;
import org.the3deer.android.engine.gui.Text;
import org.the3deer.android.engine.gui.Widget;
import org.the3deer.android.engine.model.Camera;
import org.the3deer.android.engine.model.Model;
import org.the3deer.android.viewer.ui.home.HomeFragment;
import org.the3deer.util.bean.BeanInit;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class GlyphsDemoFragment extends HomeFragment {

    private final static String TAG = GlyphsDemoFragment.class.getSimpleName();

    @Inject
    private GUI gui;

    @Inject @Named("gui.camera")
    private Camera camera;

    private Text abcd;

    @BeanInit
    public void setUp() {
        Log.i(TAG, "Starting up...");

        ModelEngineViewModel viewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
        @NotNull Model model = viewModel.createModel("demo.glyphs");
        ModelEngine modelEngine = viewModel.loadEngine("demo.glyphs");

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
