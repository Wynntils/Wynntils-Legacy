package cf.wynntils.core.framework.settings.annotations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by HeyZeer0 on 24/03/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SettingsInfo {
    String name();
    String displayPath() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @interface Instance {}
}
