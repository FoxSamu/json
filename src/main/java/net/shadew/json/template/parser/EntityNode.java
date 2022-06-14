package net.shadew.json.template.parser;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import net.shadew.json.JsonNode;
import net.shadew.json.JsonType;
import net.shadew.json.template.Expression;
import net.shadew.json.template.FunctionDefinition;
import net.shadew.json.template.Instruction;
import net.shadew.json.template.Instructions;

import static net.shadew.json.template.Instruction.*;

public abstract class EntityNode extends ParserNode {
    @Override
    public EntityNode asEntity() {
        return this;
    }

    public abstract void compile(InstructionSink builder);

    public interface InstructionSink {
        InstructionSink add(Instruction insns);
        InstructionSink add(Instruction... insns);
        InstructionSink add(Collection<? extends Instruction> insns);
        InstructionSink defFunction(FunctionDefinition funcdef);
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
        protected List<ParserNode> childList() {
            return List.of(val);
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
            builder.add(new Result(val.compile()));
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
        protected List<ParserNode> childList() {
            return List.of(key, val);
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
            builder.add(new ResultWithKey(key.compile(), val.compile()));
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
        protected List<ParserNode> childList() {
            return List.of(expr);
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
                builder.add(new VoidEval(e));
        }
    }

    public static If ifBlock(ExpressionNode cond) {
        return new If(cond);
    }

    public static If ifBlock(ExpressionNode cond, ParserNode... entities) {
        return ifBlock(cond).append(entities);
    }

    public static If ifBlock() {
        return new If();
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
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(condition);
            children.addAll(entities);
            children.addAll(elseIfBlocks);
            if (elseBlock != null)
                children.add(elseBlock);
            return List.copyOf(children);
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
                builder.append(" ").append(elseIf.asString());
            if (elseBlock != null)
                builder.append(" ").append(elseBlock.asString());
            return builder.toString();
        }

        @Override
        public void compile(InstructionSink sink) {
            boolean singular = elseIfBlocks.isEmpty() && elseBlock == null;

            Label endOfEntity = new Label();

            if (singular) {
                // We can skip a bunch of instructions in this case
                sink.add(new UnlessJump(condition.compile(), endOfEntity));
                compileSimpleFramedBlock("if", sink);
            } else {
                Label endOfIf = new Label();
                sink.add(new UnlessJump(condition.compile(), endOfIf));
                compileSimpleFramedBlock("if", sink);
                sink.add(new Jump(endOfEntity));
                sink.add(endOfIf);

                for (ElseIf elseIf : elseIfBlocks) {
                    Label endOfElseIf = new Label();
                    sink.add(new UnlessJump(elseIf.condition.compile(), endOfElseIf));
                    elseIf.compileSimpleFramedBlock("else if", sink);
                    sink.add(new Jump(endOfEntity));
                    sink.add(endOfElseIf);
                }

                if (elseBlock != null) {
                    elseBlock.compileSimpleFramedBlock("else", sink);
                }
            }

            sink.add(endOfEntity);
        }
    }

    public static ElseIf elseIfBlock(ExpressionNode cond) {
        return new ElseIf(cond);
    }

    public static ElseIf elseIfBlock(ExpressionNode cond, ParserNode... entities) {
        return elseIfBlock(cond).append(entities);
    }

    public static ElseIf elseIfBlock() {
        return new ElseIf();
    }

    public static class ElseIf extends ParserNode implements EntityBlockBase<ElseIf> {
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

        @Override
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(condition);
            children.addAll(entities);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.BLOCK_ELSE_IF;
        }

        @Override
        public EntityNode asEntity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String asString() {
            return "else if " + condition.asString() + " { " + listString() + " }";
        }
    }

    public static Else elseBlock(ParserNode... entities) {
        return elseBlock().append(entities);
    }

    public static class Else extends ParserNode implements EntityBlockBase<Else> {
        public final List<EntityNode> entities = new ArrayList<>();

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        protected List<ParserNode> childList() {
            return List.copyOf(entities);
        }

        @Override
        public NodeType type() {
            return NodeType.BLOCK_ELSE;
        }

        @Override
        public EntityNode asEntity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String asString() {
            return "else { " + listString() + " }";
        }
    }

    public static ForIn forInBlock(String var, ExpressionNode iterate) {
        return new ForIn(var, iterate);
    }

