package dev.runefox.json;

import dev.runefox.json.codec.JsonCodec;

import java.io.Closeable;
import java.io.IOException;

/**
 * If reading multiple Json documents from a stream (for example, a socket), it is desirable that the parser does not
 * give an error when the end of the stream is not seen at the end of a document. Normally, the parser will do this and
 * therefore the usual parsing methods are not suitable for this. {@link JsonInput} intends to solve this problem, by
 * providing a stream-like interface to read Json documents from without being bound to document boundaries.
 * <h1>Thread safety</h1>
 * This interface can read only one document at a time. Therefore, if two threads try to read a document simultaneously,
 * one will have to wait. Implementations of this interface are responsible for making sure they are safe to use in
 * concurrent applications.
 *
 * @see JsonOutput
 */
public interface JsonInput extends Closeable {
    /**
     * Reads a single {@link JsonNode} from the stream. When the end of the stream is reached between any documents,
     * this will return null. However, this method will throw if the stream ends without properly finishing a document.
     * This method blocks until a full tree has been read, possibly waiting for another thread to finish reading first.
     *
     * @return The read node, or null at the end of stream.
     *
     * @throws JsonSyntaxException When the read document has syntax errors, or is incomplete.
     * @throws IOException         When the underlying stream throws an {@link IOException}.
     */
    JsonNode read() throws IOException;

    /**
     * Reads a single {@link JsonNode} from the stream and decodes it using the given {@link JsonCodec}. When the end of
     * the stream is reached between any documents, this will return null. However, this method will throw if the stream
     * ends without properly finishing a document. This method blocks until a full tree has been read, possibly waiting
     * for another thread to finish reading first.
     *
     * @param codec The coded to decode with
     * @return The read node, or null at the end of stream.
     *
     * @throws JsonSyntaxException When the read document has syntax errors, or is incomplete.
     * @throws IOException         When the underlying stream throws an {@link IOException}.
     */
    default <A> A read(JsonCodec<A> codec) throws IOException {
        JsonNode node = read();
        if (node == null) return null;
        return codec.decode(node);
    }

    /**
     * Closes the stream, closing all associated resources. This call closes the stream or reader that was used to open
     * this stream.
     */
    @Override
    void close() throws IOException;
}
