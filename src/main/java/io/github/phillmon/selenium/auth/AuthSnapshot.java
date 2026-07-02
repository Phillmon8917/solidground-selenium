package io.github.phillmon.selenium.auth;

import io.github.phillmon.selenium.utils.logging.LoggerUtil;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * An immutable capture of everything a browser session needs to look
 * logged in again: its cookies, and, when the driver supports running
 * JavaScript, its localStorage and sessionStorage entries too. Captured
 * from one WebDriver session and later replayed onto another, so a login
 * performed once can be reused without repeating the login flow.
 */
final class AuthSnapshot {
    private final Set<Cookie> cookies;
    private final Map<String, String> localStorage;
    private final Map<String, String> sessionStorage;
    private final String capturedFromUrl;

    private AuthSnapshot(Set<Cookie> cookies, Map<String, String> localStorage,
                          Map<String, String> sessionStorage, String capturedFromUrl) {
        this.cookies = cookies;
        this.localStorage = localStorage;
        this.sessionStorage = sessionStorage;
        this.capturedFromUrl = capturedFromUrl;
    }

    /**
     * Captures the given driver's current cookies and, when possible,
     * its localStorage and sessionStorage. Expects a WebDriver that is
     * currently on the page whose logged-in state should be captured.
     */
    static AuthSnapshot capture(WebDriver driver) {
        Set<Cookie> cookies = new LinkedHashSet<>(driver.manage().getCookies());
        Map<String, String> localStorage = readStorage(driver, "localStorage");
        Map<String, String> sessionStorage = readStorage(driver, "sessionStorage");
        return new AuthSnapshot(cookies, localStorage, sessionStorage, driver.getCurrentUrl());
    }

    /**
     * Replays this snapshot's cookies and storage onto the given driver,
     * then refreshes the page so the application picks up the restored
     * state. Expects a driver that is already on a page belonging to the
     * same origin the snapshot was captured from, since cookies can only
     * be set for the domain currently loaded in the browser.
     */
    void applyTo(WebDriver driver) {
        driver.manage().deleteAllCookies();
        for (Cookie cookie : cookies) {
            try {
                driver.manage().addCookie(cookie);
            } catch (RuntimeException e) {
                LoggerUtil.warning("Could not apply a saved cookie ('" + cookie.getName()
                        + "', captured from " + capturedFromUrl + "): " + e.getMessage());
            }
        }
        writeStorage(driver, "localStorage", localStorage);
        writeStorage(driver, "sessionStorage", sessionStorage);
        driver.navigate().refresh();
    }

    private static Map<String, String> readStorage(WebDriver driver, String storageName) {
        if (!(driver instanceof JavascriptExecutor executor)) {
            return Map.of();
        }
        try {
            String script =
                    "var out = {};"
                    + "for (var i = 0; i < window." + storageName + ".length; i++) {"
                    + "  var key = window." + storageName + ".key(i);"
                    + "  out[key] = window." + storageName + ".getItem(key);"
                    + "}"
                    + "return out;";
            Object result = executor.executeScript(script);
            Map<String, String> storage = new LinkedHashMap<>();
            if (result instanceof Map<?, ?> raw) {
                raw.forEach((key, value) -> storage.put(String.valueOf(key), String.valueOf(value)));
            }
            return storage;
        } catch (RuntimeException e) {
            LoggerUtil.warning("Could not read " + storageName + " while capturing auth state: " + e.getMessage());
            return Map.of();
        }
    }

    private static void writeStorage(WebDriver driver, String storageName, Map<String, String> entries) {
        if (entries.isEmpty() || !(driver instanceof JavascriptExecutor executor)) {
            return;
        }
        try {
            String script =
                    "window." + storageName + ".clear();"
                    + "var entries = arguments[0];"
                    + "for (var key in entries) {"
                    + "  if (Object.prototype.hasOwnProperty.call(entries, key)) {"
                    + "    window." + storageName + ".setItem(key, entries[key]);"
                    + "  }"
                    + "}";
            executor.executeScript(script, entries);
        } catch (RuntimeException e) {
            LoggerUtil.warning("Could not restore " + storageName + " while applying auth state: " + e.getMessage());
        }
    }
}
