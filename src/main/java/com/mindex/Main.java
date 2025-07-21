package com.mindex;

import com.mindex.analyzer.JavaAnalyzer;
import com.mindex.model.AnalysisResult;
import com.mindex.export.ResultExporter;

public class Main {
    public static void main(String[] args) {
        if (args.length == 1 && "-help".equals(args[0])) {
            printHelp();
            System.exit(0);
        }
        if (args.length != 2 && args.length != 4) {
            printHelp();
            System.exit(1);
        }
        if (!"-project".equals(args[0])) {
            printHelp();
            System.exit(1);
        }
        String sourceDir = args[1];
        String outFile = null;
        if (args.length == 4) {
            if (!"-out".equals(args[2])) {
                printHelp();
                System.exit(1);
            }
            outFile = args[3];
        }
        JavaAnalyzer analyzer = new JavaAnalyzer();
        AnalysisResult result = analyzer.analyzeProject(sourceDir);
        if (outFile != null) {
            ResultExporter.exportResultsToCSV(result.classMethods, result.packageMethods, result.allMethods, outFile);
        } else {
            analyzer.printResults(result);
        }
    }

    /**
     * Print usage/help message for the CLI tool.
     */
    private static void printHelp() {
        System.out.println("J-MIndex - Java Maintainability Index Analyzer");
        System.out.println("Usage:");
        System.out.println("  java -jar <jarfile> -project <source-directory> [-out <output-file>]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -project <source-directory>   Path to Java source code directory");
        System.out.println("  -out <output-file>            (Optional) Export results to CSV file");
        System.out.println("  -help                         Show this help message");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  java -jar jmi-0.0.1.jar -project myproject/src -out result.csv");
    }
} 