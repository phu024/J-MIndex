package com.mindex.metrics;

/**
 * Utility class for calculating the Maintainability Index (MI) for Java methods/classes.
 * Uses the normalized MI formula (0–100).
 */
public class MaintainabilityIndexCalculator {
    /**
     * Calculates the Maintainability Index (M-Index) using the normalized formula.
     * MI = MAX(0, (171 - 5.2 * ln(Halstead Volume) - 0.23 * Cyclomatic Complexity - 16.2 * ln(LOC)) * 100 / 171)
     * @param halsteadVolume Halstead Volume (V)
     * @param cyclomaticComplexity Cyclomatic Complexity (CC)
     * @param loc Lines of Code (LOC)
     * @return Maintainability Index (0–100)
     * @throws IllegalArgumentException if any metric is invalid
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