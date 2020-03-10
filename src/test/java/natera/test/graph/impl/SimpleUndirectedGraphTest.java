package natera.test.graph.impl;

import natera.test.graph.Graph;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class SimpleUndirectedGraphTest {

    private Graph<Integer> graph;

    @Before
    public void setUp() {
        graph = new SimpleUndirectedGraph<>();
    }

    @Test
    public void shouldFindPathInSimpleGraph() {
        //given
        IntStream.range(1, 6).forEach(v -> graph.addVertex(v));
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);
        Integer start = 2, end = 5;

        //when
        List<Integer> result = graph.getPath(start, end);

        //then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(Arrays.asList(2, 4 , 5)));
    }

    @Test
    public void shouldReturnEmptyPath() {
        //given
        IntStream.range(1, 7).forEach(v -> graph.addVertex(v));
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);

        Integer start = 2, end = 6;

        //when
        List<Integer> result = graph.getPath(start, end);

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnPath() {
        //given
        IntStream.range(1, 9).forEach(v -> graph.addVertex(v));
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 2);
        graph.addEdge(2, 4);
        graph.addEdge(3, 5);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(2, 6);
        graph.addEdge(6, 7);
        graph.addEdge(6, 8);

        Integer start = 2, end = 5;

        //when
        List<Integer> result = graph.getPath(start, end);

        //then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertTrue(result.containsAll(Arrays.asList(2, 6, 4, 5)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException_whenAddEdgeToNonExistingVertex() {
        //given
        Integer first = 1;
        Integer second = 2;

        //when
        graph.addEdge(first, second);
        //then
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException_whenTryToFindPathBetweenNonExistingVertexes() {
        //given
        Integer start = 1;
        Integer end = 2;

        //when
        graph.getPath(start, end);
        //then
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowException_whenAddNullAsVertex() {
        graph.addVertex(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowException_whenAddFirstNullToEdge() {
        graph.addEdge(null, 1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowException_whenAddSecondNullToEdge() {
        graph.addEdge(1, null);
    }

    @Test
    public void shouldAddVertexAndEdgeConcurrently() {
        final int amountOfEdges = 1000;
        final int amountOfThreads = 10;
        AtomicInteger edgeCounter = new AtomicInteger();
        Runnable assertion = () -> {
            assertEquals(amountOfEdges - 1, edgeCounter.get());

            List<Integer> result = graph.getPath(1, amountOfEdges);
            assertNotNull(result);
            assertEquals(amountOfEdges, result.size());
            assertTrue(result.containsAll(IntStream.range(1, amountOfEdges + 1).boxed().collect(Collectors.toList())));
        };

        CyclicBarrier barrier = new CyclicBarrier(amountOfThreads, assertion);

        Runnable scenario = () -> {
            for(int i = 1; i <= amountOfEdges; i++) {
                graph.addVertex(i);
                if (i > 1) {
                    if (graph.addEdge(i - 1, i)) {
                        edgeCounter.incrementAndGet();
                    }
                }
            }
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < amountOfThreads; i++) {
            new Thread(scenario).start();
        }
    }
}