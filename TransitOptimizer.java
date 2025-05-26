// TransitOptimizer.java
// Optimizes the allocation of limited buses to bus routes using dynamic programming

import java.util.*;

public class TransitOptimizer {

    /**
     * This method selects the best combination of bus routes that maximizes the number of passengers served,
     * given a fixed total number of available buses.
     *
     * It uses a 0/1 Knapsack-like dynamic programming approach where:
     * - items = bus routes
     * - weight = number of buses required for a route
     * - value = number of passengers served by that route
     */
    public static List<BusRoute> optimizeBusAllocation(List<BusRoute> routes, int totalBuses) {
        int n = routes.size();  // Total number of routes available

        // dp[i][j] stores the maximum number of passengers that can be served using j buses
        // from the first i routes (1-based indexing)
        int[][] dp = new int[n + 1][totalBuses + 1];

        // Fill the dynamic programming table
        for (int i = 1; i <= n; i++) {
            BusRoute route = routes.get(i - 1);  // Get the (i-1)th route (0-based indexing in list)
            for (int j = 0; j <= totalBuses; j++) {
                // If we have enough buses to allocate to this route
                if (route.busesAssigned <= j) {
                    // Option 1: Skip this route → dp[i - 1][j]
                    // Option 2: Take this route → dp[i - 1][j - busesRequired] + passengers
                    // Take the maximum of both choices
                    dp[i][j] = Math.max(
                            dp[i - 1][j],
                            dp[i - 1][j - route.busesAssigned] + route.passengers
                    );
                } else {
                    // Not enough buses for this route, so skip it
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        // Backtrack from dp[n][totalBuses] to find which routes were selected
        List<BusRoute> selected = new ArrayList<>();
        int remainingBuses = totalBuses;

        // Go backwards through the routes to identify included ones
        for (int i = n; i > 0 && remainingBuses > 0; i--) {
            // If the value came from taking the current route
            if (dp[i][remainingBuses] != dp[i - 1][remainingBuses]) {
                BusRoute route = routes.get(i - 1); // This route was included
                selected.add(route);               // Add it to the selected list
                remainingBuses -= route.busesAssigned; // Subtract used buses
            }
        }

        // Return the list of selected routes that maximize passengers served
        return selected;
    }
}