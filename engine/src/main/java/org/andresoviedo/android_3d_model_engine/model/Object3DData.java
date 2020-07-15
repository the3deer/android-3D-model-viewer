package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.collision.Octree;
import org.andresoviedo.android_3d_model_engine.drawer.RendererFactory;
import org.andresoviedo.util.android.AndroidUtils;
import org.andresoviedo.util.event.EventListener;
import org.andresoviedo.util.io.IOUtils;

import java.net.URI;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;

/**
 * This is the basic 3D data necessary to build the 3D object
 *
 * @author andresoviedo
 */
public class Object3DData {


    protected static class ChangeEvent extends EventObject {
        ChangeEvent(Object source) {
            super(source);
        }
    }

    /**
     * Parent object if hierarchy of objects
     */
    protected Object3DData parent;
    /**
     * model resource locator
     */
    private URI uri;
    /**
     * model id
     */
    private String id;
    /**
     * model friendly name or joint name
     */
    private String name;
    /**
     * Whether to draw object using indices or not
     */
    private boolean drawUsingArrays = false;
    /**
     * Whether the object is to be drawn
     */
    private boolean isVisible = true;

    private boolean isSolid = true;
    /**
     * The minimum thing we can draw in space is a vertex (or point).
     * This drawing mode uses the vertexBuffer
     */
    private int drawMode = GLES20.GL_POINTS;

    // Model data
    protected FloatBuffer vertexBuffer = null;
    private FloatBuffer normalsBuffer = null;
    private FloatBuffer colorsBuffer = null;
    private FloatBuffer textureBuffer = null;
    protected List<Element> elements;
    /**
     * Object materials
     */
    private Materials materials;

    // simple object variables for drawing using arrays
    private Material material = new Material("default");
    private IntBuffer indexBuffer = null;
    private ShortBuffer indexShortBuffer = null;  // in case system doesn't support ints

    // Processed arrays
    private List<int[]> drawModeList = null;

    // derived data
    private BoundingBox boundingBox;

    // Transformation data
    protected float[] scale = new float[]{1, 1, 1};
    protected float[] rotation = new float[]{0f, 0f, 0f};
    protected float[] location = new float[]{0f, 0f, 0f};

    // extra transforms
    private float[] rotation1 = null;
    private float[] rotation2 = null;
    private float[] rotation2Location = null;

    /**
     * This is the local transformation
     */
    private final float[] modelMatrix = new float[16];
    /**
     * This is the local transformation when we have node hierarchy (ie. {@code <visual_scene><node><transform></transform></node></visual_scene>}
     */
    private float[] bindTransform;
    /**
     * This is the final model transformation
     */
    private final float[] newModelMatrix = new float[16];

    {
        //
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(newModelMatrix, 0);
    }

    // whether the object has changed
    private boolean changed;

    // Async Loader
    // current dimensions
    private Dimensions dimensions = null;
    protected Dimensions currentDimensions = null;

    // collision detection
    private Octree octree = null;

    // errors detected
    private List<String> errors = new ArrayList<>();

    // event listeners
    private List<EventListener> listeners = new ArrayList<>();


    public Object3DData() {
    }

    public Object3DData(FloatBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
        this.setDrawUsingArrays(true);
        updateModelDimensions();
    }

