# VideoRetentionPolicy

Package: `io.github.phillmon.selenium.video`

Controls whether [VideoRecorder](VideoRecorder.md) keeps or deletes a finished
recording when `stop(boolean testPassed)` is called.

## Values

| Value | Behaviour |
|---|---|
| `ALWAYS` | Keeps every recording, whether the test passed or failed. |
| `ON_FAILURE_ONLY` | Keeps the recording only when `testPassed` is `false`. |

## Usage example

```java
VideoRecordingOptions options = VideoRecordingOptions.builder()
        .withRetentionPolicy(VideoRetentionPolicy.ALWAYS)
        .build();
```

[Back to video index](README.md) - [Docs home](../README.md)
