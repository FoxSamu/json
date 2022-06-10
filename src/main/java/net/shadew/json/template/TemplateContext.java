package net.shadew.json.template;

import java.util.EmptyStackException;

import net.shadew.json.JsonNode;

public class TemplateContext {
    private VariableFunctionLayer varFnLayer = VariableFunctionLayer.root(this);

    public void pushVarFnLayer(Scope scope) {
        varFnLayer = varFnLayer.newLayer(scope);
    }

    public void pushVarFnLayer(ScopeType scope) {
        varFnLayer = varFnLayer.newLayer(scope);
    }

    public void pushVarFnLayerWithSameScope() {
        varFnLayer = varFnLayer.newLayerWithSameScope();
    }

    public void pushVarFnLayerForSubtemplate() {
        varFnLayer = varFnLayer.newSubtemplateLayer();
    }

    public void popVarFnLayer() {
        if (varFnLayer.parent() == null)
            throw new EmptyStackException();
        varFnLayer = varFnLayer.parent();
    }

    public VariableFunctionLayer varFnLayer() {
        return varFnLayer;
    }

    public JsonNode evaluate(Expression expr) {
        return expr.evaluate(this, varFnLayer());
    }

    private ExceptionProcessor exceptionProcessor = ExceptionProcessor.DEFAULT;

    public TemplateContext exceptionProcessor(ExceptionProcessor processor) {
        if (processor == null)
            processor = ExceptionProcessor.DEFAULT;
        exceptionProcessor = processor;
        return this;
    }

    public JsonNode exception(ExceptionType type, String message) {
        return exceptionProcessor.generateException(type, message);
    }

    public JsonNode execException(Throwable exc) {
        return exceptionProcessor.generateException(ExceptionType.EXECUTION_EXCEPTION, exceptionProcessor.writeExecutionException(exc));
    }
}
