package fr.minemobs.jsonparser.types;

public interface JsonElement {
    Types getType();
    default JsonObject asJsonObject() {
        return (JsonObject) this;
    }

    default JsonArray asJsonArray() {
        return (JsonArray) this;
    }

    default JsonPrimitive asJsonPrimitive() {
        return (JsonPrimitive) this;
    }
}
