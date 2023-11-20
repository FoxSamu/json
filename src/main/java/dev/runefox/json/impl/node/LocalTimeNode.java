package dev.runefox.json.impl.node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class LocalTimeNode extends TemporalNode<LocalTime> {
    public LocalTimeNode(LocalTime temporal) {
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
        throw expectedType("LOCAL_DATE");
    }

    @Override
    public LocalTime asLocalTime() {
        return asTemporal();
    }

    @Override
    protected String describeType() {
        return "LOCAL_TIME";
    }
}
