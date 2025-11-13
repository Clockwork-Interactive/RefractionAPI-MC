package net.refractionapi.refraction.feature.examples.twc;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.twc.TWC;

public class ExampleTWC {
    public static final ResourceLocation API_KEY = Refraction.id("api");
    public static TWC.Static API = TWC.named(API_KEY)
            .configureClient((twc) -> {
               twc.listener("example", (msg) -> {});
            })
            .configureServer((twc) -> {
                twc.markSendOnly();
                twc.preSendHook((msg) -> msg.header());
            });
}