    public static ForIn forInBlock(String var, ExpressionNode iterate, ParserNode... entities) {
        return forInBlock(var, iterate).append(entities);
    }

    public static ForIn forInBlock() {
        return new ForIn();
    }

    public static class ForIn extends EntityNode implements EntityBlockBase<ForIn> {
        public String var;
        public ExpressionNode iterate;
        public final List<EntityNode> entities = new ArrayList<>();

        public ForIn(String var, ExpressionNode iterate) {
            this.var = var;
            this.iterate = iterate;
        }

        public ForIn() {
        }

        public ForIn var(String var) {
            this.var = var;
            return this;
        }

        public ForIn in(ExpressionNode itr) {
            iterate = itr;
            return this;
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(iterate);
            children.addAll(entities);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_FOR_IN;
        }

        @Override
        public String asString() {
            return "for " + var + " in " + iterate.asString() + " { " + listString() + " }";
        }

        @Override
        public void compile(InstructionSink builder) {
            Label startOfFor = new Label();
            Label endOfFor = new Label();

            Label from = new Label();
            Label to = new Label();

            builder.add(new InitArrayItr(iterate.compile()));
            builder.add(new Jump(to));
            builder.add(startOfFor);
            builder.add(new PushFrame("for-in", from, to, endOfFor, to));
            builder.add(from);
            builder.add(new ItrGet(var));
            compileEntities(builder);
            builder.add(to);
            builder.add(new ItrJump(endOfFor));
            builder.add(new Jump(startOfFor));
            builder.add(endOfFor);
        }
    }

    public static ForInObj forInObjBlock(String kvar, String vvar, ExpressionNode iterate) {
        return new ForInObj(kvar, vvar, iterate);
    }

    public static ForInObj forInObjBlock(String kvar, String vvar, ExpressionNode iterate, ParserNode... entities) {
        return forInObjBlock(kvar, vvar, iterate).append(entities);
    }

    public static ForInObj forInObjBlock() {
        return new ForInObj();
    }

    public static class ForInObj extends EntityNode implements EntityBlockBase<ForInObj> {
        public String kvar;
        public String vvar;
        public ExpressionNode iterate;
        public final List<EntityNode> entities = new ArrayList<>();

        public ForInObj(String kvar, String vvar, ExpressionNode iterate) {
            this.kvar = kvar;
            this.vvar = vvar;
            this.iterate = iterate;
        }

        public ForInObj() {
        }

        public ForInObj kvar(String var) {
            kvar = var;
            return this;
        }

        public ForInObj vvar(String var) {
            vvar = var;
            return this;
        }

        public ForInObj in(ExpressionNode itr) {
            iterate = itr;
            return this;
        }

        @Override
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(iterate);
            children.addAll(entities);
            return List.copyOf(children);
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_FOR_IN;
        }

        @Override
        public String asString() {
            return "for " + kvar + ":" + vvar + " in " + iterate.asString() + " { " + listString() + " }";
        }

