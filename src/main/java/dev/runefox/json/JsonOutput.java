package dev.runefox.json;

import java.io.Closeable;

/**
 * The output sibling of {@link JsonInput}. {@link JsonOutput} provides a stream-like interface to send out a stream of
 * Json documents.
 * <h1>Thread safety</h1>
 * This interface can write only one document at a time. Therefore, if two threads try to write a document
 * simultaneously, one will have to wait. Implementations of this interface are responsible for making sure they are
 * safe to use in concurrent applications.
 *
 * @see JsonInput
 */
public interface JsonOutput extends Closeable {
    /**
     * Writes a single {@link JsonNode} to this output. This method blocks until another thread is done writing to the
     * stream.
     *
     * @param node The node to write.
     * @throws NullPointerException When the given node is null.
     */
    void write(JsonNode node);

    /**
     * Closes the stream, closing all associated resources. This call closes the stream or writer that was used to open
     * this stream.
     */
    @Override
    void close();
}
