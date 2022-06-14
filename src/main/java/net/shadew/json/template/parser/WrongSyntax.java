package net.shadew.json.template.parser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WrongSyntax extends Throwable {
    public WrongSyntax(String message) {
        super(message);
    }

    public static WrongSyntax expected(Expectable... expectable) {
        return new WrongSyntax("Expected " + Stream.of(expectable).map(Expectable::errorName).collect(Collectors.joining(", ")));
    }

    public static WrongSyntax expected(String... expectable) {
        return new WrongSyntax("Expected " + String.join(", ", expectable));
    }

    public static WrongSyntax unexpected(Expectable... expectable) {
        return new WrongSyntax("Unexpected " + Stream.of(expectable).map(Expectable::errorName).collect(Collectors.joining(", ")));
    }

    public static WrongSyntax unexpected(String... expectable) {
        return new WrongSyntax("Unexpected " + String.join(", ", expectable));
    }

    // If they can't merge, return self
    public WrongSyntax merge(WrongSyntax other) {
        return this;
    }

    public static class Expected extends WrongSyntax {
        private final List<String> expected;

        public Expected(String... expected) {
            super("Expected " + String.join(", ", expected));
            this.expected = List.of(expected);
        }

        public List<String> expected() {
            return expected;
        }

        @Override
        public WrongSyntax merge(WrongSyntax other) {
            if (!(other instanceof Expected))
                return this;
            Expected o = (Expected) other;
            return new Expected(Stream.concat(expected.stream(), o.expected.stream()).distinct().toArray(String[]::new));
        }
    }

    public static class Unexpected extends WrongSyntax {
        private final List<String> expected;

        public Unexpected(String... expected) {
            super("Unexpected " + String.join(", ", expected));
            this.expected = List.of(expected);
        }

        public List<String> expected() {
            return expected;
        }

        @Override
        public WrongSyntax merge(WrongSyntax other) {
            if (!(other instanceof Unexpected))
                return this;
            Unexpected o = (Unexpected) other;
            return new Unexpected(Stream.concat(expected.stream(), o.expected.stream()).distinct().toArray(String[]::new));
        }
    }
}
