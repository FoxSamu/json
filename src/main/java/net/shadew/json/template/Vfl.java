package net.shadew.json.template;

import java.util.LinkedHashMap;
import java.util.Map;

import net.shadew.json.JsonNode;

public class Vfl { // Variable-Function Layer
    private final String name;
    private final TemplateContext owner;
    private final Vfl parent;
    private final Execution scope;
    private final Map<String, JsonNode> variables = new LinkedHashMap<>();
    private final Map<String, Function> functions = new LinkedHashMap<>();
    private final boolean partial;
    private boolean used;

    public Vfl(String name, TemplateContext owner, Vfl parent, Execution scope, boolean partial) {
        this.name = name;
        this.owner = owner;
        this.parent = parent;
        this.scope = scope;
        this.partial = partial;
    }

    public Vfl parent() {
        return parent;
    }

    public boolean isRoot() {
        return scope == null;
    }

    public boolean isPartial() {
        return partial;
    }

    public String debugPath() {
        return (parent != null ? parent.debugPath() + " > " : "") + name;
    }

    private boolean hasVar(String name) {
        if (variables.containsKey(name))
            return true;
        return parent != null && parent.hasVar(name);
    }

    private boolean setPartial(String name, JsonNode val) {
        if (hasVar(name)) {
            variables.put(name, JsonNode.orNull(val));
            return true;
        }
        return false;
    }

    public void set(String name, JsonNode val) {
        if (partial && parent.setPartial(name, val))
            return;
        variables.put(name, JsonNode.orNull(val));
    }

    public JsonNode get(String name) {
        used = true;
        if (!variables.containsKey(name)) {
            if (parent == null)
                return owner.exception(ExceptionType.UNDEFINED_VARIABLE, "Variable '" + name + "' does not exist");
            return parent.get(name);
        }
        return JsonNode.orNull(variables.get(name));
    }

    public JsonNode underscore() {
        used = true;
        if (isRoot())
            return owner.exception(ExceptionType.NO_SCOPE_IN_ROOT, "Cannot access _ in root");
        return scope.value();
    }

    public JsonNode dollar() {
        used = true;
        if (isRoot())
            return owner.exception(ExceptionType.NO_SCOPE_IN_ROOT, "Cannot access $ in root");
        return dollar0();
    }

    private JsonNode dollar0() {
        if (parent.isRoot())
            return scope.value();
        return parent.dollar0();
    }

    public JsonNode call(String name, JsonNode... args) {
        used = true;
        if (!functions.containsKey(name)) {
            if (parent == null)
                return owner.exception(ExceptionType.UNDEFINED_FUNCTION, "Function '" + name + "' does not exist");
            return parent.call(name, args);
        }
        try {
            return JsonNode.orNull(functions.get(name).call(owner, this, args));
        } catch (Exception exc) {
            return owner.execException(exc);
        }
    }

    public void define(String name, Function func) {
        if (func == null)
            throw new IllegalArgumentException("Function cannot be null");
        if (used)
            throw new IllegalStateException("Cannot define function after use of layer");
        functions.put(name, func);
    }

    public Vfl newLayer(String name, Execution scope) {
        return new Vfl(name, owner, this, scope, false);
    }

    public Vfl newLayerWithSameScope(String name) {
        return new Vfl(name, owner, this, scope, true);
    }

    public Vfl newSubtemplateLayer(String name) {
        return new Vfl(name, owner, this, null, false);
    }

    public static Vfl root(String name, TemplateContext owner) {
        return new Vfl(name, owner, null, null, false);
    }
}
