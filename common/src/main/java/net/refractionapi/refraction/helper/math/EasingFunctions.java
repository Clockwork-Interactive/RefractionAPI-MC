package net.refractionapi.refraction.helper.math;

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
    EASE_IN_QUINT {
        @Override
        public float getEasing(float x) {
            return x * x * x * x * x;
        }
    },
    EASE_OUT_QUINT {
        @Override
        public float getEasing(float x) {
            return (float) (1 - Math.pow(1 - x, 5));
        }
    },
    EASE_IN_OUT_QUINT {
        @Override
        public float getEasing(float x) {
            return x < 0.5 ? 16 * x * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 5) / 2);
        }
    },
    EASE_IN_CIRC {
        @Override
        public float getEasing(float x) {
            return 1 - (float) Math.sqrt(1 - Math.pow(x, 2));
        }
    },
    EASE_OUT_CIRC {
        @Override
        public float getEasing(float x) {
            return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
        }
    },
    EASE_IN_OUT_CIRC {
        @Override
        public float getEasing(float x) {
            return x < 0.5 ? (float) (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (float) (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
        }
    },
    EASE_IN_ELASTIC {
        @Override
        public float getEasing(float x) {
            double c4 = (2 * Math.PI) / 3;

            return x == 0
                    ? 0
                    : (float) (x == 1
                    ? 1
                    : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4));
        }
    },
    EASE_OUT_ELASTIC {
        @Override
        public float getEasing(float x) {
            double c4 = (2 * Math.PI) / 3;

            return x == 0
                    ? 0
                    : (float) (x == 1
                    ? 1
                    : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);
        }
    },
    EASE_IN_OUT_ELASTIC {
        @Override
        public float getEasing(float x) {
            double c5 = (2 * Math.PI) / 4.5;

            return x == 0
                    ? 0
                    : (float) (x == 1
                    ? 1
                    : x < 0.5
                    ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2
                    : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1);
        }
    },
    EASE_IN_QUAD {
        @Override
        public float getEasing(float x) {
            return x * x;
        }
    },
    EASE_OUT_QUAD {
        @Override
        public float getEasing(float x) {
            return (float) (1 - Math.pow(1 - x, 2));
        }
    },
    EASE_IN_OUT_QUAD {
        @Override
        public float getEasing(float x) {
            return x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
        }
    },
    EASE_IN_QUART {
        @Override
        public float getEasing(float x) {
            return x * x * x * x;
        }
    },
    EASE_OUT_QUART {
        @Override
        public float getEasing(float x) {
            return (float) (1 - Math.pow(1 - x, 4));
        }
    },
    EASE_IN_OUT_QUART {
        @Override
        public float getEasing(float x) {
            return x < 0.5 ? 8 * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 4) / 2);
        }
    },
    EASE_IN_EXPO {
        @Override
        public float getEasing(float x) {
            return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
        }
    },
    EASE_OUT_EXPO {
        @Override
        public float getEasing(float x) {
            return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
        }
    },
    EASE_IN_OUT_EXPO {
        @Override
        public float getEasing(float x) {
            return x == 0
                    ? 0
                    : (float) (x == 1
                    ? 1
                    : x < 0.5
                    ? Math.pow(2, 20 * x - 10) / 2
                    : (2 - Math.pow(2, -20 * x + 10)) / 2);
        }
    },
    EASE_IN_BACK {
        @Override
        public float getEasing(float x) {
            double c1 = 1.70158;
            double c3 = c1 + 1;

            return (float) (c3 * x * x * x - c1 * x * x);
        }
    },
    EASE_OUT_BACK {
        @Override
        public float getEasing(float x) {
            double c1 = 1.70158;
            double c3 = c1 + 1;

            return (float) (1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
        }
    },
    EASE_IN_OUT_BACK {
        @Override
        public float getEasing(float x) {
            double c1 = 1.70158;
            double c2 = c1 * 1.525;

            return (float) (x < 0.5
                                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);
        }
    },
}
