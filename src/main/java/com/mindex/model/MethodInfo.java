package com.mindex.model;

/**
 * Data class for holding metrics of a single Java method.
 * Includes Halstead Volume, Cyclomatic Complexity, LOC, and Maintainability Index.
 */
public class MethodInfo {
    /** Method name */
    public String name;
    /** Halstead Volume */
    public double halsteadVolume;
    /** Cyclomatic Complexity */
    public int cyclomaticComplexity;
    /** Lines of Code */
    public int loc;
    /** Maintainability Index */
    public double maintainabilityIndex;

    /**
     * Constructor for MethodInfo.
     * @param name Method name
     * @param halsteadVolume Halstead Volume
     * @param cyclomaticComplexity Cyclomatic Complexity
     * @param loc Lines of Code
     * @param maintainabilityIndex Maintainability Index
     */
    public MethodInfo(String name, double halsteadVolume, int cyclomaticComplexity, int loc, double maintainabilityIndex) {
        this.name = name;
        this.halsteadVolume = halsteadVolume;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.loc = loc;
        this.maintainabilityIndex = maintainabilityIndex;
    }
} 