/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.cosmetics.layers;

import com.wynntils.modules.core.instances.account.WynntilsUser;
import com.wynntils.modules.core.managers.UserManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerFoxEars implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;

    public LayerFoxEars(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        WynntilsUser info = UserManager.getUser(player.getUniqueID());
        if (info == null || !info.getCosmetics().hasEars()) return;
        if (!player.hasSkin() || player.isInvisible()) return;

        this.playerRenderer.bindTexture(player.getLocationSkin());

        for (int i = 0; i < 2; ++i) {
            float f = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks - (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks);
            float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;

            pushMatrix(); {
                rotate(f, 0.0F, 1.0F, 0.0F);
                rotate(f1, 1.0F, 0.0F, 0.0F);
                translate(0.375F * (float) (i * 1.2 - 1), 0.0F, 0.0F);
                translate(0.0F, -0.5F, 0.0F);
                rotate(45f, 0.0F, 0.0F, 1.0F);
                scale(1.3333334F, 1.3333334F, 1.3333334F);

                renderModel(player, playerRenderer.getMainModel(), 0.0625f);
            } popMatrix();
        }
    }

    public void renderModel(AbstractClientPlayer player, ModelBase model, float scale) {
        ModelRenderer bipedFoxEar = new ModelRenderer(model, 24, 0);

        if (player.isSneaking()) bipedFoxEar.addBox(0.0F + 4F, -5.0F + 5F / 2F, 1f, 6, 6, 1);
        else bipedFoxEar.addBox(0.0F, -5.0F, -1.0F, 6, 6, 1);

        ModelBase.copyModelAngles(playerRenderer.getMainModel().bipedHeadwear, bipedFoxEar);
        bipedFoxEar.rotateAngleX = playerRenderer.getMainModel().bipedHeadwear.rotateAngleX * (scale / 2);
        bipedFoxEar.rotateAngleY = playerRenderer.getMainModel().bipedHeadwear.rotateAngleY * (scale / 2);
        bipedFoxEar.rotateAngleZ = playerRenderer.getMainModel().bipedHeadwear.rotateAngleZ * (scale / 2);
        bipedFoxEar.render(scale / 2);
    }

    public boolean shouldCombineTextures() {
        return true;
    }

}
