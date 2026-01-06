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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class StreamWriteTests {
    private static final String EXPECTED = """
        {"a":3}{"b":5}{"c":8}[3]""";

    private static final Json JSON = Json.jsonBuilder().serializationConfig(
        JsonSerializingConfig.compact()
    ).build();

    @Test
    void streamReadTest() throws IOException {
        Writer writer = new StringWriter();

        JsonOutput out = JSON.output(writer);
        out.write(JSON.parse("{\"a\": 3}"));
        out.write(JSON.parse("{\"b\": 5}"));
        out.write(JSON.parse("{\"c\": 8}"));
        out.write(JSON.parse("[3]"));
        out.close();

        Assertions.assertEquals(EXPECTED, writer.toString());
    }
}
