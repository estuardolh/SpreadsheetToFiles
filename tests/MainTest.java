import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.testng.annotations.Test;

public class MainTest {
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
	  
	  outContent.reset();
	  errContent.reset();
  }
  
  @Test
  public void testDebug() {
	  Main.main(new String[] {ods_file_path,"-t",template_directory_path, "-o", output_directory_path});
	  assertFalse(errContent.toString().contains("[debug]"), "[debug] tag found.");
  }
  
  @Test
  public void testNoDebug() {
	  Main.main(new String[] {ods_file_path,"-t",template_directory_path, "-o", output_directory_path,"-d"});
	  assertTrue(errContent.toString().contains("[debug]"), "[debug] tag not found.");
  }
  
  @Test
  public void testVersion() {
	  Main.main(new String[] {"-version"});
	  String out_content = outContent.toString();
	  
	  assertNotEquals(out_content.trim(), "");
  }
  
  @AfterMethod
  public void restoreStreams() {
	  System.setOut(originalOut);
	  System.setErr(originalErr);
  } 
}
