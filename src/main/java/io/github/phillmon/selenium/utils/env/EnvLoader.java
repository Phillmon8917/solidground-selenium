package io.github.phillmon.selenium.utils.env;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Resolves configuration values needed to run the tests, such as the
 * admin username, password, and base url. Looks for each value in the
 * project's .env file first, then falls back to the operating system's
 * environment variables, then to Java system properties, so the same
 * code works whether a value is set locally in a .env file or injected
 * by a CI pipeline.
 */
public class EnvLoader {
    private static Dotenv dotenv = Dotenv.load();
    private EnvLoader(){
    }

    /**
     * Returns the admin username configured for the tests. Looks it up
     * under the key ADMIN_USERNAME. Throws an IllegalStateException if it
     * is not set anywhere.
     *
     * @return the configured admin username
     */
    public static String getAdminUsername(){
        return get("ADMIN_USERNAME");
    }

    /**
     * Returns the admin password configured for the tests. Looks it up
     * under the key ADMIN_PASSWORD. Throws an IllegalStateException if it
     * is not set anywhere.
     *
     * @return the configured admin password
     */
    public static String getAdminPassword(){
        return get("ADMIN_PASSWORD");
    }

    /**
     * Returns the base url the tests should run against. Looks it up
     * under the key URL. Throws an IllegalStateException if it is not
     * set anywhere.
     *
     * @return the configured base url
     */
    public static String getUrl(){
        return get("URL");
    }

    /**
     * Looks up a named property and returns its value. Expects the
     * property name to look up. Checks the .env file first, then the
     * operating system's environment variables, then Java system
     * properties, and returns the first value found. Throws an
     * IllegalStateException if the property is not found in any of
     * those three places.
     *
     * @param property the name of the property to look up
     * @return the resolved value of the property
     */
    public static String get(String property){
        String value = dotenv.get(property);
        if (value != null) {
            return value;
        }

        value = System.getenv(property);
        if (value != null) {
            return value;
        }

        value = System.getProperty(property);
        if (value != null) {
            return value;
        }

        throw new IllegalStateException("Property '" + property + "' not found in .env, environment variables, or system properties");
    }
}
