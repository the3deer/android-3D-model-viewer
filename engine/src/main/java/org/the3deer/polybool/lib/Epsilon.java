package org.the3deer.polybool.lib;

// (c) Copyright 2016, Sean Connelly (@voidqk), http://syntheti.cc
// MIT License
// Project Home: https://github.com/voidqk/polybooljs

import org.the3deer.util.javascript.JSArray;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;

//
// provides the raw computation functions that takes epsilon into account
//
// zero is defined to be between (-epsilon, epsilon) exclusive
//
public class Epsilon {

    private float eps;
    // eps = 0.0000000001; // sane default? sure why not

    private final Epsilon my = this;

    public Epsilon(){
        this(0.001f);
    }

    public Epsilon(float eps){
        if (eps != 0)
            this.eps = eps;
        else
            this.eps = 0.0000000001f; // sane default? sure why not
    }

    // var my = {
    public float epsilon(float v) {
        this.eps = v;
        return eps;
    }

    boolean pointAboveOrOnLine(float[] pt, float[] left, float[] right){
        float Ax = left[0];
        float Ay = left[1];
        float Bx = right[0];
        float By = right[1];
        float Cx = pt[0];
        float Cy = pt[1];
        return (Bx - Ax) * (Cy - Ay) - (By - Ay) * (Cx - Ax) >= -eps;
    }

    public boolean pointBetween(float[] p, float[] left, float[] right){
        // p must be collinear with left->right
        // returns false if p == left, p == right, or left == right
        float d_py_ly = p[1] - left[1];
        float d_rx_lx = right[0] - left[0];
        float d_px_lx = p[0] - left[0];
        float d_ry_ly = right[1] - left[1];

        float dot = d_px_lx * d_rx_lx + d_py_ly * d_ry_ly;
        // if `dot` is 0, then `p` == `left` or `left` == `right` (reject)
        // if `dot` is less than 0, then `p` is to the left of `left` (reject)
        if (dot < eps)
            return false;

        float sqlen = d_rx_lx * d_rx_lx + d_ry_ly * d_ry_ly;
        // if `dot` > `sqlen`, then `p` is to the right of `right` (reject)
        // therefore, if `dot - sqlen` is greater than 0, then `p` is to the right of `right` (reject)
        if (dot - sqlen > -eps)
            return false;

        return true;
    }

    public boolean pointsSameX(float[] p1, float[] p2){
        return Math.abs(p1[0] - p2[0]) < eps;
    }
    public boolean pointsSameY(float[] p1, float[] p2){
        return Math.abs(p1[1] - p2[1]) < eps;
    }
    public boolean pointsSame(float[] p1, float[] p2){
        return my.pointsSameX(p1, p2) && my.pointsSameY(p1, p2);
    }
    public int pointsCompare(float[] p1, float[] p2){
        // returns -1 if p1 is smaller, 1 if p2 is smaller, 0 if equal
        if (my.pointsSameX(p1, p2))
            return my.pointsSameY(p1, p2) ? 0 : (p1[1] < p2[1] ? -1 : 1);
        return p1[0] < p2[0] ? -1 : 1;
    }
    public boolean pointsCollinear(float[] pt1, float[] pt2, float[] pt3){
        // does pt1->pt2->pt3 make a straight line?
        // essentially this is just checking to see if the slope(pt1->pt2) === slope(pt2->pt3)
        // if slopes are equal, then they must be collinear, because they share pt2
        float dx1 = pt1[0] - pt2[0];
        float dy1 = pt1[1] - pt2[1];
        float dx2 = pt2[0] - pt3[0];
        float dy2 = pt2[1] - pt3[1];
        return Math.abs(dx1 * dy2 - dx2 * dy1) < eps;
    }
    public Object linesIntersect(float[] a0, float[] a1, float[] b0, float[] b1){
        // returns false if the lines are coincident (e.g., parallel or on top of each other)
        //
        // returns an object if the lines intersect:
        //   {
        //     pt: [x, y],    where the intersection point is at
        //     alongA: where intersection point is along A,
        //     alongB: where intersection point is along B
        //   }
        //
        //  alongA and alongB will each be one of: -2, -1, 0, 1, 2
        //
        //  with the following meaning:
        //
        //    -2   intersection point is before segment's first point
        //    -1   intersection point is directly on segment's first point
        //     0   intersection point is between segment's first and second points (exclusive)
        //     1   intersection point is directly on segment's second point
        //     2   intersection point is after segment's second point
        float  adx = a1[0] - a0[0];
        float  ady = a1[1] - a0[1];
        float  bdx = b1[0] - b0[0];
        float  bdy = b1[1] - b0[1];

        float  axb = adx * bdy - ady * bdx;
        if (Math.abs(axb) < eps)
            return Boolean.FALSE; // lines are coincident

        float  dx = a0[0] - b0[0];
        float  dy = a0[1] - b0[1];

        float  A = (bdx * dy - bdy * dx) / axb;
        float  B = (adx * dy - ady * dx) / axb;

        JSMap ret = JSMap.of(
                "alongA", 0,
                "alongB", 0,
                "pt", (Object)JSArray.arrayOf(
                    a0[0] + A * adx,
                    a0[1] + A * ady
                )
        );

        // categorize where intersection point is along A and B

        if (A <= -eps)
            ret.p("alongA",-2);
        else if (A < eps)
            ret.p("alongA", -1);
        else if (A - 1 <= -eps)
            ret.p("alongA",0);
        else if (A - 1 < eps)
            ret.p("alongA",1);
        else
            ret.p("alongA",2);

        if (B <= -eps)
            ret.p("alongB",-2);
        else if (B < eps)
            ret.p("alongB",-1);
        else if (B - 1 <= -eps)
            ret.p("alongB",0);
        else if (B - 1 < eps)
            ret.p("alongB",1);
        else
            ret.p("alongB",2);

        return ret;
    }
    public boolean pointInsideRegion(float[] pt, JSList<float[]> region){
        float x = pt[0];
        float y = pt[1];
        float last_x = region.gfa(region.length() - 1)[0];
        float last_y = region.gfa(region.length() - 1)[1];
        boolean inside = false;
        for (int i = 0; i < region.length(); i++){
            float curr_x = region.gfa(i)[0];
            float curr_y = region.gfa(i)[1];

            // if y is between curr_y and last_y, and
            // x is to the right of the boundary created by the line
            if ((curr_y - y > eps) != (last_y - y > eps) &&
                    (last_x - curr_x) * (y - curr_y) / (last_y - curr_y) + curr_x - x > eps)
                inside = !inside;

            last_x = curr_x;
            last_y = curr_y;
        }
        return inside;
    }
	// return my;
}

// module.exports = Epsilon;