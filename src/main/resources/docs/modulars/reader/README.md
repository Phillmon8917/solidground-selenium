# modulars/reader

Downloading and reading Excel, PDF, text, and Word documents, available
on a page object as `modulars.reader` (see
[ReaderActionsContainer](../../base/ReaderActionsContainer.md)).

- [AbstractDocumentReaderActions](AbstractDocumentReaderActions.md) — shared download/read/assert behaviour every reader inherits.
- [DocumentDownloader](DocumentDownloader.md) — resolves a `DocumentSource` into a local file.
- [DocumentSource](DocumentSource.md) — describes where a document comes from.
- [UniqueFileNames](UniqueFileNames.md) — generates clash-free file names.
- [ReaderException](ReaderException.md) — thrown for any reader/downloader failure.
- [excel/ExcelReaderActions](excel/ExcelReaderActions.md) — reads `.xlsx` files.
- [pdf/PdfReaderActions](pdf/PdfReaderActions.md) — reads `.pdf` files, plus page counts.
- [text/TextReaderActions](text/TextReaderActions.md) — reads plain text files.
- [word/WordReaderActions](word/WordReaderActions.md) — reads `.docx` files.

[Back to modulars index](../README.md) · [Docs home](../../README.md)
