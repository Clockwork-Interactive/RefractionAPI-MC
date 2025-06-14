package net.refractionapi.refraction.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Client self initialization. <br>
 * Gets cleared when leaving the world if #value is true.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InitSelf {
    boolean value();
}
