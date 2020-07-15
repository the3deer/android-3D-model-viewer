package org.andresoviedo.android_3d_model_engine.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright 2020 the3deers.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * This is a derivative work from https://github.com/mapbox/earcut
 *
 */
public class EarCut {

    public static List<Integer> earcut(float[] data, int[] holeIndices, int dim) {

        boolean hasHoles = holeIndices != null && holeIndices.length > 0;
                int outerLen = hasHoles ? holeIndices[0] * dim : data.length;
                Node outerNode = linkedList(data, 0, outerLen, dim, true);
        List<Integer> triangles = new ArrayList<>();

        if (outerNode == null || outerNode.next == outerNode.prev) return Collections.emptyList();

        float minX=0, minY=0, maxX=0, maxY=0, x, y, invSize=0;

        if (hasHoles) outerNode = eliminateHoles(data, holeIndices, outerNode, dim);

        // if the shape is not too simple, we'll use z-order curve hash later; calculate polygon bbox
        if (data.length > 80 * dim) {
            minX = maxX = data[0];
            minY = maxY = data[1];

            for (int i = dim; i < outerLen; i += dim) {
                x = data[i];
                y = data[i + 1];
                if (x < minX) minX = x;
                if (y < minY) minY = y;
                if (x > maxX) maxX = x;
                if (y > maxY) maxY = y;
            }

            // minX, minY and invSize are later used to transform coords into integers for z-order calculation
            invSize = Math.max(maxX - minX, maxY - minY);
            invSize = invSize != 0 ? 1 / invSize : 0;
        }

        earcutLinked(outerNode, triangles, dim, minX, minY, invSize, 0);

        return triangles;
    }

    // create a circular doubly linked list from polygon points in the specified winding order
    private static Node linkedList(float[] data, int start, int end, int dim, boolean clockwise) {
        int i; Node last = null;

        if (clockwise == (signedArea(data, start, end, dim) > 0)) {
            for (i = start; i < end; i += dim) last = insertNode(i, data[i], data[i + 1], last);
        } else {
            for (i = end - dim; i >= start; i -= dim) last = insertNode(i, data[i], data[i + 1], last);
        }

        if (last != null && equals(last, last.next)) {
            removeNode(last);
            last = last.next;
        }

        return last;
    }

    // eliminate colinear or duplicate points
    private static Node filterPoints(Node start, Node end) {
        if (start == null) return null;
        if (end == null) end = start;

        Node p = start;
                boolean again;
        do {
            again = false;

            if (!p.steiner && (equals(p, p.next) || area(p.prev, p, p.next) == 0)) {
                removeNode(p);
                p = end = p.prev;
                if (p == p.next) break;
                again = true;

            } else {
                p = p.next;
            }
        } while (again || p != end);

        return end;
    }

    // main ear slicing loop which triangulates a polygon (given as a linked list)
    private static void earcutLinked(Node ear, List<Integer> triangles, int dim, float minX, float minY, float invSize, int pass) {
        if (ear == null) return;

        // interlink polygon nodes in z-order
        if (pass == 0 && invSize != 0) indexCurve(ear, minX, minY, invSize);

        Node stop = ear,
                prev, next;

        // iterate through ears, slicing them one by one
        while (ear.prev != ear.next) {
            prev = ear.prev;
            next = ear.next;

            if (invSize != 0 ? isEarHashed(ear, minX, minY, invSize) : isEar(ear)) {
                // cut off the triangle
                triangles.add(prev.i / dim);
                triangles.add(ear.i / dim);
                triangles.add(next.i / dim);

                removeNode(ear);

                // skipping the next vertex leads to less sliver triangles
                ear = next.next;
                stop = next.next;

                continue;
            }

            ear = next;

            // if we looped through the whole remaining polygon and can't find any more ears
            if (ear == stop) {
                // try filtering points and slicing again
                if (pass == 0) {
                    earcutLinked(filterPoints(ear, null), triangles, dim, minX, minY, invSize, 1);

                    // if this didn't work, try curing all small self-intersections locally
                } else if (pass == 1) {
                    ear = cureLocalIntersections(filterPoints(ear, null), triangles, dim);
                    earcutLinked(ear, triangles, dim, minX, minY, invSize, 2);

                    // as a last resort, try splitting the remaining polygon into two
                } else if (pass == 2) {
                    splitEarcut(ear, triangles, dim, minX, minY, invSize);
                }

                break;
            }
        }
    }

