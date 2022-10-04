package org.the3deer.polybool.lib;

// (c) Copyright 2016, Sean Connelly (@voidqk), http://syntheti.cc
// MIT License
// Project Home: https://github.com/voidqk/polybooljs

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.the3deer.util.function.TriFunction;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;

import java.util.function.BiFunction;
import java.util.function.Function;

//
// converts a list of segments into a list of regions, while also removing unnecessary verticies
//
public class SegmentChainer {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static JSList regions(JSList<JSMap> segments, Epsilon eps, BuildLog buildLog) {

        JSList<JSList<float[]>> chains = new JSList<>();
        JSList<JSList<float[]>> regions = new JSList<>();

        for (JSMap seg : segments) {
            float[] pt1 = seg.gfa("start");
            float[] pt2 = seg.gfa("end");
            if (eps.pointsSame(pt1, pt2)) {
                Log.w("PolyBool", "Warning: Zero-length segment detected; your epsilon is " +
                        "probably too small or too large");
                continue;
            }

            if (buildLog != null)
                buildLog.chainStart(seg);

            // search for two chains that this segment matches
            JSMap first_match = JSMap.of(
                    "index", 0,
                    "matches_head", false,
                    "matches_pt1", false
            );
            JSMap second_match = JSMap.of(
                    "index", 0,
                    "matches_head", false,
                    "matches_pt1", false
            );
            // JSMap next_match = first_match;
            final JSMap[] next_match = {first_match};

            TriFunction<Integer, Boolean, Boolean, Boolean> setMatch = (Integer index, Boolean matches_head, Boolean matches_pt1) -> {
                // return true if we've matched twice
                next_match[0].p("index", index);
                next_match[0].p("matches_head", matches_head);
                next_match[0].p("matches_pt1", matches_pt1);
                if (next_match[0] == first_match) {
                    next_match[0] = second_match;
                    return false;
                }
                next_match[0] = null;
                return true; // we've matched twice, we're done here
            };

            for (int i = 0; i < chains.length(); i++) {
                JSList<float[]> chain = chains.get(i);
                float[] head = chain.get(0);
                float[] head2 = chain.get(1);
                float[] tail = chain.get(chain.length() - 1);
                float[] tail2 = chain.get(chain.length() - 2);
                if (eps.pointsSame(head, pt1)) {
                    if (setMatch.apply(i, true, true))
                        break;
                } else if (eps.pointsSame(head, pt2)) {
                    if (setMatch.apply(i, true, false))
                        break;
                } else if (eps.pointsSame(tail, pt1)) {
                    if (setMatch.apply(i, false, true))
                        break;
                } else if (eps.pointsSame(tail, pt2)) {
                    if (setMatch.apply(i, false, false))
                        break;
                }
            }

            if (next_match[0] == first_match) {
                // we didn't match anything, so create a new chain
                chains.push(JSList.of(pt1, pt2));
                if (buildLog != null)
                    buildLog.chainNew(pt1, pt2);
                continue;
            }

            if (next_match[0] == second_match) {
                // we matched a single chain

                if (buildLog != null)
                    buildLog.chainMatch(first_match.gi("index"));

                // add the other point to the apporpriate end, and check to see if we've closed the
                // chain into a loop

                int index = first_match.gi("index");
                float[] pt = first_match.gb("matches_pt1") ? pt2 : pt1; // if we matched pt1, then we add pt2, etc
                boolean addToHead = first_match.gb("matches_head"); // if we matched at head, then add to the head

                JSList<float[]> chain = chains.get(index);
                float[] grow = addToHead ? chain.get(0) : chain.get(chain.length() - 1);
                float[] grow2 = addToHead ? chain.get(1) : chain.get(chain.length() - 2);
                float[] oppo = addToHead ? chain.get(chain.length() - 1) : chain.get(0);
                float[] oppo2 = addToHead ? chain.get(chain.length() - 2) : chain.get(1);

                if (eps.pointsCollinear(grow2, grow, pt)) {
                    // grow isn't needed because it's directly between grow2 and pt:
                    // grow2 ---grow---> pt
                    if (addToHead) {
                        if (buildLog != null)
                            buildLog.chainRemoveHead(first_match.gi("index"), pt);
                        chain.shift();
                    } else {
                        if (buildLog != null)
                            buildLog.chainRemoveTail(first_match.gi("index"), pt);
                        chain.pop();
                    }
                    grow = grow2; // old grow is gone... new grow is what grow2 was
                }

                if (eps.pointsSame(oppo, pt)) {
                    // we're closing the loop, so remove chain from chains
                    chains.splice(index, 1);

                    if (eps.pointsCollinear(oppo2, oppo, grow)) {
                        // oppo isn't needed because it's directly between oppo2 and grow:
                        // oppo2 ---oppo--->grow
                        if (addToHead) {
                            if (buildLog != null)
                                buildLog.chainRemoveTail(first_match.gi("index"), grow);
                            chain.pop();
                        } else {
                            if (buildLog != null)
                                buildLog.chainRemoveHead(first_match.gi("index"), grow);
                            chain.shift();
                        }
                    }

                    if (buildLog != null)
                        buildLog.chainClose(first_match.gi("index"));

                    // we have a closed chain!
                    regions.push(chain);
                    continue;
                }

                // not closing a loop, so just add it to the apporpriate side
                if (addToHead) {
                    if (buildLog != null)
                        buildLog.chainAddHead(first_match.gi("index"), pt);
                    chain.unshift(pt);
                } else {
                    if (buildLog != null)
                        buildLog.chainAddTail(first_match.gi("index"), pt);
                    chain.push(pt);
                }
                continue;
            }

            // otherwise, we matched two chains, so we need to combine those chains together

            Function<Integer, Void> reverseChain = (Integer index) -> {
                if (buildLog != null)
                    buildLog.chainReverse(index);
                chains.get(index).reverse(); // gee, that's easy
                return null;
            };


            BiFunction<Integer, Integer, Void> appendChain = (Integer index1, Integer index2) -> {
                // index1 gets index2 appended to it, and index2 is removed
                JSList<float[]> chain1 = chains.get(index1);
                JSList<float[]> chain2 = chains.get(index2);
                float[] tail = chain1.get(chain1.length() - 1);
                float[] tail2 = chain1.get(chain1.length() - 2);
                float[] head = chain2.get(0);
                float[] head2 = chain2.get(1);

                if (eps.pointsCollinear(tail2, tail, head)) {
                    // tail isn't needed because it's directly between tail2 and head
                    // tail2 ---tail---> head
                    if (buildLog != null)
                        buildLog.chainRemoveTail(index1, tail);
                    chain1.pop();
                    tail = tail2; // old tail is gone... new tail is what tail2 was
                }

                if (eps.pointsCollinear(tail, head, head2)) {
                    // head isn't needed because it's directly between tail and head2
                    // tail ---head---> head2
                    if (buildLog != null)
                        buildLog.chainRemoveHead(index2, head);
                    chain2.shift();
                }

                if (buildLog != null)
                    buildLog.chainJoin(index1, index2);
                chains.set(index1, chain1.concat(chain2));
                chains.splice(index2, 1);

                return null;
            };

            int F = first_match.gi("index");
            int S = second_match.gi("index");

            if (buildLog != null)
                buildLog.chainConnect(F, S);

            boolean reverseF = chains.get(F).length() < chains.get(S).length(); // reverse the shorter chain, if needed
            if (first_match.gb("matches_head")) {
                if (second_match.gb("matches_head")) {
                    if (reverseF) {
                        // <<<< F <<<< --- >>>> S >>>>
                        reverseChain.apply(F);
                        // >>>> F >>>> --- >>>> S >>>>
                        appendChain.apply(F, S);
                    } else {
                        // <<<< F <<<< --- >>>> S >>>>
                        reverseChain.apply(S);
                        // <<<< F <<<< --- <<<< S <<<<   logically same as:
                        // >>>> S >>>> --- >>>> F >>>>
                        appendChain.apply(S, F);
                    }
                } else {
                    // <<<< F <<<< --- <<<< S <<<<   logically same as:
                    // >>>> S >>>> --- >>>> F >>>>
                    appendChain.apply(S, F);
                }
            } else {
                if (second_match.gb("matches_head")) {
                    // >>>> F >>>> --- >>>> S >>>>
                    appendChain.apply(F, S);
                } else {
                    if (reverseF) {
                        // >>>> F >>>> --- <<<< S <<<<
                        reverseChain.apply(F);
                        // <<<< F <<<< --- <<<< S <<<<   logically same as:
                        // >>>> S >>>> --- >>>> F >>>>
                        appendChain.apply(S, F);
                    } else {
                        // >>>> F >>>> --- <<<< S <<<<
                        reverseChain.apply(S);
                        // >>>> F >>>> --- >>>> S >>>>
                        appendChain.apply(F, S);
                    }
                }
            }
        }
        return regions;
    }
}

//module.exports = SegmentChainer;