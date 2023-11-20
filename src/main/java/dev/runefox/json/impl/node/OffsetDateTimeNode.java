package dev.runefox.json.impl.node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class OffsetDateTimeNode extends TemporalNode<OffsetDateTime> {
    public OffsetDateTimeNode(OffsetDateTime temporal) {
        super(temporal);
    }

    @Override
    public boolean isOffsetDateTime() {
        return true;
    }

    @Override
    public boolean isLocalDateTime() {
        return false;
    }

    @Override
    public boolean isLocalDate() {
        return false;
    }

    @Override
    public boolean isLocalTime() {
        return false;
    }

    @Override
    public OffsetDateTime asOffsetDateTime() {
        return asTemporal();
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
        throw expectedType("LOCAL_TIME");
    }

    @Override
    protected String describeType() {
        return "OFFSET_DATE_TIME";
    }
}
