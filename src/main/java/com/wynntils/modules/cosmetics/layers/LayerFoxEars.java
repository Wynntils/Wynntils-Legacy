/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.cosmetics.layers;

import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.core.instances.account.WynntilsUser;
import com.wynntils.modules.core.managers.UserManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import com.wynntils.modules.cosmetics.layers.models.EarModelRenderer;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerFoxEars implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;
    private final EarModelRenderer bipedFoxEarL;
    private final EarModelRenderer bipedFoxEarR;

    public LayerFoxEars(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
        this.bipedFoxEarL = new EarModelRenderer(playerRendererIn.getMainModel(), 32, 0, 56, 0);
        this.bipedFoxEarR = new EarModelRenderer(playerRendererIn.getMainModel(), 32, 0, 56, 0);
    }

    public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        WynntilsUser info = UserManager.getUser(player.getUniqueID());
        if (info == null || !info.getCosmetics().hasEars()) return;
        if (!player.hasSkin() || player.isInvisible()) return;

        this.playerRenderer.bindTexture(player.getLocationSkin());

        float f = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks - (player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks);
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;


        pushMatrix();
        enableBlend();
        {
            rotate(f, 0.0F, 1.0F, 0.0F);
            rotate(f1, 1.0F, 0.0F, 0.0F);
            translate(0.375F * (float) (1 * 1.2 - 1), 0.0F, 0.0F);
            translate(0.0F, -0.5F, 0.0F); // Editing y here controls up-left
            rotate(-45f, 0.0F, 0.0F, 1.0F);
            translate(0.225F, 0.21F, 0.0F);  // -Z is forward, X is Up-Right, Y is down-right, -y is up-left
            scale(-1.3333334F, 1.3333334F, 1.3333334F); // Z is front to back, X seems right??

            renderModelR(player, playerRenderer.getMainModel(), 0.0625f);
        }
        disableBlend();
        popMatrix();

        pushMatrix();
        enableBlend();
        {
            rotate(f, 0.0F, 1.0F, 0.0F);
            rotate(f1, 1.0F, 0.0F, 0.0F);
            translate(0.375F * (float) (0 * 1.2 - 1), 0.0F, 0.0F);
            translate(0.0F, -0.5F, 0.0F);
            rotate(45f, 0.0F, 0.0F, 1.0F);
            scale(1.3333334F, 1.3333334F, 1.3333334F);

            renderModelL(player, playerRenderer.getMainModel(), 0.0625f);
        }
        disableBlend();
        popMatrix();
    }

    public void renderModelL(AbstractClientPlayer player, ModelBase model, float scale) {
        bipedFoxEarL.cubeList.clear();
        if (player.isSneaking()) {
            bipedFoxEarL.addEars(0.0F + 4F, -5.0F + 5F / 2F, 1f, 6, 6, 1);

        } else {
            bipedFoxEarL.addEars(0.0F, -5.0F, -1.0F, 6, 6, 1);
        }

        ModelBase.copyModelAngles(playerRenderer.getMainModel().bipedHeadwear, bipedFoxEarL);
        bipedFoxEarL.rotateAngleX = playerRenderer.getMainModel().bipedHeadwear.rotateAngleX * (scale/2);
        bipedFoxEarL.rotateAngleY = playerRenderer.getMainModel().bipedHeadwear.rotateAngleY * (scale/2);
        bipedFoxEarL.rotateAngleZ = playerRenderer.getMainModel().bipedHeadwear.rotateAngleZ * (scale/2);

        ReflectionFields.ModelRenderer_compiled.setValue(bipedFoxEarL, false);
        bipedFoxEarL.render(scale / 2);
    }

    public void renderModelR(AbstractClientPlayer player, ModelBase model, float scale) {
        bipedFoxEarR.cubeList.clear();
        if (player.isSneaking()) {
            bipedFoxEarR.addEars(0.0F + 4F, -5.0F + 5F / 2F, 1f, 6, 6, 1);

        } else {
            bipedFoxEarR.addEars(0.0F, -5.0F, -1.0F, 6, 6, 1);
        }


        ModelBase.copyModelAngles(playerRenderer.getMainModel().bipedHeadwear, bipedFoxEarR);
        bipedFoxEarR.rotateAngleX = playerRenderer.getMainModel().bipedHeadwear.rotateAngleX * (scale/2);
        bipedFoxEarR.rotateAngleY = playerRenderer.getMainModel().bipedHeadwear.rotateAngleY * (scale/2);
        bipedFoxEarR.rotateAngleZ = playerRenderer.getMainModel().bipedHeadwear.rotateAngleZ * (scale/2);

        ReflectionFields.ModelRenderer_compiled.setValue(bipedFoxEarR, false);
        bipedFoxEarR.render(scale/2);
    }

    public boolean shouldCombineTextures() {
        return true;
    }

}
