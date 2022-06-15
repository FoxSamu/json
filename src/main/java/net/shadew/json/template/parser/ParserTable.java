package net.shadew.json.template.parser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.json.template.TemplateDebug;

import static net.shadew.json.template.parser.Grammar.*;

public class ParserTable {
    private final State[] states;

    private ParserTable(State[] states) {
        this.states = states;
    }

    public State state(int id) {
        return states[id];
    }

    public Action action(int state, TokenType terminal) {
        return states[state].action(terminal);
    }

    public int goTo(int state, Nonterminal nonterminal) {
        return states[state].goTo(nonterminal);
    }

    public static class State {
        private final Action[] actionTable = new Action[TokenType.AMOUNT];
        private final int[] gotoTable = new int[Nonterminal.AMOUNT];

        public State() {
            Arrays.fill(gotoTable, -1);
        }

        public void setAction(TokenType terminal, Action action) {
            actionTable[terminal.ordinal()] = action;
        }

        public void setGoTo(Nonterminal nonterminal, int goTo) {
            gotoTable[nonterminal.index()] = goTo;
        }

        public Action action(TokenType terminal) {
            return actionTable[terminal.ordinal()];
        }

        public int goTo(Nonterminal nonterminal) {
            return gotoTable[nonterminal.index()];
        }
    }

    public interface Action {
        void accept(TemplateParser parser);
        String name();
    }

    public static Action shift(int state) {
        return new Action() {
            @Override
            public void accept(TemplateParser parser) {
                parser.shift(state);
            }

            @Override
            public String name() {
                return "shift(" + state + ")";
            }
        };
    }

    public static Action reduce(Rule rule) {
        return new Action() {
            @Override
            public void accept(TemplateParser parser) {
                parser.reduce(rule);
            }

            @Override
            public String name() {
                return "reduce(" + rule.toString() + ")";
            }
        };
    }

    public static Action accept() {
        return new Action() {
            @Override
            public void accept(TemplateParser parser) {
                parser.accept();
            }

            @Override
            public String name() {
                return "accept";
            }
        };
    }

    // https://en.wikipedia.org/wiki/LR_parser#Table_construction
    public static ParserTable generate(Grammar grammar) {
        class TransitionTableRow {
            final ItemSet itemSet;
            final Map<GrammarSymbol, Integer> transitions = new HashMap<>();

            TransitionTableRow(ItemSet itemSet) {
                this.itemSet = itemSet;
            }

            void transition(GrammarSymbol type, int to) {
                transitions.put(type, to);
            }

            int transition(GrammarSymbol type) {
                return transitions.getOrDefault(type, -1);
            }

            private String transitionString(GrammarSymbol type) {
                int t = transition(type);
                if (t < 0) return "___";
                return String.format("% 3d", t);
            }

            @Override
            public String toString() {
                return Stream.concat(
                    grammar.terminals.stream().map(t -> t.ruleDefinitionName() + " -> " + transitionString(t)),
                    grammar.nonterminals.stream().map(t -> t.ruleDefinitionName() + " -> " + transitionString(t))
                ).collect(Collectors.joining("; "));
            }
        }

        Set<GrammarSymbol> nextReachable = new LinkedHashSet<>();
        List<TransitionTableRow> transitionTable = new ArrayList<>();
        List<ItemSet> setsToProcess = new ArrayList<>();
        Map<ItemSet, Integer> processed = new HashMap<>();

        ItemSet withNext = new ItemSet();
        ItemSet nextUnclosured = new ItemSet();

        setsToProcess.add(grammar.closure(grammar.item(grammar.mainRule, 0)));

        int index = 0;
        while (!setsToProcess.isEmpty()) {
            ItemSet current = setsToProcess.remove(0);
            TransitionTableRow row = new TransitionTableRow(current);

            getNextReachable(current, nextReachable);

            for (GrammarSymbol type : nextReachable) {

                withNext.clear();
                nextUnclosured.clear();
                current.findAllWithNext(type, withNext);
                for (Item item : withNext) {
                    nextUnclosured.add(grammar.rightSibling(item));
                }

                ItemSet next = grammar.closure(nextUnclosured);
                int i;
                if (processed.containsKey(next)) {
                    i = processed.get(next);
                } else {
                    i = ++index;
                    processed.put(next, i);
                    setsToProcess.add(next);
                }

                row.transition(type, i);
            }

            transitionTable.add(row);
        }

        if (TemplateDebug.printTransitionTable) {
            System.out.println("Transition table report");

            int i = 0;
            for (TransitionTableRow r : transitionTable) {
                System.out.printf("% 4d | %s | %s%n", i, r, r.itemSet);
                i++;
            }
        }

        List<State> states = new ArrayList<>();

        int i = 0;
        for (TransitionTableRow row : transitionTable) {
            //System.out.printf("% 4d | %s   SET: %s%n", i, row, row.itemSet);

            State state = new State();
            for (Map.Entry<GrammarSymbol, Integer> e : row.transitions.entrySet()) {
                GrammarSymbol type = e.getKey();
                int then = e.getValue();

                if (type.isTerminal()) {
                    state.setAction(type.terminal(), shift(then));
                } else {
                    state.setGoTo(type.nonterminal(), then);
                }
            }

            // Can generate accept
            if (row.itemSet.contains(item -> item.lhs == NonterminalType.PARSER_START && item.next == TokenType.EOF)) {
                state.setAction(TokenType.EOF, accept());
            }

            Item reducable = row.itemSet.first(item -> item.next == null);
            if (reducable != null) {
                Set<TokenType> followSet = grammar.followSets.get(reducable.lhs);
                Action reduce = reduce(reducable.rule);
                for (TokenType involved : grammar.terminals) {
                    if (followSet.contains(involved)) {
                        state.setAction(involved, reduce);
                    }
                }
            }

            if (TemplateDebug.printParserTable) {
                System.out.printf(
                    "% 4d | %s%n", i,
                    Stream.concat(
                        grammar.terminals.stream().map(t -> {
                            Action a = state.action(t);
                            if (a == null) return t.ruleDefinitionName() + " -> ---";
                            return t.ruleDefinitionName() + " -> " + state.action(t).name();
                        }),
                        grammar.nonterminals.stream().map(t -> t.ruleDefinitionName() + " -> " + state.goTo(t))
                    ).collect(Collectors.joining("; "))
                );
            }
            states.add(state);

            i++;
        }

        return new ParserTable(states.toArray(State[]::new));
    }

    private static void getNextReachable(Collection<Item> items, Set<GrammarSymbol> out) {
        out.clear();

        for (Item item : items) {
            GrammarSymbol type = item.next;
            if (type != null) {
                out.add(type);
            }
        }
    }
}
