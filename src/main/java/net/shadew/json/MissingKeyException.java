package net.shadew.json;

public class MissingKeyException extends JsonException {
    private final String key;

    public MissingKeyException(String key) {
        super("Missing key '" + key + "'");
        this.key = key;
    }

    public String key() {
        return key;
    }
}
