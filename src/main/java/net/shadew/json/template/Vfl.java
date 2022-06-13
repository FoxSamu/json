package net.shadew.json.template;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.shadew.json.JsonNode;

/**
 * A variable-function layer, shortly VFL, is a layer that stores the values of variables and the defined functions in a
 * certain scope during execution.
 * <p>
 * VFLs have several properties that define the behavior of getting and setting variables. VFLs inherit all the
 * variables and functions from its parent, which is in most cases (but not necessarily always) the layer right below it
 * in the VFL stack.
 * <p>
 * When reading a variable, a VFL first checks if it has a value for that variable itself and otherwise gets the
 * variable from its parent (which then does the same).
 * <p>
 * When assigning a variable, the variable is usually set directly on this VFL, meaning that any value for the same
 * variable name in any parent gets overridden. However, if the layer is a <strong>partial layer</strong>, it first
 * tries to write an existing variable in its parent (which may again recursively do the same if its a partial layer
 * too), and only if that fails it defines the variable for itself.
 * <p>
 * When calling a function, it does the same as with reading a variable: it first looks if it has the function defined
 * for itself, and otherwise falls back on its parent.
 * <p>
 * VFLs also provide access to the {@code _} and {@code $} identifiers, and define which are the values returned by
 * that.
 */
public class Vfl {
    /**
     * The name of the layer, for debug purposes.
     */
    private final String name;

    /**
     * The template execution context that owns this layer.
     */
    private final TemplateContext owner;

    /**
     * The parent of this layer, which it will fall back to when reading variables, or that it will attempt to write
     * variables in when it's a {@link #partial partial} layer
     */
    private final Vfl parent;

    /**
     * The layer below this layer in the VFL stack. This is often its parent, but in case of function calls this is the
     * VFL of the scope that the function was called in (whereas the parent is the scope that the function was defined
     * in).
     */
    private final Vfl below;

    /**
     * The scope of this VFL, purely to obtain the value of {@code _} from. This is null if this layer is the root of a
     * (sub)template, where there is no value for {@code _}.
     */
    private final Execution scope;

    /**
     * The scope of the farthest parent layer next to the nearest root layer, purely to obtain the value of {@code $}
     * from. This is null if this layer is the root of a (sub)template, where there is no value for {@code $}.
     */
    private final Execution dollarScope;

    /**
     * The map of variables directly defined in this VFL.
     */
    private final Map<String, JsonNode> variables = new LinkedHashMap<>();

    /**
     * The map of functions directly defined in this VFL.
     */
    private final Map<String, Function> functions = new LinkedHashMap<>();

    /**
     * Flag that indicates whether this layer is partial. A partial VFL attempts to write existing variables in its
     * nearest non-partial parent and all partial parents in between. A non-partial VFL always overrides variables from
     * its parents when assigning (but not when reading).
     * <p>
     * Say, a parent layer defines variable {@code x = 3}. A partial layer is pushed, and writes {@code x = 6}; when
     * this layer is popped, {@code x} will stay 6. However, if that layer were not partial, {@code x} would reset to
     * {@code 3}.
     * <p>
     * Partial layers are pushed when entering {@code if}-{@code else}, {@code for} or {@code switch} blocks, or when
     * calling a function, to make sure they write to variables outside of their scope. Non-partial layers are pushed in
     * {@code gen} blocks and array/object definitions.
     */
    private final boolean partial;

    /**
     * Flags whether this layer is used. When it is used, no functions may be added to it anymore. A VFL becomes used
     * when variables are read or written to it, or when a function is called from it.
     */
    private boolean used;

    /**
     * An iterator, for iteration loops. A VFL can only manage one iteration at a time, for nested loops multiple VFLs
     * are pushed (since the inside of a loop has its own variable scope after all).
     */
    private Iterator<?> iterator;

    /**
     * A value that a {@code switch} is being performed on. A VFL can only manage one switch at a time, for nested
     * switched multiple VFLs are pushed.
     */
    private JsonNode switching;

