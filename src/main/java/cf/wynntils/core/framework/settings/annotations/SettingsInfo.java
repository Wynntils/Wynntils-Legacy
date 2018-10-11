package cf.wynntils.core.framework.settings.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
