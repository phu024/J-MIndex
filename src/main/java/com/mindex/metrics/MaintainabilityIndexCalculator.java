package com.mindex.metrics;

public class MaintainabilityIndexCalculator {
    /**
     * Calculates the Maintainability Index (M-Index) using the classic formula.
     * @param halsteadVolume Halstead Volume (V)
     * @param cyclomaticComplexity Cyclomatic Complexity (CC)
     * @param loc Lines of Code (LOC)
     * @return Maintainability Index
     */
    public static double calculate(double halsteadVolume, int cyclomaticComplexity, int loc) {
        if (halsteadVolume <= 0 || cyclomaticComplexity < 0 || loc <= 0) {
            throw new IllegalArgumentException("Invalid metric values");
        }
        double raw = 171 - 5.2 * Math.log(halsteadVolume) - 0.23 * cyclomaticComplexity - 16.2 * Math.log(loc);
        double normalized = Math.max(0, (raw * 100) / 171);
        return normalized;
    }
} 