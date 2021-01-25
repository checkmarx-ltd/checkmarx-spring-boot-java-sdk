package com.checkmarx.sdk.config;

import com.checkmarx.sdk.exception.ASTRuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertyFileLoader {
    private static final String DEFAULT_FILENAME = "common.properties";

    @Getter(lazy = true)
    private static final PropertyFileLoader defaultInstance = new PropertyFileLoader(DEFAULT_FILENAME);

    private final Properties properties;

    /**
     * Loads properties from resources.
     *
     * @param filenames list of resource filenames to load properties from.
     *                  If the same property appears several times in the files, the property value from a file will be overridden with the value from the next file.
     * @throws ASTRuntimeException if no filenames is provided, or if an error occurred while loading a file.
     */
    public PropertyFileLoader(String... filenames) {
        if (filenames.length == 0) {
            throw new ASTRuntimeException("Please provide at least one filename.");
        }

        properties = new Properties();
        for (String filename : filenames) {
            Properties singleFileProperties = getPropertiesFromResource(filename);
            properties.putAll(singleFileProperties);
        }
    }

    private Properties getPropertiesFromResource(String resourceName) {
        Properties result = new Properties();

        log.debug("Loading properties from resource: {}", resourceName);
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (input != null) {
                result.load(input);
            } else {
                log.warn("Unable to find resource: {}, skipping.", resourceName);
            }
        } catch (IOException e) {
            throw new ASTRuntimeException(String.format("Error loading the '%s' resource.", resourceName), e);
        }
        return result;
    }

    public String get(String propertyName) {
        return properties.getProperty(propertyName);
    }
}