    // check whether a polygon node forms a valid ear with adjacent nodes
    private static boolean isEar(Node ear) {
        Node a = ear.prev,
                b = ear,
                c = ear.next;

        if (area(a, b, c) >= 0) return false; // reflex, can't be an ear

        // now make sure we don't have other points inside the potential ear
        Node p = ear.next.next;

        while (p != ear.prev) {
            if (pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) &&
                    area(p.prev, p, p.next) >= 0) return false;
            p = p.next;
        }

        return true;
    }

    private static boolean isEarHashed(Node ear, float minX, float minY, float invSize) {
        Node a = ear.prev,
                b = ear,
                c = ear.next;

        if (area(a, b, c) >= 0) return false; // reflex, can't be an ear

        // triangle bbox; min & max are calculated like this for speed
        float minTX = a.x < b.x ? (a.x < c.x ? a.x : c.x) : (b.x < c.x ? b.x : c.x),
                minTY = a.y < b.y ? (a.y < c.y ? a.y : c.y) : (b.y < c.y ? b.y : c.y),
                maxTX = a.x > b.x ? (a.x > c.x ? a.x : c.x) : (b.x > c.x ? b.x : c.x),
                maxTY = a.y > b.y ? (a.y > c.y ? a.y : c.y) : (b.y > c.y ? b.y : c.y);

        // z-order range for the current triangle bbox;
        float minZ = zOrder(minTX, minTY, minX, minY, invSize),
                maxZ = zOrder(maxTX, maxTY, minX, minY, invSize);

        Node p = ear.prevZ,
                n = ear.nextZ;

        // look for points inside the triangle in both directions
        while (p != null && p.z >= minZ && n != null && n.z <= maxZ) {
            if (p != ear.prev && p != ear.next &&
                    pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) &&
                    area(p.prev, p, p.next) >= 0) return false;
            p = p.prevZ;

            if (n != ear.prev && n != ear.next &&
                    pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, n.x, n.y) &&
                    area(n.prev, n, n.next) >= 0) return false;
            n = n.nextZ;
        }

        // look for remaining points in decreasing z-order
        while (p != null && p.z >= minZ) {
            if (p != ear.prev && p != ear.next &&
                    pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) &&
                    area(p.prev, p, p.next) >= 0) return false;
            p = p.prevZ;
        }

        // look for remaining points in increasing z-order
        while (n != null && n.z <= maxZ) {
            if (n != ear.prev && n != ear.next &&
                    pointInTriangle(a.x, a.y, b.x, b.y, c.x, c.y, n.x, n.y) &&
                    area(n.prev, n, n.next) >= 0) return false;
            n = n.nextZ;
        }

        return true;
    }

    // go through all polygon nodes and cure small local self-intersections
    private static Node cureLocalIntersections(Node start, List<Integer> triangles, int dim) {
        Node p = start;
        do {
            Node a = p.prev,
                    b = p.next.next;

            if (!equals(a, b) && intersects(a, p, p.next, b) && locallyInside(a, b) && locallyInside(b, a)) {

                triangles.add(a.i / dim);
                triangles.add(p.i / dim);
                triangles.add(b.i / dim);

                // remove two nodes involved
                removeNode(p);
                removeNode(p.next);

                p = start = b;
            }
            p = p.next;
        } while (p != start);

        return filterPoints(p, null);
    }

    // try splitting polygon into two and triangulate them independently
    private static void splitEarcut(Node start, List<Integer> triangles, int dim, float minX, float minY, float invSize) {
        // look for a valid diagonal that divides the polygon into two
        Node a = start;
        do {
            Node b = a.next.next;
            while (b != a.prev) {
                if (a.i != b.i && isValidDiagonal(a, b)) {
                    // split the polygon in two by the diagonal
                    Node c = splitPolygon(a, b);

                    // filter colinear points around the cuts
                    a = filterPoints(a, a.next);
                    c = filterPoints(c, c.next);

                    // run earcut on each half
                    earcutLinked(a, triangles, dim, minX, minY, invSize, 0);
                    earcutLinked(c, triangles, dim, minX, minY, invSize, 0);
                    return;
                }
                b = b.next;
            }
            a = a.next;
        } while (a != start);
    }

    // link every hole into the outer loop, producing a single-ring polygon without holes
    private static Node eliminateHoles(float[] data, int[] holeIndices, Node outerNode, int dim) {
        List<Node> queue = new ArrayList<>();
        int i, len, start, end; Node list;

        for (i = 0, len = holeIndices.length; i < len; i++) {
            start = holeIndices[i] * dim;
            end = i < len - 1 ? holeIndices[i + 1] * dim : data.length;
            list = linkedList(data, start, end, dim, false);
            if (list == list.next) list.steiner = true;
            queue.add(getLeftmost(list));
        }

        Collections.sort(queue, compareX());

        // process holes from left to right
        for (i = 0; i < queue.size(); i++) {
            eliminateHole(queue.get(i), outerNode);
            outerNode = filterPoints(outerNode, outerNode.next);
        }

        return outerNode;
    }

    private static Comparator<Node> compareX() {
        return (a, b) -> Float.compare(a.x,b.x);
    }

    // find a bridge between vertices that connects hole with an outer ring and and link it
    private static void eliminateHole(Node hole, Node outerNode) {
        outerNode = findHoleBridge(hole, outerNode);
        if (outerNode != null) {
            Node b = splitPolygon(outerNode, hole);

            // filter collinear points around the cuts
            filterPoints(outerNode, outerNode.next);
            filterPoints(b, b.next);
        }
    }

    // David Eberly's algorithm for finding a bridge between hole and outer polygon
    private static Node findHoleBridge(Node hole, Node outerNode) {
        Node p = outerNode;
                float hx = hole.x,
                hy = hole.y,
                qx = -Float.MAX_VALUE;
                Node m = null;

        // find a segment intersected by a ray from the hole's leftmost point to the left;
        // segment's endpoint with lesser x will be potential connection point
        do {
            if (hy <= p.y && hy >= p.next.y && p.next.y != p.y) {
                float x = p.x + (hy - p.y) * (p.next.x - p.x) / (p.next.y - p.y);
                if (x <= hx && x > qx) {
                    qx = x;
                    if (x == hx) {
                        if (hy == p.y) return p;
                        if (hy == p.next.y) return p.next;
                    }
                    m = p.x < p.next.x ? p : p.next;
                }
            }
            p = p.next;
        } while (p != outerNode);

        if (m == null) return null;

        if (hx == qx) return m; // hole touches outer segment; pick leftmost endpoint

        // look for points inside the triangle of hole point, segment intersection and endpoint;
        // if there are no points found, we have a valid connection;
        // otherwise choose the point of the minimum angle with the ray as connection point

        Node stop = m;
                float mx = m.x,
                my = m.y,
                tanMin = Float.MAX_VALUE,
                tan;

        p = m;

        do {
            if (hx >= p.x && p.x >= mx && hx != p.x &&
                    pointInTriangle(hy < my ? hx : qx, hy, mx, my, hy < my ? qx : hx, hy, p.x, p.y)) {

                tan = Math.abs(hy - p.y) / (hx - p.x); // tangential

                if (locallyInside(p, hole) &&
                        (tan < tanMin || (tan == tanMin && (p.x > m.x || (p.x == m.x && sectorContainsSector(m, p)))))) {
                    m = p;
                    tanMin = tan;
                }
            }

            p = p.next;
        } while (p != stop);

        return m;
    }

    // whether sector in vertex m contains sector in vertex p in the same coordinates
    private static boolean sectorContainsSector(Node m, Node p) {
        return area(m.prev, m, p.prev) < 0 && area(p.next, m, m.next) < 0;
    }

    // interlink polygon nodes in z-order
    private static void indexCurve(Node start, float minX, float minY, float invSize) {
        Node p = start;
        do {
            if (p.z == -1) p.z = zOrder(p.x, p.y, minX, minY, invSize);
            p.prevZ = p.prev;
            p.nextZ = p.next;
            p = p.next;
        } while (p != start);

        p.prevZ.nextZ = null;
        p.prevZ = null;

        sortLinked(p);
    }

    // Simon Tatham's linked list merge sort algorithm
