import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import freemarker.template.TemplateException;

public class SpreadsheetToFilesTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;
  
  public static String ods_file_path = "./examples/test.ods";
  public static String template_directory_path = "./examples/templates/";
  public static String output_directory_path = "./examples/output/";

  @BeforeMethod
  public void setUpStreams() {
	  System.setOut(new PrintStream(outContent));
	  System.setErr(new PrintStream(errContent));
  }
  
  @Test
  public void testOutputContent() {
		SpreadsheetToFiles spreadsheet_to_files = new SpreadsheetToFiles();
		spreadsheet_to_files.setOdsFilePath(ods_file_path);
		spreadsheet_to_files.setTemplatesDirectory(template_directory_path);
		spreadsheet_to_files.setOutputDirectory(output_directory_path);
	  
		String output = "";
		String output_expected = "INSERT INTO PRODUCT(id, units, cost, name) VALUES('M1', 100, 10.0, 'SMART PHONE P');\n"
				+ "  INSERT INTO PRODUCT(id, units, cost, name) VALUES('M2', 200, 20.0, 'SMART PHONE Q');\n"
				+ "  INSERT INTO PRODUCT(id, units, cost, name) VALUES('M3', 300, 30.5, 'SMART PHONE R');\n";
		try {
			output = spreadsheet_to_files.render(false, false);
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		} finally {
			
			assertTrue(output.contains(output_expected),"output was not expected. did you change ods file?");
		}
  }
  
  @Test
  public void testFilesExist() {
	  	SpreadsheetToFiles spreadsheet_to_files = new SpreadsheetToFiles();
		spreadsheet_to_files.setOdsFilePath(ods_file_path);
		spreadsheet_to_files.setTemplatesDirectory(template_directory_path);
		spreadsheet_to_files.setOutputDirectory(output_directory_path);
		
		String output = "";
		try {
			output = spreadsheet_to_files.render(false, true);
		} catch (IOException | TemplateException e) {
			e.printStackTrace();
		} finally {
			
			File template_directory = new File(template_directory_path);
			for(File template_file: template_directory.listFiles()) {
				String template_file_name = template_file.getName();
				
				File output_file = new File(output_directory_path+template_file_name);
				assertTrue(output_file.exists(), "Output file not generated: "+output_directory_path+template_file_name);
			}
		}
  }
  
  @AfterMethod
  public void restoreStreams() {
	  System.setOut(originalOut);
	  System.setErr(originalErr);
  }
}
