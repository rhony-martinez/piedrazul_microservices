package com.piedrazul.frontend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiErrorParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ApiErrorParser() {
    }

    public static ParsedApiError parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ParsedApiError("No se pudo completar la operación.", Map.of());
        }

        String json = extractJson(raw);
        if (json == null) {
            return new ParsedApiError(simplify(raw), Map.of());
        }

        try {
            JsonNode root = MAPPER.readTree(json);
            Map<String, String> fieldErrors = extractFieldErrors(root);
            String message = firstNonBlank(
                    text(root, "message"),
                    text(root, "error"),
                    simplify(raw)
            );
            return new ParsedApiError(message, fieldErrors);
        } catch (Exception ignored) {
            return new ParsedApiError(simplify(raw), Map.of());
        }
    }

    private static Map<String, String> extractFieldErrors(JsonNode root) {
        JsonNode details = root.get("details");
        if (details == null || !details.isObject()) {
            return Map.of();
        }

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = details.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            fieldErrors.put(entry.getKey(), entry.getValue().asText());
        }
        return Collections.unmodifiableMap(fieldErrors);
    }

    private static String extractJson(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return raw.substring(start, end + 1);
        }
        return null;
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? "" : value.asText();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "Ocurrió un error inesperado.";
    }

    private static String simplify(String raw) {
        int idx = raw.indexOf(": ");
        if (idx >= 0 && idx < raw.length() - 2) {
            return raw.substring(idx + 2).trim();
        }
        return raw.trim();
    }

    public record ParsedApiError(String message, Map<String, String> fieldErrors) {
    }
}
