package net.shadew.json.template.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.shadew.json.JsonNode;
import net.shadew.json.JsonType;
import net.shadew.json.template.Expression;
import net.shadew.json.template.Instruction;
import net.shadew.json.template.Instructions;

public abstract class EntityNode extends ParsedTemplateNode {
    @Override
    public EntityNode asEntity() {
        return this;
    }

    public abstract void compile(InstructionSink builder);

    public interface InstructionSink {
        InstructionSink add(Instruction insns);
        InstructionSink add(Instruction... insns);
        InstructionSink add(Collection<? extends Instruction> insns);
    }

    public static Value value(ExpressionNode val) {
        return new Value(val);
    }

    public static Value value() {
        return new Value();
    }

    public static class Value extends EntityNode {
        public ExpressionNode val;

        public Value(ExpressionNode val) {
            this.val = val;
        }

        public Value() {
        }

        public Value val(ExpressionNode node) {
            val = node;
            return this;
        }

        public Value literalVal(JsonNode node) {
            return val(ExpressionNode.literal(node));
        }

        public Value literalVal(Number node) {
            return val(ExpressionNode.literal(node));
        }

        public Value literalVal(Boolean node) {
            return val(ExpressionNode.literal(node));
        }

        public Value literalVal(String node) {
            return val(ExpressionNode.literal(node));
        }

        public Value nullVal() {
            return val(ExpressionNode.nullLiteral());
        }

        public Value variableVal(String varname) {
            return val(ExpressionNode.variable(varname));
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_VALUE;
        }

        @Override
        public String asString() {
            return val.asString();
        }

        @Override
        public void compile(InstructionSink builder) {
            builder.add(new Instruction.Result(val.compile()));
        }
    }

    public static KeyValue keyValue(ExpressionNode key, ExpressionNode val) {
        return new KeyValue(key, val);
    }

    public static KeyValue keyValue() {
        return new KeyValue();
    }

    public static class KeyValue extends EntityNode {
        public ExpressionNode key;
        public ExpressionNode val;

        public KeyValue(ExpressionNode key, ExpressionNode val) {
            this.key = key;
            this.val = val;
        }

        public KeyValue() {
        }

        public KeyValue key(ExpressionNode node) {
            key = node;
            return this;
        }

        public KeyValue literalKey(String literal) {
            return key(ExpressionNode.literal(literal));
        }

        public KeyValue variableKey(String varname) {
            return key(ExpressionNode.variable(varname));
        }

        public KeyValue val(ExpressionNode node) {
            val = node;
            return this;
        }

        public KeyValue literalVal(JsonNode node) {
            return val(ExpressionNode.literal(node));
        }

        public KeyValue literalVal(Number node) {
            return val(ExpressionNode.literal(node));
        }

        public KeyValue literalVal(Boolean node) {
            return val(ExpressionNode.literal(node));
        }

        public KeyValue literalVal(String node) {
            return val(ExpressionNode.literal(node));
        }

        public KeyValue nullVal() {
            return val(ExpressionNode.nullLiteral());
        }

        public KeyValue variableVal(String varname) {
            return val(ExpressionNode.variable(varname));
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_KEY_VALUE;
        }

        @Override
        public String asString() {
            String k = null;
            if (key.type() == NodeType.EXPR_LITERAL) {
                JsonNode lit = ((ExpressionNode.Literal) key).literal;
                if (lit.isString()) {
                    k = lit.toString();
                }
            }
            if (key.type() == NodeType.EXPR_INTERPOLATE_STRING) {
                k = key.asString();
            }
            if (key.type() == NodeType.EXPR_VARIABLE) {
                k = key.asString();
            }
            if (k == null)
                k = "(" + key.asString() + ")";
            return k + ": " + val.asString();
        }

        @Override
        public void compile(InstructionSink builder) {
            builder.add(new Instruction.ResultWithKey(key.compile(), val.compile()));
        }
    }

    public static VoidLine voidLine(ExpressionNode node) {
        return new VoidLine(node);
    }

    public static VoidLine voidLine() {
        return new VoidLine();
    }

    public static class VoidLine extends EntityNode {
        public ExpressionNode expr;

        public VoidLine(ExpressionNode expr) {
            this.expr = expr;
        }

