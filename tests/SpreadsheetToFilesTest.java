import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.testng.annotations.Test;

import freemarker.template.TemplateException;

public class SpreadsheetToFilesTest {
  @Test
  public void testExecute() {
		BasicConfigurator.configure();
		
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(new NullAppender());
	  
		SpreadsheetToFiles spreadsheet_to_files = new SpreadsheetToFiles();
		spreadsheet_to_files.setOdsFilePath("./stage/test.ods");
		spreadsheet_to_files.setTemplatesDirectory("./stage/templates/");
		spreadsheet_to_files.setOutputDirectory("./stage/outputs/");
	  
		String output = "";
		String output_expected = "  INSERT INTO PRODUCT(id, units, cost, name) VALUES('M1', 100, 10.0, 'SMART PHONE P');\n"
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
}
