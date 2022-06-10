package net.shadew.json.template;

import net.shadew.json.JsonNode;

public class ScopeExecution {
    private final Scope scope;
    private final ScopeCommand[] commands;
    private int pos = 0;
    private JsonNode exception;

    public ScopeExecution(Scope scope, ScopeCommand... commands) {
        this.scope = scope;
        this.commands = commands;

        int pos = 0;
        for (ScopeCommand cmd : commands) {
            if (cmd instanceof ScopeCommand.Label) {
                ((ScopeCommand.Label) cmd).update(this, pos);
            }
            pos++;
        }
    }

    public Scope scope() {
        return scope;
    }

    public int pos() {
        return pos;
    }

    public void branch(int pos) {
        this.pos = pos;
    }

    public void branch(ScopeCommand.Label label) {
        if (label.exec() != this)
            throw new IllegalArgumentException("Invalid label");
        this.pos = label.pos();
    }

    public boolean shouldContinue() {
        return exception == null && !scope.requiresTermination();
    }

    public JsonNode exception() {
        return exception;
    }

    public void exception(JsonNode exception) {
        this.exception = exception;
    }
}
