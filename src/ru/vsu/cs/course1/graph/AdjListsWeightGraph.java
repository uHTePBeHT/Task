package ru.vsu.cs.course1.graph;

import java.util.*;

public class AdjListsWeightGraph implements WeightedGraph {

    private static class WeightedEdgeTo implements WeightedGraph.WeightedEdgeTo {
        int to;
        double weight;

        public WeightedEdgeTo(int to, double weight) {
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int to() {
            return to;
        }

        @Override
        public double weight() {
            return weight;
        }
    }

    private List<List<WeightedEdgeTo>> vEdjLists = new ArrayList<>();
    private int vCount = 0;
    private int eCount = 0;

    private static Iterable<WeightedEdgeTo> nullIterable = () -> new Iterator<>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public WeightedEdgeTo next() {
            return null;
        }
    };

    @Override
    public int vertexCount() {
        return vCount;
    }

    @Override
    public int edgeCount() {
        return eCount;
    }

    @Override
    public void addEdge(int v1, int v2, double weight) {
        int maxV = Math.max(v1, v2);
        // добавляем вершин в список списков связности
        for (; vCount <= maxV; vCount++) {
            vEdjLists.add(null);
        }
        if (!isAdj(v1, v2)) {
            if (vEdjLists.get(v1) == null) {
                vEdjLists.set(v1, new LinkedList<>());
            }
            vEdjLists.get(v1).add(new WeightedEdgeTo(v2, weight));
            eCount++;
            // для наследников
            if (!(this instanceof Digraph)) {
                if (vEdjLists.get(v2) == null) {
                    vEdjLists.set(v2, new LinkedList<>());
                }
                vEdjLists.get(v2).add(new WeightedEdgeTo(v1, weight));
            }
        }
    }

    @Override
    public void addAdge(int v1, int v2) {
        addEdge(v1, v2, 0);
    }

    private int countingRemove(List<WeightedEdgeTo> list, int v) {
        int count = 0;
        if (list != null) {
            for (Iterator<WeightedEdgeTo> it = list.iterator(); it.hasNext(); ) {
                if (it.next().to == v) {
                    it.remove();
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void removeAdge(int v1, int v2) {
        eCount -= countingRemove(vEdjLists.get(v1), v2);
        if (!(this instanceof Digraph)) {
            eCount -= countingRemove(vEdjLists.get(v2), v1);
        }
    }

    @Override
    public Iterable<WeightedEdgeTo> adjacenciesWithWeights(int v) {
        return vEdjLists.get(v) == null ? nullIterable : vEdjLists.get(v);
    }

    @Override
    public Iterable<Integer> adjacencies(int v) {
        return () -> {
            Iterator<WeightedEdgeTo> wIter = AdjListsWeightGraph.this.adjacenciesWithWeights(v).iterator();

            return new Iterator<>() {
                @Override
                public boolean hasNext() {
                    return wIter.hasNext();
                }

                @Override
                public Integer next() {
                    return wIter.next().to;
                }
            };
        };
    }

    public static class GraphPath {
        private List<Integer> path = new ArrayList<>();

        private double totalWeight;

        void addVertex(Integer vertex) {
            path.add(vertex);
        }

        void updateTotalWeight(double weight) {
            totalWeight += weight;
        }

        public void correctTotalWeight(double weight) {
            totalWeight -= weight;
        }

        public void removeVertex(Integer vertex) {
            path.remove(vertex);
        }

        public GraphPath clone() {
            GraphPath gp = new GraphPath();
            gp.totalWeight = this.totalWeight;
            gp.path = new ArrayList<>(path);
            return gp;
        }

        public int pathLength() {
            return path.size();
        } //длина пути;

        public double getTotalWeight() {
            return totalWeight;
        }

        public List<Integer> getPath() {
            return path;
        }

        public int getLastVertex() {
            return path.get(path.size() - 1);
        }

        public Integer getFirstVertex() {
            return path.get(0);
        }

        public void removeLastVertex() {
            path.remove(path.size() - 1);
        } //удалить последнюю вершину cur PAth

//        @Override
//        public String toString() {
//            return "GraphPath{" +
//                    "path=" + path +
//                    ", totalWeight=" + totalWeight +
//                    '}';
//        }
    }

    public GraphPath findShortestAllVertexPath(double maxWeight) { //нахождение минимального из подходящих гамильт. циклов
        List<GraphPath> allPaths = getAllPaths();
        if (allPaths.size() == 0) {
            return null;
        }

        int minPathIndex = 0;
        for (int i = 1; i < allPaths.size(); i++) {
            if (allPaths.get(i).getTotalWeight() < allPaths.get(minPathIndex).getTotalWeight()) {
                minPathIndex = i;
            }
        }
        GraphPath minPath = allPaths.get(minPathIndex);
        return minPath.getTotalWeight() <= maxWeight ? minPath : null;
    }

    //   21   9
    // 0 - 1 - 2  //30
    // 0 - 1      //30 - 9
    // 0 - 1 - 3  //21 + х

    private List<GraphPath> getAllPaths() { //поиск всех путей
        List<GraphPath> paths = new ArrayList<>();
        for (int i = 0; i < vCount; i++) {
            for (int j = 0; j < vCount; j++) {
                if (i == j) {
                    continue;
                }
                boolean[] isVisited = new boolean[vCount];
                GraphPath graphPath = new GraphPath();
                graphPath.addVertex(i);
                findAllHamiltonianPathsUtil(i, j, isVisited, graphPath, paths);
            }
        }
        return paths;
    }

    private void findAllHamiltonianPathsUtil(Integer v1, Integer v2, boolean[] isVisited, //все гамильтоновы циклы из найденных путей
                                             GraphPath graphPath, List<GraphPath> allPaths) {

        if (v1.equals(v2)) {
            return;
        }

        isVisited[v1] = true;

        for (WeightedEdgeTo edge : vEdjLists.get(v1)) {
            if (!isVisited[edge.to]) {
                graphPath.addVertex(edge.to);
                graphPath.updateTotalWeight(edge.weight);

                if (graphPath.pathLength() == vCount) {
                    Double weight = findWeightBetween(graphPath.getLastVertex(), graphPath.getFirstVertex());
                    graphPath.addVertex(graphPath.getFirstVertex());
                    if (weight != null) {
                        graphPath.updateTotalWeight(weight);
                        allPaths.add(graphPath.clone());
                        graphPath.correctTotalWeight(weight);
                    }
                    graphPath.removeLastVertex();
                }

                findAllHamiltonianPathsUtil(edge.to, v2, isVisited, graphPath, allPaths);
                graphPath.removeVertex(edge.to);
                graphPath.correctTotalWeight(edge.weight);
            }
        }
        isVisited[v1] = false;
    }

    private Double findWeightBetween(int from, int to) { //weight между двумя веришнами
        for (WeightedEdgeTo edge : vEdjLists.get(from)) {
            if (edge.to == to) {
                return edge.weight;
            }
        }
        return null;
    }
}

//class Main {
//    public static void main(String[] args) {
//        AdjListsWeightGraph g = new AdjListsWeightGraph();
//        g.addEdge(0, 1, 5);
//        g.addEdge(0, 2, 12);
//        g.addEdge(0, 3, 3);
//        g.addEdge(1, 2, 4);
//        g.addEdge(1, 3, 16);
//        g.addEdge(2, 3, 7);
//
//        AdjListsWeightGraph.GraphPath gp = g.findShortestAllVertexPath(20);
//        List<Integer> path = gp.getPath();
//        double weight = gp.getTotalWeight();
//        System.out.println(path);
//        System.out.println(weight);
//    }
//}
