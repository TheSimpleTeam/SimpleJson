package fr.minemobs.jsonreader.parser;

public abstract sealed class JsonElement permits JsonArray, JsonObject, JsonPrimitive {

    private final Object value;

    protected JsonElement(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public <T> T getValue(Class<T> type) {
        return type.cast(value);
    }
}
