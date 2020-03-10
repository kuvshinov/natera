package natera.test.graph;

import natera.test.graph.model.Edge;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractGraph<T> implements Graph<T> {

    protected Map<T, List<Edge<T>>> graph = new HashMap<>();

    protected ReentrantLock lock = new ReentrantLock();

    public void addVertex(T v) {
        Objects.requireNonNull(v);
        if (!graph.containsKey(v)) {
            lock.lock();
            try {
                if (!graph.containsKey(v)) {
                    graph.put(v, new LinkedList<>());
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public List<T> getPath(T start, T end) {
        lock.lock();
        try {
            checkVertexExists(start);
            checkVertexExists(end);
            Map<T, T> visited = dfs(start, end);
            if (visited.containsKey(end)) {
                return buildPath(visited, end);
            }
            return Collections.emptyList();
        } finally {
            lock.unlock();
        }
    }

    protected void checkVertexExists(T v) {
        if (!graph.containsKey(v)) {
            throw new IllegalArgumentException(String.format("The vertex %s doesn't exist", v.toString()));
        }
    }

    private Map<T, T> dfs(T start, T end) {
        Stack<Edge<T>> stack = new Stack<>();
        Map<T, T> visited = new HashMap<>();
        stack.push(Edge.of(null, start));
        while(!stack.empty()) {
            Edge<T> cur = stack.pop();
            visited.put(cur.getTo(), cur.getFrom());
            if (cur.getTo().equals(end)) {
                break;
            }
            graph.get(cur.getTo()).stream()
                    .filter(e -> !visited.containsKey(e.getTo()))
                    .forEach(stack::push);
        }
        return visited;
    }

    private List<T> buildPath(Map<T, T> visited, T end) {
        LinkedList<T> path = new LinkedList<>();
        path.push(end);
        T prev = visited.get(end);
        while (prev != null) {
            path.push(prev);
            prev = visited.get(prev);
        }
        return path;
    }

}
