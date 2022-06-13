package net.shadew.json.template;

import java.util.*;
import java.util.function.Consumer;

import net.shadew.json.template.parser.EntityNode;

public class Instructions implements Iterable<Instruction> {
    private final Instruction[] instructions;
    private final Map<String, Function> definedFunctions;

    private Instructions(Sink sink) {
        instructions = sink.instructions.toArray(Instruction[]::new);

        Map<String, Function> map = new HashMap<>();
        for (Map.Entry<String, List<FunctionDefinition>> defs : sink.functions.entrySet()) {
            String name = defs.getKey();
            List<FunctionDefinition> overloads = defs.getValue();
            map.put(name, FunctionDefinition.createOverloadedFunction(overloads));
        }
        definedFunctions = Map.copyOf(map);

        int pos = 0;
        for (Instruction cmd : instructions) {
            if (cmd instanceof Instruction.Label) {
                ((Instruction.Label) cmd).update(this, pos);
            }
            pos++;
        }
    }

    public Map<String, Function> getDefinedFunctions() {
        return definedFunctions;
    }

    public Instruction at(int pos) {
        return instructions[pos];
    }

    public int length() {
        return instructions.length;
    }

    public String writeInstructionDebug() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Instruction insn : instructions) {
            builder.append(String.format("% 4d    ", i++));
            builder.append(insn.writeDebug());
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    @Override
    public Iterator<Instruction> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < instructions.length;
            }

            @Override
            public Instruction next() {
                if (i >= instructions.length)
                    throw new NoSuchElementException();
                return instructions[i++];
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Instruction> action) {
        Objects.requireNonNull(action);
        for (Instruction i : instructions)
            action.accept(i);
    }

    public static Sink sink() {
        return new Sink();
    }

    public static Sink sink(Consumer<? super Sink> consumer) {
        Sink sink = new Sink();
        consumer.accept(sink);
        return sink;
    }

    public static class Sink implements EntityNode.InstructionSink {
        private final List<Instruction> instructions = new ArrayList<>();
        private final Map<String, List<FunctionDefinition>> functions = new HashMap<>();

        @Override
        public Sink add(Instruction insns) {
            instructions.add(insns);
            return this;
        }

        @Override
        public Sink add(Instruction... insns) {
            instructions.addAll(Arrays.asList(insns));
            return this;
        }

        @Override
        public Sink add(Collection<? extends Instruction> insns) {
            instructions.addAll(insns);
            return this;
        }

        @Override
        public EntityNode.InstructionSink defFunction(FunctionDefinition funcdef) {
            functions.computeIfAbsent(funcdef.name(), k -> new ArrayList<>()).add(funcdef);
            return null;
        }

        public Instructions build() {
            return new Instructions(this);
        }

        public void flush(EntityNode.InstructionSink sink) {
            sink.add(instructions);
        }
    }
}
