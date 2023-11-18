//package dev.runefox.json;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.NoSuchElementException;
//
//import static dev.runefox.json.JsonAsserts.*;
//import static dev.runefox.json.JsonPath.*;
//import static org.junit.jupiter.api.Assertions.*;
//
///*
// * These tests test the basic operations on JsonPath instances. It involves some parsing, but the parsing part may not
// * rely on these tests. The goal of these tests is to ensure the modification operations operate correctly.
// */
//public class PathModifierTests {
//    @Test
//    void testRoot() {
//        assertPath(ROOT);
//    }
//
//    @Test
//    void testRootIndex() {
//        assertPath(rootIndex(3), 3);
//    }
//
//    @Test
//    void testRootMember() {
//        assertPath(rootMember("abc"), "abc");
//    }
//
//    @Test
//    void testRootDotIndex() {
//        assertPath(ROOT.index(3), 3);
//    }
//
//    @Test
//    void testRootDotMember() {
//        assertPath(ROOT.member("abc"), "abc");
//    }
//
//    @Test
//    void testChain() {
//        assertPath(rootIndex(3).member("abc").index(5), 3, "abc", 5);
//    }
//
//    @Test
//    void testResolve() {
//        assertPath(rootIndex(5).resolve(rootMember("abc").index(4)), 5, "abc", 4);
//    }
//
//    @Test
//    void testResolveTo() {
//        assertPath(rootIndex(5).resolveTo(rootMember("abc").index(4)), "abc", 4, 5);
//    }
//
//    @Test
//    void testResolveParse() {
//        assertPath(rootIndex(5).resolve("abc[4]"), 5, "abc", 4);
//    }
//
//    @Test
//    void testResolveToParse() {
//        assertPath(rootIndex(5).resolveTo("abc[4]"), "abc", 4, 5);
//    }
//
//    @Test
//    void testNullMember() {
//        assertPath(ROOT.member(null), "null");
//    }
//
//    @Test
//    void testNullRootMember() {
//        assertPath(rootMember(null), "null");
//    }
//
//    @Test
//    void testParentMember() {
//        assertPath(rootMember("abc").index(4).parent(), "abc");
//    }
//
//    @Test
//    void testParentIndex() {
//        assertPath(rootIndex(4).member("abc").parent(), 4);
//    }
//
//    @Test
//    void testParentRoot() {
//        assertThrows(NoSuchElementException.class, ROOT::parent);
//    }
//
//    @Test
//    void testPathEquals() {
//        assertTrue(parse("abc[4].def").equals(parse("abc[4].def")));
//    }
//
//    @Test
//    void testPathToString() {
//        assertEquals("abc[4].def", parse("abc[4].def").toString());
//    }
//
//    @Test
//    void testPathToStringWeirdChar() {
//        // Strings are always double quoted
//        assertEquals("abc[4][\"abc'\"]", parse("abc[4]['abc\\'']").toString());
//    }
//
//    @Test
//    void testRootToString() {
//        assertEquals("", ROOT.toString());
//    }
//}
