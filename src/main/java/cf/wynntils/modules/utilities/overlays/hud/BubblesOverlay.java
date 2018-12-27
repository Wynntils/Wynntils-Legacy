package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BubblesOverlay extends Overlay {

    public BubblesOverlay() {
        super("Bubbles Overlay", 20, 20, true, 0.5f, 1.0f, 0, -29);
    }

    private static float animation = 300.0f;

    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen (0 for instant)")
    public float animated = 2f;

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = false;

    @Setting(displayName = "Level Number Position", description = "The position offset of the level number")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(0,-6);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CustomColor.fromString("6aabf5",1f);

    private static float amount = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (mc.player.getAir() != 300 && !Reference.onLobby))) return;
        if (OverlayConfig.Bubbles.INSTANCE.animated > 0.0f && OverlayConfig.Bubbles.INSTANCE.animated < 10.0f && !(amount >= 300))
            amount -= (OverlayConfig.Bubbles.INSTANCE.animated * 0.1f) * (amount - mc.player.getAir());
        else amount = getPlayerInfo().getCurrentHealth();

        if(amount <= 0) amount = 0;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if(!Reference.onWorld) return;

        if ((e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (e.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) && OverlayConfig.Bubbles.INSTANCE.enabled) {
            e.setCanceled(true);

            switch (OverlayConfig.Bubbles.INSTANCE.bubblesTexture) {
                case Wynn:
                    drawDefaultBar(0, 5, 0, 9);
                    break;
                case a: drawDefaultBar(0,5,10,19);
                    break;
                case b: drawDefaultBar(0,5,20,29);
                    break;
                case c: drawDefaultBar(0,5,30,39);
                    break;
            }
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if (!Reference.onWorld || !OverlayConfig.Bubbles.INSTANCE.drowningVignette || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }


        if(mc.player.getAir() == 300 && animation >= 300) return;

        if(mc.player.getAir() == 300) animation = easeOut(animation, 300, 1.5f, 20f);
        else animation = mc.player.getAir();

        float value = Math.abs((animation / 300.0f) - 1.0f);

        GlStateManager.pushMatrix();
        {
            transformationOrigin(0, 0);
            GlStateManager.color(0, 0.500f, 1, value);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlpha();
            Textures.Masks.vignette.bind();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, (double)screen.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos((double)screen.getScaledWidth(), (double)screen.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos((double)screen.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        GlStateManager.popMatrix();
    }

    public static float easeOut(float current, float goal, float jump, float speed) {
        if (Math.floor(Math.abs(goal - current) / jump) > 0) {
            return current + (goal - current) / speed;
        } else {
            return goal;
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2) {
        drawProgressBar(Textures.Overlays.bars_bubbles,-91, y1, 91, y2, ty1, ty2, (flip ? -amount : amount) / 300);
        drawString((mc.player.getAir() / 3 <= 0 ? 0 : mc.player.getAir()/3) + "", textPositionOffset.a, textPositionOffset.b, textColor, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Exp.INSTANCE.textShadow);
    }

}
