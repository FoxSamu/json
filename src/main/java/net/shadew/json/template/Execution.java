package net.shadew.json.template;

import java.util.ArrayList;
import java.util.List;

import net.shadew.json.JsonNode;

class Execution {
    protected final TemplateContext context;
    protected final ExecutionType type;
    protected final Instructions insns;
    protected int pos;
    protected JsonNode value;
    protected boolean terminate;
    protected final List<Frame> frames = new ArrayList<>();

    Execution(TemplateContext context, ExecutionType type, Instructions insns) {
        this.context = context;
        this.type = type;
        this.insns = insns;
        reset();
    }

    public void reset() {
        pos = 0;
        value = null;
        terminate = false;
        if (type == ExecutionType.ARRAY)
            value = JsonNode.array();
        if (type == ExecutionType.OBJECT)
            value = JsonNode.object();
    }

    public JsonNode run() {
        while (!terminate) {
            Instruction insn = insns.at(pos);
            insn(insn);
            insn.perform(this, context);
            pos++;

            while (!frames.isEmpty() && !peekFrame().isInRange(pos))
                popFrame();

            if (pos >= insns.length())
                terminate = true;
        }
        return value == null ? JsonNode.object() : value;
    }

    protected void insn(Instruction insn) {
    }

    public TemplateContext context() {
        return context;
    }

    public ExecutionType scope() {
        return type;
    }

    public int pos() {
        return pos;
    }

    public void branch(int pos) {
        this.pos = pos;
    }

    public void branch(Instruction.Label label) {
        branch(label, 0);
    }

    public void branch(Instruction.Label label, int offset) {
        if (label.insns() != insns)
            throw new IllegalArgumentException("Invalid label");
        branch(label.pos() + offset);
    }

    public void produce(JsonNode result) {
        if (type == ExecutionType.ROOT) {
            value = result;
            terminate = true;
        } else if (type == ExecutionType.ARRAY) {
            value.add(result);
        } else {
            throw new UnsupportedOperationException("Cannot produce value for OBJECT, use produce(String, JsonNode)");
        }
    }

    public void produce(String key, JsonNode result) {
        if (type == ExecutionType.OBJECT) {
            value.set(key, result);
        } else {
            throw new UnsupportedOperationException("Cannot produce key-value for ARRAY or ROOT, use produce(JsonNode)");
        }
    }

    public JsonNode value() {
        return value;
    }

    public void terminate() {
        terminate = true;
    }

    public boolean terminated() {
        return terminate;
    }

    public void exception(JsonNode exception) {
        value = exception;
        terminate = true;
    }

    protected void enterFrame(Frame frame) {

    }

    protected void exitFrame(Frame frame) {

    }

    public void pushFrame(Frame frame) {
        enterFrame(frame);
        frames.add(frame);
        context.pushVflWithSameScope(frame.name);
    }

    private Frame peekFrame() {
        return frames.get(frames.size() - 1);
    }

    private Frame popFrame() {
        Frame frame = frames.remove(frames.size() - 1);
        context.popVfl();
        exitFrame(frame);
        return frame;
    }

    public void breakFrame() {
        int breakable = findNearestBreakableFrame();
        while (frames.size() > breakable + 1) {
            popFrame();
        }

        if (breakable < 0) terminate();
        else branch(popFrame().breakPos);
    }

    public void continueFrame() {
        int breakable = findNearestContinuableFrame();
        while (frames.size() > breakable + 1) {
            popFrame();
        }

        if (breakable < 0) exception(context.exception(ExceptionType.EXECUTION_EXCEPTION, "Cannot continue here"));
        else branch(popFrame().breakPos);
    }

    private int findNearestBreakableFrame() {
        for (int i = frames.size() - 1; i >= 0; i--) {
            if (frames.get(i).canBreak())
                return i;
        }
        return -1;
    }

    private int findNearestContinuableFrame() {
        for (int i = frames.size() - 1; i >= 0; i--) {
            if (frames.get(i).canContinue())
                return i;
        }
        return -1;
    }

    public static class Frame {
        public final String name;
        public final Instruction.Label from;
        public final Instruction.Label to;
        public final Instruction.Label breakPos;
        public final Instruction.Label continuePos;

        public Frame(String name, Instruction.Label from, Instruction.Label to, Instruction.Label breakPos, Instruction.Label continuePos) {
            this.name = name;
            this.from = from;
            this.to = to;
            this.breakPos = breakPos;
            this.continuePos = continuePos;
        }

        public boolean canBreak() {
            return breakPos != null;
        }

        public boolean canContinue() {
            return continuePos != null;
        }

        public boolean isInRange(int pos) {
            return pos >= from.pos() && pos < to.pos();
        }
    }
}
