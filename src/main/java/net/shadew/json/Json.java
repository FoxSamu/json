package net.shadew.json;

import java.io.*;

public class Json {
    private static final FormattingConfig DEFAULT_FORMAT_CONFIG = FormattingConfig.pretty();
    private static final ParsingConfig DEFAULT_PARSE_CONFIG = ParsingConfig.standard();
    private static final FormattingConfig JSON5_FORMAT_CONFIG = FormattingConfig.pretty().json5(true);
    private static final ParsingConfig JSON5_PARSE_CONFIG = ParsingConfig.standard().json5(true);

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

    public JsonNode parse(Reader reader) throws JsonSyntaxException {
        try (reader) {
            return Parser.parse(createReader(reader), parseConfig.json5());
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    public JsonNode parse(String string) throws JsonSyntaxException {
        return parse(new StringReader(string));
    }

    public JsonNode parse(InputStream stream) throws JsonSyntaxException {
        return parse(new InputStreamReader(stream));
    }

    public JsonNode parse(File file) throws JsonSyntaxException, FileNotFoundException {
        return parse(new FileReader(file));
    }

    public void serialize(JsonNode node, Writer writer) {
        try (writer) {
            Serializer.serialize(node, writer, formatConfig);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    public void serialize(JsonNode node, OutputStream stream) {
        serialize(node, new OutputStreamWriter(stream));
    }

    public void serialize(JsonNode node, File file) throws IOException {
        serialize(node, new FileWriter(file));
    }

    public void serialize(JsonNode node, StringBuilder builder) {
        Serializer.serialize(node, builder, formatConfig);
    }

    public String serialize(JsonNode node) {
        StringBuilder builder = toStringBuilder.get();
        builder.setLength(0);
        serialize(node, builder);
        return builder.toString();
    }

    public static Builder jsonBuilder() {
        return new Builder();
    }

    public static Builder json5Builder() {
        return new Builder().formatConfig(JSON5_FORMAT_CONFIG).parseConfig(JSON5_PARSE_CONFIG);
    }

    public static Json json() {
        return new Json(false);
    }

    public static Json json5() {
        return new Json(true);
    }

    public static class Builder {
        private FormattingConfig formatConfig = DEFAULT_FORMAT_CONFIG;
        private ParsingConfig parseConfig = DEFAULT_PARSE_CONFIG;

        private Builder() {
        }

        public Builder formatConfig(FormattingConfig config) {
            formatConfig = config;
            return this;
        }

        public Builder parseConfig(ParsingConfig config) {
            parseConfig = config;
            return this;
        }

        public Json build() {
            return new Json(this);
        }
    }
}
