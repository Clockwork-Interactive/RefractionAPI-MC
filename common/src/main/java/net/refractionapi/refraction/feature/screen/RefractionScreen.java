package net.refractionapi.refraction.feature.screen;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.client.ClientData;

import java.util.Arrays;
import java.util.HashMap;

@Deprecated
public interface RefractionScreen {
    void handleServerEvent(Code code, CompoundTag tag);

    default void sendData(CompoundTag tag) {
        ClientData.screenHandler.sendData(tag);
    }

    default boolean screenClosingAuthority() {
        return true;
    }

    default Code getCode(CompoundTag tag) {
        return Code.codes.getOrDefault(tag.getString("code"), Code.INVALID);
    }

    enum Code {
        CLOSE("close"),
        OPEN("open"),
        REOPEN("reopen"),
        DATA("data"),
        INVALID("invalid");

        private final String identifier;
        private static final HashMap<String, Code> codes = new HashMap<>(Code.values().length) {{
            Arrays.stream(Code.values()).forEach(code -> put(code.identifier, code));
        }};

        Code(String identifier) {
            this.identifier = identifier;
        }
    }
}
