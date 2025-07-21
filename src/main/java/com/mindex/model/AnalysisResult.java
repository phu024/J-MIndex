package com.mindex.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Data class for holding all analysis results for a project.
 * Contains method-level, class-level, package-level, and project-level metrics.
 */
public class AnalysisResult {
    /** Map of class name to list of method metrics */
    public final Map<String, ArrayList<MethodInfo>> classMethods;
    /** Map of package name to list of method metrics */
    public final Map<String, ArrayList<MethodInfo>> packageMethods;
    /** List of all method metrics in the project */
    public final ArrayList<MethodInfo> allMethods;

    /**
     * Constructor for AnalysisResult.
     * @param classMethods Map of class name to list of method metrics
     * @param packageMethods Map of package name to list of method metrics
     * @param allMethods List of all method metrics in the project
     */
    public AnalysisResult(Map<String, ArrayList<MethodInfo>> classMethods,
                         Map<String, ArrayList<MethodInfo>> packageMethods,
                         ArrayList<MethodInfo> allMethods) {
        this.classMethods = classMethods;
        this.packageMethods = packageMethods;
        this.allMethods = allMethods;
    }
} 