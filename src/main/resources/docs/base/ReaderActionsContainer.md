# ReaderActionsContainer

Package: `io.github.phillmon.selenium.base`

Groups the readers for every document type the framework supports —
Excel, PDF, plain text, and Word — behind one object. [ActionsContainer](ActionsContainer.md)
creates one of these and exposes it as `modulars.reader`.

## Related classes

- [ActionsContainer](ActionsContainer.md) — owns the `reader` field of this type.
- [DocumentDownloader](../modulars/reader/DocumentDownloader.md) — the shared downloader passed to every reader.
- [ExcelReaderActions](../modulars/reader/excel/ExcelReaderActions.md), [PdfReaderActions](../modulars/reader/pdf/PdfReaderActions.md), [TextReaderActions](../modulars/reader/text/TextReaderActions.md), [WordReaderActions](../modulars/reader/word/WordReaderActions.md) — the four readers this class holds.

## Usage example

```java
DocumentSource invoice = DocumentSource.fromNewTab(() ->
        modulars.elementActions.click(downloadInvoiceLink, "download invoice link", "downloadInvoice"));

modulars.reader.pdfActions.assertContainsText(invoice, "Invoice Total", "downloadInvoice");
```

## Fields

| Field | Type | Description |
|---|---|---|
| `excelActions` | [ExcelReaderActions](../modulars/reader/excel/ExcelReaderActions.md) | Reads `.xlsx` workbooks. |
| `pdfActions` | [PdfReaderActions](../modulars/reader/pdf/PdfReaderActions.md) | Reads `.pdf` files, plus page counts. |
| `textActions` | [TextReaderActions](../modulars/reader/text/TextReaderActions.md) | Reads plain text files. |
| `wordActions` | [WordReaderActions](../modulars/reader/word/WordReaderActions.md) | Reads `.docx` files. |

## Constructor

| Constructor | Description |
|---|---|
| `ReaderActionsContainer(DocumentDownloader downloader)` | Creates one reader per document type, all sharing the given downloader. |

[Back to base index](README.md) · [Docs home](../README.md)
