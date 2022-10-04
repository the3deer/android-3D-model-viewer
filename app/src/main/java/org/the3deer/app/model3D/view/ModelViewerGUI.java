package org.the3deer.app.model3D.view;

import android.opengl.Matrix;
import android.util.Log;

import org.the3deer.android_3d_model_engine.drawer.RendererFactory;
import org.the3deer.android_3d_model_engine.event.SelectedObjectEvent;
import org.the3deer.android_3d_model_engine.gui.CheckList;
import org.the3deer.android_3d_model_engine.gui.GUI;
import org.the3deer.android_3d_model_engine.gui.Glyph;
import org.the3deer.android_3d_model_engine.gui.Menu3D;
import org.the3deer.android_3d_model_engine.gui.Rotator;
import org.the3deer.android_3d_model_engine.gui.Text;
import org.the3deer.android_3d_model_engine.gui.Widget;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.objects.Axis;
import org.the3deer.android_3d_model_engine.services.SceneLoader;
import org.the3deer.android_3d_model_engine.view.FPSEvent;
import org.the3deer.android_3d_model_engine.view.ModelSurfaceView;
import org.the3deer.util.math.Quaternion;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;

final class ModelViewerGUI extends GUI {

    private final ModelSurfaceView glView;
    private final SceneLoader scene;

    private Text fps;
    private Text info;
    private Widget axis;
    private Widget icon;
    private Glyph icon2 = Glyph.build(Glyph.CHECKBOX_ON);
    private Menu3D menu;
    private Rotator rotator;

    ModelViewerGUI(ModelSurfaceView glView, SceneLoader scene) {
        super();
        this.glView = glView;
        this.scene = scene;
        setColor(new float[]{1, 1, 1, 0f});
        setPadding(Widget.PADDING_01);
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
            initInfo();
            initAxis();
            //initMenu();
            //initMenu2();
        }catch (Exception e){
            Log.e("ModelViewerGUI",e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    private void initFPS() {
        // frame-per-second
        if (fps != null) return;
        fps = Text.allocate(7, 1);
        fps.setId("fps");
        fps.setVisible(true);
        fps.setParent(this);
        fps.setRelativeScale(new float[]{0.15f,0.15f,0.15f});

        addWidget(fps);

        fps.setPosition(Widget.POSITION_TOP_LEFT);
        //addBackground(fps).setColor(new float[]{0.25f, 0.25f, 0.25f, 0.25f});
    }

    private void initInfo() {
        // model info
        if (info != null) return;
        info = Text.allocate(15, 3, Text.PADDING_01);
        info.setId("info");
        info.setVisible(true);
        info.setParent(this);
        //info.setRelativeScale(new float[]{0.85f,0.85f,0.85f});
        info.setRelativeScale(new float[]{0.25f,0.25f,0.25f});

        addWidget(info);

        info.setPosition(Widget.POSITION_BOTTOM);
        //addBackground(fps).setColor(new float[]{0.25f, 0.25f, 0.25f, 0.25f});
    }

    private void initAxis(){
        if (axis != null) return;
        axis = new Widget(Axis.build()){
            final float[] matrix = new float[16];
            final Quaternion orientation = new Quaternion(matrix);
            @Override
            public void render(RendererFactory rendererFactory, Camera camera, float[] lightPosInWorldSpace, float[] colorMask) {
                if (camera.hasChanged()){
                    Matrix.setLookAtM(matrix,0,camera.getxPos(), camera.getyPos(), camera.getzPos(),
                            0f,0f,0f, camera.getxUp(), camera.getyUp(), camera.getzUp());
                    setOrientation(orientation);
                }
                super.render(rendererFactory, camera, lightPosInWorldSpace, colorMask);
            }
        };
        axis.setId("gui_axis");
        axis.setVisible(true);
        axis.setParent(this);
        axis.setRelativeScale(new float[]{0.1f,0.1f,0.1f});

        addWidget(axis);

        axis.setPosition(Widget.POSITION_TOP_RIGHT);
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
        icon.setPosition(Widget.POSITION_TOP_LEFT);
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
        menu.setPosition(Widget.POSITION_MIDDLE);
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
        if (event instanceof FPSEvent){
            if (fps.isVisible()) {
                FPSEvent fpsEvent = (FPSEvent) event;
                fps.update(fpsEvent.getFps() + " fps");
            }
        }
        else if (event instanceof SelectedObjectEvent){
            if (this.info.isVisible()){
                final Object3DData selected = ((SelectedObjectEvent) event).getSelected();
                final StringBuilder info = new StringBuilder();
                if (selected != null) {
                    if (selected.getId().indexOf('/') == -1){
                        info.append(selected.getId());
                    } else {
                        info.append(selected.getId().substring(selected.getId().lastIndexOf('/')+1));
                    }
                    info.append('\n');
                    info.append("size: ");
                    info.append(String.format(Locale.getDefault(), "%.2f",selected.getDimensions().getLargest()));
                    info.append('\n');
                    info.append("scale: ");
                    info.append(String.format(Locale.getDefault(), "%.2f",selected.getScaleX()));
                    //final DecimalFormat df = new DecimalFormat("0.##");
                    //info.append(df.format(selected.getScaleX()));
                    info.append("x");
                }
                Log.v("ModelViewerGUI","Selected object info: "+info);
                this.info.update(info.toString().toLowerCase());
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
