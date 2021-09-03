import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class SpreadsheetToFiles {
	private String templates_directory;
	private String ods_file_path;
	private String output_directory;
	
	public SpreadsheetToFiles() {
		this.templates_directory = "";
		this.output_directory = "";
	}
	
	public void setOdsFilePath(String ods_file_path) {
		this.ods_file_path = ods_file_path;
	}
	
	public void setTemplatesDirectory(String templates_directory) {
		this.templates_directory = templates_directory;
	}
	
	public void setOutputDirectory(String output_directory) {
		this.output_directory = output_directory;
	}
	
	private Configuration getTemplatesConfig() throws IOException {
		Configuration configuration = null;
		
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.27) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		configuration = new Configuration(Configuration.VERSION_2_3_27);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		configuration.setDirectoryForTemplateLoading(new File(this.templates_directory));

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		configuration.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		configuration.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
		configuration.setWrapUncheckedExceptions(true);
		
		return configuration;
	}
	
	public String render(boolean debug, boolean write_files) throws IOException, TemplateException {
		Configuration configuration = getTemplatesConfig();
		
		String output = "";
		
		LinkedHashMap<String, List<LinkedHashMap<String, String>>> root = new LinkedHashMap<String, List<LinkedHashMap<String, String>>>();
		
		/*
		 * Read ODS file
		 * */
		SpreadSheet spread = new SpreadSheet(new File( this.ods_file_path ));
		for(Sheet sheet: spread.getSheets()) {
			Range range = sheet.getDataRange();
			if(debug) Log.debug(range.toString());
			
			List<String> header_list = null;
			
			List<LinkedHashMap<String, String>> rows_filled = null;
			Object[] rows = range.getValues();
			int stop_here_i = -1;
			for(int row_j = 0; row_j < rows.length ; row_j++ ) {
				if(debug) Log.debug("  ["+sheet.getName()+"] Row: "+row_j);
				Object[] row = (Object[])rows[row_j];
				
				LinkedHashMap<String, String> row_filling_map = null; 
				if(row_j == 0) {
					header_list = new LinkedList<String>();
					rows_filled = new LinkedList<LinkedHashMap<String, String>>();
				}else {
					row_filling_map = new LinkedHashMap<String, String>();
				}
				
				for(int column_i = 0; column_i < row.length ; column_i++ ) {
					/*if(stop_here_i == 0 && column_i == row.length-1) {
						stop_here_i = column_i;
					}*/
					if(stop_here_i > -1 && column_i == stop_here_i) {
						break;
					}else {
						Object cell = row[column_i];
						
						if(true) {
							if(row_j == 0) {
 								if(cell == null) {
									stop_here_i = column_i;
									break; 
								}
		    					header_list.add(cell.toString());
		    					if(debug) Log.debug("  Header: Column name: \""+(cell==null?"(NULL)":cell.toString())+"\"");
		        			}
		        			
							if(debug) Log.debug("  ["+sheet.getName()+"]["+row_j+"]["+column_i+"] "+(cell==null?"(NULL)":cell.toString()));
							
		        			if(row_j > 0 && column_i < row.length) {
		        				if(header_list == null && debug) Log.debug("Null header");
		        				if(header_list.get(column_i) == null && debug) Log.debug("header-item null");
		        				
		        				row_filling_map.put(header_list.get(column_i), (cell==null?"":cell.toString()));
		        				if(debug) Log.debug("  Row: Map: key-value\""+header_list.get(column_i)+"\"-\""+(cell==null?"(NULL)":cell.toString())+"\"");
		        			}
		        			if( row_j > 0 && (column_i == stop_here_i-1 || column_i == row.length-1)) {
		        				boolean empty_row = true;
		        				for(String cell_value : row_filling_map.values()) {
		        					if(cell_value != null && !cell_value.trim().isEmpty()) {
		        						empty_row = false;
		        						break;
		        					}
		        				}
		        				if(!empty_row) rows_filled.add(row_filling_map);
		        				if(debug) Log.debug("  row filled added!");
		        			}
						}//--
					}//
				}
				
				if(row_j == rows.length-1) {
					if(debug) Log.debug("  sheet: \""+sheet.getName()+"\"");
					root.put(sheet.getName(), rows_filled);
					
				}
			}
			stop_here_i = 0;
		}
		
		/*
		 * Render templates
		 */
		File file = new File(this.templates_directory);
		boolean first = false;
		for(File a_file : file.listFiles()) {
			String file_name = a_file.getName();
			if(debug) Log.debug("  file to render: "+file_name);

			if(!first) {
				Log.message("Generating:");
				first = true;
			}
			
			Log.message("  - "+file_name);
			
			Template template = configuration.getTemplate(file_name);
			
			ByteArrayOutputStream byte_array_oput_stream = new ByteArrayOutputStream();; 
			Writer out = new OutputStreamWriter(byte_array_oput_stream);
			
			template.process(root, out);
			
			out.close();
			
			String renderized = byte_array_oput_stream.toString("UTF-8"); 
			
			String output_path = this.output_directory+file_name;
			File output_file = new File(output_path);
			
			FileUtils.writeStringToFile(output_file, renderized, "UTF-8");
			
			output += renderized;
		}
		Log.message("  ");
		Log.message("  "+file.listFiles().length+" files generated at "+this.output_directory);
		
		return output;
	}
}
