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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializeTests {
    private Json json;

    @BeforeEach
    void beforeEach() {
        json = Json.jsonBuilder().serializationConfig(
                JsonSerializingConfig.compact()
                        .json5(true)
                        .anyValue(true)
        ).build();
    }

    @Test
    void testNonfinite() throws Exception {
        String data = json.serialize(JsonNode.numberArray(
                Double.NaN,
                Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY
        ));
        Assertions.assertEquals("[NaN,Infinity,-Infinity]", data);
    }
}
