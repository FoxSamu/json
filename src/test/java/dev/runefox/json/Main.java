package dev.runefox.json;

import java.io.File;

public class Main {
    private static final Json JSON = Json.json5Builder().build();

    public static void main(String[] args) throws Exception {
        JsonNode node = JSON.parse(new File("testfiles/test.json5"));

        JsonPath path = JsonPath.parse("friends[0].name");
        for (JsonNode elem : node) {
            System.out.println(elem.query(path));
        }

        JSON.serialize(node, new File("testfiles/out.json5"));
    }
}
