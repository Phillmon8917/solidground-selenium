package io.github.phillmon.selenium.errors;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Runs a page-object step so callers no longer need to write their own
 * try/catch blocks. If the step throws, SafeStep takes a screenshot of the
 * current browser state, reports the failure through FaultReporter, and
 * then rethrows the same failure so the test still fails as normal.
 *
 * Example usage in a page object:
 *
 * <pre>{@code
 * public void loadThePage() {
 *     SafeStep.run(driver, "loadThePage", () -> {
 *         modulars.browserActions.navigateToUrl(url, "loadThePage");
 *         modulars.loadingIndicatorActions.waitForPageLoader("loadThePage");
 *     });
 * }
 * }</pre>
 */
public final class SafeStep {
    private SafeStep() {
    }

    /**
     * Runs a step that returns a value, using a label that is worked out
     * automatically from the calling method's name. Expects the WebDriver
     * for the current browser session and the step to run. Returns
     * whatever the step returns when it succeeds.
     *
     * @param <T> the type of value the step produces
     * @param driver the WebDriver for the current browser session
     * @param step the step to run
     * @return whatever the step returns when it succeeds
     */
    public static <T> T run(WebDriver driver, ThrowingSupplier<T> step) {
        return run(driver, callerLabel(), step);
    }

    /**
     * Runs a step that returns a value, using the given label to describe
     * it in logs and screenshots. Expects the WebDriver for the current
     * browser session, a label describing the step, and the step to run.
     * Returns whatever the step returns when it succeeds. If the step
     * throws, this attaches a screenshot, reports the failure, and
     * rethrows it.
     *
     * @param <T> the type of value the step produces
     * @param driver the WebDriver for the current browser session
     * @param label the label describing the step, used in logs and screenshots
     * @param step the step to run
     * @return whatever the step returns when it succeeds
     */
    public static <T> T run(WebDriver driver, String label, ThrowingSupplier<T> step) {
        try {
            return step.get();
        } catch (Exception error) {
            throw handleFailure(driver, label, error);
        }
    }

    /**
     * Runs a step that does not return anything, using a label that is
     * worked out automatically from the calling method's name. Expects the
     * WebDriver for the current browser session and the step to run.
     *
     * @param driver the WebDriver for the current browser session
     * @param step the step to run
     */
    public static void run(WebDriver driver, ThrowingRunnable step) {
        run(driver, callerLabel(), step);
    }

    /**
     * Runs a step that does not return anything, using the given label to
     * describe it in logs and screenshots. Expects the WebDriver for the
     * current browser session, a label describing the step, and the step
     * to run. If the step throws, this attaches a screenshot, reports the
     * failure, and rethrows it.
     *
     * @param driver the WebDriver for the current browser session
     * @param label the label describing the step, used in logs and screenshots
     * @param step the step to run
     */
    public static void run(WebDriver driver, String label, ThrowingRunnable step) {
        try {
            step.run();
        } catch (Exception error) {
            throw handleFailure(driver, label, error);
        }
    }

    /**
     * Runs a fixed list of steps one after another under the same overall
     * label. Expects the WebDriver for the current browser session, the
     * label for the whole sequence, and the ordered list of steps to run.
     * Each step is tagged with its position in the list, for example
     * "label [step 1]", so if one of the steps fails it is obvious which
     * one from the fault log.
     *
     * @param driver the WebDriver for the current browser session
     * @param label the label for the whole sequence
     * @param steps the ordered list of steps to run
     */
    public static void runSequence(WebDriver driver, String label, List<ThrowingRunnable> steps) {
        for (int i = 0; i < steps.size(); i++) {
            run(driver, label + " [step " + (i + 1) + "]", steps.get(i));
        }
    }

    /**
     * Handles a step that has just failed. Expects the WebDriver, the
     * label of the step that failed, and the exception that was thrown.
     * It takes a screenshot and reports the fault, then returns an
     * exception that the caller should throw: the original exception if
     * it was already unchecked, or a new StepExecutionException wrapping
     * it otherwise.
     */
    private static RuntimeException handleFailure(WebDriver driver, String label, Exception error) {
        attachScreenshot(driver, label);
        FaultReporter.captureFault(error, label);
        return (error instanceof RuntimeException runtimeError)
                ? runtimeError
                : new StepExecutionException(label + " failed", error);
    }

    /**
     * Takes a screenshot of the current browser window and attaches it to
     * the Allure report under the given label. Expects the WebDriver and
     * the label of the step that failed. If the driver cannot take
     * screenshots, or taking the screenshot fails for any reason, this
     * simply logs a warning instead of throwing, so a screenshot problem
     * never hides the real failure.
     */
    private static void attachScreenshot(WebDriver driver, String label) {
        if (!(driver instanceof TakesScreenshot capture)) {
            return;
        }
        try {
            byte[] screenshot = capture.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(label + " - failure screenshot", "image/png",
                    new ByteArrayInputStream(screenshot), "png");
        } catch (Exception screenshotError) {
            LoggerUtil.warning(label + " - unable to capture failure screenshot: "
                    + FaultAnalyzer.getMessage(screenshotError));
        }
    }

    /**
     * Works out a label for the current step by looking at the call stack
     * and finding the first method that does not belong to this class.
     * Returns that method's simple class name and method name joined with
     * a dot, for example "LoginPage.loginAsAdmin". Returns "unknown" if no
     * such frame can be found. This lets callers skip passing a label by
     * hand for the common case.
     */
    private static String callerLabel() {
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if (!frame.getClassName().equals(SafeStep.class.getName())) {
                String simpleName = frame.getClassName().substring(frame.getClassName().lastIndexOf('.') + 1);
                return simpleName + "." + frame.getMethodName();
            }
        }
        return "unknown";
    }
}
