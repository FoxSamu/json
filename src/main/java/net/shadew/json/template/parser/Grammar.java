package net.shadew.json.template.parser;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grammar {
    public final Rule mainRule;
    public final List<Rule> rules;
    public final Map<NonterminalType, List<Rule>> rulesByType;

    public final List<Item> items;
    public final List<Item> mainItems;
    public final Map<NonterminalType, List<Item>> itemsByType;

    public final Map<Rule, List<Item>> itemsByRule;

    public Grammar(List<Rule> rules, ParserNodeType main) {
        this.rules = Stream.concat(
            // Add main rule S := MAIN [eof]
            Stream.of(mainRule = rule(Nonterminal.PARSER_START, main, TokenType.EOF).reduce2((m, eof) -> m)),
            rules.stream()
        ).collect(Collectors.toUnmodifiableList());

        Map<NonterminalType, List<Rule>> rulesByType = new HashMap<>();
        for (Rule rule : this.rules) {
            if (rule.nonterminal != null)
                rulesByType.computeIfAbsent(rule.nonterminal, k -> new ArrayList<>()).add(rule);
        }
        for (Map.Entry<NonterminalType, List<Rule>> entry : rulesByType.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue()));
        }
        this.rulesByType = Map.copyOf(rulesByType);

        Map<Rule, List<Item>> itemsByRule = new HashMap<>();
        List<Item> items = new ArrayList<>();
        for (Rule rule : this.rules) {
            List<Item> ruleItems = new ArrayList<>();
            int len = rule.size;
            for (int i = 0; i <= len; i++) {
                Item item = new Item(rule, i);
                items.add(item);
                ruleItems.add(item);
            }
            itemsByRule.put(rule, List.copyOf(ruleItems));
        }
        this.items = List.copyOf(items);
        this.itemsByRule = Map.copyOf(itemsByRule);
        this.mainItems = itemsByRule.get(mainRule);

        Map<NonterminalType, List<Item>> itemsByType = new HashMap<>();
        for (Item item : items) {
            itemsByType.computeIfAbsent(item.nonterminal, k -> new ArrayList<>()).add(item);
        }
        for (Map.Entry<NonterminalType, List<Item>> entry : itemsByType.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue()));
        }
        this.itemsByType = Map.copyOf(itemsByType);
    }

    public ItemSet closure(Collection<Item> base) {
        List<Item> unprocessed = new ArrayList<>(base);
        ItemSet set = new ItemSet();

        while (!unprocessed.isEmpty()) {
            Item item = unprocessed.remove(0);
            if (!set.contains(item)) {
                set.add(item);

                if (item.next != null) {
                    ParserNodeType nextType = item.next;
                    if (!nextType.isTerminal()) {
                        List<Item> items = itemsByType.get(nextType.nonterminal());
                        for (Item add : items) {
                            if (add.statePos == 0)
                                unprocessed.add(add);
                        }
                    }
                }
            }
        }

        return set;
    }

    public ItemSet closure(Item... base) {
        return closure(Set.of(base));
    }

    public Item item(Rule rule, int pos) {
        return itemsByRule.get(rule).get(pos);
    }

    public Item rightSibling(Item item) {
        if (item.next == null)
            return null;
        return item(item.rule, item.statePos + 1);
    }

    public static class Builder {
        private final List<Rule> rules = new ArrayList<>();

        public Builder rule(Rule rule) {
            rules.add(rule);
            return this;
        }

        public Builder rule(Rule... rule) {
            rules.addAll(Arrays.asList(rule));
            return this;
        }

        public Grammar build(NonterminalType main) {
            return new Grammar(rules, main);
        }
    }

    public static RuleBuilder rule(NonterminalType nonterminal) {
        return new RuleBuilder(nonterminal);
    }

    public static RuleBuilder rule(NonterminalType nonterminal, ParserNodeType... types) {
        return new RuleBuilder(nonterminal).then(types);
    }

    public static RuleBuilder rule(NonterminalType nonterminal, String def) {
        return new RuleBuilder(nonterminal).then(parseDefinition(def));
    }

    public static RuleBuilder rule(String def) {
        return parseRule(def);
    }

    public static class ItemSet extends LinkedHashSet<Item> {
        public void findAll(Predicate<Item> test, ItemSet result) {
            for (Item item : this)
                if (test.test(item))
                    result.add(item);
        }

        public void findAllWithNext(ParserNodeType next, ItemSet result) {
            findAll(item -> item.next == next, result);
        }

        public boolean contains(Predicate<Item> test) {
            for (Item item : this)
                if (test.test(item))
                    return true;
            return false;
        }

        public Item first(Predicate<Item> test) {
            for (Item item : this)
                if (test.test(item))
                    return item;
            return null;
        }
    }

    public static class Item {
        public final NonterminalType nonterminal;
        public final Rule rule;
        public final int statePos;
        public final ParserNodeType next;

        public Item(Rule rule, int statePos) {
            this.nonterminal = rule.nonterminal;
            this.rule = rule;
            this.statePos = statePos;

            if (statePos < rule.size) {
                next = rule.definition.get(statePos);
            } else {
                next = null;
            }
        }

        @Override
        public String toString() {
            return rule.toStringWithStatePos(statePos);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return statePos == item.statePos && rule.equals(item.rule);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rule, statePos);
        }
    }

    public static class Rule {
        public final NonterminalType nonterminal;
        public final List<ParserNodeType> definition;
        public final int size;
        public final Reduction reduction;

        public Rule(NonterminalType nonterminal, List<ParserNodeType> definition, Reduction reduction) {
            this.nonterminal = nonterminal;
            this.definition = List.copyOf(definition);
            this.size = definition.size();
            this.reduction = reduction;
        }

        @Override
        public String toString() {
            return nonterminal.ruleDefinitionName() + " := "
                       + definition.stream().map(ParserNodeType::ruleDefinitionName).collect(Collectors.joining(" "));
        }

        public String toStringWithStatePos(int statePos) {
            StringBuilder builder = new StringBuilder(nonterminal.ruleDefinitionName());
            builder.append(" :=");

            int i = 0;
            for (ParserNodeType type : definition) {
                if (i == statePos)
                    builder.append(" ::");
                builder.append(" ").append(type.ruleDefinitionName());
                i++;
            }
            if (i == statePos)
                builder.append(" ::");

            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rule rule = (Rule) o;
            return nonterminal == rule.nonterminal && definition.equals(rule.definition);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nonterminal, definition);
        }
    }

    public static class RuleBuilder {
        public final NonterminalType nonterminal;
        private final List<ParserNodeType> definition = new ArrayList<>();

        public RuleBuilder(NonterminalType nonterminal) {
            this.nonterminal = nonterminal;
        }

        public RuleBuilder then(ParserNodeType... def) {
            definition.addAll(Arrays.asList(def));
            return this;
        }

        public RuleBuilder then(String def) {
            return then(parseDefinition(def));
        }

        public Rule reduce(Reduction reduction) {
            if (reduction.amount() != definition.size())
                throw new IllegalArgumentException("Reduction amount mismatch; got " + reduction.amount() + " but expected " + definition.size());
            return new Rule(nonterminal, definition, reduction);
        }

        public Rule reduce(Reduction.Def reduction) {
            int s = definition.size();
            return reduce(new Reduction() {
                @Override
                public ParserNode reduce(ParserNode... nodes) {
                    return reduction.reduce(nodes);
                }

                @Override
                public int amount() {
                    return s;
                }
            });
        }

        public Rule reduce1(Reduction.Def1 reduction) {
            return reduce(reduction);
        }

        public Rule reduce2(Reduction.Def2 reduction) {
            return reduce(reduction);
        }

        public Rule reduce3(Reduction.Def3 reduction) {
            return reduce(reduction);
        }

        public Rule reduce4(Reduction.Def4 reduction) {
            return reduce(reduction);
        }

        public Rule reduce5(Reduction.Def5 reduction) {
            return reduce(reduction);
        }
    }

    private static final Map<String, ParserNodeType> DEF_NAMES;

    static {
        Map<String, ParserNodeType> defNames = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            defNames.put(type.ruleDefinitionName(), type);
        }
        for (NodeType type : NodeType.values()) {
            defNames.put(type.ruleDefinitionName(), type);
        }
        for (Nonterminal type : Nonterminal.values()) {
            defNames.put(type.ruleDefinitionName(), type);
        }
        DEF_NAMES = Map.copyOf(defNames);
    }

    private static ParserNodeType[] parseDefinition(String str) {
        String[] parts = str.trim().split(" +");

        ParserNodeType[] types = new ParserNodeType[parts.length];
        int i = 0;
        for (String part : parts) {
            ParserNodeType type = DEF_NAMES.get(part);
            if (type == null)
                throw new IllegalArgumentException("No such terminal or non-terminal: " + part);
            types[i++] = type;
        }
        return types;
    }

    private static RuleBuilder parseRule(String str) {
        String[] parts = str.split(":=", 2);

        NodeType type = NodeType.valueOf(parts[0].trim());
        return new RuleBuilder(type).then(parseDefinition(parts[1]));
    }
}
