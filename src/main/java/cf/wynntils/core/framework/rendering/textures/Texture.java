package cf.wynntils.core.framework.rendering.textures;

import cf.wynntils.core.framework.enums.ActionResult;

public abstract class Texture {

    public boolean loaded = false;
    public float width,height;
    public abstract ActionResult load();
    public abstract ActionResult unload();
    public abstract ActionResult bind();

}
