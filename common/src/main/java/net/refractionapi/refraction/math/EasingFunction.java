package net.refractionapi.refraction.math;

public interface EasingFunction {

    default float getEasing(float x) {
        return x;
    }

}
