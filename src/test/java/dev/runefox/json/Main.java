package dev.runefox.json;

import java.io.File;

public class Main {
    private static final Json JSON = Json.json5Builder().build();

    public static void main(String[] args) throws Exception {
        JsonNode node = JSON.parse(new File("testfiles/test.json5"));

        for (JsonNode elem : node) {
            System.out.println(elem.get("friends").get(0).get("name"));
        }

        JSON.serialize(node, new File("testfiles/out.json5"));
    }
}
