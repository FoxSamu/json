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

import java.io.IOException;

public class SyntaxException extends IOException {
    private final int fromIndex;
    private final int fromLine;
    private final int fromCol;
    private final int toIndex;
    private final int toLine;
    private final int toCol;
    private final String problem;

    public SyntaxException(int fromIndex, int fromLine, int fromCol, int toIndex, int toLine, int toCol, String problem) {
        this.fromIndex = fromIndex;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toIndex = toIndex;
        this.toLine = toLine;
        this.toCol = toCol;
        this.problem = problem;
    }

    public SyntaxException(int fromIndex, int fromLine, int fromCol, String problem) {
        this.fromIndex = fromIndex;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toIndex = fromIndex + 1;
        this.toLine = fromLine;
        this.toCol = fromCol + 1;
        this.problem = problem;
    }

    public String problem() {
        return problem;
    }

    public int fromIndex() {
        return fromIndex;
    }

    public int fromLine() {
        return fromLine;
    }

    public int fromCol() {
        return fromCol;
    }

    public int toIndex() {
        return toIndex;
    }

    public int toLine() {
        return toLine;
    }

    public int toCol() {
        return toCol;
    }

    @Override
    public String getMessage() {
        return "Line " + fromLine + ", col " + fromCol + ": " + problem;
    }

    public SyntaxException withMessage(String message) {
        return new SyntaxException(fromIndex, fromLine, fromCol, toIndex, toLine, toCol, message);
    }
}
