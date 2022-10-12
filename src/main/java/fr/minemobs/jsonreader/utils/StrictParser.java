package fr.minemobs.jsonreader.utils;

import fr.minemobs.jsonreader.parser.JsonPrimitive;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class StrictParser {

    private StrictParser() {}

    public static Optional<JsonPrimitive> parsePrimitive(String s) {
        if(parseBoolean(s).isPresent()) {
            return Optional.of(new JsonPrimitive(parseBoolean(s).get()));
        } else if(s == null) {
            return Optional.of(new JsonPrimitive(null));
        } else if(parseDouble(s).isPresent()) {
            return Optional.of(new JsonPrimitive(parseDouble(s).getAsDouble()));
        } else {
            return Optional.of(new JsonPrimitive(s));
        }
    }

    public static Optional<Boolean> parseBoolean(String s) {
        if(!"true".equalsIgnoreCase(s) && !"false".equalsIgnoreCase(s)) {
            return Optional.empty();
        }
        return Optional.of(Boolean.parseBoolean(s));
    }

    public static OptionalInt parseInteger(String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch(NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public static OptionalDouble parseDouble(String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch(NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }
}