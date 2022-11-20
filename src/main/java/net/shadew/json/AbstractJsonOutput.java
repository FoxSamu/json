package net.shadew.json;

import java.util.Stack;

public abstract class AbstractJsonOutput implements JsonOutput {
    private final Stack<State> stack = new Stack<>();

    public AbstractJsonOutput() {
        stack.push(new State(Context.ROOT));
    }

    @Override
    public JsonOutput key(String key) {
        stack.peek().key(key);
        return this;
    }

    @Override
    public JsonOutput stringValue(String string) {
        stack.peek().string(string);
        return this;
    }

    @Override
    public JsonOutput stringValue(String key, String string) {
        key(key);
        stringValue(string);
        return this;
    }

    @Override
    public JsonOutput numberValue(Number number) {
        stack.peek().number(number);
        return this;
    }

    @Override
    public JsonOutput numberValue(String key, Number number) {
        key(key);
        numberValue(number);
        return this;
    }

    @Override
    public JsonOutput boolValue(Boolean bool) {
        stack.peek().bool(bool);
        return this;
    }

    @Override
    public JsonOutput boolValue(String key, Boolean bool) {
        key(key);
        boolValue(bool);
        return this;
    }

    @Override
    public JsonOutput nullValue() {
        stack.peek().nullv();
        return this;
    }

    @Override
    public JsonOutput nullValue(String key) {
        key(key);
        nullValue();
        return this;
    }

    @Override
    public JsonOutput value(JsonNode value) {
        stack.peek().value(value);
        return this;
    }

    @Override
    public JsonOutput value(String key, JsonNode value) {
        key(key);
        value(value);
        return this;
    }

    @Override
    public JsonOutput startObject() {
        stack.push(stack.peek().startObject());
        return this;
    }

    @Override
    public JsonOutput startObject(String key) {
        key(key);
        startObject();
        return this;
    }

    @Override
    public JsonOutput startArray() {
        stack.push(stack.peek().startArray());
        return this;
    }

    @Override
    public JsonOutput startArray(String key) {
        key(key);
        startArray();
        return this;
    }

    @Override
    public JsonOutput end() {
        stack.peek().end();
        stack.pop();
        return this;
    }

    @Override
    public JsonOutput generate(JsonGenerator value) {
        value.generate(this);
        return this;
    }

    @Override
    public void startStream() {
        stack.clear();
        stack.push(new State(Context.ROOT));

        outputDocumentStart();
    }

    @Override
    public void endStream() {
        if (stack.peek().context != Context.ROOT) {
            throw new IllegalStateException("Missing end call");
        }
    }

    protected abstract void outputString(String value);
    protected abstract void outputString(String key, String value);

    protected abstract void outputNumber(Number value);
    protected abstract void outputNumber(String key, Number value);

    protected abstract void outputBoolean(Boolean value);
    protected abstract void outputBoolean(String key, Boolean value);

    protected abstract void outputValue(JsonNode value);
    protected abstract void outputValue(String key, JsonNode value);

    protected abstract void outputNull();
    protected abstract void outputNull(String key);

    protected abstract void outputObjectStart();
    protected abstract void outputObjectStart(String key);
    protected abstract void outputObjectEnd();

    protected abstract void outputArrayStart();
    protected abstract void outputArrayStart(String key);
    protected abstract void outputArrayEnd();

    protected abstract void outputDocumentStart();
    protected abstract void outputDocumentEnd();


    private enum Context {
        ROOT,
        ARRAY,
        OBJECT
    }

    private class State {
        final Context context;
        String key;
        boolean hadValue;

        private State(Context context) {
            this.context = context;
        }

        void key(String key) {
            if (context == Context.OBJECT) {
                if (this.key != null)
                    throw new IllegalStateException("Key already written");
                this.key = key;
            } else {
                throw new IllegalStateException("Cannot write key to non-object context");
            }
        }

        void string(String value) {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputString(key, value);
                key = null;
            } else {
                outputString(value);
            }
            hadValue = true;
        }

        void number(Number value) {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputNumber(key, value);
                key = null;
            } else {
                outputNumber(value);
            }
            hadValue = true;
        }

        void bool(Boolean value) {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputBoolean(key, value);
                key = null;
            } else {
                outputBoolean(value);
            }
            hadValue = true;
        }

        void nullv() {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputNull(key);
                key = null;
            } else {
                outputNull();
            }
            hadValue = true;
        }

        void value(JsonNode value) {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputValue(key, value);
                key = null;
            } else {
                outputValue(value);
            }
            hadValue = true;
        }

        void end() {
            switch (context) {
                case ARRAY:
                    outputArrayEnd();
                    break;
                case OBJECT:
                    outputObjectEnd();
                    break;
                default:
                    throw new IllegalStateException("Cannot end root context");
            }
        }

        State startObject() {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputObjectStart(key);
                key = null;
            } else {
                outputObjectStart();
            }
            hadValue = true;
            return new State(Context.OBJECT);
        }

        State startArray() {
            if (hadValue && context == Context.ROOT) {
                throw new IllegalStateException("Multiple root values");
            }
            if (context == Context.OBJECT) {
                if (key == null)
                    throw new IllegalStateException("Missing key for object context");
                outputArrayStart(key);
                key = null;
            } else {
                outputArrayStart();
            }
            hadValue = true;
            return new State(Context.ARRAY);
        }
    }
}
