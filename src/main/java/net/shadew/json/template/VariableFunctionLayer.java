package net.shadew.json.template;

import java.util.LinkedHashMap;
import java.util.Map;

import net.shadew.json.JsonNode;

public class VariableFunctionLayer {
    private final TemplateContext owner;
    private final VariableFunctionLayer parent;
    private final Scope scope;
    private final Map<String, JsonNode> variables = new LinkedHashMap<>();
    private final Map<String, TemplateFunction> functions = new LinkedHashMap<>();
    private boolean used;

    public VariableFunctionLayer(TemplateContext owner, VariableFunctionLayer parent, Scope scope) {
        this.owner = owner;
        this.parent = parent;
        this.scope = scope;
    }

    public VariableFunctionLayer parent() {
        return parent;
    }

    public boolean isRoot() {
        return scope == null;
    }

    public void set(String name, JsonNode val) {
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
        if (!functions.containsKey(name))
            return owner.exception(ExceptionType.UNDEFINED_FUNCTION, "Function '" + name + "' does not exist");
        try {
            return JsonNode.orNull(functions.get(name).call(owner, this, args));
        } catch (Exception exc) {
            return owner.execException(exc);
        }
    }

    public void define(String name, TemplateFunction func) {
        if (func == null)
            throw new IllegalArgumentException("Function cannot be null");
        if (used)
            throw new IllegalStateException("Cannot define function after use of layer");
        functions.put(name, func);
    }

    public VariableFunctionLayer newLayer(Scope scope) {
        return new VariableFunctionLayer(owner, this, scope);
    }

    public VariableFunctionLayer newLayer(ScopeType scope) {
        return new VariableFunctionLayer(owner, this, new Scope(scope));
    }

    public VariableFunctionLayer newLayerWithSameScope() {
        return new VariableFunctionLayer(owner, this, scope);
    }

    public VariableFunctionLayer newSubtemplateLayer() {
        return new VariableFunctionLayer(owner, this, null);
    }

    public static VariableFunctionLayer root(TemplateContext owner) {
        return new VariableFunctionLayer(owner, null, null);
    }
}
