package com.mindex.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnalysisResult {
    public final Map<String, ArrayList<MethodInfo>> classMethods;
    public final Map<String, ArrayList<MethodInfo>> packageMethods;
    public final ArrayList<MethodInfo> allMethods;

    public AnalysisResult(Map<String, ArrayList<MethodInfo>> classMethods,
                         Map<String, ArrayList<MethodInfo>> packageMethods,
                         ArrayList<MethodInfo> allMethods) {
        this.classMethods = classMethods;
        this.packageMethods = packageMethods;
        this.allMethods = allMethods;
    }
} 