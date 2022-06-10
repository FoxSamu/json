package net.shadew.json.template;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import net.shadew.json.JsonNode;

public interface Expression {
    JsonNode evaluate(TemplateContext context, VariableFunctionLayer vfl);
    boolean isContextDependent();
    JsonNode simplifyToLiteral();

    class Literal implements Expression {
        private final JsonNode node;

        public Literal(JsonNode node) {
            this.node = node;
        }

        @Override
        public JsonNode evaluate(TemplateContext context, VariableFunctionLayer vfl) {
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
        public JsonNode evaluate(TemplateContext context, VariableFunctionLayer vfl) {
            return operator.apply(context.evaluate(a), context.evaluate(b));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent() || b.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            if (!isContextDependent()) {
                JsonNode na = a.simplifyToLiteral();
                JsonNode nb = b.simplifyToLiteral();
                if (na == null || nb == null)
                    return null;
                return operator.apply(a.simplifyToLiteral(), b.simplifyToLiteral());
            }
            return null;
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
        public JsonNode evaluate(TemplateContext context, VariableFunctionLayer vfl) {
            return operator.apply(context.evaluate(a));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            if (!isContextDependent()) {
                JsonNode na = a.simplifyToLiteral();
                if (na == null)
                    return null;
                return operator.apply(a.simplifyToLiteral());
            }
            return null;
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
        public JsonNode evaluate(TemplateContext context, VariableFunctionLayer vfl) {
            return operator.apply(context.evaluate(a), context.evaluate(b), context.evaluate(c));
        }

        @Override
        public boolean isContextDependent() {
            return a.isContextDependent() || b.isContextDependent() || c.isContextDependent();
        }

        @Override
        public JsonNode simplifyToLiteral() {
            if (!isContextDependent()) {
                JsonNode na = a.simplifyToLiteral();
                JsonNode nb = b.simplifyToLiteral();
                JsonNode nc = c.simplifyToLiteral();
                if (na == null || nb == null || nc == null)
                    return null;
                return operator.apply(a.simplifyToLiteral(), b.simplifyToLiteral(), c.simplifyToLiteral());
            }
            return null;
        }
    }
}
