package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ConfigValues;
import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.richpresence.guis.WRPGui;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class HudOverlay extends WRPGui {

    private static final ResourceLocation bars = new ResourceLocation(Reference.MOD_ID + ":textures/gui/overlay-bars.png");
    private static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");
    int lastHealth = 0;
    int lastMana = 0;
    boolean onHealAnimation = false;
    boolean onManaAnimation = false;
    String lastActionBar = "";
    long lastActionBarTime = System.currentTimeMillis();
    float ticks = 0.0f;
    private float prevVignetteBrightness = 1.0f;

    public HudOverlay(Minecraft mc){
        super(mc);
    }

    public static String getCurrentActionBar(){
        try {
            String actionBar = (String) ReflectionHelper.findField(GuiIngame.class, "overlayMessage", "field_73838_g").get(Minecraft.getMinecraft().ingameGUI);

            if (!actionBar.equals("")) {
                ModCore.mc().ingameGUI.setOverlayMessage("", false);
            }
            return actionBar;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPlayerDirection(float yaw){
        double num = (yaw + 202.5) / 45.0;
        while (num < 0.0) {
            num += 360.0;
        }
        int dir = (int) (num);
        dir = dir % 8;

        switch (dir) {
            case 1:
                return "NE";
            case 2:
                return "E";
            case 3:
                return "SE";
            case 4:
                return "S";
            case 5:
                return "SW";
            case 6:
                return "W";
            case 7:
                return "NW";
            default:
                return "N";
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRender(RenderGameOverlayEvent.Pre e){
        if (!e.isCancelable()) {
            return;
        }

        if(!ConfigValues.wynnExpansion.hud.main.a_enabled) return;

        //blocking
        if (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH || e.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT || e.getType() == RenderGameOverlayEvent.ElementType.FOOD || e.getType() == RenderGameOverlayEvent.ElementType.ARMOR || e.getType() == RenderGameOverlayEvent.ElementType.AIR) {
            e.setCanceled(true);
            return;
        }

        //removing action bar text
        if (e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            String actionBar = getCurrentActionBar();
            if (!actionBar.equalsIgnoreCase("")) {
                lastActionBar = actionBar;

                //why i need this time? just to get the actionbar timeout
                lastActionBarTime = System.currentTimeMillis();
            }
        }
    }

    public boolean renderItemName(ScaledResolution scaledRes){
        mc.gameSettings.heldItemTooltips = false;
        try {
            int remainingHighlightTicks = (int) ReflectionHelper.findField(GuiIngame.class, "remainingHighlightTicks", "field_92017_k").get(Minecraft.getMinecraft().ingameGUI);
            ItemStack highlightingItemStack = (ItemStack) ReflectionHelper.findField(GuiIngame.class, "highlightingItemStack", "field_92016_l").get(Minecraft.getMinecraft().ingameGUI);

            if (remainingHighlightTicks > 0 && !highlightingItemStack.isEmpty()) {
                String s = highlightingItemStack.getDisplayName();

                if (highlightingItemStack.hasDisplayName()) {
                    s = TextFormatting.ITALIC + s;
                }

                int i = (scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(s)) / 2;
                int j = scaledRes.getScaledHeight() - 65;

                if (!this.mc.playerController.shouldDrawHUD()) {
                    j += 14;
                }

                int k = (int) ((float) remainingHighlightTicks * 256.0F / 10.0F);

                if (k > 255) {
                    k = 255;
                }

                if (k > 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    mc.fontRenderer.drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPreRender(RenderGameOverlayEvent.Post e){
        //to render only when the survival UI is read

        if (ConfigValues.wynnExpansion.hud.main.c_health) {
            float scale = 1 - (mc.player.getHealth() / mc.player.getMaxHealth());
//            if(scale > 0.3f)
                renderVignette(1.0f, scale);
        }

        if ((e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || e.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) && ConfigValues.wynnExpansion.hud.main.a_enabled) {

            ScaledResolution resolution = new ScaledResolution(mc);

            if (mc.gameSettings.gammaSetting >= 1000) {
                drawString("§6GammaBright", resolution.getScaledWidth() - 70, 5, -1);
            }

            int x = resolution.getScaledWidth() / 2;
            int y = resolution.getScaledHeight();

            int manaBarWidth = (int) (82.0 * ((float) (mc.player.getFoodStats().getFoodLevel()) / 20.0F));

            if (lastMana != manaBarWidth) {
                if (!onManaAnimation) {
                    onManaAnimation = true;

                    lastMana = manaBarWidth;
                }

                if (lastMana > manaBarWidth) {
                    lastMana--;
                    if (lastMana < manaBarWidth) {
                        lastMana = manaBarWidth;
                        onManaAnimation = false;
                    }
                } else if (lastMana < manaBarWidth) {
                    lastMana++;
                    if (lastMana > manaBarWidth) {
                        lastMana = manaBarWidth;
                        onManaAnimation = false;
                    }
                }
            }

            int healthBarWidth = (int) (82.0 * ((mc.player.getHealth()) / mc.player.getMaxHealth()));

            if (lastHealth != healthBarWidth) {
                if (!onHealAnimation) {
                    onHealAnimation = true;

                    lastHealth = healthBarWidth;
                }

                if (lastHealth > healthBarWidth) {
                    lastHealth--;
                    if (lastHealth < healthBarWidth) {
                        lastHealth = healthBarWidth;
                        onHealAnimation = false;
                    }
                } else if (lastHealth < healthBarWidth) {
                    lastHealth++;
                    if (lastHealth > healthBarWidth) {
                        lastHealth = healthBarWidth;
                        onHealAnimation = false;
                    }
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            {
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                mc.getTextureManager().bindTexture(bars);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                drawTexturedModalRect(x + 10, y - 38, 0, 39, 82, 8);
                if (lastMana != 82) {
                    if (lastMana > 2) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                        drawTexturedModalRect(x + 13 + (82 - lastMana), y - 38, (82 - lastMana) + 3, 10, 82, 8);
                    }
                    if (lastMana > 1) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                        drawTexturedModalRect(x + 12 + (82 - lastMana), y - 38, (82 - lastMana) + 2, 10, 82, 8);
                    }
                    if (lastMana > 0) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                        drawTexturedModalRect(x + 11 + (82 - lastMana), y - 38, (82 - lastMana) + 1, 10, 82, 8);
                    }
                } else {
                    drawTexturedModalRect(x + 10 + (82 - lastMana), y - 38, (82 - lastMana), 10, 82, 8);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                drawTexturedModalRect(x - 91, y - 38, 0, 20, 82, 8);
                if (lastHealth != 82) {
                    if (lastHealth > 2) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                        drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 1, 8);
                    }
                    if (lastHealth > 1) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                        drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 2, 8);
                    }
                    if (lastHealth > 0) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                        drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 3, 8);
                    }
                } else {
                    drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth, 8);
                }

            }

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();

            //this is to check if the action bar still exists
            if (System.currentTimeMillis() - lastActionBarTime >= 3000) {
                return;
            }

            String[] divisor = lastActionBar.split("/");
            if (divisor.length < 2) {
                return;
            }

            String life = divisor[0].split(" ")[1] + " ❤ " + divisor[1].split(" ")[0];

            String health = "§c" + life + " ";

            String middle;
            String l = "";
            String r = "";

            boolean preference = false;

            //Order:
            //Powder % | RLR | Sprint | and if there is nothing more coordinates
            if (lastActionBar.contains("%")) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[7] + " " + spaces[8];
            } else if (lastActionBar.contains("R§7-") || lastActionBar.contains("N§7-")) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[5];
                preference = true;
            } else if (RichUtils.stripColor(lastActionBar).contains("Sprint") && mc.player.isSprinting()) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[5];
            } else if (ConfigValues.wynnExpansion.hud.main.b_coords) {
                l = "§7" + (int) mc.player.posX;
                middle = "§a§l" + getPlayerDirection(mc.player.rotationYaw);
                r = "§7" + (int) mc.player.posZ;
            } else {
                middle = "";
            }

            String mana = " §b" + mc.player.getFoodStats().getFoodLevel() + " ✺ 20";

            int padding = 3;

            if (preference || !renderItemName(resolution)) {
                drawString(mc.fontRenderer, l, (x - mc.fontRenderer.getStringWidth(l) - mc.fontRenderer.getStringWidth(middle) / 2 - padding), y - 65, 1);
                drawCenteredString(mc.fontRenderer, middle, x, y - 65, 1);
                drawString(mc.fontRenderer, r, (x + mc.fontRenderer.getStringWidth(middle) / 2 + padding), y - 65, 1);
            }

            drawCenteredString(mc.fontRenderer, health, (x - 5 - (86 / 2)), y - 50, 1); // DO NOT EDIT
            drawCenteredString(mc.fontRenderer, mana, (x + 5 + (86 / 2)), y - 50, 1); // DO NOT EDIT
        }
    }

    public void renderVignette(float lightLevel, float scale){

        float[] rgb = new float[]{255f / 255f, 0f / 255f, 0f / 255f};

        if (rgb == null)
            return;

        ScaledResolution scaledRes = new ScaledResolution(mc);

        lightLevel = 1.0F - lightLevel;
        lightLevel = MathHelper.clamp(lightLevel, 0.0F, 1.0F);

        float f = (float) 4;
        double d0 = Math.min(1000.0f / 10.0f * 10.0f * 1000.0D, Math.abs(1.0f - 30.0f));
        double d1 = Math.max(200.0f + Math.sin(ticks) * 180f, d0);

        if ((double) f < d1) {
            f = 1.0F - (float) ((double) f / d1);
        } else {
            f = 0.0F;
        }

        this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (lightLevel - this.prevVignetteBrightness) * 0.01D);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);

        if (f > 0.0F) {
            GlStateManager.color(rgb[0] * scale, rgb[1] * scale, rgb[2] * scale, 1.0f);
        } else {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        }

        this.mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(0.0D, (double) scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        vertexbuffer.pos((double) scaledRes.getScaledWidth(), (double) scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        vertexbuffer.pos((double) scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        vertexbuffer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }
}
