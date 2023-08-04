package fr.minemobs.jsonparser.types;

import java.util.Optional;

public class JsonPrimitive implements JsonElement {
    public enum PrimitiveType {
        NUMBER, STRING
    }

    private final String stringValue;
    private final Number number;
    private final PrimitiveType type;

    private JsonPrimitive(PrimitiveType type, String string, Number number) {
        this.type = type;
        this.stringValue = string;
        this.number = number;
    }

    public JsonPrimitive(String string) {
        this(PrimitiveType.STRING, string, null);
    }

    public JsonPrimitive(Number value) {
        this(PrimitiveType.NUMBER, null, value);
    }

    public Optional<String> getString() {
        return Optional.ofNullable(stringValue);
    }

    public Optional<Number> getNumber() {
        return Optional.ofNullable(number);
    }

    public PrimitiveType getPrimitiveType() {
        return type;
    }

    @Override
    public Types getType() {
        return Types.PRIMITIVE;
    }
}
