package org.the3deer.app.model3D.demo;

import android.os.Bundle;

import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.android_3d_model_engine.gui.GUIDefault;
import org.the3deer.android_3d_model_engine.gui.Text;
import org.the3deer.android_3d_model_engine.gui.Widget;
import org.the3deer.android_3d_model_engine.model.Camera;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class GlyphsDemoFragment extends ModelFragment {

    private GUIDefault scene;
    private Camera camera;

    private Text abcd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scene = modelEngine.getBeanFactory().find(GUIDefault.class);
        camera = modelEngine.getBeanFactory().get("gui.camera", Camera.class);
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
        abcd.setRelativeScale(new float[]{0.5f,0.5f,0.5f});
        abcd.setRelativeLocation(Widget.POSITION_MIDDLE);
        abcd.setVisible(true);
        scene.addChild(abcd);
    }
}
