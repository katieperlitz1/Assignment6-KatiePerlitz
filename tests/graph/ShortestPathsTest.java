package graph;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ShortestPathsTest {
    /** The graph example from Prof. Myers's notes. There are 7 vertices labeled a-g, as
     *  described by vertices1. 
     *  Edges are specified by edges1 as triples of the form {src, dest, weight}
     *  where src and dest are the indices of the source and destination
     *  vertices in vertices1. For example, there is an edge from a to d with
     *  weight 15.
     */
    static final String[] vertices1 = { "a", "b", "c", "d", "e", "f", "g" };
    static final int[][] edges1 = {
        {0, 1, 9}, {0, 2, 14}, {0, 3, 15},
        {1, 4, 23},
        {2, 4, 17}, {2, 3, 5}, {2, 5, 30},
        {3, 5, 20}, {3, 6, 37},
        {4, 5, 3}, {4, 6, 20},
        {5, 6, 16}
    };
    static final String[] vertices2 = { "a", "b", "c", "d", "e"};
    static final int[][] edges2 = {
            {0, 1, 1}, {0, 2, 4}, {0, 3, 3},
            {1, 2, 2},
            {2, 3, 3},
            {4, 3, 5}
    };

    static final String[] vertices3 = {"h","i","j","k","l","m"};
    static final int[][] edges3 ={
            {0,1,2},{1,2,1},{2,1,1},
            {1,1,1}, {0,2,5},{1,4,5},
            {5,3,8},{0,4,8}

    };


    static class TestGraph implements WeightedDigraph<String, int[]> {
        int[][] edges;
        String[] vertices;
        Map<String, Set<int[]>> outgoing;

        TestGraph(String[] vertices, int[][] edges) {
            this.vertices = vertices;
            this.edges = edges;
            this.outgoing = new HashMap<>();
            for (String v : vertices) {
                outgoing.put(v, new HashSet<>());
            }
            for (int[] edge : edges) {
                outgoing.get(vertices[edge[0]]).add(edge);
            }
        }
        public Iterable<int[]> outgoingEdges(String vertex) { return outgoing.get(vertex); }
        public String source(int[] edge) { return vertices[edge[0]]; }
        public String dest(int[] edge) { return vertices[edge[1]]; }
        public double weight(int[] edge) { return edge[2]; }
    }
    static TestGraph testGraph1() {
        return new TestGraph(vertices1, edges1);
    }
    static TestGraph testGraph2() {
        return new TestGraph(vertices2, edges2);
    }
    static TestGraph testGraph3(){ return new TestGraph(vertices3,edges3); }


    @Test
    void lectureNotesTest() {
        TestGraph graph = testGraph1();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(50, ssp.getDistance("g"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("g")) {
            sb.append(" " + vertices1[e[0]]);
        }
        sb.append(" g");
        assertEquals("best path: a c e f g", sb.toString());


    }

    // Tests another graph with no cycles. Case with "d" has best path directly from "a". Case with
    // "c" has roundabout best path (not directly from a to c)
    @Test
    void lecture23Graph() {
        TestGraph graph = testGraph2();
        ShortestPaths<String, int[]> ssp = new ShortestPaths<>(graph);
        ssp.singleSourceDistances("a");
        assertEquals(3, ssp.getDistance("d"));
        assertEquals(3, ssp.getDistance("c"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("d")) {
            sb.append(" " + vertices2[e[0]]);
        }
        sb.append(" d");
        assertEquals("best path: a d", sb.toString());
        sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp.bestPath("c")) {
            sb.append(" " + vertices2[e[0]]);
        }
        sb.append(" c");
        assertEquals("best path: a b c", sb.toString());
    }


    //This tests a graph where there is self loops and reversing edges
    //the purpose of this test is to makes sure the code does not enter the self loop as that will never
    // return the shortest path as well as to make sure the vertices are being marked as settled to prevent two
    // vertices with directed paths to each other from going in an endless cycle and loop
    @Test
    void testThree(){
        System.out.println("Test 3");
        TestGraph graph3 = testGraph3();
        System.out.println("Made TestGraph");
        ShortestPaths<String, int[]> ssp3 = new ShortestPaths<>(graph3);
        System.out.println("Made ShortestPaths");
        ssp3.singleSourceDistances("h");
        System.out.println("Called SingleSource");
        assertEquals(7, ssp3.getDistance("l"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp3.bestPath("l")) {
            System.out.println("entered for loop");
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" l");
        assertEquals("best path: h i l", sb.toString());

    }

    //the purpose of this test is to use the same graph with self-loops and paths to check if the shortest
    // distance to one edge is accurately met and the graph does not go in an endless cycles as there exists
    // a self loop on the ending vertex
    @Test
    void testFour(){
        TestGraph graph3 = testGraph3();
        ShortestPaths<String, int[]> ssp3 = new ShortestPaths<>(graph3);
        ssp3.singleSourceDistances("h");
        assertEquals(2, ssp3.getDistance("i"));
        StringBuilder sb = new StringBuilder();
        sb.append("best path:");
        for (int[] e : ssp3.bestPath("i")) {
            System.out.println("entered for loop");
            sb.append(" " + vertices3[e[0]]);
        }
        sb.append(" i");
        assertEquals("best path: h i", sb.toString());

    }

}
