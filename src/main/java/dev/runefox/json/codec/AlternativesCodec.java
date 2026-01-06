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

package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class AlternativesCodec<A> implements JsonCodec<A> {
    private final List<JsonCodec<A>> options;

    AlternativesCodec(List<JsonCodec<A>> options) {
        this.options = options;
    }

    @Override
    public JsonNode encode(A obj) {
        List<NodeException> exceptions = null;
        for (JsonCodec<A> codec : options) {
            try {
                return codec.encode(obj);
            } catch (NoCodecImplementation ignored) {
            } catch (NodeException exc) {
                if (exceptions == null)
                    exceptions = new ArrayList<>();
                exceptions.add(exc);
            }
        }
        if (exceptions == null)
            throw new CodecException("Could not encode");

        if (exceptions.size() == 1)
            throw exceptions.get(0);

        CodecException exc = new CodecException("Could not encode");
        exceptions.forEach(exc::addSuppressed);
        throw exc;
    }

    @Override
    public A decode(JsonNode json) {
        List<NodeException> exceptions = null;
        for (JsonCodec<A> codec : options) {
            try {
                return codec.decode(json);
            } catch (NoCodecImplementation ignored) {
            } catch (NodeException exc) {
                if (exceptions == null)
                    exceptions = new ArrayList<>();
                exceptions.add(exc);
            }
        }
        if (exceptions == null)
            throw new CodecException("Could not decode");

        if (exceptions.size() == 1)
            throw exceptions.get(0);

        CodecException exc = new CodecException("Could not decode");
        exceptions.forEach(exc::addSuppressed);
        throw exc;
    }

    @Override
    public JsonCodec<A> alternatively(JsonCodec<A> option) {
        return new AlternativesCodec<>(
            List.copyOf(Stream.concat(
                options.stream(),
                Stream.of(option)
            ).toList())
        );
    }
}
