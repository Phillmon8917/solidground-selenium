# WordReaderActions

Package: `io.github.phillmon.selenium.modulars.reader.word`

Reads the text content of Word document (`.docx`) files, so tests can
check what a generated or uploaded Word document contains. Extends
[AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md),
which supplies `download`, `readText`, and the `assert*` methods.

## Related classes

- [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md) — parent class supplying the download/read/assert methods.
- [DocumentSource](../DocumentSource.md) — describes where the document comes from.
- [ReaderActionsContainer](../../../base/ReaderActionsContainer.md) — exposes this as `modulars.reader.wordActions`.

## Usage example

```java
DocumentSource contract = DocumentSource.fromNewTab(() ->
        modulars.elementActions.click(downloadContractLink, "download contract link", "downloadContract"));

modulars.reader.wordActions.assertContainsAllTexts(contract,
        List.of("Terms and Conditions", "Signature"), "downloadContract");
```

## Constructor

| Constructor | Description |
|---|---|
| `WordReaderActions(DocumentDownloader)` | Saves downloaded documents with the `.docx` extension. |

## Methods (own)

| Method | Description |
|---|---|
| `extractText(Path filePath)` (protected) | Extracts all readable text from the document. |

See [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md)
for the full inherited method list.

[Back to reader index](../README.md) · [Back to modulars index](../../README.md) · [Docs home](../../../README.md)
