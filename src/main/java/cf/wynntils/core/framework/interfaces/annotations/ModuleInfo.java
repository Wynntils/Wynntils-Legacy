package cf.wynntils.core.framework.interfaces.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

    String name();

}
