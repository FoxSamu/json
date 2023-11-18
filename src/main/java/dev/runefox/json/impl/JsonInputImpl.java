package dev.runefox.json.impl;

import dev.runefox.json.JsonInput;
import dev.runefox.json.JsonNode;
import dev.runefox.json.JsonParsingConfig;
import dev.runefox.json.impl.parse.json.JsonParser;
import dev.runefox.json.impl.parse.json.JsonReader;

import java.io.IOException;

public class JsonInputImpl implements JsonInput {
    private final JsonReader reader;
    private final JsonParser parser;
    private final JsonParsingConfig config;

    public JsonInputImpl(JsonReader reader, JsonParsingConfig config) {
        this.reader = reader;
        this.config = config;
        this.parser = new JsonParser();
        parser.streamed();
    }

    @Override
    public JsonNode read() throws IOException {
        synchronized (parser) {
            parser.parse0(reader, config);
            if (!parser.hasValue())
                return null;

            return parser.popValue(JsonNode.class);
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
