package io.github.phillmon.selenium.auth;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Lets a login performed once be reused across many tests and many
 * browser sessions instead of repeating the login flow every time, kept
 * under an arbitrary, caller-chosen profile name so an application with
 * several distinct roles (for example "admin", "user1", "approver") can
 * cache each one independently. Not every test needs this: an
 * application with no login, or a test that deliberately exercises the
 * login flow itself, simply never calls into this class.
 *
 * <p>This class does not know how any particular application logs in.
 * The consumer supplies their own login flow (typically driving their
 * own page objects) as a plain Runnable; SharedAuthState only captures
 * and replays the resulting cookies and browser storage, so it works the
 * same way regardless of what the login page looks like.
 *
 * <p>Shared across every thread in the JVM by design, since the whole
 * point is letting one thread's login be reused by another thread's
 * parallel test. ensureAuthenticated is safe to call concurrently for
 * the same profile from many threads at once: only one of them actually
 * performs the login flow, and the rest wait for it to finish and then
 * reuse the result, so a profile is only ever logged in once per run.
 */
public final class SharedAuthState {
    private static final ConcurrentHashMap<String, AuthSnapshot> snapshots = new ConcurrentHashMap<>();

    private SharedAuthState() {
    }

    /**
     * Returns whether a snapshot is already cached for the given
     * profile. Expects a non-null, non-blank profile name.
     *
     * @param profile the profile name to check
     * @return true if a snapshot is already cached for this profile
     */
    public static boolean isCached(String profile) {
        return snapshots.containsKey(requireProfile(profile));
    }

    /**
     * Captures the given driver's current cookies and storage and caches
     * them under the given profile name, overwriting any snapshot
     * already cached for that profile. Expects a non-null, non-blank
     * profile name and a driver that is already logged in.
     *
     * @param profile the profile name to save the current session under
     * @param driver  the WebDriver, currently logged in, to capture the session from
     */
    public static void save(String profile, WebDriver driver) {
        requireProfile(profile);
        requireDriver(driver);
        snapshots.put(profile, AuthSnapshot.capture(driver));
        LoggerUtil.info("Saved shared auth state for profile '" + profile + "'");
    }

    /**
     * Applies a previously saved profile's cookies and storage onto the
     * given driver, so it becomes logged in without going through the
     * login flow. Expects a non-null, non-blank profile name, the driver
     * to apply the session to, and, when given, a url to navigate to
     * first, since cookies can only be applied once the browser is on a
     * page belonging to the right domain. Returns false without changing
     * the driver if no snapshot is cached for this profile yet.
     *
     * @param profile    the profile name to apply
     * @param driver     the WebDriver to apply the saved session to
     * @param landingUrl a url on the target application to navigate to before
     *                   applying cookies, or null to apply to whatever page the
     *                   driver is already on
     * @return true if a cached snapshot was found and applied, false if none was cached
     */
    public static boolean apply(String profile, WebDriver driver, String landingUrl) {
        requireProfile(profile);
        requireDriver(driver);
        AuthSnapshot snapshot = snapshots.get(profile);
        if (snapshot == null) {
            return false;
        }
        if (landingUrl != null) {
            driver.get(landingUrl);
        }
        snapshot.applyTo(driver);
        LoggerUtil.info("Applied shared auth state for profile '" + profile + "'");
        return true;
    }

    /**
     * Makes sure the given driver is logged in as the given profile,
     * either by reusing a previously cached session or, if none is
     * cached yet, by running the given login flow once and caching the
     * result for every future call with this profile name. Expects a
     * non-null, non-blank profile name, the driver to authenticate, a
     * url on the target application to navigate to before applying a
     * reused session (or null to skip that navigation), and the login
     * flow to run the first time this profile is needed. Safe to call
     * concurrently for the same profile from many threads: the login
     * flow runs at most once per profile per run, and every other caller
     * simply reuses its result.
     *
     * @param profile    the profile name to authenticate as
     * @param driver     the WebDriver to authenticate
     * @param landingUrl a url on the target application to navigate to before
     *                   applying a reused session, or null to skip that navigation
     * @param loginFlow  the login flow to run the first time this profile is needed
     */
    public static void ensureAuthenticated(String profile, WebDriver driver, String landingUrl, Runnable loginFlow) {
        requireProfile(profile);
        requireDriver(driver);
        if (loginFlow == null) {
            throw new IllegalArgumentException("loginFlow must not be null");
        }

        boolean[] performedLogin = {false};
        AuthSnapshot snapshot = snapshots.computeIfAbsent(profile, key -> {
            performedLogin[0] = true;
            loginFlow.run();
            LoggerUtil.info("Logged in and saved shared auth state for profile '" + key + "'");
            return AuthSnapshot.capture(driver);
        });

        if (!performedLogin[0]) {
            if (landingUrl != null) {
                driver.get(landingUrl);
            }
            snapshot.applyTo(driver);
            LoggerUtil.info("Reused shared auth state for profile '" + profile + "' instead of logging in again");
        }
    }

    /**
     * Removes a cached profile, so the next call to apply() returns
     * false, or the next call to ensureAuthenticated() with this profile
     * performs the login flow again. Use this when a cached session is
     * known to have gone stale, for example after it expired or a test
     * deliberately logged it out. Expects a non-null, non-blank profile
     * name; does nothing if no snapshot is cached for it.
     *
     * @param profile the profile name to remove
     */
    public static void forget(String profile) {
        snapshots.remove(requireProfile(profile));
    }

    /**
     * Removes every cached profile. Use this between full test suite
     * runs, or in any situation where every cached session should be
     * treated as stale.
     */
    public static void forgetAll() {
        snapshots.clear();
    }

    private static String requireProfile(String profile) {
        if (profile == null || profile.isBlank()) {
            throw new IllegalArgumentException("profile must not be null or blank");
        }
        return profile;
    }

    private static void requireDriver(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("driver must not be null");
        }
    }
}
