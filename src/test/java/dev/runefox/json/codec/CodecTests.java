package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodecTests {
    @Test
    void testCodec() {
        EncodableObject initial = new EncodableObject(
            621, "Foo", true,
            new EncodableObject(69, "Foo", false)
        );

        JsonNode node = EncodableObject.CODEC.encode(initial);
        System.out.println(node);

        EncodableObject reconstruction = EncodableObject.CODEC.decode(node);
        Assertions.assertEquals(initial, reconstruction);
    }
}
