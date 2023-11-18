//package dev.runefox.json;
//
//import org.junit.jupiter.api.Test;
//
//import static dev.runefox.json.JsonAsserts.*;
//import static dev.runefox.json.JsonPath.*;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * These tests test the JsonPath.parse function, to ensure it parses correctly and throws errors for incorrect paths.
// */
//public class PathParseTests {
//    static final Class<? extends Throwable> PARSE_ERROR_EXCEPTION = IllegalArgumentException.class;
//
//    @Test
//    void testBasic() {
//        assertPath(parse("path.parse.test"), "path", "parse", "test");
//    }
//
//    @Test
//    void testPeriodFirst() {
//        assertPath(parse(".path.parse.test"), "path", "parse", "test");
//    }
//
//    @Test
//    void testArrayIndex() {
//        assertPath(parse("array[12]"), "array", 12);
//    }
//
//    @Test
//    void testArrayPositiveIndex() {
//        assertPath(parse("array[+8]"), "array", 8);
//    }
//
//    @Test
//    void testArrayNegativeIndex() {
//        assertPath(parse("array[-8]"), "array", -8);
//    }
//
//    @Test
//    void testKeySq() {
//        assertPath(parse("key['with space']"), "key", "with space");
//    }
//
//    @Test
//    void testKeyDq() {
//        assertPath(parse("key[\"with space\"]"), "key", "with space");
//    }
//
//    @Test
//    void testKeySqEscapeQuote() {
//        assertPath(parse("key['escape\\'']"), "key", "escape'");
//    }
//
//    @Test
//    void testKeyDqEscapeQuote() {
//        assertPath(parse("key[\"escape\\\"\"]"), "key", "escape\"");
//    }
//
//    @Test
//    void testKeyBackslash() {
//        assertPath(parse("key['backslash\\\\']"), "key", "backslash\\");
//    }
//
//    @Test
//    void testKeyUseless() {
//        assertPath(parse("key['useless\\_e\\s\\c\\a\\p\\i\\n\\g']"), "key", "useless_escaping");
//    }
//
//    @Test
//    void testKeyUnicode() {
//        assertPath(parse("key['unicode\\u002D']"), "key", "unicode\u002D");
//    }
//
//    @Test
//    void testIdUnicode() {
//        assertPath(parse("id.unicode\\u002D"), "id", "unicode\u002D");
//    }
//
//    @Test
//    void testSpaced() {
//        assertPath(parse("  path . with . spaces  "), "path", "with", "spaces");
//    }
//
//    @Test
//    void testSpacedIndex() {
//        assertPath(parse("  path . with . spaces  [ + 9 ]  "), "path", "with", "spaces", 9);
//    }
//
//    @Test
//    void testSpacedKey() {
//        assertPath(parse("  path . with . spaces  [ '  spaces  ' ]  "), "path", "with", "spaces", "  spaces  ");
//    }
//
//    @Test
//    void testMultiIndex() {
//        assertPath(parse("path[3][5]"), "path", 3, 5);
//    }
//
//    @Test
//    void testIndexThenId() {
//        assertPath(parse("path[3].another"), "path", 3, "another");
//    }
//
//    @Test
//    void testMissingBracket() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("path[3.another"));
//    }
//
//    @Test
//    void testExtraBracket() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("path[3]].another"));
//    }
//
//    @Test
//    void testNothingInBrackets() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("path[].another"));
//    }
//
//    @Test
//    void testUnclosedString() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("path['unclosed].another"));
//    }
//
//    @Test
//    void testMissingPeriod() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("path['closed']another"));
//    }
//
//    @Test
//    void testExtraPeriod() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("path['closed']..another"));
//    }
//
//    @Test
//    void testPeriodOnly() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("."));
//    }
//
//    @Test
//    void testEmpty() {
//        assertPath(parse(""));
//    }
//
//    @Test
//    void testEmptySpaced() {
//        assertPath(parse("    "));
//    }
//
//    @Test
//    void testEmptyEqualsRoot() {
//        assertEquals(JsonPath.ROOT, parse(""));
//    }
//
//    @Test
//    void testSingleIdentifier() {
//        assertPath(parse("single_id"), "single_id");
//    }
//
//    @Test
//    void testIncorrectSingleId() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("3incorrect"));
//    }
//
//    @Test
//    void testIncorrectId() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("correct.3incorrect"));
//    }
//
//    @Test
//    void testSignOnly() {
//        assertThrows(PARSE_ERROR_EXCEPTION, () -> parse("sign.only[+]"));
//    }
//
//    @Test
//    void testSpacedEqualsNonSpaced() {
//        assertEquals(parse(" spaces [ 'don\\'t' ] . matter "), parse("spaces['don\\'t'].matter"));
//    }
//
//    @Test
//    void testNull() {
//        assertThrows(NullPointerException.class, () -> parse(null));
//    }
//}