        public VoidLine() {
        }

        public VoidLine expr(ExpressionNode expr) {
            this.expr = expr;
            return this;
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_VOID_LINE;
        }

        @Override
        public String asString() {
            return "@ " + expr.asString();
        }

        @Override
        public void compile(InstructionSink builder) {
            Expression e = expr.compile();
            if (e.simplifyToLiteral() == null)
                builder.add(new Instruction.VoidLine(e));
        }
    }

    public static If ifBlock(ExpressionNode cond) {
        return new If(cond);
    }

    public static If ifBlock(ExpressionNode cond, ParsedTemplateNode... entities) {
        return ifBlock(cond).append(entities);
    }

    public static If ifBlock() {
        return new If();
    }

    public static If.ElseIf elseIfBlock(ExpressionNode cond) {
        return new If.ElseIf(cond);
    }

    public static If.ElseIf elseIfBlock(ExpressionNode cond, ParsedTemplateNode... entities) {
        return elseIfBlock(cond).append(entities);
    }

    public static If.ElseIf elseIfBlock() {
        return new If.ElseIf();
    }

    public static If.Else elseBlock(ParsedTemplateNode... entities) {
        return elseBlock().append(entities);
    }

    public static If.Else elseBlock() {
        return new If.Else();
    }

    public static class If extends EntityNode implements EntityBlockBase<If> {
        public ExpressionNode condition;
        public final List<EntityNode> entities = new ArrayList<>();
        public final List<ElseIf> elseIfBlocks = new ArrayList<>();
        public Else elseBlock;

        public If(ExpressionNode condition) {
            this.condition = condition;
        }

        public If() {
        }

        public If cond(ExpressionNode cond) {
            condition = cond;
            return this;
        }

        public If addElseIf(ElseIf elseIf) {
            elseIfBlocks.add(elseIf);
            return this;
        }

        public If addElseIf(ExpressionNode condition, Consumer<? super ElseIf> c) {
            ElseIf block = new ElseIf(condition);
            c.accept(block);
            elseIfBlocks.add(block);
            return this;
        }

        public If addElse(Else elseBlock) {
            this.elseBlock = elseBlock;
            return this;
        }

        public If addElse(Consumer<? super Else> c) {
            this.elseBlock = new Else();
            c.accept(elseBlock);
            return this;
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_IF;
        }

        @Override
        public String asString() {
            StringBuilder builder = new StringBuilder();
            builder.append("if ").append(condition.asString()).append(" { ").append(listString()).append(" }");
            for (ElseIf elseIf : elseIfBlocks)
                builder.append(elseIf.asString());
            if (elseBlock != null)
                builder.append(elseBlock.asString());
            return builder.toString();
        }

        @Override
        public void compile(InstructionSink sink) {
            boolean singular = elseIfBlocks.isEmpty() && elseBlock == null;

            Instruction.Label endOfEntity = new Instruction.Label();

            if (singular) {
                // We can skip a bunch of instructions in this case
                sink.add(new Instruction.If(condition.compile(), endOfEntity));
                compileSimpleFramedBlock("if", sink);
            } else {
                Instruction.Label endOfIf = new Instruction.Label();
                sink.add(new Instruction.If(condition.compile(), endOfIf));
                compileSimpleFramedBlock("if", sink);
                sink.add(new Instruction.Jump(endOfEntity));
                sink.add(endOfIf);

                for (ElseIf elseIf : elseIfBlocks) {
                    Instruction.Label endOfElseIf = new Instruction.Label();
                    sink.add(new Instruction.If(elseIf.condition.compile(), endOfElseIf));
                    elseIf.compileSimpleFramedBlock("else if", sink);
                    sink.add(new Instruction.Jump(endOfEntity));
                    sink.add(endOfElseIf);
                }

                if (elseBlock != null) {
                    elseBlock.compileSimpleFramedBlock("else", sink);
                }
            }

            sink.add(endOfEntity);
        }

        public static class ElseIf implements EntityBlockBase<ElseIf> {
            public ExpressionNode condition;
            public final List<EntityNode> entities = new ArrayList<>();

            public ElseIf(ExpressionNode condition) {
                this.condition = condition;
            }

            public ElseIf() {
            }

