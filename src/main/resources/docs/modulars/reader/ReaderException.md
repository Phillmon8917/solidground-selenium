# ReaderException

Package: `io.github.phillmon.selenium.modulars.reader`

Thrown when a document reader can't download, read, or delete a
document, or when a [DocumentSource](DocumentSource.md) or reader is
configured with arguments that don't make sense.

## Related classes

- [DocumentDownloader](DocumentDownloader.md), [DocumentSource](DocumentSource.md), [AbstractDocumentReaderActions](AbstractDocumentReaderActions.md) — throw this for their respective failures.
- [PdfReaderActions](pdf/PdfReaderActions.md) — also throws this from `getPageCount`.

## Constructors

| Constructor | Description |
|---|---|
| `ReaderException(String message)` | No underlying cause. |
| `ReaderException(String message, Throwable cause)` | Wraps the original I/O or parsing exception. |

[Back to reader index](README.md) · [Docs home](../../README.md)
