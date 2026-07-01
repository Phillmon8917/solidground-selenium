# ExcelReaderActions

Package: `io.github.phillmon.selenium.modulars.reader.excel`

Reads the text content of Excel workbook (`.xlsx`) files, so tests can
check what an exported or uploaded spreadsheet contains. Extends
[AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md),
which supplies `download`, `readText`, and the `assert*` methods.

## Related classes

- [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md) — parent class supplying the download/read/assert methods.
- [DocumentSource](../DocumentSource.md) — describes where the workbook comes from.
- [DocumentDownloader](../DocumentDownloader.md) — used internally to fetch the file.
- [ReaderActionsContainer](../../../base/ReaderActionsContainer.md) — exposes this as `modulars.reader.excelActions`.

## Usage example

```java
DocumentSource export = DocumentSource.fromNewTab(() ->
        modulars.elementActions.click(exportToExcelButton, "export to excel button", "exportOrders"));

modulars.reader.excelActions.assertContainsText(export, "Order #1029", "exportOrders");

String content = modulars.reader.excelActions.readText(export, "exportOrders");
```

## Constructor

| Constructor | Description |
|---|---|
| `ExcelReaderActions(DocumentDownloader)` | Saves downloaded documents with the `.xlsx` extension. |

## Methods (own)

| Method | Description |
|---|---|
| `extractText(Path filePath)` (protected) | Reads every sheet/row/cell and joins their formatted values, cells separated by spaces, rows by new lines. |

See [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md)
for the full inherited method list (`download`, `readText`,
`assertContainsText`, and so on).

[Back to reader index](../README.md) · [Back to modulars index](../../README.md) · [Docs home](../../../README.md)
