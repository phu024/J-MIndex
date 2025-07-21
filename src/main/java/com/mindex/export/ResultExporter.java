package com.mindex.export;

import com.mindex.model.MethodInfo;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ResultExporter {
    public static void exportResultsToCSV(Map<String, ArrayList<MethodInfo>> classMethods, Map<String, ArrayList<MethodInfo>> packageMethods, ArrayList<MethodInfo> allMethods, String outFile) {
        try (PrintWriter writer = new PrintWriter(outFile)) {
            writer.println("Class,Method,MI,CyclomaticComplexity,HalsteadVolume,LOC");
            for (String className : classMethods.keySet()) {
                ArrayList<MethodInfo> methods = classMethods.get(className);
                for (MethodInfo m : methods) {
                    writer.printf("%s,%s,%.2f,%d,%.2f,%d\n", className, m.name, m.maintainabilityIndex, m.cyclomaticComplexity, m.halsteadVolume, m.loc);
                }
            }
            // Section: Package Avg MI
            writer.println();
            writer.println("Package,AvgMI");
            for (String packageName : packageMethods.keySet()) {
                ArrayList<MethodInfo> methods = packageMethods.get(packageName);
                double pkgMI = methods.stream().mapToDouble(m -> m.maintainabilityIndex).average().orElse(0);
                writer.printf("%s,%.2f\n", packageName, pkgMI);
            }
            // Section: Project Avg MI
            writer.println();
            writer.println("ProjectAvgMI,AvgMI");
            double projectMI = allMethods.stream().mapToDouble(m -> m.maintainabilityIndex).average().orElse(0);
            writer.printf("ProjectAvgMI,%.2f\n", projectMI);
            System.out.println("Results exported to: " + outFile);
        } catch (IOException e) {
            System.err.println("Failed to write output file: " + e.getMessage());
        }
    }
} 