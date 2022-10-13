package fr.minemobs.jsonreader;

import fr.minemobs.jsonreader.parser.*;
import net.thesimpleteam.colors.Colors;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JsonReader {

    private enum TypeColor {
        //Based on https://github.com/atomiks/moonlight-vscode-theme/blob/master/src/colors.js
        KEY(Color.decode("#65bcff")),
        STRING(Color.decode("#c3e88d")),
        NUMBER(Color.decode("#ff966c")),
        BOOLEAN(Color.decode("#ff966c")),
        NULL(Color.decode("#c099ff")),
        DEFAULT(Color.WHITE);

        private final Color color;

        TypeColor(Color color) {
            this.color = color;
        }

        public String getColor() {
            return Colors.getForegroundColorFromRGB(color);
        }
    }

    public static void main(String[] args) throws MalformedJsonException {
        JsonReader jr = new JsonReader();
        if (args.length == 0) {
            System.out.println("java -jar jsonreader.jar <path | url>");
            System.exit(1);
        }
        String json = null;
        try {
            json = jr.read(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        List<String> lines = json.lines().toList();
        AtomicInteger lineNumber = new AtomicInteger(1);
        JsonElement parse = JsonParser.parse(lines);
        AtomicInteger indentation = new AtomicInteger();
        if (parse instanceof JsonObject obj) {
            printObject("", obj, indentation, lineNumber, lines.size());
        } else if (parse instanceof JsonArray array) {
            printArray("", array, indentation, lineNumber, lines.size());
        }
        System.out.println(Colors.RESET);
    }

    private static void printObject(String baseKey, JsonObject obj, AtomicInteger indentation, AtomicInteger lineNumber, int size) {
        System.out.println(Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + formatNumber(lineNumber.get(), size) + ": " + " ".repeat(indentation.get()) +
                (baseKey.isEmpty() ? "" : TypeColor.KEY.getColor() + "\"" + baseKey + "\"" + TypeColor.DEFAULT.getColor() + ": ") +
                TypeColor.DEFAULT.getColor() + "{");
        indentation.addAndGet(2);
        for (Map.Entry<String, JsonElement> entry : obj.getElements().entrySet()) {
            String line = Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + formatNumber(lineNumber.incrementAndGet(), size) + ": ";
            String key = entry.getKey();
            if (entry.getValue() instanceof JsonPrimitive primitive) {
                TypeColor primitiveColor = getPrimitiveColor(primitive);
                System.out.println(line + " ".repeat(indentation.get()) + TypeColor.KEY.getColor() + "\"" + key + "\"" +
                                   TypeColor.DEFAULT.getColor() + ": " + primitiveColor.getColor() + format(primitive) + TypeColor.DEFAULT.getColor() + ",");
            } else if (entry.getValue() instanceof JsonObject object) {
                printObject(key, object, indentation, lineNumber, size);
            } else if (entry.getValue() instanceof JsonArray array) {
                printArray(key, array, indentation, lineNumber, size);
            }
        }
        indentation.addAndGet(-2);
        System.out.println(Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + formatNumber(lineNumber.incrementAndGet(), size) + ": " + TypeColor.DEFAULT.getColor() + " ".repeat(indentation.get()) + "}");
    }

    private static String formatNumber(int i, int length) {
        return String.format("%0" + String.valueOf(length).length() + "d", i);
    }

    private static void printArray(String baseKey, JsonArray array, AtomicInteger indentation, AtomicInteger lineNumber, int size) {
        System.out.println(Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + formatNumber(lineNumber.get(), size) + ": " + " ".repeat(indentation.get()) +
                (baseKey.isEmpty() ? "" : TypeColor.KEY.getColor() + "\"" + baseKey + "\"" + TypeColor.DEFAULT.getColor() + ": ") +
                TypeColor.DEFAULT.getColor() + "[");
        indentation.addAndGet(2);
        array.forEach(element -> {
            String line = Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + formatNumber(lineNumber.incrementAndGet(), size) + ": ";
            if (element instanceof JsonPrimitive primitive) {
                TypeColor primitiveColor = getPrimitiveColor(primitive);
                System.out.println(line + " ".repeat(indentation.get()) + primitiveColor.getColor() + format(primitive) + TypeColor.DEFAULT.getColor() + ",");
            } else if (element instanceof JsonObject object) {
                indentation.addAndGet(2);
                printObject("", object, indentation, lineNumber, size);
                indentation.addAndGet(-2);
            }
        });
        indentation.addAndGet(-2);
        System.out.println(Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + formatNumber(lineNumber.incrementAndGet(), size) + ": " + TypeColor.DEFAULT.getColor() + " ".repeat(indentation.get()) + "]");
    }

    private static String format(JsonPrimitive primitive) {
        return primitive.getType() == JsonPrimitive.Type.STRING ? "\"" + primitive.getValue() + "\"" : String.valueOf(primitive.getValue());
    }

    private static TypeColor getPrimitiveColor(JsonPrimitive primitive) {
        return switch (primitive.getType()) {
            case STRING -> TypeColor.STRING;
            case NUMBER -> TypeColor.NUMBER;
            case BOOLEAN -> TypeColor.BOOLEAN;
            case DEFAULT -> TypeColor.DEFAULT;
        };
    }

    private String read(String arg) throws IOException {
        Path filePath = Path.of(arg);
        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } else {
            URL url = null;
            try {
                url = new URL(arg);
            } catch (MalformedURLException e) {
                System.out.println("The URL is not valid: " + e.getMessage());
                System.exit(11);
            }
            ByteArrayOutputStream writer = new ByteArrayOutputStream();
            try (InputStream is = url.openStream()) {
                is.transferTo(writer);
            }
            return writer.toString();
        }
    }
}