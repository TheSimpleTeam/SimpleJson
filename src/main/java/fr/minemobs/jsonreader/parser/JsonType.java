package fr.minemobs.jsonreader.parser;

public enum JsonType {

    OBJECT,
    ARRAY,
    DEFAULT,
    PRIMITIVE;

    public static JsonType fromElement(JsonElement element) {
        return element instanceof JsonObject ? OBJECT : element instanceof JsonArray ? ARRAY : element instanceof JsonPrimitive ? PRIMITIVE : DEFAULT;
    }

}
