package dev.runefox.json;

import dev.runefox.json.codec.JsonRepresentable;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

class JsonOutputImpl implements JsonOutput {
    private final Serializer serializer = new Serializer();
    private final Appendable output;
    private final Closeable closeable;
    private final Flushable flushable;
    private final FormattingConfig config;

    JsonOutputImpl(Appendable output, Closeable closeable, Flushable flushable, FormattingConfig config) {
        this.output = output;
        this.closeable = closeable;
        this.flushable = flushable;
        this.config = config;
    }

    @Override
    public void write(JsonRepresentable json) throws IOException {
        JsonNode node = json.toJson();

        if (node == null)
            throw new NullPointerException();
        if (!config.anyValue())
            node.requireConstruct();

        synchronized (serializer) {
            serializer.reset(output, config);
            serializer.writeJson(node);
        }
    }

    @Override
    public void close() throws IOException {
        closeable.close();
    }

    @Override
    public void flush() throws IOException {
        flushable.flush();
    }
}
