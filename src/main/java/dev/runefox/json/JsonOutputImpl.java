package dev.runefox.json;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.UncheckedIOException;

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

    @Override
    public void flush() {
        try {
            flushable.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
