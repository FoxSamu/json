package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface ExceptionProcessor {
    ExceptionProcessor DEFAULT = (type, problem) -> JsonNode.object()
                                                            .set("type", type.toString().toLowerCase())
                                                            .set("message", problem);

    JsonNode generateException(ExceptionType type, String problem);

    default String writeExecutionException(Throwable exc) {
        return exc.toString();
    }
}
