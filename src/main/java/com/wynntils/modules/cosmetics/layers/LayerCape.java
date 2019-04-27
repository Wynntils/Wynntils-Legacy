/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics.layers;

import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class LayerCape implements LayerRenderer<AbstractClientPlayer>
{
    private final RenderPlayer playerRenderer;
    private ModelRenderer bipedCape;

    public LayerCape(RenderPlayer playerRendererIn)
    {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(net.minecraft.client.entity.AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (!Minecraft.getMinecraft().gameSettings.getModelParts().toString().contains("CAPE")) return;
        //loading cape
        ResourceLocation rl;
        if (WebManager.hasCape(entitylivingbaseIn.getUniqueID())) {
            rl = new ResourceLocation("wynntils:capes/" + entitylivingbaseIn.getUniqueID().toString().replace("-", ""));
        }else{ return; }

        //applying cape
        if (rl != null && entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible())
        {
            ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

            if (itemstack.getItem() != Items.ELYTRA)
            {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.playerRenderer.bindTexture(rl);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.125F);
                double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)partialTicks);
                double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double)partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double)partialTicks);
                double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)partialTicks);
                float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
                double d3 = (double) MathHelper.sin(f * 0.017453292F);
                double d4 = (double)(-MathHelper.cos(f * 0.017453292F));
                float f1 = (float)d1 * 10.0F;
                f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;

                if (f2 < 0.0F)
                {
                    f2 = 0.0F;
                }

                // Clamping f2 and f3 ...
                f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
                f3 = MathHelper.clamp(f3, 0.0F, 50.0F);

                float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
                f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

                if (entitylivingbaseIn.isSneaking())
                {
                    f1 += 15.0F;
                }

                GlStateManager.rotate((6.0F + f2 / 2.0F + f1), 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate((f3 / 2.0F), 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                renderModel(entitylivingbaseIn, this.playerRenderer.getMainModel(), 0.0625f);
                GlStateManager.popMatrix();
            }
        }
    }

    public void renderModel(AbstractClientPlayer player, ModelBase model, float scale) {
        this.bipedCape = new ModelRenderer(model, 0, 0);
        this.bipedCape.setTextureSize(128, 64); // 128x64 Capes, double the default mc capes
        this.bipedCape.addBox(-10.0F, 0.0F, -2.0F, 20, 32, 2);
        if (player.isSneaking()) {
            this.bipedCape.rotationPointY = 3.0F;
        } else {
            this.bipedCape.rotationPointY = 0.0F;
        }


        this.bipedCape.render(scale / 2);
    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
