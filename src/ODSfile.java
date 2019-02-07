import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.github.miachm.SODS.spreadsheet.Range;
import com.github.miachm.SODS.spreadsheet.Sheet;
import com.github.miachm.SODS.spreadsheet.SpreadSheet;


public class ODSfile {
	private List<Sheet> list_sheets = null;
	private LinkedHashMap<String, List<String>> header_map = null; 
	
	public ODSfile(String file_path, boolean debug) throws IOException {
		this.list_sheets = new LinkedList<Sheet>();
		this.header_map = new LinkedHashMap<String, List<String>>();
		
        SpreadSheet spread = new SpreadSheet(new File( file_path ));
        if(debug) Log.debug("  Numero de hojas: "+spread.getNumSheets());

        this.list_sheets = spread.getSheets();
        if(this.list_sheets != null) {
        	for(int sheet_i = 0; sheet_i < this.list_sheets.size(); sheet_i++) {
        		Sheet sheet = this.list_sheets.get(sheet_i);
        		if(debug) Log.debug("  Sheet name: \""+sheet.getName()+"\"");
        		
        	    if(sheet != null) {
                	Range rango = sheet.getDataRange();
                	
                	if(debug) Log.debug("    Range.size.rows: "+rango.getNumRows());
                	if(debug) Log.debug("    Range.size.columns: "+rango.getNumColumns());
                	
                    if(rango != null) {
                    	for(int row_j = 0; row_j < rango.getValues().length ; row_j++ ) {
                    		Object[] range_objects = rango.getValues()[row_j];
                    		
                    		List<String> header_list = new LinkedList<String>();
                    		
                    		for(int column_i = 0; column_i < range_objects.length ; column_i++ ) {
                    			Object object = range_objects[column_i];
                    			
                    			if(object != null) {
                    				if(row_j == 0) {
                    					header_list.add(object.toString());
                        			}
                        			if(row_j == 0 && column_i == range_objects.length-1) {
                        				this.header_map.put(sheet.getName(), header_list);
                        			}
                        			
                        			if(debug) Log.debug("      ["+sheet.getName()+"]["+column_i+"]["+row_j+"]"+object);
                        		}else {
                        			if(debug) Log.debug("    Sheet \""+sheet.getName()+"\", in object range, an object is null.  It is ignored.");
                        		}
                    		}
                    	}
                    }else {
                    	Log.error("A sheet has no objects.");
                    }
                }else {
                	Log.error("A sheet is empty.");
                }
        	}
        }else {
        	Log.error("ODS file has no sheets.");
        }
	}
	
	public Object[] getFila() {
		return null;
	}
	
	public List<String> getHeaderBySheetName(String sheet_name) {
		return this.header_map.get(sheet_name);
	}
}
