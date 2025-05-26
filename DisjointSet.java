// DisjointSet.java
// Union-Find structure used in Kruskal's MST to avoid cycles

import java.util.HashMap;
import java.util.Map;

public class DisjointSet {

    // This map keeps track of the parent of each node
    // Initially, each node is its own parent (separate set)
    private final Map<Integer, Integer> parent = new HashMap<>();

    // Initializes a separate set for a node (set contains only this node)
    public void makeSet(int node) {
        parent.put(node, node); // Assign the node to be its own parent
    }

    // Finds the representative (root) of the set that a node belongs to
    // Uses path compression to flatten the structure and speed up future finds
    public int find(int node) {
        // If the node is not its own parent, recursively find the root
        // and update the parent of this node to point directly to the root
        if (parent.get(node) != node) {
            parent.put(node, find(parent.get(node))); // Path compression step
        }
        return parent.get(node); // Return the root of the set
    }

    // Unites the sets that contain node 'a' and node 'b'
    // It connects the root of one set to the root of the other
    public void union(int a, int b) {
        int rootA = find(a); // Find root of node a
        int rootB = find(b); // Find root of node b

        // If they are not in the same set, merge them
        if (rootA != rootB) {
            parent.put(rootA, rootB); // Connect rootA to rootB (could be arbitrary)
        }
    }
}