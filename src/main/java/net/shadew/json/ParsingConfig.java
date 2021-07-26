package net.shadew.json;

public class ParsingConfig {
    private ParsingConfig() {
    }

    private boolean json5;

    public boolean json5() {
        return json5;
    }

    public ParsingConfig json5(boolean json5) {
        this.json5 = json5;
        return this;
    }

    private boolean allowNonExecutePrefix;

    public boolean allowNonExecutePrefix() {
        return allowNonExecutePrefix;
    }

    public ParsingConfig allowNonExecutePrefix(boolean allowNonExecutePrefix) {
        this.allowNonExecutePrefix = allowNonExecutePrefix;
        return this;
    }

    public ParsingConfig copy() {
        return new ParsingConfig().copyFrom(this);
    }

    public ParsingConfig copyFrom(ParsingConfig copy) {
        this.json5 = copy.json5;
        return this;
    }

    public static ParsingConfig standard() {
        return new ParsingConfig();
    }
}
