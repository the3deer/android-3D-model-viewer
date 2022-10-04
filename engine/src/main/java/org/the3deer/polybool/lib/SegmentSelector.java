package org.the3deer.polybool.lib;

// (c) Copyright 2016, Sean Connelly (@voidqk), http://syntheti.cc
// MIT License
// Project Home: https://github.com/voidqk/polybooljs

//
// filter a list of segments based on boolean operations
//

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;

public class SegmentSelector {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSList select(JSList<JSMap> segments, JSList<Integer> selection, BuildLog buildLog) {
        JSList result = new JSList();
        segments.forEach((JSMap seg) -> {
            int index =
                    (seg.gb("myFill.above") ? 8 : 0) +
                            (seg.gb("myFill.below") ? 4 : 0) +
                            ((seg.c("otherFill") && seg.gb("otherFill.above")) ? 2 : 0) +
                            ((seg.c("otherFill") && seg.gb("otherFill.below")) ? 1 : 0);
            if (selection.get(index) != 0) {
                // copy the segment to the results, while also calculating the fill status
                result.push(JSMap.of(
                        "id", buildLog != null ? buildLog.segmentId() : -1,
                        "start", seg.gr("start"),
                        "end", seg.gr("end"),
                        "myFill", JSMap.of(
                                "above", selection.get(index) == 1, // 1 if filled above
                                "below", selection.get(index) == 2  // 2 if filled below
                        ),
                        "otherFill", null
                ));
            }
        });

        if (buildLog != null)
            buildLog.selected(result);

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSList union(JSList<JSMap> segments, BuildLog buildLog) {  // primary | secondary
        // above1 below1 above2 below2    Keep?               Value
        //    0      0      0      0   =>   no                  0
        //    0      0      0      1   =>   yes filled below    2
        //    0      0      1      0   =>   yes filled above    1
        //    0      0      1      1   =>   no                  0
        //    0      1      0      0   =>   yes filled below    2
        //    0      1      0      1   =>   yes filled below    2
        //    0      1      1      0   =>   no                  0
        //    0      1      1      1   =>   no                  0
        //    1      0      0      0   =>   yes filled above    1
        //    1      0      0      1   =>   no                  0
        //    1      0      1      0   =>   yes filled above    1
        //    1      0      1      1   =>   no                  0
        //    1      1      0      0   =>   no                  0
        //    1      1      0      1   =>   no                  0
        //    1      1      1      0   =>   no                  0
        //    1      1      1      1   =>   no                  0
        return select(segments, JSList.of(
                0, 2, 1, 0,
                2, 2, 0, 0,
                1, 0, 1, 0,
                0, 0, 0, 0
        ), buildLog);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSList intersect(JSList<JSMap> segments, BuildLog buildLog) {  // primary & secondary
        // above1 below1 above2 below2    Keep?               Value
        //    0      0      0      0   =>   no                  0
        //    0      0      0      1   =>   no                  0
        //    0      0      1      0   =>   no                  0
        //    0      0      1      1   =>   no                  0
        //    0      1      0      0   =>   no                  0
        //    0      1      0      1   =>   yes filled below    2
        //    0      1      1      0   =>   no                  0
        //    0      1      1      1   =>   yes filled below    2
        //    1      0      0      0   =>   no                  0
        //    1      0      0      1   =>   no                  0
        //    1      0      1      0   =>   yes filled above    1
        //    1      0      1      1   =>   yes filled above    1
        //    1      1      0      0   =>   no                  0
        //    1      1      0      1   =>   yes filled below    2
        //    1      1      1      0   =>   yes filled above    1
        //    1      1      1      1   =>   no                  0
        return select(segments, JSList.of(
                0, 0, 0, 0,
                0, 2, 0, 2,
                0, 0, 1, 1,
                0, 2, 1, 0
        ), buildLog);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSList difference(JSList<JSMap> segments, BuildLog buildLog) {// primary - secondary
        // above1 below1 above2 below2    Keep?               Value
        //    0      0      0      0   =>   no                  0
        //    0      0      0      1   =>   no                  0
        //    0      0      1      0   =>   no                  0
        //    0      0      1      1   =>   no                  0
        //    0      1      0      0   =>   yes filled below    2
        //    0      1      0      1   =>   no                  0
        //    0      1      1      0   =>   yes filled below    2
        //    0      1      1      1   =>   no                  0
        //    1      0      0      0   =>   yes filled above    1
        //    1      0      0      1   =>   yes filled above    1
        //    1      0      1      0   =>   no                  0
        //    1      0      1      1   =>   no                  0
        //    1      1      0      0   =>   no                  0
        //    1      1      0      1   =>   yes filled above    1
        //    1      1      1      0   =>   yes filled below    2
        //    1      1      1      1   =>   no                  0
        return select(segments, JSList.of(
                0, 0, 0, 0,
                2, 0, 2, 0,
                1, 1, 0, 0,
                0, 1, 2, 0
        ), buildLog);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSList differenceRev(JSList<JSMap> segments, BuildLog buildLog) {// secondary - primary
        // above1 below1 above2 below2    Keep?               Value
        //    0      0      0      0   =>   no                  0
        //    0      0      0      1   =>   yes filled below    2
        //    0      0      1      0   =>   yes filled above    1
        //    0      0      1      1   =>   no                  0
        //    0      1      0      0   =>   no                  0
        //    0      1      0      1   =>   no                  0
        //    0      1      1      0   =>   yes filled above    1
        //    0      1      1      1   =>   yes filled above    1
        //    1      0      0      0   =>   no                  0
        //    1      0      0      1   =>   yes filled below    2
        //    1      0      1      0   =>   no                  0
        //    1      0      1      1   =>   yes filled below    2
        //    1      1      0      0   =>   no                  0
        //    1      1      0      1   =>   no                  0
        //    1      1      1      0   =>   no                  0
        //    1      1      1      1   =>   no                  0
        return select(segments,JSList.of(
                0, 2, 1, 0,
                0, 0, 1, 1,
                0, 2, 0, 2,
                0, 0, 0, 0
        ), buildLog);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSList xor(JSList<JSMap> segments, BuildLog buildLog) { // primary ^ secondary
        // above1 below1 above2 below2    Keep?               Value
        //    0      0      0      0   =>   no                  0
        //    0      0      0      1   =>   yes filled below    2
        //    0      0      1      0   =>   yes filled above    1
        //    0      0      1      1   =>   no                  0
        //    0      1      0      0   =>   yes filled below    2
        //    0      1      0      1   =>   no                  0
        //    0      1      1      0   =>   no                  0
        //    0      1      1      1   =>   yes filled above    1
        //    1      0      0      0   =>   yes filled above    1
        //    1      0      0      1   =>   no                  0
        //    1      0      1      0   =>   no                  0
        //    1      0      1      1   =>   yes filled below    2
        //    1      1      0      0   =>   no                  0
        //    1      1      0      1   =>   yes filled above    1
        //    1      1      1      0   =>   yes filled below    2
        //    1      1      1      1   =>   no                  0
        return select(segments, JSList.of(
                0, 2, 1, 0,
                2, 0, 0, 1,
                1, 0, 0, 2,
                0, 1, 2, 0
        ), buildLog);
    }
}

//module.exports = SegmentSelector;