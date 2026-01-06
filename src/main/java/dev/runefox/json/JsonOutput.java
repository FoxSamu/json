/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package dev.runefox.json;

import dev.runefox.json.codec.JsonCodec;
import dev.runefox.json.codec.JsonRepresentable;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

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
public interface JsonOutput extends Closeable, Flushable {
    /**
     * Writes a single {@link JsonRepresentable} to this output. This method blocks until another thread is done writing
     * to the stream. Note that {@link JsonNode}s are {@link JsonRepresentable}s, you can use this method to write them
     * to the stream.
     *
     * @param json The JSON data to write.
     * @throws NullPointerException When the given node is null.
     * @throws SerializationException If the serializer does not accept the given node as valid JSON data.
     */
    void write(JsonRepresentable json) throws IOException;

    /**
     * Writes a single JSON document to this output, encoded using the given codec. This method blocks until another
     * thread is done writing to the stream.
     *
     * @param codec The codec to encode with
     * @param value The value to encode and write
     * @throws NullPointerException When the given node is null.
     * @throws SerializationException If the serializer does not accept the encoded data as valid JSON data.
     */
    default <A> void write(JsonCodec<? super A> codec, A value) throws IOException {
        write(codec.encode(value));
    }

    /**
     * Closes the stream, closing all associated resources. This call closes the stream or writer that was used to open
     * this stream.
     */
    @Override
    void close() throws IOException;

    /**
     * Flushes the stream. This must be called in server-client applications to flush any buffers used by the underlying
     * streams.
     */
    @Override
    void flush() throws IOException;
}
