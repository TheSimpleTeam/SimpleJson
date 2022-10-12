package fr.minemobs.jsonreader.parser;

public class JsonPrimitive extends JsonElement {

    public JsonPrimitive(String value) {
        super(value);
    }

    public JsonPrimitive(int value) {
        super(value);
    }

    public JsonPrimitive(double value) {
        super(value);
    }

    public JsonPrimitive(boolean value) {
        super(value);
    }

    public enum Type {
        STRING, NUMBER, BOOLEAN, DEFAULT;
    }

    public String getValueAsString() {
        return (String) this.getValue();
    }

    public int getValueAsInt() {
        return (int) this.getValue();
    }

    public boolean getValueAsBoolean() {
        return (boolean) this.getValue();
    }

    public JsonElement getValueAsJsonElement() {
        return (JsonElement) this.getValue();
    }

    @Override
    public String toString() {
        return this.getValue() == null ? "null" : this.getValue().toString();
    }

    public Type getType() {
        if (this.getValue() instanceof String) return Type.STRING;
        if (this.getValue() instanceof Integer || this.getValue() instanceof Double) return Type.NUMBER;
        if (this.getValue() instanceof Boolean) return Type.BOOLEAN;
        return Type.DEFAULT;
    }
}