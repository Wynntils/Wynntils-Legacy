package com.wynntils.modules.cosmetics.layers.models;

import com.google.common.collect.Lists;
import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

class ExtendedModelRenderer extends ModelRenderer {
    public ExtendedModelRenderer(ModelBase model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }
}