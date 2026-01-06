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

package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

class TemporalCodec<T extends TemporalAccessor> implements JsonCodec<T> {
    static final DateTimeFormatter YEAR_FMT = new DateTimeFormatterBuilder()
                                                  .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                                                  .toFormatter();
    static final DateTimeFormatter MONTH_FMT = new DateTimeFormatterBuilder()
                                                   .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                                                   .toFormatter();
    static final DateTimeFormatter YEAR_MONTH_FMT = new DateTimeFormatterBuilder()
                                                        .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                                                        .appendLiteral('-')
                                                        .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                                                        .toFormatter();
    static final DateTimeFormatter MONTH_DAY_FMT = new DateTimeFormatterBuilder()
                                                       .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                                                       .appendLiteral('-')
                                                       .appendValue(ChronoField.DAY_OF_MONTH, 2)
                                                       .toFormatter();

    private final DateTimeFormatter fmt;
    private final TemporalQuery<T> q;

    TemporalCodec(DateTimeFormatter fmt, TemporalQuery<T> q) {
        this.fmt = fmt;
        this.q = q;
    }

    @Override
    public JsonNode encode(T obj) {
        return JsonNode.string(fmt.format(obj));
    }

    @Override
    public T decode(JsonNode json) {
        try {
            return fmt.parse(json.asExactString(), q);
        } catch (DateTimeParseException exc) {
            throw new CodecException("Expected format " + fmt.toString() + ", got " + json.asExactString(), exc);
        }
    }
}
