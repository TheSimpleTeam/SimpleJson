package fr.minemobs.jsonreader.parser;

import fr.minemobs.jsonreader.JsonReader;
import fr.minemobs.jsonreader.MalformedJsonException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject extends JsonElement {

    private final Map<String, JsonElement> elements;

    public JsonObject() {
        this(new LinkedHashMap<>());
    }

    public JsonObject(Map<String, JsonElement> elements) {
        super(elements);
        this.elements = elements;
    }

    public void put(String key, JsonElement element) {
        elements.put(key, element);
    }

    public JsonElement get(String key) {
        return elements.get(key);
    }

    public boolean containsKey(String key) {
        return elements.containsKey(key);
    }

    public Map<String, JsonElement> getElements() {
        return Collections.unmodifiableMap(elements);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        elements.forEach((key, value) -> builder.append("\"").append(key).append("\":").append(value.toString()).append(","));
        if (builder.length() > 1) {
            builder.deleteCharAt(builder.length() - 1);
        } else if(builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }
}
