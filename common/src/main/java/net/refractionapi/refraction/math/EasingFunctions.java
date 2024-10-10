package net.refractionapi.refraction.math;

import net.minecraft.util.Mth;

public enum EasingFunctions implements EasingFunction {
    LINEAR {
        @Override
        public float getEasing(float x) {
            return x;
        }
    },
    EASE_IN_SINE {
        @Override
        public float getEasing(float x) {
            return 1 - Mth.cos((x * Mth.PI) / 2);
        }
    },
    EASE_OUT_SINE {
        @Override
        public float getEasing(float x) {
            return (float) Math.sin((x * Math.PI) / 2);
        }
    },
    EASE_IN_OUT_SINE {
        @Override
        public float getEasing(float x) {
            return (float) -(Math.cos(Math.PI * x) - 1) / 2;
        }
    },
    EASE_IN_CUBIC {
        @Override
        public float getEasing(float x) {
            return x * x * x;
        }
    },
    EASE_OUT_CUBIC {
        @Override
        public float getEasing(float x) {
            return (float) (1 - Math.pow(1 - x, 3));
        }
    },
    EASE_IN_OUT_CUBIC {
        @Override
        public float getEasing(float x) {
            return x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
        }
    },
}
