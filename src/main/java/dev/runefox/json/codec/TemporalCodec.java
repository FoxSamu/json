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
            throw new JsonCodecException("Expected format " + fmt.toString() + ", got " + json.asExactString(), exc);
        }
    }
}
