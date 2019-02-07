import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

import freemarker.template.TemplateException;

public class Main {
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(new NullAppender());
		
		// Hello world
		String[] values = processParametersAndReturnValues(args);
		String ods_file_path = values[0];
		String template_directory = values[1];
		String output_directory = values[2];
		
		SpreadsheetToFiles spreadsheet_to_files = new SpreadsheetToFiles();
		spreadsheet_to_files.setOdsFilePath(ods_file_path);
		spreadsheet_to_files.setTemplatesDirectory( template_directory );
		spreadsheet_to_files.setOutputDirectory(output_directory);
		
		try {
			spreadsheet_to_files.execute(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param args
	 * @return [ODS_FILE_PATH, TEMPLATE_DIRECTORY, OUTPUT_DIRECTORY]
	 */
	public static String[] processParametersAndReturnValues(String[] args) {
		String[] parameters = new String[3];
		
		Option output_option = OptionBuilder.withArgName("output directory")
				.hasArg()
				.withDescription("output file path")
				.create("o");
		
		Option templates_option = OptionBuilder.withArgName("templates directory")
				.hasArg()
				.withDescription("templates directory")
				.create("t");
		
		Options options = new Options();
		options.addOption(output_option);
		options.addOption(templates_option);
		
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
			help_formatter.printHelp("java -jar SpreadsheetToFiles.jar <input ods file> -t <templates directory> -o <output directory>", options);
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
		
		return parameters;
	}
}
