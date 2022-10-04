package org.the3deer.util.javascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSList<E> extends ArrayList<E> {

    public JSList(List<E> list) {
        super.addAll(list);
    }

    public JSList() {
    }

    public static JSList<float[]> of(float[] pt1, float[] pt2) {
        final JSList ret = new JSList();
        ret.add(pt1);
        ret.add(pt2);
        return ret;
    }

    public static <E> JSList<E> of(E... e) {
        JSList<E> ret = new JSList<E>();
        for (int i=0; i<e.length; i++)
            ret.add(e[i]);
        return ret;
    }

    public int push(E e){
        super.add(e);
        return size();
    }

    public int push(JSList<E> e){
        super.addAll(e);
        return size();
    }

    public float[] gfa(int i){
        return (float[]) super.get(i);
    }

    public int length(){
        return super.size();
    }

    public void splice(int index, int length) {
        for (int i=0; i<length; i++){
            super.remove(index);
        }
    }

    public E pop() {
        if (isEmpty()) return null;
        return super.remove(super.size()-1);
    }

    public E shift() {
        if (isEmpty()) return null;
        return super.remove(0);
    }

    public int unshift(E pt) {
        super.add(0, pt);
        return size();
    }

    public JSList reverse() {
        Collections.reverse(this);
        return this;
    }

    public JSList<E> concat(JSList<E> chain2) {
        JSList<E> ret = new JSList<>();
        ret.addAll(this);
        ret.addAll(chain2);
        return ret;
    }

    // FIXME: fix this!!!!!
    public JSList<E> slice(int from, int end){
        return (JSList<E>) super.subList(from, end);
    }

    public <T> JSList<T> gl(int i) {
        return (JSList<T>) super.get(i);
    }

    public JSMap gm(int i) {
        return (JSMap)super.get(i);
    }
}
