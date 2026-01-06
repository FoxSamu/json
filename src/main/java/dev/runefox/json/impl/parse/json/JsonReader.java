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

import dev.runefox.json.SyntaxException;

import java.io.IOException;

public interface JsonReader {
    boolean readBoolean() throws IOException;
    String readString() throws IOException;
    String readIdentifier() throws IOException;
    Number readNumber() throws IOException;
    void readNull() throws IOException;
    void readObjectStart() throws IOException;
    void readObjectEnd() throws IOException;
    void readArrayStart() throws IOException;
    void readArrayEnd() throws IOException;
    void readColon() throws IOException;
    void readComma() throws IOException;
    JsonTokenType peekToken() throws IOException;
    void readToken() throws IOException;
    void close() throws IOException;

    SyntaxException error(String message);
}
