package net.shadew.json.template.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.json.JsonNode;
import net.shadew.json.JsonType;
import net.shadew.json.template.*;

public abstract class ExpressionNode extends ParsedTemplateNode {
    @Override
    public EntityNode asEntity() {
        return asValue();
    }

    public abstract Expression compile();

    public static abstract class CanAssign extends ExpressionNode {
        public abstract Assignable compileAssignable();

        public Assign asAssign() {
            return assign().left(this);
        }

        public Assign assign(ExpressionNode right, AssignType type) {
            return new Assign(this, right, type);
        }

        public Assign assign(ExpressionNode right) {
            return assign(this, right, AssignType.SET);
        }

        public Assign assignAdd(ExpressionNode right) {
            return assign(this, right, AssignType.ADD);
        }

        public Assign assignSub(ExpressionNode right) {
            return assign(this, right, AssignType.SUB);
        }

        public Assign assignMul(ExpressionNode right) {
            return assign(this, right, AssignType.MUL);
        }

        public Assign assignDiv(ExpressionNode right) {
            return assign(this, right, AssignType.DIV);
        }

        public Assign assignMod(ExpressionNode right) {
            return assign(this, right, AssignType.MOD);
        }

        public Assign assignBAnd(ExpressionNode right) {
            return assign(this, right, AssignType.BIT_AND);
        }

        public Assign assignBOr(ExpressionNode right) {
            return assign(this, right, AssignType.BIT_OR);
        }

        public Assign assignBXor(ExpressionNode right) {
            return assign(this, right, AssignType.BIT_XOR);
        }

        public Assign assignLsh(ExpressionNode right) {
            return assign(this, right, AssignType.BIT_LSH);
        }

        public Assign assignRsh(ExpressionNode right) {
            return assign(this, right, AssignType.BIT_RSH);
        }

        public Assign assignRrsh(ExpressionNode right) {
            return assign(this, right, AssignType.BIT_RRSH);
        }

        public Incr incr(boolean postfix, boolean decr) {
            return incr(this, postfix, decr);
        }

        public Incr incr(boolean postfix) {
            return incr(this, postfix);
        }

        public Incr decr(boolean postfix) {
            return decr(this, postfix);
        }

        public Incr preIncr() {
            return preIncr(this);
        }

        public Incr preDecr() {
            return preDecr(this);
        }

        public Incr postIncr() {
            return postIncr(this);
        }

        public Incr postDecr() {
            return postDecr(this);
        }
    }

    public EntityNode.Value asValue() {
        return EntityNode.value(this);
    }

    public EntityNode.KeyValue asKey() {
        return EntityNode.keyValue().key(this);
    }

    public EntityNode.KeyValue withKey() {
        return EntityNode.keyValue().val(this);
    }

    public EntityNode.KeyValue withKey(ExpressionNode key) {
        return EntityNode.keyValue(key, this);
    }

    public EntityNode.KeyValue asKey(ExpressionNode value) {
        return EntityNode.keyValue(this, value);
    }

    public EntityNode.VoidLine asVoidLine() {
        return EntityNode.voidLine(this);
    }

    public EntityNode.If asIf() {
        return EntityNode.ifBlock(this);
    }

    public EntityNode.If.ElseIf asElseIf() {
        return EntityNode.elseIfBlock(this);
    }

    public static Literal literal(JsonNode literal) {
        return new Literal(JsonNode.orNull(literal));
    }

    public static Literal literal(Number literal) {
        return new Literal(JsonNode.number(literal));
    }

    public static Literal literal(String literal) {
        return new Literal(JsonNode.string(literal));
    }

    public static Literal literal(Boolean literal) {
        return new Literal(JsonNode.bool(literal));
    }

    public static Literal nullLiteral() {
        return new Literal(JsonNode.NULL);
    }

    public static class Literal extends ExpressionNode {
        public JsonNode literal;

        public Literal(JsonNode literal) {
            this.literal = literal;
        }

        public Literal() {
        }

        public Literal val(JsonNode node) {
            literal = JsonNode.orNull(node);
            return this;
        }

        public Literal val(String node) {
            literal = JsonNode.string(node);
            return this;
        }

        public Literal val(Number node) {
            literal = JsonNode.number(node);
            return this;
        }

        public Literal val(Boolean node) {
            literal = JsonNode.bool(node);
            return this;
        }

        public Literal nullVal() {
            literal = JsonNode.NULL;
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_LITERAL;
        }

        @Override
        public String asString() {
            return literal.toString();
        }

        @Override
        public Expression compile() {
            return new Expression.Literal(literal);
        }
    }

    public static Underscore underscore() {
        return new Underscore();
    }

    public static class Underscore extends ExpressionNode {
        @Override
        public Expression compile() {
            return Expression.UNDERSCORE;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_UNDERSCORE;
        }

        @Override
        public String asString() {
            return "_";
        }
    }

    public static Dollar dollar() {
        return new Dollar();
    }

    public static class Dollar extends ExpressionNode {
        @Override
        public Expression compile() {
            return Expression.DOLLAR;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_DOLLAR;
        }

        @Override
        public String asString() {
            return "$";
        }
    }

    public static Variable variable(String variable) {
        return new Variable(variable);
    }

    public static Variable variable() {
        return new Variable();
    }

    public static class Variable extends CanAssign {
        public String variable;

        public Variable(String variable) {
            this.variable = variable;
        }

        public Variable() {
        }

        public Variable var(String name) {
            variable = name;
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of();
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_VARIABLE;
        }

        @Override
        public String asString() {
            return variable;
        }

        @Override
        public Expression compile() {
            return new Expression.Variable(variable);
        }

        @Override
        public Assignable compileAssignable() {
            return new Assignable.Variable(variable);
        }
    }

    public static UnaryOp unary(UnaryOperatorType type, ExpressionNode node) {
        return new UnaryOp(type, node);
    }

    public static UnaryOp unaryPlus(ExpressionNode node) {
        return unary(UnaryOperatorType.PLUS, node);
    }

    public static UnaryOp unaryNeg(ExpressionNode node) {
        return unary(UnaryOperatorType.MINUS, node);
    }

    public static UnaryOp unaryNot(ExpressionNode node) {
        return unary(UnaryOperatorType.NOT, node);
    }

    public static UnaryOp unaryBNot(ExpressionNode node) {
        return unary(UnaryOperatorType.BIT_NOT, node);
    }

    public static UnaryOp unaryLen(ExpressionNode node) {
        return unary(UnaryOperatorType.LENGTH, node);
    }

    public static UnaryOp unaryCopy(ExpressionNode node) {
        return unary(UnaryOperatorType.COPY, node);
    }

    public static UnaryOp unary() {
        return new UnaryOp();
    }

