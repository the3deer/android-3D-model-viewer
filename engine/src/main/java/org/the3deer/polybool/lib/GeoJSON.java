package org.the3deer.polybool.lib;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.the3deer.polybool.PolyBool;
import org.the3deer.util.function.Recursive;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;

import java.util.function.BiFunction;
import java.util.function.Function;

public class GeoJSON {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap toPolygon(PolyBool polyBool, JSMap geojson) {


        // converts list of LineString's to segments
        Function<JSList<float[]>, JSMap> GeoPoly = (JSList<float[]> coords) -> {
            // check for empty coords
            if (coords.length() <= 0)
                return PolyBool.segments(JSMap.of("inverted", false, "regions", new JSList()));

            // convert LineString to segments
            Function<JSList, JSMap> LineString = (JSList ls) -> {
                // remove tail which should be the same as head
                JSList reg = ls.slice(0, ls.length() - 1);
                return PolyBool.segments(JSMap.of("inverted", false, "regions", JSList.of(reg)));
            };

            // the first LineString is considered the outside
            JSMap out = LineString.apply(coords.gl(0));

            // the rest of the LineStrings are considered interior holes, so subtract them from the
            // current result
            for (int i = 1; i < coords.length(); i++)
                out = PolyBool.selectDifference().apply(PolyBool.combine(out, LineString.apply(coords.gl(i))));

            return out;
        };

        if (geojson.gs("type").equals("Polygon")) {
            // single polygon, so just convert it and we're done
            return PolyBool.polygon(GeoPoly.apply(geojson.gl("coordinates")));
        } else if (geojson.gs("type").equals("MultiPolygon")) {
            // multiple polygons, so union all the polygons together
            JSMap out = PolyBool.segments(JSMap.of("inverted", false, "regions", new JSList()));
            for (int i = 0; i < geojson.gl("coordinates").length(); i++) {
                JSList l = geojson.gl("coordinates").gl(i);
                out = PolyBool.selectUnion().apply(PolyBool.combine(out, GeoPoly.apply(l)));
            }
            return PolyBool.polygon(out);
        }
        throw new RuntimeException("PolyBool: Cannot convert GeoJSON object to PolyBool polygon");
    }

