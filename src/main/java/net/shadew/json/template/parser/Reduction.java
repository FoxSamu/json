package net.shadew.json.template.parser;

public interface Reduction {
    ParserNode reduce(ParserNode... nodes);

    int amount();

    interface Def {
        ParserNode reduce(ParserNode... nodes);
    }

    interface Def1 extends Reduction {
        ParserNode reduce(ParserNode a);

        @Override
        default ParserNode reduce(ParserNode... nodes) {
            return reduce(nodes[0]);
        }

        @Override
        default int amount() {
            return 1;
        }
    }

    interface Def2 extends Reduction {
        ParserNode reduce(ParserNode a, ParserNode b);

        @Override
        default ParserNode reduce(ParserNode... nodes) {
            return reduce(nodes[0], nodes[1]);
        }

        @Override
        default int amount() {
            return 2;
        }
    }

    interface Def3 extends Reduction {
        ParserNode reduce(ParserNode a, ParserNode b, ParserNode c);

        @Override
        default ParserNode reduce(ParserNode... nodes) {
            return reduce(nodes[0], nodes[1], nodes[2]);
        }

        @Override
        default int amount() {
            return 3;
        }
    }

    interface Def4 extends Reduction {
        ParserNode reduce(ParserNode a, ParserNode b, ParserNode c, ParserNode d);

        @Override
        default ParserNode reduce(ParserNode... nodes) {
            return reduce(nodes[0], nodes[1], nodes[2], nodes[3]);
        }

        @Override
        default int amount() {
            return 4;
        }
    }

    interface Def5 extends Reduction {
        ParserNode reduce(ParserNode a, ParserNode b, ParserNode c, ParserNode d, ParserNode e);

        @Override
        default ParserNode reduce(ParserNode... nodes) {
            return reduce(nodes[0], nodes[1], nodes[2], nodes[3], nodes[4]);
        }

        @Override
        default int amount() {
            return 5;
        }
    }
}
