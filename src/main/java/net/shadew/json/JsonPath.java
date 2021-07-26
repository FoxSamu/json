package net.shadew.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A pre-parsed JSON path. A path can be parsed from a JavaScript-like syntax. See {@link #parse(String)} for exact
 * syntax rules.
 * <p>
 * A {@link JsonPath} instance is immutable and any modifying method will return a modified copy of the original.
 */
public final class JsonPath {
    /**
     * The root path, a.k.a. the path that references the root element in a JSON tree. The root path is the path that
     * serializes to an empty string, and is the path returned by {@link #parse(String)} when a blank string is
     * entered.
     * <p>
     * {@link #parse(String) JsonPath.parse}{@code ("")} returns this path.
     */
    public static final JsonPath ROOT = new JsonPath();

    private final Step[] path;

    // Since JsonPath is final, these can be lazily computed
    private String toString;
    private Integer hashCode;
    private JsonPath parent;

    private JsonPath() {
        this.path = new Step[0];
        toString = "";
        hashCode = 1;
    }

    private JsonPath(Step[] steps) {
        this.path = steps;
    }

    /**
     * Returns the path to the parent of the object or array referenced by this path. This essentially returns the same
     * path as this path, but without the last entry.
     * <ul>
     * <li>The parent of <code>a.b.c</code> is <code>a.b</code></li>
     * <li>The parent of <code>a[3].c</code> is <code>a[3]</code></li>
     * <li>The parent of <code>a.b[2]</code> is <code>a.b</code></li>
     * <li>The parent of the root path does not exist</li>
     * </ul>
     *
     * @return The parent path
     *
     * @throws NoSuchElementException If this path is the {@linkplain #ROOT root path}.
     */
    public JsonPath parent() {
        if (path.length == 1) {
            return ROOT;
        }
        if (path.length == 0) {
            throw new NoSuchElementException("Path is root");
        }
        if (parent != null) {
            return parent;
        }
        Step[] newPath = new Step[path.length - 1];
        System.arraycopy(path, 0, newPath, 0, path.length - 1);
        return parent = new JsonPath(newPath);
    }

    /**
     * Returns the path to element <code>index</code> of the array referenced by this path. This is similar to calling
     * {@link #resolve(String) resolve}{@code ("[" + index + "]")}.
     *
     * @param index The index, zero based. When negative, it indexes arrays from the end.
     * @return The child path.
     */
    public JsonPath index(int index) {
        Step[] newPath = new Step[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[path.length] = new Index(index);
        return new JsonPath(newPath);
    }

    /**
     * Returns the path to member <code>key</code> of the object referenced by this path. This is similar to calling
     * {@link #resolve(String) resolve}{@code ("['" + key + "']")}, although characters in the key must be properly
     * escaped.
     *
     * @param key The key to access. When null, the literal key "null" is accessed.
     * @return The child path.
     */
    public JsonPath member(String key) {
        if (key == null)
            key = "null";

        Step[] newPath = new Step[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[path.length] = new Key(key);
        return new JsonPath(newPath);
    }

    /**
     * Resolves the given path {@code p} against this path. For example, if this path is {@code a.b}, and {@code p} is
     * {@code p[1]}, then the returned path is {@code a.b.p[1]}.
     *
     * @param p The subpath to resolve.
     * @return The fully resolved path.
     *
     * @throws NullPointerException If the given path {@code p} is null.
     */
    public JsonPath resolve(JsonPath p) {
        if (p == null)
            throw new NullPointerException("Path to resolve must not be null");

        Step[] newPath = new Step[path.length + p.path.length];
        System.arraycopy(path, 0, newPath, 0, path.length);
        System.arraycopy(p.path, 0, newPath, path.length, p.path.length);
        return new JsonPath(newPath);
    }

    /**
     * Parses and resolves the given path {@code p} against this path. For example, if this path is {@code a.b}, and
     * {@code p} is {@code p[1]}, then the returned path is {@code a.b.p[1]}. This is the same as {@link
     * #resolve(JsonPath) resolve}{@code (}{@link #parse JsonPath.parse}{@code (p))}.
     *
     * @param p The subpath to resolve.
     * @return The fully resolved path.
     *
     * @throws NullPointerException If the given path {@code p} is null.
     */
    public JsonPath resolve(String p) {
        if (p == null)
            throw new NullPointerException("Path to resolve must not be null");

        return resolve(JsonPath.parse(p));
    }

    /**
     * Resolves this path against the given path {@code p}. For example, if this path is {@code a.b}, and {@code p} is
     * {@code p[1]}, then the returned path is {@code p[1].a.b}.
     *
     * @param p The parent path to resolve to.
     * @return The fully resolved path.
     *
     * @throws NullPointerException If the given path {@code p} is null.
     */
    public JsonPath resolveTo(JsonPath p) {
        if (p == null)
            throw new NullPointerException("Path to resolve must not be null");

        return p.resolve(this);
    }

    /**
     * Parses and resolves this path against the given path {@code p}. For example, if this path is {@code a.b}, and
     * {@code p} is {@code p[1]}, then the returned path is {@code p[1].a.b}. This is the same as {@link
     * #resolveTo(JsonPath) resolveTo}{@code (}{@link #parse JsonPath.parse}{@code (p))}.
     *
     * @param p The parent path to resolve to.
     * @return The fully resolved path.
     *
     * @throws NullPointerException If the given path {@code p} is null.
     */
    public JsonPath resolveTo(String p) {
        if (p == null)
            throw new NullPointerException("Path to resolve must not be null");

        return resolveTo(JsonPath.parse(p));
    }

    /**
     * Queries the given JSON tree using this path.
     *
     * @param tree The JSON tree to query.
     * @return The queried value.
     */
    public JsonNode query(JsonNode tree) {
        if (tree == null) {
            throw new NullPointerException();
        }

        for (Step step : path) {
            tree = step.get(tree);
        }
        return tree;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        JsonPath that = (JsonPath) o;
        return Arrays.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        if (hashCode != null)
            return hashCode;
        int hc = Arrays.hashCode(path);
        hashCode = hc;
        return hc;
    }

    /**
     * Returns a string representation of this path. The string representation of a path is a JavaScript-like path
     * string. This representation can be parsed using {@link #parse(String)} to obtain a path that equals this path.
     * <p>
     * This representation is unique for each different path, meaning that, although it's not as efficient, {@link
     * JsonPath}s can be compared like {@link JsonPath#equals} by comparing their string representations via {@link
     * String#equals}.
     */
    @Override
    public String toString() {
        if (toString != null)
            return toString;

        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Step step : path) {
            step.appendString(first, builder);
            first = false;
        }

        toString = builder.toString();
        return toString;
    }

    /**
     * Returns a path to element {@code index} of the root array, which is essentially the path {@code [index]}. This is
     * the same as {@link #ROOT JsonPath.ROOT}{@code .}{@link #index(int) index}{@code (index)}.
     *
     * @param index The index, when negative it indexes from the end.
     * @return The created path.
     */
    public static JsonPath rootIndex(int index) {
        return new JsonPath(new Step[] {new Index(index)});
    }

    /**
     * Returns a path to element {@code key} of the root object, which is essentially the path {@code [key]}. This is
     * the same as {@link #ROOT JsonPath.ROOT}{@code .}{@link #member(String) member}{@code (key)}.
     *
     * @param key The key. When null, the literal key "null" is used.
     * @return The created path.
     */
    public static JsonPath rootMember(String key) {
        if (key == null)
            key = "null";
        return new JsonPath(new Step[] {new Key(key)});
    }

    /**
     * Parses a serialized JSON path. This accepts a similar syntax as JavaScript. A path consists of zero or more
     * entries, which are either identified by a period with an identifier ({@code .entry}) or a string or number
     * between brackets ({@code [3]}, {@code ["entry"]}). The first entry may also be an identifier without period
     * ({@code entry}).
     * <p>
     * Formal syntax rules:
     * <ul>
     * <li><em>Path</em> := <strong>Nothing</strong> | <em>FirstPathEntry</em> <em>PathEntry*</em></li>
     * <li><em>FirstPathEntry</em> := <em>Identifier</em> | <em>PathEntry</em></li>
     * <li><em>PathEntry</em> := <code>'.'</code> <em>Identifier</em> | <em>IndexEntry</em> | <em>StringEntry</em></li>
     * <li><em>IndexEntry</em> := <code>'['</code> <em>Number</em> <code>']'</code></li>
     * <li><em>StringEntry</em> := <code>'['</code> <em>String</em> <code>']'</code></li>
     * <li><em>Identifier</em> := <em>IdentifierStart</em> <em>IdentifierChar</em>*</li>
     * <li><em>String</em> := <code>'"'</code> <em>CharacterOrSingleQuote</em>* <code>'"'</code></li>
     * <li><em>DoubleQuoteString</em> := <code>'"'</code> <em>DoubleQuoteChar</em>* <code>'"'</code></li>
     * <li><em>SingleQuoteString</em> := <code>"'"</code> <em>SingleQuoteChar</em>* <code>"'"</code></li>
     * <li><em>Number</em> := <em>Sign</em>? <em>Digit</em>+</li>
     * <li><em>Digit</em> := <code>[0-9]</code></li>
     * <li><em>Sign</em> := <code>'+'</code> | <code>'-'</code></li>
     * <li><em>IdentifierStart</em> := <strong>Unicode Identifier Start</strong> | <em>UnicodeEscape</em></li>
     * <li><em>IdentifierChar</em> := <strong>Unicode Identifier Part</strong> | <em>UnicodeEscape</em></li>
     * <li><em>DoubleQuoteChar</em> := <em>Character</em> | <code>"'"</code></li>
     * <li><em>SingleQuoteChar</em> := <em>Character</em> | <code>'"'</code></li>
     * <li><em>Character</em> := <strong>Any Character</strong> | <em>Escape</em></li>
     * <li><em>Escape</em> := <em>UnicodeEscape</em> | <code>'&#92;'</code> <strong>Any Character</strong></li>
     * <li><em>UnicodeEscape</em> := <code>'&#92;u'</code> <em>HexDigit</em> <em>HexDigit</em> <em>HexDigit</em> <em>HexDigit</em></li>
     * <li><em>HexDigit</em> := <code>[0-9a-fA-F]</code></li>
     * </ul>
     * Here, <strong>Unicode Identifier Start</strong> is any character matched by {@link Character#isUnicodeIdentifierStart(char)},
     * <strong>Unicode Identifier Part</strong> is any character matched by {@link Character#isUnicodeIdentifierPart(char)}, and
     * <strong>Any Character</strong> is, obviously, any character.
     * <p>
     * A special case is where the path is an empty or blank string. In that case this method will return {@link #ROOT}.
     *
     * @param path The path to parse.
     * @return The parsed path.
     *
     * @throws IllegalArgumentException If the given path string does not match the above syntax rules.
     * @throws NullPointerException     If the given path string is null.
     */
    public static JsonPath parse(String path) {
        if (path == null)
            throw new NullPointerException("Cannot parse null path.");

        // Quick and easy checks
        String trim = path.trim();
        if (trim.isEmpty())
            return ROOT;

        if (CharUtil.isIdentifierValid(trim))
            return rootMember(trim);

        // Advanced path, parse
        Reader reader = new Reader(trim);
        ArrayList<Step> steps = new ArrayList<>();

        // Read steps until end
        Step step = readStep(true, reader);
        while (step != null) {
            steps.add(step);
            step = readStep(false, reader);
        }

        // Never happens due to quick check before
        // If it still happens, parser implementation is incorrect or the quick check did not work properly
        if (steps.size() == 0) {
            assert false : "parser did not parse";
            return ROOT;
        }

        Step[] compiled = steps.toArray(Step[]::new);
        return new JsonPath(compiled);
    }

    // Moves on to next non-whitespace and returns next character
    private static int skipWhitespaces(Reader reader) {
        int c = reader.peek();

        while (CharUtil.isWhitespace5(c)) { // Skip whitespaces
            reader.skip();
            c = reader.peek();
        }
        return c;
    }

    // Reads a full path step (the 'first' argument indicate whether identifiers may start without period)
    private static Step readStep(boolean first, Reader reader) {
        int c = skipWhitespaces(reader);
        if (c == -1) // End of string, no more step here
            return null;

        if (c == '.' || first && CharUtil.isIdentifierStart(c)) {
            // Identifier: ".abc", or "abc" and first step
            return readId(reader);
        } else if (c == '[') {
            // String/Number index
            reader.skip();
            return readIndex(reader);
        } else {
            // Something unexpected: error
            if (first)
                throw reader.positionalError("Expected identifier, '.' or '['");
            else
                throw reader.positionalError("Expected '.' or '['");
        }
    }

    // Reads an identifier, e.g. .abc
    private static Step readId(Reader reader) {
        int c = reader.peek();

        if (c == '.') // Ignore period if present, this is already enforced in readStep
            reader.skip();

        c = skipWhitespaces(reader);

        StringBuilder builder = reader.builder();
        builder.setLength(0);

        // TODO It is essentially possible to allow any identifier character as first character, decide if that's wanted
        //   Advantage:    it allows some more strings to be identifiers
        //   Disadvantage: numeric identifiers (i.e. path.3.y is possible) can be confused with indices
        if (!CharUtil.isIdentifierStart(c) && c != '\\')
            throw reader.positionalError("Expected identifier");

        // First character
        reader.skip();
        if (c == '\\')
            builder.append(readUnicodeEscape(reader));
        else
            builder.appendCodePoint(c);

        c = reader.peek();

        // Extra characters
        while (CharUtil.isIdentifier(c) || c == '\\') {
            reader.skip();
            if (c == '\\')
                builder.append(readUnicodeEscape(reader));
            else
                builder.appendCodePoint(c);

            c = reader.peek();
        }

        return new Key(builder.toString());
    }

    // Handles square bracket queries, e.g. [3], ['abc']
    private static Step readIndex(Reader reader) {
        int c = skipWhitespaces(reader);

        // String case
        if (c == '"' || c == '\'') {
            reader.skip();
            String str = readString(reader, (char) c);

            // Require a close bracket
            c = skipWhitespaces(reader);
            if (c != ']')
                throw reader.positionalError("Expected ']'");

            reader.skip();
            return new Key(str);
        }

        // Number case
        if (CharUtil.isDigit(c) || c == '-' || c == '+') {
            // Sign
            boolean neg = false;
            if (c == '-' || c == '+') {
                neg = c == '-';
                reader.skip();
                c = skipWhitespaces(reader);
            }

            int n = c - '0';
            c = reader.skipAndPeek();

            // Parse number
            while (CharUtil.isDigit(c)) {
                int v = c - '0';
                n = n * 10 + v;

                if (n < 0) // Integer overflow
                    throw reader.positionalError("Number too large");

                c = reader.skipAndPeek();
            }

            // Require a close bracket
            c = skipWhitespaces(reader);
            if (c != ']')
                throw reader.positionalError("Expected ']'");

            reader.skip();
            return new Index(neg ? -n : n);
        }

        // Neither case: error
        throw reader.positionalError("Expected string or number");
    }

    // Handles strings
    private static String readString(Reader reader, char quote) {
        StringBuilder builder = reader.builder();
        builder.setLength(0);

        int c = reader.peek();
        while (c != quote) {
            if (c == -1) // Reached end before ending quote: error
                throw reader.positionalError("Unclosed string");

            reader.skip();
            if (c == '\\')
                builder.append(readEscapeStr(reader));
            else
                builder.appendCodePoint(c);

            c = reader.peek();
        }
        reader.skip(); // Skip last quote

        return builder.toString();
    }

    // Handles escapes in strings
    private static char readEscapeStr(Reader reader) {
        int c = reader.peek();
        if (c == 'u') // Unicode
            return readUnicodeEscape(reader);

        if (c != -1) { // This automatically covers quotes and backslash too
            reader.skip();
            return (char) c;
        }

        throw reader.positionalError("Illegal escape");
    }

    // Handles unicode escapes
    private static char readUnicodeEscape(Reader reader) {
        int c = reader.peek();
        if (c != 'u') // Extra check in case of identifier escape
            throw reader.positionalError("Illegal escape");

        reader.skip();

        int hc = 0;
        for (int i = 0; i < 4; i++) {
            c = reader.peek();
            if (!CharUtil.isHexDigit(c))
                throw reader.positionalError("Illegal escape");

            int v = CharUtil.getHexDigitValue(c);
            hc = hc << 4 | v;
            reader.skip();
        }

        return (char) hc;
    }

    // Basic reader class that walks over the string when parsing a path
    private static class Reader {
        private final String string;
        private final StringBuilder builder = new StringBuilder();
        private int pos;

        private Reader(String string) {
            this.string = string;
        }

        // See next character, or -1 at end
        int peek() {
            if (pos >= string.length()) return -1;
            return string.charAt(pos);
        }

        // Skip one character and return it, or -1 at end
        void skip() {
            if (pos < string.length())
                pos++;
        }

        // Skip one character and return next, or -1 at end
        int skipAndPeek() {
            if (pos >= string.length()) return -1;
            if (pos == string.length() - 1) {
                pos++;
                return -1;
            }
            return string.charAt(++pos);
        }

        // Generates an error at the current position, like this:
        // Invalid pattern: Expected ']' (at pos 15)
        // error.in['here'
        //                ^
        IllegalArgumentException positionalError(String error) {
            return new IllegalArgumentException( // TODO Different exception type?
                                                 String.format("Invalid pattern: %s (at pos: %d)%n", error, pos)
                                                     + string + System.lineSeparator()
                                                     + " ".repeat(pos) + "^"
            );
        }

        // A reusable builder
        StringBuilder builder() {
            return builder;
        }
    }

    // For unit tests, to easily compare
    boolean test(Object... p) {
        int len = p.length;
        if (len != path.length) return false;

        for (int i = 0; i < len; i++) {
            if (!path[i].test(p[i]))
                return false;
        }
        return true;
    }

    // A step is either an index step or a key/member step.
    private interface Step {
        // For querying
        JsonNode get(JsonNode parent);

        // For converting to string
        void appendString(boolean first, StringBuilder builder);

        // For unit tests
        boolean test(Object test);
    }

    // Array queries, e.g.: [391], [-3], [0], [+8]
    private static class Index implements Step {
        private final int index;

        private Index(int index) {
            this.index = index;
        }

        @Override
        public JsonNode get(JsonNode parent) {
            return parent.get(index);
        }

        @Override
        public void appendString(boolean first, StringBuilder builder) {
            builder.append("[").append(index).append("]");
        }

        @Override
        public boolean test(Object test) {
            return test instanceof Number && ((Number) test).intValue() == index;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (obj == null || getClass() != obj.getClass())
                return false;

            Index that = (Index) obj;
            return index == that.index;
        }

        @Override
        public int hashCode() {
            return index;
        }
    }

    // Object queries, e.g.: .foo, ["bar"], ['baz']
    private static class Key implements Step {
        private final String key;

        private Key(String key) {
            this.key = key;
        }

        @Override
        public JsonNode get(JsonNode parent) {
            return parent.get(key);
        }

        @Override
        public void appendString(boolean first, StringBuilder builder) {
            if (CharUtil.isIdentifierValid(key)) {
                if (!first)
                    builder.append(".");
                builder.append(key);
            } else {
                builder.append("[\"");
                for (int i = 0, l = key.length(); i < l; i++) {
                    char c = key.charAt(i);
                    if (c == '"')
                        builder.append("\\");

                    builder.append(c);
                }
                builder.append("\"]");
            }
        }

        @Override
        public boolean test(Object test) {
            return test instanceof String && test.equals(key);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (obj == null || getClass() != obj.getClass())
                return false;

            Key that = (Key) obj;
            return key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}
