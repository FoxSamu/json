package net.shadew.json;

import org.junit.jupiter.api.Test;

public class JsonNodeOutputTest {
    @Test
    void test() {
        JsonNodeOutput output = new JsonNodeOutput();
        JsonOutput out = output;

        out.startObject()
           .key("hello").stringValue("world")
           .stringValue("foo", "bar")
           .nullValue("hi")
           .key("array")
           .startArray()
           .numberValue(3)
           .numberValue(6)
           .numberValue(2)
           .numberValue(1)
           .end()
           .end()
           .endStream();

        System.out.println(output.result());
    }
}
