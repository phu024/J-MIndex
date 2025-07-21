package com.mindex.metrics;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetricCalculatorTest {
    private MethodDeclaration parseMethod(String code) {
        return StaticJavaParser.parseMethodDeclaration(code);
    }

    @Test
    void testCalculateLOC() {
        MethodDeclaration method = parseMethod("void foo() { int a = 1; if(a > 0) { a++; } }");
        int loc = MetricCalculator.calculateLOC(method);
        assertTrue(loc >= 3); // at least 3 lines (method, if, statement)
    }

    @Test
    void testCalculateCyclomaticComplexity() {
        MethodDeclaration method = parseMethod("void foo() { if(true) {} else {} for(int i=0;i<1;i++){} }");
        int cc = MetricCalculator.calculateCyclomaticComplexity(method);
        assertTrue(cc >= 3); // 1 (base) + 1 (if) + 1 (for)
    }

    @Test
    void testCalculateHalsteadVolume() {
        MethodDeclaration method = parseMethod("void foo() { int a = 1; a = a + 2; }");
        double hv = MetricCalculator.calculateHalsteadVolume(method);
        assertTrue(hv > 0);
    }
} 