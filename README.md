# JSON for Java

This small and performant library allows for easy JSON parsing, modifying and serializing, in Java, supporting
the [JSON standard specification][json-spec] and the extended [JSON 5 specifiecation][json5-spec].

## Design goals

This library is shaped around easy analyzation and manipulation of JSON data.

- Easy to use JSON tree representation with little as possible `instanceof` checks and casts.
- Navigating JSON trees like JavaScript objects using path strings (i.e. `friends[3].name`)
- Quick and easy parsing of files and JSON strings
- Quick serialization with a high variety of formatting options
- Support for the [JSON 5 specification][json5-spec].

## Work in progress

This library is in development and the API can change at any time. Do not expect this library to be stable.

The current version is `0.2.1`.

## Installing

The artifact can be installed from my Maven repository.

### Gradle

```groovy
repositories {
    // Add my repository
    maven { url "https://maven.shadew.net/" }
}

dependencies {
    // Add the artifact
    implementation "net.shadew:json:0.2.1"
}
```

### Maven

Soon™

### Download

The artifact can be downloaded from my Maven repository:

- **[Download v0.2.1](https://maven.shadew.net/net/shadew/json/0.2.1/json-0.2.1.jar)**
- **[Download sources v0.2.1](https://maven.shadew.net/net/shadew/json/0.2.1/json-0.2.1-sources.jar)**
- **[All artifacts for v0.2.1](https://maven.shadew.net/net/shadew/json/0.2.1/)**

## Usage

### Setup

First you want a `net.shadew.json.Json` instance. This instance is used to parse and serialize JSON trees. Each `Json`
instance manages one specific configuration, if you need multiple configurations, you need multiple `Json` instances.

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
an `UncheckedIOException`, they are usually more critical. Catch an `UncheckedIOException` if you want to handle those
too.

### Using JSON paths

JSON paths are JavaScript-like paths that navigate to a place within the JSON tree. A `JsonPath` holds a pre-parsed JSON
path. Suppose you have the following JSON:

```json
[
  {
    "name": "Lottie Mills",
    "friends": [
      { "id": 0, "name": "Priscilla Hahn" },
      { "id": 1, "name": "Odom Lynch" },
      { "id": 2, "name": "Rojas Mccormick" }
    ]
  },
  {
    "name": "Middleton Hayden",
    "friends": [
      { "id": 0, "name": "Charlene Munoz" },
      { "id": 1, "name": "Rios Casey" },
      { "id": 2, "name": "Chase Schroeder" }
    ]
  }
]
```

You want to find the name of the first friend of every person in the array. You can pre-parse and reuse a `JsonPath`
instance to save performance (just like compiling a `Pattern`).

```java
JsonNode tree = parseJsonInSomeWayOrAnother();
JsonPath path = JsonPath.parse("friends[0].name");

// Iteration over a JsonNode automatically asserts the node
// is an array, throwing an IncorrectTypeException in case
// it is not an array
for (JsonNode person : tree) {
    System.out.println(person.query(path).asString());
}

// Prints:
// Priscilla Hahn
// Charlene Munoz

// Another, less practical way to do it would be
for (JsonNode person : tree) {
    System.out.println(
        person.get("friends")
              .get(0)
              .get("name")
              .asString()
    );
}
```

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

## Documentation

Documentation is being worked on. Parts of the library are documented with JavaDoc comments. More documentation coming
in later versions.

I am working on hosting the compiled JavaDoc online.

## Changelog

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

**See LICENSE for full license**

Copyright 2021 Shadew

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.

[json-spec]: https://www.json.org/json-en.html

[json5-spec]: https://json5.org/
