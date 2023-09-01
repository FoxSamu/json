package dev.runefox.json.codec;

import java.util.Objects;

public class EncodableObject {
    public static final JsonCodec<EncodableObject> CODEC = new RecordCodec<>() {
        private final JsonCodec<String> string5 = JsonCodec.string(5);

        @Override
        protected EncodableObject decode(DecodeContext<EncodableObject> ctx) {
            EncodableObject obj = new EncodableObject(
                ctx.field("integer", INT),
                ctx.field("string", string5),
                ctx.field("bool", BOOLEAN)
            );
            ctx.applyField("optional_string", this, o -> obj.optional = o);
            return obj;
        }

        @Override
        protected void encode(EncodeContext<EncodableObject> ctx, EncodableObject obj) {
            ctx.field("integer", INT, obj.integer);
            ctx.field("string", string5, obj.string);
            ctx.field("bool", BOOLEAN, obj.bool);
            if (obj.optional != null)
                ctx.field("optional_string", this, obj.optional);
        }
    };

    public final int integer;
    public final String string;
    public final boolean bool;
    public EncodableObject optional;

    public EncodableObject(int integer, String string, boolean bool) {
        this.integer = integer;
        this.string = string;
        this.bool = bool;
    }

    public EncodableObject(int integer, String string, boolean bool, EncodableObject optional) {
        this.integer = integer;
        this.string = string;
        this.bool = bool;
        this.optional = optional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EncodableObject that = (EncodableObject) o;
        return integer == that.integer
                   && bool == that.bool
                   && string.equals(that.string)
                   && Objects.equals(optional, that.optional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(integer, string, bool, optional);
    }

    @Override
    public String toString() {
        return "EncodableObject [" +
                   "integer: " + integer +
                   ", string: '" + string + "'" +
                   ", bool: " + bool +
                   ", optional: " + optional +
                   "]";
    }
}
