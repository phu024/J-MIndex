package com.mindex.model;

public class MethodInfo {
    public String name;
    public double halsteadVolume;
    public int cyclomaticComplexity;
    public int loc;
    public double maintainabilityIndex;

    public MethodInfo(String name, double halsteadVolume, int cyclomaticComplexity, int loc, double maintainabilityIndex) {
        this.name = name;
        this.halsteadVolume = halsteadVolume;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.loc = loc;
        this.maintainabilityIndex = maintainabilityIndex;
    }
} 