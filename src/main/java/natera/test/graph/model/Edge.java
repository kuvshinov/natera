package natera.test.graph.model;

public class Edge<T> {

    private final T from;

    private final T to;

    public Edge(T from, T to) {
        this.from = from;
        this.to = to;
    }

    public T getFrom() {
        return from;
    }

    public T getTo() {
        return to;
    }

    public static <T> Edge<T> of(T from, T to) {
        return new Edge<>(from, to);
    }
}
