/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {

    String displayName() default "";
    String description() default "";
    boolean upload() default true;
    int order() default 100;

    class Limitations {
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface IntLimit {
            int min();
            int max();
            int precision() default 1;
        }
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface FloatLimit {
            float min();
            float max();
            float precision() default 0.1f;
        }
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface DoubleLimit {
            double min();
            double max();
            double precision() default 0.1d;
        }
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface StringLimit {
            int maxLength();
        }
    }

    class Features {
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        public @interface StringParameters {
            String[] parameters();
        }
    }
}
