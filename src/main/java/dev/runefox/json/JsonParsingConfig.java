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

public class JsonParsingConfig {
    private JsonParsingConfig() {
    }

    private boolean json5;

    /**
     * Whether to allow JSON5 syntax. That includes object keys without quotes, comments, trailing commas, single quoted
     * strings, NaN and infinite number values, hexadecimal number values and a few other things.
     */
    public boolean json5() {
        return json5;
    }

    /**
     * Set whether to allow JSON5 syntax. That includes object keys without quotes, comments, trailing commas, single
     * quoted strings, NaN and infinite number values, hexadecimal number values and a few other things.
     */
    public JsonParsingConfig json5(boolean json5) {
        this.json5 = json5;
        return this;
    }

    private boolean anyValue;

    /**
     * Whether to accept any JSON value as a valid document. Normally only objects and arrays are valid documents.
     */
    public boolean anyValue() {
        return anyValue;
    }

    /**
     * Set whether to accept any JSON value as a valid document. Normally only objects and arrays are valid documents.
     */
    public JsonParsingConfig anyValue(boolean anyValue) {
        this.anyValue = anyValue;
        return this;
    }

    private boolean allowNonExecutePrefix;

    /**
     * Whether to accept a non-execution prefix. These prefixes are often added to avoid browsers from executing a
     * JSON document as a JavaScript script.
     */
    public boolean allowNonExecutePrefix() {
        return allowNonExecutePrefix;
    }

    /**
     * Set whether to accept a non-execution prefix. These prefixes are often added to avoid browsers from executing a
     * JSON document as a JavaScript script.
     */
    public JsonParsingConfig allowNonExecutePrefix(boolean allowNonExecutePrefix) {
        this.allowNonExecutePrefix = allowNonExecutePrefix;
        return this;
    }

    /**
     * Copy this parsing configuration instance.
     */
    public JsonParsingConfig copy() {
        return new JsonParsingConfig().copyFrom(this);
    }

    /**
     * Copy the given configuration to this configuration instance.
     */
    public JsonParsingConfig copyFrom(JsonParsingConfig copy) {
        this.json5 = copy.json5;
        this.anyValue = copy.anyValue;
        this.allowNonExecutePrefix = copy.allowNonExecutePrefix;
        return this;
    }

    /**
     * The standard JSON syntax configuration.
     */
    public static JsonParsingConfig standard() {
        return new JsonParsingConfig();
    }
}
