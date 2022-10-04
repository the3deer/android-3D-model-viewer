package org.the3deer.android_3d_model_engine.objects;

import android.opengl.GLES20;
import android.util.Log;

import org.the3deer.android_3d_model_engine.model.CubeMap;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.io.IOUtils;

import java.io.IOException;
import java.net.URI;

/**
 * Skyboxes downloaded from:
 * <p>
 * https://learnopengl.com/Advanced-OpenGL/Cubemaps
 * https://github.com/mobialia/jmini3d
 */
public class SkyBox {

    private final static float VERTEX_DATA[] = {
            // positions
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,

            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f
    };

    public final URI[] images;

    private CubeMap cubeMap = null;

    public SkyBox(URI[] images) throws IOException {
        if (images == null || images.length != 6)
            throw new IllegalArgumentException("skybox must contain exactly 6 faces");
        this.images = images;
        this.cubeMap = getCubeMap();
    }

    public CubeMap getCubeMap() throws IOException {
        if (cubeMap != null) {
            return cubeMap;
        }

        cubeMap = new CubeMap(
                IOUtils.read(ContentUtils.getInputStream(images[0])),
                IOUtils.read(ContentUtils.getInputStream(images[1])),
                IOUtils.read(ContentUtils.getInputStream(images[2])),
                IOUtils.read(ContentUtils.getInputStream(images[3])),
                IOUtils.read(ContentUtils.getInputStream(images[4])),
                IOUtils.read(ContentUtils.getInputStream(images[5])));

        return cubeMap;
    }

    /**
     * skybox downloaded from https://github.com/mobialia/jmini3d
     *
     * @return
     */
    public static SkyBox getSkyBox2() {
        try {
            return new SkyBox(new URI[]{
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/posx.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/negx.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/posy.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/negy.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/posz.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/negz.png")});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * skybox downloaded from https://learnopengl.com/Advanced-OpenGL/Cubemaps
     *
     * @return
     */
    public static SkyBox getSkyBox1() {
        try {
            return new SkyBox(new URI[]{
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/right.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/left.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/top.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/bottom.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/front.png"),
                    URI.create("android://org.the3deer.dddmodel2/res/drawable/back.png")});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object3DData build(SkyBox skyBox) throws IOException {

        Object3DData ret = new Object3DData(IOUtils.createFloatBuffer(VERTEX_DATA.length).put(VERTEX_DATA)).setId("skybox");
        ret.setDrawMode(GLES20.GL_TRIANGLES);
        ret.getMaterial().setTextureId(skyBox.getCubeMap().getTextureId());


        Log.i("SkyBox", "Skybox : " + ret.getDimensions());

        return ret;
    }
}