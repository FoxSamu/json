package dev.runefox.json;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Instances of the {@link Json} class define how JSON data is parsed and formatted. It defines whether it accepts JSON
 * 5 data, and various other options.
 *
 * {@link Json} instances can be obtained via a builder: {@link #jsonBuilder()}, {@link #json5Builder()}, but
 * ready-to-use presets can also be obtained via {@link #json()}, {@link #json5()}, {@link #compactJson()} and
 * {@link #compactJson5()} (the latter two will print compact JSON, while the other two print pretty JSON).
 */
public class Json {
    private static final FormattingConfig DEFAULT_FORMAT_CONFIG = FormattingConfig.pretty();
    private static final ParsingConfig DEFAULT_PARSE_CONFIG = ParsingConfig.standard();
    private static final FormattingConfig JSON5_FORMAT_CONFIG = FormattingConfig.pretty().json5(true);
    private static final ParsingConfig JSON5_PARSE_CONFIG = ParsingConfig.standard().json5(true);

    private static final Json JSON = new Json(false);
    private static final Json JSON5 = new Json(true);

    private static final Json COMPACT_JSON = jsonBuilder().formatConfig(FormattingConfig.compact()).build();
    private static final Json COMPACT_JSON5 = json5Builder().formatConfig(FormattingConfig.compact()).build();

    private final FormattingConfig formatConfig;
    private final ParsingConfig parseConfig;
    private final ThreadLocal<StringBuilder> toStringBuilder = ThreadLocal.withInitial(StringBuilder::new);

    private Json(Builder builder) {
        formatConfig = builder.formatConfig.copy();
        parseConfig = builder.parseConfig.copy();
    }

    private Json(boolean j5) {
        formatConfig = j5 ? JSON5_FORMAT_CONFIG : DEFAULT_FORMAT_CONFIG;
        parseConfig = j5 ? JSON5_PARSE_CONFIG : DEFAULT_PARSE_CONFIG;
    }

    private JsonReader createReader(Reader reader) {
        if (parseConfig.json5()) {
            return new LexerReader(new Json5Lexer(reader), parseConfig.allowNonExecutePrefix());
        } else {
            return new LexerReader(new JsonLexer(reader), parseConfig.allowNonExecutePrefix());
        }
    }

    /**
     * Parses a {@link Reader} as JSON. The reader will <strong>not</strong> be closed.
     *
     * @param reader The reader to parse
     * @return The parsed {@link JsonNode}
     *
     * @throws NullPointerException If the reader is null
     * @throws JsonSyntaxException  When the JSON has invalid syntax
     * @throws IOException          If an I/O error occurs
     */
    public JsonNode parse(Reader reader) throws IOException {
        if (reader == null)
            throw new NullPointerException();
        return Parser.parse(createReader(reader), parseConfig);
    }

    /**
     * Parses a {@link String} as JSON.
     *
     * @param string The string to parse
     * @return The parsed {@link JsonNode}
     *
     * @throws NullPointerException If the string is null
     * @throws JsonSyntaxException  When the JSON has invalid syntax
     */
    public JsonNode parse(String string) throws JsonSyntaxException {
        if (string == null)
            throw new NullPointerException();
        try {
            return parse(new StringReader(string));
        } catch (JsonSyntaxException e) {
            throw e;
        } catch (IOException e) {
            // This is not supposed to happen
            throw new AssertionError("String reader throws IOException?!");
        }
    }

    /**
     * Parses an {@link InputStream} as JSON, using the {@linkplain Charset#defaultCharset() default charset}. The
     * stream will <strong>not</strong> be closed.
     *
     * @param stream The input stream to parse
     * @return The parsed {@link JsonNode}
     *
     * @throws NullPointerException If the stream is null
     * @throws JsonSyntaxException  When the JSON has invalid syntax
     * @throws IOException          If an I/O error occurs
     */
    public JsonNode parse(InputStream stream) throws IOException {
        if (stream == null)
            throw new NullPointerException();
        return parse(new InputStreamReader(stream));
    }

    /**
     * Parses the contents of a {@link File} as JSON.
     *
     * @param file The file to parse
     * @return The parsed {@link JsonNode}
     *
     * @throws NullPointerException  If the file is null
     * @throws JsonSyntaxException   When the JSON has invalid syntax
     * @throws FileNotFoundException When the file cannot be found, is a directory, or could not be opened for some
     *                               other reason
     * @throws IOException           If an I/O error occurs
     */
    public JsonNode parse(File file) throws IOException {
        if (file == null)
            throw new NullPointerException();
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            return parse(reader);
        }
    }

    /**
     * Parses a {@link Reader} as a stream of JSON documents. The reader will be closed when the returned stream is
     * closed. Not closing the returned stream will simply result in the reader not being closed. It has no other
     * consequences.
     *
     * @param reader The reader to parse
     * @return A {@link JsonInput} to read
     *
     * @throws NullPointerException If the reader is null
     * @throws IOException          If an I/O error occurs
     */
    public JsonInput input(Reader reader) throws IOException {
        if (reader == null)
            throw new NullPointerException();
        return new JsonInputImpl(createReader(reader), parseConfig);
    }

    /**
     * Parses a {@link String} as a stream of JSON documents.
     *
     * @param string The string to parse
     * @return A {@link JsonInput} to read
     *
     * @throws NullPointerException If the string is null
     */
    public JsonInput input(String string) throws JsonSyntaxException {
        if (string == null)
            throw new NullPointerException();
        try {
            return input(new StringReader(string));
        } catch (JsonSyntaxException e) {
            throw e;
        } catch (IOException e) {
            // This is not supposed to happen
            throw new AssertionError("String reader throws IOException?!");
        }
    }

    /**
     * Parses an {@link InputStream} as a stream of JSON documents, using the
     * {@linkplain Charset#defaultCharset() default charset}. The reader will be closed when the returned stream is
     * closed. Not closing the returned stream will simply result in the reader not being closed. It has no other
     * consequences.
     *
     * @param stream The input stream to parse
     * @return A {@link JsonInput} to read
     *
     * @throws NullPointerException If the stream is null
     * @throws JsonSyntaxException  When the JSON has invalid syntax
     * @throws IOException          If an I/O error occurs
     */
    public JsonInput input(InputStream stream) throws IOException {
        if (stream == null)
            throw new NullPointerException();
        return input(new InputStreamReader(stream));
    }

    /**
     * Parses the contents of a {@link File} as a stream of JSON documents. The returned stream must be closed in order
     * to close the file stream.
     *
     * @param file The file to parse
     * @return A {@link JsonInput} to read
     *
     * @throws NullPointerException  If the file is null
     * @throws FileNotFoundException When the file cannot be found, is a directory, or could not be opened for some
     *                               other reason
     * @throws IOException           If an I/O error occurs
     */
    public JsonInput input(File file) throws IOException {
        if (file == null)
            throw new NullPointerException();
        Reader reader = new BufferedReader(new FileReader(file));
        return input(reader);
    }

    /**
     * Serializes JSON to a {@link Writer}. The writer will <strong>not</strong> be closed.
     *
     * @param writer The writer to serialize to
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     * @throws IOException            If an I/O error occurs
     */
    public void serialize(JsonNode node, Writer writer) throws IOException {
        if (node == null || writer == null)
            throw new NullPointerException();
        if (!formatConfig.anyValue())
            node.requireConstruct();

        Serializer.serialize(node, writer, formatConfig);
    }

    /**
     * Serializes JSON to an {@link OutputStream}, using the {@linkplain Charset#defaultCharset() default charset}. The
     * stream will <strong>not</strong> be closed.
     *
     * @param stream The output stream to serialize to
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     * @throws IOException            If an I/O error occurs
     */
    public void serialize(JsonNode node, OutputStream stream) throws IOException {
        if (node == null || stream == null)
            throw new NullPointerException();
        serialize(node, new OutputStreamWriter(stream));
    }

    /**
     * Serializes JSON to a {@link File}.
     *
     * @param file The file to serialize to
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     * @throws FileNotFoundException  If the file does not exist and cannot be created
     * @throws IOException            If the file is a directory, or when an I/O error occurs
     */
    public void serialize(JsonNode node, File file) throws IOException {
        if (node == null || file == null)
            throw new NullPointerException();
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            serialize(node, writer);
        }
    }

    /**
     * Serializes JSON to a {@link StringBuilder}.
     *
     * @param builder The builder to serialize to
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     */
    public void serialize(JsonNode node, StringBuilder builder) {
        if (node == null || builder == null)
            throw new NullPointerException();
        if (!formatConfig.anyValue())
            node.requireConstruct();

        try {
            Serializer.serialize(node, builder, formatConfig);
        } catch (IOException e) {
            throw new AssertionError("StringBuilder throws IOException?!");
        }
    }

    /**
     * Serializes JSON to a {@link String}.
     *
     * @return The serialized JSON string
     *
     * @throws NullPointerException   If the given JSON is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     */
    public String serialize(JsonNode node) {
        if (node == null)
            throw new NullPointerException();
        if (!formatConfig.anyValue())
            node.requireConstruct();

        StringBuilder builder = toStringBuilder.get();
        builder.setLength(0);
        serialize(node, builder);
        return builder.toString();
    }




    /**
     * Opens a stream to write multiple JSON documents to a {@link Writer}. The writer will be closed when the returned
     * stream is closed. Not closing the returned stream has no other effects than not closing the writer, simply
     * closing the writer is also enough.
     *
     * @param writer The writer to serialize to
     * @return A {@link JsonOutput} to stream documents to
     *
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     */
    public JsonOutput output(Writer writer) {
        if (writer == null)
            throw new NullPointerException();
        return new JsonOutputImpl(writer, writer, writer, formatConfig);
    }

    /**
     * Opens a stream to write multiple JSON documents to an {@link OutputStream}, using the
     * {@linkplain Charset#defaultCharset() default charset}. The stream will be closed when the returned stream is
     * closed. Not closing the returned stream has no other effects than not closing the writer, simply closing the
     * given stream is also enough.
     *
     * @param stream The output stream to serialize to
     * @return A {@link JsonOutput} to stream documents to
     *
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     */
    public JsonOutput output(OutputStream stream) {
        if (stream == null)
            throw new NullPointerException();
        return output(new OutputStreamWriter(stream));
    }

    /**
     * Opens a stream to write multiple JSON documents to a {@link File}. The returned stream must be closed to close
     * the file stream.
     *
     * @param file The file to serialize to
     * @return A {@link JsonOutput} to stream documents to
     *
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     * @throws FileNotFoundException  If the file does not exist and cannot be created
     * @throws IOException            If the file is a directory, or when an I/O error occurs
     */
    public JsonOutput output(File file) throws IOException {
        if (file == null)
            throw new NullPointerException();
        Writer writer = new BufferedWriter(new FileWriter(file));
        return output(writer);
    }

    /**
     * Opens a stream to write multiple JSON documents to a {@link StringBuilder}.
     *
     * @param builder The builder to serialize to
     * @throws NullPointerException   If any parameter is null
     * @throws IncorrectTypeException If the given node is not an array or object and the formatting config does not
     *                                allow any value
     */
    public JsonOutput output(StringBuilder builder) {
        if (builder == null)
            throw new NullPointerException();

        return new JsonOutputImpl(builder, () -> { }, () -> { }, formatConfig);
    }



    /**
     * Returns a builder with the standard JSON as defaults, with pretty formatting.
     *
     * @return A builder with the standard JSON as defaults.
     */
    public static Builder jsonBuilder() {
        return new Builder();
    }

    /**
     * Returns a builder with JSON 5 as defaults, with pretty formatting.
     *
     * @return A builder with JSON 5 as defaults.
     */
    public static Builder json5Builder() {
        return new Builder().formatConfig(JSON5_FORMAT_CONFIG).parseConfig(JSON5_PARSE_CONFIG);
    }

    /**
     * Returns a standard JSON preset with {@linkplain ParsingConfig#standard() standard parsing} and
     * {@linkplain FormattingConfig#pretty() pretty formatting}.
     *
     * @return A standard JSON preset
     */
    public static Json json() {
        return JSON;
    }

    /**
     * Returns a JSON 5 preset with {@linkplain ParsingConfig#standard() standard parsing} and
     * {@linkplain FormattingConfig#pretty() pretty formatting}.
     *
     * @return A JSON 5 preset
     */
    public static Json json5() {
        return JSON5;
    }

    /**
     * Returns a standard JSON preset with {@linkplain ParsingConfig#standard() standard parsing} and
     * {@linkplain FormattingConfig#compact() compact formatting}.
     *
     * @return A standard JSON preset
     */
    public static Json compactJson() {
        return COMPACT_JSON;
    }

    /**
     * Returns a JSON 5 preset with {@linkplain ParsingConfig#standard() standard parsing} and
     * {@linkplain FormattingConfig#compact() compact formatting}.
     *
     * @return A JSON 5 preset
     */
    public static Json compactJson5() {
        return COMPACT_JSON5;
    }

    public static class Builder {
        private FormattingConfig formatConfig = DEFAULT_FORMAT_CONFIG;
        private ParsingConfig parseConfig = DEFAULT_PARSE_CONFIG;

        private Builder() {
        }

        /**
         * Sets a custom {@link FormattingConfig}. The configuration is copied upon {@link #build()}. Modification of
         * the configuration while building will affect the future {@link Json} instance
         *
         * @param config The configuration
         * @return This instance for chaining
         *
         * @throws NullPointerException When the configuration is null
         */
        public Builder formatConfig(FormattingConfig config) {
            if (config == null)
                throw new NullPointerException();

            formatConfig = config;
            return this;
        }

        /**
         * Sets a custom {@link ParsingConfig}. The configuration is copied upon {@link #build()}. Modification of the
         * configuration while building will affect the future {@link Json} instance
         *
         * @param config The configuration
         * @return This instance for chaining
         *
         * @throws NullPointerException When the configuration is null
         */
        public Builder parseConfig(ParsingConfig config) {
            if (config == null)
                throw new NullPointerException();

            parseConfig = config;
            return this;
        }

        /**
         * Builds the {@link Json} instance
         *
         * @return The built {@link Json} instance
         */
        public Json build() {
            return new Json(this);
        }
    }
}
