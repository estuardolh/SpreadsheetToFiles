import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import freemarker.template.TemplateException;

public class Main {
	public static final String SPREADSHEET_TO_FILES_VERSION = "1.0";
	
	public static void main(String[] args) {
		// Hello world
		String[] values = processParametersAndReturnValues(args);
		String ods_file_path = values[0];
		String template_directory = values[1];
		String output_directory = values[2];
		Boolean debug_mode = Boolean.parseBoolean(values[3]);
		
		long since = System.currentTimeMillis();
		
		if(! ods_file_path.isEmpty()) {
			SpreadsheetToFiles spreadsheet_to_files = new SpreadsheetToFiles();
			spreadsheet_to_files.setOdsFilePath(ods_file_path);
			spreadsheet_to_files.setTemplatesDirectory( template_directory );
			spreadsheet_to_files.setOutputDirectory(output_directory);
			
			try {
				spreadsheet_to_files.render(debug_mode, true);
				
				long miliseconds_used = (System.currentTimeMillis()-since);
				if(miliseconds_used > 1000) {
					Log.message("Generated in "+(miliseconds_used/1000)+" seconds.");
				}else {
					Log.message("");
					Log.message("Generated in "+miliseconds_used+" miliseconds.");
				}
			} catch (IOException e) {
				Log.error("At input/output.");
				e.printStackTrace();
			} catch (TemplateException e) {
				Log.error("At Template reading.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param args
	 * @return [ODS_FILE_PATH, TEMPLATE_DIRECTORY, OUTPUT_DIRECTORY, DEBUG_MODE]
	 */
	public static String[] processParametersAndReturnValues(String[] args) {
		String[] parameters = new String[] {"", "", "", Boolean.toString(false)};
		
		Option output_option = OptionBuilder.withArgName("output directory")
				.hasArg()
				.withDescription("output directory path")
				.create("o");
		
		Option templates_option = OptionBuilder.withArgName("templates directory")
				.hasArg()
				.withDescription("templates directory path")
				.create("t");
		
		Option debug_option = OptionBuilder
				.withDescription("debug mode on")
				.create("d");
		
		Option version_option = OptionBuilder
				.withDescription("show version")
				.create("version");
		
		Options options = new Options();
		options.addOption(output_option);
		options.addOption(templates_option);
		options.addOption(debug_option);
		options.addOption(version_option);
				
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		}catch(ParseException excepcion) {
			Log.error("At parsing line.");
			excepcion.printStackTrace();
		}
		
		HelpFormatter help_formatter = new HelpFormatter();
		
		if(args.length == 0) {
			help_formatter.printHelp("java -jar SpreadsheetToFiles-"+SPREADSHEET_TO_FILES_VERSION+".jar <input ods file> -t <templates directory> -o <output directory> [ -d ]", options);
		}
		
		if(line.getArgs().length > 0) {
			parameters[0] = line.getArgs()[0];
		}
		if(line.hasOption("t")) {
			parameters[1] = line.getOptionValue("t");
		}
		if(line.hasOption("o")) {
			parameters[2] = line.getOptionValue("o");
		}
		
		if(line.hasOption("d")) {
			parameters[3] = Boolean.toString(true);
		}
		
		if(line.hasOption("version")) {
			Log.message("SpreadsheetToFiles Version "+SPREADSHEET_TO_FILES_VERSION);
		}
		
		INIConfiguration ini_configuration = getIniConfiguration("configuration.ini");
		
		if(ini_configuration != null && !line.hasOption("version") ) {
			parameters[0] = (line.getArgs().length == 0?ini_configuration.getString("main.ods_file_path"):line.getArgs()[0]);
			parameters[1] = (!line.hasOption("t")?ini_configuration.getString("main.templates_directory_path"):line.getOptionValue("t"));
			parameters[2] = (!line.hasOption("o")?ini_configuration.getString("main.output_directory_path"):line.getOptionValue("o"));
		}
		
		return parameters;
	}
	
	public static INIConfiguration getIniConfiguration(String ods_file_path) {
		File ods_file = new File(ods_file_path);
		INIConfiguration ini_configuration = null;
		if(ods_file.exists()) {
			Configurations configurations = new Configurations();
			try {
				ini_configuration = configurations.ini(ods_file_path);
			} catch (ConfigurationException e) {
				Log.error("At reading "+ods_file_path+".");
				e.printStackTrace();
			}
		}
		
		return ini_configuration;
	}
}
