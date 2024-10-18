package net.refractionapi.refraction.helper.math;

import net.minecraft.util.RandomSource;

public class MathHelper {

    public static final RandomSource randomSource = RandomSource.createNewThreadLocalInstance();

    /**
     * Gets either 1 or -1
     */
    public static int getRandomOne() {
        return randomSource.nextIntBetweenInclusive(0, 1) * 2 - 1;
    }


}
