/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.cosmetics.layers.models;

import com.google.common.collect.Lists;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class EarModelRenderer extends ModelRenderer {

    /** The first X offset into the texture used for displaying this model */
    private int textureOffsetX1;
    /** The first Y offset into the texture used for displaying this model */
    private int textureOffsetY1;
    /** The second X offset into the texture used for displaying this model */
    private int textureOffsetX2;
    /** The second Y offset into the texture used for displaying this model */
    private int textureOffsetY2;


    public EarModelRenderer(ModelBase model, String boxNameIn) {
        super(model, boxNameIn);

        this.cubeList = Lists.newArrayList();
    }

    public EarModelRenderer(ModelBase model)
    {
        this(model, null);
    }

    public EarModelRenderer(ModelBase model, int texOffX1, int texOffY1, int texOffX2, int texOffY2) {
        this(model);

        this.setTextureOffset(texOffX1, texOffY1, texOffX2, texOffY2);
    }

    public EarModelRenderer setTextureOffset(int x1, int y1, int x2, int y2) {
        this.textureOffsetX1 = x1;
        this.textureOffsetY1 = y1;
        this.textureOffsetX2 = x2;
        this.textureOffsetY2 = y2;
        return this;
    }

    public EarModelRenderer addEars(float offX, float offY, float offZ, int width, int height, int depth) {
        this.cubeList.add(new EarModelBox(this, this.textureOffsetX1, this.textureOffsetY1, this.textureOffsetX2, this.textureOffsetY2, offX, offY, offZ, width, height, depth, 0.0f, false));
        return this;
    }

    public ModelRenderer returnRenderer() {
        return this;
    }

}
