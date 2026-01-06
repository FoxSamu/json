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

package dev.runefox.json.impl.parse;

import dev.runefox.json.SyntaxException;

public class Token {
    private TokenType type;
    private Object value;
    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;

    public Token() {

    }

    public Token(TokenType type, Object value, int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
        this.type = type;
        this.value = value;
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public void set(TokenType type, Object value, int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
        this.type = type;
        this.value = value;
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public TokenType type() {
        return type;
    }

    public Object value() {
        return value;
    }

    public SyntaxException error(String problem) {
        return new SyntaxException(fromPos, fromLine, fromCol, toPos, toLine, toCol, problem);
    }
}
