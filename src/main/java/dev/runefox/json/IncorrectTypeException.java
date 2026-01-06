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

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncorrectTypeException extends NodeException {
    public IncorrectTypeException() {
    }

    public IncorrectTypeException(String message) {
        super(message);
    }

    public IncorrectTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTypeException(Throwable cause) {
        super(cause);
    }

    public IncorrectTypeException(NodeType found, NodeType... required) {
        super(makeMessage(found, required));
    }

    public IncorrectTypeException(int index, NodeType found, NodeType... required) {
        super(makeMessage(index, found, required));
    }


    private static String makeMessage(NodeType found, NodeType... required) {
        return String.format(
            "Unmatched types, required %s, found %s",
            Stream.of(required).map(NodeType::name).collect(Collectors.joining(", ")),
            found
        );
    }

    private static String makeMessage(int index, NodeType found, NodeType... required) {
        return String.format(
            "Unmatched types for index %d of array, required %s, found %s",
            index,
            Stream.of(required).map(NodeType::name).collect(Collectors.joining(", ")),
            found
        );
    }
}
