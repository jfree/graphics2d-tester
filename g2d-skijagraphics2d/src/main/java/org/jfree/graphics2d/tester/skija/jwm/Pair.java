package org.jfree.graphics2d.tester.skija.jwm;

public class Pair<A, B> {

    public final A _first;
    public final B _second;

    public Pair(A first, B second) {
        this._first = first;
        this._second = second;
    }

    public A getFirst() {
        return _first;
    }

    public B getSecond() {
        return _second;
    }

}
