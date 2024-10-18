package net.refractionapi.refraction.helper.math;

public interface EasingFunction {

    default float getEasing(float x) {
        return x;
    }

}
