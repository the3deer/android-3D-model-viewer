package org.the3deer.android.viewer.demo;

import org.jetbrains.annotations.Nullable;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.event.EngineEvent;
import org.the3deer.android.engine.model.Object3D;
import org.the3deer.android.engine.text.FontManager;
import org.the3deer.android.engine.text.Text;
import org.the3deer.android.viewer.ui.home.HomeFragment;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.inject.Inject;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class TextDemoFragment extends HomeFragment {

    private static final Logger logger = Logger.getLogger(TextDemoFragment.class.getSimpleName());

    @Inject
    private FontManager fontManager;

    @Inject
    private ModelEngine modelEngine;

    private Object3D abc;
    private Object3D label;

    @Override
    public boolean onEvent(@Nullable EventObject event) {
        if (event instanceof EngineEvent){
            if (modelEngine.getStatus() == ModelEngine.Status.OK){
                logger.info("Starting up...");

                abc = Text.build("0123456789\n" +
                        "abcdefghij\n" +
                        "klmnopqrst\n" +
                        "uvwxyz\n" +
                        "ABCDEFGHIJ\n" +
                        "KLMNOPQRST\n" +
                        "UVWXYZ" , fontManager.getFactoryObject());
                //abcd.setMargin(Widget.PADDING_01);
                //abc.setScale(new float[]{10f, 10f, 10f});
                //abc.setLocation(new float[]{-200f, 0f, 0f});
                abc.setVisible(true);
                modelEngine.getModel().getActiveScene().addObject(abc);

                /*Object3D welcome3d = Text3D.build("TEXT 3D", 2.0f, fontManager.getFactoryObject());
                welcome3d.setLocation(0, 50, 0);
                welcome3d.setVisible(true);
                modelEngine.getModel().getActiveScene().addObject(welcome3d);*/

                logger.info("Demo loaded");
            }
        }
        return super.onEvent(event);
    }
}
