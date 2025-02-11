package net.refractionapi.refraction.feature.examples.reconfig;

import net.refractionapi.refraction.feature.reconfig.RCBuilder;
import net.refractionapi.refraction.feature.reconfig.RCDouble;
import net.refractionapi.refraction.feature.reconfig.RCMap;
import net.refractionapi.refraction.feature.reconfig.RCString;

import java.util.Map;

public class ReConfigExample {
    public static final RCBuilder builder = new RCBuilder();
    public static final RCMap<RCString, RCDouble> EXAMPLE;

    static {
        EXAMPLE = builder.set("example", "Example string list", RCString.class, RCDouble.class, Map.of(
                new RCString("key1"), new RCDouble(1.0),
                new RCString("key2"), new RCDouble(2.0)
        )).build();
    }
}
