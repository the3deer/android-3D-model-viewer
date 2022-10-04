package org.poly2tri.transform.coordinate;

import org.poly2tri.geometry.primitives.Point;

import java.util.List;

public abstract interface CoordinateTransform
{
    public abstract void transform( Point p, Point store );
    public abstract void transform( Point p );
    public abstract void transform( List<? extends Point> list );
}
