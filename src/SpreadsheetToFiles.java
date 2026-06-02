import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private String templatesDirectory;
    private String odsFilePath;
    private String outputDirectory;

    public SpreadsheetToFiles() {
        this.templatesDirectory = "";
        this.outputDirectory = "";
    }

    public void setOdsFilePath(String odsFilePath) {
        this.odsFilePath = odsFilePath;
    }

    public void setTemplatesDirectory(String templatesDirectory) {
        this.templatesDirectory = templatesDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    private Configuration getTemplatesConfig() throws IOException {
        var config = new Configuration(Configuration.VERSION_2_3_27);
        config.setDirectoryForTemplateLoading(new File(this.templatesDirectory));
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        return config;
    }

    public String render(boolean debug, boolean writeFiles) throws IOException, TemplateException {
        var configuration = getTemplatesConfig();
        var output = new StringBuilder();

        var root = new LinkedHashMap<String, List<LinkedHashMap<String, String>>>();

        var spread = new SpreadSheet(new File(this.odsFilePath));
        for (Sheet sheet : spread.getSheets()) {
            Range range = sheet.getDataRange();
            if (debug) Log.debug(range.toString());

            List<String> headers = null;
            List<LinkedHashMap<String, String>> rows = null;

            var rawRows = (Object[]) range.getValues();
            int stopAtColumn = -1;

            for (int rowIdx = 0; rowIdx < rawRows.length; rowIdx++) {
                if (debug) Log.debug("  [" + sheet.getName() + "] Row: " + rowIdx);
                var row = (Object[]) rawRows[rowIdx];

                LinkedHashMap<String, String> rowMap = null;
                if (rowIdx == 0) {
                    headers = new ArrayList<>();
                    rows = new ArrayList<>();
                } else {
                    rowMap = new LinkedHashMap<>();
                }

                for (int colIdx = 0; colIdx < row.length; colIdx++) {
                    if (stopAtColumn > -1 && colIdx == stopAtColumn) break;

                    Object cell = row[colIdx];

                    if (rowIdx == 0) {
                        if (cell == null) {
                            stopAtColumn = colIdx;
                            break;
                        }
                        headers.add(cell.toString());
                        if (debug) Log.debug("  Header: \"" + cell + "\"");
                    } else {
                        String value = cell == null ? "" : cell.toString();
                        rowMap.put(headers.get(colIdx), value);
                        if (debug) Log.debug("  [" + sheet.getName() + "][" + rowIdx + "][" + colIdx + "] " + value);

                        boolean lastColumn = colIdx == row.length - 1 || colIdx == stopAtColumn - 1;
                        if (lastColumn) {
                            boolean emptyRow = rowMap.values().stream().allMatch(v -> v == null || v.isBlank());
                            if (!emptyRow) rows.add(rowMap);
                            if (debug) Log.debug("  row filled added!");
                        }
                    }
                }

                if (rowIdx == rawRows.length - 1) {
                    if (debug) Log.debug("  sheet: \"" + sheet.getName() + "\"");
                    root.put(sheet.getName(), rows);
                }
            }
            stopAtColumn = 0;
        }

        var templateDir = new File(this.templatesDirectory);
        var templateFiles = templateDir.listFiles();
        if (templateFiles == null || templateFiles.length == 0) return output.toString();

        Log.message("Generating:");
        for (File templateFile : templateFiles) {
            String fileName = templateFile.getName();
            if (debug) Log.debug("  file to render: " + fileName);
            Log.message("  - " + fileName);

            Template template = configuration.getTemplate(fileName);

            var byteOut = new ByteArrayOutputStream();
            try (var writer = new OutputStreamWriter(byteOut)) {
                template.process(root, writer);
            }

            String rendered = byteOut.toString("UTF-8");

            if (writeFiles) {
                FileUtils.writeStringToFile(new File(this.outputDirectory + fileName), rendered, "UTF-8");
            }

            output.append(rendered);
        }

        Log.message("  ");
        Log.message("  " + templateFiles.length + " files generated at " + this.outputDirectory);

        return output.toString();
    }
}
