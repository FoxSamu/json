package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.impl.node.StringNode;
import dev.runefox.json.impl.parse.CharUtil;

import java.util.List;
import java.util.stream.Collectors;

public class KeyValue implements TomlSyntax {
    private final List<String> key;
    private TomlValue value;

    private final int fromPos;
    private final int fromLine;
    private final int fromCol;

    public KeyValue(List<String> key, int fromPos, int fromLine, int fromCol) {
        this.key = List.copyOf(key);
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
    }

    public List<String> key() {
        return key;
    }

    public void value(TomlValue value) {
        this.value = value;
    }

    public TomlValue value() {
        return value;
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
        return value.toPos();
    }

    @Override
    public int toLine() {
        return value.toLine();
    }

    @Override
    public int toCol() {
        return value.toCol();
    }

    public String reportKey() {
        return reportKey(key);
    }

    public static String reportKey(List<String> key) {
        return key.stream().map(k -> {
            if (CharUtil.isIdentifierValid(k)) {
                return k;
            } else {
                return StringNode.quote(k);
            }
        }).collect(Collectors.joining("."));
    }
}
