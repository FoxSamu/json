package dev.runefox.json;

import dev.runefox.json.codec.JsonCodec;
import dev.runefox.json.codec.ObjectCodecBuilder;

public class Main2 {
    private static final Json JSON = Json.json5Builder().build();

    public static void main(String[] args) {
        enum PermissionLevel {
            USER,
            MODERATOR,
            ADMIN
        }

        record Person(String name, int age, PermissionLevel perms) {
        }


        JsonCodec<Person> codec = ObjectCodecBuilder.of(Person.class)
                                                    .with("name", JsonCodec.STRING, Person::name)
                                                    .with("age", JsonCodec.INT, Person::age)
                                                    .with("perms", JsonCodec.ofEnum(PermissionLevel.values(), PermissionLevel::name), Person::perms)
                                                    .build(Person::new);

        System.out.println(codec.encode(new Person("Me", 21, PermissionLevel.ADMIN)));
        System.out.println(codec.encode(new Person("Them", 32, PermissionLevel.MODERATOR)));
        System.out.println(codec.decode(JsonNode.object().set("name", "He").set("age", 29).set("perms", "USER")));
    }
}
