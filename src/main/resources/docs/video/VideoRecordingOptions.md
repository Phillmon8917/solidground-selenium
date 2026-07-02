# VideoRecordingOptions

Package: `io.github.phillmon.selenium.video`

Immutable settings for [VideoRecorder](VideoRecorder.md): where AVI files are
written, which recordings are kept, the capture frame rate, JPEG quality, and
an optional maximum frame size.

## Related classes

- [VideoRecorder](VideoRecorder.md) - receives these options in its constructor.
- [VideoRetentionPolicy](VideoRetentionPolicy.md) - controls whether recordings
  are kept always or only on failure.

## Usage example

```java
VideoRecordingOptions options = VideoRecordingOptions.builder()
        .withOutputDirectory(Path.of("build", "selenium-videos"))
        .withRetentionPolicy(VideoRetentionPolicy.ON_FAILURE_ONLY)
        .withFrameRate(12)
        .withQuality(75)
        .withMaxDimensions(1366, 768)
        .build();
```

Defaults are suitable when you only want failed-test recordings:

```java
VideoRecordingOptions options = VideoRecordingOptions.defaults();
```

## Defaults

| Setting | Default |
|---|---|
| Output directory | `target/videos` |
| Retention policy | `ON_FAILURE_ONLY` |
| Frame rate | `8` frames per second |
| JPEG quality | `70` |
| Maximum dimensions | No cap; Chrome's viewport size is used. |

## Builder methods

| Method | Description |
|---|---|
| `withOutputDirectory(Path)` | Sets where finished AVI files are written. Rejects null. |
| `withRetentionPolicy(VideoRetentionPolicy)` | Sets whether recordings are kept always or only on failure. Rejects null. |
| `withFrameRate(int)` | Sets capture/playback frames per second. Must be at least 1. |
| `withQuality(int)` | Sets JPEG quality for captured frames. Must be between 0 and 100. |
| `withMaxDimensions(int maxWidth, int maxHeight)` | Requests a maximum captured frame size. Both values must be positive. |
| `build()` | Creates the immutable options object. |

[Back to video index](README.md) - [Docs home](../README.md)
