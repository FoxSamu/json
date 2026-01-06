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

package dev.runefox.json.impl.parse.json;

import dev.runefox.json.impl.parse.TokenType;

public enum JsonTokenType implements TokenType {
    BOOLEAN("boolean", true, false),
    STRING("string", true, true),
    IDENTIFIER("identifier", false, true),
    NUMBER("number", true, false),
    NULL("null", true, false),
    OBJECT_START("'{'", true, false),
    OBJECT_END("'}'", false, false),
    ARRAY_START("'['", true, false),
    ARRAY_END("']'", false, false),
    COLON("':'", false, false),
    COMMA("','", false, false),
    EOF("EOF", false, false);

    private final String errorName;
    private final boolean isValue;
    private final boolean isKey;

    JsonTokenType(String name, boolean isValue, boolean isKey) {
        this.errorName = name;
        this.isValue = isValue;
        this.isKey = isKey;
    }

    public String getErrorName() {
        return errorName;
    }

    public boolean isValue() {
        return isValue;
    }

    public boolean isKey() {
        return isKey;
    }
}
