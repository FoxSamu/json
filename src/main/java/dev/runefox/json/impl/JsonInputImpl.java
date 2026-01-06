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
