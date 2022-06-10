package net.shadew.json.template;

import java.util.Iterator;
import java.util.Map;

import net.shadew.json.JsonNode;

public abstract class ScopeCommand {
    public abstract void perform(ScopeExecution scope, TemplateContext context);
    public abstract boolean isValidIn(ScopeType type);

    static class Result extends ScopeCommand {
        private final Expression expr;

        Result(Expression expr) {
            this.expr = expr;
        }

        @Override
        public void perform(ScopeExecution execution, TemplateContext context) {
            Scope scope = execution.scope();
            if (scope.type() == ScopeType.ARRAY) {
                scope.value().add(context.evaluate(expr));
            } else {
                scope.produceResult(context.evaluate(expr));
            }
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return type != ScopeType.OBJECT;
        }
    }

    static class ResultWithKey extends ScopeCommand {
        private final Expression key;
        private final Expression expr;

        ResultWithKey(Expression key, Expression expr) {
            this.key = key;
            this.expr = expr;
        }

        @Override
        public void perform(ScopeExecution execution, TemplateContext context) {
            execution.scope().value().set(Operators.stringify(context.evaluate(key)), context.evaluate(expr));
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return type == ScopeType.OBJECT;
        }
    }

    static class VoidLine extends ScopeCommand {
        private final Expression expr;

        VoidLine(Expression expr) {
            this.expr = expr;
        }

        @Override
        public void perform(ScopeExecution scope, TemplateContext context) {
            context.evaluate(expr);
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return true;
        }
    }

    static class Label extends ScopeCommand {
        private ScopeExecution exec;
        private int pos = -1;

        @Override
        public void perform(ScopeExecution scope, TemplateContext context) {
        }

        public void update(ScopeExecution exec, int pos) {
            this.exec = exec;
            this.pos = pos;
        }

        public ScopeExecution exec() {
            return exec;
        }

        public int pos() {
            return pos;
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return true;
        }
    }

    static class Jump extends ScopeCommand {
        private final Label label;

        Jump(Label label) {
            this.label = label;
        }

        @Override
        public void perform(ScopeExecution exec, TemplateContext context) {
            exec.branch(label);
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return true;
        }
    }

    static class If extends Jump {
        private final Expression cond;

        If(Expression cond, Label label) {
            super(label);
            this.cond = cond;
        }

        @Override
        public void perform(ScopeExecution exec, TemplateContext context) {
            if (Operators.truthy(context.evaluate(cond)))
                super.perform(exec, context);
        }
    }

    static class StartIterationRange extends ScopeCommand {
        private final Expression from;
        private final Expression to;
        private final IfIterator iterator;

        StartIterationRange(Expression from, Expression to, IfIterator iterator) {
            this.from = from;
            this.to = to;
            this.iterator = iterator;
        }

        @Override
        public void perform(ScopeExecution scope, TemplateContext context) {
            JsonNode from = context.evaluate(this.from);
            JsonNode to = context.evaluate(this.to);
            if (from.isNumber() && to.isNumber()) {
                iterator.iterator(new RangeIterator(from.asInt(), to.asInt()));
            } else if (!from.isNumber()) {
                scope.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Range iteration 'from' cannot be " + from.type()));
            } else {
                scope.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Range iteration 'to' cannot be " + from.type()));
            }
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return true;
        }
    }

    static class StartIteration extends ScopeCommand {
        private final Expression iterable;
        private final IfIterator iterator;

        StartIteration(Expression iterable, IfIterator iterator) {
            this.iterable = iterable;
            this.iterator = iterator;
        }

        @Override
        public void perform(ScopeExecution scope, TemplateContext context) {
            JsonNode itr = context.evaluate(iterable);
            if (itr.isString()) {
                iterator.iterator(new StringIterator(itr.asString()));
            } else if (itr.isArray()) {
                iterator.iterator(itr.iterator());
            } else {
                scope.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Cannot iterate " + itr.type()));
            }
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return true;
        }
    }

    static class StartIterationObj extends ScopeCommand {
        private final Expression iterable;
        private final IfIteratorObj iterator;

        StartIterationObj(Expression iterable, IfIteratorObj iterator) {
            this.iterable = iterable;
            this.iterator = iterator;
        }

        @Override
        public void perform(ScopeExecution scope, TemplateContext context) {
            JsonNode itr = context.evaluate(iterable);
            if (itr.isObject()) {
                iterator.iterator(itr.entrySet().iterator());
            } else {
                scope.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Cannot object-iterate " + itr.type()));
            }
        }

        @Override
        public boolean isValidIn(ScopeType type) {
            return true;
        }
    }

    static class IfIterator extends Jump {
        private final String var;
        private Iterator<JsonNode> itr;

        IfIterator(String var, Label label) {
            super(label);
            this.var = var;
        }

        public void iterator(Iterator<JsonNode> itr) {
            this.itr = itr;
        }

        @Override
        public void perform(ScopeExecution exec, TemplateContext context) {
            if (itr.hasNext()) {
                context.varFnLayer().set(var, itr.next());
                super.perform(exec, context);
            }
        }
    }

    static class IfIteratorObj extends Jump {
        private final String kvar;
        private final String vvar;
        private Iterator<Map.Entry<String, JsonNode>> itr;

        IfIteratorObj(String kvar, String vvar, Label label) {
            super(label);
            this.kvar = kvar;
            this.vvar = vvar;
        }

        public void iterator(Iterator<Map.Entry<String, JsonNode>> itr) {
            this.itr = itr;
        }

        @Override
        public void perform(ScopeExecution exec, TemplateContext context) {
            if (itr.hasNext()) {
                Map.Entry<String, JsonNode> entry = itr.next();
                context.varFnLayer().set(kvar, JsonNode.string(entry.getKey()));
                context.varFnLayer().set(vvar, entry.getValue());
                super.perform(exec, context);
            }
        }
    }
}
