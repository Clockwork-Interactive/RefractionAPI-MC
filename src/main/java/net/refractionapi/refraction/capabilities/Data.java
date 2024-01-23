package net.refractionapi.refraction.capabilities;

import net.minecraft.nbt.CompoundTag;

/**
 * You should probably mark this with @AutoRegisterCapability. <br>
 * Create a Capabilities class and register it there. <br>
 * Example: <br>
 * <pre>
 *     public class Capabilities {
 *          public static Capability<Data> DATA = CapabilityManager.get(new CapabilityToken() {});
 *     }
 * </pre>
 */
public abstract class Data<T extends Data<T>> {

    public abstract void saveNBTData(CompoundTag tag);

    public abstract void loadNBTData(CompoundTag tag);

    public abstract void copyFromData(T data);

}
