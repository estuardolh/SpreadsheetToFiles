import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.testng.annotations.Test;

public class MainTest {
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeMethod
  public void setUpStreams() {
	  System.setOut(new PrintStream(outContent));
	  System.setErr(new PrintStream(errContent));
  }
  
  @Test
  public void MainTest() {
	  
	  Main.main(new String[] {"./stage/test.ods","-t","./stage/templates/", "-o", "./stage/outputs/"});
	  assertFalse(errContent.toString().contains("[debug]"), "[debug] tag found.");
	  
	  Main.main(new String[] {"./stage/test.ods","-t","./stage/templates/", "-o", "./stage/outputs/","-d"});
	  assertTrue(errContent.toString().contains("[debug]"), "[debug] tag not found.");
  }
  
  @AfterMethod
  public void restoreStreams() {
	  System.setOut(originalOut);
	  System.setErr(originalErr);
  } 
}
