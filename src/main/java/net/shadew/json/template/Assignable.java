package net.shadew.json.template;

import net.shadew.json.JsonNode;

public interface Assignable {
    void assign(TemplateContext context, Vfl vfl, JsonNode node);
    String writeDebug();

    class Variable implements Assignable {
        private final String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public void assign(TemplateContext context, Vfl vfl, JsonNode node) {
            vfl.set(name, node);
        }

        @Override
        public String writeDebug() {
            return "variable(" + name + ")";
        }
    }

    class Member implements Assignable {
        private final Expression parent;
        private final String member;

        public Member(Expression parent, String member) {
            this.parent = parent;
            this.member = member;
        }

        @Override
        public void assign(TemplateContext context, Vfl vfl, JsonNode node) {
            JsonNode n = context.evaluate(parent);
            Operators.fieldSet(n, member, node);
        }

        @Override
        public String writeDebug() {
            return "member(" + parent.writeDebug() + ", " + member + ")";
        }
    }

    class Index implements Assignable {
        private final Expression parent;
        private final Expression index;

        public Index(Expression parent, Expression index) {
            this.parent = parent;
            this.index = index;
        }

        @Override
        public void assign(TemplateContext context, Vfl vfl, JsonNode node) {
            JsonNode n = context.evaluate(parent);
            JsonNode i = context.evaluate(index);
            Operators.indexSet(n, i, node);
        }

        @Override
        public String writeDebug() {
            return "index(" + parent.writeDebug() + ", " + index.writeDebug() + ")";
        }
    }
}
