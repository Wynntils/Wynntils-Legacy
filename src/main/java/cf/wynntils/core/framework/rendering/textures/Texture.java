package cf.wynntils.core.framework.rendering.textures;

import cf.wynntils.core.utils.GenericActionResult;

public abstract class Texture {

    public boolean loaded = false;
    public float width,height;
    public abstract GenericActionResult load();
    public abstract GenericActionResult unload();
    public abstract GenericActionResult bind();
}