    // convert a PolyBool polygon to a GeoJSON object
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap fromPolygon(PolyBool polyBool, Epsilon eps, JSMap poly) {
        // make sure out polygon is clean
        poly = PolyBool.polygon(PolyBool.segments(poly));

        // test if r1 is inside r2
        BiFunction<JSList<float[]>, JSList<float[]>, Boolean> regionInsideRegion = (JSList<float[]> r1, JSList<float[]> r2) -> {
            // we're guaranteed no lines intersect (because the polygon is clean), but a vertex
            // could be on the edge -- so we just average pt[0] and pt[1] to produce a point on the
            // edge of the first line, which cannot be on an edge
            return eps.pointInsideRegion(new float[]{
                    (float) ((r1.gfa(0)[0] + r1.gfa(1)[0]) * 0.5),
                    (float) ((r1.gfa(0)[1] + r1.gfa(1)[1]) * 0.5)
            }, r2);
        };

        // calculate inside heirarchy
        //
        //  _____________________   _______    roots -> A       -> F
        // |          A          | |   F   |            |          |
        // |  _______   _______  | |  ___  |            +-- B      +-- G
        // | |   B   | |   C   | | | |   | |            |   |
        // | |  ___  | |  ___  | | | |   | |            |   +-- D
        // | | | D | | | | E | | | | | G | |            |
        // | | |___| | | |___| | | | |   | |            +-- C
        // | |_______| |_______| | | |___| |                |
        // |_____________________| |_______|                +-- E

        Function<JSList<float[]>, JSMap<Object>> newNode = (JSList<float[]> region) -> {
            return JSMap.of(
                    "region", region,
                    "children", new JSList<JSMap<Object>>()
            );
        };

        JSMap roots = newNode.apply(null);

        final Recursive<BiFunction<JSMap, JSList<float[]>, Integer>> recursive = new Recursive<>();
        recursive.addChild = (JSMap root, JSList<float[]> region) -> {
            // first check if we're inside any children
            for (int i = 0; i < root.gl("children").length(); i++) {
                JSMap child = root.gl("children").gm(i);
                if (regionInsideRegion.apply(region, child.gl("region"))) {
                    // we are, so insert inside them instead
                    recursive.addChild.apply(child, region);
                    return null;
                }
            }

            // not inside any children, so check to see if any children are inside us
            JSMap node = newNode.apply(region);
            for (int i = 0; i < root.gl("children").length(); i++) {
                JSMap child = root.gl("children").gm(i);
                if (regionInsideRegion.apply(child.gl("region"), region)) {
                    // oops... move the child beneath us, and remove them from root
                    node.gl("children").push(child);
                    root.gl("children").splice(i, 1);
                    i--;
                }
            }

            // now we can add ourselves
            return root.gl("children").push(node);
        };

        // add all regions to the root
        for (int i = 0; i < poly.gl("regions").length(); i++) {
            JSList<float[]> region = poly.gl("regions").gl(i);
            if (region.length() < 3) // regions must have at least 3 points (sanity check)
                continue;
            recursive.addChild.apply(roots, region);
        }

        // with our heirarchy, we can distinguish between exterior borders, and interior holes
        // the root nodes are exterior, children are interior, children's children are exterior,
        // children's children's children are interior, etc

        // while we're at it, exteriors are counter-clockwise, and interiors are clockwise

        BiFunction<JSList<float[]>, Boolean, JSList<float[]>> forceWinding = (JSList<float[]> region, Boolean clockwise) -> {
            // first, see if we're clockwise or counter-clockwise
            // https://en.wikipedia.org/wiki/Shoelace_formula
            int winding = 0;
            float last_x = region.gfa(region.length() - 1)[0];
            float last_y = region.gfa(region.length() - 1)[1];
            JSList<float[]> copy = new JSList<>();
            for (int i = 0; i < region.length(); i++) {
                float curr_x = region.gfa(i)[0];
                float curr_y = region.gfa(i)[1];
                copy.push(new float[]{curr_x, curr_y}); // create a copy while we're at it
                winding += curr_y * last_x - curr_x * last_y;
                last_x = curr_x;
                last_y = curr_y;
            }
            // this assumes Cartesian coordinates (Y is positive going up)
            boolean isclockwise = winding < 0;
            if (isclockwise != clockwise)
                copy.reverse();
            // while we're here, the last point must be the first point...
            copy.push(new float[]{copy.gfa(0)[0], copy.gfa(0)[1]});
            return copy;
        };

        JSList<JSList<JSList<float[]>>> geopolys = new JSList();

        Function<JSMap<Object>, JSList<float[]>> getInterior = null;

        Function<JSMap<Object>, JSList<float[]>> finalGetInterior = getInterior;
        Function<JSMap<Object>, Void> addExterior = (JSMap<Object> node) -> {
            JSList<float[]> poly2 = forceWinding.apply(node.gl("region"), false);
            geopolys.push(JSList.of(poly2));
            // children of exteriors are interior
            for (int i = 0; i < node.gl("children").length(); i++)
                poly2.push(finalGetInterior.apply(node.gl("children").gm(i)));
            return null;
        };

        getInterior = (JSMap<Object> node) -> {
            // children of interiors are exterior
            for (int i = 0; i < node.gl("children").length(); i++)
                addExterior.apply(node.gl("children").gm(i));
            // return the clockwise interior
            return forceWinding.apply(node.gl("region"), true);
        };

        // root nodes are exterior
        for (int i = 0; i < roots.gl("children").length(); i++)
            addExterior.apply(roots.gl("children").gm(i));

        // lastly, construct the approrpriate GeoJSON object

        if (geopolys.length() <= 0) // empty GeoJSON Polygon
            return JSMap.of("type", "Polygon", "coordinates", new JSList<>());
        if (geopolys.length() == 1) // use a GeoJSON Polygon
            return JSMap.of("type", "Polygon", "coordinates", geopolys.gl(0));
        return JSMap.of( // otherwise, use a GeoJSON MultiPolygon
                "type", "MultiPolygon",
                "coordinates", geopolys
        );
    }
}

// module.exports = GeoJSON;