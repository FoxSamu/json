package net.shadew.json.template;

import java.util.EmptyStackException;
import java.util.Stack;

import net.shadew.json.JsonNode;

public class TemplateContext {
    private Vfl vfl = Vfl.root("root", this);

    public void pushVfl(String name, Execution scope) {
        vfl = vfl.newLayer(name, scope);
        if (TemplateDebug.debug)
            TemplateDebug.pushVfl.accept(vfl);
    }

    public void pushVflWithSameScope(String name) {
        vfl = vfl.newPartialLayer(name);
        if (TemplateDebug.debug)
            TemplateDebug.pushVfl.accept(vfl);
    }

    public void pushVflForSubtemplate() {
        vfl = vfl.newSubtemplateLayer("subtemplate");
        if (TemplateDebug.debug)
            TemplateDebug.pushVfl.accept(vfl);
    }

    public void pushVflForFunctionCall(String fnName, Vfl functionLayer, boolean subtemplate) {
        vfl = functionLayer.newFunctionCallLayer(fnName + "()", vfl, subtemplate);
        if (TemplateDebug.debug)
            TemplateDebug.pushVfl.accept(vfl);
    }

    public void popVfl() {
        if (vfl.below() == null)
            throw new EmptyStackException();
        if (TemplateDebug.debug)
            TemplateDebug.popVfl.accept(vfl);
        vfl = vfl.below();
    }

    public Vfl vfl() {
        return vfl;
    }

    private final Stack<Execution> executionStack = new Stack<>();

    private Execution newExecution(ExecutionType type, Instructions insns) {
        if (TemplateDebug.debug)
            return new ExecutionWithDebug(this, type, insns);
        return new Execution(this, type, insns);
    }

    public JsonNode evaluate(Expression expr) {
        return expr.evaluate(this, vfl());
    }

    public JsonNode evaluate(Instructions instructions, ExecutionType type) {
        Execution exec = newExecution(type, instructions);

        try {
            if (type == ExecutionType.ROOT) pushVflForSubtemplate();
            else pushVfl(type == ExecutionType.ARRAY ? "array" : "object", exec);

            return runExec(exec);
        } finally {
            popVfl();
        }
    }

    public JsonNode evaluate(Instructions instructions) {
        Execution exec = newExecution(ExecutionType.ROOT, instructions);
        return runExec(exec);
    }

    private JsonNode runExec(Execution exec) {
        exec.registerFunctions();
        try {
            executionStack.push(exec);
            return exec.run();
        } finally {
            executionStack.pop();
        }
    }

    public Execution currentExecution() {
        if (executionStack.empty())
            return null;
        return executionStack.peek();
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
