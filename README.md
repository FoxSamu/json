# JSON for Java

This small and performant library allows for easy JSON parsing, modifying and serializing, in Java, supporting
the [JSON standard specification][json-spec] and the extended [JSON 5 specification][json5-spec].

## Design goals

This library is shaped around easy analyzation and manipulation of JSON data.

- Easy to use JSON tree representation with little as possible `instanceof` checks and casts
- Quick and easy parsing of files and JSON strings
- Quick serialization with a high variety of formatting options
- Reading and writing of streams containing multiple JSON documents (such as over socket streams)
- Support for the [JSON 5 specification][json5-spec].

## Work in progress

This library is in development and the API can change at any time, although at this point it has become relatively stable. I try to make API changes as smooth as possible.

## Installing

The current version is `0.7.2`. This version is compatible with Java 11 and above. However, I plan to drop Java 11 compat and move to Java 17 (allowing for sealing the `JsonNode` interface).

The artifact can be installed from my [Maven repository](https://maven.shadew.net/).

### Gradle

```groovy
repositories {
    // Add my repository
    maven { url "https://maven.shadew.net/" }
}

dependencies {
    // Add the artifact
    implementation "dev.runefox:json:0.7.2"
}
```

### Maven

```xml
<repositories>
    <!-- Add my repository -->
    <repository>
        <id>Runefox Maven</id>
        <url>https://maven.shadew.net/</url>
    </repository>
</repositories>

<dependencies>
    <!-- Add the artifact -->
    <dependency>
        <groupId>dev.runefox</groupId>
        <artifactId>json</artifactId>
        <version>0.7.2</version>
    </dependency>
</dependencies>
```

### Download

You can also manually download the artifacts manually from my Maven repository:

- **[Download v0.7.2](https://maven.shadew.net/dev/runefox/json/0.7.2/json-0.7.2.jar)**
- **[Download sources v0.7.2](https://maven.shadew.net/dev/runefox/json/0.7.2/json-0.7.2-sources.jar)**
- **[All artifacts for v0.7.2](https://maven.shadew.net/dev/runefox/json/0.7.2/)**

## Usage

### Setup

First you want a `dev.runefox.json.Json` instance. This instance is used to parse and serialize JSON trees. Each `Json`
instance manages one specific configuration, if you need multiple configurations, you need multiple `Json` instances.
There is a lot to configure. But not to worry, the `Json` class provides some quick factory methods for the most common
configurations.

```java
// Create a preconfigured JSON instance
Json json = Json.json();

// Or use JSON 5
Json json5 = Json.json5();

// Or use your own configurations
Json custom = Json.json5Builder().formatConfig(
    // Setting the format config overrides the JSON 5 option:
    // you have to set it manually
    FormattingConfig.compact().json5(true)
);
```

Using this JSON instance you can parse files, strings, and other things that can be read using a `java.io.Reader`. This
instance is also used to write to files, strings or other writeable things that are a `java.io.Writer`, or
a `java.lang.StringBuilder`.

Any JSON value is represented using a `JsonNode` instance. This node can be a primitive, null, an array or an object. **
Do not implement `JsonNode` yourself!!**

### Parsing

A JSON file or string is easily parsed using one of the `parse` methods of your `Json` instance.

```java
try {
    JsonNode tree = json.parse(new File("json/file.json"));
    // Read data from tree
} catch (IOException exc) {
    // Handle your exceptions appropriately
}
```

The method will return a `JsonNode` instance, or throw a checked `JsonSyntaxException` when parsing fails. Most often, other `IOException`s can occur as well. You should handle these appropriately.

### Serializing

Serialization is again done using a `Json` instance. A `Json` instance can be configured with a wide range of formatting
options by providing a `FormattingConfig` instance as you configure the `Json` instance using it's builder. This
configuration is copied when you build the `Json` instance.

To serialize a JSON tree, simply call any `serialize` method on your `Json` instance, and provide a tree and (unless you
want a string) a file, writer or `StringBuilder` to write to.

```java
String jsonString = json.serialize(tree);
```

A few examples of different `FormatConfig` setups.

<!-- @formatter:off -->
<!-- ^ for IntelliJ -->
#### `FormattingConfig.pretty()`
Spreads objects and arrays over multiple lines. This is the default configuration when no `FormattingConfig` has been set for the `Json` instance.
```json
{
    "mode": "pretty",
    "indents": 4
}
```

#### `FormattingConfig.prettyCompact()`
Puts JSON on one line but keeps spacing. Useful for small JSON trees.
```json
{ "mode": "prettyCompact", "keep_spaces": true }
```

#### `FormattingConfig.compact()` 

Makes JSON as compact as possible.
```json
{"mode":"compact","keep_spaces":false}
```
In JSON 5 it also removes quotes where possible.
```json5
{mode:"compact",keep_spaces:false}
```

#### `FormattingConfig.pretty().indent(2)`
Use a different indentation (2 spaces, instead of the default of 4).
```json
{ 
  "mode": "pretty",
  "indents": 2
}
```
<!-- @formatter:on -->

Another way to serialize JSON is by calling `toString` on a `JsonNode`. However, this is less optimal and is intended
for debugging only. For production, use `Json#serialize`.

### Using `JsonNode`s

The central part of this library is the `JsonNode` interface. This interface abstractly represents any JSON data in any form. You don't need to worry about the implementation, this is all managed by the library. In fact, you should not be making a custom implementation in the first place. `JsonNode` has a bunch of constants and factory methods to retrieve new instances to serialize.

```java
// Create a new object
JsonNode obj = JsonNode.object();

// Create a new array
JsonNode arr = JsonNode.array();

// Create a new string
JsonNode str = JsonNode.string("Hello");

// Obtain the boolean `true`
JsonNode bool = JsonNode.TRUE; // Or JsonNode.bool(true)
```

The library differentiates between two primary classes of JSON types: primitives and constructs.
- Primitives are simple values: numbers, booleans, strings and `null`. They are immutable and can be put into a constant and reused.
- Constructs are compound values: objects and arrays. They are mutable instances of `JsonNode`.

`JsonNode` is a huge interface, exposing many methods to obtain and manipulate JSON data. Many of such methods only work on certain
types of data. The library tries to be lenient in this, but not too lenient. It also performs the necessary type checking for
you. For example, if you call `node.length()`, it implicitly asserts your data has a length (that is, it must be an object, array or string).
You can also manually assert the types of your data using various `require` methods.

```java
obj.requireObject();  // fine
arr.requireObject();  // not fine!

obj.length();  // fine
arr.length();  // also fine
str.length();  // also fine
bool.length(); // not fine!

for (JsonNode element : arr) { // fine
}

for (JsonNode element : obj) { // not fine!
    // Instead you can do: forEachEntry or get an entrySet
}
```

Objects and arrays can be queried and manipulated using various functions: `get`, `set`, `remove` and in case of arrays: `add`.
As shown above, arrays can be iterated using a for-each loop.

```java
JsonNode nums = JsonNode.numberArray(1, 2, 3, 4, 5, 6.7);

System.out.println(nums.get(3) * 10 + nums.get(1)); // 42

nums.add("impostor"); // Same as nums.add(JsonNode.string("impostor"))

for (JsonNode elem : nums) {
    System.out.println(elem.asInt());
}
// Above loop prints (newlines omitted for brevity):
// 1 2 3 4 5 6    (see how it truncates the non-integral value)
//
// (but then...)
// Uncaught dev.runefox.json.IncorrectTypeException: Unmatched types, required NUMBER, found STRING


for (JsonNode elem : nums) {
    System.out.println(elem.asString());
}
// Note that asString converts all primitives to strings. Above loop prints:
// 1 2 3 4 5 6.7 impostor
//
// Use asExactString if you don't want the conversion of other primitives to strings
```

### Numeric flexibility

Whenever the library parses a number, it doesn't really convert it to an actual numeric value internally. Instead, it
keeps the string representation of the number and lazily parses it whenever different types of the number are requested.
This is done using a custom implementation of `java.lang.Number`. The benefit of this is that as many precision is kept
as possible. Let's say the JSON data has an extremely large integer, then retrieving it as a `long` will cause overflow,
and a `double` will become `Infinity`. But when retrieved as a `BigInteger`, the exact value is retained.

The specific implementation of this is encapsulated by the library,
you don't need to worry about it.

### Codecs

Codecs are a handy tool to easily encode and decode Java objects into JSON trees and vice versa. All the logic for this can be found in a separate package: `dev.runefox.json.codec`.

The main type that is important in defining codecs is the `JsonCodec` interface. This interface contains many base codec definitions, for primitives and other basic Java types. You can use codecs of other types to define new codecs.

Say, you have a record class like this:
```java
public record Person(String firstName, String lastName, int age) {
}
```
The class contains a few fields and you preferably want to serialize it in a JSON structure like this:
```json
{
    "first_name": "Lottie",
    "last_name": "Mills",
    "age": 32
}
```
In this scenario, you have a variety of options:
- Implement `JsonCodec` manually
- Make your class implement the `JsonEncodable` interface
- Implement a `RecordCodec`, which is useful for large objects
- Use `ObjectCodecBuilder`

The first option means you have to perform the checks for the presence and correctness of fields yourself. This might be preferred in some cases, but the codec system can do this for you. The second option is not really practical, as this is more useful for mutable types and your class is mostly immutable. Additionally, it will still require you to perform the same checks as with implementing `JsonCodec`. The third option is a good solution but it's quite cumbersome for simple classes like this.

The `ObjectCodecBuilder` solves problems here. It allows you to specify which fields to serialize, how to get them from your object and where to put them in the constructor.

For the `Person` class, the codec implementation would look something like this:

```java
public static final JsonCodec<Person> CODEC 
    = ObjectCodecBuilder.of(Person.class)
                        .with("first_name", JsonCodec.STRING, Person::firstName)
                        .with("last_name", JsonCodec.STRING, Person::lastName)
                        .with("age", JsonCodec.INT, Person::age)
                        .build(Person::new)
```

This system is useful for classes with up to 16 serialized fields. Note that a this codec always produces and requires a JSON object. It cannot handle arrays or primitives. See the static methods of `JsonCodec` for other ways to construct codecs.


### Stream reading and writing

In 0.5.1, it is now possible to read or write multiple JSON documents through one single stream. This can be useful for e.g. networking or databases. The default parsing methods of `Json` boldly assume that you are just streaming one document and then closing the stream, and will throw a syntax error if the end of the stream is not reached after reading the document. 

The `JsonInput` interface resolves this. Using `JsonInput`, you can read as many JSON documents from one single stream as you like. Documents in such streams are considered to be separated by nothing but the syntax of JSON, and optionally some spaces (e.g. `{} {} [][]` is a stream of four different documents). You can obtain a `JsonInput`, just like you can parse a single document, from the `Json` instance. The returned `JsonInput` will abide the parsing configuration of your `Json` instance like usual (thus, `Json.json5()` will allow you to read JSON 5 documents).

```java
Json json = ...;  // something not null
JsonNode myJsonNode;

try (JsonInput input = json.input(...)) {
    myJsonNode = input.read();
}
```

Note that it is crucial to close your `JsonInput` object, you can use a try-with-resources block for this or just do it manually. Closing the input closes the underlying streams.

A similar interface exists for outgoing traffic: `JsonOutput`. It works in a very similar fashion as serializing, also abiding the formatting config of the `Json` object it is created with.

```java
Json json = ...;

try (JsonOutput output = json.output(...)) {
    output.write(myJsonNode);
}
```

Once again, closing your stream is crucial, so that the underlying streams are closed.

`JsonInput` and `JsonOutput` are thread safe, meaning that only one thread can read from or write to them at a time.


### Java 9 modules support

To use this library in a project that uses Java 9 modules, do the following in your `module-info.java`:
```java
requires dev.runefox.json;
```

### Kotlin

Since 0.7.2 the Kotlin part is a separate artifact and must be added. It's named `jsonkt` and follows
the same version as the main artifact.

Since 0.6.1, the library now integrates better with Kotlin:
```kotlin
val json = jsonObject {
    it["x"] = 3
    it["y"] = 5
}

json["z"] = 9

println(json)  // {"x": 3, "y": 5, "z": 9}
```

Some extra `JsonNode` factory methods were added to reduce the need of escaping reserved kotlin words in backticks. For
example, `JsonNode.object()` would be called in kotlin as <code>JsonNode.\`object\`()</code>. This is ugly, and since 0.6.1, the `jsonObject()` function is available as a replacement to this. Additional functions like this are added for other types, to keep consistency.

When using `JsonCodec`s, any object now has the `encoded` infix function, and `JsonNode` also has the reversed `decoded` infix function, allowing for the following syntax:
```kotlin
val json = LocalDateTime.now() encoded JsonCodec.LOCAL_DATE_TIME

println(json decoded JsonCodec.LOCAL_DATE_TIME)
```

Since `JsonNode` has natural `get` and `set` methods, Kotlin allows you to call these using subscript notation:
```kotlin
arr[1]
arr[2] = 3 // Converts 3 to a JsonNode automatically

obj["key"]
obj["foo"] = "bar" // also automatically converted to JsonNode
```

As an extra feature, elements can be added to an array node using `+=`:

```kotlin
val arr = jsonArray()
arr += 1
arr += 2
arr += jsonObject()

println(arr) // [1, 2, {}]
```


## Documentation

Documentation is being worked on. The most commonly needed parts of the library are well documented with JavaDoc comments. More documentation coming
in later versions.

I am working on hosting the compiled JavaDoc online.

## Changelog

Since 0.7.2, changelogs have moved to GitHub Releases. See [Release v0.7.2](https://github.com/FoxSamu/json/releases/tag/v0.7.2) for the changelog of all versions up to 0.7.2. Newer versions will include the changelog for that version in the corresponding release.

## License

**[See LICENSE for full license](LICENSE)**

Copyright 2022 Sam&umacr;

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.

[json-spec]: https://www.json.org/json-en.html

[json5-spec]: https://json5.org/
