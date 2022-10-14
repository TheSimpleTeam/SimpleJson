package fr.minemobs.jsonreader.parser;

public abstract sealed class JsonElement permits JsonArray, JsonObject, JsonPrimitive {

    private final Object value;

    protected JsonElement(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public JsonObject getAsJsonObject() {
        return (JsonObject) this;
    }

    public JsonPrimitive getAsJsonPrimitive() {
        return (JsonPrimitive) this;
    }

    public JsonArray getAsJsonArray() {
        return (JsonArray) this;
    }

    public <T> T getValue(Class<T> type) {
        return type.cast(value);
    }
}