    public UnaryOp thenUnary(UnaryOperatorType type) {
        return unary(type, this);
    }

    // @formatter:off
    public UnaryOp thenPlus() { return thenUnary(UnaryOperatorType.PLUS); }
    public UnaryOp thenNeg() { return thenUnary(UnaryOperatorType.MINUS); }
    public UnaryOp thenNot() { return thenUnary(UnaryOperatorType.NOT); }
    public UnaryOp thenBNot() { return thenUnary(UnaryOperatorType.BIT_NOT); }
    public UnaryOp thenLen() { return thenUnary(UnaryOperatorType.LENGTH); }
    public UnaryOp thenCopy() { return thenUnary(UnaryOperatorType.COPY); }
    // @formatter:on

    public UnaryOp asUnary() {
        return unary().expr(this);
    }

    public static class UnaryOp extends ExpressionNode {
        public ExpressionNode expr;
        public UnaryOperatorType type;

        public UnaryOp(UnaryOperatorType type, ExpressionNode expr) {
            this.expr = expr;
            this.type = type;
        }

        public UnaryOp() {
        }

        public UnaryOp expr(ExpressionNode expr) {
            this.expr = expr;
            return this;
        }

        public UnaryOp type(UnaryOperatorType type) {
            this.type = type;
            return this;
        }

        // @formatter:off
        public UnaryOp plus() { return type(UnaryOperatorType.PLUS); }
        public UnaryOp neg() { return type(UnaryOperatorType.MINUS); }
        public UnaryOp not() { return type(UnaryOperatorType.NOT); }
        public UnaryOp bnot() { return type(UnaryOperatorType.BIT_NOT); }
        public UnaryOp len() { return type(UnaryOperatorType.LENGTH); }
        public UnaryOp copy() { return type(UnaryOperatorType.COPY); }
        // @formatter:on

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(expr);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_UNARY_OP;
        }

        @Override
        public String asString() {
            return type.symbol + " " + expr.asString();
        }