// http://www.chiark.greenend.org.uk/~sgtatham/algorithms/listsort.html
    private static Node sortLinked(Node list) {
        int i; Node p, q, e, tail; int numMerges, pSize, qSize,
                inSize = 1;

        do {
            p = list;
            list = null;
            tail = null;
            numMerges = 0;

            while (p != null) {
                numMerges++;
                q = p;
                pSize = 0;
                for (i = 0; i < inSize; i++) {
                    pSize++;
                    q = q.nextZ;
                    if (q == null) break;
                }
                qSize = inSize;

                while (pSize > 0 || (qSize > 0 && q != null)) {

                    if (pSize != 0 && (qSize == 0 || q == null || p.z <= q.z)) {
                        e = p;
                        p = p.nextZ;
                        pSize--;
                    } else {
                        e = q;
                        q = q.nextZ;
                        qSize--;
                    }

                    if (tail != null) tail.nextZ = e;
                    else list = e;

                    e.prevZ = tail;
                    tail = e;
                }

                p = q;
            }

            tail.nextZ = null;
            inSize *= 2;

        } while (numMerges > 1);

        return list;
    }

    // z-order of a point given coords and inverse of the longer side of data bbox
    static float zOrder(float x0, float y0, float minX, float minY, float invSize) {
        // coords are transformed into non-negative 15-bit integer range
        int x = (int) (32767 * (x0 - minX) * invSize);
        int y = (int) (32767 * (y0 - minY) * invSize);

        x = (x | (x << 8)) & 0x00FF00FF;
        x = (x | (x << 4)) & 0x0F0F0F0F;
        x = (x | (x << 2)) & 0x33333333;
        x = (x | (x << 1)) & 0x55555555;

        y = (y | (y << 8)) & 0x00FF00FF;
        y = (y | (y << 4)) & 0x0F0F0F0F;
        y = (y | (y << 2)) & 0x33333333;
        y = (y | (y << 1)) & 0x55555555;

        return x | (y << 1);
    }

    // find the leftmost node of a polygon ring
    private static Node getLeftmost(Node start) {
        Node p = start,
                leftmost = start;
        do {
            if (p.x < leftmost.x || (p.x == leftmost.x && p.y < leftmost.y)) leftmost = p;
            p = p.next;
        } while (p != start);

        return leftmost;
    }

    // check if a point lies within a convex triangle
    private static boolean pointInTriangle(float ax, float ay, float bx, float by, float cx, float cy, float px, float py) {
        return (cx - px) * (ay - py) - (ax - px) * (cy - py) >= 0 &&
                (ax - px) * (by - py) - (bx - px) * (ay - py) >= 0 &&
                (bx - px) * (cy - py) - (cx - px) * (by - py) >= 0;
    }

    // check if a diagonal between two polygon nodes is valid (lies in polygon interior)
    private static boolean isValidDiagonal(Node a, Node b) {
        return a.next.i != b.i && a.prev.i != b.i && !intersectsPolygon(a, b) && // dones't intersect other edges
                (locallyInside(a, b) && locallyInside(b, a) && middleInside(a, b) && // locally visible
                        (area(a.prev, a, b.prev) != 0 || area(a, b.prev, b) != 0) || // does not create opposite-facing sectors
                        equals(a, b) && area(a.prev, a, a.next) > 0 && area(b.prev, b, b.next) > 0); // special zero-length case
    }

    // signed area of a triangle
    private static float area(Node p, Node q, Node r) {
        return (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
    }

    // check if two points are equal
    private static boolean equals(Node p1, Node p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    // check if two segments intersect
    private static boolean intersects(Node p1, Node q1, Node p2, Node q2) {
        int o1 = sign(area(p1, q1, p2));
        int o2 = sign(area(p1, q1, q2));
        int o3 = sign(area(p2, q2, p1));
        int o4 = sign(area(p2, q2, q1));

        if (o1 != o2 && o3 != o4) return true; // general case

        if (o1 == 0 && onSegment(p1, p2, q1)) return true; // p1, q1 and p2 are collinear and p2 lies on p1q1
        if (o2 == 0 && onSegment(p1, q2, q1)) return true; // p1, q1 and q2 are collinear and q2 lies on p1q1
        if (o3 == 0 && onSegment(p2, p1, q2)) return true; // p2, q2 and p1 are collinear and p1 lies on p2q2
        if (o4 == 0 && onSegment(p2, q1, q2)) return true; // p2, q2 and q1 are collinear and q1 lies on p2q2

        return false;
    }

    // for collinear points p, q, r, check if point q lies on segment pr
    private static boolean onSegment(Node p, Node q, Node r) {
        return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
    }

    private static int sign(float num) {
        return num > 0 ? 1 : num < 0 ? -1 : 0;
    }

    // check if a polygon diagonal intersects any polygon segments
    private static boolean intersectsPolygon(Node a, Node b) {
        Node p = a;
        do {
            if (p.i != a.i && p.next.i != a.i && p.i != b.i && p.next.i != b.i &&
                    intersects(p, p.next, a, b)) return true;
            p = p.next;
        } while (p != a);

        return false;
    }

    // check if a polygon diagonal is locally inside the polygon
    private static boolean locallyInside(Node a, Node b) {
        return area(a.prev, a, a.next) < 0 ?
                area(a, b, a.next) >= 0 && area(a, a.prev, b) >= 0 :
                area(a, b, a.prev) < 0 || area(a, a.next, b) < 0;
    }

    // check if the middle point of a polygon diagonal is inside the polygon
    private static boolean middleInside(Node a, Node b) {
        Node p = a;
                boolean inside = false;
                float px = (a.x + b.x) / 2,
                py = (a.y + b.y) / 2;
        do {
            if (((p.y > py) != (p.next.y > py)) && p.next.y != p.y &&
                    (px < (p.next.x - p.x) * (py - p.y) / (p.next.y - p.y) + p.x))
                inside = !inside;
            p = p.next;
        } while (p != a);

        return inside;
    }

    // link two polygon vertices with a bridge; if the vertices belong to the same ring, it splits polygon into two;
// if one belongs to the outer ring and another to a hole, it merges it into a single ring
    private static Node splitPolygon(Node a, Node b) {
        Node a2 = new Node(a.i, a.x, a.y),
                b2 = new Node(b.i, b.x, b.y),
                an = a.next,
                bp = b.prev;

        a.next = b;
        b.prev = a;

        a2.next = an;
        an.prev = a2;

        b2.next = a2;
        a2.prev = b2;

        bp.next = b2;
        b2.prev = bp;

        return b2;
    }

    // create a node and optionally link it with previous one (in a circular doubly linked list)
    private static Node insertNode(int i, float x, float y, Node last) {
        Node p = new Node(i, x, y);

        if (last == null) {
            p.prev = p;
            p.next = p;

        } else {
            p.next = last.next;
            p.prev = last;
            last.next.prev = p;
            last.next = p;
        }
        return p;
    }

    private static void removeNode(Node p) {
        p.next.prev = p.prev;
        p.prev.next = p.next;

        if (p.prevZ != null) p.prevZ.nextZ = p.nextZ;
        if (p.nextZ != null) p.nextZ.prevZ = p.prevZ;
    }

    static class Node {

        int i;
        float x;
        float y;
        float z;
        boolean steiner;
        Node prev;
        Node next;
        Node nextZ;
        Node prevZ;

        Node(int i, float x, float y) {
        // vertex index in coordinates array
        this.i = i;

        // vertex coordinates
        this.x = x;
        this.y = y;

        // previous and next vertex nodes in a polygon ring
        this.prev = null;
        this.next = null;

        // z-order curve value
        this.z = -1;

        // previous and next nodes in z-order
        this.prevZ = null;
        this.nextZ = null;

        // indicates whether this is a steiner point
        this.steiner = false;
    }
    }

// return a percentage difference between the polygon area and its triangulation area;
// used to verify correctness of triangulation
    public float deviation(float[] data, int[] holeIndices, int dim, List<Integer> triangles) {
        boolean hasHoles = holeIndices != null && holeIndices.length > 0;
        int outerLen = hasHoles ? holeIndices[0] * dim : data.length;

        float polygonArea = Math.abs(signedArea(data, 0, outerLen, dim));
        if (hasHoles) {
            for (int i = 0, len = holeIndices.length; i < len; i++) {
                int start = holeIndices[i] * dim;
                int end = i < len - 1 ? holeIndices[i + 1] * dim : data.length;
                polygonArea -= Math.abs(signedArea(data, start, end, dim));
            }
        }

        float trianglesArea = 0;
        for (int i = 0; i < triangles.size(); i += 3) {
            int a = triangles.get(i) * dim;
            int b = triangles.get(i + 1) * dim;
            int c = triangles.get(i + 2) * dim;
            trianglesArea += Math.abs(
                    (data[a] - data[c]) * (data[b + 1] - data[a + 1]) -
                            (data[a] - data[b]) * (data[c + 1] - data[a + 1]));
        }

        return polygonArea == 0 && trianglesArea == 0 ? 0 :
                Math.abs((trianglesArea - polygonArea) / polygonArea);
    };

    private static float signedArea(float[] data, int start, int end, int dim) {
        float sum = 0;
        for (int i = start, j = end - dim; i < end; i += dim) {
            sum += (data[j] - data[i]) * (data[i + 1] + data[j + 1]);
            j = i;
        }
        return sum;
    }

// turn a polygon in a multi-dimensional array form (e.g. as in GeoJSON) into a form Earcut accepts
    public static Object[] flatten(float[][][] data) {
        int dim = data[0][0].length;
                Object[] result = new Object[]{new ArrayList<Float>(), new ArrayList<Integer>(), dim};
        int holeIndex = 0;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                for (int d = 0; d < dim; d++) ((List<Float>)result[0]).add(data[i][j][d]);
            }
            if (i > 0) {
                holeIndex += data[i - 1].length;
                ((List<Integer>)result[1]).add(holeIndex);
            }
        }
        return result;
    };
}
