package cf.wynntils.modules.capes.layers;

import cf.wynntils.webapi.WebManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

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
    private final ModelElytra modelElytra = new ModelElytra();

    public LayerElytra(RenderPlayer playerRendererIn) {
        this.renderPlayer = playerRendererIn;
    }

    @Override
    public void doRenderLayer(net.minecraft.client.entity.AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

        if (WebManager.hasCape(entitylivingbaseIn.getUniqueID().toString().replace("-", "")) && itemstack.getItem() != Items.ELYTRA) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            if (entitylivingbaseIn instanceof AbstractClientPlayer) {
                AbstractClientPlayer abstractclientplayer = entitylivingbaseIn;

                if (abstractclientplayer.isPlayerInfoSet() && abstractclientplayer.getLocationElytra() != null) {
                    this.renderPlayer.bindTexture(abstractclientplayer.getLocationElytra());
                } else if (abstractclientplayer.hasPlayerInfo() && WebManager.isPremium(entitylivingbaseIn.getUniqueID().toString().replace("-", ""))) {
                    this.renderPlayer.bindTexture(new ResourceLocation("wynntils:capes/" + entitylivingbaseIn.getUniqueID().toString().replace("-", "")));
                } else {
                    this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
                }
            } else {
                this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
            }

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.125F);

            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double) partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double) partialTicks);
            double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double) partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double) partialTicks);
            double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double) partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double) partialTicks);
            float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
            double d3 = (double) MathHelper.sin(f * 0.017453292F);
            double d4 = (double) (-MathHelper.cos(f * 0.017453292F));
            float f1 = (float) d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
            float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
            f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

            if (entitylivingbaseIn.isSneaking()) {
                f1 += 15.0F;
            }

            GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
//            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entitylivingbaseIn);
            this.modelElytra.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (WebManager.isModerator(entitylivingbaseIn.getUniqueID().toString().replace("-", ""))) {
                LayerArmorBase.renderEnchantedGlint(this.renderPlayer, entitylivingbaseIn, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}
