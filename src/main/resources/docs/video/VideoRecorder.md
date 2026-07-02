# VideoRecorder

Package: `io.github.phillmon.selenium.video`

Records a Chrome-backed WebDriver session using Chrome DevTools screencast and
writes the result as an MJPEG AVI file. It does not attach videos to a report;
`stop(boolean testPassed)` returns the kept file path so the caller can attach
it to Allure, TestNG, or another reporting layer.

Recording works in headless Chrome because frames come from Chrome DevTools,
not from desktop screen capture.

## Related classes

- [VideoRecordingOptions](VideoRecordingOptions.md) - configures output,
  retention, frame rate, quality, and maximum dimensions.
- [VideoRetentionPolicy](VideoRetentionPolicy.md) - decides whether a completed
  recording is kept or discarded.
- [VideoRecordingException](VideoRecordingException.md) - thrown when recording
  cannot start or finish.
- [UniqueFileNames](../modulars/reader/UniqueFileNames.md) - generates the AVI
  file name.
- [LoggerUtil](../utils/logging/LoggerUtil.md) - receives recording lifecycle
  messages.

## Usage example

Keep videos only for failed tests:

```java
Optional<Path> video;

try (VideoRecorder recorder = new VideoRecorder(driver)) {
    recorder.start("checkout-creates-order");

    checkoutPage.submitOrder();

    video = recorder.stop(testPassed);
}

video.ifPresent(path -> attachVideoToReport(path));
```

Custom output and capture settings:

```java
VideoRecordingOptions options = VideoRecordingOptions.builder()
        .withOutputDirectory(Path.of("target", "videos"))
        .withRetentionPolicy(VideoRetentionPolicy.ALWAYS)
        .withFrameRate(10)
        .withQuality(80)
        .withMaxDimensions(1280, 720)
        .build();

try (VideoRecorder recorder = new VideoRecorder(driver, options)) {
    recorder.start("admin-can-update-profile");
    adminPage.updateProfile();
    Optional<Path> video = recorder.stop(true);
}
```

## Constructors

| Constructor | Description |
|---|---|
| `VideoRecorder(WebDriver driver)` | Uses `VideoRecordingOptions.defaults()`. Requires a `ChromeDriver`. |
| `VideoRecorder(WebDriver driver, VideoRecordingOptions options)` | Uses custom options. Requires a `ChromeDriver` and non-null options. |

## Methods

| Method | Description |
|---|---|
| `void start(String testName)` | Starts recording and uses `testName` to build a safe AVI file name. |
| `Optional<Path> stop(boolean testPassed)` | Stops recording, applies retention, and returns the kept file path if one remains. |
| `void close()` | Stops and discards an in-progress recording if `stop` was not called. |

[Back to video index](README.md) - [Docs home](../README.md)
