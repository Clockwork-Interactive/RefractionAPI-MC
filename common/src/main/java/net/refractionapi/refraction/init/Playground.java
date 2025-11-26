package net.refractionapi.refraction.init;

import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.feature.twc.TWC;
import net.refractionapi.refraction.platform.RefractionServices;

public class Playground {
    private static final TWC.Sided API = new TWC.Sided(Refraction.id("playground"))
            .configure((twc) -> {
            })
            .configureClient((twc) -> {
            })
            .configureServer((twc) -> {
            });

    public static void init() {
        RRuntimeConfig.debugTools = true;
        new TestHooks();
        if (RefractionServices.PLATFORM.isClient()) new TestHooksClient();
        API.initCommon();
    }
}
