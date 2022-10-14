package fr.minemobs.jsonreader.parser;

public non-sealed class JsonPrimitive extends JsonElement {

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

    public int getValueAsInt() {
        return (int) getValue();
    }

    public double getValueAsDouble() {
        return (double) getValue();
    }

    public boolean getValueAsBoolean() {
        return (boolean) getValue();
    }

    public String getValueAsString() {
        return (String) getValue();
    }

    @Override
    public String toString() {
        return this.getValue() == null ? "null" : this.getValue().toString();
    }

    public Type getType() {
        return this.getValue() instanceof String ? Type.STRING :
                this.getValue() instanceof Number ? Type.NUMBER :
                        this.getValue() instanceof Boolean ? Type.BOOLEAN :
                                Type.DEFAULT;
    }
}