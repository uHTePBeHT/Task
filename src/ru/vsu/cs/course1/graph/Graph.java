package ru.vsu.cs.course1.graph;

import java.util.ArrayList;

/**
 * Интерфейс для описания неориентированного графа (н-графа)
 * с реализацией некоторых методов графа
 */
public interface Graph {
    /**
     * Кол-во вершин в графе
     * @return
     */
    int vertexCount();

    /**
     * Кол-во ребер в графе
     * @return
     */
    int edgeCount();

    /**
     * Добавление ребра между вершинами с номерами v1 и v2
     * @param v1
     * @param v2
     */
    void addAdge(int v1, int v2);

    /**
     * Удаление ребра/ребер между вершинами с номерами v1 и v2
     * @param v1
     * @param v2
     */
    void removeAdge(int v1, int v2);

    /**
     * @param v Номер вершины, смежные с которой необходимо найти
     * @return Объект, поддерживающий итерацию по номерам связанных с v вершин
     */
    Iterable<Integer> adjacencies(int v);

    /**
     * Проверка смежности двух вершин
     * @param v1
     * @param v2
     * @return
     */
    default boolean isAdj(int v1, int v2) {
        for (Integer adj : adjacencies(v1)) {
            if (adj == v2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получение dot-описяния графа (для GraphViz)
     * @return
     */
    default String toDot() {
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        boolean isDigraph = this instanceof Digraph;
        sb.append(isDigraph ? "digraph" : "strict graph").append(" {").append(nl);
        for (int v1 = 0; v1 < vertexCount(); v1++) {
            int count = 0;
            for (Integer v2 : this.adjacencies(v1)) {
                sb.append(String.format("  %d %s %d", v1, (isDigraph ? "->" : "--"), v2)).append(nl);
                count++;
            }
            if (count == 0) {
                sb.append(v1).append(nl);
            }
        }
        sb.append("}").append(nl);

        return sb.toString();
    }

    default String toDotWithPath(ArrayList<Integer> path) {
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        boolean isDigraph = this instanceof Digraph;
        sb.append(isDigraph ? "digraph" : "strict graph").append(" {").append(nl);
        for (int v1 = 0; v1 < vertexCount(); v1++) {
            int count = 0;
            for (Integer v2 : this.adjacencies(v1)) {
                sb.append(String.format("  %d %s %d", v1, (isDigraph ? "->" : "--"), v2)).append(nl);
                count++;
            }
            if (count == 0) {
                sb.append(v1).append(nl);
            }
        }

        for(int i = 0; i < path.size(); i++){
            sb.append(path.get(i));
            if(i != path.size()-1){
                sb.append(isDigraph ? "->" : "--");
            }
        }
        sb.append("[color=\"#ff0000\"]").append(nl);

        sb.append("}").append(nl);

        return sb.toString();
    }
}
