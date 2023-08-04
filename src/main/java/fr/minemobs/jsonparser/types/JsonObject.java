package fr.minemobs.jsonparser.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonObject implements JsonElement {
    private final Map<String, JsonElement> elementMap = new HashMap<>();

    @Override
    public Types getType() {
        return Types.OBJECT;
    }

    public void putElement(String key, JsonElement element) {
        this.elementMap.put(key, element);
    }

    public JsonElement removeElement(String key) {
        return this.elementMap.remove(key);
    }

    public Optional<JsonElement> getElement(String key) {
        return Optional.ofNullable(this.elementMap.get(key));
    }
}
