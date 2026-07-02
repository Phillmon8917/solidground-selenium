# VideoRecordingException

Package: `io.github.phillmon.selenium.video`

Runtime exception thrown when [VideoRecorder](VideoRecorder.md) cannot create,
capture, or finalize a video recording. Common causes are using a non-Chrome
driver, failing to open a DevTools screencast session, or being unable to write
the output AVI file.

## Usage example

```java
try (VideoRecorder recorder = new VideoRecorder(driver)) {
    recorder.start("payment-flow");
    paymentPage.pay();
    recorder.stop(false);
} catch (VideoRecordingException e) {
    LoggerUtil.warning("Video recording was not available: " + e.getMessage());
}
```

## Constructors

| Constructor | Description |
|---|---|
| `VideoRecordingException(String message)` | Creates the exception with a message only. |
| `VideoRecordingException(String message, Throwable cause)` | Creates the exception with a message and original cause. |

[Back to video index](README.md) - [Docs home](../README.md)
