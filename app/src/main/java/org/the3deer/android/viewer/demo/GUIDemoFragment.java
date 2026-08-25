package org.the3deer.android.viewer.demo;

import org.jetbrains.annotations.Nullable;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.event.EngineEvent;
import org.the3deer.android.engine.gui.GUI;
import org.the3deer.android.engine.gui.Label;
import org.the3deer.android.engine.model.Camera;
import org.the3deer.android.engine.text.FontManager;
import org.the3deer.android.viewer.ui.home.HomeFragment;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * This activity represents the container for our 3D viewer.
 *
 * @author andresoviedo
 */
public class GUIDemoFragment extends HomeFragment {

    private static final Logger logger = Logger.getLogger(GUIDemoFragment.class.getSimpleName());

    @Inject
    private GUI gui;

    @Inject
    @Named("gui.camera")
    private Camera camera;

    @Inject
    private FontManager fontManager;

    @Inject
    private ModelEngine modelEngine;

    private Label abcd;
    private Label label;

    @Override
    public boolean onEvent(@Nullable EventObject event) {
        if (event instanceof EngineEvent){
            if (modelEngine.getStatus() == ModelEngine.Status.OK){
                logger.info("Starting up...");

                abcd = new Label(null, 10, 6, fontManager.getFactoryObject());
                //abcd.setMargin(Widget.PADDING_01);
                abcd.update("0123456789\n" +
                        "abcdefghij\n" +
                        "klmnopqrst\n" +
                        "uvwxyz\n" +
                        "ABCDEFGHIJ\n" +
                        "KLMNOPQRST\n" +
                        "UVWXYZ");
                gui.addChild(abcd);
                abcd.setScale(new float[]{25f, 25f, 25f});
                abcd.setLocation(new float[]{-200f, 0f, 0f});
                abcd.setVisible(true);

                label = new Label(fontManager.getFactoryObject(), "0123456789\nabcdefghij");
                label.setVisible(true);

                modelEngine.getModel().getActiveScene().addObject(label);
                modelEngine.getModel().getActiveScene().addObject(abcd);

                logger.info("Demo loaded");
            }
        }
        return super.onEvent(event);
    }
}
