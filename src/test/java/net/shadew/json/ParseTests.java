package net.shadew.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParseTests {
    private Json json;

    @BeforeEach
    void beforeEach() {
        Debug.debug = true;
        Debug.tokenConsumer = token -> System.out.println(token.getType() + ": " + token.getValue());

        json = Json.jsonBuilder().parseConfig(
            ParsingConfig.standard()
                         .anyValue(true)
        ).formatConfig(FormattingConfig.prettyCompact()).build();
    }

    @Test
    void testParseFloat() throws Exception {
        JsonNode node = json.parse("0.421");
        Assertions.assertEquals(0.421f, node.asFloat());
    }

    @Test
    void testParseDouble() throws Exception {
        JsonNode node = json.parse("0.421");
        Assertions.assertEquals(node.asDouble(), 0.421);
    }
}
