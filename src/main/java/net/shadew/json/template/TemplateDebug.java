package net.shadew.json.template;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TemplateDebug {
    public static boolean debug;
    public static Consumer<Vfl> pushVfl = vfl -> { };
    public static Consumer<Vfl> popVfl = vfl -> { };
    public static Consumer<Execution> startExec = exec -> { };
    public static Consumer<Execution> endExec = exec -> { };
    public static BiConsumer<Execution, Execution.Frame> enterFrame = (exec, frame) -> { };
    public static BiConsumer<Execution, Execution.Frame> exitFrame = (exec, frame) -> { };
    public static BiConsumer<Execution, Instruction> instruction = (exec, insn) -> { };
    public static BiConsumer<Execution, Integer> branch = (exec, pos) -> { };
    public static long sleepParser;
}
