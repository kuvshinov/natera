package natera.test.graph;

import java.util.List;

public interface Graph<T> {

    void addVertex(T v);

    boolean addEdge(T first, T second);

    List<T> getPath(T start, T end);
}
