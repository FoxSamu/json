package dev.runefox.json.impl.node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class LocalDateNode extends TemporalNode<LocalDate> {
    public LocalDateNode(LocalDate temporal) {
        super(temporal);
    }

    @Override
    public boolean isOffsetDateTime() {
        return false;
    }

    @Override
    public boolean isLocalDateTime() {
        return false;
    }

    @Override
    public boolean isLocalDate() {
        return true;
    }

    @Override
    public boolean isLocalTime() {
        return false;
    }

    @Override
    public OffsetDateTime asOffsetDateTime() {
        throw expectedType("OFFSET_DATE_TIME");
    }

    @Override
    public LocalDateTime asLocalDateTime() {
        throw expectedType("LOCAL_DATE_TIME");
    }

    @Override
    public LocalDate asLocalDate() {
        return asTemporal();
    }

    @Override
    public LocalTime asLocalTime() {
        throw expectedType("LOCAL_TIME");
    }

    @Override
    protected String describeType() {
        return "LOCAL_DATE";
    }
}
