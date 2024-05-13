package com.increatum.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Asserts that the JSON serialization of an object matches the expected resource.
 * If the expected resource is not found, it will be created.
 * <b>Note: The developer and pull request reviewers must examine the expected resource and ensure that
 * it matches the requirements.</b>
 */
public class AssertJson {
    private AssertJson() {
        throw new UnsupportedOperationException();
    }

    /**
     * Assert json representation of the passed object matches with previously saved resource at the path provided.
     * <p>
     * Use Java system property <code>-DassertOverride=true</code> to overwrite previously saved resources.
     */
    public static void assertJson(Object o, String expectedResource) {
        assertJson(o, expectedResource, false, false);
    }

    public static void assertJson(Object o, String expectedResource, boolean addKey, boolean sortProperties) {
        String json = prettyPrint(o, sortProperties);
        if (addKey) {
            json = expectedResource + "\n\n" + json;
        }
        assertData(json, expectedResource);
    }

    /**
     * Assert if value matches with previously saved resource at the path provided.
     * <p>
     * Use Java system property <code>-DassertOverride=true</code> to overwrite previously saved resources.
     */
    public static void assertData(String value, String expectedResource) {
        String expectedValue = JsonUtil.read(expectedResource);
        if ("".equals(expectedValue) || Boolean.getBoolean("assertOverride")) {
            File f = new File("src/test/resources" + expectedResource);
            File parentFile = f.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                throw new IllegalStateException("Resource folder could not be created: " + parentFile.getAbsolutePath());
            }
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                writer.write(JsonUtil.normalize(value));
                writer.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            assertEquals(JsonUtil.normalize(expectedValue), JsonUtil.normalize(value), expectedResource);
        }
    }

    public static String prettyPrint(Object o) {
        return prettyPrint(o, false);
    }

    /**
     * Pretty prints object while keeping a consistent order for {@code Set} elements.
     */
    public static String prettyPrint(Object o, boolean sortProperties) {
        try {
            ObjectMapper om = sortProperties ? //
                    JsonUtil.OM.copy().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true) : JsonUtil.OM;
            return om.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String, Object> readMap(@Nullable String json) {
        try {
            return json == null ? Collections.emptyMap() : JsonUtil.OM.readerFor(Map.class).readValue(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
