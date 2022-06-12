package net.shadew.json.template;

import java.util.Iterator;
import java.util.Map;

import net.shadew.json.JsonNode;

public abstract class Instruction {
    public abstract void perform(Execution scope, TemplateContext context);
    public abstract boolean isValidIn(ExecutionType type);
    public abstract String writeDebug();

    public static class Result extends Instruction {
        private final Expression expr;

        public Result(Expression expr) {
            this.expr = expr;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            exec.produce(context.evaluate(expr));
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return type != ExecutionType.OBJECT;
        }

        @Override
        public String writeDebug() {
            return "RESULT " + expr.writeDebug();
        }
    }

    public static class ResultWithKey extends Instruction {
        private final Expression key;
        private final Expression expr;

        public ResultWithKey(Expression key, Expression expr) {
            this.key = key;
            this.expr = expr;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            JsonNode keyN = context.evaluate(key);
            JsonNode node = context.evaluate(expr);
            exec.produce(Operators.stringify(keyN), node);
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return type == ExecutionType.OBJECT;
        }

        @Override
        public String writeDebug() {
            return "RESULT_KEY " + key.writeDebug() + " = " + expr.writeDebug();
        }
    }

    public static class VoidLine extends Instruction {
        private final Expression expr;

        public VoidLine(Expression expr) {
            this.expr = expr;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            context.evaluate(expr);
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "VOID_LINE " + expr.writeDebug();
        }
    }

    public static class Label extends Instruction {
        private Instructions insns;
        private int pos = -1;

        @Override
        public void perform(Execution exec, TemplateContext context) {
        }

        public void update(Instructions exec, int pos) {
            this.insns = exec;
            this.pos = pos;
        }

        public Instructions insns() {
            return insns;
        }

        public int pos() {
            return pos;
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "LABEL @" + pos;
        }
    }

    public static class PushFrame extends Instruction {
        private final Execution.Frame frame;

        public PushFrame(Execution.Frame frame) {
            this.frame = frame;
        }

        public PushFrame(String name, Instruction.Label from, Instruction.Label to, Instruction.Label breakPos, Instruction.Label continuePos) {
            this(new Execution.Frame(name, from, to, breakPos, continuePos));
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            exec.pushFrame(frame);
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "FRAME '" + frame.name + "' from @" + frame.from.pos + " to @" + frame.to.pos;
        }
    }

    public static class Jump extends Instruction {
        protected final Label label;

        public Jump(Label label) {
            this.label = label;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            exec.branch(label, 0);
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "JUMP to @" + label.pos;
        }
    }

    public static class If extends Jump {
        private final Expression cond;

        public If(Expression cond, Label label) {
            super(label);
            this.cond = cond;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (!Operators.truthy(context.evaluate(cond)))
                super.perform(exec, context);
        }

        @Override
        public String writeDebug() {
            return "IF to @" + label.pos + ", " + cond.writeDebug();
        }
    }

    public static class StartIterationRange extends Instruction {
        private final Expression from;
        private final Expression to;
        private final IfIterator iterator;

        public StartIterationRange(Expression from, Expression to, IfIterator iterator) {
            this.from = from;
            this.to = to;
            this.iterator = iterator;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            JsonNode from = context.evaluate(this.from);
            JsonNode to = context.evaluate(this.to);
            if (from.isNumber() && to.isNumber()) {
                iterator.iterator(new RangeIterator(from.asInt(), to.asInt()));
            } else if (!from.isNumber()) {
                exec.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Range iteration 'from' cannot be " + from.type()));
            } else {
                exec.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Range iteration 'to' cannot be " + from.type()));
            }
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "START_ITR_RANGE " + from.writeDebug() + " .. " + to.writeDebug();
        }
    }

    public static class StartIteration extends Instruction {
        private final Expression iterable;
        private final IfIterator iterator;

        public StartIteration(Expression iterable, IfIterator iterator) {
            this.iterable = iterable;
            this.iterator = iterator;
        }

        @Override
        public void perform(Execution scope, TemplateContext context) {
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
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "START_ITR " + iterable.writeDebug();
        }
    }

    public static class StartIterationObj extends Instruction {
        private final Expression iterable;
        private final IfIteratorObj iterator;

        public StartIterationObj(Expression iterable, IfIteratorObj iterator) {
            this.iterable = iterable;
            this.iterator = iterator;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            JsonNode itr = context.evaluate(iterable);
            if (itr.isObject()) {
                iterator.iterator(itr.entrySet().iterator());
            } else {
                exec.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Cannot object-iterate " + itr.type()));
            }
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "START_ITR_OBJ " + iterable.writeDebug();
        }
    }

    public static class IfIterator extends Jump {
        private final String var;
        private Iterator<JsonNode> itr;

        public IfIterator(String var, Label label) {
            super(label);
            this.var = var;
        }

        public void iterator(Iterator<JsonNode> itr) {
            this.itr = itr;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (itr.hasNext()) {
                context.vfl().set(var, itr.next());
                super.perform(exec, context);
            }
        }

        @Override
        public String writeDebug() {
            return "IF_ITR to @" + label.pos + " for " + var;
        }
    }

    public static class IfIteratorObj extends Jump {
        private final String kvar;
        private final String vvar;
        private Iterator<Map.Entry<String, JsonNode>> itr;

        public IfIteratorObj(String kvar, String vvar, Label label) {
            super(label);
            this.kvar = kvar;
            this.vvar = vvar;
        }

        public void iterator(Iterator<Map.Entry<String, JsonNode>> itr) {
            this.itr = itr;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (itr.hasNext()) {
                Map.Entry<String, JsonNode> entry = itr.next();
                context.vfl().set(kvar, JsonNode.string(entry.getKey()));
                context.vfl().set(vvar, entry.getValue());
                super.perform(exec, context);
            }
        }

        @Override
        public String writeDebug() {
            return "IF_ITR_OBJ to @" + label.pos + " for " + kvar + ":" + vvar;
        }
    }
}