        @Override
        public void compile(InstructionSink builder) {
            Label startOfFor = new Label();
            Label endOfFor = new Label();

            Label from = new Label();
            Label to = new Label();

            builder.add(new InitObjectItr(iterate.compile()));
            builder.add(new Jump(to));
            builder.add(startOfFor);
            builder.add(new PushFrame("for-in-obj", from, to, endOfFor, to));
            builder.add(from);
            builder.add(new ItrGetKV(kvar, vvar));
            compileEntities(builder);
            builder.add(to);
            builder.add(new ItrJump(endOfFor));
            builder.add(new Jump(startOfFor));
            builder.add(endOfFor);
        }
    }

    public static ForFromTo forFromToBlock(String var, ExpressionNode from, ExpressionNode to) {
        return new ForFromTo(var, from, to);
    }

    public static ForFromTo forFromToBlock(String var, ExpressionNode from, ExpressionNode to, ParserNode... entities) {
        return forFromToBlock(var, from, to).append(entities);
    }

    public static ForFromTo forFromToBlock() {
        return new ForFromTo();
    }

    public static class ForFromTo extends EntityNode implements EntityBlockBase<ForFromTo> {
        public String var;
        public ExpressionNode from;
        public ExpressionNode to;
        public final List<EntityNode> entities = new ArrayList<>();

        public ForFromTo(String var, ExpressionNode from, ExpressionNode to) {
            this.var = var;
            this.from = from;
            this.to = to;
        }

        public ForFromTo() {
        }

        public ForFromTo var(String var) {
            this.var = var;
            return this;
        }

        public ForFromTo from(ExpressionNode itr) {
            from = itr;
            return this;
        }

        public ForFromTo to(ExpressionNode itr) {
            to = itr;
            return this;
        }

        @Override
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(from);
            children.add(to);
            children.addAll(entities);
            return List.copyOf(children);
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_FOR_FROM_TO;
        }

        @Override
        public String asString() {
            return "for " + var + " from " + from.asString() + " to " + to.asString() + " { " + listString() + " }";
        }

        @Override
        public void compile(InstructionSink builder) {
            Label startOfFor = new Label();
            Label endOfFor = new Label();

            Label from = new Label();
            Label to = new Label();

            builder.add(new InitRangeItr(this.from.compile(), this.to.compile()));
            builder.add(new Jump(to));
            builder.add(startOfFor);
            builder.add(new PushFrame("for-from-to", from, to, endOfFor, to));
            builder.add(from);
            builder.add(new ItrGet(var));
            compileEntities(builder);
            builder.add(to);
            builder.add(new ItrJump(endOfFor));
            builder.add(new Jump(startOfFor));
            builder.add(endOfFor);
        }
    }

    public static Break breakStatement(int depth) {
        return new Break(depth);
    }

    public static Break breakStatement() {
        return new Break();
    }

    public static class Break extends EntityNode {
        public int depth = 1;

        public Break(int depth) {
            this.depth = depth;
        }

        public Break() {
        }

        public Break depth(int depth) {
            this.depth = depth;
            return this;
        }

        @Override
        public void compile(InstructionSink builder) {
            builder.add(new BreakFrame(depth));
        }

        @Override
        protected List<ParserNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_BREAK;
        }

        @Override
        public String asString() {
            if (depth == 1) return "break";
            return "break " + depth;
        }
    }

    public static Continue continueStatement(int depth) {
        return new Continue(depth);
    }

    public static Continue continueStatement() {
        return new Continue();
    }

    public static class Continue extends EntityNode {
        public int depth = 1;

        public Continue(int depth) {
            this.depth = depth;
        }

        public Continue() {
        }

        public Continue depth(int depth) {
            this.depth = depth;
            return this;
        }

        @Override
        public void compile(InstructionSink builder) {
            builder.add(new ContinueFrame(depth));
        }

        @Override
        protected List<ParserNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_CONTINUE;
        }

        @Override
        public String asString() {
            if (depth == 1) return "continue";
            return "continue " + depth;
        }
    }

    public static Return returnStatement() {
        return new Return();
    }

    public static class Return extends EntityNode {
        @Override
        public void compile(InstructionSink builder) {
            builder.add(new Instruction.Return());
        }

        @Override
        protected List<ParserNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_RETURN;
        }

        @Override
        public String asString() {
            return "return";
        }
    }

    public static DefineExpressionFunction defExpr(String name, ExpressionNode expr, boolean vararg, String... params) {
        return new DefineExpressionFunction(name, expr, vararg).params(params);
    }

    public static DefineExpressionFunction defExpr(String name, ExpressionNode expr, String... params) {
        return new DefineExpressionFunction(name, expr, false).params(params);
    }

    public static DefineExpressionFunction defExpr(String name, ExpressionNode expr, boolean vararg) {
        return new DefineExpressionFunction(name, expr, vararg);
    }

    public static DefineExpressionFunction defExpr(String name, ExpressionNode expr) {
        return new DefineExpressionFunction(name, expr, false);
    }

    public static DefineExpressionFunction defExpr() {
        return new DefineExpressionFunction();
    }

    public static class DefineExpressionFunction extends EntityNode {
        public String name;
        public ExpressionNode expr;
        public boolean vararg;
        public final List<String> params = new ArrayList<>();

        public DefineExpressionFunction(String name, ExpressionNode expr, boolean vararg) {
            this.name = name;
            this.expr = expr;
            this.vararg = vararg;
        }

        public DefineExpressionFunction() {
        }

        public DefineExpressionFunction name(String name) {
            this.name = name;
            return this;
        }

        public DefineExpressionFunction param(String name) {
            params.add(name);
            return this;
        }

        public DefineExpressionFunction params(String... name) {
            params.addAll(Arrays.asList(name));
            return this;
        }

        public DefineExpressionFunction expr(ExpressionNode expr) {
            this.expr = expr;
            return this;
        }

        public DefineExpressionFunction vararg(boolean vararg) {
            this.vararg = vararg;
            return this;
        }

        @Override
        public void compile(InstructionSink builder) {
            FunctionDefinition.Call call = FunctionDefinition.callExpression(expr.compile());
            builder.defFunction(new FunctionDefinition(name, params, vararg, false, call));
        }

        @Override
        protected List<ParserNode> childList() {
            return List.of(expr);
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_DEF_EXPRESSION_FN;
        }

        @Override
        public String asString() {
            return "def " + name + "(" + String.join(", ", params) + (vararg ? "..." : "") + ") -> " + expr.asString();
        }
    }

    public static DefineSubtemplateFunction defSubtemplate(String name, boolean vararg, String... params) {
        return new DefineSubtemplateFunction(name, vararg).params(params);
    }

    public static DefineSubtemplateFunction defSubtemplate(String name, String... params) {
        return new DefineSubtemplateFunction(name, false).params(params);
    }

    public static DefineSubtemplateFunction defSubtemplate(String name, boolean vararg) {
        return new DefineSubtemplateFunction(name, vararg);
    }

    public static DefineSubtemplateFunction defSubtemplate(String name) {
        return new DefineSubtemplateFunction(name, false);
    }

    public static DefineSubtemplateFunction defSubtemplate() {
        return new DefineSubtemplateFunction();
    }

    public static class DefineSubtemplateFunction extends EntityNode implements EntityBlockBase<DefineSubtemplateFunction> {
        public String name;
        public boolean vararg;
        public final List<String> params = new ArrayList<>();
        public final List<EntityNode> entities = new ArrayList<>();

        public DefineSubtemplateFunction(String name, boolean vararg) {
            this.name = name;
            this.vararg = vararg;
        }

        public DefineSubtemplateFunction() {
        }

        public DefineSubtemplateFunction name(String name) {
            this.name = name;
            return this;
        }

        public DefineSubtemplateFunction param(String name) {
            params.add(name);
            return this;
        }

        public DefineSubtemplateFunction params(String... name) {
            params.addAll(Arrays.asList(name));
            return this;
        }

        public DefineSubtemplateFunction vararg(boolean vararg) {
            this.vararg = vararg;
            return this;
        }

        @Override
        protected List<ParserNode> childList() {
            return List.copyOf(entities);
        }

        @Override
        public void compile(InstructionSink builder) {
            FunctionDefinition.Call call = FunctionDefinition.callSubtemplate(compileToInstructions());
            builder.defFunction(new FunctionDefinition(name, params, vararg, true, call));
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_DEF_SUBTEMPLATE_FN;
        }

        @Override
        public String asString() {
            return "def " + name + "(" + String.join(", ", params) + (vararg ? "..." : "") + ") { " + listString() + " }";
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }
    }

    public static Switch switchBlock(ExpressionNode expr) {
        return new Switch(expr);
    }

    public static Switch switchBlock() {
        return new Switch();
    }

    public static class Switch extends EntityNode {
        public ExpressionNode value;
        public final List<Case> cases = new ArrayList<>();
        public Else elseBlock;

        public Switch(ExpressionNode value) {
            this.value = value;
        }

        public Switch() {
        }

        public Switch val(ExpressionNode val) {
            value = val;
            return this;
        }

        public Switch appendCase(Case c) {
            cases.add(c);
            return this;
        }

        public Switch prependCase(Case c) {
            cases.add(0, c);
            return this;
        }

        public Switch addElse(Else e) {
            elseBlock = e;
            return this;
        }

        @Override
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(value);
            children.addAll(cases);
            if (elseBlock != null)
                children.add(elseBlock);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.ENT_SWITCH;
        }

        @Override
        public void compile(InstructionSink sink) {
            boolean val = value != null;
            if (cases.isEmpty()) {
                if (val) {
                    Expression e = value.compile();
                    if (e.simplifyToLiteral() == null)
                        sink.add(new VoidEval(value.compile()));
                }

                if (elseBlock != null) {
                    elseBlock.compileSimpleFramedBlock("switch-else", sink);
                }
                return;
            }

            if (val) {
                sink.add(new InitSwitch(value.compile()));
            }

            // Collect compiled case and else blocks in here
            // We cannot compile blocks directly in the main sink because we need their labels before that
            Instructions.Sink blocks = Instructions.sink();

            Label endOfSwitch = new Label();
            for (Case c : cases) {
                Label label = addBlock("switch-case", c, blocks, endOfSwitch);
                sink.add(val ? new SwitchJump(c.cond.compile(), label) : new IfJump(c.cond.compile(), label));
            }
            if (elseBlock != null) {
                // No need to jump at end of else block since it's at the end of the switch already (hence null)
                Label label = addBlock("switch-else", elseBlock, blocks, null);
                sink.add(new Jump(label));
            } else {
                // When there is no else block we just exit the switch in the else-case
                sink.add(new Jump(endOfSwitch));
            }

            // Flush the compiled blocks through the main sink now
            blocks.flush(sink);

            sink.add(endOfSwitch);
        }

        private Label addBlock(String name, EntityBlockBase<?> block, InstructionSink sink, Label endOfSwitch) {
            Label start = new Label();
            sink.add(start);
            block.compileSimpleFramedBlock(name, sink);
            if (endOfSwitch != null)
                sink.add(new Jump(endOfSwitch));
            return start;
        }

        @Override
        public String asString() {
            return "switch" + (value != null ? " " + value.asString() : "") + " { "
                       + cases.stream().map(Case::asString).collect(Collectors.joining(", "))
                       + (elseBlock != null ? ", " + elseBlock.asString() : "") + " }";
        }
    }

    public static Case caseBlock(ExpressionNode cond) {
        return new Case(cond);
    }

    public static Case caseBlock(ExpressionNode cond, ParserNode... entities) {
        return caseBlock(cond).append(entities);
    }

    public static Case caseBlock() {
        return new Case();
    }

    public static class Case extends ParserNode implements EntityBlockBase<Case> {
        public ExpressionNode cond;
        public final List<EntityNode> entities = new ArrayList<>();

        public Case(ExpressionNode cond) {
            this.cond = cond;
        }

        public Case() {
        }

        public Case cond(ExpressionNode cond) {
            this.cond = cond;
            return this;
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        protected List<ParserNode> childList() {
            ArrayList<ParserNode> children = new ArrayList<>();
            children.add(cond);
            children.addAll(entities);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.BLOCK_CASE;
        }

        @Override
        public EntityNode asEntity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String asString() {
            return "case " + cond.asString() + " { " + listString() + " }";
        }
    }

    @SuppressWarnings("unchecked")
    public interface EntityBlockBase<T extends EntityBlockBase<T>> {
        List<EntityNode> entities();

        default Instructions.Sink compileSimpleFramedBlock(String name) {
            return Instructions.sink(s -> compileSimpleFramedBlock(name, s));
        }

        default void compileSimpleFramedBlock(String name, InstructionSink sink) {
            Label from = new Label();
            Label to = new Label();

            sink.add(new PushFrame(name, from, to, null, null));
            sink.add(from);
            compileEntities(sink);
            sink.add(to);
        }

        default Instructions compileToInstructions() {
            return Instructions.sink(this::compileEntities).build();
        }

        default void compileEntities(InstructionSink sink) {
            entities().forEach(e -> e.compile(sink));
        }

        default String listString() {
            return entities().stream().map(EntityNode::asString).collect(Collectors.joining(", "));
        }

        default T append(ParserNode node) {
            entities().add(node.asEntity());
            return (T) this;
        }

        default T append(ParserNode... nodes) {
            for (ParserNode node : nodes) {
                entities().add(node.asEntity());
            }
            return (T) this;
        }

        default T append(Collection<? extends ParserNode> nodes) {
            for (ParserNode node : nodes) {
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

        default T prepend(ParserNode node) {
            entities().add(0, node.asEntity());
            return (T) this;
        }

        default T prepend(ParserNode... nodes) {
            int i = 0;
            for (ParserNode node : nodes) {
                entities().add(i++, node.asEntity());
            }
            return (T) this;
        }

        default T prepend(Collection<? extends ParserNode> nodes) {
            int i = 0;
            for (ParserNode node : nodes) {
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
