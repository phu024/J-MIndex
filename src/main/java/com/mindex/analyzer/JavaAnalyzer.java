package com.mindex.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.mindex.metrics.MaintainabilityIndexCalculator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.mindex.model.MethodInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.mindex.metrics.MetricCalculator;
import com.mindex.model.AnalysisResult;

/**
 * Main analyzer class for traversing Java source files, extracting methods/classes/packages,
 * and calculating metrics for Maintainability Index analysis.
 * Stateless: returns all results via AnalysisResult.
 */
public class JavaAnalyzer {
    /**
     * Analyze all Java files in the given source directory (recursively, skipping test folders).
     * @param sourceDir Path to the root of Java source code
     * @return AnalysisResult containing all metrics at method/class/package/project levels
     */
    public AnalysisResult analyzeProject(String sourceDir) {
        Map<String, ArrayList<MethodInfo>> classMethods = new HashMap<>();
        Map<String, ArrayList<MethodInfo>> packageMethods = new HashMap<>();
        ArrayList<MethodInfo> allMethods = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(new File(sourceDir).toPath())) {
            List<Path> javaFiles = paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains("/test/") && !p.toString().contains("\\test\\"))
                    .collect(Collectors.toList());
            for (Path javaFile : javaFiles) {
                analyzeFile(javaFile.toFile(), classMethods, packageMethods, allMethods);
            }
        } catch (IOException e) {
            System.err.println("Error reading source files: " + e.getMessage());
        }
        return new AnalysisResult(classMethods, packageMethods, allMethods);
    }

    /**
     * Parse a Java file and extract metrics for all classes/methods inside.
     * @param file Java source file
     * @param classMethods Map to collect method info by class
     * @param packageMethods Map to collect method info by package
     * @param allMethods List to collect all method info
     */
    private void analyzeFile(File file, Map<String, ArrayList<MethodInfo>> classMethods, Map<String, ArrayList<MethodInfo>> packageMethods, ArrayList<MethodInfo> allMethods) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);
            String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                String className = clazz.getNameAsString();
                ArrayList<MethodInfo> methods = new ArrayList<>();
                clazz.findAll(MethodDeclaration.class).forEach(method -> {
                    MethodInfo info = analyzeMethod(method);
                    if (info != null) {
                        methods.add(info);
                        allMethods.add(info);
                        packageMethods.computeIfAbsent(packageName, k -> new ArrayList<>()).add(info);
                    }
                });
                classMethods.put(className, methods);
            });
        } catch (IOException e) {
            System.err.println("Failed to parse " + file.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Analyze a single method and calculate all metrics (Halstead, CC, LOC, MI).
     * @param method JavaParser MethodDeclaration node
     * @return MethodInfo with all calculated metrics
     */
    private MethodInfo analyzeMethod(MethodDeclaration method) {
        String name = method.getNameAsString();
        int loc = MetricCalculator.calculateLOC(method);
        int cyclomatic = MetricCalculator.calculateCyclomaticComplexity(method);
        double halstead = MetricCalculator.calculateHalsteadVolume(method);
        double mi = 0;
        try {
            mi = MaintainabilityIndexCalculator.calculate(halstead, cyclomatic, loc);
        } catch (Exception e) {
            // skip invalid
        }
        return new MethodInfo(name, halstead, cyclomatic, loc, mi);
    }

    /**
     * Print results to the console at method, class, package, and project levels.
     * @param result AnalysisResult containing all metrics
     */
    public void printResults(AnalysisResult result) {
        Map<String, ArrayList<MethodInfo>> classMethods = result.classMethods;
        Map<String, ArrayList<MethodInfo>> packageMethods = result.packageMethods;
        ArrayList<MethodInfo> allMethods = result.allMethods;
        System.out.println("\n--- Maintainability Index Results ---");
        for (String className : classMethods.keySet()) {
            System.out.println("Class: " + className);
            ArrayList<MethodInfo> methods = classMethods.get(className);
            double classMI = 0;
            for (MethodInfo m : methods) {
                System.out.printf("  Method: %s | MI: %.2f | CC: %d | HV: %.2f | LOC: %d\n", m.name, m.maintainabilityIndex, m.cyclomaticComplexity, m.halsteadVolume, m.loc);
                classMI += m.maintainabilityIndex;
            }
            if (!methods.isEmpty()) {
                classMI /= methods.size();
                System.out.printf("  [Class Avg MI: %.2f]\n", classMI);
            }
        }
        // Package level
        System.out.println("\n[Package Avg MI]");
        for (String packageName : packageMethods.keySet()) {
            ArrayList<MethodInfo> methods = packageMethods.get(packageName);
            double pkgMI = methods.stream().mapToDouble(m -> m.maintainabilityIndex).average().orElse(0);
            System.out.printf("  Package: %s | Avg MI: %.2f\n", packageName, pkgMI);
        }
        // Project level
        double projectMI = allMethods.stream().mapToDouble(m -> m.maintainabilityIndex).average().orElse(0);
        System.out.printf("\n[Project Avg MI: %.2f]\n", projectMI);
    }
} 