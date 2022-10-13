package fr.minemobs.jsonreader.parser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public non-sealed class JsonObject extends JsonElement {

    private final Map<String, JsonElement> elements;

    public JsonObject() {
        this(new LinkedHashMap<>());
    }

    public JsonObject(Map<String, JsonElement> elements) {
        super(new LinkedHashMap<>(elements));
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
}