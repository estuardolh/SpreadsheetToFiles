import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import freemarker.template.TemplateException;

public class Main {
    public static final String SPREADSHEET_TO_FILES_VERSION = "1.0";

    record AppConfig(String odsFilePath, String templatesDirectory, String outputDirectory, boolean debugMode) {}

    public static void main(String[] args) {
        var config = processParameters(args);

        if (config.odsFilePath().isEmpty()) return;

        var spreadsheetToFiles = new SpreadsheetToFiles();
        spreadsheetToFiles.setOdsFilePath(config.odsFilePath());
        spreadsheetToFiles.setTemplatesDirectory(config.templatesDirectory());
        spreadsheetToFiles.setOutputDirectory(config.outputDirectory());

        long since = System.currentTimeMillis();

        try {
            spreadsheetToFiles.render(config.debugMode(), true);

            long ms = System.currentTimeMillis() - since;
            if (ms > 1000) {
                Log.message("Generated in " + (ms / 1000) + " seconds.");
            } else {
                Log.message("");
                Log.message("Generated in " + ms + " milliseconds.");
            }
        } catch (IOException e) {
            Log.error("At input/output.");
            e.printStackTrace();
        } catch (TemplateException e) {
            Log.error("At template reading.");
            e.printStackTrace();
        }
    }

    public static AppConfig processParameters(String[] args) {
        var outputOption = Option.builder("o")
                .argName("output directory")
                .hasArg()
                .desc("output directory path")
                .build();

        var templatesOption = Option.builder("t")
                .argName("templates directory")
                .hasArg()
                .desc("templates directory path")
                .build();

        var debugOption = Option.builder("d")
                .desc("debug mode on")
                .build();

        var versionOption = Option.builder("version")
                .desc("show version")
                .build();

        var options = new Options();
        options.addOption(outputOption);
        options.addOption(templatesOption);
        options.addOption(debugOption);
        options.addOption(versionOption);

        CommandLineParser parser = new DefaultParser();
        CommandLine line;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            Log.error("At parsing line.");
            e.printStackTrace();
            return new AppConfig("", "", "", false);
        }

        if (args.length == 0) {
            new HelpFormatter().printHelp(
                "java -jar SpreadsheetToFiles-" + SPREADSHEET_TO_FILES_VERSION + ".jar" +
                " <input ods file> -t <templates directory> -o <output directory> [ -d ]",
                options
            );
        }

        if (line.hasOption("version")) {
            Log.message("SpreadsheetToFiles Version " + SPREADSHEET_TO_FILES_VERSION);
        }

        String odsFilePath  = line.getArgs().length > 0  ? line.getArgs()[0]        : "";
        String templatesDir = line.hasOption("t")        ? line.getOptionValue("t") : "";
        String outputDir    = line.hasOption("o")        ? line.getOptionValue("o") : "";
        boolean debugMode   = line.hasOption("d");

        var ini = getIniConfiguration("configuration.ini");
        if (ini != null && !line.hasOption("version")) {
            if (odsFilePath.isEmpty())  odsFilePath  = ini.getString("main.ods_file_path", "");
            if (templatesDir.isEmpty()) templatesDir = ini.getString("main.templates_directory_path", "");
            if (outputDir.isEmpty())    outputDir    = ini.getString("main.output_directory_path", "");
        }

        return new AppConfig(odsFilePath, templatesDir, outputDir, debugMode);
    }

    public static INIConfiguration getIniConfiguration(String path) {
        var file = new File(path);
        if (!file.exists()) return null;

        try {
            return new Configurations().ini(file);
        } catch (ConfigurationException e) {
            Log.error("At reading " + path + ".");
            e.printStackTrace();
            return null;
        }
    }
}
