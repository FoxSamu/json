package net.shadew.json.template;

import java.util.stream.Collectors;

import net.shadew.json.JsonNode;

class ExecutionWithDebug extends Execution {
    ExecutionWithDebug(TemplateContext context, ExecutionType type, Instructions insns) {
        super(context, type, insns);
    }

    @Override
    public JsonNode run() {
        try {
            TemplateDebug.startExec.accept(this);
            JsonNode res = super.run();
            TemplateDebug.endExec.accept(this);
            return res;
        } catch (Exception exc) {
            String msg = "Failed to execute instructions at @" + pos + "." + System.lineSeparator();
            msg += "Problem: " + exc.getLocalizedMessage() + System.lineSeparator();
            msg += "Instructions are:" + System.lineSeparator();
            msg += insns.writeInstructionDebug() + System.lineSeparator();
            msg += "Frames are:" + frames.stream().map(
                frame -> " - '" + frame.name + "' from @" + frame.from.pos() + " to @" + frame.to.pos() + System.lineSeparator()
            ).collect(Collectors.joining(""));

            throw new RuntimeException(msg, exc);
        }
    }

    @Override
    public void branch(int pos) {
        super.branch(pos);
        TemplateDebug.branch.accept(this, pos);
    }

    @Override
    protected void insn(Instruction insn) {
        TemplateDebug.instruction.accept(this, insn);
    }

    @Override
    protected void enterFrame(Frame frame) {
        TemplateDebug.enterFrame.accept(this, frame);
    }

    @Override
    protected void exitFrame(Frame frame) {
        TemplateDebug.exitFrame.accept(this, frame);
    }
}
