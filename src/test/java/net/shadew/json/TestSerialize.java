package net.shadew.json;

import net.shadew.json.annotation.Factory;
import net.shadew.json.annotation.Opt;

public class TestSerialize {
    @Factory("henlo")
    public TestSerialize(@Opt int i) {
    }
}
