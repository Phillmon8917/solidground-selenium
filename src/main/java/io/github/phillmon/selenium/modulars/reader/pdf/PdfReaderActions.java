package io.github.phillmon.selenium.modulars.reader.pdf;

import io.github.phillmon.selenium.modulars.reader.AbstractDocumentReaderActions;
import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;
import io.github.phillmon.selenium.modulars.reader.ReaderException;
import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Reads the text content of PDF files and reports how many pages a PDF
 * has, so tests can check what a generated or uploaded PDF contains.
 */
public class PdfReaderActions extends AbstractDocumentReaderActions {

    /**
     * Creates the PDF reader using the given downloader, and configures
     * it to save downloaded documents with the .pdf extension.
     */
    public PdfReaderActions(DocumentDownloader downloader) {
        super(downloader, "pdf");
    }

    /**
     * Extracts all readable text from every page of the PDF. Expects the
     * path to the PDF file. Returns the extracted text with leading and
     * trailing whitespace removed.
     */
    @Override
    protected String extractText(Path filePath) throws IOException {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            return new PDFTextStripper().getText(document).trim();
        }
    }

    /**
     * Returns how many pages a PDF file has. Expects the path to the PDF
     * file and the name of the calling method for logging. Throws a
     * ReaderException if the file cannot be opened or read.
     */
    public int getPageCount(Path filePath, String methodName) {
        try (PDDocument document = Loader.loadPDF(filePath.toFile())) {
            int count = document.getNumberOfPages();
            LoggerUtil.info(methodName + " found page count: " + count);
            return count;
        } catch (IOException e) {
            throw new ReaderException(methodName + " - failed to read page count from: " + filePath, e);
        }
    }
}
