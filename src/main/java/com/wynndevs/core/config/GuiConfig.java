package com.wynndevs.core.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GuiConfig {

    String title();
    boolean isInstance() default false;

}
