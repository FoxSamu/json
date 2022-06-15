package net.shadew.json.template.parser;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.json.template.TemplateDebug;

public class Grammar {
    // Rules in this grammar
    public final List<Rule> rules; // All together
    public final Rule mainRule; // Main
    public final Map<Nonterminal, List<Rule>> rulesByNt; // By nonterminal

    // Items in this grammar ('R := A B' --> 'R := :: A B', 'R := A :: B', 'R := A B ::')
    public final List<Item> items; // All together
    public final List<Item> mainItems; // Main
    public final Map<Nonterminal, List<Item>> itemsByNt; // By nonterminal
    public final Map<Rule, List<Item>> itemsByRule; // By rule

    public final Set<Nonterminal> nonterminals;
    public final Set<TokenType> terminals;

    // FIRST and FOLLOW sets of the nonterminals present in this grammar
    public final Map<Nonterminal, Set<TokenType>> firstSets;
    public final Map<Nonterminal, Set<TokenType>> followSets;

    public Grammar(List<Rule> rules, GrammarSymbol main) {
        this.rules = Stream.concat(
            // Add main rule S := MAIN [eof]
            Stream.of(mainRule = rule(NonterminalType.PARSER_START, main, TokenType.EOF).reduce2((m, eof) -> m)),
            rules.stream()
        ).collect(Collectors.toUnmodifiableList());

        // Sort rules by nonterminal and find all used nonterminals and terminals
        Map<Nonterminal, List<Rule>> rulesByType = new HashMap<>();
        Set<Nonterminal> nonterminals = new HashSet<>();
        Set<TokenType> terminals = new HashSet<>();
        for (Rule rule : this.rules) {
            nonterminals.add(rule.lhs);
            for (GrammarSymbol rhs : rule.rhs) {
                if (rhs.isTerminal()) terminals.add(rhs.terminal());
                else nonterminals.add(rhs.nonterminal());
            }

            rulesByType.computeIfAbsent(rule.lhs, k -> new ArrayList<>()).add(rule);
        }
        for (Map.Entry<Nonterminal, List<Rule>> entry : rulesByType.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue()));
        }
        this.rulesByNt = Map.copyOf(rulesByType);
        this.nonterminals = Set.copyOf(nonterminals);
        this.terminals = Set.copyOf(terminals);

        // Generate items
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

        // Sort items by nonterminal
        Map<Nonterminal, List<Item>> itemsByType = new HashMap<>();
        for (Item item : items) {
            itemsByType.computeIfAbsent(item.lhs, k -> new ArrayList<>()).add(item);
        }
        for (Map.Entry<Nonterminal, List<Item>> entry : itemsByType.entrySet()) {
            entry.setValue(List.copyOf(entry.getValue()));
        }
        this.itemsByNt = Map.copyOf(itemsByType);

        // Generate FIRST/FOLLOW sets
        FirstFollowSetsGenerator gen = new FirstFollowSetsGenerator(this);
        gen.computeFirstSets();
        gen.computeFollowSets();
        if (TemplateDebug.printFirstFollowSets) {
            gen.debug();
        }

        Map<Nonterminal, Set<TokenType>> firstSets = new HashMap<>();
        Map<Nonterminal, Set<TokenType>> followSets = new HashMap<>();
        gen.firstSets.forEach((k, v) -> firstSets.put(k, Set.copyOf(v)));
        gen.followSets.forEach((k, v) -> followSets.put(k, Set.copyOf(v)));
        this.firstSets = Map.copyOf(firstSets);
        this.followSets = Map.copyOf(followSets);
    }

    public ItemSet closure(Collection<Item> base) {
        List<Item> unprocessed = new ArrayList<>(base);
        ItemSet set = new ItemSet();

        while (!unprocessed.isEmpty()) {
            Item item = unprocessed.remove(0);
            if (!set.contains(item)) {
                set.add(item);

                if (item.next != null) {
                    GrammarSymbol nextType = item.next;
                    if (!nextType.isTerminal()) {
                        List<Item> items = itemsByNt.get(nextType.nonterminal());
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

        public Grammar build(Nonterminal main) {
            return new Grammar(rules, main);
        }
    }

    public static RuleBuilder rule(Nonterminal nonterminal) {
        return new RuleBuilder(nonterminal);
    }

    public static RuleBuilder rule(Nonterminal nonterminal, GrammarSymbol... types) {
        return new RuleBuilder(nonterminal).then(types);
    }

    public static RuleBuilder rule(Nonterminal nonterminal, String def) {
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

        public void findAllWithNext(GrammarSymbol next, ItemSet result) {
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
        public final Nonterminal lhs;
        public final Rule rule;
        public final int statePos;
        public final GrammarSymbol next;

        public Item(Rule rule, int statePos) {
            this.lhs = rule.lhs;
            this.rule = rule;
            this.statePos = statePos;

            if (statePos < rule.size) {
                next = rule.rhs.get(statePos);
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
        public final Nonterminal lhs;
        public final List<GrammarSymbol> rhs;
        public final int size;
        public final Reduction reduction;

        public Rule(Nonterminal lhs, List<GrammarSymbol> rhs, Reduction reduction) {
            this.lhs = lhs;
            this.rhs = List.copyOf(rhs);
            this.size = rhs.size();
            this.reduction = reduction;
        }

        @Override
        public String toString() {
            return lhs.ruleDefinitionName() + " := "
                       + rhs.stream().map(GrammarSymbol::ruleDefinitionName).collect(Collectors.joining(" "));
        }

        public String toStringWithStatePos(int statePos) {
            StringBuilder builder = new StringBuilder(lhs.ruleDefinitionName());
            builder.append(" :=");

            int i = 0;
            for (GrammarSymbol type : rhs) {
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
            return lhs == rule.lhs && rhs.equals(rule.rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(lhs, rhs);
        }
    }

    public static class RuleBuilder {
        public final Nonterminal nonterminal;
        private final List<GrammarSymbol> definition = new ArrayList<>();

        public RuleBuilder(Nonterminal nonterminal) {
            this.nonterminal = nonterminal;
        }

        public RuleBuilder then(GrammarSymbol... def) {
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

    private static final Map<String, GrammarSymbol> DEF_NAMES;

    static {
        Map<String, GrammarSymbol> defNames = new HashMap<>();
        for (TokenType type : TokenType.values()) {
            defNames.put(type.ruleDefinitionName(), type);
        }
        for (NodeType type : NodeType.values()) {
            defNames.put(type.ruleDefinitionName(), type);
        }
        for (NonterminalType type : NonterminalType.values()) {
            defNames.put(type.ruleDefinitionName(), type);
        }
        DEF_NAMES = Map.copyOf(defNames);
    }

    private static GrammarSymbol[] parseDefinition(String str) {
        String[] parts = str.trim().split(" +");

        GrammarSymbol[] types = new GrammarSymbol[parts.length];
        int i = 0;
        for (String part : parts) {
            GrammarSymbol type = DEF_NAMES.get(part);
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

    private static class FirstFollowSetsGenerator {
        final Grammar grammar;

        final Map<Nonterminal, Set<TokenType>> firstSets = new LinkedHashMap<>();
        final Map<List<GrammarSymbol>, Set<TokenType>> firstSetsW = new LinkedHashMap<>();
        final Map<Nonterminal, Set<TokenType>> followSets = new LinkedHashMap<>();

        boolean firstChanged = false;
        boolean followChanged = false;

        FirstFollowSetsGenerator(Grammar grammar) {
            this.grammar = grammar;
        }

        Set<TokenType> firstSet(Nonterminal nt) {
            return firstSets.computeIfAbsent(nt, k -> new LinkedHashSet<>());
        }

        Set<TokenType> firstSet(List<GrammarSymbol> r) {
            return firstSetsW.computeIfAbsent(r, k -> new LinkedHashSet<>());
        }

        Set<TokenType> followSet(Nonterminal nt) {
            return followSets.computeIfAbsent(nt, k -> new LinkedHashSet<>());
        }

        Set<TokenType> computeFirstSet(GrammarSymbol sym) {
            if (sym.isTerminal()) {
                return Set.of(sym.terminal());
            } else {
                Nonterminal nt = sym.nonterminal();
                Set<TokenType> set = firstSet(nt);

                for (Rule rule : grammar.rulesByNt.get(nt)) {
                    firstChanged |= set.addAll(firstSet(rule.rhs));
                }

                return set;
            }
        }

        Set<TokenType> computeFirstSet(List<GrammarSymbol> rule) {
            Set<TokenType> set = firstSet(rule);
            if (rule.size() == 0) { // R := epsilon
                firstChanged |= set.add(null); // We represent epsilon by 'null'
            } else {
                GrammarSymbol fst = rule.get(0);
                if (fst.isTerminal()) { // R := terminal R'
                    firstChanged |= set.add(fst.terminal());
                } else { // R := NONTERMINAL R'
                    Nonterminal nt = fst.nonterminal();
                    Set<TokenType> collectiveFirstSet = firstSet(nt);

                    if (collectiveFirstSet.contains(null)) {
                        // (Fi(A) \ EPS) U Fi(w')
                        collectiveFirstSet.remove(null);

                        for (TokenType type : collectiveFirstSet)
                            if (type != null) firstChanged |= set.add(type);

                        GrammarSymbol snd = rule.get(1);
                        if (snd.isTerminal()) {
                            firstChanged |= set.add(snd.terminal());
                        } else {
                            firstChanged |= set.addAll(firstSet(nt));
                        }
                    } else {
                        // Fi(A)
                        firstChanged |= set.addAll(collectiveFirstSet);
                    }
                }
            }

            return set;
        }

        void contributeToFollowSets(Rule rule) {
            Nonterminal aj = rule.lhs;
            Set<TokenType> foaj = followSet(aj);

            List<GrammarSymbol> def = rule.rhs;
            for (int i = 0, l = rule.size; i < l; i++) {
                GrammarSymbol sym = def.get(i);

                if (!sym.isTerminal()) {
                    Nonterminal ai = sym.nonterminal();
                    Set<TokenType> foai = followSet(ai);

                    List<GrammarSymbol> rem = List.copyOf(def.subList(i + 1, l));

                    if (rem.isEmpty()) {
                        followChanged |= foai.addAll(foaj);
                    } else {
                        Set<TokenType> first = computeFirstSet(rem);

                        for (TokenType f : first)
                            if (f != null) followChanged |= foai.add(f);

                        if (foai.contains(null))
                            followChanged |= foai.addAll(foaj);
                    }
                }
            }
        }

        void computeFirstSets() {
            do {
                firstChanged = false;
                for (Map.Entry<Nonterminal, List<Rule>> e : grammar.rulesByNt.entrySet()) {
                    for (Rule rule : e.getValue()) {
                        computeFirstSet(rule.rhs);
                    }
                    computeFirstSet(e.getKey());
                }
            } while (firstChanged);
        }

        void computeFollowSets() {
            followSet(NonterminalType.PARSER_START).add(TokenType.EOF);
            do {
                followChanged = false;
                for (Rule rule : grammar.rules) {
                    contributeToFollowSets(rule);
                }
            } while (followChanged);
        }

        void debug() {
            System.out.println("FIRST/FOLLOW set report:");
            for (Map.Entry<Nonterminal, Set<TokenType>> firstSets : firstSets.entrySet()) {
                System.out.println("- FIRST(" + firstSets.getKey() + "): " + firstSets.getValue());
            }
            for (Map.Entry<Nonterminal, Set<TokenType>> followSets : followSets.entrySet()) {
                System.out.println("- FOLLOW(" + followSets.getKey() + "): " + followSets.getValue());
            }
        }
    }
}
