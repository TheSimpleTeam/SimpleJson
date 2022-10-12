package fr.minemobs.jsonreader.parser;

public abstract class JsonElement {

    private final Object value;

    protected JsonElement(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
