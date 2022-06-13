package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface Function {
    JsonNode call(TemplateContext ctx, Vfl vfl, JsonNode... nodes);

    static Function of(String name, Expression expr, String... params) {
        // TODO: Make sure that parameters don't leak into parent scopes
        return (ctx, vfl, nodes) -> {
            if (params.length > nodes.length) {
                return ctx.exception(ExceptionType.FUNCTION_PARAM_MISMATCH, "Function parameter mismatch, expected " + params.length + " but got " + nodes.length);
            }

            int i = 0;
            for (String param : params) {
                vfl.set(param, nodes[i]);
                i++;
            }

            return ctx.evaluate(expr);
        };
    }
}