    public Object3DData(FloatBuffer vertexBuffer, IntBuffer drawOrder) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = drawOrder;
        this.setDrawUsingArrays(false);
        updateModelDimensions();
    }

    public Object3DData(FloatBuffer vertexBuffer, FloatBuffer textureBuffer, byte[] texData) {
        this.vertexBuffer = vertexBuffer;
        this.textureBuffer = textureBuffer;
        this.getMaterial().setTextureData(texData);
        this.setDrawUsingArrays(true);
        updateModelDimensions();
    }

    public Object3DData(FloatBuffer vertexBuffer, FloatBuffer colorsBuffer,
                        FloatBuffer textureBuffer, byte[] texData) {
        this.vertexBuffer = vertexBuffer;
        this.colorsBuffer = colorsBuffer;
        this.textureBuffer = textureBuffer;
        this.getMaterial().setTextureData(texData);
        this.setDrawUsingArrays(true);
        updateModelDimensions();
    }

    public Object3DData(FloatBuffer verts, FloatBuffer normals,
                        Materials materials) {
        super();
        this.vertexBuffer = verts;
        this.normalsBuffer = normals;
        this.materials = materials;
        this.setDrawUsingArrays(false);
        this.updateModelDimensions();
    }

    public Object3DData getParent() {
        return parent;
    }

    public void setParent(Object3DData parent) {
        this.parent = parent;
    }

    public Object3DData setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return this.uri;
    }

    public boolean isDrawUsingArrays() {
        return drawUsingArrays;
    }

    public Object3DData setDrawUsingArrays(boolean drawUsingArrays) {
        this.drawUsingArrays = drawUsingArrays;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Materials getMaterials() {
        return materials;
    }


    public Object3DData setSolid(boolean solid) {
        isSolid = solid;
        return this;
    }

    public boolean isSolid() {
        return isSolid;
    }

    // ---------------------------- dimensions ----------------------------- //

    public float getWidth() {
        return getCurrentDimensions().getWidth() * getScaleX();
    }

    public float getHeight() {
        return getCurrentDimensions().getHeight() * getScaleY();
    }

    public float getDepth() {
        return getCurrentDimensions().getDepth() * getScaleZ();
    }

    public void setCurrentDimensions(Dimensions currentDimensions) {
        this.currentDimensions = currentDimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
        setCurrentDimensions(dimensions);
        Log.d("Object3DData", "New fixed dimensions for " + getId() + ": " + this.dimensions);
    }

    public Dimensions getDimensions() {
        if (dimensions == null) {

            final Dimensions dimensions = new Dimensions();

            if (this.elements == null || this.elements.isEmpty()) {
                for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
                    dimensions.update(vertexBuffer.get(i), vertexBuffer.get(i + 1), vertexBuffer.get(i + 2));
                }
            } else {
                for (Element element : getElements()) {
                    final IntBuffer indexBuffer = element.getIndexBuffer();
                    for (int i = 0; i < indexBuffer.capacity(); i++) {
                        final int idx = indexBuffer.get(i);
                        dimensions.update(vertexBuffer.get(idx * 3), vertexBuffer.get(idx * 3 + 1), vertexBuffer.get(idx * 3 + 2));
                    }
                }
            }
            this.dimensions = dimensions;

            Log.d("Object3DData", "New dimensions for '" + getId() + "': " + this.dimensions);
        }
        return dimensions;
    }

    public Dimensions getCurrentDimensions() {
        if (this.currentDimensions == null) {
            final float[] location = new float[4];
            final float[] ret = new float[4];

            final Dimensions newDimensions = new Dimensions();

            Log.i("Object3DData", "id:" + getId() + ", elements:" + elements);
            if (this.elements == null || this.elements.isEmpty()) {
                for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
                    location[0] = vertexBuffer.get(i);
                    location[1] = vertexBuffer.get(i + 1);
                    location[2] = vertexBuffer.get(i + 2);
                    location[3] = 1;
                    Matrix.multiplyMV(ret, 0, this.getModelMatrix(), 0, location, 0);
                    newDimensions.update(ret[0], ret[1], ret[2]);
                }
            } else {
                for (Element element : getElements()) {
                    final IntBuffer indexBuffer = element.getIndexBuffer();
                    for (int i = 0; i < indexBuffer.capacity(); i++) {
                        final int idx = indexBuffer.get(i);
                        location[0] = vertexBuffer.get(idx * 3);
                        location[1] = vertexBuffer.get(idx * 3 + 1);
                        location[2] = vertexBuffer.get(idx * 3 + 2);
                        location[3] = 1;
                        Matrix.multiplyMV(ret, 0, this.getModelMatrix(), 0, location, 0);
                        newDimensions.update(ret[0], ret[1], ret[2]);
                    }
                }
            }
            this.currentDimensions = newDimensions;

            Log.d("Object3DData", "Calculated current dimensions for '" + getId() + "': " + this.currentDimensions);
        }
        return currentDimensions;
    }

    public void setOctree(Octree octree) {
        this.octree = octree;
    }

    public Octree getOctree() {
        return octree;
    }

    public void addListener(EventListener listener) {
        Log.d("Object3DData", "Listener for " + getId() + " --> " + listener);
        this.listeners.add(listener);
    }

    protected void fireEvent(EventObject event) {
        AndroidUtils.fireEvent(listeners, event);
    }

    public void render(RendererFactory drawer, float[] lightPosInWorldSpace, float[] colorMask) {
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Object3DData hide() {
        return this.setVisible(false);
    }

    public Object3DData show() {
        return this.setVisible(true);
    }

    public Object3DData setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        fireEvent(new ChangeEvent(this));
        return this;
    }

    public void toggleVisible() {
        setVisible(!this.isVisible());
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public float[] getColor() {
        return getMaterial().getColor();
    }

    public Object3DData setColor(float[] color) {

        // set color only if valid data
        if (color != null) {

            // assert
            if (color.length != 4) {
                throw new IllegalArgumentException("color should be RGBA");
            }

            // color variable when using single color
            this.getMaterial().setDiffuse(color);
            this.getMaterial().setAlpha(color[3]);
        }
        return this;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public Object3DData setDrawMode(int drawMode) {
        this.drawMode = drawMode;
        return this;
    }

    public int getDrawSize() {
        return 0;
    }

    // -----------

    public byte[] getTextureData() {
        return getMaterial().getTextureData();
    }

    public void setTextureData(byte[] textureData) {
        Log.i("Object3DData","New texture: "+textureData.length+" (bytes)");
        this.getMaterial().setTextureData(textureData);
        if (this.getElements() != null && this.getElements().size() == 1){
            // TODO: let user pick object and/or element to update texture
            // as for now, let's just update 1st element
            for (int i=0; i<1; i++) {
                if (getElements().get(i).getMaterial() == null) continue;
                if (getElements().get(i).getMaterial().getTextureData() == null) continue;
                this.getElements().get(i).getMaterial().setTextureData(textureData);
                Log.i("Object3DData","New texture for element ("+i+"): "+getElements().get(i).getMaterial());
            }
        }
    }

    public Object3DData setLocation(float[] location) {
        this.location = location;
        this.updateModelMatrix();
        this.updateModelDimensions();
        this.changed = true;
        return this;
    }

    public void translate(float[] translation) {
        this.location[0] += translation[0];
        this.location[1] += translation[1];
        this.location[2] += translation[2];
        this.updateModelMatrix();
        this.updateModelDimensions();
        this.changed = true;
    }

    public float[] getLocation() {
        return location;
    }

    public float getLocationX() {
        return location != null ? location[0] : 0;
    }

    public float getLocationY() {
        return location != null ? location[1] : 0;
    }

    public float getLocationZ() {
        return location != null ? location[2] : 0;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float getRotationZ() {
        return rotation[2];
    }

    public Object3DData setScale(float[] scale) {
        this.scale = scale;
        updateModelMatrix();
        updateModelDimensions();
        this.changed = true;
        return this;
    }

    public Object3DData setScale(float x, float y, float z) {
        return this.setScale(new float[]{x, y, z});
    }

    public float[] getScale() {
        return scale;
    }

    public float getScaleX() {
        return getScale()[0];
    }

    public float getScaleY() {
        return getScale()[1];
    }

    public float getScaleZ() {
        return getScale()[2];
    }

    public Object3DData setRotation(float[] rotation) {
        this.rotation = rotation;
        updateModelMatrix();
        return this;
    }

    public Object3DData setRotation1(float[] rotation) {
        this.rotation1 = rotation;
        updateModelMatrix();
        return this;
    }

    public Object3DData setRotation2(float[] rotation2, float[] rotation2Location) {
        this.rotation2 = rotation2;
        this.rotation2Location = rotation2Location;
        updateModelMatrix();
        return this;
    }

    public float[] getRotation2() {
        return rotation2;
    }

    // binding coming from skeleton
    public Object3DData setBindTransform(float[] matrix) {
        this.bindTransform = matrix;
        this.updateModelMatrix();
        this.updateModelDimensions();
        return this;
    }

    /**
     * This is the bind shape transform found in sking (ie. {@code <library_controllers><skin><bind_shape_matrix>}
     */
    public void setBindShapeMatrix(float[] matrix) {
        if (matrix == null) return;

        float[] vertex = new float[]{0, 0, 0, 1};
        float[] shaped = new float[]{0, 0, 0, 1};
        for (int i = 0; i < this.vertexBuffer.capacity(); i += 3) {
            vertex[0] = this.vertexBuffer.get(i);
            vertex[1] = this.vertexBuffer.get(i + 1);
            vertex[2] = this.vertexBuffer.get(i + 2);
            Matrix.multiplyMV(shaped, 0, matrix, 0, vertex, 0);
            this.vertexBuffer.put(i, shaped[0]);
            this.vertexBuffer.put(i + 1, shaped[1]);
            this.vertexBuffer.put(i + 2, shaped[2]);
        }
        updateModelDimensions();
    }

    public float[] getBindTransform() {
        return bindTransform;
    }

    private void updateModelMatrix() {

        Matrix.setIdentityM(modelMatrix, 0);

        if (rotation1 != null) {
            //Matrix.rotateM(modelMatrix, 0, rotation1[0], 1f, 0f, 0f);
            Matrix.rotateM(modelMatrix, 0, rotation1[1], 0, 1f, 0f);
            //Matrix.rotateM(modelMatrix, 0, rotation1[2], 0, 0, 1f);
        }

        if (getLocation() != null) {
            Matrix.translateM(modelMatrix, 0, getLocationX(), getLocationY(), getLocationZ());
        }

        if (rotation2 != null && rotation2Location != null) {
            Matrix.translateM(modelMatrix, 0, rotation2Location[0], rotation2Location[1], rotation2Location[2]);
            Matrix.rotateM(modelMatrix, 0, getRotation2()[0], 1f, 0f, 0f);
            Matrix.rotateM(modelMatrix, 0, getRotation2()[1], 0, 1f, 0f);
            Matrix.rotateM(modelMatrix, 0, getRotation2()[2], 0, 0, 1f);
            Matrix.translateM(modelMatrix, 0, -rotation2Location[0], -rotation2Location[1], -rotation2Location[2]);
        }
        if (getRotation() != null) {
            Matrix.rotateM(modelMatrix, 0, getRotation()[0], 1f, 0f, 0f);
            Matrix.rotateM(modelMatrix, 0, getRotation()[1], 0, 1f, 0f);
            Matrix.rotateM(modelMatrix, 0, getRotationZ(), 0, 0, 1f);
        }

        if (getScale() != null) {
            Matrix.scaleM(modelMatrix, 0, getScaleX(), getScaleY(), getScaleZ());
        }

        if (this.bindTransform == null) {
            // geometries not linked to any joint does not have bind transform
            System.arraycopy(this.modelMatrix, 0, this.newModelMatrix, 0, 16);
        } else {
            Matrix.multiplyMM(newModelMatrix, 0, this.modelMatrix, 0, this.bindTransform, 0);
        }

        fireEvent(new ChangeEvent(this));
    }

    public float[] getModelMatrix() {
        return newModelMatrix;
    }

    public Transform getTransform(){
        return new Transform(this.scale, this.rotation, this.location);
    }

    public IntBuffer getDrawOrder() {
        return indexBuffer;
    }

    /**
     * In case OpenGL doesn't support using GL_UNSIGNED_INT for glDrawElements(), then use this buffer
     *
     * @return the draw buffer as short
     */
    public ShortBuffer getDrawOrderAsShort() {
        if (indexShortBuffer == null && indexBuffer != null) {
            indexShortBuffer = IOUtils.createShortBuffer(indexBuffer.capacity());
            for (int i = 0; i < indexBuffer.capacity(); i++) {
                indexShortBuffer.put((short) indexBuffer.get(i));
            }
        }
        return indexShortBuffer;
    }

    public Object3DData setDrawOrder(IntBuffer drawBuffer) {
        this.indexBuffer = drawBuffer;
        return this;
    }


    // -------------------- Buffers ---------------------- //

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public Object3DData setVertexBuffer(FloatBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
        updateModelDimensions();
        return this;
    }

    public FloatBuffer getNormalsBuffer() {
        return normalsBuffer;
    }

    public Object3DData setNormalsBuffer(FloatBuffer normalsBuffer) {
        this.normalsBuffer = normalsBuffer;
        return this;
    }

    public FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    public Object3DData setTextureBuffer(FloatBuffer textureBuffer) {
        this.textureBuffer = textureBuffer;
        return this;
    }

    public List<int[]> getDrawModeList() {
        return drawModeList;
    }

    public Object3DData setDrawModeList(List<int[]> drawModeList) {
        this.drawModeList = drawModeList;
        return this;
    }

    public FloatBuffer getColorsBuffer() {
        return colorsBuffer;
    }

    public Object3DData setColorsBuffer(FloatBuffer colorsBuffer) {
        if (colorsBuffer != null && colorsBuffer.capacity() % 4 != 0)
            throw new IllegalArgumentException("Color buffer not multiple of 4 floats");
        this.colorsBuffer = colorsBuffer;
        return this;
    }

    protected void updateModelDimensions() {
        // FIXME: this breaks GUI
        //this.currentDimensions = null;

        /*final float[] location = new float[4];
        final float[] ret = new float[4];

        final Dimensions dimensions = new Dimensions();
        final Dimensions currentDimensions = new Dimensions();

        if (this.elements == null || this.elements.isEmpty()){
            for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
                if (getBindTransform() != null) {
                    location[0] = vertexBuffer.get(i);
                    location[1] = vertexBuffer.get(i + 1);
                    location[2] = vertexBuffer.get(i + 2);
                    location[3] = 1;
                    Matrix.multiplyMV(ret, 0, this.getModelMatrix(), 0, location, 0);
                    currentDimensions.update(ret[0], ret[1], ret[2]);
                } else {
                    currentDimensions.update(vertexBuffer.get(i), vertexBuffer.get(i + 1), vertexBuffer.get(i + 2));
                }
                dimensions.update(vertexBuffer.get(i), vertexBuffer.get(i + 1), vertexBuffer.get(i + 2));
            }
        }
        else {
            for (Element element : getElements()) {
                final IntBuffer indexBuffer = element.getIndexBuffer();
                for (int i = 0; i < indexBuffer.capacity(); i++) {
                    final int idx = indexBuffer.get(i);
                    location[0] = vertexBuffer.get(idx * 3);
                    location[1] = vertexBuffer.get(idx * 3 + 1);
                    location[2] = vertexBuffer.get(idx * 3 + 2);
                    location[3] = 1;
                    Matrix.multiplyMV(ret, 0, this.getModelMatrix(), 0, location, 0);
                    currentDimensions.update(ret[0], ret[1], ret[2]);

                    dimensions.update(location[0], location[1], location[2]);
                }
            }
        }
        this.dimensions = dimensions;
        this.currentDimensions = currentDimensions;

        Log.d("Object3DData","New dimensions for '"+getId()+"': "+this.dimensions+", real: "+this.currentDimensions);*/
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.create(getId() + "_BoundingBox1", getDimensions(), getModelMatrix());
    }

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public Object3DData setElements(List<Element> elements) {
        this.elements = elements;
        this.updateModelDimensions();
        return this;
    }

    /**
     * @return either list of elements (when using indices), otherwise <code>null</code>
     */
    public List<Element> getElements() {
        if (elements == null && getDrawOrder() != null) {
            Element element = new Element(getId(), getDrawOrder(), null);
            element.setMaterial(this.getMaterial());
            elements = Collections.singletonList(element);
        }
        return elements;
    }

    public float[] getRotation2Location() {
        return rotation2Location;
    }

    @Override
    public Object3DData clone() {
        Object3DData ret = new Object3DData();
        copy(ret);
        return ret;
    }

    void copy(Object3DData ret) {
        ret.setId(this.getId());
        ret.setLocation(this.getLocation().clone());
        ret.setScale(this.getScale().clone());
        ret.setRotation(this.getRotation().clone());
        ret.setCurrentDimensions(this.getCurrentDimensions());
        ret.setVertexBuffer(this.getVertexBuffer());
        ret.setNormalsBuffer(this.getNormalsBuffer());
        ret.setColorsBuffer(this.getColorsBuffer());
        ret.setTextureBuffer(this.getTextureBuffer());
        ret.setMaterial(this.getMaterial());
        ret.setElements(this.getElements());
        ret.setDrawMode(this.getDrawMode());
        ret.setDrawUsingArrays(this.isDrawUsingArrays());
    }

    @Override
    public String toString() {
        return "Object3DData{" +
                "id='" + id + "'" +
                ", name=" + getName() +
                ", isVisible=" + isVisible +
                ", color=" + Arrays.toString(getMaterial().getColor()) +
                ", position=" + Arrays.toString(location) +
                ", scale=" + Arrays.toString(scale) +
                ", indexed=" + !isDrawUsingArrays() +
                ", vertices: " + (vertexBuffer != null ? vertexBuffer.capacity() / 3 : 0) +
                ", normals: " + (normalsBuffer != null ? normalsBuffer.capacity() / 3 : 0) +
                ", dimensions: " + this.dimensions +
                ", current dimensions: " + this.currentDimensions +
                ", material=" + getMaterial() +
                ", elements=" + getElements() +
                '}';
    }
}
