package fr.minemobs.jsonreader.parser;

import fr.minemobs.jsonreader.MalformedJsonException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser {

    public static final Pattern pattern = Pattern.compile(
            "(?<key>\"\\w+\":)|" +
                    "(?<null>null)|" +
                    "(?<primitive>(?<bool>false|true)|" +
                    "(?<string>\"(?:[^\"\\\\]|\\.)*\")|" +
                    "(?<int>\\d+(?<decimal>\\.\\d+)?)),?|" +
                    "(?<obj>\\{)|" + "(?<endObj>})|" +
                    "(?<array>\\[)|(?<endArray>])");

    private static final AtomicInteger index = new AtomicInteger(0);
    private static int maxIndex = -1;

    public static JsonElement parse(List<String> stringsList) throws MalformedJsonException {
        List<String> lines = stringsList.stream().map(String::strip).filter(s -> !s.isEmpty()).toList();
        maxIndex = lines.size();
        String line = lines.get(index.get());
        Matcher matcher = pattern.matcher(line);
        increase();
        if(!matcher.find()) throw new MalformedJsonException("Invalid json, the first line must be an object or an array");
        if(group(matcher, "array")) {
            return parseArray(lines);
        } else if(group(matcher, "obj")) {
            return parseObject(lines);
        } else {
            throw new MalformedJsonException("Invalid json, the first line must be an object or an array");
        }
    }

    private static JsonObject parseObject(List<String> lines) throws MalformedJsonException {
        Map<String, JsonElement> object = new LinkedHashMap<>();
        String line = lines.get(index.get());
        Matcher matcher = pattern.matcher(line);
        while(matcher.find()) {
            if(group(matcher, "endObj")) return new JsonObject(object);
            if(group(matcher, "key")) {
                String key = matcher.group("key");
                key = key.substring(1, key.length() - 2);
                if(matcher.find()) {
                    if(group(matcher, "primitive")) {
                        object.put(key, parsePrimitive(matcher, matcher.group("primitive")));
                    } else if(group(matcher, "null")) object.put(key, new JsonNull());
                    else if(group(matcher, "obj")) object.put(key, parseObject(lines));
                    else if(group(matcher, "array")) object.put(key, parseArray(lines));
                    else throw new MalformedJsonException("Invalid json, the value of the key " + key + " is invalid");
                } else throw new MalformedJsonException("Invalid json, the object is not closed");
            }
            increase();
            line = lines.get(index.get() - 1);
            matcher = pattern.matcher(line);
        }
        return new JsonObject(object);
    }

    private static JsonPrimitive parsePrimitive(Matcher matcher, String line) throws MalformedJsonException {
        if(group(matcher, "string")) {
            String value = matcher.group("string");
            return new JsonPrimitive(value.substring(1, value.length() - 1));
        } else if(group(matcher, "int")) {
            return new JsonPrimitive(group(matcher, "decimal") ?
                    Double.parseDouble(matcher.group("int")) :
                    Integer.parseInt(matcher.group("int")));
        } else if(group(matcher, "bool")) {
            return new JsonPrimitive(Boolean.parseBoolean(matcher.group("bool")));
        } else {
            throw new MalformedJsonException("Invalid json, unknown primitive type for " + line);
        }
    }

    private static JsonArray parseArray(List<String> lines) throws MalformedJsonException {
        List<JsonElement> elements = new ArrayList<>() {
            @Override
            public boolean add(JsonElement jsonElement) {
                for (JsonElement element : this) {
                    //TODO: remove this hack and find out why the same element is added twice
                    if (element.getValue().equals(jsonElement.getValue())) return false;
                }
                return super.add(jsonElement);
            }
        };
        String line = lines.get(index.get());
        line = line.endsWith(",") ? line.substring(0, line.length() - 1) : line;
        Matcher matcher = pattern.matcher(line);
        while(matcher.find()) {
            if(group(matcher, "endArray")) return new JsonArray(elements);
            if(group(matcher, "primitive")) {
                System.out.println(matcher.group("primitive"));
                elements.add(parsePrimitive(matcher, line));
            } else if(group(matcher, "null")) elements.add(new JsonNull());
            else if(group(matcher, "obj")) elements.add(parseObject(lines));
            else if(group(matcher, "array")) elements.add(parseArray(lines));
            increase();
            line = lines.get(index.get() - 1);
            line = line.endsWith(",") ? line.substring(0, line.length() - 1) : line;
            matcher = pattern.matcher(line);
        }
        throw new MalformedJsonException("Invalid json, the array is not closed");
    }

    private static boolean group(Matcher matcher, String group) {
        return matcher.group(group) != null;
    }

    private static void increase() {
        if(maxIndex == -1 || index.get() < maxIndex) index.incrementAndGet();
    }
}