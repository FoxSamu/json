package net.shadew.json;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.json.annotation.*;

class TemplateTypeSerializer<T> implements TypeSerializer<T> {
    private final Key key;
    private final Map<String, TypeSerializer<?>> serializers;
    private final Map<String, Class<?>> types;
    private final Class<T> cls;

    private InstanceFactory factory;
    private List<PropertySetter> setters;
    private List<PropertyGetter> getters;
    private List<MultiSetter> multiSetters;

    TemplateTypeSerializer(Key key, Map<String, TypeSerializer<?>> serializers, Map<String, Class<?>> types, Class<T> cls) {
        this.key = key;
        this.serializers = serializers;
        this.types = types;
        this.cls = cls;

        analyzeClass(null);
    }

    TemplateTypeSerializer(Class<T> cls) {
        this.key = null;
        this.serializers = null;
        this.types = new HashMap<>();
        this.cls = cls;

        analyzeClass(null);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void analyzeClass(String target) {
        List<FieldInfo> fieldInfo = Stream.of(cls.getDeclaredFields())
                                          .map(FieldInfo::new)
                                          .peek(FieldInfo::analyze)
                                          .collect(Collectors.toList());

        List<MethodInfo> methodInfo = Stream.of(cls.getDeclaredMethods())
                                            .map(MethodInfo::new)
                                            .peek(MethodInfo::analyze)
                                            .collect(Collectors.toList());

        List<ConstructorInfo> constructorInfo = Stream.of(cls.getDeclaredConstructors())
                                                      .map(c -> (Constructor<T>) c)
                                                      .map(ConstructorInfo::new)
                                                      .peek(ConstructorInfo::analyze)
                                                      .collect(Collectors.toList());

        Map<String, PropertyGetter> getters = new HashMap<>();
        Map<String, PropertySetter> setters = new HashMap<>();
        List<MultiSetter> multiSetters = new ArrayList<>();
        List<InstanceFactory> instanceFactories = new ArrayList<>();

        for (FieldInfo info : fieldInfo) {
            if (!targetMatch(info.targets, target))
                continue;

            if (info.property != null) {
                PropertyGetter getter = new PropertyGetter(info.propertyName, t -> {
                    info.field.setAccessible(true);
                    try {
                        return cast(info.getType, info.field.get(t));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

                if (info.notNullOnly) {
                    getter.check = t -> {
                        info.field.setAccessible(true);
                        try {
                            return info.field.get(t) != null;
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    };
                }

                getters.put(getter.name, getter);

                PropertySetter setter = new PropertySetter(info.propertyName, (t, v) -> {
                    try {
                        info.field.set(t, cast(info.setType, v));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }, info.notNullOnly);

                setters.put(setter.name, setter);
            }
        }

        for (MethodInfo info : methodInfo) {
            if (!targetMatch(info.targets, target))
                continue;

            if (info.type == MethodType.INSTANCE_FACTORY) {
                MultiSetter setter = new MultiSetter((t, vs) -> {
                    Object[] values = info.constructParams(vs);
                    info.method.setAccessible(true);
                    try {
                        info.method.invoke(t, values);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

                multiSetters.add(setter);
            } else if (info.type == MethodType.STATIC_FACTORY) {
                InstanceFactory factory = new InstanceFactory(vs -> {
                    Object[] values = info.constructParams(vs);
                    info.method.setAccessible(true);
                    try {
                        return cast(cls, info.method.invoke(null, values));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

                instanceFactories.add(factory);
            } else if (info.type == MethodType.SETTER) {
                PropertySetter setter = new PropertySetter(info.propertyName, (t, v) -> {
                    info.method.setAccessible(true);
                    try {
                        info.method.invoke(t, cast(info.propertyType, v));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }, info.notNullOnly);

                setters.put(setter.name, setter);
            } else if (info.type == MethodType.GETTER) {
                PropertyGetter getter = new PropertyGetter(info.propertyName, t -> {
                    info.method.setAccessible(true);
                    try {
                        return cast(info.propertyType, info.method.invoke(t));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

                getters.put(getter.name, getter);
            }
        }

        for (MethodInfo info : methodInfo) {
            if (!targetMatch(info.targets, target))
                continue;

            if (info.type == MethodType.CONDITION) {
                PropertyGetter getter = getters.get(info.propertyName);
                Predicate<T> test = t -> {
                    info.method.setAccessible(true);
                    try {
                        return cast(boolean.class, info.method.invoke(t));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };

                getter.check = getter.check == null ? test : getter.check.and(test);
            }
        }

        for (ConstructorInfo info : constructorInfo) {
            if (!targetMatch(info.targets, target))
                continue;

            if (info.factory != null) {
                InstanceFactory factory = new InstanceFactory(vs -> {
                    Object[] values = info.constructParams(vs);
                    info.constructor.setAccessible(true);
                    try {
                        return cast(cls, info.constructor.newInstance(values));
                    } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                });

                instanceFactories.add(factory);
            }
        }

        if (instanceFactories.size() > 1) {
            throw new TypeSerializerFormatException("Found multiple factory methods/constructors for target " + target);
        }

        if (instanceFactories.size() == 0) {
            throw new TypeSerializerFormatException("Found no factory methods/constructors for target " + target);
        }

        this.factory = instanceFactories.get(0);
        this.getters = List.copyOf(getters.values());
        this.setters = List.copyOf(setters.values());
        this.multiSetters = List.copyOf(multiSetters);
    }

    private static boolean targetMatch(Targets targets, String target) {
        if (targets == null) return true;
        if (target == null) return false;

        String[] ts = targets.value();
        for (String t : ts) {
            if (t.equals(target))
                return true;
        }
        return false;
    }

    private class PropertyGetter {
        final String name;
        final Function<T, ?> getter;
        Predicate<T> check;

        private PropertyGetter(String name, Function<T, ?> getter) {
            this.name = name;
            this.getter = getter;
        }

        @SuppressWarnings("unchecked")
        <N> void apply(T obj, Map<String, N> objs) {
            if (check.test(obj)) {
                objs.put(name, (N) getter.apply(obj));
            }
        }
    }

    private class PropertySetter {
        final String name;
        final BiConsumer<T, ?> setter;
        final boolean optional;

        private PropertySetter(String name, BiConsumer<T, ?> setter, boolean optional) {
            this.name = name;
            this.setter = setter;
            this.optional = optional;
        }

        @SuppressWarnings("unchecked")
        <N> void apply(T obj, Map<String, N> objs) {
            BiConsumer<T, N> consumer = (BiConsumer<T, N>) setter;
            if (!objs.containsKey(name)) {
                if (!optional) {
                    throw new TypeSerializerException("Missing property '" + name + "'");
                }
                return;
            }
            consumer.accept(obj, objs.get(name));
        }
    }

    private class MultiSetter {
        final BiConsumer<T, Map<String, ?>> setter;

        private MultiSetter(BiConsumer<T, Map<String, ?>> setter) {
            this.setter = setter;
        }

        void apply(T obj, Map<String, ?> objs) {
            setter.accept(obj, objs);
        }
    }

    private class InstanceFactory {
        final Function<Map<String, ?>, T> factory;

        private InstanceFactory(Function<Map<String, ?>, T> factory) {
            this.factory = factory;
        }

        T apply(Map<String, ?> objs) {
            return factory.apply(objs);
        }
    }

    private class FieldInfo {
        final Field field;
        Property property;
        Targets targets;
        String propertyName;
        Class<?> setType;
        Class<?> getType;
        boolean notNullOnly;

        FieldInfo(Field field) {
            this.field = field;
        }

        void analyze() {
            property = field.getAnnotation(Property.class);
            targets = field.getAnnotation(Targets.class);

            if (property != null) {
                int mods = field.getModifiers();
                if (Modifier.isFinal(mods))
                    throw new TypeSerializerFormatException("@Property field '" + field.getName() + "' should not be final");
                if (Modifier.isStatic(mods))
                    throw new TypeSerializerFormatException("@Property field '" + field.getName() + "' should not be static");

                String n = property.value();
                if (n.isEmpty())
                    propertyName = field.getName();
                else
                    propertyName = n;

                if (!types.containsKey(propertyName))
                    throw new TypeSerializerFormatException("No property '" + propertyName + "' specified");

                Class<?> knownType = types.get(n);
                Class<?> fieldType = field.getType();

                if (!canAssign(knownType, fieldType) || !canAssign(fieldType, knownType))
                    throw new TypeSerializerFormatException("Field type (" + fieldType.getTypeName() + ") does not match type of property '" + propertyName + "' (" + knownType.getTypeName() + ")");

                setType = fieldType;
                getType = knownType;

                notNullOnly = field.isAnnotationPresent(Opt.class);
            }
        }
    }

    private class MethodInfo {
        final Method method;
        MethodType type;
        Factory factory;
        Getter getter;
        Setter setter;
        Condition condition;
        Targets targets;
        String[] names;
        Class<?>[] types;
        ParamMode[] modes;
        String propertyName;
        Class<?> propertyType;
        boolean notNullOnly;

        private MethodInfo(Method method) {
            this.method = method;
        }

        Object[] constructParams(Map<String, ?> vs) {
            int l = types.length;
            Object[] values = new Object[l];
            for (int i = 0; i < l; i++) {
                String name = names[i];
                Class<?> type = types[i];
                ParamMode mode = modes[i];

                if (mode == ParamMode.CHECK) {
                    values[i] = vs.containsKey(name);
                } else if (mode == ParamMode.OPTIONAL) {
                    if (vs.containsKey(name))
                        values[i] = cast(type, vs.get(name));
                    else values[i] = defaultValue(type);
                } else {
                    if (!vs.containsKey(name))
                        throw new TypeSerializerException("Missing property '" + name + "'");
                    values[i] = cast(type, vs.get(name));
                }
            }

            return values;
        }

        void analyze() {
            factory = method.getAnnotation(Factory.class);
            getter = method.getAnnotation(Getter.class);
            setter = method.getAnnotation(Setter.class);
            condition = method.getAnnotation(Condition.class);
            targets = method.getAnnotation(Targets.class);

            if (factory != null) {
                if (getter != null)
                    throw new TypeSerializerFormatException("@Factory and @Getter cannot be combined, on method '" + method.getName() + "'");

                if (setter != null)
                    throw new TypeSerializerFormatException("@Factory and @Setter cannot be combined, on method '" + method.getName() + "'");

                if (condition != null)
                    throw new TypeSerializerFormatException("@Factory and @Condition cannot be combined, on method '" + method.getName() + "'");

                int mods = method.getModifiers();
                type = Modifier.isStatic(mods) ? MethodType.STATIC_FACTORY : MethodType.INSTANCE_FACTORY;

                if (type == MethodType.STATIC_FACTORY) {
                    Class<?> returnType = method.getReturnType();
                    Class<?> knownType = cls;

                    if (!canAssign(returnType, knownType))
                        throw new TypeSerializerFormatException("Method return type (" + returnType.getTypeName() + ") of method '" + method.getName() + "' cannot be converted to " + knownType.getTypeName());
                }

                names = factory.value();
                Parameter[] params = method.getParameters();

                if (params.length != names.length) {
                    throw new TypeSerializerFormatException("Method '" + method.getName() + "' has " + params.length + " parameters, but @Factory specifies " + names.length);
                }

                modes = new ParamMode[params.length];
                types = new Class<?>[params.length];

                for (int i = 0, l = names.length; i < l; i++) {
                    String name = names[i];
                    Parameter param = params[i];

                    Opt opt = param.getAnnotation(Opt.class);
                    Check check = param.getAnnotation(Check.class);

                    if (opt != null && check != null)
                        throw new TypeSerializerFormatException("Parameter " + i + " of '" + method.getName() + "' has both @Opt and @Check");

                    ParamMode mode = ParamMode.REQUIRED;
                    if (opt != null) mode = ParamMode.OPTIONAL;
                    if (check != null) mode = ParamMode.CHECK;

                    modes[i] = mode;

                    if (!TemplateTypeSerializer.this.types.containsKey(name)) {
                        throw new TypeSerializerFormatException("No property '" + name + "' specified");
                    }

                    Class<?> paramType = param.getType();
                    Class<?> knownType = mode == ParamMode.CHECK ? boolean.class
                                                                 : TemplateTypeSerializer.this.types.get(name);

                    if (!canAssign(knownType, paramType))
                        throw new TypeSerializerFormatException("Parameter type (" + paramType.getTypeName() + ") does not match type of property '" + name + "' (" + knownType.getTypeName() + ")");

                    types[i] = paramType;
                }

                return;
            }

            if (getter != null) {
                if (setter != null)
                    throw new TypeSerializerFormatException("@Getter and @Setter cannot be combined, on method '" + method.getName() + "'");

                if (condition != null)
                    throw new TypeSerializerFormatException("@Getter and @Condition cannot be combined, on method '" + method.getName() + "'");

                type = MethodType.GETTER;

                int mods = method.getModifiers();
                if (Modifier.isStatic(mods))
                    throw new TypeSerializerFormatException("@Getter method '" + method.getName() + "' should not be static");

                if (method.getParameters().length > 0)
                    throw new TypeSerializerFormatException("@Getter method '" + method.getName() + "' should not have parameters");

                propertyName = getter.value();

                if (!TemplateTypeSerializer.this.types.containsKey(propertyName))
                    throw new TypeSerializerFormatException("No property '" + propertyName + "' specified");

                Class<?> returnType = method.getReturnType();
                Class<?> knownType = TemplateTypeSerializer.this.types.get(propertyName);

                if (!canAssign(returnType, knownType))
                    throw new TypeSerializerFormatException("Method return type (" + returnType.getTypeName() + ") of '" + method.getName() + "' does not match type of property '" + propertyName + "' (" + knownType.getTypeName() + ")");

                propertyType = knownType;

                notNullOnly = method.isAnnotationPresent(Opt.class);

                return;
            }

            if (setter != null) {
                if (condition != null)
                    throw new TypeSerializerFormatException("@Getter and @Condition cannot be combined, on method '" + method.getName() + "'");

                type = MethodType.SETTER;

                int mods = method.getModifiers();
                if (Modifier.isStatic(mods))
                    throw new TypeSerializerFormatException("@Setter method '" + method.getName() + "' should not be static");

                Parameter[] params = method.getParameters();
                if (params.length != 1)
                    throw new TypeSerializerFormatException("@Setter method '" + method.getName() + "' should have one parameter");

                Parameter param = params[0];

                propertyName = getter.value();

                if (!TemplateTypeSerializer.this.types.containsKey(propertyName))
                    throw new TypeSerializerFormatException("No property '" + propertyName + "' specified");

                Class<?> paramType = param.getType();
                Class<?> knownType = TemplateTypeSerializer.this.types.get(propertyName);

                if (!canAssign(knownType, paramType))
                    throw new TypeSerializerFormatException("Method parameter type (" + paramType.getTypeName() + ") of '" + method.getName() + "' does not match type of property '" + propertyName + "' (" + knownType.getTypeName() + ")");

                propertyType = paramType;

                notNullOnly = method.isAnnotationPresent(Opt.class);

                return;
            }

            if (condition != null) {
                type = MethodType.CONDITION;

                int mods = method.getModifiers();
                if (Modifier.isStatic(mods))
                    throw new TypeSerializerFormatException("@Condition method '" + method.getName() + "' should not be static");

                if (method.getParameters().length > 0)
                    throw new TypeSerializerFormatException("@Condition method '" + method.getName() + "' should not have parameters");

                propertyName = getter.value();

                if (!TemplateTypeSerializer.this.types.containsKey(propertyName))
                    throw new TypeSerializerFormatException("No property '" + propertyName + "' specified");

                Class<?> returnType = method.getReturnType();

                if (!canAssign(returnType, boolean.class))
                    throw new TypeSerializerFormatException("@Condition method return type (" + returnType.getTypeName() + ") of '" + method.getName() + "' is not boolean");

                propertyType = boolean.class;

                return;
            }
        }
    }

    private enum MethodType {
        STATIC_FACTORY,
        INSTANCE_FACTORY,
        SETTER,
        GETTER,
        CONDITION
    }

    private enum ParamMode {
        REQUIRED,
        OPTIONAL,
        CHECK
    }

    private class ConstructorInfo {
        final Constructor<T> constructor;
        Factory factory;
        Targets targets;
        String[] names;
        Class<?>[] types;
        ParamMode[] modes;

        private ConstructorInfo(Constructor<T> constructor) {
            this.constructor = constructor;
        }

        Object[] constructParams(Map<String, ?> vs) {
            int l = types.length;
            Object[] values = new Object[l];
            for (int i = 0; i < l; i++) {
                String name = names[i];
                Class<?> type = types[i];
                ParamMode mode = modes[i];

                if (mode == ParamMode.CHECK) {
                    values[i] = vs.containsKey(name);
                } else if (mode == ParamMode.OPTIONAL) {
                    if (vs.containsKey(name))
                        values[i] = cast(type, vs.get(name));
                    else values[i] = defaultValue(type);
                } else {
                    if (!vs.containsKey(name))
                        throw new TypeSerializerException("Missing property '" + name + "'");
                    values[i] = cast(type, vs.get(name));
                }
            }

            return values;
        }

        void analyze() {
            factory = constructor.getAnnotation(Factory.class);
            targets = constructor.getAnnotation(Targets.class);

            if (factory != null) {
                names = factory.value();
                Parameter[] params = constructor.getParameters();

                if (params.length != names.length) {
                    throw new TypeSerializerFormatException("Constructor '" + constructor.getName() + "' has " + params.length + " parameters, but @Factory specifies " + names.length);
                }

                modes = new ParamMode[params.length];
                types = new Class<?>[params.length];

                for (int i = 0, l = names.length; i < l; i++) {
                    String name = names[i];
                    Parameter param = params[i];

                    Opt opt = param.getAnnotation(Opt.class);
                    Check check = param.getAnnotation(Check.class);

                    if (opt != null && check != null)
                        throw new TypeSerializerFormatException("Parameter " + i + " of '" + constructor.getName() + "' has both @Opt and @Check");

                    ParamMode mode = ParamMode.REQUIRED;
                    if (opt != null) mode = ParamMode.OPTIONAL;
                    if (check != null) mode = ParamMode.CHECK;

                    modes[i] = mode;

                    if (!TemplateTypeSerializer.this.types.containsKey(name)) {
                        throw new TypeSerializerFormatException("No property '" + name + "' specified");
                    }

                    Class<?> paramType = param.getType();
                    Class<?> knownType = mode == ParamMode.CHECK ? boolean.class
                                                                 : TemplateTypeSerializer.this.types.get(name);

                    if (!canAssign(knownType, paramType))
                        throw new TypeSerializerFormatException("Parameter type (" + paramType.getTypeName() + ") does not match type of property '" + name + "' (" + knownType.getTypeName() + ")");

                    types[i] = paramType;
                }

                return;
            }
        }
    }

    static boolean canAssign(Class<?> type, Class<?> to) {
        if (type == to) return true;
        int typeA = numberAssignability(type);
        int toA = numberAssignability(to);
        if (typeA < 0 || toA < 0) {
            if ((type == char.class || type == Character.class) && toA >= 2)
                return true;

            if (type.isPrimitive()) {
                return primitiveMatchesBoxed(type, to);
            }
            if (to.isPrimitive()) {
                return primitiveMatchesBoxed(to, type);
            }
            return to.isAssignableFrom(type);
        }
        return toA >= typeA;
    }

    private static boolean primitiveMatchesBoxed(Class<?> primitive, Class<?> boxed) {
        if (primitive == byte.class && boxed == Byte.class) return true;
        if (primitive == short.class && boxed == Short.class) return true;
        if (primitive == int.class && boxed == Integer.class) return true;
        if (primitive == long.class && boxed == Long.class) return true;
        if (primitive == float.class && boxed == Float.class) return true;
        if (primitive == double.class && boxed == Double.class) return true;
        if (primitive == boolean.class && boxed == Boolean.class) return true;
        return primitive == char.class && boxed == Character.class;
    }

    private static int numberAssignability(Class<?> primitive) {
        if (primitive == byte.class || primitive == Byte.class) return 0;
        if (primitive == short.class || primitive == Short.class) return 1;
        if (primitive == int.class || primitive == Integer.class) return 2;
        if (primitive == long.class || primitive == Long.class) return 3;
        if (primitive == float.class || primitive == Float.class) return 4;
        if (primitive == double.class || primitive == Double.class) return 5;
        return -1;
    }

    private static Object defaultValue(Class<?> v) {
        if (v == byte.class) return (byte) 0;
        if (v == short.class) return (short) 0;
        if (v == int.class) return 0;
        if (v == long.class) return 0L;
        if (v == float.class) return 0F;
        if (v == double.class) return 0D;
        if (v == boolean.class) return false;
        if (v == char.class) return (char) 0;
        return null;
    }

    // We need boxing, not boxing causes ClassCastExceptions even though IntelliJ thinks it looks safe
    @SuppressWarnings({"UnnecessaryBoxing", "unchecked"})
    static <T> T cast(Class<T> cls, Object value) {
        if (value == null) {
            if (cls.isPrimitive())
                throw new ClassCastException("Cannot cast null to " + cls.getName());
            return null;
        }

        if (cls == byte.class) cls = (Class<T>) Byte.class;
        if (cls == short.class) cls = (Class<T>) Short.class;
        if (cls == int.class) cls = (Class<T>) Integer.class;
        if (cls == long.class) cls = (Class<T>) Long.class;
        if (cls == float.class) cls = (Class<T>) Float.class;
        if (cls == double.class) cls = (Class<T>) Double.class;
        if (cls == boolean.class) cls = (Class<T>) Boolean.class;
        if (cls == char.class) cls = (Class<T>) Character.class;

        if (canAssign(value.getClass(), cls)) {
            if (value instanceof Character) {
                if (cls == Integer.class)
                    return cls.cast(Integer.valueOf((char) value));
                if (cls == Long.class)
                    return cls.cast(Long.valueOf((char) value));
                if (cls == Float.class)
                    return cls.cast(Float.valueOf((char) value));
                if (cls == Double.class)
                    return cls.cast(Double.valueOf((char) value));
                if (cls == Character.class)
                    return cls.cast(Character.valueOf((char) value));
            } else if (value instanceof Number) {
                if (cls == Byte.class)
                    return cls.cast(Byte.valueOf(((Number) value).byteValue()));
                if (cls == Short.class)
                    return cls.cast(Short.valueOf(((Number) value).shortValue()));
                if (cls == Integer.class)
                    return cls.cast(Integer.valueOf(((Number) value).intValue()));
                if (cls == Long.class)
                    return cls.cast(Long.valueOf(((Number) value).longValue()));
                if (cls == Float.class)
                    return cls.cast(Float.valueOf(((Number) value).floatValue()));
                if (cls == Double.class)
                    return cls.cast(Double.valueOf(((Number) value).doubleValue()));
            }
            return cls.cast(value);
        }
        throw new ClassCastException("Cannot cast " + value.getClass().getTypeName() + " to " + cls.getName());
    }

    @Override
    public T deserialize(JsonNode node) {
        return null;
    }

    @Override
    public JsonNode serialize(T obj) {
        return null;
    }

    interface Key {
        void apply(JsonNode node, Map<String, JsonNode> result);
        boolean canApply(JsonNode node);
        boolean canBuild(Map<String, JsonNode> map);
        JsonNode build(Map<String, JsonNode> map);
    }

    static class RawKey implements Key {
        private final String name;

        private RawKey(String name) {
            this.name = name;
        }

        @Override
        public void apply(JsonNode node, Map<String, JsonNode> result) {
            result.put(name, node);
        }

        @Override
        public boolean canApply(JsonNode node) {
            return node != null;
        }

        @Override
        public boolean canBuild(Map<String, JsonNode> map) {
            return map.containsKey(name);
        }

        @Override
        public JsonNode build(Map<String, JsonNode> map) {
            return map.get(name);
        }
    }

    static class ObjectKey implements Key {
        private final Map<String, Key> keys;

        private ObjectKey(Map<String, Key> keys) {
            this.keys = keys;
        }

        @Override
        public void apply(JsonNode node, Map<String, JsonNode> result) {
            for (Map.Entry<String, Key> entry : keys.entrySet()) {
                String k = entry.getKey();
                Key v = entry.getValue();

                JsonNode child = node.get(k);
                v.apply(child, result);
            }
        }

        @Override
        public boolean canApply(JsonNode node) {
            if (node == null || !node.isObject())
                return false;

            for (Map.Entry<String, Key> entry : keys.entrySet()) {
                String k = entry.getKey();
                Key v = entry.getValue();

                JsonNode child = node.get(k);
                if (!v.canApply(child)) return false;
            }
            return true;
        }

        @Override
        public boolean canBuild(Map<String, JsonNode> map) {
            for (Key v : keys.values()) {
                if (!v.canBuild(map))
                    return false;
            }
            return true;
        }

        @Override
        public JsonNode build(Map<String, JsonNode> map) {
            JsonNode obj = JsonNode.object();

            for (Map.Entry<String, Key> entry : keys.entrySet()) {
                String k = entry.getKey();
                Key v = entry.getValue();
                JsonNode child = v.build(map);
                if (child != null)
                    obj.set(k, child);
            }

            return obj;
        }
    }

    static class ArrayKey implements Key {
        private final List<Key> keys;

        private ArrayKey(List<Key> keys) {
            this.keys = keys;
        }

        @Override
        public void apply(JsonNode node, Map<String, JsonNode> result) {
            for (int i = 0, l = keys.size(); i < l; i++) {
                Key v = keys.get(i);

                JsonNode child = node.get(i);
                v.apply(child, result);
            }
        }

        @Override
        public boolean canApply(JsonNode node) {
            if (node == null || !node.isObject())
                return false;

            boolean applied = true;
            for (int i = 0, l = keys.size(); i < l; i++) {
                Key v = keys.get(i);

                JsonNode child = node.get(i);
                applied &= v.canApply(child);
            }
            return applied;
        }

        @Override
        public boolean canBuild(Map<String, JsonNode> map) {
            for (Key v : keys) {
                if (!v.canBuild(map))
                    return false;
            }
            return true;
        }

        @Override
        public JsonNode build(Map<String, JsonNode> map) {
            JsonNode arr = JsonNode.nullArray(keys.size());

            int lastNonNullIndex = -1;
            for (int i = 0, l = keys.size(); i < l; i++) {
                Key v = keys.get(i);
                JsonNode child = v.build(map);
                if (child != null) {
                    arr.set(i, child);
                    lastNonNullIndex = i;
                }
            }

            lastNonNullIndex++;
            while (arr.length() > lastNonNullIndex) {
                arr.remove(lastNonNullIndex);
            }

            return arr;
        }
    }

    static class OptionalKey implements Key {
        private final Key key;

        private OptionalKey(Key key) {
            this.key = key;
        }

        @Override
        public void apply(JsonNode node, Map<String, JsonNode> result) {
            if (key.canApply(node))
                key.apply(node, result);
        }

        @Override
        public boolean canApply(JsonNode node) {
            return true;
        }

        @Override
        public boolean canBuild(Map<String, JsonNode> map) {
            return true;
        }

        @Override
        public JsonNode build(Map<String, JsonNode> map) {
            if (!key.canBuild(map))
                return null;
            return key.build(map);
        }
    }

    static class AlternateKey implements Key {
        private final List<Key> alts;

        private AlternateKey(List<Key> alts) {
            this.alts = alts;
        }

        @Override
        public void apply(JsonNode node, Map<String, JsonNode> result) {
            for (Key key : alts) {
                if (key.canApply(node)) {
                    key.apply(node, result);
                    return;
                }
            }
        }

        @Override
        public boolean canApply(JsonNode node) {
            for (Key key : alts) {
                if (key.canApply(node)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canBuild(Map<String, JsonNode> map) {
            for (Key key : alts) {
                if (key.canBuild(map)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public JsonNode build(Map<String, JsonNode> map) {
            for (Key key : alts) {
                if (key.canBuild(map)) {
                    return key.build(map);
                }
            }
            return null;
        }
    }

    static class DefaultKey implements Key {
        private final Key key;
        private final JsonNode def;

        private DefaultKey(Key key, JsonNode def) {
            this.key = key;
            this.def = def;
        }

        @Override
        public void apply(JsonNode node, Map<String, JsonNode> result) {
            if (key.canApply(node))
                key.apply(node, result);
            else if (key.canApply(def))
                key.apply(def, result);
        }

        @Override
        public boolean canApply(JsonNode node) {
            return key.canApply(node) || key.canApply(def);
        }

        @Override
        public boolean canBuild(Map<String, JsonNode> map) {
            return true;
        }

        @Override
        public JsonNode build(Map<String, JsonNode> map) {
            if (!key.canBuild(map))
                return def;
            return key.build(map);
        }
    }
}
