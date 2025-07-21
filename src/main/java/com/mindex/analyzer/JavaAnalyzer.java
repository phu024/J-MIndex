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
import com.github.javaparser.ast.stmt.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import com.mindex.metrics.MetricCalculator;
import com.mindex.export.ResultExporter;
import com.mindex.model.AnalysisResult;

public class JavaAnalyzer {
    private final Map<String, ArrayList<MethodInfo>> classMethods = new HashMap<>();
    private final ArrayList<MethodInfo> allMethods = new ArrayList<>();
    private final Map<String, ArrayList<MethodInfo>> packageMethods = new HashMap<>();

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

    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        // McCabe: CC = E - N + 2P
        // N = number of statements
        // E = N - 1 + number of branch points
        // P = 1 (for a single method)
        int numStatements = method.findAll(Statement.class).size();
        int numBranches = 0;
        numBranches += method.findAll(com.github.javaparser.ast.stmt.IfStmt.class).size();
        numBranches += method.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
        numBranches += method.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
        numBranches += method.findAll(com.github.javaparser.ast.stmt.DoStmt.class).size();
        numBranches += method.findAll(com.github.javaparser.ast.stmt.SwitchEntry.class).size();
        numBranches += method.findAll(com.github.javaparser.ast.stmt.CatchClause.class).size();
        int N = numStatements;
        int E = N - 1 + numBranches;
        int P = 1;
        int cc = E - N + 2 * P;
        return cc;
    }

    private double calculateHalsteadVolume(MethodDeclaration method) {
        // Halstead Volume: V = N * log2(n)
        Set<String> operators = new HashSet<>();
        Set<String> operands = new HashSet<>();
        AtomicInteger total = new AtomicInteger(0);

        method.walk(node -> {
            // Operators
            if (node instanceof com.github.javaparser.ast.expr.BinaryExpr) {
                operators.add(((com.github.javaparser.ast.expr.BinaryExpr) node).getOperator().asString());
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.expr.UnaryExpr) {
                operators.add(((com.github.javaparser.ast.expr.UnaryExpr) node).getOperator().asString());
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.expr.AssignExpr) {
                operators.add("=");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.IfStmt) {
                operators.add("if");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.ForStmt) {
                operators.add("for");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.WhileStmt) {
                operators.add("while");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.DoStmt) {
                operators.add("do");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.SwitchStmt) {
                operators.add("switch");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.SwitchEntry) {
                operators.add("case");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.CatchClause) {
                operators.add("catch");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.BreakStmt) {
                operators.add("break");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.ContinueStmt) {
                operators.add("continue");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.ReturnStmt) {
                operators.add("return");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.ThrowStmt) {
                operators.add("throw");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.stmt.TryStmt) {
                operators.add("try");
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.expr.MethodCallExpr) {
                operators.add("call");
                total.incrementAndGet();
            }
            // Operands
            if (node instanceof com.github.javaparser.ast.expr.NameExpr) {
                operands.add(((com.github.javaparser.ast.expr.NameExpr) node).getNameAsString());
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.expr.SimpleName) {
                operands.add(((com.github.javaparser.ast.expr.SimpleName) node).asString());
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.expr.LiteralExpr) {
                operands.add(node.toString());
                total.incrementAndGet();
            } else if (node instanceof com.github.javaparser.ast.body.VariableDeclarator) {
                operands.add(((com.github.javaparser.ast.body.VariableDeclarator) node).getNameAsString());
                total.incrementAndGet();
            }
        });
        int n = operators.size() + operands.size();
        if (total.get() == 0 || n == 0) return 1.0;
        return total.get() * (Math.log(n) / Math.log(2));
    }

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

    private void exportResultsToCSV(String outFile) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(outFile)) {
            writer.println("Class,Method,MI,CyclomaticComplexity,HalsteadVolume,LOC");
            for (String className : classMethods.keySet()) {
                ArrayList<MethodInfo> methods = classMethods.get(className);
                for (MethodInfo m : methods) {
                    writer.printf("%s,%s,%.2f,%d,%.2f,%d\n", className, m.name, m.maintainabilityIndex, m.cyclomaticComplexity, m.halsteadVolume, m.loc);
                }
            }
            System.out.println("Results exported to: " + outFile);
        } catch (IOException e) {
            System.err.println("Failed to write output file: " + e.getMessage());
        }
    }
} 