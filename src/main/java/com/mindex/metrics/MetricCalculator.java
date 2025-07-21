package com.mindex.metrics;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for calculating code metrics for Java methods.
 * Includes Lines of Code (LOC), Cyclomatic Complexity (CC), and Halstead Volume (HV).
 * All methods are static and operate on JavaParser's MethodDeclaration AST node.
 */
public class MetricCalculator {
    /**
     * Calculates the number of lines of code (LOC) in a method.
     * @param method JavaParser MethodDeclaration node
     * @return Number of lines of code
     */
    public static int calculateLOC(MethodDeclaration method) {
        return (int) method.toString().lines().count();
    }

    /**
     * Calculates Cyclomatic Complexity (CC) using McCabe's formula: CC = E - N + 2P
     * E = edges, N = nodes, P = connected components (usually 1 per method)
     * Decision points include if, for, while, do, switch, catch.
     * @param method JavaParser MethodDeclaration node
     * @return Cyclomatic Complexity
     */
    public static int calculateCyclomaticComplexity(MethodDeclaration method) {
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

    /**
     * Calculates Halstead Volume (HV) for a method.
     * HV = N * log2(n), where N = total operators+operands, n = distinct operators+operands.
     * Operators and operands are extracted from the JavaParser AST.
     * @param method JavaParser MethodDeclaration node
     * @return Halstead Volume
     */
    public static double calculateHalsteadVolume(MethodDeclaration method) {
        Set<String> operators = new HashSet<>();
        Set<String> operands = new HashSet<>();
        AtomicInteger total = new AtomicInteger(0);
        method.walk(node -> {
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
} 