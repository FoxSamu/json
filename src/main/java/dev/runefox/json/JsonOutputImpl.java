package dev.runefox.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;

class JsonOutputImpl implements JsonOutput {
    private final Serializer serializer = new Serializer();
    private final Appendable output;
    private final Closeable closeable;
    private final FormattingConfig config;

    JsonOutputImpl(Appendable output, Closeable closeable, FormattingConfig config) {
        this.output = output;
        this.closeable = closeable;
        this.config = config;
    }

    @Override
    public void write(JsonNode node) {
        if (node == null)
            throw new NullPointerException();
        if (!config.anyValue())
            node.requireConstruct();

        synchronized (serializer) {
            serializer.reset(output, config);
            try {
                serializer.writeJson(node);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public void close() {
        try {
            closeable.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
