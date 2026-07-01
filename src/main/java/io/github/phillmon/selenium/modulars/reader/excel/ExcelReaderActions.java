package io.github.phillmon.selenium.modulars.reader.excel;

import io.github.phillmon.selenium.modulars.reader.AbstractDocumentReaderActions;
import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Reads the text content of Excel workbook (.xlsx) files, so tests can
 * check what an exported or uploaded spreadsheet contains.
 */
public class ExcelReaderActions extends AbstractDocumentReaderActions {

    /**
     * Creates the Excel reader using the given downloader, and configures
     * it to save downloaded documents with the .xlsx extension.
     */
    public ExcelReaderActions(DocumentDownloader downloader) {
        super(downloader, "xlsx");
    }

    /**
     * Reads every sheet, row, and cell in the workbook and joins their
     * formatted values into one block of text, with cells separated by
     * spaces and rows separated by new lines. Expects the path to the
     * workbook file. Returns the combined text with leading and trailing
     * whitespace removed.
     */
    @Override
    protected String extractText(Path filePath) throws IOException {
        StringBuilder builder = new StringBuilder();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream inputStream = new FileInputStream(filePath.toFile());
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        builder.append(formatter.formatCellValue(cell)).append(" ");
                    }
                    builder.append("\n");
                }
            }
        }

        return builder.toString().trim();
    }
}
