/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.ui.elements;

import com.wynntils.core.framework.ui.UIElement;

import java.util.*;

public class UIEList extends UIElement {
    public List<UIElement> elements;

    public UIEList(float anchorX, float anchorY, int offsetX, int offsetY) {
        this(new ArrayList<>(), anchorX, anchorY, offsetX, offsetY);
    }
    public UIEList(List<UIElement> elements, float anchorX, float anchorY, int offsetX, int offsetY) {
        super(anchorX, anchorY, offsetX, offsetY);
        this.elements = elements;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        for (UIElement uie : this.elements) {
            uie.position.anchorX = this.position.anchorX;
            uie.position.anchorY = this.position.anchorY;
            uie.position.offsetX += this.position.offsetX;
            uie.position.offsetY += this.position.offsetY;
            uie.position.refresh(screen);
            if(!(uie instanceof UIEList)) {
                uie.position.offsetX -= this.position.offsetX;
                uie.position.offsetY -= this.position.offsetY;
            }
            if(!uie.visible) continue;
            uie.render(mouseX, mouseY);
        }
    }

    @Override
    public void tick(long ticks) {
        for(UIElement uie : elements)
            uie.tick(ticks);
    }

    public void add(UIElement uie) {
        this.elements.add(uie);
    }
}
