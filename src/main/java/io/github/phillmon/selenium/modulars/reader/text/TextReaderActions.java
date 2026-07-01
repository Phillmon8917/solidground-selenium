package io.github.phillmon.selenium.modulars.reader.text;

import io.github.phillmon.selenium.modulars.reader.AbstractDocumentReaderActions;
import io.github.phillmon.selenium.modulars.reader.DocumentDownloader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads the content of plain text files, so tests can check what a
 * downloaded or generated text file contains.
 */
public class TextReaderActions extends AbstractDocumentReaderActions {

    /**
     * Creates the text reader using the given downloader, and configures
     * it to save downloaded documents with the .txt extension.
     *
     * @param downloader the downloader this reader will use to fetch documents
     */
    public TextReaderActions(DocumentDownloader downloader) {
        this(downloader, "txt");
    }

    /**
     * Creates the text reader using the given downloader and a custom
     * file extension, for text-based formats other than plain .txt.
     * Expects the downloader to use and the file extension to save
     * downloaded documents with.
     *
     * @param downloader    the downloader this reader will use to fetch documents
     * @param fileExtension the file extension for this reader's format, without a leading dot
     */
    public TextReaderActions(DocumentDownloader downloader, String fileExtension) {
        super(downloader, fileExtension);
    }

    /**
     * Reads the whole file as text. Expects the path to the file. Returns
     * its contents with leading and trailing whitespace removed.
     *
     * @param filePath the path to the file to read
     * @return the file's contents with leading and trailing whitespace removed
     * @throws IOException if the file cannot be read
     */
    @Override
    protected String extractText(Path filePath) throws IOException {
        return Files.readString(filePath).trim();
    }
}
