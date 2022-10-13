package fr.minemobs.jsonreader.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public non-sealed class JsonArray extends JsonElement {

    private final List<JsonElement> elements;

    public JsonArray() {
        this(new ArrayList<>());
    }

    public JsonArray(JsonElement[] elements) {
        this(Arrays.asList(elements));
    }

    @SuppressWarnings("unchecked")
    public JsonArray(List<JsonElement> elements) {
        super(new ArrayList<>(elements));
        this.elements = (List<JsonElement>) getValue();
    }

    public List<JsonElement> getElements() {
        return List.copyOf(elements);
    }

    public JsonElement getElement(int index) {
        return elements.get(index);
    }

    public int size() {
        return elements.size();
    }

    public Stream<JsonElement> stream() {
        return elements.stream();
    }

    public void forEach(Consumer<JsonElement> consumer) {
        elements.forEach(consumer);
    }
}
