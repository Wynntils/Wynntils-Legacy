package cf.wynntils.core.framework.interfaces.annotations;

import cf.wynntils.core.framework.enums.Priority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    Priority priority() default Priority.NORMAL;

}
