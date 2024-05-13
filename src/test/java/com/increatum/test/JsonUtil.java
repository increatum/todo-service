package com.increatum.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtil {
    public static final TypeReference<Map<String, Object>> REF_MAP = //
            new TypeReference<Map<String, Object>>() {};

    private static final Pattern CRNL_PATTERN = Pattern.compile("\r\n");

    private JsonUtil(){
        throw new IllegalStateException("Utility class"); //SONAR
    }

    public static final ObjectMapper OM = new ObjectMapper() //
            .registerModule(new JavaTimeModule()) //
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd")) //
            .setSerializationInclusion(Include.NON_EMPTY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static String prettyPrint(String jsonString) {
    	try {
			return OM.writerWithDefaultPrettyPrinter().writeValueAsString(OM.readTree(jsonString));
		} catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
		}
    }
    
    public static String nodeToString(JsonNode node) {
        try {
            StringWriter sw = new StringWriter();
            OM.writerWithDefaultPrettyPrinter().writeValue(sw, node);
            return normalize(sw.toString());
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toJsonString(Object o) {
        return nodeToString(OM.convertValue(o, JsonNode.class));
    }

    public static <T> String toJsonString(Object o, TypeReference<T> t) {
        try {
            return OM.writerFor(t).writeValueAsString(o);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T read(String resource, Class<T> c) {
        try (InputStream is = JsonUtil.class.getResourceAsStream(resource)) {
            return OM.readerFor(c).readValue(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T read(String resource, TypeReference<T> t) {
        try(InputStream is = JsonUtil.class.getResourceAsStream(resource)) {
            if(is == null) {
                throw new RuntimeException("No resource " + resource);
            }
            return OM.readerFor(t).readValue(is);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T readString(String json, TypeReference<T> t) {
        try {
            return OM.readerFor(t).readValue(json);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String read(String resource) {
        try(InputStream is = JsonUtil.class.getResourceAsStream(resource)) {
            if(is == null) {
                return "";
            }
            return normalize(StreamUtils.copyToString(is, StandardCharsets.UTF_8));
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Replace <CR><LF> with <LF>
     */
    public static String normalize(String str) {
        return CRNL_PATTERN.matcher(str).replaceAll("\n").trim();
    }

}
