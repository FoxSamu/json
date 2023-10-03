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

The current version is `0.5.1`. This version is compatible with Java 11 and above. However, I plan to drop Java 11 compat and move to Java 17 (allowing for sealing the `JsonNode` interface).

The artifact can be installed from my [Maven repository](https://maven.shadew.net/).

### Gradle

```groovy
repositories {
    // Add my repository
    maven { url "https://maven.shadew.net/" }
}

dependencies {
    // Add the artifact
    implementation "dev.runefox:json:0.5.1"
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
        <version>0.5.1</version>
    </dependency>
</dependencies>
```

### Download

You can also manually download the artifacts manually from my Maven repository:

- **[Download v0.5.1](https://maven.shadew.net/dev/runefox/json/0.5.1/json-0.5.1.jar)**
- **[Download sources v0.5.1](https://maven.shadew.net/dev/runefox/json/0.5.1/json-0.5.1-sources.jar)**
- **[All artifacts for v0.5.1](https://maven.shadew.net/dev/runefox/json/0.5.1/)**

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
} catch (JsonSyntaxException | FileNotFoundException exc) {
    // Handle your exceptions appropriately
}
```

The method will return a `JsonNode` instance, or throw a checked `JsonSyntaxException` when parsing fails. When parsing
a `File`, a `FileNotFoundException` could also occur. Any other `IOException` that occurs is thrown as
an `UncheckedIOException`, they are usually fatal. Catch an `UncheckedIOException` if you want to handle those
too.

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

### Codecs

Codecs are a handy tool to easily encode and decode Java objects into JSON trees and vice versa. All the logic for this can be found in a separate package: `dev.runefox.json.codec`.

The main type that is important in defining codecs is the `JsonCodec` interface. This interface contains many base codec definitions, for primitives and other basic Java types. You can use codecs of other types to define new codecs.

Say, you have a class like this:
```java
public class Person {
    public final String firstName;
    public final String lastName;
    public String nickname; // Optional
    
    public Person(String first, String last) {
        firstName = first;
        lastName = last;
    }
}
```
The class contains a few fields and you preferably want to serialize it in a JSON structure like this:
```json
{
    "first_name": "Lottie",
    "last_name": "Mills",
    "nickname": "Lot"
}
```
In this scenario, you have a variety of options:
- Implement `JsonCodec` manually
- Make your class implement the `JsonEncodable` interface
- Implement a `RecordCodec`

The first option means you have to perform the checks for the presence and correctness of fields yourself. This might be preferred in some cases, but the codec system can do this for you. The second option is not really practical, as this is more useful for mutable types and your class is mostly immutable. Additionally, it will still require you to perform the same checks as with implementing `JsonCodec`.

The `RecordCodec` solves problems here. It allows you to read and write fields in the format you prefer, while the `RecordCodec` class keeps track of the validity of the JSON tree.

For the `Person` class, the codec implementation would look something like this:

```java
public static final JsonCodec<Person> CODEC = new RecordCodec<>() {
    @Override
    protected Person decode(DecodeContext<Person> ctx) {
        Person person = new Person(
            ctx.field("first_name", JsonCodec.STRING),
            ctx.field("last_name", JsonCodec.STRING)
        );
        // Applies the field only if it's present in the JSON object
        ctx.applyField("nickname", JsonCodec.STRING, nickname -> person.nickname = nickname);
        return person;
    }

    @Override
    protected void encode(EncodeContext<Person> ctx, Person obj) {
        ctx.field("first_name", JsonCodec.STRING, obj.firstName);
        ctx.field("last_name", JsonCodec.STRING, obj.lastName);
        if (obj.nickname != null)
            // Set the optional field only if it's present
            ctx.field("nickname", JsonCodec.STRING, obj.nickname);
    }
}
```

Note that a `RecordCodec` always produces and requires a JSON object. It cannot handle arrays or primitives. See the static methods of `JsonCodec` for other ways to construct codecs.


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


## Documentation

Documentation is being worked on. The most commonly needed parts of the library are well documented with JavaDoc comments. More documentation coming
in later versions.

I am working on hosting the compiled JavaDoc online.

## Changelog

### 0.5.1
- Added support for streaming multiple JSON documents over a single stream, through two new interfaces `JsonInput` and `JsonOutput`.
- The project now has a named module and can now be used as a dependency in other module-based projects.

### 0.5
- **Changed base package name and artifact group from `net.shadew` to `dev.runefox`**
- Added `merge(...)` which merges an object into the main object
- Added `JsonNode.arrayCollector(...)` that takes a mapping function to quickly map Java objects to JSON in a `Stream`.
- Added `JsonNode.objectCollector(...)` that takes two mapping functions to collect elements into an object.
- Added `wrap()` to quickly wrap a node into a new array and `wrap(...)` to quickly wrap a node into a new object under a given key.
- The `JsonNode.numberArray(...)` methods taking primitives no longer accept varargs, except for the one taking `Number` objects, because the Java compiler would find it ambiguous otherwise.
- Fixed serializer expecting an object or array despite `FormattingConfig.anyValue` being set to true.

### 0.4
- Added new methods for checking values of object elements (i.e. `isBoolean(String key)`)
- Added `MissingKeyException` that can be thrown when a required key is missing
- Fixed `isPrimitive` returning true for all JSON structures and `isConstruct` returning false for all JSON structures
- Deprecated `JsonPath` and `JsonNode.query(...)` methods

### 0.3.2
- Fixed `NoSuchMethodError` with Android not desugaring `toArray(IntFunction)`

### 0.3.1
- Fixed floating point numbers between 0 and 1 being rounded to 0 in `JsonNode.number()`

### 0.3
- Added the codec system for easy encoding and decoding of Java objects to/from JSON trees
- Fixed `toString` returning `dev.runefox.json.BooleanNode@.....` for boolean types, making `toString` JSON data not parsable
- A new unchecked exception, `JsonException`, is now the superclass of all the exceptions thrown by the assertions in `JsonNode` methods, as well as exceptions thrown from codecs
- Added `JsonNode.arrayCollector()` for easily collecting all `JsonNode`s in a `Stream` into an array node
- The numeric values returned from `JsonNode`s should now be closer to the actual value stored in the JSON data
- `JsonRepresentable` now replaces `JsonSerializable`, and a `JsonNode` is now `JsonRepresentable`
- `JsonNode.fromJavaObject` is now deprecated and only for debug purposes
- Improved documentation

### 0.2.1
- Add methods to create array nodes from arrays of primitives

### 0.2
- It's now possible to parse JSON that is not an object or array at root (can enabled in `ParsingConfig`)
- Added `Json` presets with compact printing
- Various new `JsonNode` API for reading and writing a JSON structure
- `IncorrectArrayLengthException` has been replaced with `IncorrectSizeException`
- `JsonType` has now has some extra API
- Serializing a JSON node that is not an object or array now throws an exception (this check can be disabled in `FormattingConfig`)
- Surrogate pairs are now treated as one code point when parsing
- Improved documentation

### 0.1
Initial release

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
