/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.rendering.textures;

import com.wynntils.core.framework.enums.ActionResult;

public abstract class Texture {

    public boolean loaded = false;
    public float width, height;
    public abstract ActionResult load();
    public abstract ActionResult unload();
    public abstract ActionResult bind();

}
