package com.mindex;

import com.mindex.analyzer.JavaAnalyzer;
import com.mindex.model.AnalysisResult;
import com.mindex.export.ResultExporter;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 4) {
            System.out.println("Usage: java -jar <jarfile> -project <source-directory> [-out <output-file>]");
            System.exit(1);
        }
        if (!"-project".equals(args[0])) {
            System.out.println("Usage: java -jar <jarfile> -project <source-directory> [-out <output-file>]");
            System.exit(1);
        }
        String sourceDir = args[1];
        String outFile = null;
        if (args.length == 4) {
            if (!"-out".equals(args[2])) {
                System.out.println("Usage: java -jar <jarfile> -project <source-directory> [-out <output-file>]");
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
} 