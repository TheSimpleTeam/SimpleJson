package fr.minemobs.jsonreader.parser;

import fr.minemobs.jsonreader.MalformedJsonException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonParser {

    public static final Pattern pattern = Pattern.compile("(?<key>\"\\w+\":)|(?<null>null)|(?<primitive>(?<bool>false|true)|(?<string>\"(?:[^\"\\\\]|\\\\.)*\")|(?<int>\\d+(\\.\\d+)?)),?|(?<obj>\\{(?<objContent>.+|\\s+)*})|(?<array>\\[(?<arrayContent>.+|\\s+)*])");

    public static JsonElement parse(List<String> stringsList) throws MalformedJsonException {
        String line = stringsList.stream().map(String::strip).filter(s -> !s.isEmpty()).collect(Collectors.joining("\n"));
        Matcher matcher = pattern.matcher(line);
        if(!matcher.find()) throw new MalformedJsonException("Invalid json, the first line must be an object or an array");
        if(group(matcher, "array")) {
            if(group(matcher, "arrayContent")) {
                return parseArray(matcher.group("arrayContent"));
            } else {
                return new JsonArray(new ArrayList<>());
            }
        } else if(group(matcher, "obj")) {
            if(group(matcher, "objContent")) {
                return parseObject(matcher.group("objContent"));
            } else {
                return new JsonObject(new HashMap<>());
            }
        } else {
            throw new MalformedJsonException("Invalid json, the first line must be an object or an array");
        }
    }

    private static JsonObject parseObject(String line) throws MalformedJsonException {
        Matcher matcher = pattern.matcher(line);
        JsonObject object = new JsonObject();
        while(matcher.find()) {
            if(group(matcher, "key")) {
                String key = matcher.group("key").replace("\"", "").replace(":", "");
                if(!matcher.find()) continue;
                if(group(matcher, "primitive")) {
                    object.put(key, parsePrimitive(matcher, line));
                } else if(group(matcher, "obj")) {
                    object.put(key, parseObject(matcher.group("objContent")));
                } else if(group(matcher, "array")) {
                    object.put(key, parseArray(matcher.group("arrayContent")));
                } else if(group(matcher, "null")) {
                    object.put(key, new JsonPrimitive(null));
                }
            }
        }
        return object;
    }

    private static JsonArray parseArray(String line) throws MalformedJsonException {
        List<JsonElement> elements = new ArrayList<>();
        Matcher matcher = pattern.matcher(line);
        while(matcher.find()) {
            if(group(matcher, "primitive")) {
                elements.add(parsePrimitive(matcher, line));
            } else if(group(matcher, "obj")) {
                elements.add(parseObject(matcher.group("objContent")));
            } else if(group(matcher, "array")) {
                elements.add(parseArray(matcher.group("arrayContent")));
            } else if(group(matcher, "null")) {
                elements.add(new JsonPrimitive(null));
            }
        }
        return new JsonArray(elements);
    }

    private static JsonPrimitive parsePrimitive(Matcher matcher, String line) throws MalformedJsonException {
        if(group(matcher, "string")) {
            String value = matcher.group("string");
            return new JsonPrimitive(value.substring(1, value.length() - 1));
        } else if(group(matcher, "int")) {
            try {
                return new JsonPrimitive(Integer.parseInt(matcher.group("int")));
            } catch (NumberFormatException ignored) {
                return new JsonPrimitive(Double.parseDouble(matcher.group("int")));
            }
        } else if(group(matcher, "bool")) {
            return new JsonPrimitive(Boolean.parseBoolean(matcher.group("bool")));
        } else throw new MalformedJsonException("Invalid json, unknown primitive type for " + line);
    }

    private static boolean group(Matcher matcher, String group) {
        return Objects.nonNull(matcher.group(group));
    }
}