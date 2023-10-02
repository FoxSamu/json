package dev.runefox.json;

class JsonInputImpl implements JsonInput {
    private final JsonReader reader;
    private final Parser parser;
    private final ParsingConfig config;

    JsonInputImpl(JsonReader reader, ParsingConfig config) {
        this.reader = reader;
        this.config = config;
        this.parser = new Parser();
        parser.streamed();
    }

    @Override
    public JsonNode read() throws JsonSyntaxException {
        synchronized (parser) {
            parser.parse0(reader, config);
            if (!parser.hasValue())
                return null;

            return parser.popValue(JsonNode.class);
        }
    }

    @Override
    public void close() {
        reader.close();
    }
}
