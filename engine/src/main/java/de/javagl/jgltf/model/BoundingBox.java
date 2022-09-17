/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model;

import java.util.Objects;

/**
 * A very simple (package-private!) bounding box implementation
 */
class BoundingBox
{
    /**
     * The minimum x coordinate
     */
    private float minX;
    
    /**
     * The minimum y coordinate
     */
    private float minY;
    
    /**
     * The minimum z coordinate
     */
    private float minZ;
    
    /**
     * The maximum x coordinate
     */
    private float maxX;
    
    /**
     * The maximum y coordinate
     */
    private float maxY;
    
    /**
     * The maximum z coordinate
     */
    private float maxZ;

    /**
     * Creates a bounding box  
     */
    BoundingBox()
    {
        minX = Float.MAX_VALUE;
        minY = Float.MAX_VALUE;
        minZ = Float.MAX_VALUE;
        maxX = -Float.MAX_VALUE;
        maxY = -Float.MAX_VALUE;
        maxZ = -Float.MAX_VALUE;
    }
    
    /**
     * Combine this bounding box with the given point
     * 
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param z The z-coordinate
     */
    void combine(float x, float y, float z)
    {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        minZ = Math.min(minZ, z);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        maxZ = Math.max(maxZ, z);
    }

    /**
     * Combine this bounding box with the given one
     * 
     * @param other The other bounding box
     */
    void combine(BoundingBox other)
    {
        Objects.requireNonNull(other, "The other bounding box may not be null");
        minX = Math.min(minX, other.getMinX());
        minY = Math.min(minY, other.getMinY());
        minZ = Math.min(minZ, other.getMinZ());
        maxX = Math.max(maxX, other.getMaxX());
        maxY = Math.max(maxY, other.getMaxY());
        maxZ = Math.max(maxZ, other.getMaxZ());
    }

    /**
     * Returns the x-coordinate of the center
     *
     * @return The x-coordinate of the center
     */
    float getCenterX()
    {
        return getMinX() + getSizeX() * 0.5f;
    }

    /**
     * Returns the y-coordinate of the center
     *
     * @return The y-coordinate of the center
     */
    float getCenterY()
    {
        return getMinY() + getSizeY() * 0.5f;
    }

    /**
     * Returns the z-coordinate of the center
     *
     * @return The z-coordinate of the center
     */
    float getCenterZ()
    {
        return getMinZ() + getSizeZ() * 0.5f;
    }

    /**
     * Returns the size in x-direction
     *
     * @return The size in x-direction
     */
    float getSizeX()
    {
        return getMaxX() - getMinX();
    }

    /**
     * Returns the size in y-direction
     *
     * @return The size in y-direction
     */
    float getSizeY()
    {
        return getMaxY() - getMinY();
    }

    /**
     * Returns the size in z-direction
     *
     * @return The size in z-direction
     */
    float getSizeZ()
    {
        return getMaxZ() - getMinZ();
    }

    /**
     * Returns the minimum x coordinate
     *
     * @return The minimum x coordinate
     */
    float getMinX()
    {
        return minX;
    }

    /**
     * Returns the minimum y coordinate
     *
     * @return The minimum y coordinate
     */
    float getMinY()
    {
        return minY;
    }

    /**
     * Returns the minimum z coordinate
     *
     * @return The minimum z coordinate
     */
    float getMinZ()
    {
        return minZ;
    }

    /**
     * Returns the maximum x coordinate
     *
     * @return The maximum x coordinate
     */
    float getMaxX()
    {
        return maxX;
    }

    /**
     * Returns the maximum y coordinate
     *
     * @return The maximum y coordinate
     */
    float getMaxY()
    {
        return maxY;
    }

    /**
     * Returns the maximum z coordinate
     *
     * @return The maximum z coordinate
     */
    float getMaxZ()
    {
        return maxZ;
    }
    
    @Override
    public String toString()
    {
        return "[(" + 
            getMinX() + "," + getMinY() + "," + getMinZ() + ")-(" + 
            getMaxX() + "," + getMaxY() + "," + getMaxZ() + ")]";
    }
}