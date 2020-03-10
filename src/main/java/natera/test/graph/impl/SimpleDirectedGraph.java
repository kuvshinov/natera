package natera.test.graph.impl;

import natera.test.graph.AbstractGraph;
import natera.test.graph.model.Edge;

import java.util.List;
import java.util.Objects;

public class SimpleDirectedGraph<T> extends AbstractGraph<T> {

    public boolean addEdge(T first, T second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        lock.lock();
        try {
            checkVertexExists(first);
            checkVertexExists(second);
            List<Edge<T>> edges = graph.get(first);
            for (Edge<T> edge : edges) {
                if (Objects.equals(edge.getTo(), second)) {
                    return false;
                }
            }
            edges.add(Edge.of(first, second));
            return true;
        } finally {
            lock.unlock();
        }
    }
}
