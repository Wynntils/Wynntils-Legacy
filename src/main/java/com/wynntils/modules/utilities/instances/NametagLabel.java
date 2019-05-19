/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.instances;

import com.wynntils.core.framework.rendering.colors.CustomColor;

/**
 * This is a container class which stores nametag label informations
 */
public class NametagLabel {

    public CustomColor color;
    public String text;
    public float scale;

    public NametagLabel(CustomColor color, String text, float scale) {
        this.color = color; this.text = text; this.scale = scale;
    }

}
