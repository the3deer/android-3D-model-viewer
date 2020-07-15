package org.andresoviedo.app.model3D.view;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.gui.CheckList;
import org.andresoviedo.android_3d_model_engine.gui.GUI;
import org.andresoviedo.android_3d_model_engine.gui.Glyph;
import org.andresoviedo.android_3d_model_engine.gui.Menu3D;
import org.andresoviedo.android_3d_model_engine.gui.Rotator;
import org.andresoviedo.android_3d_model_engine.gui.Text;
import org.andresoviedo.android_3d_model_engine.gui.Widget;
import org.andresoviedo.android_3d_model_engine.services.SceneLoader;
import org.andresoviedo.android_3d_model_engine.view.ModelRenderer;
import org.andresoviedo.android_3d_model_engine.view.ModelSurfaceView;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

final class ModelViewerGUI extends GUI {

    private final ModelSurfaceView glView;
    private final SceneLoader scene;

    private Text fps;
    private Widget icon;
    private Glyph icon2 = Glyph.build(Glyph.CHECKBOX_ON);
    private Menu3D menu;
    private Rotator rotator;

    ModelViewerGUI(ModelSurfaceView glView, SceneLoader scene) {
        super();
        this.glView = glView;
        this.scene = scene;
        setColor(new float[]{1, 1, 1, 0f});
    }

    /**
     * @param width screenpixels
     * @param height screen y pixels
     */
    public void setSize(int width, int height) {

        // log event
        Log.i("ModelViewerGUI","New size: "+width+"/"+height);

        super.setSize(width, height);
        try {
            initFPS();
            //initMenu();
            //initMenu2();
        }catch (Exception e){
            Log.e("ModelViewerGUI",e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    private void initFPS() {
        // frame-per-second
        fps = Text.allocate(7, 1);
        fps.setId("fps");
        fps.setVisible(true);
        fps.setParent(this);
        fps.setRelativeScale(new float[]{0.15f,0.15f,0.15f});

        addWidget(fps);

        fps.setPosition(GUI.POSITION_TOP_LEFT);
        //addBackground(fps).setColor(new float[]{0.25f, 0.25f, 0.25f, 0.25f});
    }

    private void initMenu2() {
        // checklist
        CheckList.Builder menuB = new CheckList.Builder();
        menuB.add("lights");
        menuB.add("wireframe");
        menuB.add("textures");
        menuB.add("colors");
        menuB.add("animation");
        menuB.add("stereoscopic");
        menuB.add("------------");
        menuB.add("Load texture");
        menuB.add("------------");
        menuB.add("    Close   ");
        CheckList menu2 = menuB.build();
        menu2.setScale(new float[]{0.1f,0.1f,0.1f});
        menu2.addListener(this);
        menu2.setVisible(true);
        menu2.setParent(icon);
        menu2.setPosition(Widget.POSITION_BOTTOM);
        super.addWidget(menu2);
    }

    private void initMenu() {
        // icon
        icon = Glyph.build(Glyph.MENU);
        icon.setParent(this);
        icon.setRelativeScale(new float[]{0.1f,0.1f,0.1f});
        super.addWidget(icon);
        icon.setPosition(GUI.POSITION_TOP_LEFT);
        icon.setVisible(true);
        //super.addBackground(icon).setColor(new float[]{0.25f, 0.25f, 0.25f, 0.25f});

        // menu
        List<String> options = new ArrayList<>();
        options.add("lights");
        options.add("wireframe");
        options.add("textures");
        options.add("colors");
        options.add("animation");
        options.add("stereoscopic");
        options.add("------------");
        options.add("Load texture");
        options.add("------------");
        options.add("    Close   ");
        menu = Menu3D.build(options.toArray(new String[options.size()]));
        menu.addListener(this);
        menu.setVisible(!icon.isVisible());
        menu.setParent(this);
        menu.setRelativeScale(new float[]{0.5f,0.5f,0.5f});
        //menu.setColor(new float[]{0.25f, 0.25f, 0.25f, 0.25f});
        //icon.addListener(menu);

        super.addWidget(menu);
        menu.setPosition(GUI.POSITION_MIDDLE);
        super.addBackground(menu).setColor(new float[]{0.5f, 0f, 0f, 0.25f});

        // menu rotator
        rotator = Rotator.build(menu);
        rotator.setColor(new float[]{1f,0,0,1});
        rotator.setVisible(menu.isVisible());
        rotator.setLocation(menu.getLocation());
        rotator.setScale(menu.getScale());
        super.addWidget(rotator);
        //super.addBackground(rotator).setColor(new float[]{0.5f, 0.5f, 0f, 0.25f});
    }

    @Override
    public boolean onEvent(EventObject event) {
        super.onEvent(event);
        if (event instanceof ModelRenderer.FPSEvent){
            if (fps.isVisible()) {
                ModelRenderer.FPSEvent fpsEvent = (ModelRenderer.FPSEvent) event;
                fps.update(fpsEvent.getFps() + " fps");
            }
        }
        else if (event instanceof Menu3D.ItemSelected) {
            switch (((Menu3D.ItemSelected) event).getSelected()) {
                case 0:
                    Log.i("ModelViewerGUI","Toggling lights...");
                    glView.toggleLights();
                    menu.setState(0, glView.isLightsEnabled());
                    break;
                case 1:
                    glView.toggleWireframe();
                    break;
                case 2:
                    glView.toggleTextures();
                    break;
                case 3:
                    glView.toggleColors();
                    break;
                case 4:
                    glView.toggleAnimation();
                    break;
                case 9:
                    menu.setVisible(false);
                    break;
            }
        } else if (event instanceof GUI.ClickEvent) {
            GUI.ClickEvent clickEvent = (GUI.ClickEvent) event;
            Widget widget = clickEvent.getWidget();

            Log.i("ModelViewerGUI","Click... widget: "+widget.getId());

            if (widget == icon) {
                menu.toggleVisible();
            }

            if (widget ==  icon2){
                if (icon2.getCode() == Glyph.CHECKBOX_ON) {
                    icon2.setCode(Glyph.CHECKBOX_OFF);
                } else {
                    icon2.setCode(Glyph.CHECKBOX_ON);
                }
            }

            if (widget == menu){
                //menu.onEvent(event);
            }

            if (widget == rotator){
                //rotator.onEvent(clickEvent);
            }
        } else if (event instanceof MoveEvent) {
            float dx = ((MoveEvent) event).getDx();
            float dy = ((MoveEvent) event).getDy();
            float[] newPosition = ((MoveEvent) event).getWidget().getLocation().clone();
            newPosition[1]+=dy;
            ((MoveEvent) event).getWidget().setLocation(newPosition);
        }
        return true;
    }
}
