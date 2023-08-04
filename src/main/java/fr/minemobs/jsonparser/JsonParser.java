package fr.minemobs.jsonparser;

import fr.minemobs.jsonparser.types.JsonArray;
import fr.minemobs.jsonparser.types.JsonElement;
import fr.minemobs.jsonparser.types.JsonObject;
import fr.minemobs.jsonparser.types.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonParser {

    record ObjectWithLength<T extends JsonElement>(T element, int length) {}

    private final Pattern commentPattern = Pattern.compile("//.*$");
    private enum Exceptions {
        UNKNOWN_TOKEN("Unknown token '%c' at index %d"),
        MISSING_QUOTE("Missing quote at index '%d'"),
        MISSING_ENDING_BRACKET("Missing ending bracket at index '%d'"),
        MISSING_COLON("Missing colon at index '%d'"),
        ;

        private final String msg;

        Exceptions(String msg) {
            this.msg = msg;
        }

        @SuppressWarnings("unused")
        public IllegalStateException getException() {
            return new IllegalStateException(this.msg);
        }

        public IllegalStateException getExceptionFormatted(Object... format) {
            return new IllegalStateException(this.msg.formatted(format));
        }
    }

    int jsonStartAt(String rawJson) {
        List<String> lines = new ArrayList<>(rawJson.lines().toList());
        ListIterator<String> iter = lines.listIterator();
        int i = 0;
        while(iter.hasNext()) {
            var next = iter.next();
            if(next.startsWith("//") || next.isBlank() || next.isEmpty()) {
                i += next.length() + 1;
                iter.remove();
            } else break;
        }
        return i;
    }

    public JsonElement readJson(String rawJson) {
        int startAt = jsonStartAt(rawJson);
        return switch (rawJson.charAt(startAt)) {
            case '{' -> readObject(rawJson, startAt).element();
            case '[' -> readArray(rawJson, startAt).element();
            default -> throw new IllegalStateException("Unexpected char: '%s' at index %d".formatted(rawJson.charAt(startAt) == '\n' ? "\\n" : rawJson.charAt(startAt), startAt));
        };
    }

    ObjectWithLength<JsonObject> readObject(String rawJson, int startAt) {
        char lastChar = '{';
        String lastKey = null;
        JsonObject object = new JsonObject();
        boolean foundClosingBracket = false;
        int length = 0;
        for(int i = startAt + 1; i < rawJson.length(); i++) {
            char c = rawJson.charAt(i);
            if(c == '}') {
                foundClosingBracket = true;
                length = ++i;
                break;
            }
            if(c == '\n' || c == ' ') continue;
            if(c == '/') {
                Matcher matcher = commentPattern.matcher(rawJson.substring(i - 1));
                if(matcher.find()) i += matcher.end();
                else throw Exceptions.UNKNOWN_TOKEN.getExceptionFormatted(c, i);
                continue;
            }

            if (lastChar == ',' || lastChar == '{') {
                if(c != '"') throw Exceptions.MISSING_QUOTE.getExceptionFormatted(i);
                lastKey = readString(rawJson, i);
                i += lastKey.length() + 2;
                lastChar = c;
                continue;
            }
            switch (c) {
                case '"' -> {
                    String string = readString(rawJson, i);
                    i += string.length() + 2;
                    object.putElement(lastKey, new JsonPrimitive(string));
                }
                case '{' -> {
                    ObjectWithLength<JsonObject> obj = readObject(rawJson, i);
                    i += obj.length;
                    object.putElement(lastKey, obj.element);
                }
                case '[' -> {
                    ObjectWithLength<JsonArray> arr = readArray(rawJson, i);
                    i += arr.length;
                    object.putElement(lastKey, arr.element);
                }
                default -> {
                    if (Character.isDigit(c)) {
                        Number number = readNumber(rawJson, i);
                        var asString = number.toString();
                        //TODO: Remove this hack
                        i += (asString.endsWith(".0") ? asString.substring(0, asString.length() - 2) : asString).length();
                        object.putElement(lastKey, new JsonPrimitive(number));
                    }
                    else {
                        throw Exceptions.UNKNOWN_TOKEN.getExceptionFormatted(c, i);
                    }
                }
            }
            if(nextCharIn(rawJson, i, '}') == -1 && rawJson.charAt(i) != ',' && lastChar == ':') throw Exceptions.MISSING_COLON.getExceptionFormatted(i);
            lastChar = rawJson.charAt(i);
            length = i;
            lastKey = null;
        }
        if(!foundClosingBracket) {
            throw Exceptions.MISSING_ENDING_BRACKET.getExceptionFormatted(rawJson.length());
        }
        return new ObjectWithLength<>(object, length - startAt);
    }

    private ObjectWithLength<JsonArray> readArray(String rawJson, int startAt) {
        char lastChar = '[';
        JsonArray array = new JsonArray();
        boolean foundClosingBracket = false;
        int length = 0;
        for(int i = startAt + 1; i < rawJson.length(); i++) {
            char c = rawJson.charAt(i);
            if(c == ']') {
                foundClosingBracket = true;
                length = ++i;
                break;
            }
            if(c == '\n' && lastChar != ',' && lastChar != '[') {
                throw Exceptions.MISSING_COLON.getExceptionFormatted(i);
            }
            if(c == '\n' || c == ' ') continue;
            if(c == '/') {
                String str = rawJson.substring(i - 1);
                Matcher matcher = commentPattern.matcher(str);
                if(matcher.find()) i += matcher.end();
                else throw Exceptions.UNKNOWN_TOKEN.getExceptionFormatted(c, i);
                continue;
            }
            switch (c) {
                case '"' -> {
                    String string = readString(rawJson, i);
                    i += string.length() + 2;
                    array.addElement(new JsonPrimitive(string));
                }
                case '{' -> {
                    ObjectWithLength<JsonObject> obj = readObject(rawJson, i);
                    i += obj.length;
                    array.addElement(obj.element);
                }
                case '[' -> {
                    ObjectWithLength<JsonArray> arr = readArray(rawJson, i);
                    i += arr.length;
                    array.addElement(arr.element);
                }
                default -> {
                    if (Character.isDigit(c)) {
                        Number value = readNumber(rawJson, i);
                        var asString = value.toString();
                        //TODO: Remove this hack
                        i += (asString.endsWith(".0") ? asString.substring(0, asString.length() - 2) : asString).length();
                        array.addElement(new JsonPrimitive(value));
                    }
                    else throw Exceptions.UNKNOWN_TOKEN.getExceptionFormatted(c, i);
                }
            }
            if(rawJson.charAt(i) == ']') {
                foundClosingBracket = true;
                length = i;
                break;
            }
            if(nextCharIn(rawJson, i, ']') == -1 && rawJson.charAt(i) != ',') {
                throw Exceptions.MISSING_COLON.getExceptionFormatted(i);
            }
            lastChar = rawJson.charAt(i);
            length = i;
        }
        if(!foundClosingBracket) {
            throw Exceptions.MISSING_ENDING_BRACKET.getExceptionFormatted(rawJson.length());
        }
        return new ObjectWithLength<>(array, length - startAt);
    }

    private int nextCharIn(String rawJson, int startAt, char shouldBe) {
        for(int i = startAt + 1; i < rawJson.length(); i++) {
            char c = rawJson.charAt(i);
            if(c == ' ' || c == '\n') continue;
            if(c == '/') {
                Matcher matcher = commentPattern.matcher(rawJson.substring(i - 1));
                if(matcher.find()) i += matcher.end();
                else throw Exceptions.UNKNOWN_TOKEN.getExceptionFormatted(c, i);
                continue;
            }
            if(c == shouldBe) return i;
            else return -1;
        }
        return -1;
    }

    Number readNumber(String rawJson, int startAt) {
        int length = 0;
        boolean isFloating = false;
        for(int i = startAt; i < rawJson.length(); i++) {
            char c = rawJson.charAt(i);
            if(Character.isDigit(c)) length++;
            else if(c == '.' && !isFloating) {
                isFloating = true;
                length++;
            }
            else break;
        }
        var subString = rawJson.substring(startAt, startAt + length);
        return isFloating ? Double.parseDouble(subString) : Long.parseLong(subString);
    }

    String readString(String rawJson, int startAt) {
        StringBuilder builder = new StringBuilder(50);
        boolean reachedEnd = false;
        for(int i = startAt + 1; i < rawJson.length(); i++) {
            char c = rawJson.charAt(i);
            if(c == '"') {
                reachedEnd = true;
                break;
            }
            if(c == '\\' && startAt != rawJson.length() - 1 && rawJson.charAt(i + 1) == '"') {
                builder.append('"');
                i++;
                continue;
            }
            builder.append(c);
        }
        if(!reachedEnd) throw Exceptions.MISSING_QUOTE.getExceptionFormatted(rawJson.length() - 1);
        return builder.toString();
    }
}