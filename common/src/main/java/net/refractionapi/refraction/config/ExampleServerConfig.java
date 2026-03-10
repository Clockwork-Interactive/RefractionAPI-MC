package net.refractionapi.refraction.config;

import net.refractionapi.refraction.feature.config.Reconfig;
import net.refractionapi.refraction.feature.config.values.RString;

public class ExampleServerConfig {
    public static RString EXAMPLE_STRING;
    public static final Reconfig EXAMPLE = Reconfig.create(builder -> {
        builder.push("Examples");
        EXAMPLE_STRING = builder.comment("An example string configuration")
                .define("example_string", "default_value");
        builder.pop();
    });
}
