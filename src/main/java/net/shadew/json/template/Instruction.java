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

    public static class VoidEval extends Instruction {
        private final Expression expr;

        public VoidEval(Expression expr) {
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
            return "VOID_EVAL " + expr.writeDebug();
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

        public PushFrame(String name, Label from, Label to, Label breakPos, Label continuePos) {
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
            return "FRAME '" + frame.name + "' from @" + frame.from.pos + " to @" + frame.to.pos
                       + (frame.canBreak() ? " break at @" + frame.breakPos.pos : "")
                       + (frame.canContinue() ? " continue at @" + frame.continuePos.pos : "");
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

    public static class IfJump extends Jump {
        private final Expression cond;

        public IfJump(Expression cond, Label label) {
            super(label);
            this.cond = cond;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (Operators.truthy(context.evaluate(cond)))
                super.perform(exec, context);
        }

        @Override
        public String writeDebug() {
            return "IF_JUMP to @" + label.pos + ", " + cond.writeDebug();
        }
    }

    public static class SwitchJump extends Jump {
        private final Expression cond;

        public SwitchJump(Expression cond, Label label) {
            super(label);
            this.cond = cond;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (context.vfl().switching().equals(context.evaluate(cond)))
                super.perform(exec, context);
        }

        @Override
        public String writeDebug() {
            return "SWITCH_JUMP to @" + label.pos + ", " + cond.writeDebug();
        }
    }

    public static class InitSwitch extends Instruction {
        private final Expression val;

        public InitSwitch(Expression val) {
            this.val = val;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            context.vfl().switchOver(context.evaluate(val));
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "INIT_SWITCH " + val.writeDebug();
        }
    }

    public static class UnlessJump extends Jump {
        private final Expression cond;

        public UnlessJump(Expression cond, Label label) {
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
            return "UNLESS_JUMP to @" + label.pos + ", " + cond.writeDebug();
        }
    }

    public static class InitRangeItr extends Instruction {
        private final Expression from;
        private final Expression to;

        public InitRangeItr(Expression from, Expression to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            JsonNode from = context.evaluate(this.from);
            JsonNode to = context.evaluate(this.to);
            if (from.isNumber() && to.isNumber()) {
                context.vfl().iterate(createIterator(from.asInt(), to.asInt()));
            } else if (!from.isNumber()) {
                exec.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Range iteration 'from' cannot be " + from.type()));
            } else {
                exec.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Range iteration 'to' cannot be " + to.type()));
            }
        }

        private static Iterator<JsonNode> createIterator(int from, int to) {
            if (from > to) {
                return new DownRangeIterator(from, to);
            } else if (from < to) {
                return new UpRangeIterator(from, to);
            } else {
                return EmptyIterator.INSTANCE;
            }
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "INIT_RANGE_ITR " + from.writeDebug() + " .. " + to.writeDebug();
        }
    }

    public static class InitArrayItr extends Instruction {
        private final Expression iterable;

        public InitArrayItr(Expression iterable) {
            this.iterable = iterable;
        }

        @Override
        public void perform(Execution scope, TemplateContext context) {
            JsonNode itr = context.evaluate(iterable);
            if (itr.isString()) {
                context.vfl().iterate(createIterator(itr.asString()));
            } else if (itr.isArray()) {
                context.vfl().iterate(createIterator(itr));
            } else {
                scope.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Cannot iterate " + itr.type()));
            }
        }

        private Iterator<JsonNode> createIterator(String str) {
            if (str.isEmpty())
                return EmptyIterator.INSTANCE;
            else
                return new StringIterator(str);
        }

        private Iterator<JsonNode> createIterator(JsonNode arr) {
            if (arr.empty())
                return EmptyIterator.INSTANCE;
            else
                return arr.iterator();
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "INIT_ARRAY_ITR " + iterable.writeDebug();
        }
    }

    public static class InitObjectItr extends Instruction {
        private final Expression iterable;

        public InitObjectItr(Expression iterable) {
            this.iterable = iterable;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            JsonNode itr = context.evaluate(iterable);
            if (itr.isObject()) {
                context.vfl().iterate(itr.entrySet().iterator());
            } else {
                exec.exception(context.exception(ExceptionType.INCORRECT_TYPES, "Cannot iterate key-value pairs of " + itr.type()));
            }
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "INIT_OBJECT_ITR " + iterable.writeDebug();
        }
    }

    public static class ItrJump extends Jump {
        public ItrJump(Label label) {
            super(label);
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            Vfl vfl = context.vfl();
            Iterator<JsonNode> itr = vfl.iterator();
            if (!itr.hasNext()) {
                super.perform(exec, context);
            }
        }

        @Override
        public String writeDebug() {
            return "ITR_JUMP to @" + label.pos;
        }
    }

    public static class ItrGet extends Instruction {
        private final String var;

        public ItrGet(String var) {
            this.var = var;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            Vfl vfl = context.vfl();
            Iterator<JsonNode> itr = vfl.iterator();
            vfl.set(var, itr.next());
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "ITR " + var;
        }
    }

    public static class ItrGetKV extends Instruction {
        private final String kvar;
        private final String vvar;

        public ItrGetKV(String kvar, String vvar) {
            this.kvar = kvar;
            this.vvar = vvar;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            Vfl vfl = context.vfl();
            Iterator<Map.Entry<String, JsonNode>> itr = vfl.iterator();
            Map.Entry<String, JsonNode> entry = itr.next();
            vfl.set(kvar, JsonNode.string(entry.getKey()));
            vfl.set(vvar, entry.getValue());
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "ITR_KV " + kvar + ":" + vvar;
        }
    }

    public static class BreakFrame extends Instruction {
        private final int depth;

        public BreakFrame(int depth) {
            this.depth = depth;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (depth == 1)
                exec.breakFrame();
            else
                exec.breakFrames(depth);
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "BREAK_FRAME " + depth;
        }
    }

    public static class ContinueFrame extends Instruction {
        private final int depth;

        public ContinueFrame(int depth) {
            this.depth = depth;
        }

        @Override
        public void perform(Execution exec, TemplateContext context) {
            if (depth == 1)
                exec.continueFrame();
            else
                exec.continueFrames(depth);
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "CONTINUE_FRAME " + depth;
        }
    }

    public static class Return extends Instruction {
        @Override
        public void perform(Execution exec, TemplateContext context) {
            exec.terminate();
        }

        @Override
        public boolean isValidIn(ExecutionType type) {
            return true;
        }

        @Override
        public String writeDebug() {
            return "RETURN";
        }
    }
}
