# DocumentDownloader

Package: `io.github.phillmon.selenium.modulars.reader`

Works out the url of a document based on a [DocumentSource](DocumentSource.md)
and downloads it to a local file, reusing the browser's own cookies so
documents behind a login still download correctly. Every document reader
(Excel, PDF, text, Word) shares one of these.

Cookies are forwarded only when the document URL is on the same host as the
browser's current page. This avoids leaking a session cookie to a different
origin. On platforms with POSIX file permissions, downloaded files are also
restricted to owner read/write permissions after they are saved.

## Related classes

- [DocumentSource](DocumentSource.md) — describes what to resolve into a file.
- [UniqueFileNames](UniqueFileNames.md) — generates the file name used for a fresh download.
- [ReaderException](ReaderException.md) — thrown for any download/delete/directory failure.
- [AbstractDocumentReaderActions](AbstractDocumentReaderActions.md) — every reader holds one of these as `downloader`.
- [ReaderActionsContainer](../../base/ReaderActionsContainer.md) — creates one shared instance for all four readers.
- [PageModularOptions](../../base/PageModularOptions.md) — `withDownloadDirectory` / `withDownloadTimeout` configure the instance built for a page object.

## Usage example

Usually you don't construct this yourself — [ReaderActionsContainer](../../base/ReaderActionsContainer.md)
does it for you via [PageModularOptions](../../base/PageModularOptions.md).
Direct use looks like:

```java
DocumentDownloader downloader = new DocumentDownloader(driver, Path.of("build/downloads"), Duration.ofSeconds(45));
Path file = downloader.resolve(DocumentSource.fromUrl("https://example.com/report.pdf"), "pdf", "downloadReport");
downloader.delete(file, "cleanupReport");
```

For authenticated downloads, navigate the browser to the same application host
before resolving the document:

```java
driver.get("https://app.example.com/reports");
Path file = downloader.resolve(DocumentSource.fromUrl("https://app.example.com/reports/monthly.pdf"), "pdf", "downloadReport");
```

## Constructors

| Constructor | Description |
|---|---|
| `DocumentDownloader(WebDriver)` | Default directory (system temp folder), 30-second timeout. |
| `DocumentDownloader(WebDriver, Path defaultDirectory)` | Custom default directory, 30-second timeout. |
| `DocumentDownloader(WebDriver, Path defaultDirectory, Duration requestTimeout)` | Full control. Creates the directory immediately. |

## Methods

| Method | Description |
|---|---|
| `resolve(DocumentSource, fileExtension, methodName)` | Resolves into the default directory. |
| `resolve(DocumentSource, fileExtension, targetDirectory, methodName)` | Resolves into a custom directory. |
| `delete(Path filePath, methodName)` | Deletes a downloaded file if it still exists. |

All methods throw [ReaderException](ReaderException.md) on download,
directory-creation, or delete failures.

[Back to reader index](README.md) · [Docs home](../../README.md)
