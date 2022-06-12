# The template engine's language spec
The template engine features a handy way to generate JSON data based on a JSON-like template language. A basic specification of the language here.

## Example
The following template
```
[for i from 1 to 5 {   // 5 is exclusive
    i
}]
```
Generates the following JSON
```json
[
    1,
    2,
    3,
    4
]
```


## Design goals
- Should be easy to use and understand.
- Any JSON document and most JSON 5 documents (with the exception of unquoted key names being illegal) should be a valid teplate document, generating that exact JSON object.

# Base design
The template language takes advantage of the fact that anything in JSON is a comma-separated list of entries. An object is a list of key-value pairs, and an array is a list of values. The template language sticks to this design.

Any object or array, and any other context within the template language, is a list of several comma-separated entities. These lists form lists of instructions that modify the context. The types of entities allowed depends on the context (e.g. you may not use a key-value pair in an array). The language identifies the following entities:

- A value: only allowed in a root context, or an array. It instructs an array context to add the value to the array, or a root context to terminate evaluation and return with the value as result. It can be:
  - Primitive: `true`, `3.1415`, `null`, `"foo"`
  - An expression: `3 + 8`, `var * 2`
  - An array: `[ ... ]`
  - An object: `{ ... }`
- A key-value pair: only allowed in an object. It instructs an object context to add (or overwrite) a field with the value. The key must be a string (`"hello"`), a variable (`var`) or an expression in parentheses (`(3+4)`; will be [stringified](#string-representation)). The value can be anything an array/root value entry can be:
  - Primitive: `"bool": true`, `"pi": 3.1415`, `"null": null`, `"foo": "bar"`
  - An expression: `"num": 3 + 8`, `"var": var * 2`
  - An array: `"arr": [ ... ]`
  - An object: `"obj": { ... }`
- A void line: allowed anywhere. A void line is a line that evaluates an expression and then ignores the result. This is ideal for updating variables without having the result of the variable update generated.
  - `@ this = "a void line"`
  - `@ the_outcome_of("this expression") == "ignored"`
- A `if`-`else` block: allowed anywhere. This allows to use different entities based on a condition. More below.
- A `for` block: allowed anywhere. This allows to repeat a set of entities. More below.
- A `switch` block: allowed anywhere. This allows to pick a set of entities based on a value. More below.
- A function definition: allowed in the root, and in objects and arrays. It is not allowed in any `if`, `else`, `for` or `switch` block. While they are part of the list, they do not belong to the instructions in that list and are instead processed before the rest. More below.
- A `break` statement: allowed anywhere. In a loop, this immediately exists the loop. Outside of a loop, it indicates the object or array must be finished as-is, not adding any further entries.
- A `continue` statement: allowed in any loop. This immediately finishes the current iteration of the loop, not generating any further entries for that iteration.
- A `return` statement: allowed anywhere. Upon encounter, the generated document or sub-template value as it is will immediately be returned. In sub-templates, this will only finish the sub-template.

Some contexts define a comma-separated list but with its own entities, such as `switch`, `match` or inline `if` blocks.

# Features

## Value
Any literal JSON object is considered a value. In other words, the following entities are all values:
- A number
- A boolean
- `null`
- A string
- An object
- An array

### Exceptions
There is one more type of value that is only a valid value in the template language: the **exception**. An exception contains an erroneous value, usually an error message, or some other form of error data. If an exception is generated and no further processing is done to it, the template engine will directly insert the exception value into the JSON tree.

### Truthiness
Any value can be converted to a boolean given its truthiness.

- Any number except `0` and `NaN` are true
- The boolean value `true` is true ad the value `false` is false (duh)
- `null` is false
- Any non-empty string is true, `""` is false
- Any non-empty object is true, `{}` is false
- Any non-empty array is true, `[]` is false

### Size
Some values have a size (which can be read with the `#` operator).

- Numbers, booleans and null have no size
- The size of a string is its length
- The size of an array is its length
- The size of an object is the amount of unique keys that it has

### String representation
Any value can be converted to a string representation. This is done when they are concatenated to a string with the `+` operator. Objects and arrays can be converted to strings directly but this is highly discouraged.

- Numbers convert to a decimal representation: `3.1415` becomes `"3.1415"`
- The boolean value `true` converts to `"true"` and `false` converts to `"false"`
- Strings convert into themselves, since they are strings already
- Arrays convert to a comma-separated list of the string representations of its elements, wrapped in square brackets: `[1, "foo"]` becomes `"[1, foo]"`
- Objects convert to a comma-separated list of key-value pairs, wrapped in curly brackets, keys being unquoted, ending in `:` and values being their respective string representations: `{"a": 1, "b": "foo"}` becomes `"{a: 1, b: foo}"`

## Operator
An operator is a symbol that modifies or combines values.

- `a++`: 
- `( a )`: Parentheses: value can be anything (takes precedence over anything)
- `a[b]`: Indexing: left must be array or string and between brackets must a number (non-integers are rounded towards 0), or left must be an object and between brackets any value (see [String representation](#string-representation)); can be combined with `++`, `--` or an assignment
- `a.b`: Field access: left must be object and right must be an identifier (the identifier has a special meaning in this context)
- `a[b..c]`, `a[..b]`, `a[b..]`: Slicing: left must be array or string, and between brackets must be numbers (non-integers are rounded towards 0)
- `+ a`: Arithmetic identity: value must be a number
- `- a`: Arithmetic negation: value must be a number
- `! a`: Logical not: value can be anything (see [Truthiness](#truthiness))
- `~ a`: Bitwise not: value must be a number (it is rounded towards zero if it's not an integer)
- `# a`: Size: value must be a value that has a [Size](#size)
- `copy a`: Copy: value can be anything (note that this is always a deep copy, for consistency shallow copies are not by default included)
- `a * b`: Arithmetic multiply: values must be both numbers
- `a / b`: Arithmetic divide: values must be both numbers
- `a + b`:
  - Arithmetic add: values must be both numbers
  - Concatenation: one value must be a string (see [String representation](#string-representation)), or both must be either arrays or objects (note that if duplicate keys exist, the right hand object takes the precedence)
- `a - b`: Arithmetic subtract
- `a << b`: Bitwise left-shift: values must be both numbers (they are rounded towards zero if they're not integers)
- `a >> b`: Bitwise right-shift: values must be both numbers (they are rounded towards zero if they're not integers)
- `a >>> b`: Bitwise unsigned right-shift: values must be both numbers (they are rounded towards zero if they're not integers)
- `a < b`: Less than comparison: values must be both numbers
- `a > b`: Greater than comparison: values must be both numbers
- `a <= b`: Less than or equal to comparison: values must be both numbers
- `a >= b`: Greater than or equal to comparison: values must be both numbers
- `a == b`: Equality comparison: values can be anything
- `a != b`: Inequality comparison: values can be anything
- `a is type`: Type check: left hand can be anything, right hand must be an identifier: `num`, `bool`, `null`, `str`, `arr`, `obj` (this aren't reserved keywords, but have an overridden meaning only in this context; however `is` is reserved)
- `a isnt type`: Negated type check: left hand can be anything, right hand must be an identifier: `num`, `bool`, `null`, `str`, `arr`, `obj` (this aren't reserved keywords, but have an overridden meaning only in this context; however `isnt` is reserved)
- `a has key`: Key check: left hand must be object, right hand can be any value (see [String representation](#string-representation))
- `a hasnt key`: Key check: left hand must be object, right hand can be any value (see [String representation](#string-representation))
- `a & b`:
  - Bitwise and: values must be both numbers (they are rounded towards zero if they're not integers)
  - Raw logic and: values can be anything (see [Truthiness](#truthiness))
- `a ^ b`: Bitwise exclusive-or: values must be both numbers (they are rounded towards zero if they're not integers)
- `a | b`:
  - Bitwise or: values must be both numbers (they are rounded towards zero if they're not integers)
  - Raw logic or: values can be anything (see [Truthiness](#truthiness))
- `a && b`: Logic and: values can be anything (note that right side is not processed if left side is false)
- `a || b`: Logic or: values can be anything (note that right side is not processed if left side is true)
- `a ? b : c`: Conditional ternary operation: values can be anything (left operand is converted to boolean, see [Truthiness](#truthiness))
- `if {}`: Advanced conditional expression
- `match a {}`: Match expression
- `do {} then a`: Do-before block
- `a then do {}`: Do-after block
- `a ++`, `a --`: Add or subtract from variable and return old value: operand must be a field or variable reference
- `++ a`, `-- a`: Add or subtract from variable and return old value: operand must be a field or variable reference
- `a = b`, `a *= b`, `a /= b`, `a %= b`, `a += b`, `a -= b`, `a <<= b`, `a >>= b`, `a >>>= b`, `a &= b`, `a |= b`, `a ^= b`: Alter a variable or field with operator: left side must be a field or variable reference and right side can be any value (but exceptions will raise if the operator can't function on the variable's value and the given value, e.g. `a *= "str"` will never work).

### Operator precedence
- `(a)`
- `a[b]`, `a[b..c]`, `a[..b]`, `a[b..]`, `a.b`
- `+a`, `-a`, `!a`, `~a`, `#a`, `*a`, `copy a`
- `a*b`, `a/b`, `a%b`
- `a+b`, `a-b`
- `a<<b`, `a>>b`, `a>>>b`
- `a<b`, `a>b`, `a<=b`, `a>=b`
- `a==b`, `a!=b`, `a is type`, `a isnt type`, `a has key`, `a hasnt key`
- `a&b`
- `a^b`
- `a|b`
- `a&&b`
- `a||b`
- `a?b:c`
- `if {}`, `match a {}`, `do {} then a`, `a then do {}`

### On `&&` and `||`
The logic and (`&&`) and logic or (`||`) operators have special functionality, as they don't evaluate their right-hand side if the result of the operation is already known. That means that any changes to variables on that side won't occur.

- For `&&`, the right hand expression is only processed if the left hand expression evaluates to a truthy value. The result of the operator will be `true` if and only if both sides evaluate to a truthy value. In ternary operator terms: `a && b` is equivalent to `a ? (b ? true : false) : false`.
- For `||`, the right hand expression is only processed if the left hand expression evaluates to a not-truthy value. The result of the operator is not a boolean (unlike with `&&`), but is the left hand value if it's truthy and otherwise the right hand value. In ternary operator terms: `a || b` is equivalent to `a ? a : b`.

### Advanced conditional expression
The `if` keyword initiates an advanced conditional expression, where multiple conditions and outcomes can be defined.
```
if {
    case condition1 -> result1,
    case condition2 -> result2,
    case condition3 -> result3,
    
    else -> result4
}
```

If no `else` is defined, it will generate an exception if none of the conditions applies.

### Match
The `match` keyword initiates a match expression, where it compares a value to multiple other values and generates an outcome based on that.
```
match value {
    case value1 -> result1,
    case value2 -> result2,
    case value3 -> result3,
    
    else -> result4
}
```

If no `else` is defined, it will generate an exception if none of the conditions applies.

### Do-before and do-after blocks
The `then` keyword can initiate a do-before or do-after block, where variables can be altered or defined. Variables assigned here are assigned in the scope of the containing object or array. Only variables may be assigned in this block, functions are illegal.
```
var = 3
do {
    var += 3
} then var     // 6
```

These blocks are especially useful to update variables in a function:
```
var = 0
incr_get() -> var then do { var += 1 }

// However, this can be done like this as well:
// incr_get() -> var ++
```

## Comments
Comments are sections in the document that aren't parsed or evaluated, and can be used to annotate the document. They come in two forms: line comments and block comments.

Line comments start with a `//` symbol and stretch until the next line break, or the end of the document.
```
{
    // Hi, this is a line comment, I am not part of the document
    "this": "is part of the document"
}
```

Block comments start with a `/*` symbol and stretch until a `*/` symbol. It is illegal to not close a block comment.
```
{
    /* Hi this is a block comment, I am not part of the document
    I can span multiple lines
    "this": "is not parsed so have something that would otherwise be a syntax error: ',
    The block comment ends here: */
    "this": "is part of the document"
}
```

Comments cannot exist in strings, they are instead part of the string literal:
```
[
    "// I am not a comment",
    "/* Neither am I */"
]
```

## Variables
Variables are entities that store a value that isn't present in the JSON tree generated by the template. They can be
defined in the template or externally.

A variable can be accessed with its identifier.
```
name_of_the_variable
```

Variables can be defined by assigning them with the `=` symbol, and can be defined anywhere in the document.
```
name_of_the_variable = "value of the variable"
```

Note that anywhere, except in a do-before/-after block, you will have to write the variable definition on a void line to avoid the assignment from being part of the generated JSON data.
```
[  // This array will become: ["value 2"]
    @ name_1 = "value 1",
    name_2 = "value 2"
]
```

Defining a variable in an object without a void line is illegal and gives a syntax error.
```
{
    name = "value"  // Syntax error: illegal expression
}
```

Accessing an undefined variable will generate an exception.
```
{
    "key": value  // Generates an exception
}
```

Variables defined in an object or array cannot be accessed outside of that scope.
```
{
    "key": [
        @ variable = 3,
        variable
    ],
    "var": variable  // Generates an exception
}
```

Variables can be reassigned in an inner scope, and will reset to its old value outside of that scope.
```
{
    @ variable = 5,
    "key": [
        @ variable = 3,
        variable     // 3
    ],
    "var": variable  // 5
}
```

Variables can be reassigned in the same scope, and any reference of the variable after that will have the new value.
```
{
    @ variable = 5,
    "var1": variable, // 5
    @ variable = 3,
    "var2": variable  // 3
}
```

Keep in mind that forward referencing is not allowed.
```
{
    "key": value,  // Generates an exception
    @ value = 3
}
```

## Function
A function is an entity that takes in zero or more values as arguments, and produces another value. They can be defined
in the template or externally. Examples of function calls (the parentheses are always required):

```
name_of_the_function("argument", "another argument")
name_of_a_function_that_does_not_take_arguments()
```

A function can be defined with the `def` keyword and the `->` symbol.
```
def name_of_the_function(arg1, arg2) -> arg1 + arg2
```

Note that defining a function is not an expression and therefore must not happen in a void line.
```
@ def function() -> 3  // Syntax error: unexpected 'def'
```

A function can, unlike variables, not be reassigned.
```
def function() -> 3

def function() -> 6  // Syntax error: function reassignment
```

However, a function can be overloaded. The best matching function will be used.
```
def function() -> 4,
def function(a) -> 4 + a
```

And unlike variables, functions may be forward-referenced.
```
{
    "key": function(),  // Does not generate exception
    def function() -> 4
}
```

Like variables, functions defined in an inner scope are not accessible in an outer scope.
```
{
    "key1": {
        def function(a) -> a,
        "key": function(3)
    },
    "key2": function(6)  // Exception: function overload with one parameter is not defined
}
```

Overloading a function in an inner scope is illegal, as it is seen as a function reassignment. All overloads must be defined in the same scope.
```
def function() -> 4,
{
    def function(a) -> 4 + a  // Syntax error: function reassignment
}
```

A function can also be defined with a sub-template. This sub-template is evaluated just like a usual template.
```
def function(a) {
    @ b = a,
    @ b *= 3,
    @ b += 1,
    a + b
}
```

## String literals
Double-quoted string literals can be interpolated with inline expressions. This applies to string values, as well as object keys. Single quoted strings stay raw. The value evaluated inline expressions is concatenated in the string in its [stringified form](#string-representation).

```
{
    @ a = 3,
    "key": "a = #[a]"   // The string will be 'a = 3'
}
```

An inline expression can be escaped, which will turn the expression into raw string content, which is not parsed.
```
"a = \#[a]"   // The string will be 'a = #[a]'
```

While a basic string literal may not be multiline itself, the inline expression may be multiline.
```
"a = #[
    a
]"
```

### Multiline strings
A multiline string is defined with a triple delimiter: `"""` or `'''`. Double quoted delimiters, once again, allow for interpolation. After the opening delimiter must follow a line break. The first line starts on the next line. The closing delimiter must also be on a new line. Any line break is converted to a `\n` character, to unify system dependent line breaks in the document.

```
@ multiline_string = """
I am multiline.
Yay.
""",
multiline_string  // Will generate 'I am multiline.\nYay.'
```

The indentation of the closing delimiter, or the least indented line (whichever is less indented), defines the base indentation of the string, which is stripped. Tab characters are illegal between the delimiters, to avoid confusion in indentation.

```
@ multiline_string = """
    I am multiline.
        This line is indented.
    """,
multiline_string  // Will generate 'I am multiline.\n    This line is indented.'
```

Trailing spaces on lines are trimmed. To preserve trailing spaces, the end of the line must be indicated with a backslash.
```
@ multiline_string = """
    Spaces before the line break.    \
    Yay.
    """,
multiline_string  // Will generate 'Spaces before the line break.    \nYay.'
```

A newline can be removed for wrapping by escaping a tilde at the end of the line.
```
@ multiline_string = """
    No line break. \~
    Yay.
    """,
multiline_string  // Will generate 'No line break. Yay.'
```

## Object and array literals, and the root scope
Object and array literals are entities that define and object or array value. The root scope is the scope in which the
generated JSON tree is defined, but note that sub-template functions also define a root scope.

All three have similar design principles: a comma-separated list of instructions.
```
a, b, c
```

Extra comma's are ignored.
```
, a, b,, c,,
```

Each entry in the list is evaluated as encountered. The allowed types of entries differs per scope. Note that entries with blocks **must** also be delimited with commas.

### Void line entry
Void lines are lines with expressions that **should not** be included in the final output. They are evaluated each time they are encountered. A void line starts with an `@` symbol and is terminated by the end of the scope or a `,`. They are only legal in array and object literals.
```
@ "this is a void line, this string will never be in the generated document"
```

### Expression entry
Expression entries are entries with expressions that **should** be included in the final output. They are evaluated each time they are encountered. They are only valid in an array or the root scope. In an array, it adds the evaluated value to the scope array. In the root scope, it will terminate the template evaluation immediately and the generated value will be the resulting JSON tree (after processing of all the exceptions).
```
"this is an expression entry, this string will return in the generated document"
```

### Key-value entry
Key-value entries are entries with expressions that **should** be included in the final output, as part of an object. They are evaluated each time they are encountered. They are only valid in an object, where it adds the evaluated value under the evaluated key to the object. The key must be a string literal (interpolation and multiline are allowed), a variable (whose value will be the key), or an expression between parentheses.
```
"key": "this is a key-value entry, this string will return in the generated document",
var: "this entry is named after a variable value",
(a + b): "this entry is named after an expression result"
```

### Function definition entry
Function definition entries are entries that define a function.
```
def function() -> 3
```

### Conditional entry
Conditional entries (`if`-`else` blocks) define branching based on one or more conditions. Each branch defines a list of entries that must be included when that condition holds. These entries must be valid in the scope it is placed in. The curly brackets are required.
```
if a {
    // Entries here
} else if b {
    // Entries here
} else {
    // Entries here
}
```

### Iterating entry
Iterating entries (`for` blocks) repeat entries based on an iteration.

They can iterate over a string, iterating the individual characters of the string.
```
[for char in 'hello world' {
    char
}]  // ['h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd']
```

They can iterate over an array, iterating the individual elements of the array.
```
[for elem in [3, 6, 9] {
    elem / 3
}]  // [1, 2, 3]
```

They can iterate over a range of numbers (`from` is inclusive, `to` is exclusive). When `to` is less than `from`, it will iterate backwards.
```
[for i from 1 to 5 {
    i
}]  // [1, 2, 3, 4, 5]
```

They can iterate over an object. The iteration order is the order in which the keys are inserted in the object. If a key is overridden, it will not be moved.
```
[for key:value in {"a": 1, "b": 2, "c": 3, "b": 'override'} {
    "#[key] = #[value]"
}]  // ["a = 1", "b = override", "c = 3"]
```

### Switch entry
Switch entries (`switch` blocks) generate entries for the given value. The `else` branch generates entries if none matches, other branches are matched in order. This is the statement version of the `match` block.
```
@ a = 3,
[switch a {
    case 1 { "a is 1" },
    case 2 { "a is 2" },
    case 3 { "a is 3" },
    else { "a is something else" }
}]  // ["a is 3"]
```

If the `else` block is omitted but no entry in the switch block matches, then no entries are generated.
```
@ a = 4,
[switch a {
    case 1 { "a is 1" },
    case 2 { "a is 2" },
    case 3 { "a is 3" }
}]  // []
```

## The `$` sign
The `$` sign refers to the root of the document that is at the very moment being generated. In the root scope, using this generates an exception. Note that if there are multiple root elements (due to branching), it will refer to the one it is used in. This does not carry into sub-template functions; it must be assigned to a variable before use there.

## The `_` sign
The `_` sign refers to the object or array hosting the current scope. In the root scope, using this generates an exception. This does not carry into sub-template functions; it must be assigned to a variable before use there. This is especially useful in arrays or objects to add elements just in case former operations did not generate entries:
```
@ messages = get_log_messages(),
[
    for i in messages {
        if message.level == "error" {
            message.value
        }
    }
    
    if #_ == 0 {
        "No error messages"
    }
]
```

## On infinite recursion
At the moment infinite recursion is detected, an exception is generated.
```
[ _, _, _ ]  // This adds an array to itself 3 times, which instead generates an exception 3 times
```

The above example can be made valid by copying:
```
[ copy _, copy _, copy _ ]
// This becomes [[], [[]], [[], [[]]]]
```

## The `gen` block
The `gen` block runs an entire sub-template immediately, inline. This is especially useful for some advanced logic, since it will return a value immediately after it has been generated, ignoring any further steps. Note however that `_` and `$` are not inherited and must be assigned to a variable first.

```
{
    "gen": gen {
        if ext_var {
            3
        },
        
        for i in some_list {
            if can_generate(i) {
                i
            }
        },
        
        "fallback"
    }
}
```

## Alteration of values
Array and object values are mutable. Therefore, one can alter them. It is important to remember that their instances are not copied unless they go through the `copy` operator, which makes a deep copy of them. Shallow copies are discouraged, and therefore not an operator, but may be added externally through a function. If a value is not copied, but referenced multiple times, it will update everywhere when it's altered later on.

```
@ var = {"key": 3}
[
    var.key,   // 'key' is the one being altered, its value isn't; this will stay 3
    var.key,
    copy var,  // The copy will not be altered; this will stay {"key": 3}
    copy var,
    var,       // The value of var is being altered; this will become {"key": 6}
    var,
    
    @ var.key = 6
]  // Generates [3, 3, {"key": 3}, {"key": 3}, {"key": 6}, {"key": 6}]
```

Since manual alteration of values is usually not needed and in most cases discouraged, you'll usually not get in touch with this semantic. But it is important to keep in mind when you do alter values, and especially with the use of `_` and `$`.

## Numbers
Numbers are an important feature of templates. Numbers must at least keep the precision of 64-bits floating point numbers. This means that any more precise numbers lose precision.