        @Override
        public Expression compile() {
            return new Expression.Unary(expr.compile(), type.operator);
        }
    }

    public static BinaryOp binary(ExpressionNode left, BinaryOperatorType type, ExpressionNode right) {
        return new BinaryOp(left, type, right);
    }

    public static BinaryOp binaryAdd(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.ADD, right);
    }

    public static BinaryOp binarySub(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.SUB, right);
    }

    public static BinaryOp binaryMul(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.MUL, right);
    }

    public static BinaryOp binaryDiv(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.DIV, right);
    }

    public static BinaryOp binaryMod(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.MOD, right);
    }

    public static BinaryOp binaryBAnd(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.BIT_AND, right);
    }

    public static BinaryOp binaryBOr(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.BIT_OR, right);
    }

    public static BinaryOp binaryBXor(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.BIT_XOR, right);
    }

    public static BinaryOp binaryLsh(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.BIT_LSH, right);
    }

    public static BinaryOp binaryRsh(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.BIT_RSH, right);
    }

    public static BinaryOp binaryRrsh(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.BIT_RRSH, right);
    }

    public static BinaryOp binaryEq(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.EQ, right);
    }

    public static BinaryOp binaryNeq(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.NEQ, right);
    }

    public static BinaryOp binaryLt(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.LT, right);
    }

    public static BinaryOp binaryLe(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.LE, right);
    }

    public static BinaryOp binaryGt(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.GT, right);
    }

    public static BinaryOp binaryGe(ExpressionNode left, ExpressionNode right) {
        return binary(left, BinaryOperatorType.GE, right);
    }

    public static BinaryOp binary() {
        return new BinaryOp();
    }

    public BinaryOp asBinaryLeft() {
        return binary().left(this);
    }

    public BinaryOp asBinaryRight() {
        return binary().right(this);
    }

    public BinaryOp thenBinary(BinaryOperatorType type, ExpressionNode right) {
        return new BinaryOp(this, type, right);
    }

    // @formatter:off
    public BinaryOp thenAdd(ExpressionNode right) { return thenBinary(BinaryOperatorType.ADD, right); }
    public BinaryOp thenSub(ExpressionNode right) { return thenBinary(BinaryOperatorType.SUB, right); }
    public BinaryOp thenMul(ExpressionNode right) { return thenBinary(BinaryOperatorType.MUL, right); }
    public BinaryOp thenDiv(ExpressionNode right) { return thenBinary(BinaryOperatorType.DIV, right); }
    public BinaryOp thenMod(ExpressionNode right) { return thenBinary(BinaryOperatorType.MOD, right); }
    public BinaryOp thenBAnd(ExpressionNode right) { return thenBinary(BinaryOperatorType.BIT_AND, right); }
    public BinaryOp thenBOr(ExpressionNode right) { return thenBinary(BinaryOperatorType.BIT_OR, right); }
    public BinaryOp thenBXor(ExpressionNode right) { return thenBinary(BinaryOperatorType.BIT_XOR, right); }
    public BinaryOp thenLsh(ExpressionNode right) { return thenBinary(BinaryOperatorType.BIT_LSH, right); }
    public BinaryOp thenRsh(ExpressionNode right) { return thenBinary(BinaryOperatorType.BIT_RSH, right); }
    public BinaryOp thenRrsh(ExpressionNode right) { return thenBinary(BinaryOperatorType.BIT_RRSH, right); }
    public BinaryOp thenEq(ExpressionNode right) { return thenBinary(BinaryOperatorType.EQ, right); }
    public BinaryOp thenNeq(ExpressionNode right) { return thenBinary(BinaryOperatorType.NEQ, right); }
    public BinaryOp thenLt(ExpressionNode right) { return thenBinary(BinaryOperatorType.LT, right); }
    public BinaryOp thenGt(ExpressionNode right) { return thenBinary(BinaryOperatorType.GT, right); }
    public BinaryOp thenLe(ExpressionNode right) { return thenBinary(BinaryOperatorType.LE, right); }
    public BinaryOp thenGe(ExpressionNode right) { return thenBinary(BinaryOperatorType.GE, right); }
    // @formatter:on

    public static class BinaryOp extends ExpressionNode {
        public ExpressionNode left;
        public ExpressionNode right;
        public BinaryOperatorType type;

        public BinaryOp(ExpressionNode left, BinaryOperatorType type, ExpressionNode right) {
            this.left = left;
            this.right = right;
            this.type = type;
        }

        public BinaryOp() {
        }

        public BinaryOp left(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public BinaryOp right(ExpressionNode expr) {
            right = expr;
            return this;
        }

        public BinaryOp type(BinaryOperatorType type) {
            this.type = type;
            return this;
        }

        // @formatter:off
        public BinaryOp add() { return type(BinaryOperatorType.ADD); }
        public BinaryOp sub() { return type(BinaryOperatorType.SUB); }
        public BinaryOp mul() { return type(BinaryOperatorType.MUL); }
        public BinaryOp div() { return type(BinaryOperatorType.DIV); }
        public BinaryOp mod() { return type(BinaryOperatorType.MOD); }
        public BinaryOp band() { return type(BinaryOperatorType.BIT_AND); }
        public BinaryOp bor() { return type(BinaryOperatorType.BIT_OR); }
        public BinaryOp bxor() { return type(BinaryOperatorType.BIT_XOR); }
        public BinaryOp lsh() { return type(BinaryOperatorType.BIT_LSH); }
        public BinaryOp rsh() { return type(BinaryOperatorType.BIT_RSH); }
        public BinaryOp rrsh() { return type(BinaryOperatorType.BIT_RRSH); }
        public BinaryOp eq() { return type(BinaryOperatorType.EQ); }
        public BinaryOp neq() { return type(BinaryOperatorType.NEQ); }
        public BinaryOp lt() { return type(BinaryOperatorType.LT); }
        public BinaryOp gt() { return type(BinaryOperatorType.GT); }
        public BinaryOp le() { return type(BinaryOperatorType.LE); }
        public BinaryOp ge() { return type(BinaryOperatorType.GE); }
        // @formatter:on

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left, right);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_BINARY_OP;
        }

        @Override
        public String asString() {
            return left.asString() + " " + type.symbol + " " + right.asString();
        }

        @Override
        public Expression compile() {
            return new Expression.Binary(left.compile(), right.compile(), type.operator);
        }
    }

    public static TernaryOp ternary(ExpressionNode left, ExpressionNode middle, ExpressionNode right) {
        return new TernaryOp(left, middle, right);
    }

    public static TernaryOp ternary() {
        return new TernaryOp();
    }

    public TernaryOp thenTernary(ExpressionNode middle, ExpressionNode right) {
        return ternary(this, middle, right);
    }

    public TernaryOp asTernaryLeft() {
        return ternary().left(this);
    }

    public TernaryOp asTernaryMiddle() {
        return ternary().middle(this);
    }

    public TernaryOp asTernaryRight() {
        return ternary().right(this);
    }

    public static class TernaryOp extends ExpressionNode {
        public ExpressionNode left;
        public ExpressionNode middle;
        public ExpressionNode right;

        public TernaryOp(ExpressionNode left, ExpressionNode middle, ExpressionNode right) {
            this.left = left;
            this.middle = middle;
            this.right = right;
        }

        public TernaryOp() {
        }

        public TernaryOp left(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public TernaryOp middle(ExpressionNode expr) {
            middle = expr;
            return this;
        }

        public TernaryOp right(ExpressionNode expr) {
            right = expr;
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left, middle, right);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_TERNARY_OP;
        }

        @Override
        public String asString() {
            return left.asString() + " ? " + middle.asString() + " : " + right.asString();
        }

        @Override
        public Expression compile() {
            return new Expression.Ternary(left.compile(), middle.compile(), right.compile(), Operators::cond);
        }
    }

    public static HasKeyOp hasKey(ExpressionNode expr, ExpressionNode key, boolean hasnt) {
        return new HasKeyOp(expr, key, hasnt);
    }

    public static HasKeyOp hasKey(ExpressionNode expr, ExpressionNode key) {
        return new HasKeyOp(expr, key, false);
    }

    public static HasKeyOp hasntKey(ExpressionNode expr, ExpressionNode key) {
        return new HasKeyOp(expr, key, true);
    }

    public static HasKeyOp hasKey() {
        return new HasKeyOp();
    }

    public HasKeyOp asHasKeyLeft() {
        return hasKey().expr(this);
    }

    public HasKeyOp asHasKeyRight() {
        return hasKey().key(this);
    }

    public HasKeyOp hasKey(ExpressionNode key, boolean hasnt) {
        return hasKey(this, key, hasnt);
    }

    public HasKeyOp hasKey(ExpressionNode key) {
        return hasKey(this, key);
    }

    public HasKeyOp hasntKey(ExpressionNode key) {
        return hasntKey(this, key);
    }

    public HasKeyOp hasKey(String key, boolean hasnt) {
        return hasKey(this, literal(key), hasnt);
    }

    public HasKeyOp hasKey(String key) {
        return hasKey(this, literal(key));
    }

    public HasKeyOp hasntKey(String key) {
        return hasntKey(this, literal(key));
    }

    public static class HasKeyOp extends ExpressionNode {
        public ExpressionNode left;
        public ExpressionNode right;
        public boolean hasnt;

        public HasKeyOp(ExpressionNode left, ExpressionNode right, boolean hasnt) {
            this.left = left;
            this.right = right;
            this.hasnt = hasnt;
        }

        public HasKeyOp() {
        }

        public HasKeyOp expr(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public HasKeyOp key(ExpressionNode expr) {
            right = expr;
            return this;
        }

        public HasKeyOp key(String key) {
            right = literal(key);
            return this;
        }

        public HasKeyOp hasnt(boolean hasnt) {
            this.hasnt = hasnt;
            return this;
        }

        public HasKeyOp has(boolean has) {
            this.hasnt = !has;
            return this;
        }

        public HasKeyOp hasnt() {
            return hasnt(true);
        }

        public HasKeyOp has() {
            return hasnt(false);
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left, right);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_HAS_KEY;
        }

        @Override
        public String asString() {
            return left.asString() + " " + (hasnt ? "hasnt" : "has") + " " + right.asString();
        }

        @Override
        public Expression compile() {
            return new Expression.Binary(left.compile(), right.compile(), hasnt ? Operators::hasnt : Operators::has);
        }
    }

    public static IsTypeOp isType(ExpressionNode expr, JsonType type, boolean isnt) {
        return new IsTypeOp(expr, type, isnt);
    }

    public static IsTypeOp isType(ExpressionNode expr, JsonType type) {
        return isType(expr, type, false);
    }

    public static IsTypeOp isntType(ExpressionNode expr, JsonType type) {
        return isType(expr, type, true);
    }

    public static IsTypeOp isType() {
        return new IsTypeOp();
    }

    public static IsTypeOp isNull(ExpressionNode expr, boolean isnt) {
        return isType(expr, JsonType.NULL, isnt);
    }

    public static IsTypeOp isNull(ExpressionNode expr) {
        return isType(expr, JsonType.NULL, false);
    }

    public static IsTypeOp isntNull(ExpressionNode expr) {
        return isType(expr, JsonType.NULL, true);
    }

    public static IsTypeOp isBool(ExpressionNode expr, boolean isnt) {
        return isType(expr, JsonType.BOOLEAN, isnt);
    }

    public static IsTypeOp isBool(ExpressionNode expr) {
        return isType(expr, JsonType.BOOLEAN, false);
    }

    public static IsTypeOp isntBool(ExpressionNode expr) {
        return isType(expr, JsonType.BOOLEAN, true);
    }

    public static IsTypeOp isNum(ExpressionNode expr, boolean isnt) {
        return isType(expr, JsonType.NUMBER, isnt);
    }

    public static IsTypeOp isNum(ExpressionNode expr) {
        return isType(expr, JsonType.NUMBER, false);
    }

    public static IsTypeOp isntNum(ExpressionNode expr) {
        return isType(expr, JsonType.NUMBER, true);
    }

    public static IsTypeOp isStr(ExpressionNode expr, boolean isnt) {
        return isType(expr, JsonType.STRING, isnt);
    }

    public static IsTypeOp isStr(ExpressionNode expr) {
        return isType(expr, JsonType.STRING, false);
    }

    public static IsTypeOp isntStr(ExpressionNode expr) {
        return isType(expr, JsonType.STRING, true);
    }

    public static IsTypeOp isArr(ExpressionNode expr, boolean isnt) {
        return isType(expr, JsonType.ARRAY, isnt);
    }

    public static IsTypeOp isArr(ExpressionNode expr) {
        return isType(expr, JsonType.ARRAY, false);
    }

    public static IsTypeOp isntArr(ExpressionNode expr) {
        return isType(expr, JsonType.ARRAY, true);
    }

    public static IsTypeOp isObj(ExpressionNode expr, boolean isnt) {
        return isType(expr, JsonType.OBJECT, isnt);
    }

    public static IsTypeOp isObj(ExpressionNode expr) {
        return isType(expr, JsonType.OBJECT, false);
    }

    public static IsTypeOp isntObj(ExpressionNode expr) {
        return isType(expr, JsonType.OBJECT, true);
    }

    public IsTypeOp asIsType() {
        return isType().expr(this);
    }

    public IsTypeOp isType(JsonType type, boolean isnt) {
        return isType(this, type, isnt);
    }

    public IsTypeOp isType(JsonType type) {
        return isType(this, type);
    }

    public IsTypeOp isntType(JsonType type) {
        return isntType(this, type);
    }

    public IsTypeOp isNull(boolean isnt) {
        return isType(this, JsonType.NULL, isnt);
    }

    public IsTypeOp isNull() {
        return isType(this, JsonType.NULL);
    }

    public IsTypeOp isntNull() {
        return isntType(this, JsonType.NULL);
    }

    public IsTypeOp isNum(boolean isnt) {
        return isType(this, JsonType.NUMBER, isnt);
    }

    public IsTypeOp isNum() {
        return isType(this, JsonType.NUMBER);
    }

    public IsTypeOp isntNum() {
        return isntType(this, JsonType.NUMBER);
    }

    public IsTypeOp isStr(boolean isnt) {
        return isType(this, JsonType.STRING, isnt);
    }

    public IsTypeOp isStr() {
        return isType(this, JsonType.STRING);
    }

    public IsTypeOp isntStr() {
        return isntType(this, JsonType.STRING);
    }

    public IsTypeOp isBool(boolean isnt) {
        return isType(this, JsonType.BOOLEAN, isnt);
    }

    public IsTypeOp isBool() {
        return isType(this, JsonType.BOOLEAN);
    }

    public IsTypeOp isntBool() {
        return isntType(this, JsonType.BOOLEAN);
    }

    public IsTypeOp isArr(boolean isnt) {
        return isType(this, JsonType.ARRAY, isnt);
    }

    public IsTypeOp isArr() {
        return isType(this, JsonType.ARRAY);
    }

    public IsTypeOp isntArr() {
        return isntType(this, JsonType.ARRAY);
    }

    public IsTypeOp isObj(boolean isnt) {
        return isType(this, JsonType.OBJECT, isnt);
    }

    public IsTypeOp isObj() {
        return isType(this, JsonType.OBJECT);
    }

    public IsTypeOp isntObj() {
        return isntType(this, JsonType.OBJECT);
    }

    public static class IsTypeOp extends ExpressionNode {
        public ExpressionNode left;
        public JsonType right;
        public boolean isnt;

        public IsTypeOp(ExpressionNode left, JsonType right, boolean isnt) {
            this.left = left;
            this.right = right;
            this.isnt = isnt;
        }

        public IsTypeOp() {
        }

        public IsTypeOp expr(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public IsTypeOp type(JsonType type) {
            right = type;
            return this;
        }

        public IsTypeOp typeNull() {
            return type(JsonType.NULL);
        }

        public IsTypeOp typeNum() {
            return type(JsonType.NUMBER);
        }

        public IsTypeOp typeStr() {
            return type(JsonType.STRING);
        }

        public IsTypeOp typeBool() {
            return type(JsonType.BOOLEAN);
        }

        public IsTypeOp typeArr() {
            return type(JsonType.ARRAY);
        }

        public IsTypeOp typeObj() {
            return type(JsonType.OBJECT);
        }

        public IsTypeOp isnt(boolean isnt) {
            this.isnt = isnt;
            return this;
        }

        public IsTypeOp is(boolean is) {
            this.isnt = !is;
            return this;
        }

        public IsTypeOp is() {
            return isnt(false);
        }

        public IsTypeOp isnt() {
            return isnt(true);
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_IS_TYPE;
        }

        @Override
        public String asString() {
            return left.asString() + " " + (isnt ? "isnt" : "is") + " " + right.templateName();
        }

        @Override
        public Expression compile() {
            return new Expression.Unary(left.compile(), isnt ? n -> Operators.isnt(n, right) : n -> Operators.is(n, right));
        }
    }

    public static LogicOp logic(ExpressionNode left, ExpressionNode right, boolean conjunction) {
        return new LogicOp(left, right, conjunction);
    }

    public static LogicOp or(ExpressionNode left, ExpressionNode right) {
        return new LogicOp(left, right, false);
    }

    public static LogicOp and(ExpressionNode left, ExpressionNode right) {
        return new LogicOp(left, right, true);
    }

    public static LogicOp logic() {
        return new LogicOp();
    }

    public LogicOp logic(ExpressionNode right, boolean conjunction) {
        return logic(this, right, conjunction);
    }

    public LogicOp and(ExpressionNode right) {
        return and(this, right);
    }

    public LogicOp or(ExpressionNode right) {
        return or(this, right);
    }

    public LogicOp asLogicLeft() {
        return logic().left(this);
    }

    public LogicOp asLogicRight() {
        return logic().right(this);
    }

    public static class LogicOp extends ExpressionNode {
        public ExpressionNode left;
        public ExpressionNode right;
        public boolean conjunction;

        public LogicOp(ExpressionNode left, ExpressionNode right, boolean conjunction) {
            this.left = left;
            this.right = right;
            this.conjunction = conjunction;
        }

        public LogicOp() {
        }

        public LogicOp left(ExpressionNode expr) {
            this.left = expr;
            return this;
        }

        public LogicOp right(ExpressionNode expr) {
            this.right = expr;
            return this;
        }

        public LogicOp conjunction(boolean conjunction) {
            this.conjunction = conjunction;
            return this;
        }

        public LogicOp disjunction(boolean disjunction) {
            this.conjunction = !disjunction;
            return this;
        }

        public LogicOp conjunction() {
            return conjunction(true);
        }

        public LogicOp disjunction() {
            return conjunction(false);
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left, right);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_LOGIC_OP;
        }

        @Override
        public String asString() {
            return left.asString() + " " + (conjunction ? "&&" : "||") + " " + right.asString();
        }

        @Override
        public Expression compile() {
            if (conjunction)
                return new Expression.Conjunction(left.compile(), right.compile());
            return new Expression.Disjunction(left.compile(), right.compile());
        }
    }

    public static StringInterpolation string() {
        return new StringInterpolation();
    }

    public static StringInterpolation string(String content) {
        return string().append(content);
    }

    public static StringInterpolation string(ExpressionNode interpolation) {
        return string().append(interpolation);
    }

    public StringInterpolation inString() {
        return string().append(this);
    }

    public static class StringInterpolation extends ExpressionNode {
        public final List<StringInterpolation.Interpolation> interpolations = new ArrayList<>();

        public StringInterpolation prepend(String content) {
            interpolations.add(0, new StringInterpolation.Interpolation(content));
            return this;
        }

        public StringInterpolation prepend(ExpressionNode node) {
            interpolations.add(0, new StringInterpolation.Interpolation(node));
            return this;
        }

        public StringInterpolation append(String content) {
            interpolations.add(new StringInterpolation.Interpolation(content));
            return this;
        }

        public StringInterpolation append(ExpressionNode node) {
            interpolations.add(new StringInterpolation.Interpolation(node));
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.copyOf(interpolations);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_INTERPOLATE_STRING;
        }

        @Override
        public Expression compile() {
            List<StringInterpolation.CompiledInterpolation> interpolations = new ArrayList<>();
            for (StringInterpolation.Interpolation ipl : this.interpolations) {
                StringInterpolation.CompiledInterpolation cipl = ipl.compile();
                if (cipl.str != null) {
                    if (cipl.str.isEmpty())
                        continue;

                    if (!interpolations.isEmpty()) {
                        StringInterpolation.CompiledInterpolation last = interpolations.get(interpolations.size() - 1);
                        if (last.str != null) {
                            cipl = new StringInterpolation.CompiledInterpolation(last.str + cipl.str);
                            interpolations.set(interpolations.size() - 1, cipl);
                            continue;
                        }
                    }
                }
                interpolations.add(cipl);
            }

            if (interpolations.isEmpty()) {
                return new Expression.Literal(JsonNode.EMPTY_STRING);
            }

            if (interpolations.size() == 1) {
                StringInterpolation.CompiledInterpolation cipl = interpolations.get(0);
                if (cipl.str != null)
                    return new Expression.Literal(JsonNode.string(cipl.str));
                else
                    return cipl.expr;
            }

            return new Expression() {
                @Override
                public JsonNode evaluate(TemplateContext context, Vfl vfl) {
                    StringBuilder builder = new StringBuilder();
                    for (CompiledInterpolation cipl : interpolations) {
                        if (cipl.expr != null)
                            builder.append(Operators.stringify(context.evaluate(cipl.expr)));
                        else
                            builder.append(cipl.str);
                    }
                    return JsonNode.string(builder.toString());
                }

                @Override
                public boolean isContextDependent() {
                    return true; // All simplification has been done already
                }

                @Override
                public JsonNode simplifyToLiteral() {
                    return null; // All simplification has been done already
                }

                @Override
                public String writeDebug() {
                    return "interpolateString(" + interpolations.stream().map(CompiledInterpolation::writeDebug).collect(Collectors.joining()) + ")";
                }
            };
        }

        @Override
        public String asString() {
            StringBuilder builder = new StringBuilder("\"");
            for (StringInterpolation.Interpolation ipl : interpolations) {
                builder.append(ipl.asString());
            }
            return builder.append("\"").toString();
        }

        private static void escape(String string, StringBuilder builder, char quote) {
            for (int i = 0, l = string.length(); i < l; i++) {
                char c = string.charAt(i);
                if (c == quote || c == '\\' || c == '#')
                    builder.append("\\");

                if (c < 0x20 || c > 0x7F) {
                    if (c == '\n')
                        builder.append("\\n");
                    else if (c == '\r')
                        builder.append("\\r");
                    else if (c == '\t')
                        builder.append("\\t");
                    else {
                        builder.append(String.format("\\u%04X", (int) c));
                    }
                } else {
                    builder.append(c);
                }
            }
        }

        private static class CompiledInterpolation {
            final String str;
            final Expression expr;

            CompiledInterpolation(String str) {
                this.str = str;
                this.expr = null;
            }

            CompiledInterpolation(Expression expr) {
                JsonNode simplified = expr.simplifyToLiteral();
                if (simplified != null) {
                    this.str = Operators.stringify(simplified);
                    this.expr = null;
                } else {
                    this.str = null;
                    this.expr = expr;
                }
            }

            String writeDebug() {
                if (expr != null)
                    return expr.writeDebug();
                StringBuilder builder = new StringBuilder("\"");
                escape(str, builder, '"');
                builder.append("\"");
                return builder.toString();
            }
        }

        public static class Interpolation extends ParsedTemplateNode {
            public final String str;
            public final ExpressionNode node;

            public Interpolation(String str) {
                this.str = str;
                this.node = null;
            }

            public Interpolation(ExpressionNode node) {
                this.str = null;
                this.node = node;
            }

            private CompiledInterpolation compile() {
                if (node != null)
                    return new CompiledInterpolation(node.compile());
                return new CompiledInterpolation(str);
            }

            @Override
            protected List<ParsedTemplateNode> childList() {
                return node != null ? List.of(node) : List.of();
            }

            @Override
            public NodeType type() {
                return NodeType.STRING_INTERPOLATION;
            }

            @Override
            public EntityNode asEntity() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String asString() {
                StringBuilder builder = new StringBuilder();
                if (str != null) {
                    escape(str, builder, '"');
                } else {
                    assert node != null;
                    builder.append("#[ ").append(node.asString()).append(" ]");
                }
                return builder.toString();
            }
        }
    }

    public static Member member(ExpressionNode expr, String member) {
        return new Member(expr, member);
    }

    public static Member member() {
        return new ExpressionNode.Member();
    }

    public Member member(String member) {
        return member(this, member);
    }

    public Member asMember() {
        return member().expr(this);
    }

    public static class Member extends CanAssign {
        public ExpressionNode left;
        public String right;

        public Member(ExpressionNode left, String right) {
            this.left = left;
            this.right = right;
        }

        public Member() {
        }

        public Member var(String var) {
            left = variable(var);
            return this;
        }

        public Member expr(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public Member name(String name) {
            right = name;
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_MEMBER;
        }

        @Override
        public String asString() {
            return left.asString() + "." + right;
        }

        @Override
        public Expression compile() {
            return new Expression.Unary(left.compile(), n -> Operators.field(n, right));
        }

        @Override
        public Assignable compileAssignable() {
            return new Assignable.Member(left.compile(), right);
        }
    }

    public static Index index(ExpressionNode expr, ExpressionNode index) {
        return new Index(expr, index);
    }

    public static Index index(ExpressionNode expr, String member) {
        return index(expr, literal(member));
    }

    public static Index index(ExpressionNode expr, Number member) {
        return index(expr, literal(member));
    }

    public static Index index() {
        return new Index();
    }

    public Index asIndexed() {
        return index().expr(this);
    }

    public Index asIndex() {
        return index().idx(this);
    }

    public Index index(ExpressionNode idx) {
        return index(this, idx);
    }

    public Index index(String idx) {
        return index(this, idx);
    }

    public Index index(Number idx) {
        return index(this, idx);
    }

    public Index indexOf(ExpressionNode expr) {
        return index(expr, this);
    }

    public static class Index extends CanAssign {
        public ExpressionNode left;
        public ExpressionNode name;

        public Index(ExpressionNode left, ExpressionNode name) {
            this.left = left;
            this.name = name;
        }

        public Index() {
        }

        public Index expr(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public Index idx(ExpressionNode expr) {
            name = expr;
            return this;
        }

        public Index idx(Number index) {
            name = literal(index);
            return this;
        }

        public Index idx(String member) {
            name = literal(member);
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left, name);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_INDEX;
        }

        @Override
        public String asString() {
            return left.asString() + "[" + name.asString() + "]";
        }

        @Override
        public Expression compile() {
            return new Expression.Binary(left.compile(), name.compile(), Operators::index);
        }

        @Override
        public Assignable compileAssignable() {
            return new Assignable.Index(left.compile(), name.compile());
        }
    }

    public static Slice slice(ExpressionNode expr, ExpressionNode from, ExpressionNode to) {
        return new Slice(expr, from, to);
    }

    public static Slice slice(ExpressionNode expr, Number from, Number to) {
        return new Slice(expr, literal(from), literal(to));
    }

    public static Slice sliceFrom(ExpressionNode expr, ExpressionNode from) {
        return new Slice(expr, from, null);
    }

    public static Slice sliceFrom(ExpressionNode expr, Number from) {
        return new Slice(expr, literal(from), null);
    }

    public static Slice sliceTo(ExpressionNode expr, ExpressionNode to) {
        return new Slice(expr, null, to);
    }

    public static Slice sliceTo(ExpressionNode expr, Number to) {
        return new Slice(expr, null, literal(to));
    }

    public static Slice slice(ExpressionNode expr) {
        return new Slice(expr, null, null);
    }

    public static Slice slice() {
        return new Slice();
    }

    public Slice asSliceFrom() {
        return slice().from(this);
    }

    public Slice asSliceTo() {
        return slice().to(this);
    }

    public Slice sliced(ExpressionNode from, ExpressionNode to) {
        return slice(this, from, to);
    }

    public Slice sliced(Number from, Number to) {
        return slice(this, from, to);
    }

    public Slice slicedFrom(ExpressionNode from) {
        return sliceFrom(this, from);
    }

    public Slice slicedFrom(Number from) {
        return sliceFrom(this, from);
    }

    public Slice slicedTo(ExpressionNode from) {
        return sliceTo(this, from);
    }

    public Slice slicedTo(Number from) {
        return sliceTo(this, from);
    }

    public Slice sliced() {
        return slice(this);
    }

    public static class Slice extends ExpressionNode {
        public ExpressionNode left;
        public ExpressionNode from;
        public ExpressionNode to;

        public Slice(ExpressionNode left, ExpressionNode from, ExpressionNode to) {
            this.left = left;
            this.from = from;
            this.to = to;
        }

        public Slice() {
        }

        public Slice expr(ExpressionNode expr) {
            left = expr;
            return this;
        }

        public Slice noFrom() {
            from = null;
            return this;
        }

        public Slice from(ExpressionNode expr) {
            from = expr;
            return this;
        }

        public Slice from(Number from) {
            return from(literal(from));
        }

        public Slice noTo() {
            to = null;
            return this;
        }

        public Slice to(ExpressionNode expr) {
            to = expr;
            return this;
        }

        public Slice to(Number from) {
            return to(literal(from));
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            ArrayList<ParsedTemplateNode> children = new ArrayList<>();
            children.add(left);
            if (from != null) children.add(from);
            if (to != null) children.add(to);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_SLICE;
        }

        @Override
        public String asString() {
            return left.asString() + "[" + (from != null ? from.asString() : "") + ".." + (to != null ? to.asString() : "") + "]";
        }

        @Override
        public Expression compile() {
            if (from == null && to == null)
                return new Expression.Unary(left.compile(), Operators::slice);
            if (from == null)
                return new Expression.Binary(left.compile(), to.compile(), Operators::sliceTo);
            if (to == null)
                return new Expression.Binary(left.compile(), from.compile(), Operators::sliceFrom);
            return new Expression.Ternary(left.compile(), from.compile(), to.compile(), Operators::slice);
        }
    }

    public static DoThen doThen(ExpressionNode node) {
        return new DoThen(node);
    }

    public static DoThen doThen() {
        return new DoThen();
    }

    public DoThen asDoThen() {
        return doThen(this);
    }

    public DoThen firstDo(ExpressionNode before) {
        return doThen(this).prependBefore(before);
    }

    public DoThen thenDo(ExpressionNode after) {
        return doThen(this).appendAfter(after);
    }

    public static class DoThen extends ExpressionNode {
        public final List<ExpressionNode> doBefore = new ArrayList<>();
        public final List<ExpressionNode> doAfter = new ArrayList<>();
        public ExpressionNode expr;

        public DoThen(ExpressionNode expr) {
            this.expr = expr;
        }

        public DoThen() {
        }

        public DoThen prependBefore(ExpressionNode node) {
            doBefore.add(0, node);
            return this;
        }

        public DoThen appendBefore(ExpressionNode node) {
            doBefore.add(node);
            return this;
        }

        public DoThen prependAfter(ExpressionNode node) {
            doAfter.add(0, node);
            return this;
        }

        public DoThen appendAfter(ExpressionNode node) {
            doAfter.add(node);
            return this;
        }

        public DoThen expr(ExpressionNode expr) {
            this.expr = expr;
            return this;
        }

        @Override
        public DoThen asDoThen() {
            return this;
        }

        @Override
        public DoThen thenDo(ExpressionNode after) {
            return appendAfter(after);
        }

        @Override
        public DoThen firstDo(ExpressionNode before) {
            return prependBefore(before);
        }

        public void flatten() {
            if (expr.type() == NodeType.EXPR_DO_THEN) {
                DoThen e = (DoThen) expr;
                e.flatten();
                doBefore.addAll(e.doBefore);
                doAfter.addAll(0, e.doAfter);
                expr = e.expr;
            }
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            ArrayList<ParsedTemplateNode> children = new ArrayList<>(doBefore);
            children.add(expr);
            children.addAll(doAfter);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_DO_THEN;
        }

        @Override
        public String asString() {
            String str = expr.asString();
            if (!doBefore.isEmpty())
                str = doBefore.stream()
                              .map(ParsedTemplateNode::asString)
                              .collect(Collectors.joining(", ", "do { ", " }")) + " then " + str;
            if (!doAfter.isEmpty())
                str = str + " then " + doAfter.stream()
                                              .map(ParsedTemplateNode::asString)
                                              .collect(Collectors.joining(", ", "do { ", " } "));
            return str;
        }

        @Override
        public Expression compile() {
            flatten();
            return new Expression.EvaluateBeforeAfter(
                expr.compile(),
                doBefore.stream()
                        .map(ExpressionNode::compile)
                        .filter(e -> e.simplifyToLiteral() == null)
                        .collect(Collectors.toUnmodifiableList()),
                doAfter.stream()
                       .map(ExpressionNode::compile)
                       .filter(e -> e.simplifyToLiteral() == null)
                       .collect(Collectors.toUnmodifiableList())
            );
        }
    }

    public static Assign assign(CanAssign left, ExpressionNode right, AssignType type) {
        return new Assign(left, right, type);
    }

    public static Assign assign(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.SET);
    }

    public static Assign assignAdd(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.ADD);
    }

    public static Assign assignSub(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.SUB);
    }

    public static Assign assignMul(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.MUL);
    }

    public static Assign assignDiv(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.DIV);
    }

    public static Assign assignMod(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.MOD);
    }

    public static Assign assignBAnd(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.BIT_AND);
    }

    public static Assign assignBOr(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.BIT_OR);
    }

    public static Assign assignBXor(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.BIT_XOR);
    }

    public static Assign assignLsh(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.BIT_LSH);
    }

    public static Assign assignRsh(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.BIT_RSH);
    }

    public static Assign assignRrsh(CanAssign left, ExpressionNode right) {
        return assign(left, right, AssignType.BIT_RRSH);
    }

    public static Assign assign() {
        return new Assign();
    }

    public Assign asAssigned() {
        return assign().right(this);
    }

    public static class Assign extends ExpressionNode {
        public CanAssign left;
        public ExpressionNode right;
        public AssignType type = AssignType.SET;

        public Assign(CanAssign left, ExpressionNode right, AssignType type) {
            this.left = left;
            this.right = right;
            this.type = type;
        }

        public Assign() {
        }

        public Assign left(CanAssign left) {
            this.left = left;
            return this;
        }

        public Assign right(ExpressionNode right) {
            this.right = right;
            return this;
        }

        public Assign type(AssignType type) {
            this.type = type;
            return this;
        }

        // @formatter:off
        public Assign set() { return type(AssignType.SET); }
        public Assign add() { return type(AssignType.ADD); }
        public Assign sub() { return type(AssignType.SUB); }
        public Assign mul() { return type(AssignType.MUL); }
        public Assign div() { return type(AssignType.DIV); }
        public Assign mod() { return type(AssignType.MOD); }
        public Assign band() { return type(AssignType.BIT_AND); }
        public Assign bor() { return type(AssignType.BIT_OR); }
        public Assign bxor() { return type(AssignType.BIT_XOR); }
        public Assign lsh() { return type(AssignType.BIT_LSH); }
        public Assign rsh() { return type(AssignType.BIT_RSH); }
        public Assign rrsh() { return type(AssignType.BIT_RRSH); }
        // @formatter:on

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(left, right);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_ASSIGN;
        }

        @Override
        public String asString() {
            return left.asString() + " " + type.symbol + " " + right.asString();
        }

        @Override
        public Expression compile() {
            Expression r = right.compile();
            if (type.operator != null) {
                r = new Expression.Binary(left.compile(), r, type.operator.operator);
            }
            return new Expression.Assign(left.compileAssignable(), r);
        }
    }

    public static Incr incr(CanAssign expr, boolean postfix, boolean decr) {
        return new Incr(expr, postfix, decr);
    }

    public static Incr incr(CanAssign expr, boolean postfix) {
        return new Incr(expr, postfix, false);
    }

    public static Incr decr(CanAssign expr, boolean postfix) {
        return new Incr(expr, postfix, true);
    }

    public static Incr preIncr(CanAssign expr, boolean decr) {
        return new Incr(expr, false, decr);
    }

    public static Incr postIncr(CanAssign expr, boolean decr) {
        return new Incr(expr, true, decr);
    }

    public static Incr preIncr(CanAssign expr) {
        return new Incr(expr, false, false);
    }

    public static Incr postIncr(CanAssign expr) {
        return new Incr(expr, true, false);
    }

    public static Incr preDecr(CanAssign expr) {
        return new Incr(expr, false, true);
    }

    public static Incr postDecr(CanAssign expr) {
        return new Incr(expr, true, true);
    }

    public static Incr incr() {
        return new Incr();
    }

    public static class Incr extends ExpressionNode {
        public CanAssign expr;
        public boolean postfix;
        public boolean decr;

        public Incr(CanAssign expr, boolean postfix, boolean decr) {
            this.expr = expr;
            this.postfix = postfix;
            this.decr = decr;
        }

        public Incr() {
        }

        public Incr set(CanAssign expr) {
            this.expr = expr;
            return this;
        }

        public Incr post(boolean post) {
            postfix = post;
            return this;
        }

        public Incr pre(boolean pre) {
            postfix = !pre;
            return this;
        }

        public Incr post() {
            return post(true);
        }

        public Incr pre() {
            return post(false);
        }

        public Incr decrement(boolean decr) {
            this.decr = decr;
            return this;
        }

        public Incr increment(boolean incr) {
            decr = !incr;
            return this;
        }

        public Incr decrement() {
            return decrement(true);
        }

        public Incr increment() {
            return decrement(false);
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.of(expr);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_ASSIGN;
        }

        @Override
        public String asString() {
            String op = decr ? "--" : "++";
            return postfix ? expr.asString() + " " + op : op + " " + expr.asString();
        }

        @Override
        public Expression compile() {
            return new Expression.Increment(expr.compileAssignable(), expr.compile(), decr, postfix);
        }
    }

    public static Match match(ExpressionNode value) {
        return new Match(value);
    }

    public static Match match() {
        return new Match();
    }

    public Match asMatch() {
        return match(this);
    }

    public static class Match extends ExpressionNode {
        public ExpressionNode match;
        public final List<Case> cases = new ArrayList<>();
        public ExpressionNode caseElse;

        public Match(ExpressionNode match) {
            this.match = match;
        }

        public Match() {
        }

        public Match val(ExpressionNode expr) {
            match = expr;
            return this;
        }

        public Match cond() {
            match = null;
            return this;
        }

        public Match caseIf(ExpressionNode match, ExpressionNode then) {
            cases.add(new Case(match, then));
            return this;
        }

        public Match caseElse(ExpressionNode then) {
            caseElse = then;
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            ArrayList<ParsedTemplateNode> children = new ArrayList<>();
            if (match != null) children.add(match);
            children.addAll(cases);
            if (caseElse != null) children.add(caseElse);
            return List.copyOf(children);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_MATCH;
        }

        @Override
        public String asString() {
            String inner = Stream.concat(cases.stream().map(Case::asString), Stream.ofNullable(caseElse).map(n -> "else -> " + n.asString()))
                                 .collect(Collectors.joining(", ", "{ ", " }"));
            return "match " + (match != null ? match.asString() + " " : "") + inner;
        }

        @Override
        public Expression compile() {
            List<Expression.ConditionalExpression> c = cases.stream()
                                                            .map(cs -> new Expression.ConditionalExpression(
                                                                cs.match.compile(),
                                                                cs.then.compile()
                                                            ))
                                                            .collect(Collectors.toUnmodifiableList());

            Expression e = caseElse != null ? caseElse.compile() : new Expression.Literal(JsonNode.NULL);

            if (match != null) {
                return new Expression.MatchValue(match.compile(), c, e);
            }
            return new Expression.MatchCondition(c, e);
        }

        public static class Case extends ParsedTemplateNode {
            public final ExpressionNode match;
            public final ExpressionNode then;

            public Case(ExpressionNode match, ExpressionNode then) {
                this.match = match;
                this.then = then;
            }

            @Override
            protected List<ParsedTemplateNode> childList() {
                return List.of(match, then);
            }

            @Override
            public NodeType type() {
                return NodeType.MATCH_CASE;
            }

            @Override
            public EntityNode asEntity() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String asString() {
                return "case " + match.asString() + " -> " + then.asString();
            }
        }
    }

    public static Call call(String fn, ExpressionNode... args) {
        return new Call(fn, args);
    }

    public static Call call(String fn) {
        return new Call(fn);
    }

    public static Call call() {
        return new Call();
    }

    public Call asArgument() {
        return new Call().appendArg(this);
    }

    public Call asArgument(String fn) {
        return new Call(fn).appendArg(this);
    }

    public static class Call extends ExpressionNode {
        public String function;
        public final List<ExpressionNode> arguments = new ArrayList<>();

        public Call(String function, ExpressionNode... args) {
            this.function = function;
            arguments.addAll(Arrays.asList(args));
        }

        public Call(String function) {
            this.function = function;
        }

        public Call() {
        }

        public Call function(String name) {
            function = name;
            return this;
        }

        public Call appendArg(ExpressionNode arg) {
            arguments.add(arg);
            return this;
        }

        public Call prependArg(ExpressionNode arg) {
            arguments.add(0, arg);
            return this;
        }

        public Call appendArg(ExpressionNode... args) {
            arguments.addAll(Arrays.asList(args));
            return this;
        }

        public Call prependArg(ExpressionNode... args) {
            arguments.addAll(0, Arrays.asList(args));
            return this;
        }

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.copyOf(arguments);
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_CALL_FN;
        }

        @Override
        public String asString() {
            return function + arguments.stream()
                                       .map(ParsedTemplateNode::asString)
                                       .collect(Collectors.joining(", ", "(", ")"));
        }

        @Override
        public Expression compile() {
            return new Expression.Call(
                function,
                arguments.stream().map(ExpressionNode::compile).collect(Collectors.toUnmodifiableList())
            );
        }
    }

    public static abstract class EntityList<T extends EntityList<T>> extends ExpressionNode implements EntityNode.EntityBlockBase<T> {
        public final List<EntityNode> entities = new ArrayList<>();

        protected abstract ExecutionType executionType();

        @Override
        protected List<ParsedTemplateNode> childList() {
            return List.copyOf(entities);
        }

        @Override
        public List<EntityNode> entities() {
            return entities;
        }

        @Override
        public Expression compile() {
            return new Expression.Execute(compileToInstructions(), executionType());
        }

        @Override
        public String asString() {
            return listString();
        }
    }

    public static Object object() {
        return new Object();
    }

    public static Object object(JsonNode node) {
        return object().appendFrom(node);
    }

    public static Object object(ParsedTemplateNode... entities) {
        return object().append(entities);
    }

    public static Object object(Collection<? extends ParsedTemplateNode> entities) {
        return object().append(entities);
    }

    public static class Object extends EntityList<Object> {
        @Override
        protected ExecutionType executionType() {
            return ExecutionType.OBJECT;
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_OBJECT;
        }

        @Override
        public String asString() {
            return "{ " + listString() + " }";
        }
    }

    public static Array array() {
        return new Array();
    }

    public static Array array(ParsedTemplateNode... entities) {
        return array().append(entities);
    }

    public static Array array(Collection<? extends ParsedTemplateNode> entities) {
        return array().append(entities);
    }

    public static class Array extends EntityList<Array> {
        @Override
        protected ExecutionType executionType() {
            return ExecutionType.ARRAY;
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_ARRAY;
        }

        @Override
        public String asString() {
            return "[ " + listString() + " ]";
        }
    }

    public static Subtemplate subtemplate() {
        return new Subtemplate();
    }

    public static Subtemplate subtemplate(ExpressionNode... entities) {
        return subtemplate().append(entities);
    }

    public static Subtemplate subtemplate(ParsedTemplateNode... entities) {
        return subtemplate().append(entities);
    }

    public static Subtemplate subtemplate(Collection<? extends ParsedTemplateNode> entities) {
        return subtemplate().append(entities);
    }

    public static class Subtemplate extends EntityList<Subtemplate> {
        @Override
        protected ExecutionType executionType() {
            return ExecutionType.ROOT;
        }

        @Override
        public NodeType type() {
            return NodeType.EXPR_SUBTEMPLATE;
        }

        @Override
        public String asString() {
            return "gen { " + listString() + " }";
        }
    }
}
