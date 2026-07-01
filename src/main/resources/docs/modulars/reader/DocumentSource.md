# DocumentSource

Package: `io.github.phillmon.selenium.modulars.reader`

Describes where a document should come from before it can be read: an
already downloaded file on disk, a direct url, the page currently open in
the browser, or a new tab opened by running an action such as clicking a
"view document" link.

## Related classes

- [DocumentDownloader](DocumentDownloader.md) — resolves this into an actual local file.
- [AbstractDocumentReaderActions](AbstractDocumentReaderActions.md) — every `download`/`readText`/`assert*` method takes one of these.
- [ReaderException](ReaderException.md) — thrown by the factory methods for invalid arguments.

## Usage example

```java
// A document whose url can be seen from the current tab (e.g. a print-preview page):
DocumentSource fromCurrentTab = DocumentSource.fromCurrentTab();

// A direct link to the file:
DocumentSource fromUrl = DocumentSource.fromUrl("https://example.com/reports/latest.pdf");

// A document that opens in a new tab when a link is clicked:
DocumentSource fromNewTab = DocumentSource.fromNewTab(() ->
        modulars.elementActions.click(downloadInvoiceLink, "download invoice link", "downloadInvoice"));

// A file that was already saved to disk by an earlier step:
DocumentSource fromPath = DocumentSource.fromPath(Path.of("build/downloads/invoice.pdf"));

modulars.reader.pdfActions.assertContainsText(fromNewTab, "Invoice Total", "downloadInvoice");
```

## Static factories

| Method | Description |
|---|---|
| `static DocumentSource fromPath(Path)` | Points at a file already saved to disk; no download needed. |
| `static DocumentSource fromUrl(String)` | Downloads directly from a given url. |
| `static DocumentSource fromCurrentTab()` | Downloads whatever is at the url currently open in the active tab. |
| `static DocumentSource fromNewTab(Runnable triggerAction)` | Runs an action that opens a new tab, downloads from its url, then closes the tab. |
| `static DocumentSource fromNewTab(Runnable triggerAction, boolean closeTabAfterCapture)` | Same, with control over whether the new tab gets closed. |

All factories throw [ReaderException](ReaderException.md) for null/blank
required arguments.

## Instance methods

| Method | Description |
|---|---|
| `hasExistingPath()` | Whether this source already points at a file on disk. |
| `getExistingPath()` | The existing file path, or null. |
| `getUrl()` | The direct url, or null. |
| `getTriggerAction()` | The action that opens the new tab, or null. |
| `isCloseTabAfterCapture()` | Whether the new tab should be closed after capturing its url. |

[Back to reader index](README.md) · [Docs home](../../README.md)
