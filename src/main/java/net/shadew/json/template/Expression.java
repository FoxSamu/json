package net.shadew.json.template;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import net.shadew.json.JsonNode;

public interface Expression {
    JsonNode evaluate(TemplateContext context, Vfl vfl);
    boolean isContextDependent();
    JsonNode simplifyToLiteral();
    String writeDebug();

    class Literal implements Expression {
        private final JsonNode node;

        public Literal(JsonNode node) {
            this.node = node;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            return node;
        }

        @Override
        public boolean isContextDependent() {
            return false;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            return node;
        }

        @Override
        public String writeDebug() {
            return "literal(" + node + ")";
        }
    }

    class Variable implements Expression {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            return vfl.get(name);
        }

        @Override
        public boolean isContextDependent() {
            return true;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            return null;
        }

        @Override
        public String writeDebug() {
            return "variable(" + name + ")";
        }
    }

    class Assign implements Expression {
        private final Assignable assignable;
        private final Expression value;

        public Assign(Assignable assignable, Expression value) {
            this.assignable = assignable;
            this.value = value;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            JsonNode val = context.evaluate(value);
            assignable.assign(context, vfl, val);
            return val;
        }

        @Override
        public boolean isContextDependent() {
            return true;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            return null;
        }

        @Override
        public String writeDebug() {
            return "assign(" + assignable.writeDebug() + ", " + value.writeDebug() + ")";
        }
    }

    class Increment implements Expression {
        private final Assignable set;
        private final Expression get;
        private final boolean decr;
        private final boolean postfix;

        public Increment(Assignable set, Expression get, boolean decr, boolean postfix) {
            this.set = set;
            this.get = get;
            this.decr = decr;
            this.postfix = postfix;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            JsonNode curr = context.evaluate(get);
            JsonNode next = Operators.incr(curr, decr);
            set.assign(context, vfl, next);
            return postfix ? curr : next;
        }

        @Override
        public boolean isContextDependent() {
            return true;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            return null;
        }

        @Override
        public String writeDebug() {
            return "increment(" + set.writeDebug() + (postfix ? ", postfix" : ", prefix") + (decr ? ", decrement" : ", increment") + ")";
        }
    }

    class Binary implements Expression {
        private final Expression a, b;
        private final BinaryOperator<JsonNode> operator;

        public Binary(Expression a, Expression b, BinaryOperator<JsonNode> operator) {
            this.a = a;
            this.b = b;
            this.operator = operator;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            return operator.apply(context.evaluate(a), context.evaluate(b));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent() || b.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            JsonNode na = a.simplifyToLiteral();
            JsonNode nb = b.simplifyToLiteral();
            if (na == null || nb == null)
                return null;
            return operator.apply(a.simplifyToLiteral(), b.simplifyToLiteral());
        }

        @Override
        public String writeDebug() {
            return "binaryOperator(" + a.writeDebug() + ", " + b.writeDebug() + ", " + operator + ")";
        }
    }

    class Unary implements Expression {
        private final Expression a;
        private final UnaryOperator<JsonNode> operator;

        public Unary(Expression a, UnaryOperator<JsonNode> operator) {
            this.a = a;
            this.operator = operator;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            return operator.apply(context.evaluate(a));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            JsonNode na = a.simplifyToLiteral();
            if (na == null)
                return null;
            return operator.apply(a.simplifyToLiteral());
        }

        @Override
        public String writeDebug() {
            return "unaryOperator(" + a.writeDebug() + ", " + operator + ")";
        }
    }

    class Ternary implements Expression {
        private final Expression a;
        private final Expression b;
        private final Expression c;
        private final TernaryOperator operator;

        public Ternary(Expression a, Expression b, Expression c, TernaryOperator operator) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.operator = operator;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            return operator.apply(context.evaluate(a), context.evaluate(b), context.evaluate(c));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent() || b.isContextDependent() || c.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            JsonNode na = a.simplifyToLiteral();
            JsonNode nb = b.simplifyToLiteral();
            JsonNode nc = c.simplifyToLiteral();
            if (na == null || nb == null || nc == null)
                return null;
            return operator.apply(a.simplifyToLiteral(), b.simplifyToLiteral(), c.simplifyToLiteral());
        }

        @Override
        public String writeDebug() {
            return "ternaryOperator(" + a.writeDebug() + ", " + b.writeDebug() + ", " + c.writeDebug() + ", " + operator + ")";
        }
    }

    class Conjunction implements Expression {
        private final Expression a, b;

        public Conjunction(Expression a, Expression b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            JsonNode ae = context.evaluate(a);
            if (!Operators.truthy(ae))
                return JsonNode.FALSE;
            return JsonNode.bool(Operators.truthy(context.evaluate(b)));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent() || b.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            JsonNode na = a.simplifyToLiteral();
            if (na == null)
                return null;
            if (!Operators.truthy(na))
                return JsonNode.FALSE;

            JsonNode nb = b.simplifyToLiteral();
            if (nb == null)
                return null;
            return JsonNode.bool(Operators.truthy(nb));
        }

        @Override
        public String writeDebug() {
            return "conjunction(" + a.writeDebug() + ", " + b.writeDebug() + ")";
        }
    }

    class Disjunction implements Expression {
        private final Expression a, b;

        public Disjunction(Expression a, Expression b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            JsonNode ae = context.evaluate(a);
            if (Operators.truthy(ae))
                return ae;
            return context.evaluate(b);
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent() || b.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            JsonNode na = a.simplifyToLiteral();
            if (na == null)
                return null;
            if (Operators.truthy(na))
                return na;

            return b.simplifyToLiteral();
        }

        @Override
        public String writeDebug() {
            return "disjunction(" + a.writeDebug() + ", " + b.writeDebug() + ")";
        }
    }

    class EvaluateBeforeAfter implements Expression {
        private final Expression expr;
        private final List<Expression> evalBefore;
        private final List<Expression> evalAfter;

        public EvaluateBeforeAfter(Expression expr, List<Expression> evalBefore, List<Expression> evalAfter) {
            this.expr = expr;
            this.evalBefore = evalBefore;
            this.evalAfter = evalAfter;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            for (Expression e : evalBefore)
                context.evaluate(e);
            JsonNode result = context.evaluate(expr);
            for (Expression e : evalAfter)
                context.evaluate(e);
            return result;
        }

        @Override
        public boolean isContextDependent() {
            if (expr.isContextDependent())
                return true;
            for (Expression e : evalBefore)
                if (e.isContextDependent())
                    return true;
            for (Expression e : evalAfter)
                if (e.isContextDependent())
                    return true;
            return false;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            for (Expression e : evalBefore)
                if (e.simplifyToLiteral() == null)
                    return null;
            for (Expression e : evalAfter)
                if (e.simplifyToLiteral() == null)
                    return null;
            return expr.simplifyToLiteral();
        }

        @Override
        public String writeDebug() {
            return "evaluateBeforeAfter("
                       + expr
                       + ", before [" + evalBefore.stream().map(Expression::writeDebug).collect(Collectors.joining())
                       + ", after [" + evalAfter.stream().map(Expression::writeDebug).collect(Collectors.joining())
                       + "])";
        }
    }

    class ConditionalExpression {
        public final Expression condition;
        public final Expression expression;

        public ConditionalExpression(Expression condition, Expression expression) {
            this.condition = condition;
            this.expression = expression;
        }

        public String writeDebug() {
            return "conditional(" + condition.writeDebug() + ", " + expression.writeDebug() + ")";
        }
    }

    class MatchCondition implements Expression {
        private final List<ConditionalExpression> cases;
        private final Expression elseCase;

        public MatchCondition(List<ConditionalExpression> cases, Expression elseCase) {
            this.cases = cases;
            this.elseCase = elseCase;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            for (ConditionalExpression e : cases) {
                if (Operators.truthy(context.evaluate(e.condition)))
                    return context.evaluate(e.expression);
            }
            return context.evaluate(elseCase);
        }

        @Override
        public boolean isContextDependent() {
            for (ConditionalExpression e : cases) {
                if (e.condition.isContextDependent())
                    return true;
                if (e.expression.isContextDependent())
                    return true;
            }
            return elseCase.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            for (ConditionalExpression e : cases) {
                JsonNode node = e.condition.simplifyToLiteral();
                if (node == null)
                    return null;

                if (Operators.truthy(node))
                    return e.expression.simplifyToLiteral();
            }
            return elseCase.simplifyToLiteral();
        }

        @Override
        public String writeDebug() {
            return "matchCondition(" + cases.stream().map(ConditionalExpression::writeDebug).collect(Collectors.joining()) + ")";
        }
    }

    class MatchValue implements Expression {
        private final Expression value;
        private final List<ConditionalExpression> cases;
        private final Expression elseCase;

        public MatchValue(Expression value, List<ConditionalExpression> cases, Expression elseCase) {
            this.value = value;
            this.cases = cases;
            this.elseCase = elseCase;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            JsonNode toMatch = context.evaluate(value);
            for (ConditionalExpression e : cases) {
                if (toMatch.equals(context.evaluate(e.condition)))
                    return context.evaluate(e.expression);
            }
            return context.evaluate(elseCase);
        }

        @Override
        public boolean isContextDependent() {
            if (value.isContextDependent())
                return true;
            for (ConditionalExpression e : cases) {
                if (e.condition.isContextDependent())
                    return true;
                if (e.expression.isContextDependent())
                    return true;
            }
            return elseCase.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            JsonNode toMatch = value.simplifyToLiteral();
            if (toMatch == null)
                return null;
            for (ConditionalExpression e : cases) {
                JsonNode node = e.condition.simplifyToLiteral();
                if (node == null)
                    return null;

                if (toMatch.equals(node))
                    return e.expression.simplifyToLiteral();
            }
            return elseCase.simplifyToLiteral();
        }

        @Override
        public String writeDebug() {
            return "matchValue(" + value.writeDebug()
                       + ", " + cases.stream().map(ConditionalExpression::writeDebug).collect(Collectors.joining()) + ")";
        }
    }

    class Execute implements Expression {
        private final Instructions insns;
        private final ExecutionType type;

        public Execute(Instructions insns, ExecutionType type) {
            this.insns = insns;
            this.type = type;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            return context.evaluate(insns, type);
        }

        @Override
        public boolean isContextDependent() {
            return true;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            return null;
        }

        @Override
        public String writeDebug() {
            return "execute(" + type + ")";
        }
    }

    class Call implements Expression {
        private final String function;
        private final List<Expression> arguments;

        public Call(String function, List<Expression> arguments) {
            this.function = function;
            this.arguments = arguments;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, Vfl vfl) {
            JsonNode[] args = arguments.stream().map(context::evaluate).toArray(JsonNode[]::new);
            return vfl.call(function, args);
        }

        @Override
        public boolean isContextDependent() {
            return true;
        }

        @Override
        public JsonNode simplifyToLiteral() {
            return null;
        }

        @Override
        public String writeDebug() {
            return "call(" + function + "(" + arguments.stream().map(Expression::writeDebug).collect(Collectors.joining()) + "))";
        }
    }

    interface TernaryOperator {
        JsonNode apply(JsonNode a, JsonNode b, JsonNode c);
    }
}
