package org.the3deer.polybool;

/*
 * @copyright 2016 Sean Connelly (@voidqk), http://syntheti.cc
 * @license MIT
 * @preserve Project Home: https://github.com/voidqk/polybooljs
 */
/**

 *  This code is a Java port of the polybool Javascript library.
 *  There is no changes respect to the original source.
 *                                      the3deer.org
 *  ---------------------------------------------------------------------
 *
 * The MIT License (MIT)

    Copyright (c) 2016 Sean Connelly (@voidqk, web: syntheti.cc)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */


import android.os.Build;

import androidx.annotation.RequiresApi;

import org.the3deer.polybool.lib.BuildLog;
import org.the3deer.polybool.lib.Epsilon;
import org.the3deer.polybool.lib.GeoJSON;
import org.the3deer.polybool.lib.Intersecter;
import org.the3deer.polybool.lib.SegmentChainer;
import org.the3deer.polybool.lib.SegmentSelector;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;

import java.util.function.Function;

public class PolyBool {

    public static BuildLog buildLog = null;
    public static Epsilon epsilon = new Epsilon();
    public static SegmentSelector SegmentSelector = new SegmentSelector();
    // var GeoJSON = require('./lib/geojson');

    // getter/setter for buildLog
    public static JSList<?> buildLog(boolean bl) {
        if (bl)
            buildLog = new BuildLog();
        return buildLog == null ? null : buildLog.list;
    }

    // getter/setter for epsilon
    public static float epsilon(float v) {
        return epsilon.epsilon(v);
    }

    // core API
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap segments(JSMap poly) {
        Intersecter i_ = new Intersecter(true, epsilon, buildLog);
        JSMap i = i_.do1();
        poly.gl("regions").forEach((region) -> {i.gfn("addRegion").apply(region); });
        return JSMap.of(
                "segments", i.gfn("calculate").apply(poly.gb("inverted")),
                "inverted", poly.gb("inverted")
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap combine(JSMap segments1, JSMap segments2) {
        Intersecter i3_ = new Intersecter(false, epsilon, buildLog);
        JSMap i3 = i3_.do1();
        return JSMap.of(
                "combined", i3.gqfn("calculate").apply(
                        segments1.gl("segments"), segments1.gb("inverted"),
                        segments2.gl("segments"), segments2.gb("inverted")
                ),
                "inverted1", segments1.gb("inverted"),
                "inverted2", segments2.gb("inverted")
        );
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Function<JSMap, JSMap> selectUnion() {
        return (JSMap combined) -> JSMap.of(
                "segments", SegmentSelector.union(combined.gl("combined"), buildLog),
                "inverted", combined.gb("inverted1") || combined.gb("inverted2")
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Function<JSMap, JSMap> selectIntersect() {
        return (JSMap combined) -> JSMap.of(
                "segments", SegmentSelector.intersect(combined.gl("combined"), buildLog),
                "inverted", combined.gb("inverted1") || combined.gb("inverted2")
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Function<JSMap, JSMap> selectDifference() {
        return (JSMap combined) -> JSMap.of(
                "segments", SegmentSelector.difference(combined.gl("combined"), buildLog),
                "inverted", combined.gb("inverted1") && !combined.gb("inverted2")
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Function<JSMap, JSMap> selectDifferenceRev() {
        return (JSMap combined) -> JSMap.of(
                "segments", SegmentSelector.differenceRev(combined.gl("combined"), buildLog),
                "inverted", !combined.gb("inverted1") && combined.gb("inverted2")
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Function<JSMap, JSMap> selectXor() {
        return (JSMap combined) -> JSMap.of(
                "segments", SegmentSelector.xor(combined.gl("combined"), buildLog),
                "inverted", combined.gb("inverted1") != combined.gb("inverted2")    //  !==   should we instanceof() ?
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap polygon(JSMap segments) {
        return JSMap.of(
                "regions", SegmentChainer.regions(segments.gl("segments"), epsilon, buildLog),
                "inverted", segments.gb("inverted")
        );
    }

    // GeoJSON converters
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap polygonFromGeoJSON(JSMap geojson){
        return GeoJSON.toPolygon(null, geojson);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap polygonToGeoJSON(JSMap poly){
        return GeoJSON.fromPolygon(null, epsilon, poly);
    }

    // helper functions for common operations
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap union(JSMap poly1, JSMap poly2) {
        return operate(poly1, poly2, PolyBool.selectUnion());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap intersect(JSMap poly1, JSMap poly2) {
        return operate(poly1, poly2, PolyBool.selectIntersect());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap difference(JSMap poly1, JSMap poly2) {
        return operate(poly1, poly2, PolyBool.selectDifference());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap differenceRev(JSMap poly1, JSMap poly2) {
        return operate(poly1, poly2, PolyBool.selectDifferenceRev());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap xor(JSMap poly1, JSMap poly2) {
        return operate(poly1, poly2, PolyBool.selectXor());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSMap operate(JSMap poly1, JSMap poly2, Function<JSMap, JSMap> selector) {
        JSMap seg1 = PolyBool.segments(poly1);
        JSMap seg2 = PolyBool.segments(poly2);
        JSMap comb = PolyBool.combine(seg1, seg2);
        JSMap seg3 = selector.apply(comb);
        return PolyBool.polygon(seg3);
    }
}

// if (typeof window === 'object')
//         window.PolyBool = PolyBool;

// module.exports = PolyBool;
