package ru.vsu.cs.course1.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Task {

    public static List<Integer> solution(AdjListsWeightGraph graph, double n) {
        AdjListsWeightGraph.GraphPath gr = graph.findShortestAllVertexPath(n);
        List<Integer> path = null;
        if (gr != null) {
            path = gr.getPath();
        } else  {
            return path;
        }
        return path;
    }

}
