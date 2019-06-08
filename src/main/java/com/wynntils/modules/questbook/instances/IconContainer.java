package com.wynntils.modules.questbook.instances;

import com.wynntils.core.framework.rendering.textures.Texture;
import com.wynntils.core.framework.rendering.textures.Textures;

public class IconContainer {

    public static IconContainer questPageIcon = new IconContainer(Textures.UIs.quest_book, 2, 26, 220, 256);

    public Texture texture;
    public int x1, x2, y1, y2;

    public IconContainer(Texture texture, int x1, int x2, int y1, int y2) {
        this.texture = texture;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
}
