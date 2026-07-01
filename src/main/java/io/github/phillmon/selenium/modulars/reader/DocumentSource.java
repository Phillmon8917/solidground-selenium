package io.github.phillmon.selenium.modulars.reader;

import java.nio.file.Path;

/**
 * Describes where a document should come from before it can be read:
 * an already downloaded file on disk, a direct url, the page currently
 * open in the browser, or a new tab that gets opened by running an
 * action such as clicking a "view document" link. DocumentDownloader
 * uses this to work out how to get hold of the file.
 */
public class DocumentSource {
    private final Path existingPath;
    private final String url;
    private final Runnable triggerAction;
    private final boolean closeTabAfterCapture;

    private DocumentSource(Path existingPath, String url, Runnable triggerAction, boolean closeTabAfterCapture) {
        this.existingPath = existingPath;
        this.url = url;
        this.triggerAction = triggerAction;
        this.closeTabAfterCapture = closeTabAfterCapture;
    }

    /**
     * Creates a source pointing at a file that has already been
     * downloaded and saved to disk, so no download is needed. Expects a
     * non-null path to the existing file. Throws a ReaderException if the
     * path is null.
     *
     * @param existingPath the path to the already downloaded file
     * @return a source that points directly at the existing file
     */
    public static DocumentSource fromPath(Path existingPath) {
        if (existingPath == null) {
            throw new ReaderException("existingPath must not be null");
        }
        return new DocumentSource(existingPath, null, null, false);
    }

    /**
     * Creates a source that downloads the document directly from a given
     * url. Expects a non-blank url. Throws a ReaderException if the url
     * is null or blank.
     *
     * @param url the direct url to download the document from
     * @return a source that downloads from the given url
     */
    public static DocumentSource fromUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new ReaderException("url must not be null or blank");
        }
        return new DocumentSource(null, url, null, false);
    }

    /**
     * Creates a source that downloads whatever document is at the url
     * currently open in the browser's active tab.
     *
     * @return a source that downloads from the browser's current tab
     */
    public static DocumentSource fromCurrentTab() {
        return new DocumentSource(null, null, null, false);
    }

    /**
     * Creates a source that runs an action expected to open a new browser
     * tab showing the document, downloads it from that tab's url, and
     * then closes the tab afterwards. Expects the action to run, such as
     * clicking a link that opens the document in a new tab.
     *
     * @param triggerAction the action to run that is expected to open the new tab
     * @return a source that opens a new tab and closes it after capturing the document
     */
    public static DocumentSource fromNewTab(Runnable triggerAction) {
        return fromNewTab(triggerAction, true);
    }

    /**
     * Creates a source that runs an action expected to open a new browser
     * tab showing the document, and downloads it from that tab's url.
     * Expects the action to run and whether the new tab should be closed
     * again once the document's url has been captured. Throws a
     * ReaderException if the action is null.
     *
     * @param triggerAction        the action to run that is expected to open the new tab
     * @param closeTabAfterCapture whether the new tab should be closed after the document's url is captured
     * @return a source that opens a new tab and optionally closes it after capturing the document
     */
    public static DocumentSource fromNewTab(Runnable triggerAction, boolean closeTabAfterCapture) {
        if (triggerAction == null) {
            throw new ReaderException("triggerAction must not be null");
        }
        return new DocumentSource(null, null, triggerAction, closeTabAfterCapture);
    }

    /**
     * Returns whether this source already points at a file on disk,
     * meaning no download is required.
     *
     * @return true if this source already has an existing file path, false otherwise
     */
    public boolean hasExistingPath() {
        return existingPath != null;
    }

    /**
     * Returns the path to the already downloaded file, or null if this
     * source is not based on an existing path.
     *
     * @return the path to the existing file, or null if this source has none
     */
    public Path getExistingPath() {
        return existingPath;
    }

    /**
     * Returns the direct url to download from, or null if this source is
     * not based on a direct url.
     *
     * @return the direct url to download from, or null if this source has none
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the action that should be run to open the document in a new
     * tab, or null if this source does not use that approach.
     *
     * @return the trigger action to open a new tab, or null if this source has none
     */
    public Runnable getTriggerAction() {
        return triggerAction;
    }

    /**
     * Returns whether the new tab opened by the trigger action should be
     * closed again after the document's url has been captured.
     *
     * @return true if the new tab should be closed after capture, false otherwise
     */
    public boolean isCloseTabAfterCapture() {
        return closeTabAfterCapture;
    }
}
