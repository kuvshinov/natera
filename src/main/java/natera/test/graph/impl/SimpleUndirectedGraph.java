package natera.test.graph.impl;

import natera.test.graph.AbstractGraph;
import natera.test.graph.model.Edge;

import java.util.List;
import java.util.Objects;

public class SimpleUndirectedGraph<T> extends AbstractGraph<T> {

    public boolean addEdge(T first, T second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        lock.lock();
        try {
            checkVertexExists(first);
            checkVertexExists(second);
            List<Edge<T>> edgesFrom = graph.get(first);
            for (Edge<T> edge : edgesFrom) {
                if (Objects.equals(edge.getTo(), second)) {
                    return false;
                }
            }
            edgesFrom.add(Edge.of(first, second));
            graph.get(second).add(Edge.of(second, first));
            return true;
        } finally {
            lock.unlock();
        }
    }
}
