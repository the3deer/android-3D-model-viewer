// (c) Copyright 2016, Sean Connelly (@voidqk), http://syntheti.cc
// MIT License
// Project Home: https://github.com/voidqk/polybooljs

//
// used strictly for logging the processing of the algorithm... only useful if you intend on
// looking under the covers (for pretty UI's or debugging)
//

package org.the3deer.polybool.lib;

import static org.the3deer.util.javascript.JSMap.of;

import org.the3deer.polybool.demo.PolyBoolWebInterface;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSMap;
public class BuildLog {

    final BuildLog my = this;
    Integer nextSegmentId = 0;
    float curVert = 0;


    public BuildLog push(String type, JSMap data) {
        if (data == null){
            my.list.push(of(
                    "type", type));
        } else {
            my.list.push(of(
                    "type", type,
                    "data", PolyBoolWebInterface.fromJSon(PolyBoolWebInterface.toJSon(data)) //  ? JSON.parse(JSON.stringify(data)) : void 0
            ));
        }
        return my;
    }

    public JSList list = new JSList();

    public Integer segmentId() {
        return nextSegmentId++;
    }
    public BuildLog checkIntersection(JSMap seg1, JSMap seg2){
        return push("check", of( "seg1", seg1, "seg2", seg2 ));
    }
    public BuildLog segmentChop(Object seg, Object end){
        push("div_seg", JSMap.of("seg", seg, "pt", end));
        return push("chop", JSMap.of("seg", seg, "pt", end));
    }
    public BuildLog statusRemove(JSMap seg){
        return push("pop_seg", JSMap.of("seg", seg ));
    }
    public BuildLog segmentUpdate(JSMap seg){
        return push("seg_update", JSMap.of("seg", seg ));
    }
    public BuildLog segmentNew(Object seg, boolean primary){
        return push("new_seg", JSMap.of("seg", seg, "primary", primary ));
    }
    public BuildLog segmentRemove(JSMap seg){
        return push("rem_seg", JSMap.of("seg", seg ));
    }
    public BuildLog tempStatus(JSMap seg, Object above, Object below){
        return push("temp_status", JSMap.of("seg", seg, "above", above, "below", below ));
    }
    public BuildLog rewind(JSMap seg){
        return push("rewind", JSMap.of("seg", seg ));
    }
    public BuildLog status(JSMap seg, Object above, Object below){
        return push("status", JSMap.of("seg", seg, "above", above, "below", below ));
    }
    public BuildLog vert(float x){
        if (x == curVert)
            return my;
        curVert = x;
        return push("vert", JSMap.of( "x", x ));
    }
    public BuildLog log(Object data){
        if (!(data instanceof String))
            data = PolyBoolWebInterface.getGsonBuilder().create().toJson(data); // TODO: JSON.stringify(data, false, '  ');
        return push("log", JSMap.of( "txt", data ));
    }
    public BuildLog reset(){
        return push("reset", null);
    }
    public BuildLog selected(JSList segs){
        return push("selected", JSMap.of("seg", segs ));
    }
    public BuildLog chainStart(JSMap seg){ return push("chain_start", JSMap.of("seg", seg ));
    }
    public BuildLog chainRemoveHead(int index, float[] pt){
        return push("chain_rem_head", JSMap.of("index", index, "pt", pt ));
    }
    public BuildLog chainRemoveTail(int index, float[] pt){
        return push("chain_rem_tail", JSMap.of("index", index, "pt", pt ));
    }
    public BuildLog chainNew(float[] pt1, float[] pt2){
        return push("chain_new", JSMap.of("pt1", pt1, "pt2", pt2 ));
    }
    public BuildLog chainMatch(int index){
        return push("chain_match", JSMap.of("index", index ));
    }
    public BuildLog chainClose(int index){
        return push("chain_close", JSMap.of("index", index ));
    }
    public BuildLog chainAddHead(int index, float[] pt){
        return push("chain_add_head", JSMap.of("index", index, "pt", pt));
    }
    public BuildLog chainAddTail(int index, float[] pt){
        return push("chain_add_tail", JSMap.of("index", index, "pt", pt));
    }
    public BuildLog chainConnect(int index1, int index2){
        return push("chain_con", JSMap.of("index1", index1, "index2", index2 ));
    }
    public BuildLog chainReverse(int index){
        return push("chain_rev", JSMap.of("index", index ));
    }
    public BuildLog chainJoin(int index1, int index2){
        return push("chain_join", JSMap.of("index1", index1, "index2", index2 ));
    }
    public BuildLog done(){
        return push("done", null);
    }
    // return my;
}

// module.exports = BuildLog;