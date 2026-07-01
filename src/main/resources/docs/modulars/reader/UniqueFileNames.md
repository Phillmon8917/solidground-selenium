# UniqueFileNames

Package: `io.github.phillmon.selenium.modulars.reader`

Small helper for building file names that are guaranteed not to clash
with each other, even when several downloads happen at once from
different threads.

## Related classes

- [DocumentDownloader](DocumentDownloader.md) — the only caller, used when saving a fresh download.

## Usage example

```java
String fileName = UniqueFileNames.generate("document", "pdf");
// e.g. "document-1719830000-1-9f2c6b0a-....pdf"
```

## Method

| Method | Description |
|---|---|
| `static String generate(String prefix, String extension)` | Combines the prefix, current time, thread id, and a random UUID into a unique file name. |

[Back to reader index](README.md) · [Docs home](../../README.md)
