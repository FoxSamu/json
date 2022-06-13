package net.shadew.json.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.shadew.json.JsonNode;

public class FunctionDefinition implements Comparable<FunctionDefinition> {
    private final String name;
    private final List<String> params;
    private final boolean varArg;
    private final boolean subtemplate;
    private final Call call;

    public FunctionDefinition(String name, List<String> params, boolean varArg, boolean subtemplate, Call call) {
        this.name = name;
        this.params = List.copyOf(params);
        this.varArg = varArg;
        this.subtemplate = subtemplate;
        this.call = call;
    }

    public String name() {
        return name;
    }

    public List<String> params() {
        return params;
    }

    public boolean isVarArg() {
        return varArg;
    }

    public boolean isSubtemplate() {
        return subtemplate;
    }

    public int parameterCount() {
        if (varArg) return -1;
        return params.size();
    }

    public JsonNode call(TemplateContext context, Vfl fnLayer, JsonNode... args) {
        if (varArg && args.length < params.size() - 1)
            throw new IllegalArgumentException("Incorrect argument count");
        if (!varArg && args.length != params.size())
            throw new IllegalArgumentException("Incorrect argument count");
        context.pushVflForFunctionCall(name, fnLayer, subtemplate);
        Vfl vfl = context.vfl();
        if (varArg) {
            JsonNode varargsArray = JsonNode.array();
            int pl = params.size() - 1;
            for (int i = 0, l = args.length; i < l; i++) {
                if (i < pl) {
                    vfl.fnParam(params.get(i), args[i]);
                } else {
                    varargsArray.add(args[i]);
                }
            }
            vfl.fnParam(params.get(pl), varargsArray);
        } else {
            for (int i = 0, l = args.length; i < l; i++) {
                vfl.fnParam(params.get(i), args[i]);
            }
        }

        JsonNode node = call.call(context);
        context.popVfl();
        return node;
    }

    @Override
    public int compareTo(FunctionDefinition o) {
        int mc = parameterCount();
        int nc = o.parameterCount();
        if (mc == nc)
            return 0;
        if (mc == -1)
            return -1;
        if (nc == -1)
            return 1;
        return Integer.compare(mc, nc);
    }

    public interface Call {
        JsonNode call(TemplateContext context);
    }

    public static Call callExpression(Expression expr) {
        return ctx -> ctx.evaluate(expr);
    }

    public static Call callSubtemplate(Instructions insns) {
        return ctx -> ctx.evaluate(insns);
    }

    public static Function createOverloadedFunction(List<FunctionDefinition> definitions) {
        if (definitions.size() == 0) {
            throw new IllegalArgumentException("Provide at least one overload");
        }

        String name = definitions.get(0).name;

        Map<Integer, FunctionDefinition> overloads = new HashMap<>();

        for (FunctionDefinition definition : definitions) {
            if (!definition.name.equals(name))
                throw new IllegalArgumentException("All overloads must have the same name");

            int pcount = definition.parameterCount();
            if (overloads.containsKey(pcount))
                throw new IllegalArgumentException("Multiple overloads with same parameter count");
            overloads.put(pcount, definition);
        }

        FunctionDefinition vararg = overloads.get(-1);

        if (vararg != null) {
            return (ctx, vfl, nodes) -> {
                int count = nodes.length;
                if (overloads.containsKey(count))
                    return overloads.get(count).call(ctx, vfl, nodes);
                return vararg.call(ctx, vfl, nodes);
            };
        } else {
            return (ctx, vfl, nodes) -> {
                int count = nodes.length;
                if (overloads.containsKey(count))
                    return overloads.get(count).call(ctx, vfl, nodes);
                return ctx.exception(ExceptionType.FUNCTION_PARAM_MISMATCH, "No overload of " + name + " that takes " + count + " parameters");
            };
        }
    }
}
