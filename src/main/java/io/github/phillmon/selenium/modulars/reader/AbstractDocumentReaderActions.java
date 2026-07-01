package io.github.phillmon.selenium.modulars.reader;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;

import java.nio.file.Path;
import java.util.List;

/**
 * Shared behaviour for every document reader (Excel, PDF, text, Word):
 * downloading the document through a DocumentDownloader, extracting its
 * text, and running common text assertions such as checking that the
 * document contains or does not contain certain phrases. Each concrete
 * reader only needs to implement extractText for its own file format.
 */
public abstract class AbstractDocumentReaderActions {
    protected final DocumentDownloader downloader;
    private final String fileExtension;

    /**
     * Creates the reader with the downloader it should use and the file
     * extension its documents are saved with. Expects a non-null
     * DocumentDownloader and the file extension for this reader's format,
     * such as "pdf" or "xlsx".
     */
    protected AbstractDocumentReaderActions(DocumentDownloader downloader, String fileExtension) {
        this.downloader = downloader;
        this.fileExtension = fileExtension;
    }

    /**
     * Reads the text content out of a downloaded file. Expects the path
     * to the file on disk. Returns the extracted text. Each concrete
     * reader implements this differently depending on the file format,
     * and may throw any exception while parsing the file.
     */
    protected abstract String extractText(Path filePath) throws Exception;

    /**
     * Downloads the document described by the given source into the
     * downloader's default directory. Expects the document source and
     * the name of the calling method for logging. Returns the path to
     * the downloaded (or already existing) file.
     */
    public Path download(DocumentSource source, String methodName) {
        return downloader.resolve(source, fileExtension, methodName);
    }

    /**
     * Downloads the document described by the given source into a custom
     * directory. Expects the document source, the directory to save into,
     * and the name of the calling method for logging. Returns the path to
     * the downloaded (or already existing) file.
     */
    public Path download(DocumentSource source, Path targetDirectory, String methodName) {
        return downloader.resolve(source, fileExtension, targetDirectory, methodName);
    }

    /**
     * Reads the text out of a file that has already been downloaded.
     * Expects the path to the file and the name of the calling method for
     * logging. Returns the extracted text. Throws a ReaderException if
     * the file cannot be read or parsed.
     */
    public String readText(Path filePath, String methodName) {
        try {
            String text = extractText(filePath);
            LoggerUtil.info(methodName + " extracted text from: " + filePath);
            return text;
        } catch (Exception e) {
            throw new ReaderException(methodName + " - failed to extract text from: " + filePath, e);
        }
    }

    /**
     * Downloads the document described by the given source and reads its
     * text in one step. Expects the document source and the name of the
     * calling method for logging. Returns the extracted text. If the
     * source did not already point at an existing file, the downloaded
     * file is deleted again afterwards to avoid leaving temporary files
     * behind.
     */
    public String readText(DocumentSource source, String methodName) {
        Path filePath = download(source, methodName);
        try {
            return readText(filePath, methodName);
        } finally {
            if (!source.hasExistingPath()) {
                downloader.delete(filePath, methodName);
            }
        }
    }

    /**
     * Downloads the document and asserts that its text contains the given
     * phrase. Expects the document source, the text expected to be
     * present, and the name of the calling method for logging. Throws a
     * ReaderException if the document does not contain the expected
     * text.
     */
    public void assertContainsText(DocumentSource source, String expectedText, String methodName) {
        String content = readText(source, methodName);
        if (!content.contains(expectedText)) {
            throw new ReaderException(methodName + " - document does not contain expected text: '" + expectedText + "'");
        }
        LoggerUtil.info(methodName + " document contains expected text: '" + expectedText + "'");
    }

    /**
     * Downloads the document and asserts that its text does not contain
     * the given phrase. Expects the document source, the text that must
     * not be present, and the name of the calling method for logging.
     * Throws a ReaderException if the document contains the text anyway.
     */
    public void assertNotContainsText(DocumentSource source, String expectedText, String methodName) {
        String content = readText(source, methodName);
        if (content.contains(expectedText)) {
            throw new ReaderException(methodName + " - document unexpectedly contains text: '" + expectedText + "'");
        }
        LoggerUtil.info(methodName + " document does not contain text: '" + expectedText + "'");
    }

    /**
     * Downloads the document and asserts that its text contains every
     * phrase in the given list. Expects the document source, the list of
     * texts that must all be present, and the name of the calling method
     * for logging. Throws a ReaderException listing whichever phrases
     * were missing, if any were.
     */
    public void assertContainsAllTexts(DocumentSource source, List<String> expectedTexts, String methodName) {
        String content = readText(source, methodName);
        List<String> missing = expectedTexts.stream().filter(text -> !content.contains(text)).toList();
        if (!missing.isEmpty()) {
            throw new ReaderException(methodName + " - document is missing expected text(s): " + missing);
        }
        LoggerUtil.info(methodName + " document contains all expected texts: " + expectedTexts);
    }

    /**
     * Downloads the document and asserts that its text contains at least
     * one phrase from the given list. Expects the document source, the
     * list of texts where any one of them being present is enough, and
     * the name of the calling method for logging. Throws a
     * ReaderException if none of the phrases are found.
     */
    public void assertContainsAnyText(DocumentSource source, List<String> expectedTexts, String methodName) {
        String content = readText(source, methodName);
        boolean foundAny = expectedTexts.stream().anyMatch(content::contains);
        if (!foundAny) {
            throw new ReaderException(methodName + " - document contains none of the expected text(s): " + expectedTexts);
        }
        LoggerUtil.info(methodName + " document contains at least one of the expected texts: " + expectedTexts);
    }

    /**
     * Deletes a downloaded file. Expects the path to the file and the
     * name of the calling method for logging.
     */
    public void delete(Path filePath, String methodName) {
        downloader.delete(filePath, methodName);
    }
}
