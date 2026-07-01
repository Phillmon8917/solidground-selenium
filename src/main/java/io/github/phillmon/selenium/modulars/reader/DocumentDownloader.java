package io.github.phillmon.selenium.modulars.reader;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.github.phillmon.selenium.utils.timeout.TimeoutUtil;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Works out the url of a document based on a DocumentSource and downloads
 * it to a local file, reusing the browser's own cookies so documents
 * behind a login still download correctly. Every document reader (Excel,
 * PDF, text, Word) shares one of these to fetch files before reading
 * them.
 */
public class DocumentDownloader {
    private final WebDriver driver;
    private final Path defaultDirectory;
    private final Duration requestTimeout;

    /**
     * Creates the downloader using the system's temp folder as the
     * default download directory and a request timeout of 30 seconds.
     * Expects the WebDriver for the current browser session.
     *
     * @param driver the WebDriver for the current browser session
     */
    public DocumentDownloader(WebDriver driver) {
        this(driver, Path.of(System.getProperty("java.io.tmpdir")), Duration.ofSeconds(30));
    }

    /**
     * Creates the downloader with a custom default download directory and
     * a request timeout of 30 seconds. Expects the WebDriver for the
     * current browser session and the folder documents should be saved
     * into by default.
     *
     * @param driver           the WebDriver for the current browser session
     * @param defaultDirectory the folder documents should be saved into by default
     */
    public DocumentDownloader(WebDriver driver, Path defaultDirectory) {
        this(driver, defaultDirectory, Duration.ofSeconds(30));
    }

    /**
     * Creates the downloader with a custom default download directory and
     * a custom request timeout. Expects the WebDriver for the current
     * browser session, the folder documents should be saved into by
     * default, and how long to wait for a download request to complete.
     * Creates the default directory immediately if it does not already
     * exist.
     *
     * @param driver           the WebDriver for the current browser session
     * @param defaultDirectory the folder documents should be saved into by default
     * @param requestTimeout   how long to wait for a download request to complete
     */
    public DocumentDownloader(WebDriver driver, Path defaultDirectory, Duration requestTimeout) {
        this.driver = driver;
        this.defaultDirectory = defaultDirectory;
        this.requestTimeout = TimeoutUtil.adjust(requestTimeout);
        createDirectory(defaultDirectory);
    }

    /**
     * Resolves a document source into a local file, saving it into the
     * default download directory if a download is needed. Expects the
     * document source, the file extension to use for the saved file, and
     * the name of the calling method for logging. Returns the path to the
     * resulting file, which may be the source's existing path if it
     * already pointed at one.
     *
     * @param source        the document source to resolve
     * @param fileExtension the file extension to use for the saved file, without a leading dot
     * @param methodName    the name of the calling method, for logging
     * @return the path to the resulting file
     */
    public Path resolve(DocumentSource source, String fileExtension, String methodName) {
        return resolve(source, fileExtension, defaultDirectory, methodName);
    }

    /**
     * Resolves a document source into a local file, saving it into the
     * given directory if a download is needed. Expects the document
     * source, the file extension to use for the saved file, the
     * directory to save into, and the name of the calling method for
     * logging. Returns the path to the resulting file, which may be the
     * source's existing path if it already pointed at one.
     *
     * @param source          the document source to resolve
     * @param fileExtension   the file extension to use for the saved file, without a leading dot
     * @param targetDirectory the directory to save the document into
     * @param methodName      the name of the calling method, for logging
     * @return the path to the resulting file
     */
    public Path resolve(DocumentSource source, String fileExtension, Path targetDirectory, String methodName) {
        if (source.hasExistingPath()) {
            LoggerUtil.info(methodName + " using existing file path: " + source.getExistingPath());
            return source.getExistingPath();
        }

        createDirectory(targetDirectory);
        String url = resolveUrl(source, methodName);
        Path targetPath = targetDirectory.resolve(UniqueFileNames.generate("document", fileExtension));
        fetchToFile(url, targetPath, methodName);
        return targetPath;
    }

    /**
     * Works out the url to download the document from, based on how the
     * source was created: a direct url, the current tab's url, or a url
     * captured from a new tab opened by the source's trigger action.
     * Expects the document source and the name of the calling method for
     * logging. Returns the resolved url. Throws a ReaderException if a
     * trigger action was given but it did not open a new tab or window.
     */
    private String resolveUrl(DocumentSource source, String methodName) {
        if (source.getTriggerAction() == null) {
            return source.getUrl() != null ? source.getUrl() : driver.getCurrentUrl();
        }

        Set<String> handlesBefore = driver.getWindowHandles();
        source.getTriggerAction().run();

        Set<String> handlesAfter = new HashSet<>(driver.getWindowHandles());
        handlesAfter.removeAll(handlesBefore);

        if (handlesAfter.isEmpty()) {
            throw new ReaderException(methodName + " - trigger action did not open a new tab/window");
        }

        String newHandle = handlesAfter.iterator().next();
        String originalHandle = driver.getWindowHandle();

        driver.switchTo().window(newHandle);
        String url = driver.getCurrentUrl();
        LoggerUtil.info(methodName + " captured document url from new tab: " + url);

        if (source.isCloseTabAfterCapture()) {
            driver.close();
            driver.switchTo().window(originalHandle);
        }

        return url;
    }

    /**
     * Downloads the bytes at a url and writes them to a file, sending the
     * browser's current cookies along with the request so documents that
     * need the user to be logged in still download correctly. Expects
     * the url to download from, the path to save the file to, and the
     * name of the calling method for logging. Throws a ReaderException if
     * the request fails, if the response status is not in the 200 to 299
     * range, or if the download is interrupted.
     */
    private void fetchToFile(String url, Path targetPath, String methodName) {
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(requestTimeout).build();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(requestTimeout)
                    .GET();

            String cookieHeader = buildCookieHeader();
            if (!cookieHeader.isEmpty()) {
                requestBuilder.header("Cookie", cookieHeader);
            }

            HttpResponse<byte[]> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ReaderException(methodName + " - failed to fetch document: HTTP "
                        + response.statusCode() + " for url: " + url);
            }

            Files.write(targetPath, response.body());
            LoggerUtil.info(methodName + " downloaded document to: " + targetPath);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new ReaderException(methodName + " - failed to download document from url: " + url, e);
        }
    }

    /**
     * Builds a Cookie header value out of every cookie currently held by
     * the browser session, so a downloaded document can be requested with
     * the same session the browser is using.
     */
    private String buildCookieHeader() {
        List<Cookie> cookies = new ArrayList<>(driver.manage().getCookies());
        return cookies.stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .collect(Collectors.joining("; "));
    }

    /**
     * Deletes a downloaded file if it still exists. Expects the path to
     * the file and the name of the calling method for logging. Throws a
     * ReaderException if the file exists but cannot be deleted.
     *
     * @param filePath   the path to the file to delete
     * @param methodName the name of the calling method, for logging
     */
    public void delete(Path filePath, String methodName) {
        try {
            boolean deleted = Files.deleteIfExists(filePath);
            LoggerUtil.info(deleted
                    ? methodName + " deleted file: " + filePath
                    : methodName + " file already absent, nothing to delete: " + filePath);
        } catch (IOException e) {
            throw new ReaderException(methodName + " - failed to delete file: " + filePath, e);
        }
    }

    /**
     * Creates a directory, including any missing parent folders, if it
     * does not already exist. Expects the directory to create. Throws a
     * ReaderException if the directory cannot be created.
     */
    private void createDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new ReaderException("Could not create directory: " + directory, e);
        }
    }
}
