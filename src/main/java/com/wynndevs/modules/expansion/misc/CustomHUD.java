package com.wynndevs.modules.expansion.misc;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.overrides.EntityPlayerEXP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomHUD extends Gui {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/icons.png");
    private static final int LERP_TICKS = 60;
    public int oldHealth = 0;
    public int oldMana = 0;
    private Minecraft mc;
    private ITextComponent leftCoord, direction, rightCoord;
    private Timer lerpTimer = null;
    // Cache hashcode
    private int actionBarHash = -1;

    public CustomHUD(Minecraft mc) {
        super();

        this.mc = mc;
    }


    @SubscribeEvent
    public void draw(RenderGameOverlayEvent event) {
        if (true) {
            if (lerpTimer == null) // This has to be reset every time mod disables, or you will be stuck in a loop on re-enable
                lerpTimer = new Timer(1000L / LERP_TICKS);
            mc.gameSettings.heldItemTooltips = false;
            /* Cancel render vanilla hearts,Food, air and armor */
            if (event.getType() == ElementType.HEALTH || event.getType() == ElementType.FOOD || event.getType() == ElementType.ARMOR || event.getType() == ElementType.AIR || event.getType() == ElementType.HEALTHMOUNT) {
                event.setCanceled(true);
            }

            /* Do the actual rendering */
            if (!event.isCancelable() && (event.getType() == ElementType.EXPERIENCE || event.getType() == ElementType.JUMPBAR)) {
                if (mc.world == null || mc.player == null) {
                    return;
                }

                mc.mcProfiler.startSection("renderCustomHUD");

                // Constants
                final float SMOOTH_TICK = 60; // How many tick per second to lerp. has to be SMOOTH_TICK > 0

                /* Needed Variables */
                final float partialTicks = event.getPartialTicks();

                EntityPlayerSP player = mc.player;

                ScaledResolution resolution = new ScaledResolution(mc);
                int width = resolution.getScaledWidth();
                int height = resolution.getScaledHeight();

                if (true) {
                    GlStateManager.pushMatrix(); // Push the matrix
                    mc.ingameGUI.drawString(mc.fontRenderer, "\u00A73FPS: \u00A7b" + Minecraft.getDebugFPS(), 0, 10, 0xFF79F21E);
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
                    Date date = new Date();
                    mc.ingameGUI.drawString(mc.fontRenderer, "\u00A73Time: \u00A7b" + dateFormat.format(date), 0, 20, 0xFF79F21E);
                    GlStateManager.popMatrix(); // Pop the matrix
                }

                /* Set needed OpenGL settings */
                GlStateManager.pushMatrix(); // Push the matrix
                GlStateManager.pushAttrib(); // Push the styling
                GlStateManager.enableAlpha(); // Enable transparency
                GlStateManager.enableBlend(); // Enable blending
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);

                mc.getTextureManager().bindTexture(TEXTURE);


                int yOffset = 38;

                int healthY = 10;
                int manaY = 0;

                /* Math part */
                float currentHealth = mc.player.getHealth(); // Health
                float maxHealth = mc.player.getMaxHealth();
                int healthBarWidth = (int) (83.0 * (currentHealth / maxHealth));
                int foodLevel = mc.player.getFoodStats().getFoodLevel(); // Mana
                //				foodLevel = 10;
                int manaBarWidth = (int) (83.0 * ((float) (foodLevel == 20 ? 21 : foodLevel) / 20.0F)); // The 21 is so that it properly renders #19, don't change this, it's correct.
                if (foodLevel == 20) manaBarWidth = manaBarWidth - 4;

                /* Initialize on world entry */
                if (oldHealth == -1f)
                    oldHealth = healthBarWidth;
                if (oldMana == -1f)
                    oldMana = manaBarWidth;

                /* Lerp part */
                while (lerpTimer.ready()) { // While lerp available
                    // Lerp value
                    final float lerp = 1.0f;
                    // Lerp Health
                    if (healthBarWidth < oldHealth) {
                        oldHealth -= lerp;
                        if (healthBarWidth > oldHealth)
                            oldHealth = healthBarWidth;
                    } else if (healthBarWidth > oldHealth) {
                        oldHealth += lerp;
                        if (healthBarWidth < oldHealth)
                            oldHealth = healthBarWidth;
                    }
                    // Lerp Mana
                    if (manaBarWidth < oldMana) {
                        oldMana -= lerp;
                        if (manaBarWidth > oldMana)
                            oldMana = manaBarWidth;
                    } else if (manaBarWidth > oldMana) {
                        oldMana += lerp;
                        if (manaBarWidth < oldMana)
                            oldMana = manaBarWidth;
                    }
                }

                if (oldHealth < 0) oldHealth = 0;
                else if (oldHealth > 83) oldHealth = 83;
                if (oldMana < 0) oldMana = 0;
                else if (oldMana > 83) oldMana = 83;

                /* Draw border */
                mc.ingameGUI.drawTexturedModalRect(width / 2 - (9 + 83), height - yOffset, 1, 170, 83, 9); // Health
                mc.ingameGUI.drawTexturedModalRect(width / 2 + 9, height - yOffset, 1, 170, 83, 9); // Mana

                /* Draw health bar */
                if (oldHealth == 83) { // Full - Solid
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                    mc.ingameGUI.drawTexturedModalRect(width / 2 - (9 + 83), height - yOffset, 1, healthY, oldHealth, 9);
                } else { // Not Full - Faded
                    mc.ingameGUI.drawTexturedModalRect(width / 2 - (9 + 83), height - yOffset, 1, healthY, oldHealth - 3, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
                    if (oldHealth > 2)
                        mc.ingameGUI.drawTexturedModalRect(width / 2 - (9 + 83) + (oldHealth - 3), height - yOffset, oldHealth - 2, healthY, 1, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.50F);
                    if (oldHealth > 1)
                        mc.ingameGUI.drawTexturedModalRect(width / 2 - (9 + 83) + (oldHealth - 2), height - yOffset, oldHealth - 1, healthY, 1, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                    if (oldHealth > 0)
                        mc.ingameGUI.drawTexturedModalRect(width / 2 - (9 + 83) + (oldHealth - 1), height - yOffset, oldHealth - 0, healthY, 1, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                }



                /* Draw mana bar */
                if (oldMana == 83) { // Full - Solid
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                    mc.ingameGUI.drawTexturedModalRect((83 - manaBarWidth) + width / 2 + 9, height - yOffset, 1 + (83 - manaBarWidth), manaY, manaBarWidth, 9);
                } else { // Not Full - Faded
                    int inv = (83 - oldMana - 2);
                    mc.ingameGUI.drawTexturedModalRect(width / 2 + (9 + 3) + (inv), height - yOffset, 1 + (inv + 3), manaY, oldMana - 2, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
                    if (oldMana > 2)
                        mc.ingameGUI.drawTexturedModalRect(width / 2 + (9 + 2) + (inv), height - yOffset, 1 + (inv + 2), manaY, 1, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.50F);
                    if (oldMana > 1)
                        mc.ingameGUI.drawTexturedModalRect(width / 2 + (9 + 1) + (inv), height - yOffset, 1 + (inv + 1), manaY, 1, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                    if (oldMana > 0)
                        mc.ingameGUI.drawTexturedModalRect(width / 2 + (9 + 0) + (inv), height - yOffset, 1 + (inv + 0), manaY, 1, 9);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
                }

                /* Update Action Bar */
                updateActionBar(EntityPlayerEXP.rawText);

                /* Get Action Bar Text */
                int padding = 3;
                String lc = leftCoord.getUnformattedText();
                String rc = rightCoord.getUnformattedText();
                String dc = direction.getUnformattedText();

                /* Render Action Bar */
                mc.ingameGUI.drawString(mc.fontRenderer, lc, width / 2 - mc.fontRenderer.getStringWidth(lc) - mc.fontRenderer.getStringWidth(dc) / 2 - padding, height - 48, 0xFF79F21E);
                mc.ingameGUI.drawCenteredString(mc.fontRenderer, dc, width / 2, height - 48, 0xFF79F21E);
                mc.ingameGUI.drawString(mc.fontRenderer, rc, width / 2 + mc.fontRenderer.getStringWidth(dc) / 2 + padding, height - 48, 0xFF79F21E);
                mc.ingameGUI.setOverlayMessage("", false); //renders the action bar
                renderSelectedItem(resolution);
                //				new SkillpointUI(mc);


                /* Disable needed OpenGL */
                GlStateManager.disableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
                //mc.mcProfiler.endSection();
            }
        } else { // Not enabled
            oldHealth = -1;
            oldMana = -1;
            lerpTimer = null;
        }
    }

    private int genHash(String[] texts, int[] nums) {
        final int BASE = 17;
        final int MULTIPLIER = 31;

        int result = BASE;

        for (String text : texts)
            result = MULTIPLIER * result + text.hashCode();
        for (int num : nums)
            result = MULTIPLIER * result + Integer.hashCode(num);

        return result;
    }

    /**
     * Updates action bar to new value
     */
    private void updateActionBar(String text) {
        // Hash the text. This value is unique for each text.
        int hash = genHash(new String[]{text, EntityPlayerEXP.getHealthText()}, new int[]{(int) mc.player.posX, (int) mc.player.posZ, getDirNum(), mc.player.getFoodStats().getFoodLevel()});
        // Checking for change
        boolean changed = actionBarHash == -1 || actionBarHash != hash;
        // Store new hash
        actionBarHash = hash;

        // Where the magic happens
        if (changed) {
            String textLeft = EntityPlayerEXP.getHealthText();
            String textRight = String.format("%s%s %s%s/%s", "\u00A7r\u00A7b", EPowderSymbol.MANA.getSymbol(), "\u00A7r\u00A7b", mc.player.getFoodStats().getFoodLevel(), 20);
            String lCoord = "";
            String direction = null;
            String rCoord = "";

            String[] tmp = null;

            if ((tmp = text.split("[\\?LR]-[\\?LR]-[\\?LR]")).length == 2 && tmp[0].length() > 0 && tmp[1].length() > 0) {
                String[] match = text.split("[\\?LR]-[\\?LR]-[\\?LR]");
                String spell = text.replace(match[0], "").replaceAll(match[1], "");
                direction = "\u00A7r\u00A77" + spell.replace("-?-?", "-\u00A7n?\u00A7r\u00A77\u00A77-?\u00A7r").replace("R-?", "R-\u00A7n?\u00A7r").replace("L-?", "L-\u00A7n?\u00A7r").replace("R", "\u00A7aR\u00A77").replace("L", "\u00A7aL\u00A77");
            } else if (EntityPlayerEXP.getSprintText() != null && mc.player.isSprinting())
                direction = EntityPlayerEXP.getSprintText();
            else {
                lCoord = "\u00A7r\u00A77" + String.valueOf((int) mc.player.posX);
                direction = "\u00A7r" + getDirectionDisplay();
                rCoord = "\u00A7r\u00A77" + String.valueOf((int) mc.player.posZ);
            }
            String delimiter = " ";
            String lCoord1 = String.join(delimiter, textLeft, lCoord);
            String rCoord1 = String.join(delimiter, rCoord, textRight);
            leftCoord = new TextComponentString(lCoord1);
            this.direction = new TextComponentString(direction);
            rightCoord = new TextComponentString(rCoord1);
        }
    }

    public void renderSelectedItem(ScaledResolution scaledRes) {
        int remTicks = Reference.getField(GuiIngame.class, 12, mc.ingameGUI);
        ItemStack item = Reference.getField(GuiIngame.class, 13, mc.ingameGUI);

        if (remTicks > 0 && !item.isEmpty()) {
            String s = item.getDisplayName();

            if (item.hasDisplayName()) {
                s = TextFormatting.ITALIC + s;
            }

            int i = (scaledRes.getScaledWidth() - mc.ingameGUI.getFontRenderer().getStringWidth(s)) / 2;
            int j = scaledRes.getScaledHeight() - 83;

            if (!this.mc.playerController.shouldDrawHUD()) {
                j += 14;
            }

            int k = (int) ((float) remTicks * 256.0F / 10.0F);

            if (k > 255) {
                k = 255;
            }

            if (k > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                mc.ingameGUI.getFontRenderer().drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        //mc.mcProfiler.endSection();
    }

    private int getDirNum() {
        double num = (mc.player.rotationYaw + 202.5) / 45.0;
        while (num < 0.0)
            num += 360.0;
        int dir = (int) (num);
        return dir % 8;
    }

    public String getDirectionDisplay() {
        int dir = getDirNum();

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
            case 0:
            default:
                return "N";
        }
    }
}

class Timer {

    private long startTime;
    private long lastTime;
    private long interval;
    private int ticks;

    private boolean strict = false;

    /**
     * Create a new instance of timer
     *
     * @param interval The interval for each tick in milliseconds
     */
    public Timer(long interval) {
        this.ticks = 0;
        this.interval = interval;
        this.startTime = System.currentTimeMillis();
        this.lastTime = this.startTime;
    }

    /**
     * Internal update method. Called from {@link Timer#ready()}
     */
    private void update() {
        long now = System.currentTimeMillis();
        long delta = (now - this.lastTime);

        int lt = 0;

        while (delta >= interval) {
            lt++;
            delta -= interval;
        }

        this.lastTime += lt * interval;
        this.ticks += lt;
    }

    /**
     * Updates the timer and calculates ticks
     *
     * @return true when ticks are ready.
     */
    public boolean ready() {
        update();
        if (strict)
            ticks = Math.min(1, ticks);
        boolean r = ticks > 0;
        if (r)
            --ticks;
        return r;
    }

    /**
     * Set the strict flag to a new value. When turned on, the ticks will be capped at 1
     *
     * @param state The state
     */
    public void setStrict(boolean state) {
        this.strict = state;
    }

}