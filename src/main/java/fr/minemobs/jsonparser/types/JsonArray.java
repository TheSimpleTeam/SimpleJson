package fr.minemobs.jsonparser.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JsonArray implements JsonElement {
    private final List<JsonElement> list = new ArrayList<>();

    @Override
    public Types getType() {
        return Types.ARRAY;
    }

    public void addElement(JsonElement element) {
        this.list.add(element);
    }

    public JsonElement removeElement(int index) {
        return this.list.remove(index);
    }

    public Optional<JsonElement> getElement(int index) {
        return Optional.ofNullable(this.list.get(index));
    }

    public List<JsonElement> getUnmodifiableElements() {
        return Collections.unmodifiableList(list);
    }
}
