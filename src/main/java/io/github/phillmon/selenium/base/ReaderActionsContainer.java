package io.github.phillmon.selenium.base;

import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;
import io.github.phillmon.selenium.modulars.reader.excel.ExcelReaderActions;
import io.github.phillmon.selenium.modulars.reader.pdf.PdfReaderActions;
import io.github.phillmon.selenium.modulars.reader.text.TextReaderActions;
import io.github.phillmon.selenium.modulars.reader.word.WordReaderActions;

/**
 * Groups together the readers for every document type the framework
 * supports: Excel, PDF, plain text, and Word. A page object reaches these
 * through ActionsContainer's reader field instead of creating its own
 * reader instances.
 */
public class ReaderActionsContainer {
    public final ExcelReaderActions excelActions;
    public final PdfReaderActions pdfActions;
    public final TextReaderActions textActions;
    public final WordReaderActions wordActions;

    /**
     * Creates one reader action instance per supported document type, all
     * sharing the same DocumentDownloader so downloaded files are found in
     * the same folder regardless of which reader opens them.
     */
    public ReaderActionsContainer(DocumentDownloader downloader) {
        this.excelActions = new ExcelReaderActions(downloader);
        this.pdfActions = new PdfReaderActions(downloader);
        this.textActions = new TextReaderActions(downloader);
        this.wordActions = new WordReaderActions(downloader);
    }
}