    /**
     * Instantiates a VFL.
     *
     * @param name    The name of this layer, for debug
     * @param owner   The owner context
     * @param parent  The parent layer
     * @param below   The layer below in the stack
     * @param scope   The execution scope for {@code _} and {@code $} access, or null if this is a (sub)template root
     * @param partial Whether this layer is partial
     */
    public Vfl(String name, TemplateContext owner, Vfl parent, Vfl below, Execution scope, boolean partial) {
        this.name = name;
        this.owner = owner;
        this.parent = parent;
        this.below = below;
        this.scope = scope;
        this.partial = partial;

        if (scope == null) {
            dollarScope = null;
        } else if (parent.scope == null) {
            dollarScope = scope;
        } else {
            Vfl p = parent;
            while (p.parent != null && p.parent.scope != null) {
                p = p.parent;
            }
            dollarScope = p.scope;
        }
    }

    /**
     * Returns the parent of this layer.
     */
    public Vfl parent() {
        return parent;
    }

    /**
     * Returns the layer below this layer in the VFL stack.
     */
    public Vfl below() {
        return below;
    }

    /**
     * Returns whether this layer is a root of the template or a subtemplate.
     */
    public boolean isRoot() {
        return scope == null;
    }

    /**
     * Returns whether this layer is partial.
     */
    public boolean isPartial() {
        return partial;
    }

    /**
     * Returns a debug path string, from the root through all parents to here.
     */
    public String debugPath() {
        return (parent != null ? parent.debugPath() + " > " : "") + name;
    }

