# TextReaderActions

Package: `io.github.phillmon.selenium.modulars.reader.text`

Reads the content of plain text files, so tests can check what a
downloaded or generated text file contains. Extends
[AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md),
which supplies `download`, `readText`, and the `assert*` methods.

## Related classes

- [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md) — parent class supplying the download/read/assert methods.
- [DocumentSource](../DocumentSource.md) — describes where the text file comes from.
- [ReaderActionsContainer](../../../base/ReaderActionsContainer.md) — exposes this as `modulars.reader.textActions`.

## Usage example

```java
DocumentSource exportedLog = DocumentSource.fromUrl(EnvLoader.getUrl() + "/logs/export.log");
modulars.reader.textActions.assertContainsText(exportedLog, "Export completed", "verifyExportLog");
```

Using a custom extension for a text-based format other than `.txt`:

```java
TextReaderActions csvReader = new TextReaderActions(downloader, "csv");
```

## Constructors

| Constructor | Description |
|---|---|
| `TextReaderActions(DocumentDownloader)` | Saves downloaded documents with the `.txt` extension. |
| `TextReaderActions(DocumentDownloader, String fileExtension)` | Saves with a custom extension. |

## Methods (own)

| Method | Description |
|---|---|
| `extractText(Path filePath)` (protected) | Reads the whole file as text. |

See [AbstractDocumentReaderActions](../AbstractDocumentReaderActions.md)
for the full inherited method list.

[Back to reader index](../README.md) · [Back to modulars index](../../README.md) · [Docs home](../../../README.md)
