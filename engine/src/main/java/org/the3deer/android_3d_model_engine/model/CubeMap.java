package org.the3deer.android_3d_model_engine.model;

/**
 * Cube with six textures used to implement the SkyBox
 */
public final class CubeMap {

    private final byte[] posX;
    private final byte[] negX;
    private final byte[] posY;
    private final byte[] negY;
    private final byte[] posZ;
    private final byte[] negZ;

    private int textureId = -1;

    public CubeMap(byte[] posX, byte[] negX, byte[] posY, byte[] negY, byte[] posZ, byte[] negZ) {
        this.posX = posX;
        this.negX = negX;
        this.posY = posY;
        this.negY = negY;
        this.posZ = posZ;
        this.negZ = negZ;
    }

    public byte[] getNegx() {
        return negX;
    }

    public byte[] getNegy() {
        return negY;
    }

    public byte[] getNegz() {
        return negZ;
    }

    public byte[] getPoxx() {
        return posX;
    }

    public byte[] getPoxy() {
        return posY;
    }

    public byte[] getPoxz() {
        return posZ;
    }

    public int getTextureId() {
        return textureId;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
}
