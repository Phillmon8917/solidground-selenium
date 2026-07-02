package io.github.phillmon.selenium.video;

import io.github.phillmon.selenium.modulars.network.CoordinationOptions;
import io.github.phillmon.selenium.modulars.network.NetworkValidationActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Drives VideoRecorder against a real, headless Chrome session to confirm
 * it actually produces a playable video via CDP screencast (not just that
 * the retention bookkeeping compiles), and that both retention policies
 * behave correctly against genuine recordings.
 */
public class VideoRecorderRealBrowserTest {
    private WebDriver driver;
    private Path outputDirectory;

    @BeforeMethod
    public void setUpDriver() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        driver = new ChromeDriver(options);
        outputDirectory = Files.createTempDirectory("video-recorder-test");

        URL resource = getClass().getClassLoader().getResource("fixtures/parallel-test-page.html");
        driver.get(resource.toExternalForm());
    }

    @AfterMethod
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void keepsARealRecordingWhenTheTestFailsUnderOnFailureOnlyPolicy() throws Exception {
        Optional<Path> video;
        try (VideoRecorder recorder = new VideoRecorder(driver, VideoRecordingOptions.builder()
                .withOutputDirectory(outputDirectory)
                .withRetentionPolicy(VideoRetentionPolicy.ON_FAILURE_ONLY)
                .withFrameRate(10)
                .build())) {
            recorder.start("failing-test");
            interactWithPage();
            video = recorder.stop(false);
        }

        assertTrue(video.isPresent(), "a failed test's recording should be kept under ON_FAILURE_ONLY");
        assertTrue(Files.exists(video.get()));
        assertTrue(Files.size(video.get()) > 0);
        assertTrue(frameCountInVideo(video.get()) > 0, "the kept video should contain at least one real frame");
    }

    @Test
    public void discardsTheRealRecordingWhenTheTestPassesUnderOnFailureOnlyPolicy() throws Exception {
        Optional<Path> video;
        try (VideoRecorder recorder = new VideoRecorder(driver, VideoRecordingOptions.builder()
                .withOutputDirectory(outputDirectory)
                .withRetentionPolicy(VideoRetentionPolicy.ON_FAILURE_ONLY)
                .withFrameRate(10)
                .build())) {
            recorder.start("passing-test");
            interactWithPage();
            video = recorder.stop(true);
        }

        assertFalse(video.isPresent(), "a passed test's recording should be discarded under ON_FAILURE_ONLY");
    }

    @Test
    public void keepsARealRecordingEvenWhenTheTestPassesUnderAlwaysPolicy() throws Exception {
        Optional<Path> video;
        try (VideoRecorder recorder = new VideoRecorder(driver, VideoRecordingOptions.builder()
                .withOutputDirectory(outputDirectory)
                .withRetentionPolicy(VideoRetentionPolicy.ALWAYS)
                .withFrameRate(10)
                .build())) {
            recorder.start("passing-test-always-kept");
            interactWithPage();
            video = recorder.stop(true);
        }

        assertTrue(video.isPresent(), "recordings should always be kept under ALWAYS regardless of outcome");
        assertTrue(Files.exists(video.get()));
        assertTrue(frameCountInVideo(video.get()) > 0);
    }

    @Test
    public void recordingWorksAlongsideNetworkValidationOnTheSameDriver() throws Exception {
        // Exercises both DevTools consumers (video + network validation) sharing one
        // ChromeDriver, since both go through driver.getDevTools() independently.
        try (VideoRecorder recorder = new VideoRecorder(driver, VideoRecordingOptions.builder()
                .withOutputDirectory(outputDirectory)
                .withRetentionPolicy(VideoRetentionPolicy.ALWAYS)
                .withFrameRate(10)
                .build())) {

            recorder.start("recording-with-network-validation");

            NetworkValidationActions networkActions = new NetworkValidationActions(driver);
            networkActions.coordinateActionsAndResponses(new CoordinationOptions(
                    java.util.List.of(() -> {
                        driver.findElement(By.id("input-field")).sendKeys("shared-devtools-session");
                        driver.findElement(By.id("set-button")).click();
                    })));

            Thread.sleep(1500);
            Optional<Path> video = recorder.stop(true);

            assertTrue(video.isPresent());
            assertTrue(frameCountInVideo(video.get()) > 0);
        }
    }

    private void interactWithPage() throws InterruptedException {
        driver.findElement(By.id("input-field")).sendKeys("hello from video test");
        driver.findElement(By.id("set-button")).click();
        Thread.sleep(1500); // give CDP screencast time to deliver at least one frame
    }

    /**
     * Reads dwTotalFrames straight out of the AVI file's avih header, at
     * the fixed offset the RIFF/AVI format specification places it at.
     * Monte Media's own AVIReader.getChunkCount() is not implemented in
     * the version this project depends on (it unconditionally returns 0),
     * so this reads the standard header field directly instead.
     */
    private long frameCountInVideo(Path videoFile) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(videoFile.toFile(), "r")) {
            raf.seek(48);
            int b1 = raf.read();
            int b2 = raf.read();
            int b3 = raf.read();
            int b4 = raf.read();
            return (b1 & 0xFFL) | ((b2 & 0xFFL) << 8) | ((b3 & 0xFFL) << 16) | ((b4 & 0xFFL) << 24);
        }
    }
}
