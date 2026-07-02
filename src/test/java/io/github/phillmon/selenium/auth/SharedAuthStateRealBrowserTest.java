package io.github.phillmon.selenium.auth;

import com.sun.net.httpserver.HttpServer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Drives SharedAuthState against a real, tiny local web app (a cookie
 * plus localStorage based login, served over real http so cookies behave
 * the way they would on any real application) using real headless Chrome
 * sessions, to confirm login-once-and-reuse actually works: a session
 * captured on one browser genuinely lets a second, freshly created
 * browser skip the login form, multiple profiles stay independent, and
 * concurrent callers for the same profile only log in once.
 */
public class SharedAuthStateRealBrowserTest {
    private static HttpServer server;
    private static String appUrl;

    private WebDriver driver;

    @BeforeClass
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/app", exchange -> {
            byte[] body = APP_HTML.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(body);
            }
        });
        server.start();
        appUrl = "http://localhost:" + server.getAddress().getPort() + "/app";
    }

    @AfterClass
    public void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @BeforeMethod
    public void setUp() {
        SharedAuthState.forgetAll();
        driver = newHeadlessDriver();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        SharedAuthState.forgetAll();
    }

    @Test
    public void ensureAuthenticatedLogsInOnceAndLetsADifferentBrowserReuseTheSession() {
        AtomicInteger firstCallLogins = new AtomicInteger();
        SharedAuthState.ensureAuthenticated("admin", driver, appUrl, () -> {
            firstCallLogins.incrementAndGet();
            loginAs(driver, "admin");
        });
        assertEquals(firstCallLogins.get(), 1, "the first call for a never-cached profile should perform the login");
        assertEquals(welcomeText(driver), "Welcome, admin (ADMIN)");

        WebDriver secondDriver = newHeadlessDriver();
        try {
            AtomicInteger secondCallLogins = new AtomicInteger();
            SharedAuthState.ensureAuthenticated("admin", secondDriver, appUrl, () -> {
                secondCallLogins.incrementAndGet();
                loginAs(secondDriver, "admin");
            });

            assertEquals(secondCallLogins.get(), 0,
                    "a second, different browser should reuse the cached session instead of logging in again");
            assertEquals(welcomeText(secondDriver), "Welcome, admin (ADMIN)",
                    "the second browser should show as logged in purely from the reused cookies/storage");
        } finally {
            secondDriver.quit();
        }
    }

    @Test
    public void multipleProfilesAreCachedIndependently() {
        SharedAuthState.ensureAuthenticated("admin", driver, appUrl, () -> loginAs(driver, "admin"));
        assertEquals(welcomeText(driver), "Welcome, admin (ADMIN)");

        SharedAuthState.ensureAuthenticated("user1", driver, appUrl, () -> loginAs(driver, "user1"));
        assertEquals(welcomeText(driver), "Welcome, user1 (USER1)");

        assertTrue(SharedAuthState.apply("admin", driver, appUrl));
        assertEquals(welcomeText(driver), "Welcome, admin (ADMIN)",
                "reapplying the admin profile should not have been corrupted by logging in as user1 afterwards");

        assertTrue(SharedAuthState.apply("user1", driver, appUrl));
        assertEquals(welcomeText(driver), "Welcome, user1 (USER1)");
    }

    @Test
    public void concurrentEnsureAuthenticatedForTheSameProfileOnlyLogsInOnce() throws Exception {
        int browserCount = 5;
        AtomicInteger totalLogins = new AtomicInteger();
        List<WebDriver> drivers = new ArrayList<>();
        for (int i = 0; i < browserCount; i++) {
            drivers.add(newHeadlessDriver());
        }

        ExecutorService pool = Executors.newFixedThreadPool(browserCount);
        try {
            List<Callable<Void>> tasks = new ArrayList<>();
            for (WebDriver oneDriver : drivers) {
                tasks.add(() -> {
                    SharedAuthState.ensureAuthenticated("concurrent-user", oneDriver, appUrl, () -> {
                        totalLogins.incrementAndGet();
                        loginAs(oneDriver, "concurrent-user");
                    });
                    return null;
                });
            }

            List<Future<Void>> futures = pool.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get(60, TimeUnit.SECONDS);
            }

            assertEquals(totalLogins.get(), 1,
                    "exactly one of the concurrent callers should have performed the actual login");
            for (WebDriver oneDriver : drivers) {
                assertEquals(welcomeText(oneDriver), "Welcome, concurrent-user (CONCURRENT-USER)");
            }
        } finally {
            pool.shutdown();
            drivers.forEach(WebDriver::quit);
        }
    }

    @Test
    public void applyReturnsFalseWhenNoProfileIsCachedYet() {
        driver.get(appUrl);
        boolean applied = SharedAuthState.apply("never-logged-in", driver, appUrl);

        assertFalse(applied);
        assertFalse(isShowingWelcome(driver), "the page should still be on the login form");
    }

    @Test
    public void forgetForcesTheNextEnsureAuthenticatedCallToLogInAgain() {
        AtomicInteger logins = new AtomicInteger();
        SharedAuthState.ensureAuthenticated("temp", driver, appUrl, () -> {
            logins.incrementAndGet();
            loginAs(driver, "temp");
        });
        assertEquals(logins.get(), 1);

        SharedAuthState.forget("temp");

        AtomicInteger loginsAfterForget = new AtomicInteger();
        SharedAuthState.ensureAuthenticated("temp", driver, appUrl, () -> {
            loginsAfterForget.incrementAndGet();
            loginAs(driver, "temp");
        });
        assertEquals(loginsAfterForget.get(), 1, "forget() should force the login flow to run again");
    }

    private void loginAs(WebDriver targetDriver, String username) {
        // Always start from a clean slate: a driver reused for a second login in the same
        // test would otherwise still carry the previous login's session cookie, leaving the
        // login form hidden and not interactable.
        targetDriver.get(appUrl);
        targetDriver.manage().deleteAllCookies();
        targetDriver.navigate().refresh();
        new WebDriverWait(targetDriver, Duration.ofSeconds(5))
                .until(d -> d.findElement(By.id("login-form")).isDisplayed());

        targetDriver.findElement(By.id("username")).sendKeys(username);
        targetDriver.findElement(By.id("login-button")).click();
        new WebDriverWait(targetDriver, Duration.ofSeconds(5)).until(d -> isShowingWelcome(targetDriver));
    }

    private boolean isShowingWelcome(WebDriver targetDriver) {
        return targetDriver.findElement(By.id("welcome")).isDisplayed();
    }

    private String welcomeText(WebDriver targetDriver) {
        new WebDriverWait(targetDriver, Duration.ofSeconds(5)).until(d -> isShowingWelcome(targetDriver));
        return targetDriver.findElement(By.id("welcome")).getText();
    }

    private WebDriver newHeadlessDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        return new ChromeDriver(options);
    }

    private static final String APP_HTML = "<!DOCTYPE html>"
            + "<html><head><title>Auth Fixture</title></head><body>"
            + "<div id=\"login-form\">"
            + "<input id=\"username\" type=\"text\" />"
            + "<button id=\"login-button\" onclick=\""
            + "document.cookie = 'session=' + document.getElementById('username').value + '; path=/';"
            + "localStorage.setItem('displayName', document.getElementById('username').value.toUpperCase());"
            + "window.location.reload();\">Login</button>"
            + "</div>"
            + "<div id=\"welcome\" style=\"display:none;\"></div>"
            + "<script>"
            + "function getCookie(name) {"
            + "  var match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));"
            + "  return match ? match[2] : null;"
            + "}"
            + "var session = getCookie('session');"
            + "if (session) {"
            + "  document.getElementById('login-form').style.display = 'none';"
            + "  var el = document.getElementById('welcome');"
            + "  el.style.display = 'block';"
            + "  el.innerText = 'Welcome, ' + session + ' (' + (localStorage.getItem('displayName') || '') + ')';"
            + "}"
            + "</script>"
            + "</body></html>";
}
