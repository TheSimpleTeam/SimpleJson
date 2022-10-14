import fr.minemobs.jsonreader.MalformedJsonException;
import fr.minemobs.jsonreader.parser.JsonElement;
import fr.minemobs.jsonreader.parser.JsonObject;
import fr.minemobs.jsonreader.parser.JsonParser;
import fr.minemobs.jsonreader.parser.JsonPrimitive;

import java.awt.Color;
import java.util.Arrays;

public class Example {

    public static void main(String[] args) throws MalformedJsonException {
        JsonElement parse = JsonParser.parse(Arrays.stream("""
                {
                    "a": 1,
                    "df": 2,
                    "b": "\\"Hello World\\"",
                    "color": {
                        "red": 255,
                        "green": 0,
                        "blue": 0
                    }
                }
                """.split("\n")).toList());
        Example example = new Example(parse.getAsJsonObject());
        System.out.println(example);
    }

    private final int a, df;
    private final String b;
    private final Color color;

    public Example(JsonObject object) {
        this.a = object.get("a", JsonPrimitive.class).getValueAsInt();
        this.b = object.get("b").getValue(String.class);
        this.df = object.get("df", JsonPrimitive.class).getValueAsInt();
        JsonObject cr = object.get("color", JsonObject.class);
        this.color = new Color(
                cr.get("red", JsonPrimitive.class).getValueAsInt(),
                cr.get("green", JsonPrimitive.class).getValueAsInt(),
                cr.get("blue", JsonPrimitive.class).getValueAsInt()
        );
    }

    @Override
    public String toString() {
        return "TestTruc{" +
                "a=" + a +
                ", df=" + df +
                ", b='" + b + '\'' +
                ", color={" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "}" +
                '}';
    }
}