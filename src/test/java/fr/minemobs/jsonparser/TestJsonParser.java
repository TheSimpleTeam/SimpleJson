package fr.minemobs.jsonparser;

import fr.minemobs.jsonparser.types.JsonElement;
import fr.minemobs.jsonparser.types.JsonObject;
import fr.minemobs.jsonparser.types.JsonPrimitive;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TestJsonParser {

    private static JsonParser parser;
    private static String json;

    @BeforeAll
    public static void createJsonParser() {
        parser = new JsonParser();
        try(var is = TestJsonParser.class.getClassLoader().getResourceAsStream("test.json")) {
            json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReadString() {
        assertEquals("Hello\" Funny text i guess ??", parser.readString("\"Hello\\\" Funny text i guess ??\"Non text", 0));
    }

    @Test
    void testReadNumber() {
        assertEquals(0.5720d, parser.readNumber("0.5720okfkoefko", 0));
        assertEquals(0, parser.readNumber("0okfkoefko", 0).intValue());
        assertEquals(0.21, parser.readNumber("0.21.14", 0));
    }

    @Test
    void testJsonObjectParser() {
        JsonObject object = parser.readObject(json, parser.jsonStartAt(json)).element();
        assertEquals("world", object.getElement("hello").get()
                .asJsonPrimitive().getString().get());
        object = parser.readJson(json).asJsonObject();
        assertEquals(213, object.getElement("numberOfRatiosGiven").get()
                .asJsonPrimitive().getNumber().get().intValue());
    }

    @Test
    void testNestedObjects() {
        JsonObject object = parser.readObject(json, parser.jsonStartAt(json)).element();
        assertEquals("guys", object.getElement("nested").get()
                .asJsonObject().getElement("hi").get()
                .asJsonPrimitive().getString().get());
    }

    @Test
    void testArray() {
        JsonObject object = parser.readObject(json, parser.jsonStartAt(json)).element();
        assertArrayEquals(
                new String[] {"minemobs", "other guy I guess", "yes"},
                object.getElement("names").get()
                        .asJsonArray().getUnmodifiableElements().stream()
                        .map(el -> el.asJsonPrimitive().getString().get())
                        .toArray(String[]::new)
        );
    }

    @Test
    void testArrayWithJsonString() {
        assertArrayEquals(
                new String[]{"java", "kotlin", "c", "rust"},
                parser.readObject("{\"hello\": [\"java\", \"kotlin\", \"c\", \"rust\"]}", 0)
                        .element().getElement("hello").get()
                        .asJsonArray().getUnmodifiableElements()
                        .stream().map(JsonElement::asJsonPrimitive).map(JsonPrimitive::getString).map(Optional::get)
                        .toArray(String[]::new));
    }
}
