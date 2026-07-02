package io.github.phillmon.selenium.video;

import io.github.phillmon.selenium.modulars.reader.UniqueFileNames;
import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.monte.media.avi.AVIOutputStream;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.latest.page.Page;
import org.openqa.selenium.devtools.latest.page.model.ScreencastFrame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

/**
 * Records a video of a Chrome-backed WebDriver session using Chrome
 * DevTools' screencast, and decides whether to keep or discard the
 * finished recording based on a VideoRetentionPolicy. Does not attach
 * the recording to any report itself: start() and stop() both return or
 * accept plain values, and stop() hands back the java.nio.file.Path of
 * the kept video (or an empty Optional if it was discarded), so a
 * consumer can attach that file to whichever report or listener they
 * already use (Allure, TestNG, a custom TestReporter, or anything else)
 * without this class needing to know about it.
 *
 * <p>Recording works by listening to Chrome's own screencast frames over
 * DevTools rather than capturing the desktop, so it does not need a
 * physical or virtual display and works the same way in headless Chrome
 * as it does with a visible browser window. Frames are muxed into an AVI
 * file with Monte Media's AVIOutputStream.
 */
public class VideoRecorder implements AutoCloseable {
    private final VideoRecordingOptions options;
    private final DevTools devTools;

    private final Object lock = new Object();
    private volatile boolean recording = false;
    private AVIOutputStream writer;
    private int videoTrack;
    private int frameCount;
    private Path currentFile;
    private String currentTestName;

    /**
     * Creates the video recorder using the default recording options.
     * Expects a Chrome-backed WebDriver, since screencast recording
     * relies on Chrome's DevTools protocol. Throws a
     * VideoRecordingException if the driver is not a ChromeDriver.
     *
     * @param driver the Chrome-backed WebDriver to record
     */
    public VideoRecorder(WebDriver driver) {
        this(driver, VideoRecordingOptions.defaults());
    }

