package cf.wynntils.modules.capes.layers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerFoxEars implements LayerRenderer<AbstractClientPlayer> {
    private final RenderPlayer playerRenderer;
    private ModelRenderer bipedFoxEar;

    public LayerFoxEars(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if ("Scyu_".equals(entitylivingbaseIn.getName()) && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible()) {
            this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationSkin());

            for (int i = 0; i < 2; ++i) {
                float f = entitylivingbaseIn.prevRotationYaw + (entitylivingbaseIn.rotationYaw - entitylivingbaseIn.prevRotationYaw) * partialTicks - (entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks);
                float f1 = entitylivingbaseIn.prevRotationPitch + (entitylivingbaseIn.rotationPitch - entitylivingbaseIn.prevRotationPitch) * partialTicks;
                GlStateManager.pushMatrix();
                GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f1, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.375F * (float) (i * 1.1 - 1), 0.0F, 0.0F);
                GlStateManager.translate(0.0F, -0.5F, 0.0F);
                GlStateManager.rotate(f1, 0.5F, 0.0F, 0.0F);
                GlStateManager.rotate(45f, 0.0F, 0.0F, 1.0F);

                GlStateManager.scale(1.3333334F, 1.3333334F, 1.3333334F);
//                this.playerRenderer.getMainModel().renderDeadmau5Head(0.0625F);
                renderModel(entitylivingbaseIn, this.playerRenderer.getMainModel(), 0.0625f);
                GlStateManager.popMatrix();
            }
        }
    }

    public void renderModel(AbstractClientPlayer player, ModelBase model, float scale) {
        this.bipedFoxEar = new ModelRenderer(model, 24, 0);
        if (player.isSneaking()) {
            this.bipedFoxEar.addBox(2F, -1.5F, 2F, 3, 3, 1);
        } else {
            this.bipedFoxEar.addBox(-0.1F, -3.0F, -1.0F, 3, 3, 1);
        }
        ModelBase.copyModelAngles(this.playerRenderer.getMainModel().bipedHead, this.bipedFoxEar);
        this.bipedFoxEar.rotateAngleZ = 45.0f;
        this.bipedFoxEar.rotateAngleX = 0f;
        this.bipedFoxEar.rotateAngleY = 0f;
        this.bipedFoxEar.rotateAngleZ = 0f;
        this.bipedFoxEar.render(scale);
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
