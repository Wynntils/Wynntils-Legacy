/*
 *  * Copyright © Wynntils - 2018 - 2020.
 *
 * Updated 9/8/2020:
 * Hey! Me (Cobular#5105) and Formuoli (formuoli#0719) just changed a bunch of stuff here to make ears more useful. Now,
 * they support transparency, are rotated/mirrored correctly, and pull from a different place on the skins file. If you
 * are reading this, you probably want to change this in some way. Feel free to @ us on the wynntils discord and we can
 * help get you started. Remember to enable and disable blending though!
 * Also, we're a python programmer and a C# programmer, so there may be some very non-java (read: inefficient and bad)
 * ways to do things in here. Sorry about that, we'd be happy to learn what the correct way to do things may be!
 *
 * Changelog:
 *  - Ears now support transparency
 *  - Ears now are mirrored correctly in order to make them actually useful with transparency
 *  - Added ability to load the ears from multiple parts of the skins file, since as far as we are aware, there is not a
 *    14x7 block of pixels on the skin that allow transparency. The current implementation loads the two halves of the
 *    ear texture (one 8x7, one 6x7 block, see https://docs.google.com/spreadsheets/d/1bLq-26mOE52BWusWeLWy6tvJmWfT73o3lCGFPZR40aM/edit#gid=0
 *    for more information). As far as we know, the only valid pair of places for this are to the left and right of the
 *    hat, which is what we are currently setup to use. This setup does not conflict with any other skin region in
 *    vanilla. However, it is not backwards compatible with the old ears system (let's not kid ourselves though, no one
 *    actually used that system, and this one's like a million times more useful)
 */

package com.wynntils.modules.cosmetics.layers;

import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.core.instances.account.WynntilsUser;
import com.wynntils.modules.core.managers.UserManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import com.wynntils.modules.cosmetics.layers.models.ExtendedModelRenderer;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LayerFoxEars implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;
    private final ExtendedModelRenderer bipedFoxEarL;
    private final ExtendedModelRenderer bipedFoxEarR;

    public LayerFoxEars(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
        this.bipedFoxEarL = new ExtendedModelRenderer(playerRendererIn.getMainModel(), 32, 0, 56, 0);
        this.bipedFoxEarR = new ExtendedModelRenderer(playerRendererIn.getMainModel(), 32, 0, 56, 0);
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
        bipedFoxEarL.render(scale/2);
    }

    public void renderModelR(AbstractClientPlayer player, ModelBase model, float scale) {
        bipedFoxEarR.cubeList.clear();
        if (player.isSneaking()) {
            bipedFoxEarR.addEars(0.0F + 4F, -5.0F + 5F /  2F, 1f, 6, 6, 1);

        } else {
            bipedFoxEarR.addEars(0.0F, -5.0F, -1.0F, 6, 6, 1);
        }


        ModelBase.copyModelAngles(playerRenderer.getMainModel().bipedHeadwear, bipedFoxEarR);
        bipedFoxEarR.rotateAngleX = playerRenderer.getMainModel().bipedHeadwear.rotateAngleX * (scale/2);
        bipedFoxEarR.rotateAngleY = playerRenderer.getMainModel().bipedHeadwear.rotateAngleY * (scale/2);
        bipedFoxEarR.rotateAngleZ = playerRenderer.getMainModel().bipedHeadwear.rotateAngleZ * (scale/2);

// Why is this commented? Why does it work? Come back next week for answers to these dire questions and more!!
        ReflectionFields.ModelRenderer_compiled.setValue(bipedFoxEarR, false);
        bipedFoxEarR.render(scale/2);
    }

    public boolean shouldCombineTextures() {
        return true;
    }

}
