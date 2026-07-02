package io.github.phillmon.selenium.video;

import org.testng.annotations.Test;

import java.nio.file.Path;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class VideoRecordingOptionsTest {
    @Test
    public void defaultsAreSensibleForAConsumerWhoConfiguresNothing() {
        VideoRecordingOptions options = VideoRecordingOptions.defaults();
        assertNotNull(options);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsNullOutputDirectory() {
        VideoRecordingOptions.builder().withOutputDirectory(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsNullRetentionPolicy() {
        VideoRecordingOptions.builder().withRetentionPolicy(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsZeroFrameRate() {
        VideoRecordingOptions.builder().withFrameRate(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsNegativeFrameRate() {
        VideoRecordingOptions.builder().withFrameRate(-5);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsQualityAboveOneHundred() {
        VideoRecordingOptions.builder().withQuality(101);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsNegativeQuality() {
        VideoRecordingOptions.builder().withQuality(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void builderRejectsNonPositiveMaxDimensions() {
        VideoRecordingOptions.builder().withMaxDimensions(0, 100);
    }

    @Test
    public void builderAcceptsAFullyCustomConfiguration() {
        VideoRecordingOptions options = VideoRecordingOptions.builder()
                .withOutputDirectory(Path.of("build", "custom-videos"))
                .withRetentionPolicy(VideoRetentionPolicy.ALWAYS)
                .withFrameRate(15)
                .withQuality(90)
                .withMaxDimensions(1280, 720)
                .build();

        assertEquals(options.getOutputDirectory(), Path.of("build", "custom-videos"));
        assertEquals(options.getRetentionPolicy(), VideoRetentionPolicy.ALWAYS);
        assertEquals(options.getFrameRate(), 15);
        assertEquals(options.getQuality(), 90);
        assertEquals(options.getMaxWidth(), Integer.valueOf(1280));
        assertEquals(options.getMaxHeight(), Integer.valueOf(720));
    }
}
