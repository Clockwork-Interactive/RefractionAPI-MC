package net.refractionapi.refraction.feature.examples.twc;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.twc.TWC;

public class ExampleTWC {
    public static final ResourceLocation API_KEY = Refraction.id("api");
    public static TWC.Sided API = TWC.named(API_KEY)
            .configure((twc) -> {
                twc.listener("example_common", ExampleTWC::receive);
            })
            .configureClient((twc) -> {
               twc.listener("example_client", ExampleTWC::receive);
            })
            .configureServer((twc) -> {
                twc.listener("example_server", ExampleTWC::receive);
            }).initCommon();

    private static void receive(TWC.Message msg) {
        Refraction.LOGGER.info("Received TWC Message: {} in dist {}", msg.nbt().get("data"), msg.isClient() ? "Client" : "Server");
    }

    private static void sendFor(String listener) {
        TWC.Message msg = TWC.message()
                .nbt((nbt) -> nbt.putString("data", "Hello to " + listener));
        API.sendMessage(listener, msg);
    }
}
