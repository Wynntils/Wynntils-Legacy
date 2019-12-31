/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics.layers;

import com.wynntils.ModCore;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerCape implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;

    public LayerCape(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!Minecraft.getMinecraft().gameSettings.getModelParts().toString().contains("CAPE")
                && player.getUniqueID() == ModCore.mc().player.getUniqueID())
            return;

        if(!WebManager.hasCape(player.getUniqueID())) return;

        // loading cape
        ResourceLocation rl = new ResourceLocation("wynntils:capes/" + player.getUniqueID().toString().replace("-", ""));

        // rendering verifications
        if (rl == null || !player.hasPlayerInfo() || player.isInvisible()) return;
        if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) return;

        // texture
        color(1.0F, 1.0F, 1.0F, 1.0F);
        playerRenderer.bindTexture(rl);

        // rendering
        { pushMatrix();
            translate(0.0F, 0.0F, 0.125F);

            double d0 = player.prevChasingPosX + (player.chasingPosX - player.prevChasingPosX) * (double) partialTicks - (player.prevPosX + (player.posX - player.prevPosX) * (double) partialTicks);
            double d1 = player.prevChasingPosY + (player.chasingPosY - player.prevChasingPosY) * (double) partialTicks - (player.prevPosY + (player.posY - player.prevPosY) * (double) partialTicks);
            double d2 = player.prevChasingPosZ + (player.chasingPosZ - player.prevChasingPosZ) * (double) partialTicks - (player.prevPosZ + (player.posZ - player.prevPosZ) * (double) partialTicks);
            float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
            double d3 = MathHelper.sin(f * 0.017453292F);
            double d4 = -MathHelper.cos(f * 0.017453292F);
            float f1 = (float) d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            // Clamping f2 and f3 ...
            f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
            f3 = MathHelper.clamp(f3, 0.0F, 50.0F);

            float f4 = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
            f1 = f1 + MathHelper.sin((player.prevDistanceWalkedModified + (player.distanceWalkedModified - player.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

            if (player.isSneaking()) f1 += 15.0F;

            rotate((6.0F + f2 / 2.0F + f1), 1.0F, 0.0F, 0.0F);
            rotate((f3 / 2.0F), 0.0F, 0.0F, 1.0F);
            rotate(180.0F, 0.0F, 1.0F, 0.0F);

            enableAlpha();
            enableBlend();

            renderModel(player, playerRenderer.getMainModel(), 0.0625f);

            disableBlend();
            disableAlpha();
        } popMatrix();
    }

    public static void renderModel(AbstractClientPlayer player, ModelBase model, float scale) {
        ModelRenderer bipedCape = new ModelRenderer(model, 0, 0);
        bipedCape.setTextureSize(128, 64);  // 128x64 Capes, double the default mc capes
        bipedCape.addBox(-10.0F, 0.0F, -2.0F, 20, 32, 2);

        if (player.isSneaking()) bipedCape.rotationPointY = 3.0F;
        else bipedCape.rotationPointY = 0.0F;

        bipedCape.render(scale / 2);
    }

    public boolean shouldCombineTextures() {
        return false;
    }

}
