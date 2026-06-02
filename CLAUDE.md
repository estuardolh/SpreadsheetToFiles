# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run all tests
mvn test

# Build executable fat JAR (output: target/SpreadsheetToFiles-1.0.jar)
mvn package

# Run the tool directly
java -jar target/SpreadsheetToFiles-1.0.jar <input.ods> -t <templates-dir/> -o <output-dir/> [-d]

# Run with configuration.ini instead of CLI args
java -jar target/SpreadsheetToFiles-1.0.jar
```

## Architecture

This is a Maven Java 8 project with three source files in `src/` and two test files in `tests/`.

**Core flow:**
1. `Main.java` — CLI entry point using Apache Commons CLI. Parses args (`-t`, `-o`, `-d`, `-version`) and falls back to `configuration.ini` (Apache Commons Configuration2 INI format) for any missing values.
2. `SpreadsheetToFiles.java` — Core logic. Reads an ODS spreadsheet via SODS, builds a FreeMarker data model, then renders every file in the templates directory against that model and writes results to the output directory.
3. `Log.java` — Thin wrapper: `Log.message()` → stdout, `Log.error()`/`Log.debug()` → stderr with `[error]`/`[debug]` prefixes.

**Data model passed to FreeMarker templates:**  
Each ODS sheet becomes a top-level key in the model, named after the sheet. Its value is a list of maps where each map's keys are the column headers (row 0) and values are string cell values. Empty rows are skipped. Example: a sheet named `products` is accessed in a template as `<#list products as product>`.

**Template system:**  
FreeMarker 2.3.27. Templates live in the directory passed via `-t`. Each template file is rendered and written to the output directory with the same filename. See `examples/templates/` for working JSON, SQL, XML, and YAML examples.

**configuration.ini** (optional, loaded from working directory):
```ini
[main]
ods_file_path=./examples/test.ods
templates_directory_path=./examples/templates/
output_directory_path=./examples/output/
```
CLI arguments override INI values when both are present.

**Tests** use TestNG and run against `examples/test.ods` with the templates in `examples/templates/`, writing output to `examples/output/`. The `write_files` boolean parameter on `SpreadsheetToFiles.render()` controls whether files are actually written (set to `false` in content-only tests).
