package io.github.phillmon.selenium.video;

import java.nio.file.Path;

/**
 * Holds the settings that control how VideoRecorder captures and retains
 * a recording: where finished videos are saved, whether a recording is
 * kept only when its test fails or always kept, the capture frame rate
 * and JPEG quality, and an optional cap on the captured frame size. Build
 * one with the Builder class, or call defaults() for a plain
 * configuration that keeps recordings only for failed tests.
 */
public final class VideoRecordingOptions {
    private final Path outputDirectory;
    private final VideoRetentionPolicy retentionPolicy;
    private final int frameRate;
    private final int quality;
    private final Integer maxWidth;
    private final Integer maxHeight;

    private VideoRecordingOptions(Builder builder) {
        this.outputDirectory = builder.outputDirectory;
        this.retentionPolicy = builder.retentionPolicy;
        this.frameRate = builder.frameRate;
        this.quality = builder.quality;
        this.maxWidth = builder.maxWidth;
        this.maxHeight = builder.maxHeight;
    }

    /**
     * Returns a configuration with every setting left at its default
     * value: recordings saved under target/videos, kept only for failed
     * tests, 8 frames per second, JPEG quality 70, and no cap on frame
     * size.
     *
     * @return a VideoRecordingOptions instance with every setting at its default value
     */
    public static VideoRecordingOptions defaults() {
        return builder().build();
    }

    /**
     * Returns a new Builder so a caller can set only the options it cares
     * about and leave the rest at their defaults.
     *
     * @return a new Builder for configuring a VideoRecordingOptions instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the directory finished recordings are saved into.
     */
    Path getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Returns which recordings should be kept once a test finishes.
     */
    VideoRetentionPolicy getRetentionPolicy() {
        return retentionPolicy;
    }

    /**
     * Returns the nominal frames-per-second the recording is captured
     * and played back at.
     */
    int getFrameRate() {
        return frameRate;
    }

    /**
     * Returns the JPEG quality (0 to 100) used for each captured frame.
     */
    int getQuality() {
        return quality;
    }

    /**
     * Returns the maximum frame width to request from the browser, or
     * null to use the browser's natural viewport width.
     */
    Integer getMaxWidth() {
        return maxWidth;
    }

    /**
     * Returns the maximum frame height to request from the browser, or
     * null to use the browser's natural viewport height.
     */
    Integer getMaxHeight() {
        return maxHeight;
    }

    /**
     * Fluent builder for VideoRecordingOptions. Call the with methods for
     * only the settings that need to change, then call build() to create
     * the final, immutable VideoRecordingOptions instance.
     */
    public static class Builder {
        private Path outputDirectory = Path.of("target", "videos");
        private VideoRetentionPolicy retentionPolicy = VideoRetentionPolicy.ON_FAILURE_ONLY;
        private int frameRate = 8;
        private int quality = 70;
        private Integer maxWidth;
        private Integer maxHeight;

        /**
         * Creates a builder pre-populated with the default output
         * directory, retention policy, frame rate, and quality.
         */
        public Builder() {
        }

        /**
         * Sets the directory finished recordings are saved into, instead
         * of the default target/videos. Expects a non-null Path and
         * throws an IllegalArgumentException if null is passed in.
         *
         * @param outputDirectory the directory finished recordings should be saved into
         * @return this builder, so calls can be chained
         */
        public Builder withOutputDirectory(Path outputDirectory) {
            if (outputDirectory == null) {
                throw new IllegalArgumentException("outputDirectory must not be null");
            }
            this.outputDirectory = outputDirectory;
            return this;
        }

        /**
         * Sets which recordings should be kept once a test finishes,
         * instead of the default of keeping only failed tests' videos.
         * Expects a non-null VideoRetentionPolicy and throws an
         * IllegalArgumentException if null is passed in.
         *
         * @param retentionPolicy which recordings should be kept once a test finishes
         * @return this builder, so calls can be chained
         */
        public Builder withRetentionPolicy(VideoRetentionPolicy retentionPolicy) {
            if (retentionPolicy == null) {
                throw new IllegalArgumentException("retentionPolicy must not be null");
            }
            this.retentionPolicy = retentionPolicy;
            return this;
        }

        /**
         * Sets the nominal frames-per-second the recording is captured
         * and played back at, instead of the default of 8. Expects a
         * frame rate of at least 1 and throws an IllegalArgumentException
         * otherwise.
         *
         * @param frameRate the frames-per-second to capture and play back at
         * @return this builder, so calls can be chained
         */
        public Builder withFrameRate(int frameRate) {
            if (frameRate < 1) {
                throw new IllegalArgumentException("frameRate must be at least 1");
            }
            this.frameRate = frameRate;
            return this;
        }

        /**
         * Sets the JPEG quality used for each captured frame, instead of
         * the default of 70. Expects a value from 0 to 100 and throws an
         * IllegalArgumentException otherwise.
         *
         * @param quality the JPEG quality, from 0 to 100
         * @return this builder, so calls can be chained
         */
        public Builder withQuality(int quality) {
            if (quality < 0 || quality > 100) {
                throw new IllegalArgumentException("quality must be between 0 and 100");
            }
            this.quality = quality;
            return this;
        }

        /**
         * Caps the captured frame size to the given maximum width and
         * height, instead of using the browser's natural viewport size.
         * Expects positive values for both and throws an
         * IllegalArgumentException otherwise.
         *
         * @param maxWidth  the maximum frame width to request from the browser
         * @param maxHeight the maximum frame height to request from the browser
         * @return this builder, so calls can be chained
         */
        public Builder withMaxDimensions(int maxWidth, int maxHeight) {
            if (maxWidth < 1 || maxHeight < 1) {
                throw new IllegalArgumentException("maxWidth and maxHeight must both be positive");
            }
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            return this;
        }

        /**
         * Creates the final, immutable VideoRecordingOptions using
         * whatever settings were configured on this builder.
         *
         * @return the immutable VideoRecordingOptions built from this builder's settings
         */
        public VideoRecordingOptions build() {
            return new VideoRecordingOptions(this);
        }
    }
}