    /**
     * Creates the video recorder using the given recording options.
     * Expects a Chrome-backed WebDriver and a non-null
     * VideoRecordingOptions. Throws a VideoRecordingException if the
     * driver is not a ChromeDriver, and an IllegalArgumentException if
     * options is null.
     *
     * @param driver  the Chrome-backed WebDriver to record
     * @param options the options controlling how recordings are captured and retained
     */
    public VideoRecorder(WebDriver driver, VideoRecordingOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("options must not be null");
        }
        if (!(driver instanceof ChromeDriver)) {
            throw new VideoRecordingException(
                    "Video recording requires a ChromeDriver-backed WebDriver (it uses Chrome DevTools' "
                            + "screencast, which is Chrome-only), but got: "
                            + (driver == null ? "null" : driver.getClass().getName()));
        }
        this.options = options;
        this.devTools = ((ChromeDriver) driver).getDevTools();
    }

    /**
     * Starts recording. Expects a short, file-name-safe label describing
     * the test being recorded, used to name the video file and in log
     * messages. Throws a VideoRecordingException if a recording is
     * already in progress or if recording could not be started.
     *
     * @param testName a label describing the test being recorded
     */
    public void start(String testName) {
        synchronized (lock) {
            if (recording) {
                throw new VideoRecordingException(
                        "A recording is already in progress; call stop() before starting another one");
            }
            try {
                Files.createDirectories(options.getOutputDirectory());
                this.currentTestName = testName;
                this.currentFile = options.getOutputDirectory()
                        .resolve(UniqueFileNames.generate(sanitize(testName), "avi"));
                this.writer = null;
                this.frameCount = 0;

                devTools.createSession();
                devTools.send(Page.enable(Optional.empty()));
                devTools.addListener(Page.screencastFrame(), this::onFrame);
                devTools.send(Page.startScreencast(
                        Optional.of(Page.StartScreencastFormat.JPEG),
                        Optional.of(options.getQuality()),
                        Optional.ofNullable(options.getMaxWidth()),
                        Optional.ofNullable(options.getMaxHeight()),
                        Optional.of(1)
                ));

                recording = true;
                LoggerUtil.info("Started video recording for '" + testName + "' -> " + currentFile);
            } catch (IOException | RuntimeException e) {
                throw new VideoRecordingException("Failed to start video recording for '" + testName + "'", e);
            }
        }
    }

    /**
     * Stops recording and applies the configured retention policy.
     * Expects whether the test being recorded passed, which decides
     * whether the recording is kept: always kept under ALWAYS, kept only
     * when testPassed is false under ON_FAILURE_ONLY. Returns the path
     * to the kept video, or an empty Optional if the recording was
     * discarded or if no recording was in progress. Throws a
     * VideoRecordingException if the recording could not be finalized.
     *
     * @param testPassed whether the test being recorded passed
     * @return the path to the kept video, or an empty Optional if it was discarded
     */
    public Optional<Path> stop(boolean testPassed) {
        synchronized (lock) {
            if (!recording) {
                return Optional.empty();
            }
            recording = false;

            try {
                devTools.send(Page.stopScreencast());
            } catch (RuntimeException e) {
                LoggerUtil.warning("Could not cleanly stop the screencast: " + e.getMessage());
            }

            if (writer == null) {
                LoggerUtil.warning("Video recording for '" + currentTestName
                        + "' captured no frames; nothing to keep");
                return Optional.empty();
            }

            int frames = frameCount;
            try {
                writer.close();
            } catch (IOException e) {
                throw new VideoRecordingException(
                        "Failed to finalize video recording for '" + currentTestName + "'", e);
            } finally {
                writer = null;
            }

            boolean keep = options.getRetentionPolicy() == VideoRetentionPolicy.ALWAYS || !testPassed;
            if (!keep) {
                deleteQuietly(currentFile);
                LoggerUtil.info("Discarded video recording for '" + currentTestName + "' (" + frames
                        + " frames): test passed and the retention policy is ON_FAILURE_ONLY");
                return Optional.empty();
            }

            LoggerUtil.info("Kept video recording for '" + currentTestName + "' (" + frames
                    + " frames) at " + currentFile);
            return Optional.of(currentFile);
        }
    }

    /**
     * Stops and discards an in-progress recording as a safety net, in
     * case a caller forgets to call stop() explicitly, for example when
     * an unexpected exception skips past it. Prefer calling stop()
     * yourself, since close() always discards the recording regardless
     * of the configured retention policy. Does nothing if no recording
     * is in progress.
     */
    @Override
    public void close() {
        synchronized (lock) {
            if (!recording) {
                return;
            }
            LoggerUtil.warning("VideoRecorder closed while still recording '" + currentTestName
                    + "' (stop() was never called); discarding the in-progress recording");
            recording = false;
            try {
                devTools.send(Page.stopScreencast());
            } catch (RuntimeException ignored) {
                // best effort; the recording is being discarded regardless
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // best effort; the file is about to be deleted anyway
                }
                writer = null;
                deleteQuietly(currentFile);
            }
        }
    }

    private void onFrame(ScreencastFrame frame) {
        try {
            byte[] jpegBytes = Base64.getDecoder().decode(frame.getData());
            synchronized (lock) {
                if (recording) {
                    if (writer == null) {
                        writer = newWriterSizedForFrame(jpegBytes);
                    }
                    writer.writeSample(videoTrack, jpegBytes, 0, jpegBytes.length, true);
                    frameCount++;
                }
            }
        } catch (IOException | RuntimeException e) {
            LoggerUtil.warning("Dropped a video frame for '" + currentTestName + "': " + e.getMessage());
        } finally {
            try {
                devTools.send(Page.screencastFrameAck(frame.getSessionId()));
            } catch (RuntimeException ignored) {
                // the session may already be closing; nothing useful to do about a failed ack
            }
        }
    }

    private AVIOutputStream newWriterSizedForFrame(byte[] firstFrameJpeg) throws IOException {
        int[] dimensions = readJpegDimensions(firstFrameJpeg);
        AVIOutputStream out = new AVIOutputStream(currentFile.toFile());
        videoTrack = out.addVideoTrack("MJPG", 1, options.getFrameRate(), dimensions[0], dimensions[1], 24, 1);
        return out;
    }

    /**
     * Reads the pixel width and height straight out of a JPEG's SOF
     * marker. Used instead of javax.imageio.ImageIO, since some
     * ImageIO plugins on the classpath register a JPEG ImageReaderSpi
     * that does not correctly decode standard baseline JPEGs, and
     * ImageIO offers no way to pick a specific plugin over another.
     */
    private int[] readJpegDimensions(byte[] jpegBytes) throws IOException {
        int offset = 2;
        while (offset < jpegBytes.length - 1) {
            if ((jpegBytes[offset] & 0xFF) != 0xFF) {
                offset++;
                continue;
            }
            int marker = jpegBytes[offset + 1] & 0xFF;
            if (marker == 0x01 || (marker >= 0xD0 && marker <= 0xD9)) {
                offset += 2;
                continue;
            }
            int segmentLength = ((jpegBytes[offset + 2] & 0xFF) << 8) | (jpegBytes[offset + 3] & 0xFF);
            boolean isStartOfFrame = marker >= 0xC0 && marker <= 0xCF && marker != 0xC4 && marker != 0xC8 && marker != 0xCC;
            if (isStartOfFrame) {
                int height = ((jpegBytes[offset + 5] & 0xFF) << 8) | (jpegBytes[offset + 6] & 0xFF);
                int width = ((jpegBytes[offset + 7] & 0xFF) << 8) | (jpegBytes[offset + 8] & 0xFF);
                return new int[]{width, height};
            }
            offset += 2 + segmentLength;
        }
        throw new IOException("Could not find a JPEG SOF marker to determine video dimensions");
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LoggerUtil.warning("Could not delete discarded video file " + path + ": " + e.getMessage());
        }
    }

    private String sanitize(String testName) {
        String label = (testName == null || testName.isBlank()) ? "recording" : testName;
        return label.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