            public ElseIf cond(ExpressionNode cond) {
                condition = cond;
                return this;
            }

            @Override
            public List<EntityNode> entities() {
                return entities;
            }

            public String asString() {
                return " else if " + condition.asString() + " { " + listString() + " }";
            }
        }

        public static class Else implements EntityBlockBase<Else> {
            public final List<EntityNode> entities = new ArrayList<>();

            @Override
            public List<EntityNode> entities() {
                return entities;
            }

            public String asString() {
                return " else { " + listString() + " }";
            }
        }
    }

    @SuppressWarnings("unchecked")
    public interface EntityBlockBase<T extends EntityBlockBase<T>> {
        List<EntityNode> entities();

        default Instructions.Sink compileSimpleFramedBlock(String name) {
            return Instructions.sink(s -> compileSimpleFramedBlock(name, s));
        }

        default void compileSimpleFramedBlock(String name, InstructionSink sink) {
            Instruction.Label from = new Instruction.Label();
            Instruction.Label to = new Instruction.Label();

            sink.add(new Instruction.PushFrame(name, from, to, null, null));
            sink.add(from);
            compileEntities(sink);
            sink.add(to);
        }

        default void compileEntities(InstructionSink sink) {
            entities().forEach(e -> e.compile(sink));
        }

        default String listString() {
            return entities().stream().map(EntityNode::asString).collect(Collectors.joining(", "));
        }

        default T append(ParsedTemplateNode node) {
            entities().add(node.asEntity());
            return (T) this;
        }

        default T append(ParsedTemplateNode... nodes) {
            for (ParsedTemplateNode node : nodes) {
                entities().add(node.asEntity());
            }
            return (T) this;
        }

        default T append(Collection<? extends ParsedTemplateNode> nodes) {
            for (ParsedTemplateNode node : nodes) {
                entities().add(node.asEntity());
            }
            return (T) this;
        }

        default T appendFrom(EntityBlockBase<?> list) {
            return append(list.entities());
        }

        default T appendFrom(JsonNode collection) {
            collection.require(JsonType.ARRAY, JsonType.OBJECT);
            if (collection.isArray()) {
                for (JsonNode node : collection) {
                    append(value(ExpressionNode.literal(node)));
                }
            } else {
                for (Map.Entry<String, JsonNode> node : collection.entrySet()) {
                    append(keyValue(ExpressionNode.literal(node.getKey()), ExpressionNode.literal(node.getValue())));
                }
            }
            return (T) this;
        }

        default T append(ExpressionNode key, ExpressionNode value) {
            append(value.withKey(key));
            return (T) this;
        }

        default T append(String key, ExpressionNode value) {
            append(value.withKey(ExpressionNode.literal(key)));
            return (T) this;
        }

        default T prepend(ParsedTemplateNode node) {
            entities().add(0, node.asEntity());
            return (T) this;
        }

        default T prepend(ParsedTemplateNode... nodes) {
            int i = 0;
            for (ParsedTemplateNode node : nodes) {
                entities().add(i++, node.asEntity());
            }
            return (T) this;
        }

        default T prepend(Collection<? extends ParsedTemplateNode> nodes) {
            int i = 0;
            for (ParsedTemplateNode node : nodes) {
                entities().add(i++, node.asEntity());
            }
            return (T) this;
        }

        default T prependFrom(EntityBlockBase<?> list) {
            return prepend(list.entities());
        }

        default T prependFrom(JsonNode collection) {
            collection.require(JsonType.ARRAY, JsonType.OBJECT);
            int i = 0;
            if (collection.isArray()) {
                for (JsonNode node : collection) {
                    entities().add(i++, value(ExpressionNode.literal(node)));
                }
            } else {
                for (Map.Entry<String, JsonNode> node : collection.entrySet()) {
                    entities().add(i++, keyValue(ExpressionNode.literal(node.getKey()), ExpressionNode.literal(node.getValue())));
                }
            }
            return (T) this;
        }

        default T prepend(ExpressionNode key, ExpressionNode value) {
            prepend(value.withKey(key));
            return (T) this;
        }

        default T prepend(String key, ExpressionNode value) {
            prepend(value.withKey(ExpressionNode.literal(key)));
            return (T) this;
        }
    }
}
