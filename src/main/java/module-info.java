module dev.runefox.json {
    exports dev.runefox.json;
    exports dev.runefox.json.codec;

    // Kotlin extension is allowed to use impl classes
    exports dev.runefox.json.impl to dev.runefox.json.kt;

    requires java.base;
}
