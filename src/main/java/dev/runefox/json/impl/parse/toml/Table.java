package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.Token;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Table implements TomlValue {
    private final Map<List<String>, TomlValue> values = new LinkedHashMap<>();
    private final List<String> list = new ArrayList<>();

    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;

    public void begin(Token token) {
        fromPos = token.fromPos();
        fromLine = token.fromLine();
        fromCol = token.fromCol();
    }

    public void end(Token token) {
        toPos = token.toPos();
        toLine = token.toLine();
        toCol = token.toCol();
    }

    public void add(KeyValue pair) throws SyntaxException {
        List<String> key = pair.key();
        TomlValue value = pair.value();

        if (values.containsKey(key)) {
            if (values.get(key) == null)
                throw pair.error("Key " + pair.reportKey() + " is defined as a subtable");
            throw pair.error("Key " + pair.reportKey() + " is already defined");
        }

        list.clear();
        for (String p : key) {
            list.add(p);
            if (values.get(list) != null) {
                throw pair.error("Key " + KeyValue.reportKey(list) + " is defined before, cannot add " + pair.reportKey());
            }
            values.put(List.copyOf(list), null);
        }

        values.put(key, value);
    }

    @Override
    public JsonNode toJson() {
        JsonNode node = JsonNode.object();

        for (Map.Entry<List<String>, TomlValue> entry : values.entrySet()) {
            List<String> k = entry.getKey();
            TomlValue v = entry.getValue();

            JsonNode n = node;
            for (int i = 0, l = k.size(); i < l - 1; i++) {
                String elem = k.get(i);
                if (n.has(elem)) {
                    n = n.get(elem);
                } else {
                    JsonNode o = JsonNode.object();
                    n.set(elem, o);
                    n = o;
                }
            }

            String lst = k.get(k.size() - 1);
            if (v == null) {
                if (!n.has(lst))
                    n.set(lst, JsonNode.object());
            } else {
                n.set(lst, v.toJson());
            }
        }

        return node;
    }

    @Override
    public int fromPos() {
        return fromPos;
    }

    @Override
    public int fromLine() {
        return fromLine;
    }

    @Override
    public int fromCol() {
        return fromCol;
    }

    @Override
    public int toPos() {
        return toPos;
    }

    @Override
    public int toLine() {
        return toLine;
    }

    @Override
    public int toCol() {
        return toCol;
    }
}
