// MSTBuilder.java
// Implements Kruskal's algorithm to build the Minimum Spanning Tree (MST)
import java.util.*;

public class MSTBuilder {
    public static List<Edge> buildMST(Graph graph) {
        List<Edge> result = new ArrayList<>(); // Final MST result (to store the final road)
        DisjointSet ds = new DisjointSet(); // Union-Find structure to prevent cycles

        // Create a disjoint set for each node ( at first each node btb2a lwa7daha)
        for (int nodeId : graph.nodes.keySet()) {
            ds.makeSet(nodeId);
        }

        // Sort edges by distance
        List<Edge> sortedEdges = new ArrayList<>(graph.edges);
        sortedEdges.sort(Comparator.comparingDouble(e -> e.distance));

        // Kruskal's main loop  ( to choose the roads )
        for (Edge edge : sortedEdges) {
            int rootFrom = ds.find(edge.from);  // find : to check that each 2 nodes at the same set if not
            int rootTo = ds.find(edge.to);      // if not : add the edge to mst and connect the two sets with union

            // If from and to are in different sets, add edge and union them
            if (rootFrom != rootTo) {
                result.add(edge);   // add the dge to MST
                ds.union(edge.from, edge.to);     // connect the two sets with union
            }

            // Early stop if MST is complete (repeat until edges = n-1)
            if (result.size() == graph.nodes.size() - 1) break;
        }

        return result;
    }
}


