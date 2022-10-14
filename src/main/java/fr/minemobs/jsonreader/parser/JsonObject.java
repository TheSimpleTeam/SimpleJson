package fr.minemobs.jsonreader.parser;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    public <T extends JsonElement> T get(String key, Class<T> type) {
        return (T) elements.get(key);
    }

    public boolean containsKey(String key) {
        return elements.containsKey(key);
    }

    public Map<String, JsonElement> getElements() {
        return Collections.unmodifiableMap(elements);
    }

    @Override
    public String toString() {
        return "JsonObject{" +
                "elements={" + elements.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(",")) + "}" +
                '}';
    }
}