package dev.runefox.json.impl.parse.toml;

import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TomlDates {
    private static final Pattern DATE = Pattern.compile(
        "([0-9]{4})-([0-9]{2})-([0-9]{2})"
    );
    private static final Pattern TIME = Pattern.compile(
        "([0-9]{2}):([0-9]{2}):([0-9]{2})(\\.[0-9]+)?"
    );
    private static final Pattern DATE_TIME = Pattern.compile(
        "([0-9]{4})-([0-9]{2})-([0-9]{2})[T ]([0-9]{2}):([0-9]{2}):([0-9]{2})(\\.[0-9]+)?"
    );
    private static final Pattern OFF_DATE_TIME = Pattern.compile(
        "([0-9]{4})-([0-9]{2})-([0-9]{2})[T ]([0-9]{2}):([0-9]{2}):([0-9]{2})(\\.[0-9]+)?(Z|[+-][0-9]{2}:[0-9]{2})"
    );

    private static int nano(String frac) {
        if (frac == null)
            return 0;

        frac = frac.substring(1); // Skip the .

        // Make 9 digits long
        if (frac.length() > 9) {
            frac = frac.substring(0, 9);
        }

        if (frac.length() < 9) {
            frac += "0".repeat(9 - frac.length());
        }

        return Integer.parseInt(frac);
    }

    private static ZoneOffset offset(String off) {
        return ZoneOffset.of(off);
    }

    public static OffsetDateTime offsetDateTime(String temporal) {
        try {
            Matcher m = OFF_DATE_TIME.matcher(temporal);
            if (!m.matches()) {
                return null;
            }

            int yr = Integer.parseInt(m.group(1));
            int mth = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hr = Integer.parseInt(m.group(4));
            int min = Integer.parseInt(m.group(5));
            int sec = Integer.parseInt(m.group(6));
            int ns = nano(m.group(7));
            ZoneOffset off = offset(m.group(8));

            return OffsetDateTime.of(yr, mth, day, hr, min, sec, ns, off);
        } catch (Exception exc) {
            return null;
        }
    }

    public static LocalDateTime localDateTime(String temporal) {
        try {
            Matcher m = DATE_TIME.matcher(temporal);
            if (!m.matches()) {
                return null;
            }

            int yr = Integer.parseInt(m.group(1));
            int mth = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));
            int hr = Integer.parseInt(m.group(4));
            int min = Integer.parseInt(m.group(5));
            int sec = Integer.parseInt(m.group(6));
            int ns = nano(m.group(7));

            return LocalDateTime.of(yr, mth, day, hr, min, sec, ns);
        } catch (Exception exc) {
            return null;
        }
    }

    public static LocalDate localDate(String temporal) {
        try {
            Matcher m = DATE.matcher(temporal);
            if (!m.matches()) {
                return null;
            }

            int yr = Integer.parseInt(m.group(1));
            int mth = Integer.parseInt(m.group(2));
            int day = Integer.parseInt(m.group(3));

            return LocalDate.of(yr, mth, day);
        } catch (Exception exc) {
            return null;
        }
    }

    public static LocalTime localTime(String temporal) {
        try {
            Matcher m = TIME.matcher(temporal);
            if (!m.matches()) {
                return null;
            }

            int hr = Integer.parseInt(m.group(1));
            int min = Integer.parseInt(m.group(2));
            int sec = Integer.parseInt(m.group(3));
            int ns = nano(m.group(4));

            return LocalTime.of(hr, min, sec, ns);
        } catch (Exception exc) {
            return null;
        }
    }
}
