package net.refractionapi.refraction;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Refraction.MOD_ID)
public class Refraction
{
    public static final Logger LOGGER = LogManager.getLogger("Refraction API");
    public static final String MOD_ID = "refraction";

    public Refraction() {

        MinecraftForge.EVENT_BUS.register(this);
    }

}
