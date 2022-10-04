package org.the3deer.polybool.lib;

// (c) Copyright 2016, Sean Connelly (@voidqk), http://syntheti.cc
// MIT License
// Project Home: https://github.com/voidqk/polybooljs

//
// this is the core work-horse
//

import static org.the3deer.util.javascript.JSMap.of;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.GsonBuilder;

import org.the3deer.polybool.demo.PolyBoolWebInterface;
import org.the3deer.util.function.QuadFunction;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Intersecter {

    boolean selfIntersection;
    BuildLog buildLog;
    Epsilon eps;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Intersecter(final boolean selfIntersection, Epsilon eps, BuildLog buildLog) {
        // selfIntersection is true/false depending on the phase of the overall algorithm
        this.selfIntersection = selfIntersection;
        this.buildLog = buildLog;
        this.eps = eps;
    }


    //
    // segment creation
    //

    JSMap segmentNew(float[] start, float[] end) {
        return of(
                "id", buildLog != null ? buildLog.segmentId() : -1,
                "start", start,
                "end", end,
                "myFill", of(
                        "above", null, // is there fill above us?
                        "below", null // is there fill below us?
                ),
                "otherFill", null
        );
    }

    JSMap segmentCopy(float[] start, float[] end, JSMap seg) {
        return of(
                "id", buildLog != null ? buildLog.segmentId() : -1,
                "start", start,
                "end", end,
                "myFill", (Object) of(
                        "above", seg.gr("myFill.above"),
                        "below", seg.gr("myFill.below")
                ),
                "otherFill", null
        );
    }

    //
    // event logic
    //

    LinkedList event_root = LinkedList.create();

    public int eventCompare(boolean p1_isStart, float[] p1_1, float[] p1_2, boolean p2_isStart, float[] p2_1, float[] p2_2) {
        // compare the selected points first
        int comp = eps.pointsCompare(p1_1, p2_1);
        if (comp != 0)
            return comp;
        // the selected points are the same

        if (eps.pointsSame(p1_2, p2_2)) // if the non-selected points are the same too...
            return 0; // then the segments are equal

        if (p1_isStart != p2_isStart) // if one is a start and the other isn't...
            return p1_isStart ? 1 : -1; // favor the one that isn't the start

        // otherwise, we'll have to calculate which one is below the other manually
        return eps.pointAboveOrOnLine(p1_2,
                p2_isStart ? p2_1 : p2_2, // order matters
                p2_isStart ? p2_2 : p2_1
        ) ? 1 : -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void eventAdd(JSMap ev, float[] other_pt) {
        event_root.insertBefore(ev, (JSMap here) -> {
            // should ev be inserted before here?
            int comp = eventCompare(
                    ev.gb("isStart"), ev.gfa("pt"), other_pt,
                    here.gb("isStart"), here.gfa("pt"), here.gm("other").gfa("pt")
            );
            return comp < 0;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSMap eventAddSegmentStart(JSMap seg, boolean primary) {
        JSMap ev_start = LinkedList.node(JSMap.of(
                "isStart", true,
                "pt", seg.gr("start"),
                "seg", seg,
                "primary", primary,
                "other", null,
                "status", null
        ));
        eventAdd(ev_start, seg.gfa("end"));
        return ev_start;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void eventAddSegmentEnd(JSMap ev_start, JSMap seg, boolean primary) {
        JSMap ev_end = LinkedList.node(of(
                "isStart", false,
                "pt", seg.gr("end"),
                "seg", seg,
                "primary", primary,
                "other", ev_start,
                "status", null
        ));
        ev_start.p("other", ev_end);
        eventAdd(ev_end, ev_start.gfa("pt"));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSMap eventAddSegment(JSMap seg, boolean primary) {
        JSMap ev_start = eventAddSegmentStart(seg, primary);
        eventAddSegmentEnd(ev_start, seg, primary);
        return ev_start;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void eventUpdateEnd(JSMap ev, Object end) {
        // slides an end backwards
        //   (start)------------(end)    to:
        //   (start)---(end)

        if (buildLog != null)
            buildLog.segmentChop(ev.gr("seg"), end);

        ev.gm("other").grn("remove").run();
        ev.pr("seg.end", end);
        ev.pr("other.pt", end);
        eventAdd(ev.gm("other"), ev.gfa("pt"));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSMap eventDivide(JSMap ev, float[] pt) {
        JSMap ns = segmentCopy(pt, ev.gm("seg").gfa("end"), ev.gm("seg"));
        eventUpdateEnd(ev, pt);
        return eventAddSegment(ns, ev.gb("primary"));
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSList<JSMap> calculate(boolean primaryPolyInverted, boolean secondaryPolyInverted) {
        // if selfIntersection is true then there is no secondary polygon, so that isn't used

        //
        // status logic
        //

        LinkedList status_root = LinkedList.create();

        BiFunction<JSMap, JSMap, Integer> statusCompare = (JSMap ev1, JSMap ev2) -> {
            float[] a1 = ev1.gm("seg").gfa("start");
            float[] a2 = ev1.gm("seg").gfa("end");
            float[] b1 = ev2.gm("seg").gfa("start");
            float[] b2 = ev2.gm("seg").gfa("end");

            if (eps.pointsCollinear(a1, b1, b2)) {
                if (eps.pointsCollinear(a2, b1, b2))
                    return 1;//eventCompare(true, a1, a2, true, b1, b2);
                return eps.pointAboveOrOnLine(a2, b1, b2) ? 1 : -1;
            }
            return eps.pointAboveOrOnLine(a1, b1, b2) ? 1 : -1;
        };

        Function<JSMap, JSMap> statusFindSurrounding = (JSMap ev) -> {
            return status_root.findTransition((JSMap here) -> {
                int comp = statusCompare.apply(ev, here.gm("ev"));
                return comp > 0;
            });
        };

        BiFunction<JSMap, JSMap, JSMap> checkIntersection = (JSMap ev1, JSMap ev2) -> {
            // returns the segment equal to ev1, or false if nothing equal

            JSMap seg1 = ev1.gm("seg");
            JSMap seg2 = ev2.gm("seg");
            float[] a1 = seg1.gfa("start");
            float[] a2 = seg1.gfa("end");
            float[] b1 = seg2.gfa("start");
            float[] b2 = seg2.gfa("end");

            if (buildLog != null)
                buildLog.checkIntersection(seg1, seg2);

            Object i_ = eps.linesIntersect(a1, a2, b1, b2);

            if (i_ == Boolean.FALSE) {
                // segments are parallel or coincident

                // if points aren't collinear, then the segments are parallel, so no intersections
                if (!eps.pointsCollinear(a1, a2, b1))
                    return null;
                // otherwise, segments are on top of each other somehow (aka coincident)

                if (eps.pointsSame(a1, b2) || eps.pointsSame(a2, b1))
                    return null; // segments touch at endpoints... no intersection

                boolean a1_equ_b1 = eps.pointsSame(a1, b1);
                boolean a2_equ_b2 = eps.pointsSame(a2, b2);

                if (a1_equ_b1 && a2_equ_b2)
                    return ev2; // segments are exactly equal

                boolean a1_between = !a1_equ_b1 && eps.pointBetween(a1, b1, b2);
                boolean a2_between = !a2_equ_b2 && eps.pointBetween(a2, b1, b2);

                // handy for debugging:
                // buildLog.log({
                //	a1_equ_b1: a1_equ_b1,
                //	a2_equ_b2: a2_equ_b2,
                //	a1_between: a1_between,
                //	a2_between: a2_between
                // });

                if (a1_equ_b1) {
                    if (a2_between) {
                        //  (a1)---(a2)
                        //  (b1)----------(b2)
                        eventDivide(ev2, a2);
                    } else {
                        //  (a1)----------(a2)
                        //  (b1)---(b2)
                        eventDivide(ev1, b2);
                    }
                    return ev2;
                } else if (a1_between) {
                    if (!a2_equ_b2) {
                        // make a2 equal to b2
                        if (a2_between) {
                            //         (a1)---(a2)
                            //  (b1)-----------------(b2)
                            eventDivide(ev2, a2);
                        } else {
                            //         (a1)----------(a2)
                            //  (b1)----------(b2)
                            eventDivide(ev1, b2);
                        }
                    }

                    //         (a1)---(a2)
                    //  (b1)----------(b2)
                    eventDivide(ev2, a1);
                }
            } else {
                // otherwise, lines intersect at i.pt, which may or may not be between the endpoints

                // is A divided between its endpoints? (exclusive)
                JSMap i = (JSMap)eps.linesIntersect(a1, a2, b1, b2);
                if (i.gi("alongA") == 0) {
                    if (i.gi("alongB") == -1) // yes, at exactly b1
                        eventDivide(ev1, b1);
                    else if (i.gi("alongB") == 0) // yes, somewhere between B's endpoints
                        eventDivide(ev1, i.gfa("pt"));
                    else if (i.gi("alongB") == 1) // yes, at exactly b2
                        eventDivide(ev1, b2);
                }

                // is B divided between its endpoints? (exclusive)
                if (i.gi("alongB") == 0) {
                    if (i.gi("alongA") == -1) // yes, at exactly a1
                        eventDivide(ev2, a1);
                    else if (i.gi("alongA") == 0) // yes, somewhere between A's endpoints (exclusive)
                        eventDivide(ev2, i.gfa("pt"));
                    else if (i.gi("alongA") == 1) // yes, at exactly a2
                        eventDivide(ev2, a2);
                }
            }
            return null;
        };

        //
        // main event loop
        //
        JSList<JSMap> segments = new JSList<>();
        while (!event_root.isEmpty()) {
            JSMap ev = event_root.getHead();

            if (buildLog != null)
                buildLog.vert(ev.gfa("pt")[0]);

            if (ev.gb("isStart")) {

                if (buildLog != null)
                    buildLog.segmentNew(ev.gm("seg"), ev.gb("primary"));

                JSMap surrounding = statusFindSurrounding.apply(ev);
                Object above = surrounding.gb("before") ? surrounding.gr("before.ev") : null;
                Object below = surrounding.gb("after") ? surrounding.gr("after.ev") : null;

                if (buildLog != null) {
                    buildLog.tempStatus(
                            ev.gm("seg"),
                            above != null ? ((JSMap)above).gr("seg") : false,
                            below != null ? ((JSMap)below).gr("seg") : false
                    );
                }

                Function<Void,JSMap> checkBothIntersections = (Void v) -> {
                    if (above != null) {
                        JSMap eve = checkIntersection.apply(ev, (JSMap) above);
                        if (eve != null)
                            return eve;
                    }
                    if (below != null)
                        return checkIntersection.apply(ev, (JSMap) below);
                    return null;
                };

                JSMap eve = checkBothIntersections.apply(null);
                if (eve != null) {
                    // ev and eve are equal
                    // we'll keep eve and throw away ev

                    // merge ev.seg's fill information into eve.seg

                    if (selfIntersection) {
                        boolean toggle; // are we a toggling edge?
                        if (ev.gr("seg.myFill.below") == null)
                            toggle = true;
                        else
                            toggle = ev.gr("seg.myFill.above") != ev.gr("seg.myFill.below");

                        // merge two segments that belong to the same polygon
                        // think of this as sandwiching two segments together, where `eve.seg` is
                        // the bottom -- this will cause the above fill flag to toggle
                        if (toggle)
                            eve.pr("seg.myFill.above", !eve.gb("seg.myFill.above"));
                    } else {
                        // merge two segments that belong to different polygons
                        // each segment has distinct knowledge, so no special logic is needed
                        // note that this can only happen once per segment in this phase, because we
                        // are guaranteed that all self-intersections are gone
                        eve.pr("seg.otherFill", ev.gr("seg.myFill"));
                    }

                    if (buildLog != null)
                        buildLog.segmentUpdate(eve.gm("seg"));

                    ev.gm("other").grn("remove").run();
                    ev.grn("remove").run();
                }

                if (event_root.getHead() != ev) {
                    // something was inserted before us in the event queue, so loop back around and
                    // process it before continuing
                    if (buildLog != null)
                        buildLog.rewind(ev.gm("seg"));
                    continue;
                }

                //
                // calculate fill flags
                //
                if (selfIntersection) {
                    boolean toggle; // are we a toggling edge?
                    if (ev.gr("seg.myFill.below") == null) // if we are a new segment...
                        toggle = true; // then we toggle
                    else // we are a segment that has previous knowledge from a division
                        toggle = ev.gb("seg.myFill.above") != ev.gb("seg.myFill.below"); // calculate toggle

                    // next, calculate whether we are filled below us
                    if (below == null) { // if nothing is below us...
                        // we are filled below us if the polygon is inverted
                        ev.pr("seg.myFill.below", primaryPolyInverted);
                    } else {
                        // otherwise, we know the answer -- it's the same if whatever is below
                        // us is filled above itev.gb("seg.myFill.above")
                        ev.pr("seg.myFill.below", ((JSMap)below).gb("seg.myFill.above"));
                    }

                    // since now we know if we're filled below us, we can calculate whether
                    // we're filled above us by applying toggle to whatever is below us
                    if (toggle)
                        ev.pr("seg.myFill.above", !ev.gb("seg.myFill.below"));
                    else
                        ev.pr("seg.myFill.above", ev.gr("seg.myFill.below"));
                } else {
                    // now we fill in any missing transition information, since we are all-knowing
                    // at this point

                    if (ev.gr("seg.otherFill") == null) {
                        // if we don't have other information, then we need to figure out if we're
                        // inside the other polygon
                        Object inside;
                        if (below == null) {
                            // if nothing is below us, then we're inside if the other polygon is
                            // inverted
                            inside =
                                    ev.gb("primary") ? secondaryPolyInverted : primaryPolyInverted;
                        }
                        else { // otherwise, something is below us
                            // so copy the below segment's other polygon's above
                            if (ev.gb("primary") == ((JSMap)below).gb("primary"))
                                inside = ((JSMap)below).gr("seg.otherFill.above");
                            else
                                inside = ((JSMap)below).gr("seg.myFill.above");
                        }
                        ev.pr("seg.otherFill", JSMap.of(
                                "above", inside,
                                "below", inside
                        ));
                    }
                }

                if (buildLog != null) {
                    buildLog.status(
                            ev.gm("seg"),
                            above != null ? ((JSMap)above).gr("seg") : false,
                            below != null ? ((JSMap)below).gr("seg") : false
                    );
                }

                // insert the status and remember it for later removal
                ev.pr("other.status", surrounding.gfn("insert").apply(LinkedList.node(JSMap.of("ev", ev))));
            } else {
                JSMap st = ev.gm("status");

                if (st == null) {

                    GsonBuilder gsonBuilder = PolyBoolWebInterface.getGsonBuilder();
                    /*PolyBool.logd("-----------------------------------------");
                    PolyBool.logd(gsonBuilder.create().toJson(buildLog.list));
                    PolyBool.logd("-----------------------------------------");*/

                    throw new RuntimeException("PolyBool: Zero-length segment detected; your epsilon is " +
                            "probably too small or too large");
                }

                // removing the status will create two new adjacent edges, so we'll need to check
                // for those
                if (status_root.exists(st.gm("prev")) && status_root.exists(st.gm("next")))
                    checkIntersection.apply(st.grm("prev.ev"), st.grm("next.ev"));

                if (buildLog != null)
                    buildLog.statusRemove(st.grm("ev.seg"));

                // remove the status
                st.grn("remove").run();

                // if we've reached this point, we've calculated everything there is to know, so
                // save the segment for reporting
                if (!ev.gb("primary")) {
                    // make sure `seg.myFill` actually points to the primary polygon though
                    Object s = ev.gr("seg.myFill");
                    ev.pr("seg.myFill", ev.gr("seg.otherFill"));
                    ev.pr("seg.otherFill", s);
                }
                segments.push(ev.gm("seg"));
            }

            // remove the event and continue
            event_root.getHead().grn("remove").run();
        }

        if (buildLog != null)
            buildLog.done();

        return segments;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSMap do1() {
        // return the appropriate API depending on what we're doing
        if (!selfIntersection) {
            // performing combination of polygons, so only deal with already-processed segments
            return of(
                    "calculate", (QuadFunction<JSList<JSMap>, Boolean, JSList<JSMap>, Boolean, JSList>) (JSList<JSMap> segments1, Boolean inverted1, JSList<JSMap> segments2, Boolean inverted2) -> {
                        // segmentsX come from the self-intersection API, or this API
                        // invertedX is whether we treat that list of segments as an inverted polygon or not
                        // returns segments that can be used for further operations
                        segments1.forEach((JSMap seg) -> {
                            eventAddSegment(segmentCopy(seg.gfa("start"), seg.gfa("end"), seg), true);
                        });
                        segments2.forEach((JSMap seg) -> {
                            eventAddSegment(segmentCopy(seg.gfa("start"), seg.gfa("end"), seg), false);
                        });
                        return calculate(inverted1, inverted2);
                    }
            );
        }

        // otherwise, performing self-intersection, so deal with regions
        return of(
                "addRegion", (Function<JSList<float[]>, Void>) (JSList<float[]> region) -> {
                    // regions are a list of points:
                    //  [ [0, 0], [100, 0], [50, 100] ]
                    // you can add multiple regions before running calculate
                    float[] pt1;
                    float[] pt2 = region.get(region.length() - 1);
                    for (int i = 0; i < region.length(); i++) {
                        pt1 = pt2;
                        pt2 = region.get(i);

                        int forward = eps.pointsCompare(pt1, pt2);
                        if (forward == 0) // points are equal, so we have a zero-length segment
                            continue; // just skip it

                        eventAddSegment(
                                segmentNew(
                                        forward < 0 ? pt1 : pt2,
                                        forward < 0 ? pt2 : pt1
                                ),
                                true
                        );
                    }
                    return null;
                },
                "calculate", (Function<Boolean, JSList<JSMap>>) (Boolean inverted) -> {
                    // is the polygon inverted?
                    // returns segments
                    return calculate(inverted, false);
                });
    };
}

// module.exports = Intersecter;