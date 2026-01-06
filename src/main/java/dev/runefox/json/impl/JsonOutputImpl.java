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

package dev.runefox.json.impl;

import dev.runefox.json.JsonNode;
import dev.runefox.json.JsonOutput;
import dev.runefox.json.JsonSerializingConfig;
import dev.runefox.json.codec.JsonRepresentable;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public class JsonOutputImpl implements JsonOutput {
    private final Serializer serializer = new Serializer();
    private final Appendable output;
    private final Closeable closeable;
    private final Flushable flushable;
    private final JsonSerializingConfig config;

    public JsonOutputImpl(Appendable output, Closeable closeable, Flushable flushable, JsonSerializingConfig config) {
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
