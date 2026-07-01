# PdfReaderActions

Package: `io.github.phillmon.selenium.modulars.reader.pdf`

Reads the text content of PDF files and reports how many pages a PDF has,
so tests can check what a generated or uploaded PDF contains. Extends
[AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md),
which supplies `download`, `readText`, and the `assert*` methods.

## Related classes

- [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md) — parent class supplying the download/read/assert methods.
- [DocumentSource](../DocumentSource.md) — describes where the PDF comes from.
- [ReaderException](../ReaderException.md) — thrown by `getPageCount` if the file can't be read.
- [ReaderActionsContainer](../../../base/ReaderActionsContainer.md) — exposes this as `modulars.reader.pdfActions`.

## Usage example

```java
DocumentSource invoice = DocumentSource.fromNewTab(() ->
        modulars.elementActions.click(downloadInvoiceLink, "download invoice link", "downloadInvoice"));

modulars.reader.pdfActions.assertContainsText(invoice, "Invoice Total", "downloadInvoice");

Path file = modulars.reader.pdfActions.download(invoice, "downloadInvoice");
int pages = modulars.reader.pdfActions.getPageCount(file, "downloadInvoice");
```

## Constructor

| Constructor | Description |
|---|---|
| `PdfReaderActions(DocumentDownloader)` | Saves downloaded documents with the `.pdf` extension. |

## Methods (own)

| Method | Description |
|---|---|
| `extractText(Path filePath)` (protected) | Extracts all readable text from every page. |
| `getPageCount(Path filePath, methodName)` | Returns how many pages the PDF has. Throws [ReaderException](../ReaderException.md) if the file can't be opened. |

See [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md)
for the full inherited method list.

[Back to reader index](../README.md) · [Back to modulars index](../../README.md) · [Docs home](../../../README.md)
