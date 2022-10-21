import fr.minemobs.jsonreader.MalformedJsonException;
import fr.minemobs.jsonreader.parser.*;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Example {

    public static void main(String[] args) throws MalformedJsonException {
        JsonElement parse = JsonParser.parse(List.of("{\"a\": 1,\"df\": 2,\"b\": \"\\\"Hello World\\\"\",\"color\": {\"red\": 255,\"green\": 0,\"blue\": 0}, \"array\": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]}"));
        Example example = new Example(parse.getAsJsonObject());
        System.out.println(example);
    }

    private final int a, df;
    private final String b;
    private final Color color;
    private final int[] array;

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
        this.array = object.get("array", JsonArray.class).stream().mapToInt(element -> element.getAsJsonPrimitive().getValueAsInt()).toArray();
    }

    @Override
    public String toString() {
        return "Example{" +
               "a=" + a +
               ", df=" + df +
               ", b='" + b + '\'' +
               ", color={" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "}" +
               ", array=" + Arrays.toString(array) +
               '}';
    }
}