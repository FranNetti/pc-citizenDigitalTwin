package android.util;

public class Pair<X,Y> {

    public static<X,Y> Pair<X,Y> create(X first, Y second) {
        return new Pair<X,Y>(first,second);
    }

    public final X first;
    public final Y second;

    private Pair(X x, Y y) {
        this.first = x;
        this.second = y;
    }

}
