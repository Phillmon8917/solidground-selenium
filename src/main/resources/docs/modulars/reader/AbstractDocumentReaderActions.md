# AbstractDocumentReaderActions

Package: `io.github.phillmon.selenium.modulars.reader` (abstract class)

Shared behaviour for every document reader (Excel, PDF, text, Word):
downloading the document through a [DocumentDownloader](DocumentDownloader.md),
extracting its text, and running common text assertions. Each concrete
reader only implements `extractText` for its own file format.

## Related classes

- [DocumentDownloader](DocumentDownloader.md) — used to download the document before reading it.
- [DocumentSource](DocumentSource.md) — describes where the document comes from.
- [ReaderException](ReaderException.md) — thrown when reading, extracting, or asserting fails.
- Subclasses: [ExcelReaderActions](excel/ExcelReaderActions.md), [PdfReaderActions](pdf/PdfReaderActions.md), [TextReaderActions](text/TextReaderActions.md), [WordReaderActions](word/WordReaderActions.md).

## Usage example

All the methods below are inherited by every concrete reader, so this
example works identically for `modulars.reader.pdfActions`,
`.excelActions`, `.textActions`, or `.wordActions`:

```java
DocumentSource invoice = DocumentSource.fromNewTab(() ->
        modulars.elementActions.click(downloadInvoiceLink, "download invoice link", "downloadInvoice"));

modulars.reader.pdfActions.assertContainsText(invoice, "Invoice Total", "downloadInvoice");
modulars.reader.pdfActions.assertContainsAllTexts(invoice, List.of("Invoice Total", "Due Date"), "downloadInvoice");
```

## Methods

| Method | Description |
|---|---|
| `download(DocumentSource, methodName)` | Downloads into the default directory. |
| `download(DocumentSource, targetDirectory, methodName)` | Downloads into a custom directory. |
| `readText(Path filePath, methodName)` | Reads the text out of an already-downloaded file. |
| `readText(DocumentSource, methodName)` | Downloads and reads the text in one step, deleting the file afterwards unless it was an existing path. |
| `assertContainsText(DocumentSource, expectedText, methodName)` | Asserts the document contains a phrase. |
| `assertNotContainsText(DocumentSource, expectedText, methodName)` | Asserts the document does not contain a phrase. |
| `assertContainsAllTexts(DocumentSource, List<String>, methodName)` | Asserts the document contains every phrase in a list. |
| `assertContainsAnyText(DocumentSource, List<String>, methodName)` | Asserts the document contains at least one phrase from a list. |
| `delete(Path filePath, methodName)` | Deletes a downloaded file. |

All read/assert methods throw [ReaderException](ReaderException.md) if
the file can't be read, parsed, or doesn't satisfy the assertion.

[Back to reader index](README.md) · [Docs home](../../README.md)
