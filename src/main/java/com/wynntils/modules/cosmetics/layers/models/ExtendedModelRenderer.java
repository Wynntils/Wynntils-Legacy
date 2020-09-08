package com.wynntils.modules.cosmetics.layers.models;

import com.google.common.collect.Lists;

import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelRenderer;

import javax.jws.WebParam;


public class ExtendedModelRenderer extends ModelRenderer {

    /** The first X offset into the texture used for displaying this model */
    private int textureOffsetX1;
    /** The first Y offset into the texture used for displaying this model */
    private int textureOffsetY1;
    /** The second X offset into the texture used for displaying this model */
    private int textureOffsetX2;
    /** The second Y offset into the texture used for displaying this model */
    private int textureOffsetY2;


    public ExtendedModelRenderer(ModelBase model, String boxNameIn) {
        super(model, boxNameIn);


        this.cubeList = Lists.<ModelBox>newArrayList();
    }

    public ExtendedModelRenderer(ModelBase model)
    {
        this(model, (String)null);
    }

    public ExtendedModelRenderer(ModelBase model, int texOffX1, int texOffY1, int texOffX2, int texOffY2)
    {
        this(model);
        this.setTextureOffset(texOffX1, texOffY1, texOffX2, texOffY2);
    }

    public ExtendedModelRenderer setTextureOffset(int x1, int y1, int x2, int y2)
    {
        this.textureOffsetX1 = x1;
        this.textureOffsetY1 = y1;
        this.textureOffsetX2 = x2;
        this.textureOffsetY2 = y2;
        return this;
    }

    public ExtendedModelRenderer addEars(float offX, float offY, float offZ, int width, int height, int depth)
    {
        this.cubeList.add(new ExtendedModelBox(this, this.textureOffsetX1, this.textureOffsetY1, this.textureOffsetX2, this.textureOffsetY2, offX, offY, offZ, width, height, depth, 0.0f, false));
        return this;

    }

    public ModelRenderer returnRenderer() {
        return this;
    }
}