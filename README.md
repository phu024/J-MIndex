# J-MIndex (Java Maintainability Index Analyzer)

A professional command-line tool for analyzing Java source code to calculate its Maintainability Index (M-Index) at multiple levels: method, class, package, and project. The tool uses JavaParser to parse source files and computes metrics such as Halstead Volume, Cyclomatic Complexity, and Lines of Code (LOC), then calculates a normalized Maintainability Index (0–100).

## Features
- Analyze all `.java` files in a directory (recursively, excluding test folders)
- Calculate Maintainability Index (M-Index) for each method
- Aggregate and display M-Index at class, package, and project levels
- Export results to CSV (method, class, package, project summary)
- Uses normalized MI formula (0–100)
- Skips files in any `test` directory
- Extensible, testable, and ready for research or production

## Requirements
- Java 11 or higher
- Maven

## Build
```sh
mvn clean package
```
- The output JAR will be in `target/jmi-<version>.jar` (e.g., `jmi-0.0.1.jar`)

## Run
**Analyze and print results:**
```sh
java -jar target/jmi-0.0.1.jar -project <source-directory>
```
**Export results to CSV:**
```sh
java -jar target/jmi-0.0.1.jar -project <source-directory> -out result.csv
```
- Replace `<source-directory>` with the path to your Java source code folder.

## Example Output (Console)
```
--- Maintainability Index Results ---
Class: ExampleClass
  Method: foo | MI: 85.23 | CC: 2 | HV: 23.45 | LOC: 10
  [Class Avg MI: 85.23]

[Package Avg MI]
  Package: com.example | Avg MI: 85.23

[Project Avg MI: 85.23]
```

## Example Output (CSV)
```
Class,Method,MI,CyclomaticComplexity,HalsteadVolume,LOC
ExampleClass,foo,85.23,2,23.45,10
...

Package,AvgMI
com.example,85.23

ProjectAvgMI,AvgMI
ProjectAvgMI,85.23
```

## Metrics Used
- **Maintainability Index (MI):**
  - `MI = MAX(0, (171 - 5.2 * ln(Halstead Volume) - 0.23 * Cyclomatic Complexity - 16.2 * ln(LOC)) * 100 / 171)`
  - Normalized to 0–100 (higher is better)
- **Cyclomatic Complexity (CC):**
  - McCabe's formula: `CC = E - N + 2P` (E=edges, N=nodes, P=connected components)
- **Halstead Volume (HV):**
  - `V = N * log2(n)` (N=total operators+operands, n=distinct operators+operands)
- **Lines of Code (LOC):**
  - Number of lines in each method

## How to Test
- Run `mvn test` to execute unit tests for metric calculations
- Try with open source Java projects and compare with SonarQube, JHawk, or Understand for validation

## Extending/Customizing
- Add new exporters (e.g., JSON) in `ResultExporter`
- Add new metrics in `MetricCalculator`
- Adjust exclusion logic for other folders if needed

## License
See [LICENSE](LICENSE) 