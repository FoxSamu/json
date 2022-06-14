package net.shadew.json.template.parser;

import java.util.List;

public class TokenNode extends ParserNode {
    public TokenType tokenType;
    public Object value;

    public TokenNode(Token token) {
        update(token);
    }

    public TokenNode() {
    }

    public <T> T value(Class<T> type) {
        return type.cast(value);
    }

    public void update(Token token) {
        tokenType = token.getType();
        value = token.getValue();
        position(
            token.getFromPos(),
            token.getFromLine(),
            token.getFromCol(),
            token.getToPos(),
            token.getToLine(),
            token.getToCol()
        );
    }

    @Override
    protected List<ParserNode> childList() {
        return List.of();
    }

    @Override
    public TokenType type() {
        return tokenType;
    }

    @Override
    public EntityNode asEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String asString() {
        return "<TOKEN " + tokenType + (value != null ? ": " + value : "") + ">";
    }
}
