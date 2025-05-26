// MaintenanceOptimizer.java
// Selects the most beneficial set of roads to repair within a given budget using dynamic programming

import java.util.*;

public class MaintenanceOptimizer {

    public static List<RoadRepair> optimizeRepairs(List<RoadRepair> repairs, double maxBudget) {
        int n = repairs.size(); // Number of road repair options

        int scale = 10; // Used to convert decimal costs to integers (e.g., 12.5 becomes 125) to simplify DP indexing
        int capacity = (int) (maxBudget * scale); // Convert budget to integer-based capacity

        // Dynamic programming table:
        // dp[i][j] represents the maximum benefit from the first i repairs using j units of budget
        double[][] dp = new double[n + 1][capacity + 1];

        // Fill the DP table
        for (int i = 1; i <= n; i++) {
            RoadRepair repair = repairs.get(i - 1); // Get the i-th road repair
            int costInt = (int) (repair.cost * scale); // Convert cost to integer for DP indexing

            for (int j = 0; j <= capacity; j++) {
                if (costInt <= j) {
                    // Option 1: Don't select this repair → dp[i - 1][j]
                    // Option 2: Select this repair → dp[i - 1][j - cost] + benefit
                    dp[i][j] = Math.max(
                            dp[i - 1][j], // Don't take the current repair
                            dp[i - 1][j - costInt] + repair.benefit // Take it and add its benefit
                    );
                } else {
                    // If current repair's cost exceeds the available budget at j, we skip it
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        // Backtrack to determine which repairs were selected
        List<RoadRepair> selected = new ArrayList<>();
        int remaining = capacity; // Start from full budget and trace back the selected repairs

        for (int i = n; i > 0 && remaining > 0; i--) {
            RoadRepair repair = repairs.get(i - 1);
            int costInt = (int) (repair.cost * scale);

            // If value differs from previous row, it means this repair was selected
            if (dp[i][remaining] != dp[i - 1][remaining]) {
                selected.add(repair); // Add the selected repair to the result
                remaining -= costInt; // Deduct its cost from remaining budget
            }
        }

        // Return the list of selected road repairs within budget and maximizing total benefit
        return selected;
    }
}