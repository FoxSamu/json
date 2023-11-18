package dev.runefox.json;

public class JsonParsingConfig {
    private JsonParsingConfig() {
    }

    private boolean json5;

    public boolean json5() {
        return json5;
    }

    public JsonParsingConfig json5(boolean json5) {
        this.json5 = json5;
        return this;
    }

    private boolean anyValue;

    public boolean anyValue() {
        return anyValue;
    }

    public JsonParsingConfig anyValue(boolean anyValue) {
        this.anyValue = anyValue;
        return this;
    }

    private boolean allowNonExecutePrefix;

    public boolean allowNonExecutePrefix() {
        return allowNonExecutePrefix;
    }

    public JsonParsingConfig allowNonExecutePrefix(boolean allowNonExecutePrefix) {
        this.allowNonExecutePrefix = allowNonExecutePrefix;
        return this;
    }

    public JsonParsingConfig copy() {
        return new JsonParsingConfig().copyFrom(this);
    }

    public JsonParsingConfig copyFrom(JsonParsingConfig copy) {
        this.json5 = copy.json5;
        this.anyValue = copy.anyValue;
        this.allowNonExecutePrefix = copy.allowNonExecutePrefix;
        return this;
    }

    public static JsonParsingConfig standard() {
        return new JsonParsingConfig();
    }
}
