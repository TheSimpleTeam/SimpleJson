package fr.minemobs.jsonreader;

import net.thesimpleteam.colors.Colors;

import java.awt.Color;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static void main(String[] args) {
        JsonReader jr = new JsonReader();
        if(args.length == 0) {
            //Todo: Add support for redirection like this: java -jar jsonreader.jar < cat file.json
            //It's just a BufferedReader that contains System.in
            //https://stackoverflow.com/questions/25795568/how-to-redirect-a-file-as-an-input-to-java-program-with-bash
            System.out.println("java -jar jsonreader.jar <path | url>");
            System.exit(1);
        }
        String json = jr.read(args[0]);
        List<String> lines = json.lines().toList();
        AtomicInteger lineNumber = new AtomicInteger(0);
        Pattern pattern = Pattern.compile("(?<key>.*\"*.\":)|(?<int>-?(?:\\d+\\.?\\d*|\\d*\\.?\\d+))|(?<bool>true|false)|(?<null>null)|(?<String>\".*\")|(?<default>[,\\[\\]{})])");
        lines.forEach(l -> print(l, pattern, lineNumber));
        System.out.println(Colors.RESET);
    }

    private static void print(String l, Pattern pattern, AtomicInteger lineNumber) {
        String line = l.trim();
        System.out.print(Colors.getForegroundColorFromRGB(Color.DARK_GRAY) + lineNumber.getAndIncrement() + ": " + Colors.RESET);
        System.out.print(" ".repeat(l.length() - l.stripLeading().length()));
        Matcher matcher = pattern.matcher(line);
        while(matcher.find()) {
            if(matcher.group("key") != null) {
                System.out.print(TypeColor.KEY.getColor() + matcher.group("key").substring(0, matcher.group("key").length() - 1) + TypeColor.DEFAULT.getColor() + ": ");
            } else if(matcher.group("int") != null) {
                System.out.print(TypeColor.NUMBER.getColor() + matcher.group("int") + TypeColor.DEFAULT.getColor());
            } else if(matcher.group("bool") != null) {
                System.out.print(TypeColor.BOOLEAN.getColor() + matcher.group("bool") + TypeColor.DEFAULT.getColor());
            } else if (matcher.group("null") != null) {
                System.out.print(TypeColor.NULL.getColor() + "null" + TypeColor.DEFAULT.getColor());
            } else if (matcher.group("String") != null) {
                System.out.print(TypeColor.STRING.getColor() + matcher.group("String") + TypeColor.DEFAULT.getColor());
            } else if(matcher.group("default") != null) {
                System.out.print(TypeColor.DEFAULT.getColor() + matcher.group("default"));
            }
        }
        System.out.println();
    }

    private String prettyPrint(String j) {
        List<Character> indentChar = List.of('{', '[', ',', '}', ']');
        String json = j.trim().replace("\n", "");
        Pattern p = Pattern.compile("\"(.*?)\"|\\S+");
        Matcher m = p.matcher(json);
        StringBuilder sb = new StringBuilder();
        char lastChar = 0;
        int indent = 0;
        while(m.find()) {
            sb.append(m.group());
        }
        json = sb.toString();
        sb = new StringBuilder();
        for (char c : json.toCharArray()) {
            if (c == '{' || c == '[') {
                sb.append(" ".repeat(indent)).append(c).append("\n");
                indent += 2;
            } else if (c == '}' || c == ']') {
                indent -= 2;
                sb.append("\n").append(" ".repeat(indent)).append(c);
            } else if (c == ',') {
                sb.append(c).append("\n");
            } else {
                sb.append(" ".repeat(indentChar.contains(lastChar) ? indent : 0)).append(c);
            }
            lastChar = c;
        }
        return sb.toString();
    }

    private String read(String arg) {
        Path filePath = Path.of(arg);
        if (Files.exists(filePath)) {
            try(BufferedReader reader = Files.newBufferedReader(filePath)) {
                return prettyPrint(reader.lines().collect(Collectors.joining("\n")));
            } catch (IOException e) {
                System.out.println("Error while reading the file: " + e.getMessage());
                System.exit(10);
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
            try(InputStream is = url.openStream()) {
                is.transferTo(writer);
            } catch (IOException e) {
                System.out.println("Error while reading the file from the URL: " + e.getMessage());
                System.exit(12);
            }
            return prettyPrint(writer.toString());
        }
        return null;
    }
}
