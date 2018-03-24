package cf.wynntils.core.framework.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {

    String displayName();
    String description();

    class Limitations {
        public @interface IntLimit {
            int min();
            int max();
            int precision() default 1;
        }
        public @interface FloatLimit {
            float min();
            float max();
            float precision() default 0.1f;
        }
        public @interface DoubleLimit {
            double min();
            double max();
            double precision() default 0.1d;
        }
        public @interface StringLimit {
            int minLength() default 0;
            int maxLength();
        }
        public @interface EnumLimit {
            boolean allowNull() default false;
        }
    }

    class Features {
        public @interface StringParameters {
            String[] parameters();
        }
    }
}
