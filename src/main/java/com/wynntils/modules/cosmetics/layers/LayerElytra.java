/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.cosmetics.layers;

import com.wynntils.ModCore;
import com.wynntils.modules.core.instances.account.WynntilsUser;
import com.wynntils.modules.core.managers.UserManager;
import com.wynntils.modules.cosmetics.configs.CosmeticsConfig;
import com.wynntils.modules.cosmetics.layers.models.CustomElytraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerElytra extends ModelBase implements LayerRenderer<AbstractClientPlayer> {

    /**
     * The basic Elytra texture.
     */
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
    /**
     * Instance of the player renderer.
     */
    protected final RenderLivingBase<?> renderPlayer;
    /**
     * The model used by the Elytra.
     */
    private final CustomElytraModel modelElytra = new CustomElytraModel();

    public LayerElytra(RenderPlayer playerRendererIn) {
        this.renderPlayer = playerRendererIn;
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!CosmeticsConfig.INSTANCE.forceCapes
                && !Minecraft.getMinecraft().gameSettings.getModelParts().toString().contains("CAPE")
                && player.getUniqueID() == ModCore.mc().player.getUniqueID()) return;

        WynntilsUser info = UserManager.getUser(player.getUniqueID());
        if (info == null || !info.getCosmetics().hasElytra()) return;

        // loading cape
        ResourceLocation rl = info.getCosmetics().getLocation();

        // texture
        ResourceLocation elytra;
        if (player.isPlayerInfoSet() && player.getLocationElytra() != null) {
            elytra = player.getLocationElytra();
        } else if (player.hasPlayerInfo()) {
            elytra = rl;
        } else {
            elytra = TEXTURE_ELYTRA;
        }

        color(1.0F, 1.0F, 1.0F, 1.0F);
        enableAlpha();
        enableBlend();
        blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

        renderPlayer.bindTexture(elytra);

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

            if (player.isSneaking()) {
                f1 += 15.0F;
            }

            rotate((6.0F + f2 / 2.0F + f1), 1.0F, 0.0F, 0.0F);
            rotate((f3 / 2.0F), 0.0F, 0.0F, 1.0F);

            int frameCount = info.getCosmetics().getImage().getHeight() / (info.getCosmetics().getImage().getWidth() / 2);

            modelElytra.update(frameCount);
            modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
            modelElytra.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            disableBlend();
        } popMatrix();
    }

    public boolean shouldCombineTextures() {
        return false;
    }

}