    /**
     * Returns a debug stack string, from the root through all layers below to here.
     */
    public String debugStack() {
        return (below != null ? below.debugStack() + " > " : "") + name;
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

    /**
     * Performs a variable assignment. If this layer is partial, it will first attempt to assign an existing variable on
     * its parent (which may fall back to its parents as well if it's also a partial layer).
     *
     * @param name The name of the variable to assing
     * @param val  The value, a {@code null} value will convert to {@link JsonNode#NULL}.
     */
    public void set(String name, JsonNode val) {
        setUsed();
        if (partial && parent.setPartial(name, val))
            return;
        variables.put(name, JsonNode.orNull(val));
    }


    /**
     * Performs a variable assignment for a function parameter. If this layer is partial it still overrides the variable
     * in this layer.
     *
     * @param name The name of the variable to assing
     * @param val  The value, a {@code null} value will convert to {@link JsonNode#NULL}.
     */
    public void fnParam(String name, JsonNode val) {
        // Hard override on partial VFLs, we always want function parameters to override existing variables regardless
        setUsed();
        variables.put(name, JsonNode.orNull(val));
    }

    /**
     * Performs a variable read. If no variable with the given name is accessible, it will generate an exception value,
     * which is usually a JSON object like {@code {"type": "undefined_variable", "message": "Variable '[name]' does not
     * exist"}}.
     *
     * @param name The name of the variable to read
     */
    public JsonNode get(String name) {
        setUsed();
        if (!variables.containsKey(name)) {
            if (parent == null)
                return owner.exception(ExceptionType.UNDEFINED_VARIABLE, "Variable '" + name + "' does not exist");
            return parent.get(name);
        }
        return JsonNode.orNull(variables.get(name));
    }

    /**
     * Begins an iteration with the given iterator in this VFL's scope
     *
     * @param itr The iterator to iterate
     */
    public void iterate(Iterator<?> itr) {
        iterator = itr;
    }

    /**
     * Returns the iterator for an ongoing iteration in this layer, or one in its parent (and any of its partial
     * parents) if this is a partial layer.
     */
    @SuppressWarnings("unchecked")
    public <T> Iterator<T> iterator() {
        if (iterator == null && partial && parent != null)
            // We can cache this as our own iterator since there's no way the parent changes its iterator while
            // this VFL is in use
            return (Iterator<T>) (iterator = parent.iterator);
        return (Iterator<T>) iterator;
    }

    /**
     * Begins a switch operation with the given value in this VFL's scope
     *
     * @param val The value to switch
     */
    public void switchOver(JsonNode val) {
        switching = val;
    }

    /**
     * Returns the value for an ongoing switch operation in this layer, or one in its parent (and any of its partial
     * parents) if this is a partial layer.
     */
    public JsonNode switching() {
        if (switching == null && partial && parent != null)
            // We can cache this as our own value since there's no way the parent changes its switch value while
            // this VFL is in use
            return switching = parent.switching;
        return switching;
    }

    /**
     * Returns the value of {@code _} in this VFL's scope. If this is the root layer of the template or a subtemplate,
     * it will generate an exception value, which is usually a JSON object like {@code {"type": "no_scope_in_root",
     * "message": "Cannot access _ in root or subtemplate"}}.
     */
    public JsonNode underscore() {
        setUsed();
        if (isRoot())
            return owner.exception(ExceptionType.NO_SCOPE_IN_ROOT, "Cannot access _ in root or subtemplate");
        return scope.value();
    }

    /**
     * Returns the value of {@code $} in this VFL's scope. If this is the root layer of the template or a subtemplate,
     * it will generate an exception value, which is usually a JSON object like {@code {"type": "no_scope_in_root",
     * "message": "Cannot access $ in root or subtemplate"}}.
     */
    public JsonNode dollar() {
        setUsed();
        if (dollarScope == null)
            return owner.exception(ExceptionType.NO_SCOPE_IN_ROOT, "Cannot access $ in root or subtemplate");
        return dollarScope.value();
    }

    /**
     * Performs a function call. It will first try to call a function on itself, then falls back to its parent if it
     * doesn't have a function with the given name, which will repeat the process. If no function is found, it will
     * generate an exception value, which is usually a JSON object like {@code {"type": "undefined_function", "message":
     * "Function '[name]' does not exist"}}.
     *
     * @param name The function to call
     * @param args The arguments to the function
     */
    public JsonNode call(String name, JsonNode... args) {
        setUsed();
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

    /**
     * Recursively marks this VFL and its parents as used.
     */
    private void setUsed() {
        used = true;
        if (parent != null)
            parent.setUsed();
    }

    /**
     * Defines a function on this layer. If this layer is used it will instantly throw an exception.
     *
     * @param name The name of the function
     * @param func The function implementation
     */
    public void define(String name, Function func) {
        if (func == null)
            throw new IllegalArgumentException("Function cannot be null");
        if (used)
            throw new IllegalStateException("Cannot define function after use of layer");
        functions.put(name, func);
    }

    /**
     * Creates a new layer above this layer with a new scope and this layer as parent. The created layer will be
     * non-partial.
     * <p>
     * This type of layer is used in object/array definitions.
     *
     * @param name  The name of the layer
     * @param scope The new scope of the layer
     * @return The created layer
     */
    public Vfl newLayer(String name, Execution scope) {
        return new Vfl(name, owner, this, this, scope, false);
    }

    /**
     * Creates a new layer above this layer with this layer's scope and this layer as parent. The created layer will be
     * partial.
     * <p>
     * This type of layer is used in {@code if}-{@code else} blocks and other block-like entities.
     *
     * @param name The name of the layer
     * @return The created layer
     */
    public Vfl newPartialLayer(String name) {
        return new Vfl(name, owner, this, this, scope, true);
    }

    /**
     * Creates a new layer above this layer with a {@code null} scope and this layer as parent. The created layer will
     * be non-partial.
     * <p>
     * This type of layer is used in {@code gen} blocks.
     *
     * @param name The name of the layer
     * @return The created layer
     */
    public Vfl newSubtemplateLayer(String name) {
        return new Vfl(name, owner, this, this, null, false);
    }

    /**
     * Creates a new layer above the given layer with either this layer's scope or a null scope, and this layer as
     * parent. The created layer will be partial.
     * <p>
     * This type of layer is used in function calls, whose parent is the specific layer in which the function was
     * defined rather than the layer below.
     *
     * @param name The name of the layer
     * @return The created layer
     */
    public Vfl newFunctionCallLayer(String name, Vfl below, boolean subtemplate) {
        return new Vfl(name, owner, this, below, subtemplate ? null : scope, true);
    }

    /**
     * Creates a new layer with no parent or layer below, and a {@code null} scope. The created layer will be
     * non-partial since after all it has no parent.
     *
     * @param name  The name of the layer
     * @param owner The context owning this layer
     * @return The created layer
     */
    public static Vfl root(String name, TemplateContext owner) {
        return new Vfl(name, owner, null, null, null, false);
    }
}
