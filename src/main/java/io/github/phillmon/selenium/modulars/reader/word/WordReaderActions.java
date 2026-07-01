package io.github.phillmon.selenium.modulars.reader.word;

import io.github.phillmon.selenium.modulars.reader.AbstractDocumentReaderActions;
import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Reads the text content of Word document (.docx) files, so tests can
 * check what a generated or uploaded Word document contains.
 */
public class WordReaderActions extends AbstractDocumentReaderActions {

    /**
     * Creates the Word reader using the given downloader, and configures
     * it to save downloaded documents with the .docx extension.
     *
     * @param downloader the downloader this reader will use to fetch Word documents
     */
    public WordReaderActions(DocumentDownloader downloader) {
        super(downloader, "docx");
    }

    /**
     * Extracts all readable text from the Word document. Expects the
     * path to the document file. Returns the extracted text with leading
     * and trailing whitespace removed.
     *
     * @param filePath the path to the Word document file
     * @return the extracted text with leading and trailing whitespace removed
     * @throws IOException if the document cannot be read or parsed
     */
    @Override
    protected String extractText(Path filePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath.toFile());
             XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText().trim();
        }
    }
}
