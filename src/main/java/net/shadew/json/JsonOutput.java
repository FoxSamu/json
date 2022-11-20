package net.shadew.json;

public interface JsonOutput {
    JsonOutput key(String key);

    JsonOutput stringValue(String string);
    JsonOutput stringValue(String key, String string);

    JsonOutput numberValue(Number number);
    JsonOutput numberValue(String key, Number number);

    JsonOutput boolValue(Boolean bool);
    JsonOutput boolValue(String key, Boolean bool);

    JsonOutput nullValue();
    JsonOutput nullValue(String key);

    JsonOutput value(JsonNode value);
    JsonOutput value(String key, JsonNode value);

    JsonOutput startObject();
    JsonOutput startObject(String key);

    JsonOutput startArray();
    JsonOutput startArray(String key);

    JsonOutput end();

    JsonOutput generate(JsonGenerator value);

    void startStream();
    void endStream();
}
