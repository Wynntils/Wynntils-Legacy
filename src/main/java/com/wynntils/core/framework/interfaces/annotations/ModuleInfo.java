/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.interfaces.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {

    String name();
    String displayName();
}
