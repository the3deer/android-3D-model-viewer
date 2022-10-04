package org.the3deer.polybool.lib;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.the3deer.util.javascript.JSMap;

import java.util.function.Function;

// (c) Copyright 2016, Sean Connelly (@voidqk), http://syntheti.cc
// MIT License
// Project Home: https://github.com/voidqk/polybooljs

//
// simple linked list implementation that allows you to traverse down nodes and save positions
//
// var LinkedList = {
public class LinkedList {

    public static LinkedList create() {
        return new LinkedList();

    }

    final LinkedList my = this;
    final JSMap root = JSMap.of("root", true, "next", null);

    public boolean exists(JSMap node) {
        if (node == null || node == my.root)
            return false;
        return true;
    }

    public boolean isEmpty() {
        return my.root.gr("next") == null;
    }

    public JSMap getHead() {
        return my.root.gm("next");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void insertBefore(JSMap node, Function<JSMap, Boolean> check) {
        JSMap last = my.root;
        JSMap here = my.root.gm("next");
        while (here != null) {
            if (check.apply(here)) {
                node.p("prev", here.gr("prev"));
                node.p("next", here);
                here.gm("prev").put("next", node);
                here.p("prev", node);
                return;
            }
            last = here;
            here = here.gm("next");
        }
        last.p("next", node);
        node.p("prev", last);
        node.p("next", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JSMap findTransition(Function<JSMap, Boolean> check) {
        JSMap prev = my.root;
        JSMap here = my.root.gm("next");
        while (here != null) {
            if (check.apply(here))
                break;
            prev = here;
            here = here.nextm();
        }
        JSMap finalPrev = prev;
        JSMap finalHere = here;
        return JSMap.of(
                "before", prev == my.root ? null : prev,
                "after", here,
                "insert", (Function<JSMap,JSMap>)(JSMap node) -> {
                    node.p("prev", finalPrev);
                    node.p("next", finalHere);
                    finalPrev.p("next", node);
                    if (finalHere != null)
                        finalHere.p("prev", node);
                    return node;
                }
        );
        //return my;
    }

    public static JSMap node(JSMap data) {
        data.p("prev", null);
        data.p("next", null);
        data.p("remove", (Runnable)() -> {
                data.gm("prev").p("next", data.gr("next"));
                if (data.gr("next") != null)
                    data.gm("next").p("prev", data.gr("prev"));
                data.p("prev", null);
                data.p("next", null);
            }
        );
        return data;
    }
}

// module.exports = LinkedList;