package dev.runefox.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ObjectCodecGenerator {
    private static final String CODEC_NAME = "Codec%d";
    private static final String FUNCTION_NAME = "Function%d";
    private static final String BUILDER_NAME = "Builder%d";
    private static final String PARAMETER_TYPE_NAME = "P%d";
    private static final String RETURN_TYPE_NAME = "T";
    private static final String WITH_PARAMETER_TYPE_NAME = "P";
    private static final String PARAMETER_NAME = PARAMETER_TYPE_NAME + " p%1$d";

    private static final String BUILDER_CLASS_NAME = "ObjectCodecBuilder";
    private static final String IMPLEMENTATION_NAME = "ObjectCodec";

    private final File out;
    private final String pkg;
    private final int maxParams;

    public ObjectCodecGenerator(File out, String pkg, int maxParams) {
        this.out = new File(out, pkg.replace('.', '/'));
        this.pkg = pkg;
        this.maxParams = maxParams;
    }

    private static String typeSignature(int params) {
        return IntStream.range(0, params)
                        .mapToObj(PARAMETER_TYPE_NAME::formatted)
                        .collect(Collectors.joining(", "));
    }

    private static String typeSignatureWithParamAndReturn(int params) {
        if (params == 0) return "P, T";
        return IntStream.range(0, params)
                        .mapToObj(PARAMETER_TYPE_NAME::formatted)
                        .collect(Collectors.joining(", ")) + ", P, T";
    }

    private static String typeSignatureWithReturn(int params) {
        if (params == 0) return "T";
        return IntStream.range(0, params)
                        .mapToObj(PARAMETER_TYPE_NAME::formatted)
                        .collect(Collectors.joining(", ")) + ", T";
    }

    private static String functionSignature(int params) {
        return IntStream.range(0, params)
                        .mapToObj(PARAMETER_NAME::formatted)
                        .collect(Collectors.joining(", "));
    }

    private void generateBuilder(PrintWriter out) {
        out.printf("package %s;%n%n", pkg);
        out.printf("import java.util.function.Supplier;%n");
        out.printf("import java.util.function.Function;%n%n");

        out.printf("public final class %s {%n", BUILDER_CLASS_NAME);
        out.printf("    private %s() {%n", BUILDER_CLASS_NAME);
        out.printf("    }%n%n");

        out.printf("    public static <T> %s<T> of(Class<T> type) {%n", BUILDER_NAME.formatted(0));
        out.printf("        return new ObjectCodec.%s.Builder<>();%n", CODEC_NAME.formatted(0));
        out.printf("    }%n%n");

        for (int i = 0; i <= maxParams; i++) {
            out.printf("    public interface %s<%s> {%n", FUNCTION_NAME.formatted(i), typeSignatureWithReturn(i));
            out.printf("        T apply(%s);%n", functionSignature(i));
            out.printf("    }%n%n");
            out.printf("    public interface %s<%s> {%n", BUILDER_NAME.formatted(i), typeSignatureWithReturn(i));
            out.printf("        JsonCodec<T> build(%s<%s> ctor);%n", FUNCTION_NAME.formatted(i), typeSignatureWithReturn(i));

            if (i != maxParams) {
                out.printf("        <P> %s<%s> with(String name, JsonCodec<P> codec, Function<T, P> getter);%n", BUILDER_NAME.formatted(i + 1), typeSignatureWithParamAndReturn(i));
                out.printf("        <P> %s<%s> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter);%n", BUILDER_NAME.formatted(i + 1), typeSignatureWithParamAndReturn(i));
                out.printf("        <P> %s<%s> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter);%n", BUILDER_NAME.formatted(i + 1), typeSignatureWithParamAndReturn(i));
            }
            out.printf("    }%n%n");
        }

        out.printf("}%n");
    }

    private void generateImplementation(PrintWriter out) {
        out.printf("package %s;%n%n", pkg);
        out.printf("import java.util.function.Supplier;%n");
        out.printf("import java.util.function.Function;%n%n");
        out.printf("import dev.runefox.json.MissingKeyException;%n");
        out.printf("import dev.runefox.json.JsonNode;%n%n");

        out.printf("interface %s<T> extends JsonCodec<T> {%n", IMPLEMENTATION_NAME);
        out.print("""
                          void encodeObj(T obj, JsonNode json);
                          T decodeObj(JsonNode json);

                          @Override
                          default JsonNode encode(T obj) {
                              JsonNode node = JsonNode.object();
                              encodeObj(obj, node);
                              return node;
                          }

                          @Override
                          default T decode(JsonNode json) {
                              return decodeObj(json.requireObject());
                          }
                           
                          record Field<T, P>(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {
                              void apply(T obj, JsonNode json) {
                                  P val = getter.apply(obj);
                                  if (def != null && val == null)
                                      return;

                                  json.set(name, codec.encode(val));
                              }

                              P get(JsonNode obj) {
                                  if (!obj.has(name)) {
                                      if (def == null)
                                          throw new MissingKeyException(name);
                                      return def.get();
                                  }

                                  return codec.decode(obj.get(name));
                              }
                          }

                      """.replace("\n", System.lineSeparator()));


        for (int i = 0; i <= maxParams; i++) {
//            out.printf("""
//                               static class %1$s<%3$s> implements ObjectCodec<T> {
//                                   private final ObjectCodecBuilder.%2$s<%3$s> ctor;
//
//                                   O0(ObjectCodecBuilder.%2$s<%3$s> ctor) {
//                                       this.ctor = ctor;
//                                   }
//
//                                   @Override
//                                   public void encodeObj(T obj, JsonNode json) {
//                                   }
//
//                                   @Override
//                                   public T decodeObj(JsonNode json) {
//                                       return ctor.apply();
//                                   }
//
//                                   static class Builer<T> implements ObjectCodecBuilder1.A0<T> {
//
//                                       @Override
//                                       public JsonCodec<T> build(ObjectCodecBuilder1.F0<T> ctor) {
//                                           return new O0<>(ctor);
//                                       }
//
//                                       @Override
//                                       public <P> ObjectCodecBuilder1.A1<P, T> with(String key, JsonCodec<P> codec, Function<T, P> getter) {
//                                           return new O1.Builer<>(
//                                               new Field<>(key, codec, null, getter)
//                                           );
//                                       }
//                                   }
//                               }
//
//                           """);

            out.printf("    static class %s<%s> implements ObjectCodec<T> {%n", CODEC_NAME.formatted(i), typeSignatureWithReturn(i));
            out.printf("        private final ObjectCodecBuilder.%s<%s> ctor;%n", FUNCTION_NAME.formatted(i), typeSignatureWithReturn(i));
            for (int j = 0; j < i; j++) {
                out.printf("        private final Field<T, %s> field%d;%n", PARAMETER_TYPE_NAME.formatted(j), j);
            }
            out.println();

            String ctorParams;
            if (i == 0) {
                ctorParams = "ObjectCodecBuilder.%s<%s> ctor".formatted(FUNCTION_NAME.formatted(i), typeSignatureWithReturn(i));
            } else {
                ctorParams = IntStream.range(0, i)
                                      .mapToObj(n -> "Field<T, %s> field%d".formatted(PARAMETER_TYPE_NAME.formatted(n), n))
                                      .collect(Collectors.joining(
                                          ", ",
                                          "ObjectCodecBuilder.%s<%s> ctor, ".formatted(FUNCTION_NAME.formatted(i), typeSignatureWithReturn(i)),
                                          ""
                                      ));
            }

            out.printf("        %s(%s) {%n", CODEC_NAME.formatted(i), ctorParams);
            out.printf("            this.ctor = ctor;%n");
            for (int j = 0; j < i; j++) {
                out.printf("            this.field%1$d = field%1$d;%n", j);
            }
            out.printf("        }%n%n");

//                                   @Override
//                                   public void encodeObj(T obj, JsonNode json) {
//                                   }
            out.printf("        @Override%n");
            out.printf("        public void encodeObj(T obj, JsonNode json) {%n");
            for (int j = 0; j < i; j++) {
                out.printf("            field%d.apply(obj, json);%n", j);
            }
            out.printf("        }%n%n");
//
//                                   @Override
//                                   public T decodeObj(JsonNode json) {
//                                       return ctor.apply();
//                                   }
            out.printf("        @Override%n");
            out.printf("        public T decodeObj(JsonNode json) {%n");
            if (i == 0) {
                out.printf("            return ctor.apply();%n");
            } else {
                out.printf("            return ctor.apply(%n");
                for (int j = 0; j < i; j++) {
                    out.printf("                field%d.get(json)%s%n", j, j == i - 1 ? "" : ",");
                }
                out.printf("            );%n");
            }
            out.printf("        }%n%n");
            out.printf("        static class Builder<%s> implements ObjectCodecBuilder.%s<%1$s> {%n", typeSignatureWithReturn(i), BUILDER_NAME.formatted(i));
            for (int j = 0; j < i; j++) {
                out.printf("        private final Field<T, %s> field%d;%n", PARAMETER_TYPE_NAME.formatted(j), j);
            }
            out.println();


            String builderCtorParams = IntStream.range(0, i)
                                                .mapToObj(n -> "Field<T, %s> field%d".formatted(PARAMETER_TYPE_NAME.formatted(n), n))
                                                .collect(Collectors.joining(", "));

            out.printf("            Builder(%s) {%n", builderCtorParams);
            for (int j = 0; j < i; j++) {
                out.printf("                this.field%1$d = field%1$d;%n", j);
            }
            out.printf("            }%n%n");

            out.printf("            @Override%n");
            out.printf("            public JsonCodec<T> build(ObjectCodecBuilder.%s<%s> ctor) {%n", FUNCTION_NAME.formatted(i), typeSignatureWithReturn(i));

            String ctorArgs = "ctor" + IntStream.range(0, i)
                                                .mapToObj(n -> ", field" + n)
                                                .collect(Collectors.joining(""));

            out.printf("                return new %s<>(%s);%n", CODEC_NAME.formatted(i), ctorArgs);
            out.printf("            }%n%n");

            if (i != maxParams) {
                String builderCtorArgs = IntStream.range(0, i)
                                                  .mapToObj(n -> "field" + n)
                                                  .collect(Collectors.joining(", "));

                out.printf("            @Override%n");
                out.printf("            public <P> ObjectCodecBuilder.%s<%s> with(String name, JsonCodec<P> codec, Function<T, P> getter) {%n", BUILDER_NAME.formatted(i + 1), typeSignatureWithParamAndReturn(i));
                out.printf("                return new %s.Builder<>(%n", CODEC_NAME.formatted(i + 1));
                if (i != 0)
                    out.printf("                    %s,%n", builderCtorArgs);
                out.printf("                    new Field<>(name, codec, null, getter)%n");
                out.printf("                );%n");
                out.printf("            }%n%n");

                out.printf("            @Override%n");
                out.printf("            public <P> ObjectCodecBuilder.%s<%s> withDefault(String name, JsonCodec<P> codec, P def, Function<T, P> getter) {%n", BUILDER_NAME.formatted(i + 1), typeSignatureWithParamAndReturn(i));
                out.printf("                return new %s.Builder<>(%n", CODEC_NAME.formatted(i + 1));
                if (i != 0)
                    out.printf("                    %s,%n", builderCtorArgs);
                out.printf("                    new Field<>(name, codec, () -> def, getter)%n");
                out.printf("                );%n");
                out.printf("            }%n%n");

                out.printf("            @Override%n");
                out.printf("            public <P> ObjectCodecBuilder.%s<%s> withGetDefault(String name, JsonCodec<P> codec, Supplier<P> def, Function<T, P> getter) {%n", BUILDER_NAME.formatted(i + 1), typeSignatureWithParamAndReturn(i));
                out.printf("                return new %s.Builder<>(%n", CODEC_NAME.formatted(i + 1));
                if (i != 0)
                    out.printf("                    %s,%n", builderCtorArgs);
                out.printf("                    new Field<>(name, codec, def, getter)%n");
                out.printf("                );%n");
                out.printf("            }%n");
            }
            out.printf("        }%n");
            out.printf("    }%n%n");
        }
        out.printf("}%n");
    }

    public void run() throws IOException {
        out.mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(out, BUILDER_CLASS_NAME + ".java")))) {
            generateBuilder(writer);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(out, IMPLEMENTATION_NAME + ".java")))) {
            generateImplementation(writer);
        }
    }
}
