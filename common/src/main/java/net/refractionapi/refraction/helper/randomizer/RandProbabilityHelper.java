package net.refractionapi.refraction.helper.randomizer;

import java.util.Random;

public class RandProbabilityHelper {
    public static final Random RANDOM = new Random();

    /**
     * I would recommend using some graphing tool before applying values to this method. <br>
     * This method uses an inverse Parabolic curve to make an ease out like curve to increase the probability over time. <br>
     * Time should be stored externally. The formula is M(1-(x^2/b^2)), where M is maxProb and T is maxTime
     *
     * @param time Being the time of the expression.
     * @param maxProb Maximum probability at x=0. Cannot be negative. Cannot=0, unless you want 100% probability all the time.
     * @param maxTime Changes the maximum time of the equation, once x reaches this, the chances of this to return true is 100%, Cannot=0
     * @return True if an action should be taken.
     */
    public static boolean inverseParabolicProbability(double time, double maxProb, double maxTime) {
        //I know this throw doesn't have to be here, but it's better to avoid even having the computer even try when we can just throw straight away
        if (maxTime == 0.0D || maxProb < 0.0D) throw new IllegalStateException("Invalid properties for curve, either maxProb is less than 0, or maxTime = 0!");
        int i = (int) Math.floor(maxProb*(1-(square(time)/square(maxTime))));
        return i <= 0 || RANDOM.nextInt(i) == 0;
    }

    public static double square(double value) {
        return value * value;
    }
}
